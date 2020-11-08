package thebetweenlands.common.entity.mobs;

import javax.annotation.Nullable;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAvoidEntity;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIPanic;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import thebetweenlands.api.entity.IEntityBL;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.client.render.particle.ParticleFactory.ParticleArgs;
import thebetweenlands.common.entity.EntitySplodeshroom;
import thebetweenlands.common.entity.ai.EntityAIFollowTarget;
import thebetweenlands.common.entity.ai.EntityAIJumpRandomly;
import thebetweenlands.common.registries.LootTableRegistry;
import thebetweenlands.common.registries.SoundRegistry;
import thebetweenlands.common.world.storage.BetweenlandsWorldStorage;

public class EntitySporeling extends EntityCreature implements IEntityBL {
	private float jumpHeightOverride = -1;
	
	protected static final DataParameter<Boolean> IS_FALLING = EntityDataManager.<Boolean>createKey(EntitySporeling.class, DataSerializers.BOOLEAN);

	protected float prevFloatingRotationTicks = 0;
	protected float floatingRotationTicks = 0;
	
	private EntityAIAvoidEntity<EntityLivingBase> aiRunAway;
	private EntityAIFollowTarget moveToTarget;
	private boolean canFollow = false;

	public EntitySporeling(World world) {
		super(world);
		setSize(0.3F, 0.6F);
		stepHeight = 1.0F;
		this.experienceValue = 1;
		
		this.setPathPriority(PathNodeType.WATER, -1.0F);
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		dataManager.register(IS_FALLING, false);
	}

	@Override
	protected void initEntityAI() {
		super.initEntityAI();
		aiRunAway = new EntityAIAvoidEntity<EntityLivingBase>(this, EntityLivingBase.class, entity -> entity instanceof EntityMob || entity instanceof IMob || (entity instanceof EntityPlayer && !((EntityPlayer) entity).isCreative()), 10.0F, 0.5D, 1.0D);
		moveToTarget = new EntityAIFollowTarget(this, new EntityAIFollowTarget.FollowClosest(this, EntityPlayer.class, 16), 1D, 0.5F, 16.0F, false);

		tasks.addTask(0, new EntityAISwimming(this));
		tasks.addTask(1, new EntityAIPanic(this, 1.0D));
		tasks.addTask(2, aiRunAway);
		tasks.addTask(4, new EntityAIFollowTarget(this, new EntityAIFollowTarget.FollowClosest(this, EntityRootSprite.class, 10), 0.65D, 0.5F, 10.0F, false));
		tasks.addTask(5, new EntityAIJumpRandomly(this, 10, () -> !EntitySporeling.this.world.getEntitiesWithinAABB(EntityRootSprite.class, this.getEntityBoundingBox().grow(1)).isEmpty()) {
			@Override
			public void startExecuting() {
				EntitySporeling.this.setJumpHeightOverride(0.2F);
				EntitySporeling.this.getJumpHelper().setJumping();
			}
		});
		tasks.addTask(6, new EntityAIWander(this, 0.6D));
		tasks.addTask(7, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F));
		tasks.addTask(8, new EntityAILookIdle(this));
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.49D);
		getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(5.0D);
		getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(16.0D);
	}

	@Override
	public void onLivingUpdate() {
		if(this.world.isRemote) {
			BLParticles.REDSTONE_DUST.spawn(this.world, posX + (rand.nextDouble() - 0.5D) * width, posY + rand.nextDouble() * height - 0.25D, posZ + (rand.nextDouble() - 0.5D) * width, 
					ParticleArgs.get().withColor(0.5F + this.rand.nextFloat() * 0.5F, 0.5F + this.rand.nextFloat() * 0.5F, 0.5F + this.rand.nextFloat() * 0.5F, 1.0F));
		}
		super.onLivingUpdate();
	}

	@Override
	public void onUpdate() {
		if (!getEntityWorld().isRemote) {
			if (!this.isInWater()) {
				boolean canSpin = (this.getRidingEntity() != null ? !this.getRidingEntity().onGround : !onGround) && !this.isInWeb && !this.isInWater() && !this.isInLava() && world.isAirBlock(getPosition().down());
				if (canSpin && (motionY < 0D || (this.getRidingEntity() != null && this.getRidingEntity().motionY < 0))) {
					if (!getIsFalling())
						setIsFalling(true);
					motionY *= 0.7D;
					if(this.getRidingEntity() != null) {
						this.getRidingEntity().motionY *= 0.7D;
						this.getRidingEntity().fallDistance = 0;
					}
				} else {
					if (!canSpin && getIsFalling()) {
						setIsFalling(false);
					}
				}
			}

			if (isBeingRidden() && getPassengers().get(0) instanceof EntitySplodeshroom && !canFollow) {
				tasks.removeTask(aiRunAway);
				tasks.addTask(3, moveToTarget);
				canFollow = true;
			}

			if (!isBeingRidden() && canFollow) {
				tasks.addTask(2, aiRunAway);
				tasks.removeTask(moveToTarget);
				canFollow = false;
			}
		}

		this.prevFloatingRotationTicks = this.floatingRotationTicks;
		if(this.getIsFalling()) {
			this.floatingRotationTicks += 30;
			float wrap = MathHelper.wrapDegrees(this.floatingRotationTicks) - this.floatingRotationTicks;
			this.floatingRotationTicks +=wrap;
			this.prevFloatingRotationTicks += wrap;
		} else {
			this.floatingRotationTicks = 0;
		}
		super.onUpdate();
	}

    public float smoothedAngle(float partialTicks) {
        return this.prevFloatingRotationTicks + (this.floatingRotationTicks - this.prevFloatingRotationTicks) * partialTicks;
    }

    private float updateRotation(float angle, float targetAngle, float maxIncrease) {
        float f = MathHelper.wrapDegrees(targetAngle - angle);

        if (f > maxIncrease)
        {
            f = maxIncrease;
        }

        if (f < -maxIncrease)
        {
            f = -maxIncrease;
        }

        return angle + f;
    }
    
	private void setIsFalling(boolean state) {
		dataManager.set(IS_FALLING, state);
	}

	public boolean getIsFalling() {
		return dataManager.get(IS_FALLING);
	}

	@Override
	public void fall(float distance, float damageMultiplier) {
	}

	@Override
	public int getMaxSpawnedInChunk() {
		return 1;
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return SoundRegistry.SPORELING_LIVING;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource damageSource) {
		return SoundRegistry.SPORELING_HURT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundRegistry.SPORELING_DEATH;
	}

	@Override
	protected boolean canDespawn() {
		return false;
	}
	
	@Override
	protected ResourceLocation getLootTable() {
		return LootTableRegistry.SPORELING;
	}
	
	public void setJumpHeightOverride(float jumpHeightOverride) {
		this.jumpHeightOverride = jumpHeightOverride;
	}
	
	@Override
	protected float getJumpUpwardsMotion() {
		if(this.jumpHeightOverride > 0) {
			float height = this.jumpHeightOverride;
			this.jumpHeightOverride = -1;
			return height;
		}
		return super.getJumpUpwardsMotion();
	}

	@Nullable
	@Override
	public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, @Nullable IEntityLivingData livingdata) {
		if (!getEntityWorld().isRemote) {
			if(isEntityAlive() && isBloodSkiesActive(world)) {
				EntitySplodeshroom shroom = new EntitySplodeshroom(world);
				shroom.setLocationAndAngles(posX, posY, posZ, world.rand.nextFloat() * 360, 0);
				if(!world.containsAnyLiquid(shroom.getEntityBoundingBox()) && world.getCollisionBoxes(shroom, shroom.getEntityBoundingBox()).isEmpty()) {
					shroom.onInitialSpawn(world.getDifficultyForLocation(new BlockPos(posX, posY, posZ)), null);
					world.spawnEntity(shroom);
					shroom.startRiding(this);
				}
			}
			else
				canFollow = true;
		}
		return livingdata;
	}

	public boolean isBloodSkiesActive(World world) {
		BetweenlandsWorldStorage worldStorage = BetweenlandsWorldStorage.forWorld(world);
        if(worldStorage.getEnvironmentEventRegistry().bloodSky.isActive())
            return true;
        return false;
	}
}
