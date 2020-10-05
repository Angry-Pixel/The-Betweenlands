package thebetweenlands.common.entity.mobs;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIMoveToBlock;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import thebetweenlands.api.entity.IEntityBL;
import thebetweenlands.client.render.model.ControlledAnimation;
import thebetweenlands.common.entity.ai.EntityAIAttackOnCollide;
import thebetweenlands.common.entity.ai.EntityAIFlyingWander;
import thebetweenlands.common.entity.ai.PathNavigateFlyingBL;
import thebetweenlands.common.entity.movement.FlightMoveHelper;
import thebetweenlands.common.registries.SoundRegistry;

public class EntityPuffin extends EntityFlyingCreature implements IEntityBL {

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

	public final ControlledAnimation landingTimer = new ControlledAnimation(10); // base pose
	public final ControlledAnimation divingTimer = new ControlledAnimation(10); // diving pose
	public int MAX_RESTING_TIME = 120; //ticks
	public int MAX_FLIGHT_TIME = 120;
	
	public EntityPuffin(World world) {
		super(world);
		setSize(0.5F, 0.9F);
		setIsHovering(false);

		moveHelper = new FlightMoveHelper(this);
		//setPathPriority(PathNodeType.WATER, -8F);
		//setPathPriority(PathNodeType.BLOCKED, -8.0F);
		setPathPriority(PathNodeType.OPEN, 8.0F);
		//setPathPriority(PathNodeType.FENCE, -8.0F);
	}

	@Override
	protected void initEntityAI() {
		tasks.addTask(0, new EntityAISwimming(this));
		tasks.addTask(1, new EntityPuffin.EntityAIPuffinLandRandomly(this, 1.5D));
		tasks.addTask(3, new EntityAIAttackMelee(this, 1.0D, true));
		tasks.addTask(2, new EntityPuffin.EntityAIFlyingWanderPuffin(this, 1D, 3));
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
		getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.095D);
	}
	
	@Override
	protected PathNavigate createNavigator(World world) {
		return new PathNavigateFlyingBL(this, world, 1);
	}

	@Override
	public void onUpdate() {
		super.onUpdate();

		if (!getEntityWorld().isRemote) {
			
			if(getIsFlying()) {
				if(getFlyingTimer() < MAX_FLIGHT_TIME)
					setFlyingTimer(getFlyingTimer() + 1);
				if(getFlyingTimer() >= MAX_FLIGHT_TIME) {
					if(!getShouldLand()){
						setShouldLand(true);
						setRestingTimer(0);
						//System.out.println("SHOULD FLY ABOUT NOW!");
					}
				}
			}
			
			if(!getIsFlying()) {
				if(getRestingTimer() < MAX_RESTING_TIME)
					setRestingTimer(getRestingTimer() + 1);
				if(getRestingTimer() >= MAX_RESTING_TIME) {
					if(getShouldLand()) {
						setShouldLand(false);
						setFlyingTimer(0);
						//System.out.println("SHOULD FUCKING LAND!");
					}
				}
			}
			
			/*if (!getEntityWorld().getBlockState(getPosition().down()).isSideSolid(getEntityWorld(), getPosition().down(), EnumFacing.UP)) {
				if (!getIsFlying())
					setIsFlying(true);
				if (getIsLanding())
					setIsLanding(false);

			} else {
				if (getIsFlying())
					setIsFlying(false);
			}
			
			
			if(!getIsFlying()) {
				if(getRestingTimer() < MAX_RESTING_TIME) {
					setRestingTimer(getRestingTimer() + 1);
					System.out.println("Resting Time: " + getRestingTimer());
					}
			}
		*/	
		}
		
		if (getIsLanding() && !onGround) {
			motionY -= 0.05D;
		}

	//	if (isJumping && isInWater()) {
			// Moving out of water
	//		getMoveHelper().setMoveTo(posX, posY + 1, posZ, 1.0D);
	//	}

		if (getIsHovering()) {
			motionX = motionY = motionZ = 0.0D;
		}

		if (motionY < 0.0D && getAttackTarget() == null) {
			motionY *= 1.1D;
		}

		// if
		// (getEntityWorld().getBlockState(getPosition().down()).isSideSolid(getEntityWorld(),
		// getPosition().down(), EnumFacing.UP)) {
		// if (getIsFlying())
		// setIsFlying(false);
		// getMoveHelper().setMoveTo(posX, posY + 1, posZ, 1.0D);
		// }

		if (getEntityWorld().isRemote) {
			landingTimer.updateTimer();
			divingTimer.updateTimer();

			if (this.getIsDiving()) {
				divingTimer.increaseTimer();
				landingTimer.decreaseTimer();
			} else if (!this.getIsFlying()) {
				landingTimer.increaseTimer();
				divingTimer.decreaseTimer();
			} else {
				landingTimer.decreaseTimer();
				divingTimer.decreaseTimer();
			}
		}
	}

	@Override
	public void onLivingUpdate() {
		super.onLivingUpdate();
		if (world.isRemote) {
			this.flapTicks++;

			if (!isSilent() && getIsFlying()) {
				float flapAngle1 = MathHelper.cos(this.flapTicks * this.flapSpeed);
				float flapAngle2 = MathHelper.cos((this.flapTicks + 1) * this.flapSpeed);
				if (flapAngle1 <= 0.3f && flapAngle2 > 0.3f) {
					world.playSound(posX, posY, posZ, getFlySound(), getSoundCategory(), getIsLanding() ? 0.25F : 0.5F, getIsLanding() ? 2.5F + rand.nextFloat() * 0.3F : 1.8F + rand.nextFloat() * 0.3F, false);
				}
			}

		}
	}

	@Override
	protected void updateAITasks() {
		super.updateAITasks();
		// stuff
	}

	public boolean getShouldTakeOff() {
		return dataManager.get(SHOULD_TAKE_OFF);
	}

	private void setShouldTakeOff(boolean take_off) {
		dataManager.set(SHOULD_TAKE_OFF, take_off);
	}
	
	public boolean getShouldLand() {
		return dataManager.get(SHOULD_LAND);
	}

	private void setShouldLand(boolean land) {
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
			this.puffin = puffinIn;
		}

		@Override
		public boolean shouldExecute() {
			return super.shouldExecute();
		}

		@Override
		public boolean shouldContinueExecuting() {
			return !puffin.getShouldLand() && super.shouldContinueExecuting();
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
			this.puffin = puffinIn;
		}

		@Override
		public boolean shouldExecute() {
			return puffin.getShouldLand() && super.shouldExecute();
		}

		@Override
		public void startExecuting() {
			super.startExecuting();
		}

		@Override
		public boolean shouldContinueExecuting() {
			return puffin.getShouldLand() && super.shouldContinueExecuting();
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
				if (!puffin.getIsLanding())
					puffin.setIsLanding(true);
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
}
