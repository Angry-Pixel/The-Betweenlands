package thebetweenlands.common.entity.mobs;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.particle.Particle;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.api.entity.IEntityBL;
import thebetweenlands.api.entity.IEntityScreenShake;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.client.render.particle.BatchedParticleRenderer;
import thebetweenlands.client.render.particle.DefaultParticleBatches;
import thebetweenlands.client.render.particle.ParticleFactory.ParticleArgs;
import thebetweenlands.common.block.container.BlockLootUrn;
import thebetweenlands.common.block.container.BlockMudBrickAlcove;
import thebetweenlands.common.entity.EntityShockwaveBlock;
import thebetweenlands.common.entity.ai.IPathObstructionCallback;
import thebetweenlands.common.entity.ai.PathNavigateBarrishee;
import thebetweenlands.common.registries.LootTableRegistry;
import thebetweenlands.common.registries.SoundRegistry;
import thebetweenlands.common.tile.TileEntityMudBrickAlcove;
import thebetweenlands.common.world.gen.feature.structure.utils.SludgeWormMazeBlockHelper;
import thebetweenlands.common.world.storage.location.LocationGuarded;

public class EntityBarrishee extends EntityMob implements IEntityScreenShake, IEntityBL, IPathObstructionCallback {
	private static final DataParameter<Boolean> AMBUSH_SPAWNED = EntityDataManager.createKey(EntityBarrishee.class, DataSerializers.BOOLEAN);
	private static final DataParameter<Boolean> SCREAM = EntityDataManager.createKey(EntityBarrishee.class, DataSerializers.BOOLEAN);
	private static final DataParameter<Integer> SCREAM_TIMER = EntityDataManager.createKey(EntityBarrishee.class, DataSerializers.VARINT);
	private static final DataParameter<Boolean> SCREAM_BEAM = EntityDataManager.createKey(EntityBarrishee.class, DataSerializers.BOOLEAN);
	private static final DataParameter<Boolean> SLAMMING_ANIMATION = EntityDataManager.createKey(EntityBarrishee.class, DataSerializers.BOOLEAN);

	public float standingAngle, prevStandingAngle;

	private SludgeWormMazeBlockHelper blockHelper = new SludgeWormMazeBlockHelper(null);

	//Scream timer is only used for the screen shake and is client side only.
	private int prevScreamTimer;
	public int screamTimer;

	//Adjust to length of screaming sound
	private int screamingTimerMax = 50;

	protected int actionCooldown = 0;

	public EntityBarrishee(World world) {
		super(world);
		this.setSize(2.25F, 1.8F);
		this.experienceValue = 150;
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		dataManager.register(AMBUSH_SPAWNED, false);
		dataManager.register(SCREAM, false);
		dataManager.register(SCREAM_TIMER, 50);
		dataManager.register(SCREAM_BEAM, false);
		dataManager.register(SLAMMING_ANIMATION, false);
	}

	@Override
	protected void initEntityAI() {
		tasks.addTask(1, new EntityAISwimming(this));
		tasks.addTask(2, new EntityBarrishee.AIBlockBreakAttack(this, 60, 100));
		tasks.addTask(3, new EntityBarrishee.AISonicAttack(this, 32, 50));
		tasks.addTask(4, new EntityBarrishee.AISlamAttack(this, 22, 40));
		tasks.addTask(5, new EntityBarrishee.AIBarrisheeAttack(this));
		tasks.addTask(6, new EntityAIWander(this, 0.8D, 50));
		tasks.addTask(7, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
		tasks.addTask(8, new EntityAILookIdle(this));

		targetTasks.addTask(0, new EntityAINearestAttackableTarget<>(this, EntityPlayer.class, 0, true, true, null).setUnseenMemoryTicks(1200));
		targetTasks.addTask(3, new EntityAIHurtByTarget(this, true));
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.24D);
		getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(200D);
		getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(7.0D);
		getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(24.0D);
		getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(0.75D);
	}

	@Override
	protected PathNavigate createNavigator(World worldIn) {
		return new PathNavigateBarrishee(this, worldIn);
	}

	@Override
	protected ResourceLocation getLootTable() {
		return LootTableRegistry.BARRISHEE;
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return SoundRegistry.BARRISHEE_LIVING;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
		return SoundRegistry.BARRISHEE_HURT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundRegistry.BARRISHEE_DEATH;
	}

	@Override
	protected void playStepSound(BlockPos pos, Block blockIn) {
		playSound(SoundRegistry.BARRISHEE_STEP, 0.5F, 1F);
	}

	protected SoundEvent getScreamSound() {
		return SoundRegistry.BARRISHEE_SCREAM;
	}

	public boolean isAmbushSpawn() {
		return dataManager.get(AMBUSH_SPAWNED);
	}

	public void setIsAmbushSpawn(boolean is_ambush) {
		dataManager.set(AMBUSH_SPAWNED, is_ambush);
	}

	public void setIsScreaming(boolean scream) {
		dataManager.set(SCREAM, scream);
	}

	public boolean isScreaming() {
		return dataManager.get(SCREAM);
	}

	public void setScreamTimer(int scream_timer) {
		dataManager.set(SCREAM_TIMER, scream_timer);
	}

	public int getScreamTimer() {
		return dataManager.get(SCREAM_TIMER);
	}

	public void setIsScreamingBeam(boolean scream_beam) {
		dataManager.set(SCREAM_BEAM, scream_beam);
	}

	public boolean isScreamingBeam() {
		return dataManager.get(SCREAM_BEAM);
	}

	public void setIsSlamming(boolean slamming) {
		dataManager.set(SLAMMING_ANIMATION, slamming);
	}

	public boolean isSlamming() {
		return dataManager.get(SLAMMING_ANIMATION);
	}

	@Override
	public boolean isNotColliding() {
		return !getEntityWorld().containsAnyLiquid(getEntityBoundingBox()) && getEntityWorld().getCollisionBoxes(this, getEntityBoundingBox()).isEmpty() && getEntityWorld().checkNoEntityCollision(getEntityBoundingBox(), this);
	}

	@Override
	public int getMaxSpawnedInChunk() {
		return 1;
	}

	@SideOnly(Side.CLIENT)
	public float getSmoothedStandingAngle(float partialTicks) {
		return prevStandingAngle + (standingAngle - prevStandingAngle) * partialTicks;
	}

	@Override
	public void onLivingUpdate() {
		if (getEntityWorld().isRemote && !isSlamming()) {
			prevStandingAngle = standingAngle;

			if (standingAngle <= 0.1F)
				standingAngle += isAmbushSpawn() ? 0.01F : 0.2F;
			if (standingAngle > 0.1F && standingAngle <= 1F)
				standingAngle += isAmbushSpawn() ? 0.1F : 0.2F;

			if (standingAngle > 1F) {
				standingAngle = 1F;

				if (isAmbushSpawn())
					setIsAmbushSpawn(false);
			}
		}

		if (getEntityWorld().isRemote && isSlamming()) {
			prevStandingAngle = standingAngle;

			if (standingAngle >= 0F && standingAngle <= 1F)
				standingAngle -= 0.2F;

			if (standingAngle < 0F) {
				setIsSlamming(false);
				standingAngle = 0F;
			}
		}

		prevScreamTimer = getScreamTimer();
		if (!getEntityWorld().isRemote) {
			if (getScreamTimer() == 0) {
				setIsScreaming(true);
				setScreamTimer(1);
			}

			if (getScreamTimer() == 1) {
				getEntityWorld().playSound(null, getPosition(), getScreamSound(), SoundCategory.HOSTILE, 0.75F, 0.5F);
			}

			if (getScreamTimer() > 0 && getScreamTimer() <= screamingTimerMax) {
				setScreamTimer(getScreamTimer() + 1);
			}

			setIsScreaming(getScreamTimer() < screamingTimerMax);

			if(getAttackTarget() != null) {
				getLookHelper().setLookPositionWithEntity(getAttackTarget(), 100F, 100F);

				if(isScreaming()) {
					getNavigator().setPath(getNavigator().getPathToEntityLiving(getAttackTarget()), 0);
				}
			}
		}

		if(this.world.isRemote && this.isScreamingBeam()) {
			this.spawnScreamParticles();
		}

		if(!this.world.isRemote && this.isScreaming() && !this.isScreamingBeam() && this.getScreamTimer() >= 25) {
			breakBlocksForAOEScream(getAOEScreamBounds());
		}

		if(!this.world.isRemote) {
			if(this.isDoingSpecialAttack()) {
				this.actionCooldown = 60;
			} else if(this.actionCooldown > 0) {
				this.actionCooldown--;
			}
		}

		super.onLivingUpdate();
	}

	@Override
	public void onUpdate() {
		super.onUpdate();

		this.pushEntitiesAway();
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBox() {
		return this.getEntityBoundingBox();
	}

	@Override
	public AxisAlignedBB getCollisionBox(Entity entityIn) {
		if(entityIn.canBeCollidedWith()) {
			AxisAlignedBB collisionBox = entityIn.getCollisionBoundingBox();
			return collisionBox != null ? collisionBox : entityIn.getEntityBoundingBox();
		}

		return null;
	}

	@Override
	public boolean canBePushed() {
		return false;
	}

	@Override
	public void move(MoverType type, double x, double y, double z) {
		super.move(type, x, y, z);
	}

	protected void pushEntitiesAway() {
		AxisAlignedBB collisionAABB = this.getCollisionBoundingBox().offset(this.motionX, this.motionY, this.motionZ);
		if(collisionAABB != null) {
			List<Entity> entities = this.world.getEntitiesWithinAABBExcludingEntity(this, collisionAABB);

			for(Entity entity : entities) {
				if(entity.canBeCollidedWith() && entity.canBePushed()) {
					AxisAlignedBB entityAABB = this.getCollisionBox(entity);

					if(entityAABB != null) {
						double dx = Math.max(collisionAABB.minX - entityAABB.maxX, entityAABB.minX - collisionAABB.maxX);
						double dz = Math.max(collisionAABB.minZ - entityAABB.maxZ, entityAABB.minZ - collisionAABB.maxZ);

						if(Math.abs(dz) < Math.abs(dx)) {
							entity.move(MoverType.PISTON, 0, 0, (dz - 0.005D) * Math.signum(this.posZ - entity.posZ));
						} else {
							entity.move(MoverType.PISTON, (dx - 0.005D) * Math.signum(this.posX - entity.posX), 0, 0);
						}

						//Move slightly towards ground to update onGround state etc.
						entity.move(MoverType.PISTON, 0, -0.01D, 0);
					}
				}
			}
		}
	}

	private void breakBlocksForAOEScream(AxisAlignedBB aoeScreamBounds) {
		if(ForgeEventFactory.getMobGriefingEvent(this.world, this)) {
			int minX = MathHelper.floor(aoeScreamBounds.minX);
			int minY = MathHelper.floor(aoeScreamBounds.minY);
			int minZ = MathHelper.floor(aoeScreamBounds.minZ);
			int maxX = MathHelper.floor(aoeScreamBounds.maxX);
			int maxY = MathHelper.floor(aoeScreamBounds.maxY);
			int maxZ = MathHelper.floor(aoeScreamBounds.maxZ);

			for (int sizeX = minX; sizeX <= maxX; ++sizeX) {
				for (int sizeZ = minZ; sizeZ <= maxZ; ++sizeZ) {
					for (int sizeY = minY; sizeY <= maxY; ++sizeY) {
						BlockPos pos = new BlockPos(sizeX, sizeY, sizeZ);
						IBlockState state = getEntityWorld().getBlockState(pos);

						if(state.getBlock() instanceof BlockMudBrickAlcove && checkAlcoveForUrn(this.world, state)) {
							setAlcoveUrnEmpty(this.world, pos, state);
						}

						if(state.getBlockHardness(this.world, pos) >= 0 && state.getBlock().canEntityDestroy(state, this.world, pos, this)) {
							if(state.getBlock() instanceof BlockLootUrn) {
								spawnAshSpriteMinion(getEntityWorld(), pos, state);
								this.world.destroyBlock(pos, true);
							} else if(!LocationGuarded.isLocationGuarded(this.world, this, pos) && ForgeEventFactory.onEntityDestroyBlock(this, pos, state)) {
								this.world.destroyBlock(pos, true);
							}
						}
					}
				}
			}
		}
	}

	@Nullable
	public static TileEntityMudBrickAlcove getTileEntity(IBlockAccess world, BlockPos pos) {
		TileEntity tile = world.getTileEntity(pos);
		if(tile instanceof TileEntityMudBrickAlcove) {
			return (TileEntityMudBrickAlcove) tile;
		}
		return null;
	}

	private void setAlcoveUrnEmpty(World world, BlockPos pos, IBlockState state) {
		BlockPos offsetPos = pos;
		TileEntityMudBrickAlcove tile = getTileEntity(world, pos);
		if (tile instanceof TileEntityMudBrickAlcove) {
			if (tile.hasUrn) {
				IInventory tileInv = (IInventory) tile;
				if (tileInv != null) {
					EnumFacing facing = state.getValue(BlockMudBrickAlcove.FACING);
					offsetPos = pos.offset(facing);
					InventoryHelper.dropInventoryItems(world, offsetPos, tileInv);
				}
				spawnAshSpriteMinion(getEntityWorld(), pos, state);
				world.playEvent(null, 2001, pos, Block.getIdFromBlock(state.getBlock()));
				tile.hasUrn = false;
				world.notifyBlockUpdate(pos, state, state, 2);
			}
		}
	}

	private void spawnAshSpriteMinion(World world, BlockPos pos, IBlockState state) {
		BlockPos offsetPos = pos;
		if (state.getBlock() instanceof BlockMudBrickAlcove) {
			EnumFacing facing = state.getValue(BlockMudBrickAlcove.FACING);
			offsetPos = pos.offset(facing);
		}
		EntityAshSprite entity = new EntityAshSprite(world);
		entity.setLocationAndAngles(offsetPos.getX() + 0.5D, offsetPos.getY(), offsetPos.getZ() + 0.5D, 0.0F, 0.0F);
		entity.setBoundOrigin(offsetPos);
		world.spawnEntity(entity);
	}

	private boolean checkAlcoveForUrn(World entityWorld, IBlockState state) {
		return state.getValue(BlockMudBrickAlcove.HAS_URN);	
	}

	@SideOnly(Side.CLIENT)
	protected void spawnScreamParticles() {
		Vec3d look = getLook(1.0F).normalize();
		float speed = 0.6f;
		Particle particle = BLParticles.SONIC_SCREAM.create(this.world, this.posX, this.posY + (getScreamTimer() < 25 ? 0.8 + (getScreamTimer() * 0.0125F) : 1.25 - (25 - getScreamTimer()) * 0.025F), this.posZ, 
				ParticleArgs.get().withMotion(look.x * speed, look.y * speed, look.z * speed).withScale(10).withData(30, MathHelper.floor(this.ticksExisted * 3.3f))
				.withColor(1.0f, 0.9f, 0.8f, 1.0f));
		BatchedParticleRenderer.INSTANCE.addParticle(DefaultParticleBatches.TRANSLUCENT_GLOWING, particle);
	}

	private boolean isLookingAtAttackTarget(Entity entity) {
		Vec3d vec3d = getLook(1.0F).normalize();
		Vec3d vec3d1 = new Vec3d(entity.posX - posX, entity.getEntityBoundingBox().minY + (double) entity.getEyeHeight() - (posY + (double) getEyeHeight()), entity.posZ - posZ);
		double d0 = vec3d1.length();
		vec3d1 = vec3d1.normalize();
		double d1 = vec3d.dotProduct(vec3d1);
		return d1 > 1.0D - 0.025D / d0 ? canEntityBeSeen(entity) : false;
	}

	@Override
	protected boolean isMovementBlocked() {
		return super.isMovementBlocked();// || isScreaming() && !isScreamingBeam();// || true;
	}

	public float getScreamingProgress(float delta) {
		return 1.0F / screamingTimerMax * (prevScreamTimer + (screamTimer - prevScreamTimer) * delta);
	}

	public AxisAlignedBB getAOEScreamBounds() {
		float boxsizeUnit = 0.0275F * getScreamTimer();
		AxisAlignedBB bounds = getEntityBoundingBox();
		return bounds.grow(boxsizeUnit, 0, boxsizeUnit).expand(0, 1, 0);
	}

	public boolean isDoingSpecialAttack() {
		return this.isScreaming() || this.isScreamingBeam() || this.isSlamming();
	}

	public boolean isReadyForSpecialAttack() {
		if(this.isDoingSpecialAttack()) {
			return false;
		} else {
			return this.actionCooldown <= 0;
		}
	}

	/**
	 * Called by barrishee path navigator if a path is obstructed and the barrishee becomes stuck
	 */
	@Override
	public void onPathingObstructed() {
		if(this.getAttackTarget() != null && this.isReadyForSpecialAttack()) {
			this.setScreamTimer(0);
		}
	}

	@Override
	public float getShakeIntensity(Entity viewer, float partialTicks) {
		if(isScreaming()) {
			double dist = getDistance(viewer);
			float screamMult = (float) (1.0F - dist / 30.0F);
			if(dist >= 30.0F) {
				return 0.0F;
			}
			return (float) ((Math.sin(getScreamingProgress(partialTicks) * Math.PI) + 0.1F) * 0.15F * screamMult);
		} else {
			return 0.0F;
		}
	}

	protected static final int MUTEX_SPECIAL_ATTACK = 0b1000;

	static class AIBlockBreakAttack extends EntityAIBase {
		EntityBarrishee barrishee;
		int cooldown, minCooldown, maxCooldown;

		public AIBlockBreakAttack(EntityBarrishee barrishee, int minCooldown, int maxCooldown) {
			this.barrishee = barrishee;
			this.minCooldown = minCooldown;
			this.maxCooldown = maxCooldown;
			this.cooldown = minCooldown + barrishee.rand.nextInt(maxCooldown - minCooldown);
			this.setMutexBits(MUTEX_SPECIAL_ATTACK | 1 | 2);
		}

		@Override
		public boolean shouldExecute() {
			if(barrishee.getAttackTarget() != null && barrishee.isReadyForSpecialAttack()) {
				return this.cooldown-- < 0;
			}

			return false;
		}

		@Override
		public boolean shouldContinueExecuting() {
			return false;
		}

		@Override
		public void startExecuting() {
			barrishee.setScreamTimer(0);
		}

		@Override
		public void resetTask() {
			cooldown = minCooldown + barrishee.rand.nextInt(maxCooldown - minCooldown);
		}
	}

	static class AISonicAttack extends EntityAIBase {
		EntityBarrishee barrishee;
		int cooldown, minCooldown, maxCooldown;
		int missileCount;
		int shootCount;

		public AISonicAttack(EntityBarrishee barrishee, int minCooldown, int maxCooldown) {
			this.barrishee = barrishee;
			this.minCooldown = minCooldown;
			this.maxCooldown = maxCooldown;
			this.cooldown = minCooldown + barrishee.rand.nextInt(maxCooldown - minCooldown);
			this.setMutexBits(MUTEX_SPECIAL_ATTACK | 1 | 2);
		}

		@Override
		public boolean shouldExecute() {
			EntityLivingBase target = barrishee.getAttackTarget();

			if(target != null) {
				double distance = barrishee.getDistanceSq(target);

				if (distance >= 9.0D && distance <= 144.0D && barrishee.onGround && barrishee.isReadyForSpecialAttack() && barrishee.isLookingAtAttackTarget(target)) {
					return this.cooldown-- < 0;
				}
			}

			return false;
		}

		@Override
		public boolean shouldContinueExecuting() {
			return barrishee.getAttackTarget() != null && shootCount != -1 && missileCount != -1;
		}

		@Override
		public void startExecuting() {
			missileCount = 0;
			shootCount = 0;
			barrishee.getEntityWorld().playSound(null, barrishee.getPosition(), barrishee.getScreamSound(), SoundCategory.HOSTILE, 0.75F, 0.5F);
			barrishee.setScreamTimer(0);
		}

		@Override
		public void resetTask() {
			cooldown = minCooldown + barrishee.rand.nextInt(maxCooldown - minCooldown);
			shootCount = -1;
			missileCount = -1;
			if (barrishee.isScreamingBeam()) {
				barrishee.setIsScreamingBeam(false);
			}
		}

		@Override
		public void updateTask() {
			EntityLivingBase target = barrishee.getAttackTarget();

			if(target != null) {
				int distance = MathHelper.floor(barrishee.getDistance(target));

				if (barrishee.getScreamTimer() >= 25) {
					if (!barrishee.isScreamingBeam()) {
						barrishee.setIsScreamingBeam(true);
					}

					float f = (float) MathHelper.atan2(target.posZ - barrishee.posZ, target.posX - barrishee.posX);
					missileCount++;

					if (missileCount % 2 == 0) {
						shootCount++;
						double d2 = 2D + 1D * (double) (shootCount);
						checkIfBeamHitsAnyone(barrishee.getEntityWorld(), new BlockPos(barrishee.posX + (double) MathHelper.cos(f) * d2, barrishee.posY + barrishee.getEyeHeight(), barrishee.posZ + (double) MathHelper.sin(f) * d2));
					}
				}

				if (shootCount >= distance || shootCount >= 12 || target.isDead) {
					resetTask();
				}
			}
		}

		public void checkIfBeamHitsAnyone(World world, BlockPos pos) {
			AxisAlignedBB hitBox = new AxisAlignedBB(pos).grow(0D, 0.25D, 0D);
			List<EntityLivingBase> list = world.getEntitiesWithinAABB(EntityLivingBase.class, hitBox);

			for (EntityLivingBase entity : list) {
				if(this.barrishee.attackEntityAsMob(entity)) {
					entity.knockBack(this.barrishee, 0.75F, MathHelper.sin(barrishee.rotationYaw * 3.141593F / 180.0F), -MathHelper.cos(barrishee.rotationYaw * 3.141593F / 180.0F));
				}
			}
		}
	}

	static class AISlamAttack extends EntityAIBase {
		EntityBarrishee barrishee;
		int cooldown, minCooldown, maxCooldown;
		int missileCount;
		int shootCount;

		public AISlamAttack(EntityBarrishee barrishee, int minCooldown, int maxCooldown) {
			this.barrishee = barrishee;
			this.minCooldown = minCooldown;
			this.maxCooldown = maxCooldown;
			this.cooldown = minCooldown + barrishee.rand.nextInt(maxCooldown - minCooldown);
			this.setMutexBits(MUTEX_SPECIAL_ATTACK | 1);
		}

		@Override
		public boolean shouldExecute() {
			EntityLivingBase target = barrishee.getAttackTarget();

			if(target != null) {
				double distance = barrishee.getDistanceSq(target);

				if (distance >= 9.0D && distance <= 81.0D && barrishee.onGround && barrishee.isReadyForSpecialAttack()) {
					return this.cooldown-- < 0;
				}
			}

			return false;
		}

		@Override
		public boolean shouldContinueExecuting() {
			EntityLivingBase target = barrishee.getAttackTarget();
			return target != null && shootCount != -1 && missileCount != -1 && barrishee.isLookingAtAttackTarget(target);
		}

		@Override
		public void startExecuting() {
			missileCount = 0;
			shootCount = 0;
			barrishee.setIsSlamming(true);
		}

		@Override
		public void resetTask() {
			cooldown = minCooldown + barrishee.rand.nextInt(maxCooldown - minCooldown);
			shootCount = -1;
			missileCount = -1;
			if (barrishee.isSlamming()) {
				barrishee.setIsSlamming(false);
			}
		}

		@Override
		public void updateTask() {
			EntityLivingBase target = barrishee.getAttackTarget();

			if(target != null) {
				int distance = MathHelper.floor(barrishee.getDistance(target));

				if (barrishee.isLookingAtAttackTarget(target)) {
					float f = (float) MathHelper.atan2(target.posZ - barrishee.posZ, target.posX - barrishee.posX);
					missileCount++;
					if (missileCount % 2 == 0) {
						shootCount++;

						if( shootCount==1) {
							barrishee.getEntityWorld().playSound(null, barrishee.getPosition(), SoundRegistry.WALL_SLAM, SoundCategory.HOSTILE, 1F, 1F);
						}

						double d2 = 2.5D + 1D * (double) (shootCount);

						BlockPos origin = new BlockPos(barrishee.posX + (double) MathHelper.cos(f) * d2,
								barrishee.posY - 1D, barrishee.posZ + (double) MathHelper.sin(f) * d2);
						IBlockState block = barrishee.getEntityWorld().getBlockState(origin);

						if (block.isNormalCube() && !block.getBlock().hasTileEntity(block)
								&& block.getBlockHardness(barrishee.getEntityWorld(), origin) <= 5.0F
								&& block.getBlockHardness(barrishee.getEntityWorld(), origin) >= 0.0F
								&& barrishee.getEntityWorld().getBlockState(origin).isOpaqueCube()) {

							EntityShockwaveBlock shockwaveBlock = new EntityShockwaveBlock(barrishee.getEntityWorld());
							shockwaveBlock.setOrigin(origin, 10, origin.getX() + 0.5D, origin.getZ() + 0.5D, barrishee);
							shockwaveBlock.setLocationAndAngles(origin.getX() + 0.5D, origin.getY(), origin.getZ() + 0.5D, 0.0F, 0.0F);
							shockwaveBlock.setBlock(Block.getBlockById(Block.getIdFromBlock(barrishee.getEntityWorld().getBlockState(origin).getBlock())), barrishee.getEntityWorld().getBlockState(origin).getBlock().getMetaFromState(barrishee.getEntityWorld().getBlockState(origin)));
							barrishee.getEntityWorld().spawnEntity(shockwaveBlock);
						}

					}
				}

				if (shootCount >= distance || shootCount >= 9 || target.isDead) {
					resetTask();
				}
			}
		}
	}

	static class AIBarrisheeAttack extends EntityAIAttackMelee {
		public AIBarrisheeAttack(EntityBarrishee barrishee) {
			super(barrishee, 1, true);
		}

		@Override
		protected double getAttackReachSqr(EntityLivingBase attackTarget) {
			return (double) (4.0F + attackTarget.width);
		}
	}
}