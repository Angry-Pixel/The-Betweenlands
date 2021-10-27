package thebetweenlands.common.entity.mobs;

import javax.annotation.Nullable;

import com.google.common.base.Predicate;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.util.DamageSource;
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
import thebetweenlands.api.entity.IPullerEntity;
import thebetweenlands.common.entity.ai.EntityAIAttackOnCollide;
import thebetweenlands.common.entity.ai.EntityAIFlyingWander;
import thebetweenlands.common.entity.ai.EntityAINearestAttackableSmellyTarget;
import thebetweenlands.common.entity.draeton.EntityDraeton;
import thebetweenlands.common.entity.movement.FlightMoveHelper;
import thebetweenlands.common.entity.projectiles.EntityBetweenstonePebble;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.registries.LootTableRegistry;
import thebetweenlands.common.registries.SoundRegistry;

public class EntityChiromawGreeblingRider extends EntityChiromaw {
	private static final DataParameter<Boolean> IS_SHOOTING = EntityDataManager.createKey(EntityChiromawGreeblingRider.class, DataSerializers.BOOLEAN);
	private static final DataParameter<Integer> RELOAD_TIMER = EntityDataManager.createKey(EntityChiromawGreeblingRider.class, DataSerializers.VARINT);
	public boolean playPullSound;
	
	public EntityChiromawGreeblingRider(World world) {
		super(world);
		setSize(0.7F, 0.9F);
		setIsHanging(false);

		this.moveHelper = new FlightMoveHelper(this);
		setPathPriority(PathNodeType.WATER, -8F);
		setPathPriority(PathNodeType.BLOCKED, -8.0F);
		setPathPriority(PathNodeType.OPEN, 8.0F);
		setPathPriority(PathNodeType.FENCE, -8.0F);
	}

	@Override
	protected void initEntityAI() {
		tasks.addTask(0, new EntityAISwimming(this));
		tasks.addTask(1, new EntityChiromawGreeblingRider.EntityAISlingshotAttack(this));
		tasks.addTask(2, new EntityChiromawGreeblingRider.EntityAIMoveTowardsTargetWithDistance(this, 1.5D, 8, 128));
		tasks.addTask(3, new EntityAIFlyingWander(this, 0.75D, 5));
        targetTasks.addTask(1, new EntityAIHurtByTarget(this, false));
		targetTasks.addTask(2, new EntityAIFindNearestTarget<EntityLivingBase>(this, EntityLivingBase.class, 10, true, false, e -> e instanceof IPullerEntity, 6).setUnseenMemoryTicks(160));
		targetTasks.addTask(3, new EntityAIFindNearestTarget<EntityLivingBase>(this, EntityLivingBase.class, 10, true, false, e -> e instanceof EntityChiromawMatriarch, 0).setUnseenMemoryTicks(160));
		targetTasks.addTask(4, new EntityAINearestAttackableSmellyTarget<>(this, EntityPlayer.class, true));
	}

	@Override
	protected void entityInit() {
		super.entityInit();

		this.dataManager.register(IS_SHOOTING, false);
		this.dataManager.register(RELOAD_TIMER, 0);
	}

	@Override
	public boolean getCanSpawnHere() {
		return this.world.getDifficulty() != EnumDifficulty.PEACEFUL;
	}
	
	@Override
	public void onUpdate() {
		super.onUpdate();
		// WIP Temp
		if (!getEntityWorld().isRemote) {
			if (getAttackTarget() != null) {
				if (getReloadTimer() < 90 && !getIsShooting()) {
					setReloadTimer(Math.min(90, getReloadTimer() + 2));
				}
				if (getReloadTimer() >= 90 && getIsShooting() && getReloadTimer() < 100) {
					setReloadTimer(Math.min(102, getReloadTimer() + 4));
				}
			} else {
				if (getReloadTimer() > 0 && !getIsShooting()) {
					setReloadTimer(Math.max(0, getReloadTimer() - 2));
				}
			}

			if (getReloadTimer() <= 0)
				playPullSound = true;

			if (isPulling())
				if (playPullSound) {
					getEntityWorld().playSound(null, getPosition(), SoundRegistry.SLINGSHOT_CHARGE, SoundCategory.HOSTILE, 1F, 1F);
					playPullSound = false;
				}
		}
	}
	
	@Override
	protected void onDeathUpdate() {
		super.onDeathUpdate();
		
		if(!this.world.isRemote && this.isDead) {
			EntityGreeblingVolarpadFloater floater = new EntityGreeblingVolarpadFloater(this.world, posX, posY, posZ);
			this.world.spawnEntity(floater);
		}
	}
	
	@Override
	protected void updateAITasks() {
		super.updateAITasks();
		if (getIsHanging())
			if (!this.world.isRemote)
				setIsHanging(false);
	}

	public boolean getIsShooting() {
		return dataManager.get(IS_SHOOTING);
	}

	public void setIsShooting(boolean shooting) {
		dataManager.set(IS_SHOOTING, shooting);
	}
	
	public int getReloadTimer() {
		return dataManager.get(RELOAD_TIMER);
	}

	public void setReloadTimer(int timer) {
		dataManager.set(RELOAD_TIMER, timer);
	}

	public boolean isPulling() {
		return dataManager.get(RELOAD_TIMER) > 0 && dataManager.get(RELOAD_TIMER) < 90 ;
	}

	@Override
	protected ResourceLocation getLootTable() {
		return LootTableRegistry.CHIROMAW;
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return SoundRegistry.CHIROMAW_GREEBLING_RIDER_LIVING;
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
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(10.0D);
		getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(30.0D);
		getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(2.0D);
		getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.095D);
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

    @Override
    protected boolean isValidLightLevel() {
    	return true;
    }
    
	@Override
	@Nullable
	public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, @Nullable IEntityLivingData livingdata) {
		setItemStackToSlot(EntityEquipmentSlot.OFFHAND, new ItemStack(ItemRegistry.SIMPLE_SLINGSHOT));
		return livingdata;
	}

	static class EntityAIFindNearestTarget<T extends EntityLivingBase> extends EntityAINearestAttackableTarget<T> {

		protected double minHeight;
		
		public EntityAIFindNearestTarget(EntityCreature creature, Class<T> classTarget, boolean checkSight, double minHeight) {
			super(creature, classTarget, checkSight);
			this.minHeight = minHeight;
		}

		public EntityAIFindNearestTarget(EntityCreature creature, Class<T> classTarget, int chance, boolean checkSight, boolean onlyNearby, @Nullable final Predicate <? super T > targetSelector, double minHeight) {
			super(creature, classTarget, chance, checkSight, onlyNearby, targetSelector);
			this.minHeight = minHeight;
		}

		@Override
		public boolean shouldExecute() {
			if (super.shouldExecute()) {
				if (targetEntity != null) {
					if(minHeight > 0) {
						Entity checkEntity = targetEntity;
						
						if(checkEntity instanceof IPullerEntity) {
							EntityDraeton carriage = ((IPullerEntity) checkEntity).getCarriage();
							if(carriage != null) {
								checkEntity = carriage;
							}
						}
						
						BlockPos surface = taskOwner.world.getHeight(new BlockPos(checkEntity));
						if(checkEntity.posY - surface.getY() < minHeight) {
							targetEntity = null;
							return false;
						}
					}
					
					double distance = taskOwner.getDistanceSq(targetEntity);
					
					if (distance <= 576.0D) {
						taskOwner.getEntityWorld().playSound(null, taskOwner.getPosition(), SoundRegistry.GREEBLING_HEY, SoundCategory.HOSTILE, 0.5F, 1F);
					}
				}
				
				return true;
			}
			return false;
		}

		@Override
	    protected AxisAlignedBB getTargetableArea(double targetDistance) {
	        return this.taskOwner.getEntityBoundingBox().grow(targetDistance, targetDistance, targetDistance);
	    }

		@Override
	    protected double getTargetDistance() {
	        return 90; //because softcoding is for wimps :P
	    }

    }

    static class EntityAISlingshotAttack extends EntityAIBase {
    	EntityChiromawGreeblingRider chiromawRider;
		EntityLivingBase target;
		
		public EntityAISlingshotAttack(EntityChiromawGreeblingRider chiromawRider) {
			this.chiromawRider = chiromawRider;
			this.setMutexBits(5);
		}

		@Override
		public boolean shouldExecute() {
			target = chiromawRider.getAttackTarget();

			if (target == null)
				return false;
			else {
				double distance = chiromawRider.getDistanceSq(target);
				if (distance >= 36.0D && distance <= 576.0D && chiromawRider.getReloadTimer() >= 90) {
						return true;
				} else
					return false;
			}
		}

		@Override
		public boolean shouldContinueExecuting() {
			return target != null && chiromawRider.recentlyHit <= 40 && chiromawRider.getReloadTimer() >= 90;
		}

		@Override
		public void startExecuting() {
			//chiromawRider.getEntityWorld().playSound(null, chiromawRider.getPosition(), SoundRegistry.GREEBLING_HEY, SoundCategory.HOSTILE, 1F, 1F);
		}

		@Override
		public void updateTask() {
			if(!chiromawRider.getIsShooting())
				chiromawRider.setIsShooting(true);
			if(target != null) {
				chiromawRider.faceEntity(target, 30F, 30F);
				chiromawRider.getLookHelper().setLookPositionWithEntity(target, 30.0F, 30.0F);
				if (chiromawRider.getReloadTimer() == 90) {
					double targetX = target.posX - chiromawRider.posX;
					double targetY = target.getEntityBoundingBox().minY + (double) (target.height / 2.0F) - (chiromawRider.posY + (double) (chiromawRider.height / 2.0F));
					double targetZ = target.posZ - chiromawRider.posZ;
					double targetDistance = (double) MathHelper.sqrt(targetX * targetX + targetZ * targetZ);
					EntityBetweenstonePebble pebble = new EntityBetweenstonePebble(chiromawRider.getEntityWorld(), chiromawRider);
					pebble.setDamage(2D);
					pebble.shoot(targetX, targetY + targetDistance * 0.10000000298023224D, targetZ, 1.6F, 0F);
					chiromawRider.getEntityWorld().spawnEntity(pebble);
					chiromawRider.getEntityWorld().playSound(null, chiromawRider.getPosition(), SoundRegistry.SLINGSHOT_SHOOT, SoundCategory.HOSTILE, 1F, 1F + (chiromawRider.getEntityWorld().rand.nextFloat() - chiromawRider.getEntityWorld().rand.nextFloat()) * 0.8F);
					chiromawRider.playPullSound = true;
				}
			}
			if (chiromawRider.getReloadTimer() == 102) {
				if (chiromawRider.getIsShooting()) {
					chiromawRider.setIsShooting(false);
					chiromawRider.setReloadTimer(0);
					chiromawRider.playPullSound = true;
				}
				resetTask();
			}
		}

		@Override
	    public void resetTask() {
	        target = null;
	    }
	}

    static class EntityAIMoveTowardsTargetWithDistance extends EntityAIBase {
    	EntityChiromawGreeblingRider chiromawRider;
		EntityLivingBase target;
	    double movePosX;
	    double movePosY;
	    double movePosZ;
	    double speed;
	    float minTargetDistance;
	    float maxTargetDistance;
	    
		public EntityAIMoveTowardsTargetWithDistance(EntityChiromawGreeblingRider chiromawRider, double speedIn,  float targetMinDistance, float targetMaxDistance) {
			this.chiromawRider = chiromawRider;
	        this.speed = speedIn;
	        this.minTargetDistance = targetMinDistance;
	        this.maxTargetDistance = targetMaxDistance;
	        this.setMutexBits(1);
		}

		@Override
		public boolean shouldExecute() {
			target = chiromawRider.getAttackTarget();

			if (target == null) {
				return false;
			} else if (chiromawRider.getDistanceSq(target) > (double) (maxTargetDistance * maxTargetDistance)) {
				return false;
			} else if (chiromawRider.getDistanceSq(target) > (double) (minTargetDistance * minTargetDistance) && chiromawRider.getDistanceSq(target) <= (double) (maxTargetDistance * maxTargetDistance)) {
				Vec3d vec3d = findNextPointTowards(8, 3, new Vec3d(target.posX, target.posY, target.posZ));
				if (vec3d == null) {
					return false;
				} else {
					flyToLocation(vec3d);
					return true;
				}
			} else if (chiromawRider.getDistanceSq(target) <= (double) (minTargetDistance * minTargetDistance)) {
				 Vec3d vec3d = findNextPointAway(16, 2, new Vec3d(target.posX, target.posY, target.posZ));
				if (vec3d == null) {
					return false;
				} else {
					flyToLocation(vec3d);
					return true;
				}
			}
			else
				return false;
		}
		
		@Nullable
		private Vec3d findNextPointTowards(int xz, int y, Vec3d target) {
			Vec3d newTarget = RandomPositionGenerator.findRandomTargetBlockTowards(this.chiromawRider, xz, y, target);
			if(newTarget == null) {
				return null;
			}
			if(target.y < this.chiromawRider.posY) {
				newTarget = new Vec3d(newTarget.x, this.chiromawRider.posY - Math.abs(newTarget.y - this.chiromawRider.posY), newTarget.z);
			} else {
				newTarget = new Vec3d(newTarget.x, this.chiromawRider.posY + Math.abs(newTarget.y - this.chiromawRider.posY), newTarget.z);
			}
			return newTarget;
		}
		
		@Nullable
		private Vec3d findNextPointAway(int xz, int y, Vec3d target) {
			Vec3d newTarget = RandomPositionGenerator.findRandomTargetBlockAwayFrom(this.chiromawRider, xz, y, target);
			if(newTarget == null) {
				return null;
			}
			if(target.y > this.chiromawRider.posY) {
				newTarget = new Vec3d(newTarget.x, this.chiromawRider.posY - Math.abs(newTarget.y - this.chiromawRider.posY), newTarget.z);
			} else {
				newTarget = new Vec3d(newTarget.x, this.chiromawRider.posY + Math.abs(newTarget.y - this.chiromawRider.posY), newTarget.z);
			}
			return newTarget;
		}

		public void flyToLocation(Vec3d vec3d) {
			this.movePosX = vec3d.x;
			this.movePosY = vec3d.y;
			this.movePosZ = vec3d.z;
		}

		@Override
	    public boolean shouldContinueExecuting() {
	        return !chiromawRider.getNavigator().noPath() && target.isEntityAlive() && target.getDistanceSq(chiromawRider) <= (double)(maxTargetDistance * maxTargetDistance);
	    }

		@Override
	    public void resetTask() {
	        target = null;
	    }
		
		@Override
	    public void startExecuting() {
			chiromawRider.getNavigator().tryMoveToXYZ(movePosX, movePosY, movePosZ, speed);
	    }
    }
}