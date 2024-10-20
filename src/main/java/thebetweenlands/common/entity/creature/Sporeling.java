package thebetweenlands.common.entity.creature;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.EventHooks;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import thebetweenlands.common.entity.BLEntity;
import thebetweenlands.common.entity.ai.goals.FollowTargetGoal;
import thebetweenlands.common.entity.ai.goals.JumpRandomlyGoal;
import thebetweenlands.common.entity.monster.Splodeshroom;
import thebetweenlands.common.registries.EntityRegistry;
import thebetweenlands.common.registries.EnvironmentEventRegistry;
import thebetweenlands.common.registries.SoundRegistry;
import thebetweenlands.common.world.storage.BetweenlandsWorldStorage;

public class Sporeling extends PathfinderMob implements BLEntity {

	private float jumpHeightOverride = -1;

	protected static final EntityDataAccessor<Boolean> IS_FALLING = SynchedEntityData.defineId(Sporeling.class, EntityDataSerializers.BOOLEAN);

	protected float prevFloatingRotationTicks = 0;
	protected float floatingRotationTicks = 0;

	private AvoidEntityGoal<LivingEntity> aiRunAway;
	private FollowTargetGoal<Player> moveToTarget;
	private boolean canFollow = false;

	public Sporeling(EntityType<? extends PathfinderMob> entityType, Level level) {
		super(entityType, level);
		this.xpReward = 1;

		this.setPathfindingMalus(PathType.WATER, -1.0F);
	}

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder) {
		super.defineSynchedData(builder);
		builder.define(IS_FALLING, false);
	}

	@Override
	protected void registerGoals() {
		this.aiRunAway = new AvoidEntityGoal<>(this, LivingEntity.class, entity -> entity instanceof Enemy || (entity instanceof Player player && !player.isCreative()), 10.0F, 0.5D, 1.0D, entity -> true);
		this.moveToTarget = new FollowTargetGoal<>(this, new FollowTargetGoal.FollowClosest<>(this, Player.class, 16), 1.0D, 0.5F, 16.0F, false);

		this.goalSelector.addGoal(0, new FloatGoal(this));
		this.goalSelector.addGoal(1, new PanicGoal(this, 1.0D));
		this.goalSelector.addGoal(2, aiRunAway);
		this.goalSelector.addGoal(4, new FollowTargetGoal<>(this, new FollowTargetGoal.FollowClosest<>(this, RootSprite.class, 10), 0.65D, 0.5F, 10.0F, false));
		this.goalSelector.addGoal(5, new JumpRandomlyGoal(this, 10, () -> !Sporeling.this.level().getEntitiesOfClass(RootSprite.class, this.getBoundingBox().inflate(1)).isEmpty()) {
			@Override
			public void start() {
				Sporeling.this.setJumpHeightOverride(0.2F);
				super.start();
			}
		});
		this.goalSelector.addGoal(6, new WaterAvoidingRandomStrollGoal(this, 0.6D));
		this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 6.0F));
		this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
	}

	public static AttributeSupplier.Builder registerAttributes() {
		return Mob.createMobAttributes()
			.add(Attributes.MAX_HEALTH, 5.0D)
			.add(Attributes.MOVEMENT_SPEED, 0.49D)
			.add(Attributes.STEP_HEIGHT, 1.0D);
	}

	@Override
	public void aiStep() {
		if (this.level().isClientSide()) {
			this.level().addParticle(new DustParticleOptions(new Vector3f(0.5F + this.getRandom().nextFloat() * 0.5F, 0.5F + this.getRandom().nextFloat() * 0.5F, 0.5F + this.getRandom().nextFloat() * 0.5F), 1.0F),
				this.getX() + (this.getRandom().nextDouble() - 0.5D) * this.getBbWidth(),
				this.getY() + this.getRandom().nextDouble() * this.getBbHeight() - 0.25D,
				this.getZ() + (this.getRandom().nextDouble() - 0.5D) * this.getBbWidth(),
				0.0D, 0.0D, 0.0D);
		}
		super.aiStep();
	}

	@Override
	public void tick() {
		if (!this.level().isClientSide()) {
			if (!this.isInWater()) {
				boolean canSpin = (this.getVehicle() != null ? !this.getVehicle().onGround() : !this.onGround()) && this.stuckSpeedMultiplier.equals(Vec3.ZERO) && !this.isInWater() && !this.isInLava() && this.level().isEmptyBlock(this.blockPosition().below());
				if (canSpin && (this.getDeltaMovement().y() < 0D || (this.getVehicle() != null && this.getVehicle().getDeltaMovement().y() < 0))) {
					if (!this.getIsFalling())
						this.setIsFalling(true);
					this.setDeltaMovement(this.getDeltaMovement().multiply(1.0D, 0.7D, 1.0D));
					if (this.getVehicle() != null) {
						this.getVehicle().setDeltaMovement(this.getVehicle().getDeltaMovement().multiply(1.0D, 0.7D, 1.0D));
						this.getVehicle().fallDistance = 0;
					}
				} else {
					if (!canSpin && this.getIsFalling()) {
						this.setIsFalling(false);
					}
				}
			}

			if (this.isVehicle() && this.getFirstPassenger() instanceof Splodeshroom && !this.canFollow) {
				this.goalSelector.removeGoal(this.aiRunAway);
				this.goalSelector.addGoal(3, this.moveToTarget);
				this.canFollow = true;
			}

			if (!this.isVehicle() && this.canFollow) {
				this.goalSelector.addGoal(2, this.aiRunAway);
				this.goalSelector.removeGoal(this.moveToTarget);
				this.canFollow = false;
			}
		}

		this.prevFloatingRotationTicks = this.floatingRotationTicks;
		if (this.getIsFalling()) {
			this.floatingRotationTicks += 30;
			float wrap = Mth.wrapDegrees(this.floatingRotationTicks) - this.floatingRotationTicks;
			this.floatingRotationTicks += wrap;
			this.prevFloatingRotationTicks += wrap;
		} else {
			this.floatingRotationTicks = 0;
		}
		super.tick();
	}

	public float smoothedAngle(float partialTicks) {
		return this.prevFloatingRotationTicks + (this.floatingRotationTicks - this.prevFloatingRotationTicks) * partialTicks;
	}

	private float updateRotation(float angle, float targetAngle, float maxIncrease) {
		float f = Mth.wrapDegrees(targetAngle - angle);

		if (f > maxIncrease) {
			f = maxIncrease;
		}

		if (f < -maxIncrease) {
			f = -maxIncrease;
		}

		return angle + f;
	}

	private void setIsFalling(boolean state) {
		this.getEntityData().set(IS_FALLING, state);
	}

	public boolean getIsFalling() {
		return this.getEntityData().get(IS_FALLING);
	}

	@Override
	public int getMaxSpawnClusterSize() {
		return 1;
	}

	@Override
	protected void checkFallDamage(double y, boolean onGround, BlockState state, BlockPos pos) {

	}

	@Override
	protected SoundEvent getAmbientSound() {
		return SoundRegistry.SPORELING_LIVING.get();
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return SoundRegistry.SPORELING_HURT.get();
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundRegistry.SPORELING_DEATH.get();
	}

	@Override
	public boolean removeWhenFarAway(double distanceToClosestPlayer) {
		return false;
	}

	public void setJumpHeightOverride(float jumpHeightOverride) {
		this.jumpHeightOverride = jumpHeightOverride;
	}

	@Override
	protected float getJumpPower() {
		if (this.jumpHeightOverride > 0) {
			float height = this.jumpHeightOverride;
			this.jumpHeightOverride = -1;
			return height;
		}
		return super.getJumpPower();
	}

	@Override
	public @Nullable SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType spawnType, @Nullable SpawnGroupData spawnGroupData) {
		if (this.isAlive() && this.isBloodSkiesActive()) {
			Splodeshroom shroom = new Splodeshroom(EntityRegistry.SPLODESHROOM.get(), this.level());
			shroom.moveTo(this.getX(), this.getY(), this.getZ(), level.getRandom().nextFloat() * 360.0F, 0.0F);
			if (!level.containsAnyLiquid(shroom.getBoundingBox()) && level.noCollision(shroom)) {
				EventHooks.finalizeMobSpawn(shroom, level, difficulty, spawnType, null);
				level.addFreshEntity(shroom);
				shroom.startRiding(this);
			}
		}
		return super.finalizeSpawn(level, difficulty, spawnType, spawnGroupData);
	}

	public boolean isBloodSkiesActive() {
		BetweenlandsWorldStorage worldStorage = BetweenlandsWorldStorage.get(this.level());
		return worldStorage != null && EnvironmentEventRegistry.BLOOD_SKY.get().isActive();
	}
}
