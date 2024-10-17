package thebetweenlands.common.entity.creature;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.util.TimeUtil;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.ResetUniversalAngerTargetGoal;
import net.minecraft.world.entity.ai.navigation.AmphibiousPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.fluids.FluidType;
import org.jetbrains.annotations.Nullable;
import thebetweenlands.client.particle.ParticleFactory;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.entity.BLEntity;
import thebetweenlands.common.entity.ai.goals.LurkerFindBaitGoal;
import thebetweenlands.common.entity.ai.goals.NearestSmellyAttackableTargetGoal;
import thebetweenlands.common.entity.fishing.anadia.Anadia;
import thebetweenlands.common.entity.movement.LurkerMoveControl;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.SoundRegistry;
import thebetweenlands.util.MathUtils;

import java.util.UUID;

public class Lurker extends PathfinderMob implements BLEntity, NeutralMob, Enemy {
	private static final EntityDataAccessor<Boolean> IS_LEAPING = SynchedEntityData.defineId(Lurker.class, EntityDataSerializers.BOOLEAN);
	private static final EntityDataAccessor<Boolean> SHOULD_MOUTH_BE_OPEN = SynchedEntityData.defineId(Lurker.class, EntityDataSerializers.BOOLEAN);
	private static final EntityDataAccessor<Float> MOUTH_MOVE_SPEED = SynchedEntityData.defineId(Lurker.class, EntityDataSerializers.FLOAT);
	private static final int MOUTH_OPEN_TICKS = 20;

	private int attackTime;

	private float prevRotationPitchBody;
	private float rotationPitchBody;

	private float prevTailYaw;
	private float tailYaw;

	private float prevTailPitch;
	private float tailPitch;

	private float prevMouthOpenTicks;
	private float mouthOpenTicks;

	private int ticksUntilBiteDamage = -1;

	@Nullable
	private Entity entityBeingBit;

	private static final UniformInt PERSISTENT_ANGER_TIME = TimeUtil.rangeOfSeconds(10, 14);
	private int remainingPersistentAngerTime;
	@Nullable
	private UUID persistentAngerTarget;
	public int huntingTimer;

	private boolean prevInWater;

	private int leapRiseTime;
	private int leapFallTime;

	public Lurker(EntityType<? extends PathfinderMob> type, Level level) {
		super(type, level);
		this.xpReward = 5;
		this.setPathfindingMalus(PathType.WATER, 0.0F);
		this.moveControl = new LurkerMoveControl(this);
	}

	@Override
	protected PathNavigation createNavigation(Level level) {
		return new AmphibiousPathNavigation(this, level);
	}

	@Override
	protected void registerGoals() {
		this.goalSelector.addGoal(0, new PanicGoal(this, 1.25D) {
			@Override
			public boolean canUse() {
				return super.canUse() && Lurker.this.level().getDifficulty() == Difficulty.PEACEFUL;
			}
		});
		this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.0D, false));
		this.goalSelector.addGoal(2, new MoveTowardsRestrictionGoal(this, 0.8D));
		this.goalSelector.addGoal(3, new RandomStrollGoal(this, 0.7D, 80));
		this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 6.0F));
		this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));
		this.goalSelector.addGoal(6, new LurkerFindBaitGoal(this, 8.0D));
		this.targetSelector.addGoal(0, new HurtByTargetGoal(this).setAlertOthers());
		this.targetSelector.addGoal(1, new NearestSmellyAttackableTargetGoal<>(this, Player.class, false));
		this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Dragonfly.class, true));
//		this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Angler.class, true));
		this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Anadia.class, true) {
			@Override
			public boolean canUse() {
				return super.canUse() && Lurker.this.huntingTimer <= 0;
			}
		});

		this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Jellyfish.class, true) {
			@Override
			public boolean canUse() {
				return super.canUse() && Lurker.this.huntingTimer <= 0;
			}
		});

//		this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, FreshwaterUrchin.class, true) {
//			@Override
//			public boolean canUse() {
//				return super.canUse() && Lurker.this.huntingTimer <= 0;
//			}
//		});
		this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, 10, true, false, this::isAngryAt));
		this.targetSelector.addGoal(3, new ResetUniversalAngerTargetGoal<>(this, true));
	}

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder) {
		super.defineSynchedData(builder);
		builder.define(IS_LEAPING, false);
		builder.define(SHOULD_MOUTH_BE_OPEN, false);
		builder.define(MOUTH_MOVE_SPEED, 1.0F);
	}

	public static AttributeSupplier.Builder registerAttributes() {
		return WaterAnimal.createMobAttributes()
			.add(Attributes.MAX_HEALTH, 55.0D)
			.add(Attributes.MOVEMENT_SPEED, 0.25D)
			.add(Attributes.ATTACK_DAMAGE, 5.5D)
			.add(Attributes.FOLLOW_RANGE, 16.0D)
			.add(Attributes.KNOCKBACK_RESISTANCE, 1.0D)
			.add(Attributes.STEP_HEIGHT, 1.0D);
	}

	private BlockState getRelativeBlock(int offsetY) {
		return this.level().getBlockState(BlockPos.containing(this.getBlockX(), Mth.floor(this.getBoundingBox().minY) + offsetY, this.getBlockZ()));
	}

	@Override
	public void aiStep() {
		super.aiStep();
		if (this.isInWater()) {
			if (!this.level().isClientSide()) {
				if (this.getDeltaMovement().y() < 0 && this.isLeaping()) {
					this.setIsLeaping(false);
				}
			}
		} else {
			if (this.level().isClientSide()) {
				if (this.prevInWater && this.isLeaping()) {
					this.breachWater();
				}
			} else {
				if (this.onGround()) {
					this.setIsLeaping(false);
				} else {
					this.setDeltaMovement(this.getDeltaMovement().multiply(0.4D, 0.98D, 0.4D));
				}
			}
		}
		if (this.isLeaping()) {
			this.leapRiseTime++;
			if (!this.level().isClientSide()) {
				this.setYRot(this.getYRot() + 10);
			}
		} else {
			if (this.leapRiseTime > 0 && this.leapFallTime == this.leapRiseTime) {
				this.leapFallTime = this.leapRiseTime = 0;
			}
			if (this.leapFallTime < this.leapRiseTime) {
				this.leapFallTime++;
			}
		}
		float magnitude = Mth.sqrt((float) (this.getDeltaMovement().x() * this.getDeltaMovement().x() + this.getDeltaMovement().z() * this.getDeltaMovement().z())) * (this.onGround() ? 0 : 1);
		float motionPitch = Mth.clamp((float) Math.atan2(magnitude, this.getDeltaMovement().y()) / Mth.PI * 180 - 90, -10.0F, 10.0F);
		if (magnitude > 1) {
			magnitude = 1;
		}
		float newRotationPitch = isLeaping() ? 90 : this.leapFallTime > 0 ? -45 : Mth.clamp((this.rotationPitchBody - motionPitch) * magnitude * 4 * (this.isInWater() ? 1 : 0), -30.0F, 30.0F);
		this.tailPitch += (this.rotationPitchBody - newRotationPitch) * 0.75F;
		this.rotationPitchBody += (newRotationPitch - this.rotationPitchBody) * 0.3F;
		if (Math.abs(this.rotationPitchBody) < 0.05F) {
			this.rotationPitchBody = 0;
		}
	}

	private void breachWater() {
		int ring = 2;
		int waterColorMultiplier = this.getWaterColor();
		while (ring-- > 0) {
			int particleCount = ring * 12 + 20 + this.getRandom().nextInt(10);
			for (int p = 0; p < particleCount; p++) {
				float theta = p / (float) particleCount * Mth.TWO_PI;
				float dx = Mth.cos(theta);
				float dz = Mth.sin(theta);
				double x = this.getX() + dx * ring * 1 * MathUtils.linearTransformd(this.getRandom().nextDouble(), 0, 1, 0.6, 1.2) + this.getRandom().nextDouble() * 0.3 - 0.15;
				double y = this.getY() - this.getRandom().nextDouble() * 0.2;
				double z = this.getZ() + dz * ring * 1 * MathUtils.linearTransformd(this.getRandom().nextDouble(), 0, 1, 0.6, 1.2) + this.getRandom().nextDouble() * 0.3 - 0.15;
				double motionX = dx * MathUtils.linearTransformf(this.getRandom().nextFloat(), 0, 1, 0.03F, 0.2F);
				double motionY = ring * 0.3F + this.getRandom().nextDouble() * 0.1;
				double motionZ = dz * MathUtils.linearTransformf(this.getRandom().nextFloat(), 0, 1, 0.03F, 0.2F);
				TheBetweenlands.createParticle(ParticleTypes.SPLASH, this.level(), x, y, z, ParticleFactory.ParticleArgs.get().withMotion(motionX, motionY, motionZ).withColor(waterColorMultiplier));
			}
		}
	}

	private int getWaterColor() {
		int y = 0;
		while (this.getRelativeBlock(y--).isAir() && this.getY() - y > 0) ;
		int blockY = Mth.floor(this.getBoundingBox().minY + y);
		BlockState blockState = this.level().getBlockState(this.blockPosition());
		if (blockState.liquid()) {
			int r = 255, g = 255, b = 255;
			// TODO: automatically build a map of all liquid blocks to the average color of there texture to get color from
			if (blockState.is(BlockRegistry.SWAMP_WATER)) {
				r = 147;
				g = 132;
				b = 83;
			} else if (blockState.is(Blocks.WATER)) {
				r = 49;
				g = 70;
				b = 245;
			} else if (blockState.is(Blocks.LAVA)) {
				r = 207;
				g = 85;
				b = 16;
			}
			int multiplier = blockState.getMapColor(this.level(), BlockPos.containing(this.getBlockX(), blockY, this.getBlockZ())).calculateRGBColor(MapColor.Brightness.NORMAL);
			return 0xFF000000 | (r * (multiplier >> 16 & 0xFF) / 255) << 16 | (g * (multiplier >> 8 & 0xFF) / 255) << 8 | (b * (multiplier & 0xFF) / 255);
		}
		return 0xFFFFFFFF;
	}

	@Override
	public void tick() {
		if (!this.level().isClientSide()) {
			LivingEntity target = this.getTarget();
			if (target instanceof Dragonfly && this.attackTime <= 0 && target.distanceTo(this) < 3.2D && target.getBoundingBox().maxY >= this.getBoundingBox().minY && target.getBoundingBox().minY <= this.getBoundingBox().maxY && this.ticksUntilBiteDamage == -1) {
				this.setShouldMouthBeOpen(true);
				this.setMouthMoveSpeed(10);
				this.ticksUntilBiteDamage = 10;
				this.attackTime = 20;
				this.entityBeingBit = target;

				if (this.isLeaping() && target instanceof Dragonfly) {
					target.startRiding(this, true);
					this.setTarget(target);
				}
			}
		}

		this.prevRotationPitchBody = this.rotationPitchBody;
		this.prevTailPitch = this.tailPitch;
		this.prevTailYaw = this.tailYaw;
		while (this.rotationPitchBody - this.prevRotationPitchBody < -180) {
			this.prevRotationPitchBody -= 360;
		}
		while (this.rotationPitchBody - this.prevRotationPitchBody >= 180) {
			this.prevRotationPitchBody += 360;
		}
		while (this.tailPitch - this.prevTailPitch < -180) {
			this.prevTailPitch -= 360;
		}
		while (this.tailPitch - this.prevTailPitch >= 180) {
			this.prevTailPitch += 360;
		}
		while (this.tailYaw - this.prevTailYaw < -180) {
			this.prevTailYaw -= 360;
		}
		while (this.tailYaw - this.prevTailYaw >= 180) {
			this.prevTailYaw += 360;
		}
		this.prevMouthOpenTicks = this.mouthOpenTicks;
		this.prevInWater = this.isInWater();

		super.tick();

		if (this.shouldMouthBeOpen()) {
			if (this.mouthOpenTicks < MOUTH_OPEN_TICKS) {
				this.mouthOpenTicks += this.getMouthMoveSpeed();
			}
			if (this.mouthOpenTicks > MOUTH_OPEN_TICKS) {
				this.mouthOpenTicks = MOUTH_OPEN_TICKS;
			}
		} else {
			if (this.mouthOpenTicks > 0) {
				this.mouthOpenTicks -= this.getMouthMoveSpeed();
			}
			if (this.mouthOpenTicks < 0) {
				this.mouthOpenTicks = 0;
			}
		}
		if (this.ticksUntilBiteDamage > -1) {
			this.ticksUntilBiteDamage--;
			if (this.ticksUntilBiteDamage == -1) {
				this.setShouldMouthBeOpen(false);
				if (this.entityBeingBit != null) {
					if (this.entityBeingBit.isAlive()) {
						this.doHurtTarget(this.entityBeingBit);
						if (this.getFirstPassenger() == this.entityBeingBit) {
							this.getFirstPassenger().hurt(this.damageSources().mobAttack(this), ((LivingEntity) this.entityBeingBit).getMaxHealth());
						}
					}
					this.entityBeingBit = null;
				}
			}
		}
		float movementSpeed = Mth.sqrt((float) ((this.xo - this.getX()) * (this.xo - this.getX()) + (this.yo - this.getY()) * (this.yo - this.getY()) + (this.zo - this.getZ()) * (this.zo - this.getZ())));
		if (movementSpeed > 1) {
			movementSpeed = 1;
		} else if (movementSpeed < 0.08) {
			movementSpeed = 0;
		}
		if (Math.abs(this.tailYaw) < 90) {
			this.tailYaw += (this.yBodyRotO - this.yBodyRot);
		}
		if (Math.abs(this.tailPitch) < 90) {
			this.tailPitch += (this.prevRotationPitchBody - this.rotationPitchBody);
		}
		this.tailPitch *= 0.5F;
		this.tailYaw *= (1 - movementSpeed);

		if(!this.level().isClientSide())
			if(this.huntingTimer > 0)
				this.huntingTimer--;
	}

	@Override
	protected Vec3 getPassengerAttachmentPoint(Entity entity, EntityDimensions dimensions, float partialTick) {
		return new Vec3(0.0F, this.getEyeHeight(), 0.75F);
	}

	@Override
	public void travel(Vec3 travelVector) {
		if (this.isControlledByLocalInstance() && this.isInWater()) {
			this.moveRelative(this.getSpeed(), travelVector);
			this.move(MoverType.SELF, this.getDeltaMovement());
			this.setDeltaMovement(this.getDeltaMovement().scale(0.9));
			if (this.getTarget() == null) {
				this.setDeltaMovement(this.getDeltaMovement().add(0.0, -0.005, 0.0));
			}
		} else {
			super.travel(travelVector);
		}
	}

	@Override
	protected void customServerAiStep() {
		super.customServerAiStep();
		this.attackTime--;
	}

	@Override
	public boolean doHurtTarget(Entity entity) {
		double distance = entity.distanceToSqr(this);
		if (this.entityBeingBit != null || this.getVehicle() != null || entity.getVehicle() != null) {
			return false;
		}
		if (this.isInWater() && entity instanceof Dragonfly && !this.isLeaping() && distance < 5) {
			this.setIsLeaping(true);
			double distanceX = entity.getX() - this.getX();
			double distanceZ = entity.getZ() - this.getZ();
			float magnitude = Mth.sqrt((float) (distanceX * distanceX + distanceZ * distanceZ));
			this.setDeltaMovement(this.getDeltaMovement().add(distanceX / magnitude * 0.8D, 0.9D, distanceZ / magnitude * 0.8D));
		}

		if (this.attackTime <= 0 && distance < 3.5D && entity.getBoundingBox().maxY >= this.getBoundingBox().minY && entity.getBoundingBox().minY <= this.getBoundingBox().maxY && this.ticksUntilBiteDamage == -1) {
			this.setShouldMouthBeOpen(true);
			this.setMouthMoveSpeed(10);
			this.ticksUntilBiteDamage = 10;
			this.attackTime = 10;
			this.entityBeingBit = entity;
		}
		return true;
	}

	@Override
	public AABB getAttackBoundingBox() {
		return super.getAttackBoundingBox().inflate(0.0D, 3.0D, 0.0D).move(0.0D, 1.5D, 0.0D);
	}

	@Override
	public void addAdditionalSaveData(CompoundTag compound) {
		super.addAdditionalSaveData(compound);
		this.addPersistentAngerSaveData(compound);
	}

	@Override
	public void readAdditionalSaveData(CompoundTag compound) {
		super.readAdditionalSaveData(compound);
		this.readPersistentAngerSaveData(this.level(), compound);
	}

	@Override
	public boolean hurt(DamageSource source, float damage) {
		if (this.isInvulnerableTo(source) || source.is(DamageTypes.IN_WALL)) {
			return false;
		}

		return super.hurt(source, damage);
	}

	public boolean isLeaping() {
		return this.getEntityData().get(IS_LEAPING);
	}

	public void setIsLeaping(boolean isLeaping) {
		this.getEntityData().set(IS_LEAPING, isLeaping);
	}

	public boolean shouldMouthBeOpen() {
		return this.getEntityData().get(SHOULD_MOUTH_BE_OPEN);
	}

	public void setShouldMouthBeOpen(boolean shouldMouthBeOpen) {
		this.getEntityData().set(SHOULD_MOUTH_BE_OPEN, shouldMouthBeOpen);
	}

	public float getMouthMoveSpeed() {
		return this.getEntityData().get(MOUTH_MOVE_SPEED);
	}

	public void setMouthMoveSpeed(float mouthMoveSpeed) {
		this.getEntityData().set(MOUTH_MOVE_SPEED, mouthMoveSpeed);
	}

	public void setHuntingTimer(int cooldownIn) {
		this.huntingTimer = cooldownIn;
	}

	public float getRotationPitch(float partialTick) {
		return this.rotationPitchBody * partialTick + this.prevRotationPitchBody * (1 - partialTick);
	}

	public float getMouthOpen(float partialTick) {
		return (this.mouthOpenTicks * partialTick + this.prevMouthOpenTicks * (1 - partialTick)) / MOUTH_OPEN_TICKS;
	}

	public float getTailYaw(float partialTick) {
		return this.tailYaw * partialTick + this.prevTailYaw * (1 - partialTick);
	}

	public float getTailPitch(float partialTick) {
		return this.tailPitch * partialTick + this.prevTailPitch * (1 - partialTick);
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return SoundRegistry.LURKER_LIVING.get();
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource damageSource) {
		this.setShouldMouthBeOpen(true);
		this.ticksUntilBiteDamage = 10;
		return SoundRegistry.LURKER_HURT.get();
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundRegistry.LURKER_DEATH.get();
	}

	@Override
	public boolean isPushedByFluid(FluidType type) {
		return false;
	}

	@Override
	public void setRemainingPersistentAngerTime(int time) {
		this.remainingPersistentAngerTime = time;
	}

	@Override
	public int getRemainingPersistentAngerTime() {
		return this.remainingPersistentAngerTime;
	}


	@Override
	public @Nullable UUID getPersistentAngerTarget() {
		return this.persistentAngerTarget;
	}

	@Override
	public void setPersistentAngerTarget(@Nullable UUID persistentAngerTarget) {
		this.persistentAngerTarget = persistentAngerTarget;
	}

	@Override
	public void startPersistentAngerTimer() {
		this.setRemainingPersistentAngerTime(PERSISTENT_ANGER_TIME.sample(this.random));
	}
}
