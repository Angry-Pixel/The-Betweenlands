package thebetweenlands.common.entity.mobs;

import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.projectile.EntitySmallFireball;
import net.minecraft.init.SoundEvents;
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
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import thebetweenlands.common.entity.ai.EntityAIAttackOnCollide;
import thebetweenlands.common.entity.movement.FlightMoveHelper;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.registries.LootTableRegistry;
import thebetweenlands.common.registries.SoundRegistry;

public class EntityChiromawGreeblingRider extends EntityChiromaw {
	private static final DataParameter<Boolean> IS_SHOOTING = EntityDataManager.createKey(EntityChiromawGreeblingRider.class, DataSerializers.BOOLEAN);
	private static final DataParameter<Integer> RELOAD_TIMER = EntityDataManager.createKey(EntityChiromawGreeblingRider.class, DataSerializers.VARINT);
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
		tasks.addTask(2, new EntityChiromawGreeblingRider.EntityAIMoveTowardsTargetWithDistance(this, 1D, 7, 32));
		//this.tasks.addTask(1, new EntityAIAttackMelee(this, 1.0D, true));
		//this.tasks.addTask(2, new EntityAIFlyingWander(this, 0.5D));
		targetTasks.addTask(1, new EntityAINearestAttackableTarget<EntitySheep>(this, EntitySheep.class, true).setUnseenMemoryTicks(160));
	}

	@Override
	protected void entityInit() {
		super.entityInit();

		this.dataManager.register(IS_SHOOTING, false);
		this.dataManager.register(RELOAD_TIMER, 0);
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		// WIP Temp
		if (!getEntityWorld().isRemote) {
			if(getAttackTarget() != null) {
			if (getReloadTimer() < 90 && !getIsShooting())
				setReloadTimer(getReloadTimer() + 2);
			if (getReloadTimer() >= 90 && getIsShooting() && getReloadTimer() < 100)
				setReloadTimer(getReloadTimer() + 4);
			}
			if(getAttackTarget() == null) {
			if (getReloadTimer() > 0 && !getIsShooting())
				setReloadTimer(getReloadTimer() - 2);
			}
			// if(getIsShooting())
			//System.out.println("Timer: " + getReloadTimer());
			//System.out.println("Shooting: " + getIsShooting());
		}
	}

	@Override
	protected void updateAITasks() {
		super.updateAITasks();
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

	@Override
	protected ResourceLocation getLootTable() {
		return LootTableRegistry.CHIROMAW;
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
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(10.0D);
		getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(32.0D);
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
		setLeftHanded(true);
		setItemStackToSlot(EntityEquipmentSlot.MAINHAND, new ItemStack(ItemRegistry.GEM_SINGER));
		return livingdata;
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
				if (distance >= 36.0D && distance <= 256.0D && chiromawRider.getReloadTimer() == 90) {
						return true;
				} else
					return false;
			}
		}

		@Override
		public boolean shouldContinueExecuting() {
			return chiromawRider.recentlyHit <= 40 && chiromawRider.getReloadTimer() >= 90;
		}

		@Override
		public void startExecuting() {
			//chiromawRider.getEntityWorld().playSound(null, chiromawRider.getPosition(), SoundRegistry.SOMETHING, SoundCategory.HOSTILE, 1F, 1F);
		}

		@Override
		public void updateTask() {
			EntityLivingBase entitylivingbase = chiromawRider.getAttackTarget();
			if(!chiromawRider.getIsShooting())
				chiromawRider.setIsShooting(true);
			chiromawRider.getLookHelper().setLookPositionWithEntity(target, 30.0F, 30.0F);
			float f = (float) MathHelper.atan2(target.posZ - chiromawRider.posZ, target.posX - chiromawRider.posX);
			int distance = MathHelper.floor(chiromawRider.getDistance(target));
			if(chiromawRider.getReloadTimer() == 90) {
				double radialDistance = 1D;
				
				double targetX = entitylivingbase.posX - chiromawRider.posX;
				double targetY = entitylivingbase.getEntityBoundingBox().minY + (double) (entitylivingbase.height / 2.0F) - (chiromawRider.posY + (double) (chiromawRider.height / 2.0F));
				double targetZ = entitylivingbase.posZ - chiromawRider.posZ;
				
				EntitySmallFireball entitysmallfireball = new EntitySmallFireball(chiromawRider.getEntityWorld(), chiromawRider, targetX,  targetY, targetZ);
				entitysmallfireball.setPosition(chiromawRider.posX + (double) MathHelper.cos(f) * radialDistance, chiromawRider.posY + (double) (chiromawRider.height / 2.0F) + 0.5D, chiromawRider.posZ + (double) MathHelper.sin(f) * radialDistance);
				chiromawRider.getEntityWorld().spawnEntity(entitysmallfireball);
				chiromawRider.getEntityWorld().playSound(null, chiromawRider.getPosition(), SoundEvents.ITEM_FIRECHARGE_USE, SoundCategory.HOSTILE, 0.5F, 1F + (chiromawRider.getEntityWorld().rand.nextFloat() - chiromawRider.getEntityWorld().rand.nextFloat()) * 0.8F);
				}
			if (chiromawRider.getReloadTimer() == 102) {
				if (chiromawRider.getIsShooting()) {
					chiromawRider.setIsShooting(false);
					chiromawRider.setReloadTimer(0);
				}
			}
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
			} else if (target.getDistanceSq(chiromawRider) > (double) (maxTargetDistance * maxTargetDistance)) {
				return false;
			} else if (target.getDistanceSq(chiromawRider) < (double) (minTargetDistance * minTargetDistance) || target.getDistanceSq(chiromawRider) < (double) (maxTargetDistance * maxTargetDistance)) {
				Vec3d vec3d = RandomPositionGenerator.findRandomTargetBlockTowards(chiromawRider, 16, 1, new Vec3d(target.posX, target.posY, target.posZ));

				if (vec3d == null) {
					return false;
				} else {
					this.movePosX = vec3d.x;
					this.movePosY = vec3d.y;
					this.movePosZ = vec3d.z;
					return true;
				}
			}
			else
				return false;
		}

		@Override
	    public boolean shouldContinueExecuting() {
	        return !chiromawRider.getNavigator().noPath() && target.isEntityAlive() && target.getDistanceSq(chiromawRider) < (double)(maxTargetDistance * maxTargetDistance);
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