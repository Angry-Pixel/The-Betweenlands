package thebetweenlands.common.entity.mobs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;

import com.google.common.base.Predicate;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import thebetweenlands.api.entity.IEntityBL;
import thebetweenlands.api.storage.ILocalStorage;
import thebetweenlands.client.render.model.ControlledAnimation;
import thebetweenlands.common.entity.ai.EntityAIAttackOnCollide;
import thebetweenlands.common.entity.movement.FlightMoveHelper;
import thebetweenlands.common.entity.projectiles.EntityBLArrow;
import thebetweenlands.common.entity.projectiles.EntityChiromawDroppings;
import thebetweenlands.common.item.tools.bow.EnumArrowType;
import thebetweenlands.common.registries.LootTableRegistry;
import thebetweenlands.common.registries.SoundRegistry;
import thebetweenlands.common.world.storage.BetweenlandsWorldStorage;
import thebetweenlands.common.world.storage.location.EnumLocationType;
import thebetweenlands.common.world.storage.location.LocationStorage;

public class EntityChiromawMatriarch extends EntityFlyingMob implements IEntityBL {
	private static final DataParameter<Boolean> IS_NESTING = EntityDataManager.createKey(EntityChiromawMatriarch.class, DataSerializers.BOOLEAN);
	private static final DataParameter<Boolean> RETURN_TO_NEST = EntityDataManager.createKey(EntityChiromawMatriarch.class, DataSerializers.BOOLEAN);
	private static final DataParameter<Boolean> IS_LANDING = EntityDataManager.createKey(EntityChiromawMatriarch.class, DataSerializers.BOOLEAN);
	private static final DataParameter<Boolean> IS_SPINNING = EntityDataManager.createKey(EntityChiromawMatriarch.class, DataSerializers.BOOLEAN);
	public int broodCount;
	public double pickupHeight;
	public int droppingTimer; // makes sure player is always dropped
	@Nullable
	private BlockPos boundOrigin;
	public float previousSpinAngle, spinAngle;
	public float animTime, prevAnimTime;
	public float flapSpeed = 0.5f;
	public int flapTicks;
	public int landingAbortTime;
	
	public final ControlledAnimation landingTimer = new ControlledAnimation(10);
	public final ControlledAnimation nestingTimer = new ControlledAnimation(20);
	public final ControlledAnimation spinningTimer = new ControlledAnimation(10);

	public EntityChiromawMatriarch(World world) {
		super(world);
		setSize(1.75F, 2F);
		setIsNesting(false);

		moveHelper = new FlightMoveHelper(this);
		setPathPriority(PathNodeType.WATER, -8F);
		setPathPriority(PathNodeType.BLOCKED, -8.0F);
		setPathPriority(PathNodeType.OPEN, 8.0F);
		setPathPriority(PathNodeType.FENCE, -8.0F);
	}
	
	@Override
	protected void entityInit() {
		super.entityInit();
		dataManager.register(IS_NESTING, false);
		dataManager.register(RETURN_TO_NEST, false);
		dataManager.register(IS_LANDING, false);
		dataManager.register(IS_SPINNING, false);
	}

	@Override
	protected void initEntityAI() {
		tasks.addTask(0, new EntityAISwimming(this));
		tasks.addTask(1, new EntityChiromawMatriarch.AIPickUpAndDropAttack(this, 2.0D, true));
		tasks.addTask(2, new EntityChiromawMatriarch.AIReturnToNest(this, 1.25D));
		tasks.addTask(3, new EntityChiromawMatriarch.AIMoveRandom(this));
		tasks.addTask(4, new EntityChiromawMatriarch.AIChangeNest(this));
		tasks.addTask(5, new EntityChiromawMatriarch.AIPoop(this));
		targetTasks.addTask(1, new AIFindNearestTarget<EntityPlayer>(this, EntityPlayer.class, true, 16D).setUnseenMemoryTicks(160));
		targetTasks.addTask(1, new AIFindNearestTarget<EntityVillager>(this, EntityVillager.class, true, 16D).setUnseenMemoryTicks(160));
	}
	
	@Override
	public void onUpdate() {
		super.onUpdate();

		if (!getEntityWorld().isRemote && getAttackTarget() == null) {
			if (getEntityWorld().getDifficulty() == EnumDifficulty.PEACEFUL)
				setDead();
			if (getBroodCount() <= 0) {
				if (getReturnToNest())
					setReturnToNest(false);
				else if (!getReturnToNest())
					setReturnToNest(true);
				setBroodCount(240);
			}

			if (getBroodCount() > 0)
				setBroodCount(getBroodCount() - 1);
		}
		
		if (!getEntityWorld().isRemote && getAttackTarget() != null) {
			if (getIsNesting())
				setIsNesting(false);
			if (getReturnToNest())
				setReturnToNest(false);
			if(getBroodCount() < 240)
				setBroodCount(240);
		}

		if (isJumping && isInWater()) {
			//Moving out of water
			getMoveHelper().setMoveTo(posX, posY + 1, posZ, 1.0D);
		}

		if (getIsNesting()) {
			motionX = motionY = motionZ = 0.0D;
		}
		
		if (!getEntityWorld().isRemote && getIsLanding()) {
			if (landingAbortTime > 0 )
				landingAbortTime--;
			if (landingAbortTime <= 0) {
				if (getIsLanding())
					setIsLanding(false);
				if (getReturnToNest())
					setReturnToNest(false);
				if (getBroodCount() < 240)
					setBroodCount(240);
			}
		}
		if (getBroodCount() > 0 && getAttackTarget() == null && getReturnToNest() && !getIsNesting()) {
			if(getNestBox() != null && getEntityBoundingBox().intersects(getNestBox())) {
				double d0 = getBoundOrigin().getX() + 0.5D - posX;
				double d1 = getBoundOrigin().getY() - posY;
				double d2 = getBoundOrigin().getZ() + 0.5D - posZ;
				motionX += (Math.signum(d0) - motionX) * 0.0000000003D;
				motionY += (Math.signum(d1) - motionY) * 0.03125D;
				motionZ += (Math.signum(d2) - motionZ) * 0.0000000003D;

				if (getEntityBoundingBox().minY > getNestBox().minY + 0.0625D) {
					if (!getEntityWorld().isRemote) {
						if (!getIsLanding()) {
							setIsLanding(true);
							landingAbortTime = 20;
						}
					}
				}
				if (getEntityBoundingBox().minY <= getNestBox().minY + 0.0625D) {
					if (!getEntityWorld().isRemote) {
						if(getIsLanding()) {
							setIsLanding(false);
							getEntityWorld().playSound(null, getPosition(), SoundRegistry.CHIROMAW_MATRIARCH_LAND, SoundCategory.HOSTILE, 0.5F, 1F + (getEntityWorld().rand.nextFloat() - getEntityWorld().rand.nextFloat()) * 0.8F);
						}
						if(!getIsNesting()) 
							setIsNesting(true);
						setPosition(getBoundOrigin().getX() + 0.5D, getBoundOrigin().getY(), getBoundOrigin().getZ() + 0.5D);
					}
				}
			}
		}

		if(getEntityWorld().getBlockState(getPosition().down()).isSideSolid(getEntityWorld(), getPosition().down(), EnumFacing.UP)) {
			if(!getIsLanding() && !getIsNesting())
				getMoveHelper().setMoveTo(posX, posY + 2, posZ, 1.0D);
		}
		
		if (getIsSpinning()) {
			if (getEntityWorld().isRemote) {
				previousSpinAngle = spinAngle;
				if (spinAngle <= 330F)
					spinAngle += 30F;
				if (spinAngle >= 360F)
					spinAngle = 0F;
			}

			//motionX *= 000001F;
			motionY += 0.05F;
			//motionZ *= 000001F;
		}
		
		if(this.world.isRemote) {
			nestingTimer.updateTimer();
			landingTimer.updateTimer();
			spinningTimer.updateTimer();

			if(this.getIsNesting()) {
				nestingTimer.increaseTimer();
				landingTimer.decreaseTimer();
				spinningTimer.decreaseTimer();
			} else if(this.getIsLanding()) {
				landingTimer.increaseTimer();
				nestingTimer.decreaseTimer();
				spinningTimer.decreaseTimer();
			} else if(this.getIsSpinning()) {
				spinningTimer.increaseTimer();
				landingTimer.decreaseTimer();
				nestingTimer.decreaseTimer();
			} else {
				landingTimer.decreaseTimer();
				nestingTimer.decreaseTimer();
				spinningTimer.decreaseTimer();
			}
		}
	}

	@Override
	public void onLivingUpdate() {
		super.onLivingUpdate();
        if (world.isRemote) {
        	this.flapTicks++;
        	
            if (!isSilent() && !getIsNesting()) {
            	float flapAngle1 = MathHelper.cos(this.flapTicks * this.flapSpeed);
            	float flapAngle2 = MathHelper.cos((this.flapTicks + 1) * this.flapSpeed);
            	if(flapAngle1 <= 0.3f && flapAngle2 > 0.3f) {
            		world.playSound(posX, posY, posZ, getFlySound(), getSoundCategory(), getIsLanding() ? 0.25F : 0.5F, getIsLanding() ? 1.5F + rand.nextFloat() * 0.3F : 0.8F + rand.nextFloat() * 0.3F, false);
            	}
            }
            
            prevAnimTime = animTime;
            float flaptimer = 0.079F;
            if (getIsLanding())
                animTime += flaptimer * 2F;
            if (getIsNesting())
                animTime = 0;
            else
                animTime += flaptimer;

            if (isAIDisabled())
                animTime = 0.5F;

        }
    }

	@Override
	protected void updateAITasks() {
		if (getIsNesting()) {
			if (!getEntityWorld().isRemote) {
				if (getEntityWorld().getBlockState(new BlockPos(posX, posY - 1, posZ)).isNormalCube()) {
					setIsNesting(false);
				} else if (getAttackTarget() != null) {
					setIsNesting(false);
					getEntityWorld().playEvent(null, 1025, getPosition(), 0);
				}
			}
		}
	}

	@Override
	public void updatePassenger(Entity entity) {
		super.updatePassenger(entity);
		if (entity instanceof EntityLivingBase) {
			double a = Math.toRadians(renderYawOffset);
			double offSetX = -Math.sin(a) * 0.6D;
			double offSetZ = Math.cos(a) * 0.6D;
			entity.setPosition(posX - offSetX, posY - entity.height + MathHelper.sin((ticksExisted) * 0.5F) * 0.3F, posZ - offSetZ);
			if (entity.isSneaking())
				entity.setSneaking(false);
		}
	}

	@Override
	public boolean shouldRiderSit() {
		return false;
	}

	@Override
    public boolean canPassengerSteer() {
      return false; //TODO different rider cases
    }

	public boolean getReturnToNest() {
		return dataManager.get(RETURN_TO_NEST);
	}

	public void setReturnToNest(boolean broody) {
		dataManager.set(RETURN_TO_NEST, broody);
	}
	
	public boolean getIsLanding() {
		return dataManager.get(IS_LANDING);
	}

	public void setIsLanding(boolean landing) {
		dataManager.set(IS_LANDING, landing);
	}

	public void setBroodCount(int count) {
		broodCount = count;
	}

	public int getBroodCount() {
		return broodCount;
	}
	
	public void setDroppingTimer(int count) {
		droppingTimer = count;
	}

	public int getDroppingTimer() {
		return droppingTimer;
	}

	public boolean getIsNesting() {
		return dataManager.get(IS_NESTING);
	}

	public void setIsNesting(boolean nesting) {
		dataManager.set(IS_NESTING, nesting);
	}

	public boolean getIsSpinning() {
		return dataManager.get(IS_SPINNING);
	}

	public void setIsSpinning(boolean spinning) {
		dataManager.set(IS_SPINNING, spinning);
	}

	@Override
	protected ResourceLocation getLootTable() {
		return LootTableRegistry.CHIROMAW;
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return SoundRegistry.CHIROMAW_MATRIARCH_LIVING;
	}

	@Nullable
	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return SoundRegistry.CHIROMAW_MATRIARCH_HURT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundRegistry.CHIROMAW_MATRIARCH_DEATH;
	}

	protected SoundEvent getFlySound() {
		return SoundRegistry.CHIROMAW_MATRIARCH_FLAP;
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(10.0D);
		getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(64.0D);
		getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(2.0D);
		getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.095D);
	}

	@Override
	public boolean attackEntityAsMob(Entity entityIn) {
		return EntityAIAttackOnCollide.useStandardAttack(this, entityIn);
	}

	@Override
	public int getMaxSpawnedInChunk() {
		return 1;
	}
	
	@Override
    public float getBlockPathWeight(BlockPos pos) {
        return 0.5F;
    }

    @Override
    protected boolean isValidLightLevel() {
    	return true;
    }

    @Override
    protected boolean canDespawn() {
    	return false;
    }
    
	@Override
	@Nullable
	public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, @Nullable IEntityLivingData livingdata) {
		if(!world.isRemote)
			setBoundOrigin(getPosition());
		return livingdata;
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound compound) {
		super.readEntityFromNBT(compound);
		if (compound.hasKey("BoundX")) 
			boundOrigin = new BlockPos(compound.getInteger("BoundX"), compound.getInteger("BoundY"), compound.getInteger("BoundZ"));
		setBroodCount(compound.getInteger("BroodCount"));
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound compound) {
		super.writeEntityToNBT(compound);
		if (boundOrigin != null) {
			compound.setInteger("BoundX", boundOrigin.getX());
			compound.setInteger("BoundY", boundOrigin.getY());
			compound.setInteger("BoundZ", boundOrigin.getZ());
		}
		compound.setInteger("BroodCount", getBroodCount());
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float damage) {
		if (source.equals(DamageSource.IN_WALL) || source.equals(DamageSource.DROWN))
			return false;
		if (source instanceof EntityDamageSourceIndirect && source.getTrueSource() == this)
			return false;
		return super.attackEntityFrom(source, damage);
	}

	@Nullable
	public BlockPos getBoundOrigin() {
		return boundOrigin;
	}

	public void setBoundOrigin(@Nullable BlockPos boundOriginIn) {
		boundOrigin = boundOriginIn;
	}
	
	public AxisAlignedBB getNestBox() {
		return new AxisAlignedBB(boundOrigin, boundOrigin.up(4)).grow(0.0625D, 0F, 0.0625D);
		
	}

	public class AIPoop extends EntityAIBase {
		private final EntityChiromawMatriarch largeChiromaw;

		public AIPoop(EntityChiromawMatriarch large_chiromaw) {
			setMutexBits(0);
			largeChiromaw = large_chiromaw;
		}

		@Override
		public boolean shouldExecute() {
			return !largeChiromaw.getIsNesting() && largeChiromaw.rand.nextInt(10) == 0; // frequency of random pooping
		}

		@Override
		public boolean shouldContinueExecuting() {
			return false;
		}

		@Override
		public void updateTask() {
			checkForPoopTarget();
		}

		private void checkForPoopTarget() {
			int distanceToSurface = MathHelper.floor(largeChiromaw.posY) - largeChiromaw.world.getHeight(new BlockPos(largeChiromaw)).getY();
			// TODO use a poop entity that can be aimed rather than dropping
			// from a random pos nearby
			List<BlockPos> placeToPoop = new ArrayList<>();
			if (distanceToSurface >= 16) {
				AxisAlignedBB underBox = largeChiromaw.getEntityBoundingBox().grow(0.625D, distanceToSurface, 0.625D).offset(0, -distanceToSurface / 2.0D, 0);
				if (!getPoopTarget(world, underBox).isEmpty()) {
					EntityPlayer player = getPoopTarget(world, underBox).get(0);
					if (player != null) {
						for (BlockPos posDrop : BlockPos.getAllInBox(player.getPosition().add(-1, 0, -1), player.getPosition().add(1, 2, 1)))
							if (world.isAirBlock(posDrop) && world.canSeeSky(posDrop))
								placeToPoop.add(posDrop);
						if (!placeToPoop.isEmpty()) {
							Collections.shuffle(placeToPoop);
							BlockPos posPoop = placeToPoop.get(0);
							EntityChiromawDroppings poopEntity = new EntityChiromawDroppings(world, largeChiromaw, posPoop.getX() + 0.5D, largeChiromaw.getPosition().getY(), posPoop.getZ() + 0.5D);
							world.spawnEntity(poopEntity);
							largeChiromaw.getEntityWorld().playSound(null, largeChiromaw.getPosition(), SoundRegistry.CHIROMAW_MATRIARCH_POOP, SoundCategory.HOSTILE, 0.5F, 1F + (largeChiromaw.getEntityWorld().rand.nextFloat() - largeChiromaw.getEntityWorld().rand.nextFloat()) * 0.8F);
						}
					}
				}
			}
		}

		public List<EntityPlayer> getPoopTarget(World world, AxisAlignedBB underBox) {
			return world.<EntityPlayer>getEntitiesWithinAABB(EntityPlayer.class, underBox, EntitySelectors.IS_ALIVE);
	    }
	}

	class AIFindNearestTarget<T extends EntityLivingBase> extends EntityAINearestAttackableTarget<T> {

		protected double range;
		
		public AIFindNearestTarget(EntityCreature creature, Class<T> classTarget, boolean checkSight, double rangeIn) {
			super(creature, classTarget, checkSight);
			range = rangeIn;
		}

		public AIFindNearestTarget(EntityCreature creature, Class<T> classTarget, int chance, boolean checkSight, boolean onlyNearby, @Nullable final Predicate <? super T > targetSelector) {
			super(creature, classTarget, chance, checkSight, onlyNearby, targetSelector);
		}

		@Override
		public boolean shouldExecute() {
			if (super.shouldExecute()) {
				if (targetEntity != null) {
					double distance = taskOwner.getDistanceSq(targetEntity);
					if (distance <= 256.0D)
						taskOwner.getEntityWorld().playSound(null, taskOwner.getPosition(), SoundRegistry.CHIROMAW_MATRIARCH_ROAR, SoundCategory.HOSTILE, 2F, 1.5F);
				}
				return true;
			}
			return false;
		}

		@Override
	    protected AxisAlignedBB getTargetableArea(double targetDistance) {
	        return taskOwner.getEntityBoundingBox().grow(targetDistance, targetDistance, targetDistance);
	    }

		@Override
	    protected double getTargetDistance() {
	        return range;
	    }
	}

	class AIPickUpAndDropAttack extends EntityAIBase {
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
		private final EntityChiromawMatriarch largeChiromaw;
		float rotation;
		public AIPickUpAndDropAttack(EntityChiromawMatriarch large_chiromaw, double speedIn, boolean useLongMemory) {
			largeChiromaw = large_chiromaw;
	        world = largeChiromaw.world;
	        speedTowardsTarget = speedIn;
	        longMemory = useLongMemory;
	        setMutexBits(1);
		}

		@Override
		public boolean shouldExecute() {
			EntityLivingBase entitylivingbase = largeChiromaw.getAttackTarget();
			//if (largeChiromaw.getReturnToNest())
			//	return false;
			if (entitylivingbase == null)
				return false;
			else if (!entitylivingbase.isEntityAlive())
				return false;
			else {
				if (canPenalize) {
					if (--delayCounter <= 0) {
						path = largeChiromaw.getNavigator().getPathToEntityLiving(entitylivingbase);
						delayCounter = 4 + largeChiromaw.getRNG().nextInt(7);
						return path != null;
					} else
						return true;
				}
				path = largeChiromaw.getNavigator().getPathToEntityLiving(entitylivingbase);
				if (path != null)
					return true;
				else
					return getAttackReachSqr(entitylivingbase) >= largeChiromaw.getDistanceSq(entitylivingbase.posX, entitylivingbase.getEntityBoundingBox().minY, entitylivingbase.posZ);
			}
		}

		@Override
	    public boolean shouldContinueExecuting() {
			EntityLivingBase entitylivingbase = largeChiromaw.getAttackTarget();
			if (entitylivingbase == null)
				return false;
			if (largeChiromaw.isBeingRidden())
				return false;
			if (!entitylivingbase.isEntityAlive())
	            return false;
			if (!longMemory)
	            return !largeChiromaw.getNavigator().noPath();
			if (!largeChiromaw.isWithinHomeDistanceFromPosition(new BlockPos(entitylivingbase)))
	            return false;
			return !(entitylivingbase instanceof EntityPlayer) || !((EntityPlayer)entitylivingbase).isSpectator() && !((EntityPlayer)entitylivingbase).isCreative();
	    }

		@Override
	    public void startExecuting() {
	        largeChiromaw.getNavigator().setPath(path, speedTowardsTarget);
	        delayCounter = 0;
	    }

		@Override
	    public void resetTask() {
	        EntityLivingBase entitylivingbase = largeChiromaw.getAttackTarget();
	        if (entitylivingbase instanceof EntityPlayer && (((EntityPlayer)entitylivingbase).isSpectator() || ((EntityPlayer)entitylivingbase).isCreative()))
	            largeChiromaw.setAttackTarget((EntityLivingBase)null);
	        largeChiromaw.getNavigator().clearPath();
	        rotation = 0;
			setIsSpinning(false);
	    }

		@Override
	    public void updateTask() {
	        EntityLivingBase entitylivingbase = largeChiromaw.getAttackTarget();
	        if(!largeChiromaw.getIsSpinning())
	        	largeChiromaw.getLookHelper().setLookPositionWithEntity(entitylivingbase, 30.0F, 30.0F);
	        double distToEnemySqr = largeChiromaw.getDistanceSq(entitylivingbase.posX, entitylivingbase.getEntityBoundingBox().minY, entitylivingbase.posZ);
	        --delayCounter;
			if (!largeChiromaw.isBeingRidden()) {
		        if ((longMemory || largeChiromaw.getEntitySenses().canSee(entitylivingbase)) && delayCounter <= 0 && (targetX == 0.0D && targetY == 0.0D && targetZ == 0.0D || entitylivingbase.getDistanceSq(targetX, targetY, targetZ) >= 1.0D || largeChiromaw.getRNG().nextFloat() < 0.05F)) {
		            targetX = entitylivingbase.posX;
		            targetY = entitylivingbase.getEntityBoundingBox().minY;
		            targetZ = entitylivingbase.posZ;
		            delayCounter = 4 + largeChiromaw.getRNG().nextInt(7);
	
		            if (canPenalize) {
		                delayCounter += failedPathFindingPenalty;
		                if (largeChiromaw.getNavigator().getPath() != null) {
		                    net.minecraft.pathfinding.PathPoint finalPathPoint = largeChiromaw.getNavigator().getPath().getFinalPathPoint();
		                    if (finalPathPoint != null && entitylivingbase.getDistanceSq(finalPathPoint.x, finalPathPoint.y, finalPathPoint.z) < 1)
		                        failedPathFindingPenalty = 0;
		                    else
		                        failedPathFindingPenalty += 10;
		                }
		                else
		                    failedPathFindingPenalty += 10;
		            }
		            if (distToEnemySqr > 1024.0D)
		                delayCounter += 10;
		            else if (distToEnemySqr > 256.0D)
		                delayCounter += 5;
		            if (!largeChiromaw.getNavigator().tryMoveToEntityLiving(entitylivingbase, speedTowardsTarget))
		                delayCounter += 15;
		        }
		        attackTick = Math.max(attackTick - 1, 0);
		        if (largeChiromaw.rand.nextBoolean())
		        	checkAndPerformDropAttack(entitylivingbase, distToEnemySqr);
		        else
		        	checkAndPerformSpinAttack(entitylivingbase, distToEnemySqr);
			}

			if (largeChiromaw.isBeingRidden()) {
				if (entitylivingbase != null && !world.isAirBlock(largeChiromaw.getPosition().down(3)) || largeChiromaw.getPosition().getY() < largeChiromaw.pickupHeight + 16D) {
					largeChiromaw.getNavigator().clearPath();
					Vec3d vec3d = RandomPositionGenerator.findRandomTargetBlockAwayFrom(largeChiromaw, 16, 10, new Vec3d(largeChiromaw.getBoundOrigin().getX() + 0.5D, largeChiromaw.getBoundOrigin().getY() + 0.5D, largeChiromaw.getBoundOrigin().getZ() + 0.5D));
					if(vec3d != null)
						largeChiromaw.getNavigator().tryMoveToXYZ(vec3d.x, vec3d.y, vec3d.z, 2D);
					largeChiromaw.motionY += 0.05D;
				}
				
				if (!world.isRemote &&  (largeChiromaw.posY >= largeChiromaw.pickupHeight + 16D || largeChiromaw.getDroppingTimer() <= 0 || !world.isRemote && world.isSideSolid(new BlockPos (MathHelper.floor(largeChiromaw.posX), MathHelper.floor(largeChiromaw.posY + 1D), MathHelper.floor(largeChiromaw.posZ)), EnumFacing.DOWN))) {
					largeChiromaw.setReturnToNest(true);
					//largeChiromaw.setAttackTarget((EntityLivingBase)null);
					largeChiromaw.removePassengers();
					largeChiromaw.getEntityWorld().playSound(null, largeChiromaw.getPosition(), SoundRegistry.CHIROMAW_MATRIARCH_RELEASE, SoundCategory.HOSTILE, 0.5F, 1F + (largeChiromaw.getEntityWorld().rand.nextFloat() - largeChiromaw.getEntityWorld().rand.nextFloat()) * 0.8F);
				}

				if (largeChiromaw.getDroppingTimer() >= 0)
					largeChiromaw.setDroppingTimer(getDroppingTimer() - 1);
			}
			
			if(largeChiromaw.getIsSpinning()) {
	    		rotation += 30;
	    		if (rotation%30 == 0) {
	    			Vec3d targetVector = new Vec3d(entitylivingbase.posX, entitylivingbase.posY, entitylivingbase.posZ);
	    			Vec3d chiromawVectorToTarget = new Vec3d(largeChiromaw.posX, largeChiromaw.posY, largeChiromaw.posZ).subtract(targetVector);
	    			double height = chiromawVectorToTarget.y;
	    			double distance = chiromawVectorToTarget.length();
	    			double angle = MathHelper.atan2(height, distance) * (180D / Math.PI);
	    			EntityBLArrow arrow = new EntityBLArrow(world, largeChiromaw);
	    			arrow.setType(EnumArrowType.CHIROMAW_BARB);
	    			arrow.shoot(largeChiromaw, (float)angle, rotation, 1.5F, 0.5F, 0.5F);
	    			largeChiromaw.getEntityWorld().playSound(null, largeChiromaw.getPosition(), SoundRegistry.CHIROMAW_MATRIARCH_BARB_FIRE, SoundCategory.HOSTILE, 0.5F, 1F + (largeChiromaw.getEntityWorld().rand.nextFloat() - largeChiromaw.getEntityWorld().rand.nextFloat()) * 0.8F);
	    			world.spawnEntity(arrow);
	    		}
	    		if(rotation >= 720) {
	    			rotation = 0;
	    			setIsSpinning(false);
	    		}
			}
	    }

	    private void checkAndPerformSpinAttack(EntityLivingBase entitylivingbase, double distToEnemySqr) {
			if (distToEnemySqr >= 36.0D && distToEnemySqr <= 100.0D && attackTick <= 0) {
				largeChiromaw.getEntityWorld().playSound(null, largeChiromaw.getPosition(), SoundRegistry.CHIROMAW_MATRIARCH_ROAR, SoundCategory.HOSTILE, 1F, 1F + (largeChiromaw.getEntityWorld().rand.nextFloat() - largeChiromaw.getEntityWorld().rand.nextFloat()) * 0.8F);
				attackTick = 40;
				largeChiromaw.setIsSpinning(true);
			}
			else
				return;
		}

		protected void checkAndPerformDropAttack(EntityLivingBase enemy, double distToEnemySqr) {
	        double attackReachSq = getAttackReachSqr(enemy);
	        if (distToEnemySqr <= attackReachSq && attackTick <= 0)  {
	            attackTick = 20;
	            largeChiromaw.jump();
	            largeChiromaw.swingArm(EnumHand.MAIN_HAND);
	            largeChiromaw.attackEntityAsMob(enemy);
	    		if (enemy.isSneaking())
	    			enemy.setSneaking(false);
	    		largeChiromaw.getEntityWorld().playSound(null, largeChiromaw.getPosition(), SoundRegistry.CHIROMAW_MATRIARCH_GRAB, SoundCategory.HOSTILE, 0.5F, 1F + (largeChiromaw.getEntityWorld().rand.nextFloat() - largeChiromaw.getEntityWorld().rand.nextFloat()) * 0.8F);
	    		enemy.startRiding(largeChiromaw, true);
	    		largeChiromaw.pickupHeight = largeChiromaw.posY;
	    		largeChiromaw.setDroppingTimer(120);
	        }
	    }

	    protected double getAttackReachSqr(EntityLivingBase attackTarget)  {
	        return (double)(largeChiromaw.width * 2.0F * largeChiromaw.width * 2.0F + attackTarget.width);
	    }
	}

	class AIMoveRandom extends EntityAIBase {
		private final EntityChiromawMatriarch largeChiromaw;

		public AIMoveRandom(EntityChiromawMatriarch large_chiromaw) {
			setMutexBits(1);
			largeChiromaw = large_chiromaw;
		}

		@Override
		public boolean shouldExecute() {
			return !largeChiromaw.getReturnToNest() && !largeChiromaw.getMoveHelper().isUpdating() && largeChiromaw.rand.nextInt(10) == 0;
		}

		@Override
		public boolean shouldContinueExecuting() {
			return false;
		}

		@Override
		public void updateTask() {
			BlockPos blockpos = largeChiromaw.getBoundOrigin();

			if (blockpos == null) {
				blockpos = new BlockPos(largeChiromaw);
			}

			for (int i = 0; i < 3; ++i) {
				BlockPos blockpos1 = blockpos.add(largeChiromaw.rand.nextInt(33) - 16, largeChiromaw.rand.nextInt(17) - 8, largeChiromaw.rand.nextInt(33) - 16);

				if (largeChiromaw.world.isAirBlock(blockpos1)) {
					largeChiromaw.moveHelper.setMoveTo((double) blockpos1.getX() + 0.5D, (double) blockpos1.getY() + 0.5D, (double) blockpos1.getZ() + 0.5D, 3D);

					if (largeChiromaw.getAttackTarget() == null) {
						largeChiromaw.getLookHelper().setLookPosition((double) blockpos1.getX() + 0.5D, (double) blockpos1.getY() + 0.5D, (double) blockpos1.getZ() + 0.5D, 180.0F, 20.0F);
					}
					break;
				}
			}
		}
	}
	
	class AIChangeNest extends EntityAIBase {
		private final EntityChiromawMatriarch largeChiromaw;

		protected float maxRangeSq = 180 * 180;
		
		public AIChangeNest(EntityChiromawMatriarch large_chiromaw) {
			setMutexBits(0);
			largeChiromaw = large_chiromaw;
		}

		@Override
		public boolean shouldExecute() {
			return !largeChiromaw.getReturnToNest() && largeChiromaw.getAttackTarget() == null && 
					(largeChiromaw.getBoundOrigin() == null || largeChiromaw.getBoundOrigin().distanceSq(largeChiromaw.posX, largeChiromaw.posY, largeChiromaw.posZ) < 64) && largeChiromaw.rand.nextInt(100) == 0; //TODO Timer?
		}

		@Override
		public boolean shouldContinueExecuting() {
			return false;
		}

		@Override
		public void updateTask() {
			BetweenlandsWorldStorage worldStorage = BetweenlandsWorldStorage.forWorld(largeChiromaw.world);
			
			List<LocationStorage> priorityNests = new ArrayList<>();
			List<LocationStorage> otherNests = new ArrayList<>();
			
			for(ILocalStorage localStorage : worldStorage.getLocalStorageHandler().getLoadedStorages()) {
				
				if(localStorage instanceof LocationStorage && localStorage.getBoundingBox() != null &&
						localStorage.getBoundingBox().getCenter().squareDistanceTo(largeChiromaw.posX, largeChiromaw.posY, largeChiromaw.posZ) < maxRangeSq &&
						((LocationStorage) localStorage).getType() == EnumLocationType.FLOATING_ISLAND) { //TODO Check for nest location instead
					
					Vec3d center = localStorage.getBoundingBox().getCenter();
					
					if(largeChiromaw.world.getClosestPlayer(center.x, center.y, center.z, 32, entity -> !((EntityPlayer) entity).isCreative() && !((EntityPlayer) entity).isSpectator()) != null) {
						priorityNests.add((LocationStorage) localStorage);
					} else {
						otherNests.add((LocationStorage) localStorage);
					}
				}
				
			}

			LocationStorage nest = null;
			if(!priorityNests.isEmpty()) {
				Collections.shuffle(priorityNests);
				nest = priorityNests.get(0);
			} else if(!otherNests.isEmpty()) {
				Collections.shuffle(otherNests);
				nest = otherNests.get(0);
			}
			
			if(nest != null) {
				largeChiromaw.setBoundOrigin(new BlockPos(nest.getBoundingBox().getCenter())); //TODO Use correct nest position
				largeChiromaw.setIsNesting(false);
				largeChiromaw.setReturnToNest(true);
			}
		}
	}

	class AIReturnToNest extends EntityAIBase {
		private final EntityChiromawMatriarch largeChiromaw;
		protected double x;
		protected double y;
		protected double z;
		private final double speed;

		public AIReturnToNest(EntityChiromawMatriarch large_chiromaw, double speedIn) {
			largeChiromaw = large_chiromaw;
			speed = speedIn;
			setMutexBits(1);
		}

		@Override
		public boolean shouldExecute() {
			EntityLivingBase entitylivingbase = largeChiromaw.getAttackTarget();
			if (entitylivingbase != null)
				return false;

			if (largeChiromaw.getReturnToNest() && !largeChiromaw.getIsNesting()) {

				Vec3d nestLocation = getNestPosition();

				if (nestLocation == null) {
					return false;
				} else {

					x = nestLocation.x;
					y = nestLocation.y;
					z = nestLocation.z;
					return true;
				}
			}
			return false;
		}

		@Nullable
		protected Vec3d getNestPosition() {
			return new Vec3d(getBoundOrigin().getX() + 0.5D, largeChiromaw.getBoundOrigin().getY() + 0.5D, largeChiromaw.getBoundOrigin().getZ() + 0.5D);
		}

		@Override
		public boolean shouldContinueExecuting() {
			return !largeChiromaw.getNavigator().noPath() && !largeChiromaw.getMoveHelper().isUpdating();
		}

		@Override
		public void startExecuting() {
			if(!largeChiromaw.getMoveHelper().isUpdating()) {
				largeChiromaw.getNavigator().tryMoveToXYZ(x, y, z, speed);
			}
		}
	}
}
