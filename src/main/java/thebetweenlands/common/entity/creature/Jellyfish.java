package thebetweenlands.common.entity.creature;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.MoveTowardsRestrictionGoal;
import net.minecraft.world.entity.ai.goal.RandomSwimmingGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.NeoForgeMod;
import org.jetbrains.annotations.Nullable;
import thebetweenlands.common.entity.BLEntity;
import thebetweenlands.common.entity.movement.JellyfishMoveControl;
import thebetweenlands.common.registries.FluidTypeRegistry;
import thebetweenlands.common.registries.SoundRegistry;

import java.util.List;

public class Jellyfish extends WaterAnimal implements BLEntity {

	protected Vec3 prevOrientationPos = Vec3.ZERO;
	protected Vec3 orientationPos = Vec3.ZERO;

	private static final EntityDataAccessor<Float> JELLYFISH_SIZE = SynchedEntityData.defineId(Jellyfish.class, EntityDataSerializers.FLOAT);
	private static final EntityDataAccessor<Byte> JELLYFISH_COLOR = SynchedEntityData.defineId(Jellyfish.class, EntityDataSerializers.BYTE);

	protected float lengthScale = 1.0F;

	public Jellyfish(EntityType<? extends WaterAnimal> type, Level level) {
		super(type, level);
		this.moveControl = new JellyfishMoveControl(this);
		this.setPathfindingMalus(PathType.WATER, 0.0F);
	}

	@Override
	protected void registerGoals() {
		this.goalSelector.addGoal(0, new MoveTowardsRestrictionGoal(this, 0.4D));
		this.goalSelector.addGoal(1, new RandomSwimmingGoal(this, 0.5D, 20));
	}

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder) {
		super.defineSynchedData(builder);
		builder.define(JELLYFISH_COLOR, (byte) 0);
		builder.define(JELLYFISH_SIZE, 0.5F);
	}

	public static AttributeSupplier.Builder registerAttributes() {
		return WaterAnimal.createMobAttributes()
			.add(Attributes.MAX_HEALTH, 5.0D)
			.add(Attributes.MOVEMENT_SPEED, 1.0D)
			.add(Attributes.ATTACK_DAMAGE, 2.0D)
			.add(Attributes.FOLLOW_RANGE, 12.0D);
	}

	@Override
	public boolean isInWater() {
		return this.isInFluidType(NeoForgeMod.WATER_TYPE.value()) || this.isInFluidType(FluidTypeRegistry.SWAMP_WATER.get());
	}

	@Override
	public void travel(Vec3 travelVector) {
		if (this.isEffectiveAi() && this.isInWater()) {
			this.moveRelative(0.01F, travelVector);
			this.move(MoverType.SELF, this.getDeltaMovement());
			this.setDeltaMovement(this.getDeltaMovement().scale(0.9));
			if (this.getTarget() == null) {
				this.setDeltaMovement(this.getDeltaMovement().add(0.0, -0.005, 0.0));
			}
		} else {
			super.travel(travelVector);
		}
	}

	@Nullable
	@Override
	public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType spawnType, @Nullable SpawnGroupData spawnGroupData) {
		this.setJellyfishSize(0.5F + this.getRandom().nextFloat() * 0.75F);
		this.setJellyfishLength(0.5f + this.getRandom().nextFloat() * 0.5f);
		this.setJellyfishColor((byte) this.getRandom().nextInt(5));
		return super.finalizeSpawn(level, difficulty, spawnType, spawnGroupData);
	}

	public float getJellyfishLength() {
		return this.lengthScale;
	}

	private void setJellyfishLength(float length) {
		this.lengthScale = length;
	}

	public byte getJellyfishColor() {
		return this.getEntityData().get(JELLYFISH_COLOR);
	}

	public void setJellyfishColor(byte color) {
		this.getEntityData().set(JELLYFISH_COLOR, color);
	}

	public float getJellyfishSize() {
		return this.getEntityData().get(JELLYFISH_SIZE);
	}

	private void setJellyfishSize(float size) {
		this.getEntityData().set(JELLYFISH_SIZE, size);
		this.refreshDimensions();
	}

	@Override
	protected EntityDimensions getDefaultDimensions(Pose pose) {
		return super.getDefaultDimensions(pose).scale(0.8F + this.getJellyfishSize() * 0.5F, 0.8F + this.getJellyfishSize()).withEyeHeight((0.8F + this.getJellyfishSize() * 0.5F) * 0.5F);
	}

	@Override
	public void addAdditionalSaveData(CompoundTag tag) {
		super.addAdditionalSaveData(tag);
		tag.putFloat("size", this.getJellyfishSize());
		tag.putFloat("length", this.getJellyfishLength());
		tag.putByte("color", this.getJellyfishColor());
	}

	@Override
	public void readAdditionalSaveData(CompoundTag tag) {
		super.readAdditionalSaveData(tag);
		this.setJellyfishSize(tag.getFloat("size"));
		this.setJellyfishLength(tag.getFloat("length"));
		this.setJellyfishColor(tag.getByte("color"));
	}

	@Override
	public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
		if (JELLYFISH_SIZE.equals(key)) {
			this.refreshDimensions();
			this.setYRot(this.getYHeadRot());
			this.setYBodyRot(this.getYHeadRot());
		}
		super.onSyncedDataUpdated(key);
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return SoundRegistry.JELLYFISH_HURT.get();
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundRegistry.JELLYFISH_DEATH.get();
	}

	@Override
	protected SoundEvent getSwimSound() {
		return SoundRegistry.JELLYFISH_SWIM.get();
	}

	@Override
	public void playSound(SoundEvent sound, float volume, float pitch) {
		super.playSound(sound, sound == this.getSwimSound() ? this.getSoundVolume() : volume, pitch);
	}

	@Override
	protected float getSoundVolume() {
		return 0.35F;
	}

	@Override
	protected PathNavigation createNavigation(Level level) {
		return new WaterBoundPathNavigation(this, level);
	}

	@Override
	public float getWalkTargetValue(BlockPos pos) {
		return this.level().getBlockState(pos).liquid() ? 10.0F + this.level().getMaxLocalRawBrightness(pos) - 0.5F : super.getWalkTargetValue(pos);
	}

	@Override
	public void aiStep() {
		this.xRotO = this.getXRot();
		float speedAngle = Mth.sqrt((float) (this.getDeltaMovement().x() * this.getDeltaMovement().x() + this.getDeltaMovement().z() * this.getDeltaMovement().z()));
		if (this.getDeltaMovement().x() != 0D && this.getDeltaMovement().z() != 0D)
			this.setXRot(this.getXRot() + (-((float) Mth.atan2(speedAngle, this.getDeltaMovement().y())) * Mth.RAD_TO_DEG - this.getXRot()) * 0.1F);

		super.aiStep();
	}

	@Override
	public void tick() {
		super.tick();

		if (this.level().isClientSide()) {
			this.updateOrientationPos();
		} else if (this.level().getCurrentDifficultyAt(this.blockPosition()).getDifficulty() != Difficulty.PEACEFUL) {
			AABB stingArea = this.getBoundingBox().inflate(0.25D);

			List<LivingEntity> entities = this.level().getEntitiesOfClass(LivingEntity.class, stingArea, e -> !(e instanceof BLEntity));

			for (LivingEntity entity : entities) {
				if (entity.tickCount % 10 == 0)
					entity.hurt(this.damageSources().noAggroMobAttack(this), (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE));
			}
		}
	}

	@Override
	protected boolean shouldDespawnInPeaceful() {
		return true;
	}

	@Override
	public boolean isPushable() {
		return false;
	}

	@Override
	public void push(Entity entity) {
		//No pushing
	}

	@Override
	public boolean hurt(DamageSource source, float amount) {
		if (source.getEntity() instanceof Jellyfish) {
			return false;
		}

		return super.hurt(source, amount);
	}

	@Override
	public void remove(RemovalReason reason) {
		if (!this.level().isClientSide() && reason.shouldDestroy()) {
//			if (this.getKillCredit() instanceof EntityLurker lurker) {
//				lurker.setHuntingTimer(1200); // 1 minute cooldown
//			}
		}
		super.remove(reason);
	}

	protected void updateOrientationPos() {
		final double length = 0.5D;

		Vec3 tether = this.position();

		this.prevOrientationPos = this.orientationPos;

		this.orientationPos = this.orientationPos.add(0, -0.01f, 0);

		if (this.orientationPos.distanceTo(tether) > length) {
			this.orientationPos = tether.add(this.orientationPos.subtract(tether).normalize().scale(length));
		}
	}

	public Vec3 getOrientationPos(float partialTicks) {
		return this.prevOrientationPos.add(this.orientationPos.subtract(this.prevOrientationPos).scale(partialTicks));
	}
}
