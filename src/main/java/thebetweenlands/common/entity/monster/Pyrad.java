package thebetweenlands.common.entity.monster;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.Vec3;
import thebetweenlands.client.particle.ParticleFactory;
import thebetweenlands.client.particle.options.EntitySwirlParticleOptions;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.entity.ai.goals.EntityAIFlyRandomly;
import thebetweenlands.common.entity.ai.goals.EntityAIMoveToDirect;
import thebetweenlands.common.entity.ai.goals.PyradAttackGoal;
import thebetweenlands.common.entity.movement.BLFlightMoveControl;
import thebetweenlands.common.registries.ParticleRegistry;
import thebetweenlands.common.registries.SoundRegistry;

import javax.annotation.Nullable;

public class Pyrad extends FlyingMonster {

	private static final byte EVENT_HURT = 77;
	private static final EntityDataAccessor<Boolean> CHARGING = SynchedEntityData.defineId(Pyrad.class, EntityDataSerializers.BOOLEAN);
	private static final EntityDataAccessor<Boolean> ACTIVE = SynchedEntityData.defineId(Pyrad.class, EntityDataSerializers.BOOLEAN);

	private int glowTicks = 0;
	private int prevGlowTicks = 0;

	private int activeTicks = 0;
	private int prevActiveTicks = 0;

	private int hitTicks = 0;
	private int prevHitTicks = 0;

	public Pyrad(EntityType<? extends FlyingMonster> type, Level level) {
		super(type, level);
		this.setPathfindingMalus(PathType.WATER, -1.0F);
		this.setPathfindingMalus(PathType.LAVA, 8.0F);
		this.setPathfindingMalus(PathType.DANGER_FIRE, 0.0F);
		this.xpReward = 10;
		this.setActive(false);
		this.refreshDimensions();
	}

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder) {
		super.defineSynchedData(builder);
		builder.define(CHARGING, false);
		builder.define(ACTIVE, false);
	}

	@Override
	protected void registerGoals() {
		this.goalSelector.addGoal(0, new FloatGoal(this));
		this.goalSelector.addGoal(2, new EntityAIMoveToDirect<>(this, 1.0D) {
			@Nullable
			@Override
			protected Vec3 getTarget() {
				LivingEntity target = this.entity.getTarget();
				if (target != null) {
					BlockPos pos = this.entity.blockPosition();
					int groundHeight = BLFlightMoveControl.getGroundHeight(this.entity.level(), pos, 16, pos).getY();
					Vec3 dir = new Vec3(target.getX() - this.entity.getX(), target.getY() + 1 - this.entity.getRandom().nextFloat() * 0.3 - this.entity.getY() - 1, target.getZ() - this.entity.getZ());
					double dst = dir.length();
					if (dst > 10) {
						dir = dir.normalize();
						this.setSpeed(0.75D);
						return new Vec3(this.entity.getX() + dir.x * (dst - 10), Math.min(this.entity.getY() + dir.y * (dst - 10), Math.max(groundHeight + 2, target.getY() + 2)), this.entity.getZ() + dir.z * (dst - 10));
					} else if (dst < 5) {
						dir = dir.normalize();
						this.setSpeed(1.0D);
						return new Vec3(this.entity.getX() - dir.x * 2, Math.min(this.entity.getY() - dir.y * 2, Math.max(groundHeight + (this.entity.isCharging() ? 6 : 2), target.getY() + 2)), this.entity.getZ() - dir.z * 2);
					}
				}
				return null;
			}
		});
		this.goalSelector.addGoal(3, new EntityAIFlyRandomly<>(this) {
			@Override
			protected double getTargetX(RandomSource rand, double distanceMultiplier) {
				return this.entity.getX() + ((rand.nextFloat() * 2.0F - 1.0F) * 10.0F * distanceMultiplier);
			}

			@Override
			protected double getTargetY(RandomSource rand, double distanceMultiplier) {
				return this.entity.getY() + (rand.nextFloat() * 1.45D - 1.0D) * 4.0D * distanceMultiplier;
			}

			@Override
			protected double getTargetZ(RandomSource rand, double distanceMultiplier) {
				return this.entity.getZ() + ((rand.nextFloat() * 2.0F - 1.0F) * 10.0F * distanceMultiplier);
			}

			@Override
			protected double getFlightSpeed() {
				return 0.5D;
			}
		});
		this.goalSelector.addGoal(4, new PyradAttackGoal(this));
		this.goalSelector.addGoal(5, new MoveTowardsRestrictionGoal(this, 0.04D));
		this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));
		this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));

		this.targetSelector.addGoal(0, new HurtByTargetGoal(this).setAlertOthers());
		this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true));
	}

	public static AttributeSupplier.Builder registerAttributes() {
		return FlyingMonster.createMonsterAttributes()
			.add(Attributes.MAX_HEALTH, 60.0D)
			.add(Attributes.ATTACK_DAMAGE, 6.0D)
			.add(Attributes.MOVEMENT_SPEED, 0.1D)
			.add(Attributes.FLYING_SPEED, 0.1D)
			.add(Attributes.FOLLOW_RANGE, 28.0D);
	}

	@Nullable
	@Override
	protected SoundEvent getAmbientSound() {
		if (!this.isActive()) {
			return null;
		}
		return SoundRegistry.PYRAD_LIVING.get();
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource damageSource) {
		return SoundRegistry.PYRAD_HURT.get();
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundRegistry.PYRAD_DEATH.get();
	}

	@Override
	public void addAdditionalSaveData(CompoundTag tag) {
		super.addAdditionalSaveData(tag);
		tag.putBoolean("active", this.isActive());
	}

	@Override
	public void readAdditionalSaveData(CompoundTag tag) {
		super.readAdditionalSaveData(tag);
		this.setActive(tag.getBoolean("active"));
	}

	@Override
	public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
		if (ACTIVE.equals(key)) {
			this.refreshDimensions();
		}
		super.onSyncedDataUpdated(key);
	}

	@Override
	protected EntityDimensions getDefaultDimensions(Pose pose) {
		return !this.isActive() ? EntityDimensions.scalable(0.7F, 1.2F) : super.getDefaultDimensions(pose);
	}

	@Override
	public void aiStep() {
		if (!this.onGround() && this.getDeltaMovement().y() < 0.0D) {
			this.setDeltaMovement(this.getDeltaMovement().multiply(1.0D, 0.6D, 1.0D));
		}

		if (!this.level().isClientSide()) {
			boolean day = this.level().getTimeOfDay(1) >= 0.5F;

			if (this.isAlive() && (!day || this.isInWater()) && !this.isActive()) {
				this.setActive(true);
			}

			if (this.isAlive() && this.isActive() && this.getTarget() == null && this.getRandom().nextInt(800) == 0) {
				this.setActive(false);
			}
		} else {
			this.prevGlowTicks = this.glowTicks;
			if (this.isCharging() && this.glowTicks < 10) {
				this.glowTicks++;
			} else if (!this.isCharging() && this.glowTicks > 0) {
				this.glowTicks--;
			}

			this.prevActiveTicks = this.activeTicks;
			if (this.isActive() && this.activeTicks < 60) {
				this.activeTicks++;
			} else if (!this.isActive() && this.activeTicks > 0 && this.onGround()) {
				this.activeTicks--;
			}
		}

		this.prevHitTicks = this.hitTicks;
		if (this.hitTicks > 0) {
			this.hitTicks--;
		}

		if (!this.isActive() && this.activeTicks == 0) {
			this.yBodyRot = this.yHeadRot = this.getYRot();
		}

		if (this.level().isClientSide() && this.isActive()) {
			if (this.getRandom().nextInt(4) == 0) {
				ParticleFactory.ParticleArgs<?> args = ParticleFactory.ParticleArgs.get().withDataBuilder().setData(2, this).buildData();
				if (this.isCharging()) {
					args.withColor(0.9F, 0.35F, 0.1F, 1);
				} else {
					args.withColor(1F, 0.65F, 0.25F, 1);
				}
				TheBetweenlands.createParticle(EntitySwirlParticleOptions.defaultSwirl(ParticleRegistry.LEAF_SWIRL.get()), this.level(), this.getX(), this.getY(), this.getZ(), args);
			}
			if (this.isCharging() || this.getRandom().nextInt(10) == 0) {
				ParticleFactory.ParticleArgs<?> args = ParticleFactory.ParticleArgs.get().withMotion((this.getRandom().nextFloat() - 0.5F) / 4.0F, (this.getRandom().nextFloat() - 0.5F) / 4.0F, (this.getRandom().nextFloat() - 0.5F) / 4.0F);
				if (this.isCharging()) {
					args.withColor(0.9F, 0.35F, 0.1F, 1);
					args.withData(60);
				} else {
					args.withColor(1F, 0.65F, 0.25F, 1);
				}
				TheBetweenlands.createParticle(ParticleRegistry.LEAF.get(), this.level(), this.getX(), this.getY() + this.getEyeHeight(), this.getZ(), args);
			}
		}

		super.aiStep();
	}

	public float getGlowTicks(float partialTicks) {
		return Mth.lerp(partialTicks, this.prevGlowTicks, this.glowTicks);
	}

	public float getActiveTicks(float partialTicks) {
		return Mth.lerp(partialTicks, this.prevActiveTicks, this.activeTicks);
	}

	public float getHitTicks(float partialTicks) {
		return Mth.lerp(partialTicks, this.prevHitTicks, this.hitTicks);
	}

	@Override
	public void travel(Vec3 travelVector) {
		if (!this.isActive()) {
			this.setDeltaMovement(0.0D, this.getDeltaMovement().y() - 0.1D, 0.0D);
		}

		super.travel(travelVector);
	}

	@Override
	public boolean hurt(DamageSource source, float amount) {
		if (source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) return super.hurt(source, amount);

		if (!this.isActive()) {
			if (this.hitTicks <= 0) {
				if (!this.level().isClientSide()) {
					if ((this.getRandom().nextInt(12) == 0 || amount > 3.0F)) {
						this.setActive(true);
						return super.hurt(source, amount);
					}
				}
			}
			return false;
		} else {
			return super.hurt(source, amount);
		}
	}

	@Override
	public void indicateDamage(double xDistance, double zDistance) {
		for (int i = 0; i < 10; i++) {
			this.level().broadcastEntityEvent(this, EVENT_HURT);
			this.playSound(SoundRegistry.PYRAD_HURT.get(), 0.1F, 0.3F + this.getRandom().nextFloat() * 0.3F);
		}
		this.hitTicks = 20;
	}

	@Override
	public void knockback(double strength, double x, double z) {
		if (this.isActive()) {
			super.knockback(strength, x, z);
		}
	}

	@Override
	public boolean isPushable() {
		return super.isPushable() && this.isActive();
	}

	@Override
	protected void tickDeath() {
		if (this.isActive()) {
			this.setActive(false);
		}
		if (this.onGround() && this.activeTicks < 20) {
			this.deathTime++;
			if (this.deathTime > 10) {
				if (this.level().isClientSide()) {
					for (int i = 0; i < 10; i++) {
						ParticleFactory.ParticleArgs<?> args = ParticleFactory.ParticleArgs.get().withMotion((this.getRandom().nextFloat() - 0.5F) / 2.0F, (this.getRandom().nextFloat() - 0.5F) / 2.0F, (this.getRandom().nextFloat() - 0.5F) / 2.0F).withScale(2.0F);
						args.withColor(0.25F, 0.25F + this.getRandom().nextFloat() * 0.5F, 0.05F + this.getRandom().nextFloat() * 0.25F, 1);
						TheBetweenlands.createParticle(ParticleRegistry.LEAF.get(), this.level(), this.getX(), this.getY() + 0.8D, this.getZ(), args);
						args = ParticleFactory.ParticleArgs.get().withMotion((this.getRandom().nextFloat() - 0.5F) / 2.0F, (this.getRandom().nextFloat() - 0.5F) / 2.0F, (this.getRandom().nextFloat() - 0.5F) / 2.0F);
						TheBetweenlands.createParticle(ParticleRegistry.SMOOTH_SMOKE.get(), this.level(), this.getX(), this.getY() + 0.8D, this.getZ(), args);
					}
				}
				if (this.deathTime > 30 && !this.level().isClientSide()) {
					for (int i = 0; i < 10; i++) {
						this.playSound(SoundRegistry.PYRAD_HURT.get(), 0.18F, 0.1F + this.getRandom().nextFloat() * 0.2F);
						this.playSound(SoundRegistry.PYRAD_DEATH.get(), 0.08F, 0.1F + this.getRandom().nextFloat() * 0.2F);
					}
					this.remove(Entity.RemovalReason.KILLED);
				}
			}
		}
	}

	public boolean isCharging() {
		return this.getEntityData().get(CHARGING);
	}

	public void setCharging(boolean charging) {
		this.getEntityData().set(CHARGING, charging);
	}

	public boolean isActive() {
		return this.getEntityData().get(ACTIVE);
	}

	public void setActive(boolean active) {
		this.getEntityData().set(ACTIVE, active);

		if (!this.level().isClientSide()) {
			this.getNavigation().stop();
			if (active) {
				this.registerGoals();
			} else {
				this.goalSelector.removeAllGoals(goal -> true);
				this.targetSelector.removeAllGoals(goal -> true);
			}
		}
	}

	@Override
	public void handleEntityEvent(byte id) {
		if (id == EVENT_HURT) {
			ParticleFactory.ParticleArgs<?> args = ParticleFactory.ParticleArgs.get().withMotion((this.getRandom().nextFloat() - 0.5F) / 2.0F, (this.getRandom().nextFloat() - 0.5F) / 2.0F, (this.getRandom().nextFloat() - 0.5F) / 2.0F).withColor(1.0F, 0.65F, 0.25F, 1.0F);
			TheBetweenlands.createParticle(ParticleRegistry.LEAF.get(), this.level(), this.getX(), this.getY() + 0.8D, this.getZ(), args);
			this.hitTicks = 20;
		} else {
			super.handleEntityEvent(id);
		}
	}
}
