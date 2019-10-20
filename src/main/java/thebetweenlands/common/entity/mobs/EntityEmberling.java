package thebetweenlands.common.entity.mobs;

import java.util.List;
import java.util.Random;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityMultiPart;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.MultiPartEntityPart;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIOwnerHurtByTarget;
import net.minecraft.entity.ai.EntityAIOwnerHurtTarget;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.EntityLookHelper;
import net.minecraft.entity.ai.EntityMoveHelper;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.AbstractHorse;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.pathfinding.PathNavigateSwimmer;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.api.entity.IEntityBL;
import thebetweenlands.common.entity.ai.EntityAIFollowOwnerBL;
import thebetweenlands.common.entity.ai.EntityAISitBL;
import thebetweenlands.common.item.misc.ItemMisc.EnumItemMisc;
import thebetweenlands.common.registries.LootTableRegistry;
import thebetweenlands.common.registries.SoundRegistry;

//TODO Loot tables
public class EntityEmberling extends EntityTameable implements IEntityMultiPart, IMob, IEntityBL {

	public MultiPartEntityPart[] tailPart;
	private static final DataParameter<Boolean> IS_FLAME_ATTACKING = EntityDataManager.<Boolean>createKey(EntityEmberling.class, DataSerializers.BOOLEAN);
	public float animationTicks, prevAnimationTicks;

	private EntityMoveHelper moveHelperWater;
	private EntityMoveHelper moveHelperLand;

	private PathNavigateGround pathNavigatorGround;
	private PathNavigateSwimmer pathNavigatorWater;

	public EntityEmberling(World world) {
		super(world);
		setSize(0.9F, 0.85F);
		stepHeight = 1F;
		isImmuneToFire = true;
		tailPart = new MultiPartEntityPart[] { new MultiPartEntityPart(this, "tail", 0.5F, 0.5F) };

		setPathPriority(PathNodeType.WATER, 100);

		moveHelperWater = new EntityEmberling.EmberlingMoveHelper(this);
		moveHelperLand = new EntityMoveHelper(this);

		pathNavigatorGround = new PathNavigateGround(this, world);
		pathNavigatorGround.setCanSwim(true);

		pathNavigatorWater = new PathNavigateSwimmer(this, world);

		updateMovementAndPathfinding();
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		dataManager.register(IS_FLAME_ATTACKING, false);
	}

	public void setIsFlameAttacking(boolean flame_on) {
		dataManager.set(IS_FLAME_ATTACKING, flame_on);
	}

	public boolean getIsFlameAttacking() {
		return dataManager.get(IS_FLAME_ATTACKING);
	}

	@Override
	protected void initEntityAI() {
		tasks.addTask(0, new EntityEmberling.EntityAIFlameBreath(this));
		this.aiSit = new EntityAISitBL(this);
		tasks.addTask(1, this.aiSit);
		tasks.addTask(2, new EntityEmberling.AIEmberlingAttack(this));
		tasks.addTask(3, new EntityAIFollowOwnerBL(this, 0.6D, 10.0F, 2.0F));
		tasks.addTask(4, new EntityAIWander(this, 0.6D));
		tasks.addTask(5, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
		tasks.addTask(5, new EntityAILookIdle(this));
		targetTasks.addTask(0, new EntityAINearestAttackableTarget<>(this, EntityPlayer.class, 0, false, true, null));
		targetTasks.addTask(1, new EntityAIOwnerHurtByTarget(this));
		targetTasks.addTask(2, new EntityAIOwnerHurtTarget(this));
		targetTasks.addTask(3, new EntityAIHurtByTarget(this, true, new Class[0]));
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.5D);
		getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(30D);
		getAttributeMap().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(2.0D);
		getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(16.0D);
		getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(0.25D);
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return SoundRegistry.EMBERLING_LIVING;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
		return SoundRegistry.EMBERLING_HURT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundRegistry.EMBERLING_DEATH;
	}

	@Override
	protected ResourceLocation getLootTable() {
		return LootTableRegistry.EMBERLING;
	}

	@Override
	public boolean shouldAttackEntity(EntityLivingBase target, EntityLivingBase owner) {
		if (!(target instanceof EntityCreeper) && !(target instanceof EntityGhast)) {
			if (target instanceof EntityEmberling) {
				EntityEmberling emberling = (EntityEmberling) target;

				if (emberling.isTamed() && emberling.getOwner() == owner)
					return false;
			}
			if (target instanceof EntityPlayer && owner instanceof EntityPlayer && !((EntityPlayer) owner).canAttackPlayer((EntityPlayer) target))
				return false;
			else
				return !(target instanceof AbstractHorse) || !((AbstractHorse) target).isTame();
		} else
			return false;
	}

	@Override
	public boolean attackEntityAsMob(Entity entity) {
		boolean hitTarget = entity.attackEntityFrom(DamageSource.causeMobDamage(this), (float)((int)this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue()));
		if (hitTarget)
			this.applyEnchantments(this, entity);
		return hitTarget;
	}

	@Override
	public boolean getCanSpawnHere() {
		IBlockState iblockstate = this.world.getBlockState((new BlockPos(this)).down());
		return iblockstate.canEntitySpawn(this) && getEntityWorld().getDifficulty() != EnumDifficulty.PEACEFUL;
	}

	@Override
	public boolean isNotColliding() {
		return getEntityWorld().getCollisionBoxes(this, getEntityBoundingBox()).isEmpty() && getEntityWorld().checkNoEntityCollision(getEntityBoundingBox(), this);
	}

	@Override
	public boolean isInWater() {
		return this.inWater = getEntityWorld().handleMaterialAcceleration(getEntityBoundingBox(), Material.WATER, this);
	}

	@Override
	public boolean isPushedByWater() {
		return false;
	}

	@Override
	public int getMaxSpawnedInChunk() {
		return 3;
	}

	@Override
	protected boolean canDespawn() {
		return !isTamed();
	}

	@Override
	public boolean canBeLeashedTo(EntityPlayer player) {
		return !canDespawn() && super.canBeLeashedTo(player);
	}

	@SideOnly(Side.CLIENT)
	public float smoothedAngle(float partialTicks) {
		return prevAnimationTicks + (animationTicks - prevAnimationTicks) * partialTicks;
	}

	@Override
	public void onUpdate() {
		super.onUpdate();

		updateMovementAndPathfinding();

		if (getEntityWorld().getDifficulty() == EnumDifficulty.PEACEFUL)
			if (!getEntityWorld().isRemote)
				if (!isTamed())
					setDead();

		if (getEntityWorld().isRemote) {
			if (!getIsFlameAttacking())
				if (ticksExisted % 5 == 0)
					if(!isSitting())
						flameParticles(getEntityWorld(), tailPart[0].posX, tailPart[0].posY + 0.25, tailPart[0].posZ, rand);
					else {
						sleepingParticles(getEntityWorld(), tailPart[0].posX, tailPart[0].posY + 0.25, tailPart[0].posZ, rand);
					}

			if (getIsFlameAttacking())
				spawnFlameBreathParticles();
		}
	}

	protected void updateMovementAndPathfinding() {
		if (isInWater())
			moveHelper = moveHelperWater;
		else
			moveHelper = moveHelperLand;

		if (isInWater() && !world.isAirBlock(new BlockPos(posX, getEntityBoundingBox().maxY + 0.25D, posZ)))
			navigator = pathNavigatorWater;
		else
			navigator = pathNavigatorGround;

		//renderYawOffset = rotationYaw;
		double a = Math.toRadians(renderYawOffset);
		double offSetX = -Math.sin(a) * (isSitting() ? -0.2D : 1.85D);
		double offSetZ = Math.cos(a) * (isSitting() ? -0.2D : 1.85D);
		tailPart[0].setLocationAndAngles(posX - offSetX, posY + 0.2D, posZ - offSetZ, 0.0F, 0.0F);
	}

	@Override
	public boolean processInteract(EntityPlayer player, EnumHand hand) {
		ItemStack stack = player.getHeldItem(hand);
		if (!stack.isEmpty()) {
			if (EnumItemMisc.UNDYING_EMBER.isItemOf(stack) && !isTamed()) {
				if (!this.world.isRemote) {
					setTamedBy(player);

					navigator.clearPath();

					setAttackTarget((EntityLivingBase) null);

					if (!player.capabilities.isCreativeMode) {
						stack.shrink(1);
						if (stack.getCount() <= 0)
							player.setHeldItem(hand, ItemStack.EMPTY);
					}

					getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(40D);
					setHealth(40.0F);
				} else {
					playTameEffect(true);
				}
				return true;
			}
			if (isTamed()) {
				if (EnumItemMisc.OCTINE_NUGGET.isItemOf(stack) && isTamed()) {
					if (getHealth() < getMaxHealth()) {
						if (!getEntityWorld().isRemote) {
							heal(5.0f);

							if (!player.capabilities.isCreativeMode) {
								stack.shrink(1);
								if (stack.getCount() <= 0)
									player.setHeldItem(hand, ItemStack.EMPTY);
							}

							if (getHealth() == getMaxHealth()) {
								getEntityWorld().playSound(null, getPosition(), SoundRegistry.EMBERLING_LIVING, SoundCategory.NEUTRAL, 1.0F, 0.75F);
							}
						} else {
							playTameEffect(true);
						}

						return true;
					}
				}
			}
		}

		if (isOwner(player) && !world.isRemote) {
			aiSit.setSitting(!isSitting());
			isJumping = false;
			navigator.clearPath();
			setAttackTarget((EntityLivingBase) null);
		}

		return super.processInteract(player, hand);
	}

	@Override
	public void travel(float strafe,float up, float forward) {
		if (isServerWorld()) {
			if (isInWater()) {
				moveRelative(strafe, up, forward, 0.1F);
				move(MoverType.SELF, motionX, motionY, motionZ);
				motionX *= 0.8999999761581421D;
				motionY *= 0.8999999761581421D;
				motionZ *= 0.8999999761581421D;

				if (getAttackTarget() == null) {
					motionY += 0.003D;
				}
			} else {
				super.travel(strafe, up, forward);
			}
		} else {
			super.travel(strafe, up, forward);
		}
	}

	@SideOnly(Side.CLIENT)
	public void flameParticles(World world, double x, double y, double z, Random rand) {
		for (int count = 0; count < 3; ++count) {
			double velX = 0.0D;
			double velY = 0.0D;
			double velZ = 0.0D;
			int motionX = rand.nextBoolean() ? 1 : - 1;
			int motionZ = rand.nextBoolean() ? 1 : - 1;
			velY = rand.nextFloat() * 0.05D;
			velZ = rand.nextFloat() * 0.025D * motionZ;
			velX = rand.nextFloat() * 0.025D * motionX;
			if(this.inWater) {
				world.spawnAlwaysVisibleParticle(EnumParticleTypes.WATER_BUBBLE.getParticleID(),  x, y, z, velX, velY, velZ);
				world.spawnAlwaysVisibleParticle(EnumParticleTypes.SMOKE_NORMAL.getParticleID(),  x, y, z, velX, velY, velZ);
			} else {
				world.spawnAlwaysVisibleParticle(EnumParticleTypes.FLAME.getParticleID(),  x, y, z, velX, velY, velZ);
			}
		}
	}

	@SideOnly(Side.CLIENT)
	protected void spawnFlameBreathParticles() {
		for (int count = 0; count < 5; ++count) {
			Vec3d look = getLook(1.0F).normalize();
			double a = Math.toRadians(renderYawOffset);
			double offSetX = -Math.sin(a) * 1D;
			double offSetZ = Math.cos(a) * 1D;
			int motionX = rand.nextBoolean() ? 1 : - 1;
			int motionY = rand.nextBoolean() ? 1 : - 1;
			int motionZ = rand.nextBoolean() ? 1 : - 1;
			double velX = rand.nextFloat() * 0.1D * motionX;
			double velY = rand.nextFloat() * 0.1D * motionY;
			double velZ = rand.nextFloat() * 0.1D * motionZ;

			float speed = 0.15F;
			world.spawnAlwaysVisibleParticle(EnumParticleTypes.FLAME.getParticleID(), posX + offSetX + velX, posY + getEyeHeight() * 0.75D + velY, posZ + offSetZ + velZ, look.x * speed, look.y * speed, look.z * speed);
		}
	}
	
	@SideOnly(Side.CLIENT)
	public void sleepingParticles(World world, double x, double y, double z, Random rand) {
		int motionX = rand.nextBoolean() ? 1 : -1;
		int motionZ = rand.nextBoolean() ? 1 : -1;
		double velY = rand.nextFloat() * 0.05D;
		double velZ = rand.nextFloat() * 0.025D * motionZ;
		double velX = rand.nextFloat() * 0.025D * motionX;
		world.spawnAlwaysVisibleParticle(EnumParticleTypes.SMOKE_NORMAL.getParticleID(), x, y, z, velX, velY, velZ);
	}

	@Override
	public World getWorld() {
		return getEntityWorld();
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float damage) {
		if (isEntityInvulnerable(source) || source.equals(DamageSource.IN_WALL) || source.equals(DamageSource.DROWN))
			return false;
		return super.attackEntityFrom(source, damage);
	}

	@Override
	public boolean attackEntityFromPart(MultiPartEntityPart part, DamageSource source, float damage) {
		return false;
	}

	@Override
	public Entity[] getParts(){
		return tailPart;
	}

	@Override
	public float getBlockPathWeight(BlockPos pos) {
		return 0.5F;
	}

	@Override
	public EntityAgeable createChild(EntityAgeable ageable) {
		return null;
	}

	static class AIEmberlingAttack extends EntityAIAttackMelee {

		public AIEmberlingAttack(EntityEmberling emberling) {
			super(emberling, 0.65D, false);
		}

		@Override
		protected double getAttackReachSqr(EntityLivingBase attackTarget) {
			return (double) (4.0F + attackTarget.width);
		}
	}

	static class EntityAIFlameBreath extends EntityAIBase {
		EntityEmberling emberling;
		EntityLivingBase target;
		int missileCount;
		int shootCount;

		public EntityAIFlameBreath(EntityEmberling emberling) {
			this.emberling = emberling;
			setMutexBits(5);
		}

		@Override
		public boolean shouldExecute() {
			target = emberling.getAttackTarget();

			if (target == null || emberling.isInWater() || emberling.isSitting())
				return false;
			else {
				double distance = emberling.getDistanceSq(target);
				if (distance >= 4.0D && distance <= 25.0D) {
					if (!emberling.onGround)
						return false;
					else
						return emberling.getRNG().nextInt(8) == 0;
				} else
					return false;
			}
		}

		@Override
		public boolean shouldContinueExecuting() {
			return shootCount !=-1 && missileCount !=-1 && emberling.recentlyHit <= 40;
		}

		@Override
		public void startExecuting() {
			missileCount = 0;
			shootCount = 0;
			emberling.getEntityWorld().playSound(null, emberling.getPosition(), SoundRegistry.EMBERLING_FLAMES, SoundCategory.HOSTILE, 1F, 1F);
		}

		@Override
		public void resetTask() {
			shootCount = -1;
			missileCount = -1;
			if(emberling.getIsFlameAttacking())
				emberling.setIsFlameAttacking(false);
		}

		@Override
		public void updateTask() {
			if(!emberling.getIsFlameAttacking())
				emberling.setIsFlameAttacking(true);
			emberling.getLookHelper().setLookPositionWithEntity(target, 30.0F, 30.0F);
			float f = (float) MathHelper.atan2(target.posZ - emberling.posZ, target.posX - emberling.posX);
			int distance = MathHelper.floor(emberling.getDistance(target));
			missileCount++;
			if (missileCount %5 == 0) {
				shootCount++;
				double d2 = 1D * (double) (shootCount);
				AxisAlignedBB flameBox = new AxisAlignedBB(new BlockPos(emberling.posX + (double) MathHelper.cos(f) * d2, emberling.posY, emberling.posZ + (double) MathHelper.sin(f) * d2));
				List<EntityLivingBase> list = emberling.getEntityWorld().getEntitiesWithinAABB(EntityLivingBase.class, flameBox);
				for (int entityCount = 0; entityCount < list.size(); entityCount++) {
					Entity entity = list.get(entityCount);
					if (entity != null && entity == target)
						if (entity instanceof EntityLivingBase)
							if(!entity.isBurning())
								entity.setFire(5); // seems ok for time
				}
			}
			if (shootCount >= distance || shootCount >= 4)
				resetTask();
		}
	}

	static class EmberlingMoveHelper extends EntityMoveHelper {
		private final EntityEmberling emberling;

		public EmberlingMoveHelper(EntityEmberling emberling) {
			super(emberling);
			this.emberling = emberling;
		}

		@Override
		public void onUpdateMoveHelper() {
			if (action == EntityMoveHelper.Action.MOVE_TO && !emberling.getNavigator().noPath()) {
				double d0 = posX - emberling.posX;
				double d1 = posY - emberling.posY;
				double d2 = posZ - emberling.posZ;
				double d3 = d0 * d0 + d1 * d1 + d2 * d2;
				d3 = (double) MathHelper.sqrt(d3);
				d1 = d1 / d3;
				float f = (float) (MathHelper.atan2(d2, d0) * (180D / Math.PI)) - 90.0F;
				emberling.rotationYaw = limitAngle(emberling.rotationYaw, f, 90.0F);
				emberling.renderYawOffset = emberling.rotationYaw;
				float f1 = (float) (speed * emberling.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getAttributeValue());
				emberling.setAIMoveSpeed(emberling.getAIMoveSpeed() + (f1 - emberling.getAIMoveSpeed()) * 0.125F);
				double d4 = Math.sin((double) (emberling.ticksExisted + emberling.getEntityId()) * 0.5D) * 0.05D;
				double d5 = Math.cos((double) (emberling.rotationYaw * 0.017453292F));
				double d6 = Math.sin((double) (emberling.rotationYaw * 0.017453292F));
				emberling.motionX += d4 * d5;
				emberling.motionZ += d4 * d6;
				d4 = Math.sin((double) (emberling.ticksExisted + emberling.getEntityId()) * 0.75D) * 0.05D;
				emberling.motionY += d4 * (d6 + d5) * 0.25D;
				if (Math.abs(emberling.motionY) < 0.35) {
					emberling.motionY += (double) emberling.getAIMoveSpeed() * d1 * 0.1D * (2 + (d1 > 0 ? 0.4 : 0) + (emberling.collidedHorizontally ? 20 : 0));
				}
				EntityLookHelper entitylookhelper = emberling.getLookHelper();
				double d7 = emberling.posX + d0 / d3 * 2.0D;
				double d8 = (double) emberling.getEyeHeight() + emberling.posY + d1 / d3;
				double d9 = emberling.posZ + d2 / d3 * 2.0D;
				double d10 = entitylookhelper.getLookPosX();
				double d11 = entitylookhelper.getLookPosY();
				double d12 = entitylookhelper.getLookPosZ();

				if (!entitylookhelper.getIsLooking()) {
					d10 = d7;
					d11 = d8;
					d12 = d9;
				}

				emberling.getLookHelper().setLookPosition(d10 + (d7 - d10) * 0.125D, d11 + (d8 - d11) * 0.125D, d12 + (d9 - d12) * 0.125D, 10.0F, 40.0F);
			} else {
				emberling.setAIMoveSpeed(0.0F);
			}
		}
	}
}