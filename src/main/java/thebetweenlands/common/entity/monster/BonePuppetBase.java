package thebetweenlands.common.entity.monster;

import java.util.UUID;

import javax.annotation.Nullable;

import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import thebetweenlands.common.entity.BLEntity;
import thebetweenlands.common.registries.ItemRegistry;

public abstract class BonePuppetBase extends Monster implements BLEntity {

	public static final EntityDataAccessor<Integer> SPAWN_TIMER = SynchedEntityData.defineId(BonePuppetBase.class, EntityDataSerializers.INT);
	public static final EntityDataAccessor<Integer> ATTACK_TIMER = SynchedEntityData.defineId(BonePuppetBase.class, EntityDataSerializers.INT);
	public static final EntityDataAccessor<Boolean> IS_ATTACKING = SynchedEntityData.defineId(BonePuppetBase.class, EntityDataSerializers.BOOLEAN);
	@Nullable
	public UUID masterUUID;
	@Nullable
	public Entity cachedMaster;
    public int lastSpawningAnimationTicks = 0;
    public int spawnDuration = 30;
    public int prevAttackTimer;

    public BonePuppetBase(EntityType<? extends Monster> type, Level level) {
        super(type, level);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(SPAWN_TIMER, 0);
        builder.define(ATTACK_TIMER, 0);
        builder.define(IS_ATTACKING, false);
    }

    public void setParentEntity(@Nullable Entity master) {
        if (master != null) {
            this.masterUUID = master.getUUID();
            this.cachedMaster = master;
        }
    }

    @Nullable
    public Entity getParentEntity()  {
        if (this.cachedMaster != null && !this.cachedMaster.isRemoved()) {
            return this.cachedMaster;
        } else if (this.masterUUID != null && this.level() instanceof ServerLevel serverlevel) {
            this.cachedMaster = serverlevel.getEntity(this.masterUUID);
            return this.cachedMaster;
        } else {
            return null;
        }
    }

	@Override
	public void tick() {
		super.tick();
		if (!this.level().isClientSide())
			if (getParentEntity() == null || ((LivingEntity) getParentEntity()).isDeadOrDying()) // will do unless we make some odd entity owning shit
				kill();
	}

	@Override
	protected void tickDeath() {
		if (onGround()) {
			lastSpawningAnimationTicks = getSpawnTimer();
			if (!level().isClientSide()) {
				if (getSpawnTimer() > 0)
					setSpawnTimer(getSpawnTimer() - 1);

				if (getSpawnTimer() == 0)  {
					if(getParentEntity() instanceof BoneShaman shaman)
						shaman.setPuppetCount(shaman.getPuppetCount() - 1);
					remove(Entity.RemovalReason.KILLED);
				}
			}

			if (level().isClientSide())
				if (getSpawnTimer() < 10)
					spawnEmergingParticles();
		}
	}

	@Override
	public void aiStep() {
		if (isAlive()) {
			lastSpawningAnimationTicks = getSpawnTimer();
			if (!level().isClientSide()) {
				if (getSpawnTimer() < spawnDuration)
					setSpawnTimer(getSpawnTimer() + 1);
			}
			if (level().isClientSide())
				if (getSpawnTimer() < 10)
					spawnEmergingParticles();
		}

		super.aiStep();

		if (level().isClientSide()) {
			prevAttackTimer = getAttackTimer();
			if (getAttackTimer() == 0)
				prevAttackTimer = 0;
			if (isDeadOrDying())
				setDeltaMovement(Vec3.ZERO);
		}

		if (!level().isClientSide()) {
			if (isDeadOrDying())
				getNavigation().stop();
			if (isAlive()) {
				if (isAttacking()) {
					setAttackTimer(getAttackTimer() + 1);
					if (getAttackTimer() > 20) {
						setAttackTimer(0);
						setAttacking(false);
					}
				} else
					setAttackTimer(0);
			}
			else {
				setAttackTimer(0);
			}
		}
	}

    public void spawnEmergingParticles() {
		double px = getX();
		double py = getY();
		double pz = getZ();
		for (int i = 0, amount = 5 + level().getRandom().nextInt(2); i < amount; i++) {
			double ox = level().getRandom().nextDouble() * 0.1F - 0.05F;
			double oz = level().getRandom().nextDouble() * 0.1F - 0.05F;
			double motionX = level().getRandom().nextDouble() * 0.2F - 0.1F;
			double motionY = level().getRandom().nextDouble() * 0.1F + 0.075F;
			double motionZ = level().getRandom().nextDouble() * 0.2F - 0.1F;
			level().addParticle(new BlockParticleOption(ParticleTypes.BLOCK, getBlockStateOn()), false, px + ox, py, pz + oz, motionX, motionY, motionZ);
		}
	}

	@Override
    protected boolean isImmobile() {
        return isAlive() && (super.isImmobile() || getSpawnTimer() < spawnDuration);
    }

    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        boolean isCreative = source.getEntity() instanceof Player player && player.isCreative();
        return (isEmerging() && !isCreative) || super.isInvulnerableTo(source);
    }

    @Override
    public boolean isPushable() {
        return !isEmerging() && super.isPushable();
    }

	@Override
	public boolean hurt(DamageSource source, float amount) {
		if (isEmerging() && source.is(DamageTypes.IN_WALL))
			return false;
		return super.hurt(source, amount);
	}

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("spawn_timer", getSpawnTimer());
		if (getParentEntity() != null)
			compound.putUUID("Master", masterUUID);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        setSpawnTimer(compound.getInt("spawn_timer"));
		if(compound.hasUUID("Master")) {
            this.masterUUID = compound.getUUID("Master");
            this.cachedMaster = null;
		}
    }

    @Override
    public void restoreFrom(Entity entity) {
        super.restoreFrom(entity);
        if (entity instanceof BonePuppetBase puppet) {
            this.cachedMaster = puppet.cachedMaster;
        }
    }

    @Override
    protected SoundEvent getAmbientSound() {
		return null;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
		return null;
    }

    @Override
    protected SoundEvent getDeathSound() {
		return null;
    }

    public boolean isEmerging() {
        return getEntityData().get(SPAWN_TIMER) < spawnDuration;
    }

    public int getSpawnTimer() {
        return getEntityData().get(SPAWN_TIMER);
    }

    public void setSpawnTimer(int timer) {
        getEntityData().set(SPAWN_TIMER, timer);
    }

    public void setAttackTimer(int progress) {
    	getEntityData().set(ATTACK_TIMER, progress);
    }

    public int getAttackTimer() {
        return getEntityData().get(ATTACK_TIMER);
    }

    public void setAttacking(boolean attacking) {
    	getEntityData().set(IS_ATTACKING, attacking);
    }

    public boolean isAttacking() {
        return getEntityData().get(IS_ATTACKING);
    }

    public float getSpawningAnimation(float partialTicks) {
        return Mth.lerp(partialTicks, lastSpawningAnimationTicks, getSpawnTimer()) / (float) spawnDuration;
    }

    // May need this for when wights do the thing and stuffs
    public boolean isWearingSkullMask(LivingEntity entity) {
        ItemStack helmet = entity.getItemBySlot(EquipmentSlot.HEAD);
        return !helmet.isEmpty() && helmet.is(ItemRegistry.SKULL_MASK);
    }

}
