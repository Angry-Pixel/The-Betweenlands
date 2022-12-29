package thebetweenlands.common.entity.mobs;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMoveToBlock;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityJumpHelper;
import net.minecraft.entity.ai.EntityMoveHelper;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
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
	private PathNavigateFlyingBL pathNavigatorAir;
	private PathNavigateGround pathNavigatorGround;
	
	public final ControlledAnimation landingTimer = new ControlledAnimation(10); // base pose
	public final ControlledAnimation divingTimer = new ControlledAnimation(10); // diving pose
	public int MAX_RESTING_TIME = 120; //ticks
	public int MAX_FLIGHT_TIME = 120;
	
	public EntityPuffin(World world) {
		super(world);
		setSize(0.75F, 0.9F);
		stepHeight= 1F;
		moveHelperAir = new FlightMoveHelper(this);
		moveHelperLand = new PuffinMoveHelper(this);
		jumpHelper = new PuffinJumpHelper(this);
		pathNavigatorGround = new PathNavigateGround(this, world);
		pathNavigatorGround.setCanSwim(true);
		pathNavigatorAir = new PathNavigateFlyingBL(this, world, 0);
		setPathPriority(PathNodeType.WATER, -8F);
		setPathPriority(PathNodeType.BLOCKED, -8.0F);
		setPathPriority(PathNodeType.OPEN, 8.0F);
		setPathPriority(PathNodeType.FENCE, -8.0F);
		setPathPriority(PathNodeType.DANGER_CACTUS, -8.0F);
		updateMovementAndPathfinding();
	}

	@Override
	protected void initEntityAI() {
		tasks.addTask(0, new EntityAISwimming(this));
		tasks.addTask(1, new EntityPuffin.EntityAIPuffinLandRandomly(this, 0.5D));
		tasks.addTask(2, new EntityAIAttackMelee(this, 1.0D, true));
		tasks.addTask(3, new EntityPuffin.EntityAIFlyingWanderPuffin(this, 0.5D, 10));
		tasks.addTask(3, new EntityAIWander(this, 1D, 10));
		tasks.addTask(4, new EntityAILookIdle(this));
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
				}
			}	
		}
		
		if (getIsLanding() && getEntityWorld().isAirBlock(getPosition())) {
			motionY -= 0.04D;
		}

		if (isJumping && isInWater()) {
			getMoveHelper().setMoveTo(posX, posY + 1, posZ, 1.0D);
		}

//		if (getIsHovering()) {
	//		motionX = motionY = motionZ = 0.0D;
	//	}

		if (getEntityWorld().isRemote) {
			landingTimer.updateTimer();
			divingTimer.updateTimer();

			if (getIsDiving()) {
				divingTimer.increaseTimer();
				landingTimer.decreaseTimer();
			} else if (!getIsFlying()) {
				landingTimer.increaseTimer();
				divingTimer.decreaseTimer();
			} else {
				landingTimer.decreaseTimer();
				divingTimer.decreaseTimer();
			}
		}
	}
	
	protected void updateMovementAndPathfinding() {
		if (getIsFlying())
			moveHelper = moveHelperAir;
		else
			moveHelper = moveHelperLand;

		if (getIsFlying())
			navigator = pathNavigatorAir;
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
			this.motionX *= 0.800000011920929D;
			this.motionY *= 0.800000011920929D;
			this.motionZ *= 0.800000011920929D;
		} else if (this.isInLava()) {
			this.moveRelative(strafe, vertical, forward, 0.02F);
			this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);
			this.motionX *= 0.5D;
			this.motionY *= 0.5D;
			this.motionZ *= 0.5D;
		} else if (getIsFlying()) {
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
			return puffin.getCanLand() && super.shouldExecute();
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

}
