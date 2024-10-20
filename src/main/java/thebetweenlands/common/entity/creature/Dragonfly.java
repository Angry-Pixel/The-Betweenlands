package thebetweenlands.common.entity.creature;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ambient.AmbientCreature;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import thebetweenlands.common.entity.BLEntity;
import thebetweenlands.common.registries.SoundRegistry;

public class Dragonfly extends AmbientCreature implements BLEntity {

	@Nullable
	private BlockPos currentFlightTarget;
	private boolean entityFlying;
	@Nullable
	protected BlockPos spawnPos;

	public Dragonfly(EntityType<? extends AmbientCreature> type, Level level) {
		super(type, level);
		this.xpReward = 3;
	}

	@Override
	protected void registerGoals() {
		this.goalSelector.addGoal(0, new FloatGoal(this));
		this.goalSelector.addGoal(1, new LookAtPlayerGoal(this, Player.class, 6.0F));
		this.goalSelector.addGoal(2, new RandomLookAroundGoal(this));
	}

	public static AttributeSupplier.Builder registerAttributes() {
		return Mob.createMobAttributes()
			.add(Attributes.MAX_HEALTH, 10.0D)
			.add(Attributes.MOVEMENT_SPEED, 0.9D);
	}

	@Override
	public int getMaxSpawnClusterSize() {
		return 2;
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return SoundRegistry.DRAGONFLY.get();
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return SoundRegistry.CRUNCH.get();
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundRegistry.SQUISH.get();
	}

	@Override
	protected void playStepSound(BlockPos pos, BlockState state) {
		playSound(SoundEvents.SPIDER_STEP, 0.15F, 1.0F);
	}

	public boolean isFlying() {
		return !this.onGround();
	}

	public void setEntityFlying(boolean state) {
		entityFlying = state;
	}

	@Override
	public void tick() {
		if (this.spawnPos == null) {
			this.spawnPos = this.blockPosition();
		}

		if (this.getDeltaMovement().y() < 0.0D) {
			this.setDeltaMovement(this.getDeltaMovement().multiply(1.0D, 0.6D, 1.0D));
		}
		if (!this.level().isClientSide()) {
			if (this.getRandom().nextInt(200) == 0) {
				this.setEntityFlying(!this.entityFlying);
			}
			if (this.entityFlying) {
				this.flyAbout();
			} else {
				this.land();
			}
			if (!this.entityFlying) {
				if (this.isInWater()) {
					this.setDeltaMovement(this.getDeltaMovement().add(0.0D, 0.2D, 0.0D));
				}
				if (this.level().containsAnyLiquid(this.getBoundingBox().inflate(0D, 1D, 0D))) {
					this.flyAbout();
				}
				if (this.level().getNearestPlayer(this, 4.0D) != null) {
					this.flyAbout();
				}
			}
		}
		super.tick();
	}

	public void flyAbout() {
		if (this.currentFlightTarget != null) {
			if (!this.level().isAreaLoaded(this.currentFlightTarget, 6) || !this.level().isEmptyBlock(this.currentFlightTarget) || this.currentFlightTarget.getY() < this.level().getMinBuildHeight() || this.level().getBlockState(this.currentFlightTarget.above()).liquid()) {
				this.currentFlightTarget = null;
			}
		}
		if (this.currentFlightTarget == null || this.getRandom().nextInt(30) == 0 || this.currentFlightTarget.distToCenterSqr((int) this.getX(), (int) this.getY(), (int) this.getZ()) < 8F) {
			BlockPos newTarget = BlockPos.containing((int) this.getX() + this.getRandom().nextInt(7) - this.getRandom().nextInt(7), (int) this.getY() + this.getRandom().nextInt(6) - 1, (int) this.getZ() + this.getRandom().nextInt(7) - this.getRandom().nextInt(7));
			if (this.spawnPos != null && this.spawnPos.distToCenterSqr(newTarget.getCenter()) > 32 * 32) {
				newTarget = this.spawnPos.offset(this.getRandom().nextInt(16) - this.getRandom().nextInt(16), this.getRandom().nextInt(10) - 5, this.getRandom().nextInt(16) - this.getRandom().nextInt(16));
			}
			if (this.level().isAreaLoaded(newTarget, 6)) {
				this.currentFlightTarget = newTarget;
			}
		}
		this.flyToTarget();
	}

	public void flyToTarget() {
		if (this.currentFlightTarget != null) {
			double targetX = this.currentFlightTarget.getX() + 0.5D - this.getX();
			double targetY = this.currentFlightTarget.getY() + 1D - this.getY();
			double targetZ = this.currentFlightTarget.getZ() + 0.5D - this.getZ();
			this.setDeltaMovement(this.getDeltaMovement().add(
				(Math.signum(targetX) * 0.5D - this.getDeltaMovement().x()) * 0.1D,
				(Math.signum(targetY) * 0.7D - this.getDeltaMovement().y()) * 0.1D,
				(Math.signum(targetZ) * 0.5D - this.getDeltaMovement().z()) * 0.1D
			));
			float angle = (float) (Math.atan2(this.getDeltaMovement().z(), this.getDeltaMovement().x()) * 180.0D / Math.PI) - 90.0F;
			float rotation = Mth.wrapDegrees(angle - this.getYRot());
			this.zza = 0.5F;
			this.setYRot(this.getYRot() + rotation);
		}
	}

	private void land() {
		// Nothing to see here - yet
	}

	@Override
	public boolean shouldRender(double x, double y, double z) {
		return !this.isInLurkersMouth() && super.shouldRender(x, y, z);
	}

	@Override
	public boolean isPickable() {
		return !this.isInLurkersMouth();
	}

	@Override
	public boolean canBeSeenByAnyone() {
		return !this.isInLurkersMouth() && super.canBeSeenByAnyone();
	}

	@Override
	public boolean removeWhenFarAway(double distanceToClosestPlayer) {
		return false;
	}

	@Override
	protected void checkFallDamage(double y, boolean onGround, BlockState state, BlockPos pos) {

	}

	@Override
	public boolean hasLineOfSight(Entity entity) {
		return !this.isInLurkersMouth() && super.hasLineOfSight(entity);
	}

	private boolean isInLurkersMouth() {
		return this.getVehicle() instanceof Lurker;
	}

	@Override
	public void remove(RemovalReason reason) {
		if (!this.level().isClientSide() && reason.shouldDestroy()) {
			if (this.getKillCredit() instanceof Lurker lurker) {
				lurker.setHuntingTimer(1200); // 1 minute cooldown
			}
		}
		super.remove(reason);
	}

	@Override
	protected void tickDeath() {
		this.deathTime++;
		if (this.deathTime >= 20 && !this.level().isClientSide() && !this.isRemoved()) {
			if (!this.isInLurkersMouth()) {
				this.level().broadcastEntityEvent(this, (byte) 60);
			}
			this.remove(Entity.RemovalReason.KILLED);
		}
	}

	@Override
	public @Nullable SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType spawnType, @Nullable SpawnGroupData spawnGroupData) {
		this.spawnPos = this.blockPosition();
		return super.finalizeSpawn(level, difficulty, spawnType, spawnGroupData);
	}

	@Override
	public void addAdditionalSaveData(CompoundTag tag) {
		super.addAdditionalSaveData(tag);
		if (this.spawnPos != null) {
			tag.putLong("spawn_pos", this.spawnPos.asLong());
		}
	}

	@Override
	public void readAdditionalSaveData(CompoundTag tag) {
		super.readAdditionalSaveData(tag);
		if (tag.contains("spawn_pos", Tag.TAG_LONG)) {
			this.spawnPos = BlockPos.of(tag.getLong("spawn_pos"));
		}
	}

//	@Override
//	public PullerDragonfly createPuller(Draeton draeton, DraetonPhysicsPart puller) {
//		return new PullerDragonfly(draeton.level(), draeton, puller);
//	}
}
