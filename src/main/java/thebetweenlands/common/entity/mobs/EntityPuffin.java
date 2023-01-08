package thebetweenlands.common.entity.mobs;

import javax.annotation.Nullable;

import com.google.common.base.Predicate;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMoveToBlock;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityJumpHelper;
import net.minecraft.entity.ai.EntityLookHelper;
import net.minecraft.entity.ai.EntityMoveHelper;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.pathfinding.PathNavigateSwimmer;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.api.entity.IEntityBL;
import thebetweenlands.client.render.model.ControlledAnimation;
import thebetweenlands.common.entity.ai.EntityAIAttackOnCollide;
import thebetweenlands.common.entity.ai.EntityAIFlyingWander;
import thebetweenlands.common.entity.movement.FlightMoveHelper;
import thebetweenlands.common.entity.movement.PathNavigateFlyingBL;
import thebetweenlands.common.registries.SoundRegistry;

public class EntityPuffin extends EntityCreature implements IEntityBL, net.minecraft.entity.passive.IAnimals {

	private static final DataParameter<Boolean> IS_HOVERING = EntityDataManager.createKey(EntityPuffin.class, DataSerializers.BOOLEAN);
	private static final DataParameter<Boolean> IS_DIVING = EntityDataManager.createKey(EntityPuffin.class, DataSerializers.BOOLEAN);

	private static final DataParameter<Boolean> IS_LANDING = EntityDataManager.createKey(EntityPuffin.class, DataSerializers.BOOLEAN);
	private static final DataParameter<Boolean> IS_FLYING = EntityDataManager.createKey(EntityPuffin.class, DataSerializers.BOOLEAN);
	
	private static final DataParameter<Boolean> SHOULD_LAND = EntityDataManager.createKey(EntityPuffin.class, DataSerializers.BOOLEAN);
	private static final DataParameter<Boolean> SHOULD_TAKE_OFF = EntityDataManager.createKey(EntityPuffin.class, DataSerializers.BOOLEAN);

	private static final DataParameter<Integer> FLYING_TIMER = EntityDataManager.createKey(EntityPuffin.class, DataSerializers.VARINT);
	private static final DataParameter<Integer> RESTING_TIMER = EntityDataManager.createKey(EntityPuffin.class, DataSerializers.VARINT);

	private static final DataParameter<Boolean> IS_FISHING = EntityDataManager.createKey(EntityPuffin.class, DataSerializers.BOOLEAN);

	public float animTime, prevAnimTime;
	public int flapTicks;
	public float flapSpeed = 1f;
    private int jumpTicks;
    private int jumpDuration;
    private int currentMoveTypeDuration;
    private boolean wasOnGround;

	private EntityMoveHelper moveHelperAir;
	private EntityMoveHelper moveHelperLand;
	private EntityMoveHelper moveHelperWater;
	private PathNavigateFlyingBL pathNavigatorAir;
	private PathNavigateGround pathNavigatorGround;
	private PathNavigateSwimmer pathNavigatorWater;
	
	public final ControlledAnimation landingTimer = new ControlledAnimation(10); // base pose
	public final ControlledAnimation divingTimer = new ControlledAnimation(10); // diving pose
	public int MAX_RESTING_TIME = 120; //ticks
	public int MAX_FLIGHT_TIME = 360;
	
	public EntityPuffin(World world) {
		super(world);
		setSize(0.75F, 0.9F);
		stepHeight= 1F;
		moveHelperAir = new FlightMoveHelper(this);
		moveHelperLand = new PuffinMoveHelper(this);
		moveHelperWater = new PuffinSwimMoveHelper(this);
		jumpHelper = new PuffinJumpHelper(this);
		pathNavigatorGround = new PathNavigateGround(this, world);
		pathNavigatorGround.setCanSwim(true);
		pathNavigatorAir = new PathNavigateFlyingBL(this, world, 0);
		pathNavigatorWater = new PathNavigateSwimmer(this, world);
		setPathPriority(PathNodeType.WATER, 100);
		setPathPriority(PathNodeType.BLOCKED, -8.0F);
		setPathPriority(PathNodeType.OPEN, 8.0F);
		setPathPriority(PathNodeType.FENCE, -8.0F);
		setPathPriority(PathNodeType.DANGER_CACTUS, -8.0F);
		updateMovementAndPathfinding();
	}

	@Override
	protected void initEntityAI() {
		//tasks.addTask(0, new EntityPuffin.EntityAIPuffinSwim(this));
		tasks.addTask(1, new EntityPuffin.EntityAIPuffinLandRandomly(this, 0.5D));
		tasks.addTask(0, new EntityPuffin.EntityAIPickUpFish(this, 1D, true));
		tasks.addTask(3, new EntityPuffin.EntityAIFlyingWanderPuffin(this, 0.5D, 10));
		tasks.addTask(3, new EntityPuffin.EntityAIPuffinWanderGround(this, 1D, 10));
		tasks.addTask(4, new EntityAILookIdle(this));
		targetTasks.addTask(1, new EntityPuffin.EntityAIGoFishing(this, EntityAnadia.class, true, 32D).setUnseenMemoryTicks(160));
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		dataManager.register(IS_HOVERING, false);
		dataManager.register(IS_DIVING, false);
		dataManager.register(IS_LANDING, false);
		dataManager.register(IS_FLYING, false);
		dataManager.register(SHOULD_LAND, false);
		dataManager.register(SHOULD_TAKE_OFF, true);
		dataManager.register(RESTING_TIMER, 0);
		dataManager.register(FLYING_TIMER, 0);
		dataManager.register(IS_FISHING, false);
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(10.0D);
		getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(20.0D);
		getAttributeMap().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(1.0D);
		getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.2D);
	}

	@Override
	public void onUpdate() {
		super.onUpdate();

		updateMovementAndPathfinding();

		if (!getEntityWorld().isRemote) {
			
			if(getIsFlying()) {
				if(world.getTotalWorldTime()%120 ==0)
					if (!getIsFishing())
						setIsFishing(true);
				isAirBorne = true;
				onGround = false;
				if(getFlyingTimer() < MAX_FLIGHT_TIME)
					setFlyingTimer(getFlyingTimer() + 1);
				if(getFlyingTimer() >= MAX_FLIGHT_TIME) {
					if(!getCanLand()){
						setCanLand(true);
						setRestingTimer(0);
						//System.out.println("SHOULD FLY ABOUT NOW!");
					}
				}
			}
			
			if(!getIsFlying()) {
				if (getIsFishing())
					setIsFishing(false);
				isAirBorne = false;
				onGround = true;
				if(getRestingTimer() < MAX_RESTING_TIME)
					setRestingTimer(getRestingTimer() + 1);
				if(getRestingTimer() >= MAX_RESTING_TIME) {
					if(getCanLand()) {
						setCanLand(false);
						setFlyingTimer(0);
						//System.out.println("SHOULD FUCKING LAND!");
					}
				
			}	}
		}
		
		if (getIsLanding() && getEntityWorld().isAirBlock(getPosition())) {
			motionY -= 0.04D;
		}

		//if (isJumping && isInWater() && !getIsDiving()) {
		//	getMoveHelper().setMoveTo(posX, posY + 1, posZ, 1.0D);
	//	}
		
		//if (getIsFishing() && getAttackTarget() != null) {
		//	if (motionY < 0)
		//		if (!getIsDiving())
		//			setIsDiving(true);
		//}

		if (motionY >= 0 && !isInWater())
			if (getIsDiving())
				setIsDiving(false);

	//	if (getIsHovering()) {
	//		motionX = motionY = motionZ = 0.0D;
	//	}

		if (getEntityWorld().isRemote) {
			landingTimer.updateTimer();
			divingTimer.updateTimer();

			if (getIsDiving()) {
				divingTimer.increaseTimer();
				landingTimer.decreaseTimer();
			} 
			else if (!getIsFlying()) {
				landingTimer.increaseTimer();
				divingTimer.decreaseTimer();
			} else if (!getIsDiving() && getIsFlying()) {
				landingTimer.decreaseTimer();
				divingTimer.decreaseTimer(5);
			}
		}
	}
	
	protected void updateMovementAndPathfinding() {
		if (getIsFlying() && !isInWater())
			moveHelper = moveHelperAir;
		else if(isInWater())
			moveHelper = moveHelperWater;
		else
			moveHelper = moveHelperLand;

		if (getIsFlying() && !isInWater())
			navigator = pathNavigatorAir;
		else if (isInWater())
			navigator = pathNavigatorWater;
		else
			navigator = pathNavigatorGround;
	}

	@Override
	public void onLivingUpdate() {
		super.onLivingUpdate();
		if (world.isRemote) {
			flapTicks++;

			if (!isSilent() && getIsFlying()) {
				float flapAngle1 = MathHelper.cos(flapTicks * flapSpeed);
				float flapAngle2 = MathHelper.cos((flapTicks + 1) * flapSpeed);
				if (flapAngle1 <= 0.3f && flapAngle2 > 0.3f) {
					world.playSound(posX, posY, posZ, getFlySound(), getSoundCategory(), getIsLanding() ? 0.25F : 0.5F, getIsLanding() ? 2.5F + rand.nextFloat() * 0.3F : 1.8F + rand.nextFloat() * 0.3F, false);
				}
			}
			
			if (jumpTicks != jumpDuration) {
				++jumpTicks;
			} else if (jumpDuration != 0) {
				jumpTicks = 0;
				jumpDuration = 0;
				setJumping(false);
			}
		}

	}

	@Override
	protected void updateAITasks() {
		super.updateAITasks();
		// stuff
		if (currentMoveTypeDuration > 0)
			--currentMoveTypeDuration;

		if (onGround) {
			PuffinJumpHelper puffinjumphelper = (PuffinJumpHelper) jumpHelper;
			if (!wasOnGround) {
				setJumping(false);
				if (this.moveHelper.getSpeed() < 2.2D)
					this.currentMoveTypeDuration = 10;
				else
					this.currentMoveTypeDuration = 1;
				puffinjumphelper.setCanJump(false);
			}

			if (!puffinjumphelper.getIsJumping()) {
				if (moveHelper.isUpdating() && currentMoveTypeDuration == 0) {
					Path path = navigator.getPath();
					Vec3d vec3d = new Vec3d(moveHelper.getX(), moveHelper.getY(), moveHelper.getZ());

					if (path != null && path.getCurrentPathIndex() < path.getCurrentPathLength())
						vec3d = path.getPosition(this);

					calculateRotationYaw(vec3d.x, vec3d.z);
					startJumping();
				}
			} else if (!puffinjumphelper.canJump())
				puffinjumphelper.setCanJump(true);
		}
		wasOnGround = onGround;
	}

    public void setMovementSpeed(double newSpeed) {
        getNavigator().setSpeed(newSpeed);
        moveHelper.setMoveTo(moveHelper.getX(), moveHelper.getY(), moveHelper.getZ(), newSpeed);
    }

    public void startJumping(){
        setJumping(true);
        jumpDuration = 10;
        jumpTicks = 0;
    }

	@Override
    protected float getJumpUpwardsMotion() {
        return 0.2F;
    }
	
	@Override
	public void travel(float strafe, float vertical, float forward) {
		if (this.isInWater()) {
			this.moveRelative(strafe, vertical, forward, 0.02F);
			this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);
			this.motionX *= 0.9D;
			this.motionY *= 0.9D;
			this.motionZ *= 0.9D;
		} else if (this.isInLava()) {
			this.moveRelative(strafe, vertical, forward, 0.02F);
			this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);
			this.motionX *= 0.5D;
			this.motionY *= 0.5D;
			this.motionZ *= 0.5D;
		} else if (getIsFlying() && !isInWater()) {
			float f = 0.91F;

			if (this.onGround) {
				BlockPos underPos = new BlockPos(MathHelper.floor(this.posX), MathHelper.floor(this.getEntityBoundingBox().minY) - 1, MathHelper.floor(this.posZ));
				IBlockState underState = this.world.getBlockState(underPos);
				f = underState.getBlock().getSlipperiness(underState, this.world, underPos, this) * 0.91F;
			}

			float f1 = 0.16277136F / (f * f * f);
			this.moveRelative(strafe, vertical, forward, this.onGround ? 0.1F * f1 : 0.02F);
			f = 0.91F;

			if (this.onGround) {
				BlockPos underPos = new BlockPos(MathHelper.floor(this.posX), MathHelper.floor(this.getEntityBoundingBox().minY) - 1, MathHelper.floor(this.posZ));
				IBlockState underState = this.world.getBlockState(underPos);
				f = underState.getBlock().getSlipperiness(underState, this.world, underPos, this) * 0.91F;
			}

			this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);
			this.motionX *= (double) f;
			this.motionY *= (double) f;
			this.motionZ *= (double) f;
		}
		else
			super.travel(strafe, vertical, forward);

		this.prevLimbSwingAmount = this.limbSwingAmount;
		double d1 = this.posX - this.prevPosX;
		double d0 = this.posZ - this.prevPosZ;
		float f2 = MathHelper.sqrt(d1 * d1 + d0 * d0) * 4.0F;

		if (f2 > 1.0F) {
			f2 = 1.0F;
		}

		this.limbSwingAmount += (f2 - this.limbSwingAmount) * 0.4F;
		this.limbSwing += this.limbSwingAmount;
	}

	private void calculateRotationYaw(double x, double z) {
		rotationYaw = (float) (MathHelper.atan2(z - posZ, x - posX) * (180D / Math.PI)) - 90.0F;
	}
	
    @SideOnly(Side.CLIENT)
    public float getJumpCompletion(float partialTicks) {
        return this.jumpDuration == 0 ? 0.0F : ((float)this.jumpTicks + partialTicks) / (float)this.jumpDuration;
    }
	
	@Override
	public void notifyDataManagerChange(DataParameter<?> key) {
		super.notifyDataManagerChange(key);
	}

	@Override
	public boolean isInWater() {
		return this.inWater = getEntityWorld().handleMaterialAcceleration(getEntityBoundingBox(), Material.WATER, this);
	}

	@Override
	public boolean isPushedByWater() {
		return false;
	}

	public boolean getShouldTakeOff() {
		return dataManager.get(SHOULD_TAKE_OFF);
	}

	private void setShouldTakeOff(boolean take_off) {
		dataManager.set(SHOULD_TAKE_OFF, take_off);
	}
	
	public boolean getCanLand() {
		return dataManager.get(SHOULD_LAND);
	}

	private void setCanLand(boolean land) {
		dataManager.set(SHOULD_LAND, land);
	}

	public boolean getIsFlying() {
		return dataManager.get(IS_FLYING);
	}

	public void setIsFlying(boolean flying) {
		dataManager.set(IS_FLYING, flying);
	}
	
	public int getFlyingTimer() {
		return dataManager.get(FLYING_TIMER);
	}

	public void setFlyingTimer(int timer) {
		dataManager.set(FLYING_TIMER, timer);
	}

	public boolean getIsLanding() {
		return dataManager.get(IS_LANDING);
	}

	public void setIsLanding(boolean landing) {
		dataManager.set(IS_LANDING, landing);
	}

	public int getRestingTimer() {
		return dataManager.get(RESTING_TIMER);
	}

	public void setRestingTimer(int timer) {
		dataManager.set(RESTING_TIMER, timer);
	}

	public boolean getIsFishing() {
		return dataManager.get(IS_FISHING);
	}

	public void setIsFishing(boolean fishing) {
		dataManager.set(IS_FISHING, fishing);
	}

	public boolean getIsHovering() {
		return dataManager.get(IS_HOVERING);
	}

	public void setIsHovering(boolean hovering) {
		dataManager.set(IS_HOVERING, hovering);
	}

	public boolean getIsDiving() {
		return dataManager.get(IS_DIVING);
	}

	public void setIsDiving(boolean diving) {
		dataManager.set(IS_DIVING, diving);
	}

	@Override
	protected ResourceLocation getLootTable() {
		return null; // LootTableRegistry.PUFFIN;
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return null; // SoundRegistry.PUFFIN_LIVING;
	}

	protected SoundEvent getFlySound() {
		return SoundRegistry.CHIROMAW_MATRIARCH_FLAP;
	}

	@Nullable
	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return null; // SoundRegistry.PUFFIN_HURT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return null; // SoundRegistry.PUFFIN_DEATH;
	}

	@Override
    public void fall(float distance, float damageMultiplier) {}

	@Override
	public boolean attackEntityAsMob(Entity entityIn) {
		return EntityAIAttackOnCollide.useStandardAttack(this, entityIn);
	}

	@Override
	public int getMaxSpawnedInChunk() {
		return 3;
	}

	@Override
	public float getBlockPathWeight(BlockPos pos) {
		return 0.5F;
	}
	
	class EntityAIPuffinWanderGround extends EntityAIWander {
		private final EntityPuffin puffin;

		public EntityAIPuffinWanderGround(EntityPuffin puffinIn, double speedIn, int chance) {
			super(puffinIn, speedIn, chance);
			puffin = puffinIn;
		}

		@Override
		public boolean shouldExecute() {
			return super.shouldExecute() && !puffin.getIsFlying();
		}
	}
	
	class EntityAIPuffinSwim extends EntityAISwimming {
		private final EntityPuffin puffin;

		public EntityAIPuffinSwim(EntityPuffin puffinIn) {
			super(puffinIn);
			puffin = puffinIn;
			setMutexBits(4);
		}

		@Override
		public boolean shouldExecute() {
			return super.shouldExecute() && !puffin.getIsDiving();
		}
	}
	

	class EntityAIFlyingWanderPuffin extends EntityAIFlyingWander {
		private final EntityPuffin puffin;

		public EntityAIFlyingWanderPuffin(EntityPuffin puffinIn, double speedIn, int chance) {
			super(puffinIn, speedIn, chance);
			puffin = puffinIn;
			setMutexBits(3);
		}

		@Override
		public boolean shouldExecute() {
			return super.shouldExecute();
		}

		@Override
		public boolean shouldContinueExecuting() {
			return !puffin.getCanLand() && super.shouldContinueExecuting();
		}

		@Override
		public void startExecuting() {
			super.startExecuting();
		}

		@Override
		public void updateTask() {
			super.updateTask();
			if (!puffin.getIsFlying())
				puffin.setIsFlying(true);
			if (puffin.getIsLanding())
				puffin.setIsLanding(false);
		}
	}

	class EntityAIPuffinLandRandomly extends EntityAIMoveToBlock {
		private final EntityPuffin puffin;

		public EntityAIPuffinLandRandomly(EntityPuffin puffinIn, double speed) {
			super(puffinIn, speed, 16);
			puffin = puffinIn;
			setMutexBits(3);
		}

		@Override
		public boolean shouldExecute() {
			return puffin.getCanLand() && super.shouldExecute() && !puffin.getIsFishing();
		}

		@Override
		public void startExecuting() {
			super.startExecuting();
		}

		@Override
		public boolean shouldContinueExecuting() {
			return puffin.getCanLand() && super.shouldContinueExecuting();
		}

		@Override
		public void resetTask() {
			super.resetTask();
		}

		@Override
		public void updateTask() {
			super.updateTask();
			if (getIsAboveDestination()) {
				if (puffin.getIsFlying())
					puffin.setIsFlying(false);
				if (!puffin.getIsLanding()) {
					puffin.setIsLanding(true);
				}
			}
		}

		@Override
		protected boolean shouldMoveTo(World world, BlockPos pos) {
			if (!world.isAirBlock(pos.up())) {
				return false;
			} else {
				IBlockState state = world.getBlockState(pos);
				Block block = state.getBlock();
				if (state.isSideSolid(world, pos, EnumFacing.UP)) {
					return true;
				}
				return false;
			}
		}
	}
	
	public class EntityAIGoFishing<T extends EntityLivingBase> extends EntityAINearestAttackableTarget<T> {

		protected double baseRange;
		protected double revengeRange;
		private final EntityPuffin puffin;

		public EntityAIGoFishing(EntityPuffin puffinIn, Class<T> classTarget, boolean checkSight, double rangeIn) {
			super(puffinIn, classTarget, checkSight);
			baseRange = revengeRange = rangeIn;
			puffin = puffinIn;
			setMutexBits(3);
		}

		public EntityAIGoFishing(EntityPuffin puffinIn, Class<T> classTarget, int chance, boolean checkSight, boolean onlyNearby, @Nullable final Predicate <? super T > targetSelector) {
			super(puffinIn, classTarget, chance, checkSight, onlyNearby, targetSelector);
			puffin = puffinIn;
		}

		@Override
		public boolean shouldExecute() {
			if (super.shouldExecute())
				return puffin.getIsFishing() && puffin.getIsFlying() && !puffin.getIsDiving();
			return false;
		}

		@Override
		protected AxisAlignedBB getTargetableArea(double targetDistance) {
			return puffin.getEntityBoundingBox().grow(targetDistance, targetDistance, targetDistance);
		}

		@Override
		protected double getTargetDistance() {
			if(getRevengeTarget() != null) {
				setAttackTarget(getRevengeTarget());
				revengeRange = getDistance(getRevengeTarget());
			}
			return Math.max(baseRange, revengeRange);
		}
	}
	
	class EntityAIPickUpFish extends EntityAIBase {
		World world;
		protected int attackTick;
		double speedTowardsTarget;
		boolean longMemory;
		Path path;
		private int delayCounter;
		private double targetX;
		private double targetY;
		private double targetZ;
		protected final int attackInterval = 20;
		private int failedPathFindingPenalty = 0;
		private boolean canPenalize = false;
		private final EntityPuffin puffin;

		public EntityAIPickUpFish(EntityPuffin puffinIn, double speedIn, boolean useLongMemory) {
			puffin = puffinIn;
			world = puffin.world;
			speedTowardsTarget = speedIn;
			longMemory = useLongMemory;
			setMutexBits(3);
		}

		@Override
		public boolean shouldExecute() {
			EntityLivingBase entitylivingbase = puffin.getAttackTarget();
			if (entitylivingbase == null)
				return false;
			else if (!entitylivingbase.isEntityAlive())
				return false;
			else if (!(entitylivingbase instanceof EntityAnadia))
				return false;
			else if (!puffin.getIsFlying())
				return false;
			else {
				path = puffin.getNavigator().getPathToXYZ(entitylivingbase.posX, entitylivingbase.posY + 6D, entitylivingbase.posZ);
				if (path != null)
					return true;
				else
					return getAttackReachSqr(entitylivingbase) >= puffin.getDistanceSq(entitylivingbase.posX, entitylivingbase.getEntityBoundingBox().minY + 6D, entitylivingbase.posZ);
			}
		}

		@Override
		public boolean shouldContinueExecuting() {
			EntityLivingBase entitylivingbase = puffin.getAttackTarget();
			if (entitylivingbase == null)
				return false;
			if (!puffin.getIsFishing())
				return false;
			if (!entitylivingbase.isEntityAlive())
				return false;
			if (!longMemory)
				return !puffin.getNavigator().noPath();
			return !(entitylivingbase instanceof EntityAnadia);
		}

		@Override
		public void startExecuting() {
			puffin.getNavigator().setPath(path, speedTowardsTarget);
			delayCounter = 0;
		}

		@Override
		public void resetTask() {
			puffin.setAttackTarget((EntityLivingBase)null);
			puffin.getNavigator().clearPath();
		}

		@Override
		public void updateTask() {
			EntityLivingBase entitylivingbase = puffin.getAttackTarget();
			if(entitylivingbase == null) {
				return;
			}
			puffin.getLookHelper().setLookPositionWithEntity(entitylivingbase, 30.0F, 30.0F);
			double distToEnemySqr = puffin.getDistanceSq(entitylivingbase.posX, entitylivingbase.getEntityBoundingBox().minY, entitylivingbase.posZ);
			--delayCounter;
				if ((longMemory || puffin.getEntitySenses().canSee(entitylivingbase)) && delayCounter <= 0 && (targetX == 0.0D && targetY == 0.0D && targetZ == 0.0D || entitylivingbase.getDistanceSq(targetX, targetY, targetZ) >= 1.0D || puffin.getRNG().nextFloat() < 0.05F)) {
					targetX = entitylivingbase.posX;
					targetY = entitylivingbase.getEntityBoundingBox().minY + 6D;
					targetZ = entitylivingbase.posZ;
					puffin.getNavigator().tryMoveToXYZ(targetX, targetY, targetZ, speedTowardsTarget);
					delayCounter = 4 + puffin.getRNG().nextInt(7);
				if (distToEnemySqr > 1024.0D)
						delayCounter += 10;
					else if (distToEnemySqr > 256.0D)
						delayCounter += 5;
					if (!puffin.getNavigator().tryMoveToXYZ(targetX, targetY, targetZ, speedTowardsTarget))
						delayCounter += 15;
				}
					
				if(!puffin.isInWater() && Math.floor(puffin.posX) == Math.floor(targetX) && Math.floor(puffin.posY) >= Math.floor(targetY) && Math.floor(puffin.posZ) == Math.floor(targetZ)) {
					if(!puffin.getNavigator().tryMoveToEntityLiving(entitylivingbase, speedTowardsTarget * 2D)) {
						puffin.setIsDiving(false);
						puffin.setIsFishing(false);
					}
					else
						puffin.setIsDiving(true);
				}
				else if(puffin.isInWater()) {
					if(puffin.getNavigator().tryMoveToEntityLiving(entitylivingbase, speedTowardsTarget *0.5D)) {
						puffin.setIsDiving(false);
						// need to add proper swimming here - and to remove all the other rubbish
					}
				}
				
				double attackReachSq = getAttackReachSqr(entitylivingbase);
				
				if (distToEnemySqr <= attackReachSq && attackTick <= 0)  {
					attackTick = 20;
					if(entitylivingbase instanceof EntityAnadia) {
						puffin.setIsDiving(false);
						entitylivingbase.setDead();
						puffin.setIsFishing(false);
						System.out.println("Caught a fish");
					}
					resetTask();
				}
				
			attackTick = Math.max(attackTick - 1, 0);
		}

		protected double getAttackReachSqr(EntityLivingBase attackTarget)  {
			return (double)(puffin.width * 2.0F * puffin.width * 2.0F + attackTarget.width);
		}
	}
	
	
	public class PuffinJumpHelper extends EntityJumpHelper {
		private final EntityPuffin puffin;
		private boolean canJump;

		public PuffinJumpHelper(EntityPuffin puffin) {
			super(puffin);
			this.puffin = puffin;
		}

		public boolean getIsJumping() {
			return isJumping;
		}

		public boolean canJump() {
			return canJump;
		}

		public void setCanJump(boolean canJumpIn) {
			canJump = canJumpIn;
		}

		public void doJump() {
			if (isJumping) {
				puffin.startJumping();
				isJumping = false;
			}
		}
    }

	static class PuffinMoveHelper extends EntityMoveHelper {
		private final EntityPuffin puffin;
		private double nextJumpSpeed;

		public PuffinMoveHelper(EntityPuffin puffin) {
			super(puffin);
			this.puffin = puffin;
		}

		public void onUpdateMoveHelper() {
			if (puffin.onGround && !puffin.isJumping && !((PuffinJumpHelper) puffin.jumpHelper).getIsJumping()) {
				puffin.setMovementSpeed(0.0D);
			}
			
			else if (isUpdating()) 
				puffin.setMovementSpeed(nextJumpSpeed);
			super.onUpdateMoveHelper();
		}

		public void setMoveTo(double x, double y, double z, double speedIn) {
			if (puffin.isInWater())
				speedIn = 1.5D;
			super.setMoveTo(x, y, z, speedIn);
			if (speedIn > 0.0D)
				nextJumpSpeed = speedIn;
		}
	}
	
	class PuffinSwimMoveHelper extends EntityMoveHelper {
		private final EntityPuffin puffin;

		public PuffinSwimMoveHelper(EntityPuffin puffinIn) {
			super(puffinIn);
			this.puffin = puffinIn;
		}

		@Override
		public void onUpdateMoveHelper() {
			if (action == EntityMoveHelper.Action.MOVE_TO && !puffin.getNavigator().noPath()) {
				double d0 = posX - puffin.posX;
				double d1 = posY - puffin.posY;
				double d2 = posZ - puffin.posZ;
				double d3 = d0 * d0 + d1 * d1 + d2 * d2;
				d3 = (double) MathHelper.sqrt(d3);
				d1 = d1 / d3;
				float f = (float) (MathHelper.atan2(d2, d0) * (180D / Math.PI)) - 90.0F;
				puffin.rotationYaw = limitAngle(puffin.rotationYaw, f, 90.0F);
				puffin.renderYawOffset = puffin.rotationYaw;
				float f1 = (float) (speed * puffin.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getAttributeValue());
				puffin.setAIMoveSpeed(puffin.getAIMoveSpeed() + (f1 - puffin.getAIMoveSpeed()) * 0.125F);
				double d4 = Math.sin((double) (puffin.ticksExisted + puffin.getEntityId()) * 0.5D) * 0.05D;
				double d5 = Math.cos((double) (puffin.rotationYaw * 0.017453292F));
				double d6 = Math.sin((double) (puffin.rotationYaw * 0.017453292F));
				puffin.motionX += d4 * d5;
				puffin.motionZ += d4 * d6;
				d4 = Math.sin((double) (puffin.ticksExisted + puffin.getEntityId()) * 0.75D) * 0.05D;
				puffin.motionY += d4 * (d6 + d5) * 0.25D;
				if (Math.abs(puffin.motionY) < 0.35) {
					puffin.motionY += (double) puffin.getAIMoveSpeed() * d1 * 0.1D * (2 + (d1 > 0 ? 0.4 : 0) + (puffin.collidedHorizontally ? 20 : 0));
				}
				EntityLookHelper entitylookhelper = puffin.getLookHelper();
				double d7 = puffin.posX + d0 / d3 * 2.0D;
				double d8 = (double) puffin.getEyeHeight() + puffin.posY + d1 / d3;
				double d9 = puffin.posZ + d2 / d3 * 2.0D;
				double d10 = entitylookhelper.getLookPosX();
				double d11 = entitylookhelper.getLookPosY();
				double d12 = entitylookhelper.getLookPosZ();

				if (!entitylookhelper.getIsLooking()) {
					d10 = d7;
					d11 = d8;
					d12 = d9;
				}

				puffin.getLookHelper().setLookPosition(d10 + (d7 - d10) * 0.125D, d11 + (d8 - d11) * 0.125D, d12 + (d9 - d12) * 0.125D, 10.0F, 40.0F);
			} else {
				puffin.setAIMoveSpeed(0.0F);
			}
		}
	}

}
