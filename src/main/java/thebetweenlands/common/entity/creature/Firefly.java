package thebetweenlands.common.entity.creature;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomFlyingGoal;
import net.minecraft.world.entity.ambient.AmbientCreature;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.pathfinder.PathType;
import thebetweenlands.common.entity.BLEntity;
import thebetweenlands.common.entity.ai.goals.ShelterFromRainGoal;
import thebetweenlands.common.entity.movement.FlightMoveHelper;
import thebetweenlands.common.registries.SoundRegistry;

public class Firefly extends PathfinderMob implements BLEntity {

	private static final EntityDataAccessor<Float> GLOW_STRENGTH = SynchedEntityData.defineId(Firefly.class, EntityDataSerializers.FLOAT);

	protected int glowTicks = 0;
	protected int prevGlowTicks = 0;

	public Firefly(EntityType<? extends PathfinderMob> type, Level level) {
		super(type, level);
		this.noCulling = true;
		this.moveControl = new FlightMoveHelper(this);
		this.setPathfindingMalus(PathType.WATER, -1.0F);
		this.setPathfindingMalus(PathType.BLOCKED, -1.0F);
		this.setPathfindingMalus(PathType.OPEN, 0.0F);
	}

	@Override
	public float getWalkTargetValue(BlockPos pos) {
		return this.level().isRainingAt(pos) ? -1.0F : 0.0F;
	}

	@Override
	protected void registerGoals() {
		this.goalSelector.addGoal(0, new ShelterFromRainGoal(this, 0.8D));
		this.goalSelector.addGoal(1, new FloatGoal(this));
		this.goalSelector.addGoal(2, new WaterAvoidingRandomFlyingGoal(this, 0.5D));
	}

	public static AttributeSupplier.Builder registerAttributes() {
		return Mob.createMobAttributes()
			.add(Attributes.MAX_HEALTH, 4.0D)
			.add(Attributes.MOVEMENT_SPEED, 0.035D);
	}

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder) {
		super.defineSynchedData(builder);
		builder.define(GLOW_STRENGTH, 1.0F);
	}

	@Override
	public int getMaxSpawnClusterSize() {
		return 1;
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
	public void tick() {
		super.tick();

		if (Block.isFaceFull(this.level().getBlockState(this.blockPosition().below()).getCollisionShape(this.level(), this.blockPosition().below()), Direction.UP))
			this.getMoveControl().setWantedPosition(this.getX(), this.getY() + 1, this.getZ(), 0.32D);

		this.prevGlowTicks = this.glowTicks;

		if (this.isAlive()) {
			if (!this.level().isClientSide()) {
				if (!this.isGlowActive() && this.getRandom().nextDouble() < 0.0025D) {
					this.setGlowStrength(1.0F);
				} else if (this.isGlowActive() && this.getRandom().nextDouble() < 0.00083D) {
					this.setGlowStrength(0);
				}
			}

			if (this.isGlowActive() && this.glowTicks < 20) {
				this.glowTicks++;
			} else if (!this.isGlowActive() && this.glowTicks > 0) {
				this.glowTicks--;
			}
		} else {
			this.setGlowStrength(0);
			this.glowTicks = 0;
		}
	}

	@Override
	public void addAdditionalSaveData(CompoundTag tag) {
		super.addAdditionalSaveData(tag);
		tag.putDouble("glow_strength", this.getGlowStrength());
	}

	@Override
	public void readAdditionalSaveData(CompoundTag tag) {
		super.readAdditionalSaveData(tag);
		this.setGlowStrength(tag.getDouble("glow_strength"));
	}

	public boolean isGlowActive() {
		return this.getGlowStrength() > 0;
	}

	public double getGlowStrength() {
		return this.getEntityData().get(GLOW_STRENGTH);
	}

	public void setGlowStrength(double strength) {
		this.getEntityData().set(GLOW_STRENGTH, (float) strength);
	}

	public float getGlowTicks(float partialTicks) {
		return this.prevGlowTicks + (this.glowTicks - this.prevGlowTicks) * partialTicks;
	}

//	@Override
//	public PullerFirefly createPuller(Draeton draeton, DraetonPhysicsPart puller) {
//		return new PullerFirefly(draeton.level(), draeton, puller);
//	}
}
