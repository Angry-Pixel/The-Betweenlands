package thebetweenlands.common.entity.mobs;

import java.util.UUID;

import javax.annotation.Nullable;

import com.google.common.base.Optional;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIAvoidEntity;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAITarget;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.server.management.PreYggdrasilConverter;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.entity.ai.EntityAIFlyingWander;
import thebetweenlands.common.entity.ai.PathNavigateFlyingBL;
import thebetweenlands.common.entity.movement.FlightMoveHelper;
import thebetweenlands.common.registries.SoundRegistry;

public class EntityChiromawTame extends EntityCreature {

	private static final DataParameter<Boolean> ATTACKING = EntityDataManager.<Boolean>createKey(EntityChiromawTame.class, DataSerializers.BOOLEAN);
	 protected static final DataParameter<Optional<UUID>> OWNER_UNIQUE_ID = EntityDataManager.<Optional<UUID>>createKey(EntityChiromawTame.class, DataSerializers.OPTIONAL_UNIQUE_ID);

	public EntityChiromawTame(World world) {
		super(world);
		setSize(0.7F, 0.9F);
		moveHelper = new FlightMoveHelper(this);
	}
	
	@Override
	protected void initEntityAI() {
		tasks.addTask(0, new EntityAISwimming(this));
		tasks.addTask(1, new EntityAIAttackMelee(this, 0.5D, true));
		tasks.addTask(2, new EntityAIFlyingWander(this, 0.5D));
		tasks.addTask(3, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F));
		tasks.addTask(4, new EntityAILookIdle(this));
        tasks.addTask(5, new EntityAIAvoidEntity(this, EntityMob.class, 4.0F, 0.75D, 0.75D));
		tasks.addTask(1, new EntityChiromawTame.AIFollowOwner(this, 1.0D, 10.0F, 2.0F));
		targetTasks.addTask(1, new EntityChiromawTame.AIChiromawOwnerHurtByTarget(this));
        targetTasks.addTask(2, new EntityChiromawTame.AIChiromawOwnerHurtTarget(this));
        targetTasks.addTask(3, new EntityAIHurtByTarget(this, true, new Class[0]));
        targetTasks.addTask(4, new EntityAINearestAttackableTarget(this, EntityMob.class, true));
	}

	@Override
	protected void entityInit() {
		super.entityInit();
        dataManager.register(ATTACKING, Boolean.valueOf(false));
		dataManager.register(OWNER_UNIQUE_ID, Optional.<UUID>absent());
	}

    @SideOnly(Side.CLIENT)
    public boolean isAttacking() {
        return ((Boolean)dataManager.get(ATTACKING)).booleanValue();
    }

    public void setAttacking(boolean attacking) {
        dataManager.set(ATTACKING, Boolean.valueOf(attacking));
    }
	
	@Override
	public void writeEntityToNBT(NBTTagCompound nbt) {
		super.writeEntityToNBT(nbt);
		if (getOwnerId() == null)
			nbt.setString("OwnerUUID", "");
		else
			nbt.setString("OwnerUUID", getOwnerId().toString());
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound nbt) {
		super.readEntityFromNBT(nbt);
		String s;
		if (nbt.hasKey("OwnerUUID", 8))
			s = nbt.getString("OwnerUUID");
		else {
			String s1 = nbt.getString("Owner");
			s = PreYggdrasilConverter.convertMobOwnerIfNeeded(getServer(), s1);
		}

		if (!s.isEmpty()) {
			try {
				setOwnerId(UUID.fromString(s));
			} catch (Throwable e) {
			}
		}
	}

	@Nullable
	public UUID getOwnerId() {
		return (UUID) ((Optional) dataManager.get(OWNER_UNIQUE_ID)).orNull();
	}

	public void setOwnerId(@Nullable UUID uuid) {
		dataManager.set(OWNER_UNIQUE_ID, Optional.fromNullable(uuid));
	}

	@Nullable
	public EntityLivingBase getOwner() {
		try {
			UUID uuid = getOwnerId();
			return uuid == null ? null : getEntityWorld().getPlayerEntityByUUID(uuid);
		} catch (IllegalArgumentException e) {
			return null;
		}
	}

	public boolean isOwner(EntityLivingBase entityIn) {
		return entityIn == getOwner();
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(10.0D);
		getAttributeMap().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(2.0D);
		getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.095D);
		getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(32.0D);
	}
	
	@Override
    protected boolean canDespawn() {
        return false;
    }

	@Override
	protected SoundEvent getAmbientSound() {
		return SoundRegistry.FLYING_FIEND_LIVING;
	}

	@Nullable
	@Override
	protected SoundEvent getHurtSound(DamageSource p_184601_1_) {
		return SoundRegistry.FLYING_FIEND_HURT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundRegistry.FLYING_FIEND_DEATH;
	}

	@Override
	public void onUpdate() {


		if (!world.isRemote && world.getDifficulty() == EnumDifficulty.PEACEFUL) {
			setDead();
		}

		if (isJumping && isInWater()) {
			//Moving out of water
			getMoveHelper().setMoveTo(posX, posY + 1, posZ, 1.0D);
		}
/*
		if (getIsHanging()) {
			motionX = motionY = motionZ = 0.0D;
			posY = (double) MathHelper.floor(posY) + 1.0D - (double) height;
		}
*/
		if (motionY < 0.0D && getAttackTarget() == null) {
			motionY *= 0.25D;
		}

		if(getEntityWorld().getBlockState(getPosition().down()).isSideSolid(getEntityWorld(), getPosition().down(), EnumFacing.UP)) {
			getMoveHelper().setMoveTo(posX, posY + 1, posZ, 1.0D);
		}

		super.onUpdate();
	}

	public boolean shouldAttackEntity(EntityLivingBase entityTarget, EntityLivingBase entityTarget2) {
		if (!(entityTarget instanceof EntityCreeper) && !(entityTarget instanceof EntityGhast)) {
			if (entityTarget instanceof EntityChiromawTame) {
				EntityChiromawTame chiromawIn = (EntityChiromawTame) entityTarget;

				if (chiromawIn.getOwner() == entityTarget2) {
					return false;
				}
			}
			return entityTarget instanceof EntityPlayer && entityTarget2 instanceof EntityPlayer && !((EntityPlayer) entityTarget2).canAttackPlayer((EntityPlayer) entityTarget) ? false : !(entityTarget instanceof EntityHorse) || !((EntityHorse) entityTarget).isTame();
		} else {
			return false;
		}
	}

	@Override
	public boolean attackEntityAsMob(Entity entity) { 
		boolean flag = entity.attackEntityFrom(DamageSource.causeMobDamage(this), (float) ((int) getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue()));

		if (flag)
			applyEnchantments(this, entity);

		return flag;
	}

	@Override
	protected PathNavigate createNavigator(World world) {
		return new PathNavigateFlyingBL(this, world, 2);
	}

	@Override
	public void fall(float distance, float damageMultiplier) {
	}

	@Override
	protected void updateFallState(double y, boolean onGroundIn, IBlockState state, BlockPos pos) {
	}

	@Override
	public void travel(float strafe, float vertical, float forward) {
		if (isInWater()) {
			moveRelative(strafe, vertical, forward, 0.02F);
			move(MoverType.SELF, motionX, motionY, motionZ);
			motionX *= 0.800000011920929D;
			motionY *= 0.800000011920929D;
			motionZ *= 0.800000011920929D;
		} else if (isInLava()) {
			moveRelative(strafe, vertical, forward, 0.02F);
			move(MoverType.SELF, motionX, motionY, motionZ);
			motionX *= 0.5D;
			motionY *= 0.5D;
			motionZ *= 0.5D;
		} else {
			float f = 0.91F;

			if (onGround) {
				BlockPos underPos = new BlockPos(MathHelper.floor(posX), MathHelper.floor(getEntityBoundingBox().minY) - 1, MathHelper.floor(posZ));
				IBlockState underState = world.getBlockState(underPos);
				f = underState.getBlock().getSlipperiness(underState, world, underPos, this) * 0.91F;
			}

			float f1 = 0.16277136F / (f * f * f);
			moveRelative(strafe, vertical, forward, onGround ? 0.1F * f1 : 0.02F);
			f = 0.91F;

			if (onGround) {
				BlockPos underPos = new BlockPos(MathHelper.floor(posX), MathHelper.floor(getEntityBoundingBox().minY) - 1, MathHelper.floor(posZ));
				IBlockState underState = world.getBlockState(underPos);
				f = underState.getBlock().getSlipperiness(underState, world, underPos, this) * 0.91F;
			}

			move(MoverType.SELF, motionX, motionY, motionZ);
			motionX *= (double) f;
			motionY *= (double) f;
			motionZ *= (double) f;
		}

		prevLimbSwingAmount = limbSwingAmount;
		double d1 = posX - prevPosX;
		double d0 = posZ - prevPosZ;
		float f2 = MathHelper.sqrt(d1 * d1 + d0 * d0) * 4.0F;

		if (f2 > 1.0F) {
			f2 = 1.0F;
		}

		limbSwingAmount += (f2 - limbSwingAmount) * 0.4F;
		limbSwing += limbSwingAmount;
	}

	@Override
	public boolean isOnLadder() {
		return false;
	}
	
	class AIFollowOwner extends EntityAIBase {
		private final EntityChiromawTame chiromaw;
		private EntityLivingBase owner;
		World world;
		private final double followSpeed;
		private final PathNavigate petPathfinder;
		private int timeToRecalcPath;
		float maxDist;
		float minDist;
		private float oldWaterCost;

		public AIFollowOwner(EntityChiromawTame chiromawIn, double followSpeedIn, float minDistIn, float maxDistIn) {
			chiromaw = chiromawIn;
			world = chiromawIn.getEntityWorld();
			followSpeed = followSpeedIn;
			petPathfinder = chiromawIn.getNavigator();
			minDist = minDistIn;
			maxDist = maxDistIn;
			setMutexBits(3);

			if (!(chiromawIn.getNavigator() instanceof PathNavigateFlyingBL)) {
				throw new IllegalArgumentException("Unsupported mob type for FollowOwnerGoal");
			}
		}

		@Override
		public boolean shouldExecute() {
			EntityLivingBase entitylivingbase = chiromaw.getOwner();

			if (entitylivingbase == null)
				return false;
			else if (entitylivingbase instanceof EntityPlayer && ((EntityPlayer) entitylivingbase).isSpectator())
				return false;
			else if (chiromaw.getDistanceSq(entitylivingbase) < (double) (minDist * minDist))
				return false;
			else {
				owner = entitylivingbase;
				return true;
			}
		}

		@Override
		public boolean shouldContinueExecuting() {
			return !petPathfinder.noPath() && chiromaw.getDistanceSq(owner) > (double) (maxDist * maxDist);
		}

		@Override
		public void startExecuting() {
			timeToRecalcPath = 0;
			oldWaterCost = chiromaw.getPathPriority(PathNodeType.WATER);
			chiromaw.setPathPriority(PathNodeType.WATER, 0.0F);
		}

		@Override
		public void resetTask() {
			owner = null;
			petPathfinder.clearPath();
			chiromaw.setPathPriority(PathNodeType.WATER, oldWaterCost);
		}

		private boolean isEmptyBlock(BlockPos pos) {
			IBlockState iblockstate = world.getBlockState(pos);
			return iblockstate.getMaterial() == Material.AIR ? true : !iblockstate.isFullCube();
		}

		@Override
		public void updateTask() {
			chiromaw.getLookHelper().setLookPositionWithEntity(owner, 10.0F, (float) chiromaw.getVerticalFaceSpeed());

			if (--timeToRecalcPath <= 0) {
				timeToRecalcPath = 10;

				if (!petPathfinder.tryMoveToEntityLiving(owner, followSpeed)) {
					if (!chiromaw.getLeashed()) {
						if (chiromaw.getDistanceSq(owner) >= 144.0D) {
							int i = MathHelper.floor(owner.posX) - 2;
							int j = MathHelper.floor(owner.posZ) - 2;
							int k = MathHelper.floor(owner.getEntityBoundingBox().minY);

							for (int l = 0; l <= 4; ++l) {
								for (int i1 = 0; i1 <= 4; ++i1) {
									if ((l < 1 || i1 < 1 || l > 3 || i1 > 3) && isTeleportFriendlyBlock(i, j, k, l, i1)) {
										chiromaw.setLocationAndAngles((double) ((float) (i + l) + 0.5F), (double) k, (double) ((float) (j + i1) + 0.5F), chiromaw.rotationYaw, chiromaw.rotationPitch);
										petPathfinder.clearPath();
										return;
									}
								}
							}
						}
					}
				}
			}
		}

		protected boolean isTeleportFriendlyBlock(int x, int z, int y, int xOffset, int zOffset) {
			BlockPos blockpos = new BlockPos(x + xOffset, y - 1, z + zOffset);
	        IBlockState iblockstate = world.getBlockState(blockpos);
	        return iblockstate.getBlockFaceShape(world, blockpos, EnumFacing.DOWN) == BlockFaceShape.SOLID && iblockstate.canEntitySpawn(chiromaw) && world.isAirBlock(blockpos.up()) && world.isAirBlock(blockpos.up(2));
	    }
	}

	class AIChiromawOwnerHurtByTarget extends EntityAITarget {
		EntityChiromawTame chiromaw;
		EntityLivingBase attacker;
		private int timestamp;

		public AIChiromawOwnerHurtByTarget(EntityChiromawTame theDefendingChiromaw) {
			super(theDefendingChiromaw, false);
			chiromaw = theDefendingChiromaw;
			setMutexBits(1);
		}

		@Override
		public boolean shouldExecute() {
			EntityLivingBase entity = chiromaw.getOwner();
			if (entity == null)
				return false;
			else {
				attacker = entity.getRevengeTarget();
				int i = entity.getRevengeTimer();
				return i != timestamp && isSuitableTarget(attacker, false) && chiromaw.shouldAttackEntity(attacker, entity);
			}
		}

		@Override
		public void startExecuting() {
			taskOwner.setAttackTarget(attacker);
			EntityLivingBase entity = chiromaw.getOwner();
			if (entity != null)
				timestamp = entity.getRevengeTimer();
			super.startExecuting();
		}
	}

	 class AIChiromawOwnerHurtTarget extends EntityAITarget {
		 EntityChiromawTame chiromaw;
			EntityLivingBase target;
			private int timestamp;

			public AIChiromawOwnerHurtTarget(EntityChiromawTame chirowmawIn) {
				super(chirowmawIn, false);
				chiromaw = chirowmawIn;
				setMutexBits(1);
			}

			@Override
			public boolean shouldExecute() {
				EntityLivingBase entitylivingbase = chiromaw.getOwner();
				if (entitylivingbase == null)
					return false;
				else {
					target = entitylivingbase.getRevengeTarget();
					int i = entitylivingbase.getRevengeTimer();
					return i != timestamp && isSuitableTarget(target, false) && chiromaw.shouldAttackEntity(target, entitylivingbase);
				}
			}

			@Override
			public void startExecuting() {
				taskOwner.setAttackTarget(target);
				EntityLivingBase entitylivingbase = chiromaw.getOwner();
				if (entitylivingbase != null)
					timestamp = entitylivingbase.getRevengeTimer();
				super.startExecuting();
			}
		}
}