package thebetweenlands.common.entity.monster;

import javax.annotation.Nullable;

import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
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
	public static final EntityDataAccessor<Integer> PARENT_ID = SynchedEntityData.defineId(BonePuppetBase.class, EntityDataSerializers.INT);
	public static final EntityDataAccessor<Integer> ATTACK_TIMER = SynchedEntityData.defineId(BonePuppetBase.class, EntityDataSerializers.INT);
	public static final EntityDataAccessor<Boolean> IS_ATTACKING = SynchedEntityData.defineId(BonePuppetBase.class, EntityDataSerializers.BOOLEAN);

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
        builder.define(PARENT_ID, -1);
        builder.define(ATTACK_TIMER, 0);
        builder.define(IS_ATTACKING, false);
    }

	@Nullable
	public BoneShaman getParentEntity() {
		BoneShaman parentEntity = (BoneShaman) level().getEntity(getEntityData().get(PARENT_ID));
		return getEntityData().get(PARENT_ID) != -1 ? parentEntity : null;
	}

	@Override
	public void tick() {
		super.tick();
		//TODO temp disable for test
	/*	if (!this.level().isClientSide())
			if (getParentEntity() == null || getParentEntity().isDeadOrDying())
				kill();
	*/
	}

	@Override
	protected void tickDeath() {
		if (onGround()) {
			lastSpawningAnimationTicks = getSpawnTimer();
			if (!level().isClientSide()) {
				if (getSpawnTimer() > 0)
					setSpawnTimer(getSpawnTimer() - 1);
				if (getSpawnTimer() == 0)
					remove(Entity.RemovalReason.KILLED);
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
			compound.putInt("parent", getParentEntity().getId());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        setSpawnTimer(compound.getInt("spawn_timer"));
		if(compound.contains("parent", Tag.TAG_INT))
			setParentEntityID(compound.getInt("parent"));
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

	public Integer getParentEntityID() {
		return getEntityData().get(PARENT_ID);
	}

	public void setParentEntityID(Integer parentID) {
		getEntityData().set(PARENT_ID, parentID);
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

	@Override
	public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
		if (PARENT_ID.equals(key))
			setParentEntityID(getParentEntityID());
		super.onSyncedDataUpdated(key);
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
