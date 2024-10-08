package thebetweenlands.common.entity.monster;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.EventHooks;
import thebetweenlands.api.entity.ScreenShaker;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.datagen.tags.BLBlockTagProvider;
import thebetweenlands.common.entity.BLEntity;
import thebetweenlands.common.entity.ai.goals.ApproachItemGoal;
import thebetweenlands.common.entity.ai.goals.PeatMummyChargeGoal;
import thebetweenlands.common.registries.EntityRegistry;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.registries.SoundRegistry;

public class PeatMummy extends Monster implements BLEntity, ScreenShaker {

	private static final int BREAK_COUNT = 5;

	private static final EntityDataAccessor<Integer> SPAWNING_TICKS = SynchedEntityData.defineId(PeatMummy.class, EntityDataSerializers.INT);

	private int chargingPreparation;
	private static final EntityDataAccessor<Byte> CHARGING_STATE = SynchedEntityData.defineId(PeatMummy.class, EntityDataSerializers.BYTE);

	//Scream timer is only used for the screen shake and is client side only.
	private int prevScreamTimer;
	private int screamTimer;
	private boolean screaming = false;

	//Adjust to length of screaming sound
	private static final int SCREAMING_TIMER_MAX = 50;

	private float prevSpawningOffset = 0.0F;
	private float prevSpawningProgress = 0.0F;

	private List<Goal> activeTargetTasks;
	private List<Goal> inactiveTargetTasks;

	private boolean carryingShimmerstone;
	private boolean isMinion;

	private static final AttributeModifier CHARGING_SPEED_MODIFIER = new AttributeModifier(TheBetweenlands.prefix("charging_speed"), 0.35D, AttributeModifier.Operation.ADD_VALUE);
	private static final AttributeModifier CHARGING_DAMAGE_MODIFIER = new AttributeModifier(TheBetweenlands.prefix("charging_damage"), 1.65D, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);

	public PeatMummy(EntityType<? extends Monster> type, Level level) {
		super(type, level);
		this.xpReward = 12;
		this.setSpawningTicks(0);
	}

	@Override
	public void registerGoals() {
		this.goalSelector.addGoal(0, new FloatGoal(this));
		this.goalSelector.addGoal(1, new ApproachItemGoal(this, ItemRegistry.SHIMMER_STONE.get(), 80, 64, 1.9F, 1.5F) {
			@Override
			protected double getNearSpeed() {
				if (PeatMummy.this.isCharging()) {
					return 1.0F;
				} else {
					return super.getNearSpeed();
				}
			}

			@Override
			protected double getFarSpeed() {
				if (PeatMummy.this.isCharging()) {
					return 1.0F;
				} else {
					return super.getFarSpeed();
				}
			}

			@Override
			protected void onPickup() {
				PeatMummy entity = PeatMummy.this;
				if (entity.isCharging()) {
					entity.stopCharging();
				}
				entity.setCarryShimmerstone(true);
			}
		});
		this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.0D, true));
		this.goalSelector.addGoal(3, new PeatMummyChargeGoal(this));
		this.goalSelector.addGoal(4, new MoveTowardsRestrictionGoal(this, 1.0D));
		this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 16.0F));
		this.goalSelector.addGoal(6, new WaterAvoidingRandomStrollGoal(this, 1D));
		this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));

		this.activeTargetTasks = new ArrayList<>();
		this.activeTargetTasks.add(new HurtByTargetGoal(this));
		this.activeTargetTasks.add(new NearestAttackableTargetGoal<>(this, Player.class, true));

		this.inactiveTargetTasks = new ArrayList<>();
		this.inactiveTargetTasks.add(new NearestAttackableTargetGoal<>(this, Player.class, false) {
			@Override
			protected double getFollowDistance() {
				return PeatMummy.this.getSpawningRange();
			}
		});
	}

	public static AttributeSupplier.Builder registerAttributes() {
		return Monster.createMonsterAttributes()
			.add(Attributes.MAX_HEALTH, 110.0D)
			.add(Attributes.MOVEMENT_SPEED, 0.2D)
			.add(Attributes.ATTACK_DAMAGE, 6.0D)
			.add(Attributes.FOLLOW_RANGE, 40.0D)
			.add(Attributes.KNOCKBACK_RESISTANCE, 1.0D);
	}

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder) {
		super.defineSynchedData(builder);
		builder.define(SPAWNING_TICKS, 0);
		builder.define(CHARGING_STATE, (byte) 0);
	}

	@Override
	protected boolean isImmobile() {
		return super.isImmobile() || this.getChargingState() == 1;
	}

	@Override
	public void addAdditionalSaveData(CompoundTag tag) {
		tag.putInt("spawning_ticks", this.getSpawningTicks());
		tag.putInt("charging_preparation", this.chargingPreparation);
		tag.putByte("charging_state", this.getEntityData().get(CHARGING_STATE));
		tag.putBoolean("holding_shimmerstone", this.carryingShimmerstone);
		tag.putBoolean("boss_summoned", this.isMinion);

		super.addAdditionalSaveData(tag);
	}

	@Override
	public void readAdditionalSaveData(CompoundTag tag) {
		this.setSpawningTicks(tag.getInt("spawning_ticks"));
		this.chargingPreparation = tag.getInt("charging_preparation");
		this.getEntityData().set(CHARGING_STATE, tag.getByte("charging_state"));
		this.carryingShimmerstone = tag.getBoolean("holding_shimmerstone");
		this.isMinion = tag.getBoolean("boss_summoned");
		super.readAdditionalSaveData(tag);
	}

	@Override
	public void tick() {
		super.tick();

		this.prevSpawningOffset = this.getSpawningOffset();
		this.prevSpawningProgress = this.getSpawningProgress();

		if (!this.level().isClientSide()) {
			if (this.shouldUpdateSpawningAnimation()) {
				if (this.getSpawningTicks() == 0) {
					this.playSound(SoundRegistry.PEAT_MUMMY_EMERGE.get(), 1.2F, 1.0F);
				}
				this.updateSpawningTicks();
			} else if (this.getSpawningTicks() > 0) {
				this.setSpawningFinished();
			}

			if (this.isInValidSpawn() && !this.isSpawningFinished()) {
				this.setDeltaMovement(Vec3.ZERO);
				this.hurtMarked = true;

				int breakPoint = this.getSpawningLength() / BREAK_COUNT;
				if ((this.getSpawningTicks() - breakPoint / 2 - 1) % breakPoint == 0) {
					BlockPos pos = this.blockPosition().below();
					BlockState blockState = this.level().getBlockState(pos);
					this.playSound(blockState.getSoundType(this.level(), pos, this).getBreakSound(), this.getRandom().nextFloat() * 0.3F + 0.3F, this.getRandom().nextFloat() * 0.15F + 0.7F);
				}

				if (this.getTarget() != null) {
					this.lookAt(this.getTarget(), 360, 360);
				}

				if (this.getSpawningTicks() == this.getSpawningLength() - 1) {
					this.setPos(this.position());
				}
			} else {
				this.setSpawningFinished();
			}
		} else {
			if (this.getSpawningProgress() != 0.0F && this.getSpawningProgress() != 1.0F) {
				int breakPoint = getSpawningLength() / BREAK_COUNT;
				if ((getSpawningTicks() - breakPoint / 2 - 1) % breakPoint == 0) {
					BlockPos pos = this.blockPosition().below();
					BlockState state = this.level().getBlockState(pos);
					double px = this.getX() + this.getRandom().nextDouble() - 0.5F;
					double py = this.getY() + this.getRandom().nextDouble() * 0.2 + 0.075;
					double pz = this.getZ() + this.getRandom().nextDouble() - 0.5F;
					for (int i = 0, amount = this.getRandom().nextInt(20) + 15; i < amount; i++) {
						double ox = this.getRandom().nextDouble() * 0.1F - 0.05F;
						double oz = this.getRandom().nextDouble() * 0.1F - 0.05F;
						double motionX = this.getRandom().nextDouble() * 0.2 - 0.1;
						double motionY = this.getRandom().nextDouble() * 0.25 + 0.1;
						double motionZ = this.getRandom().nextDouble() * 0.2 - 0.1;
						this.level().addParticle(new BlockParticleOption(ParticleTypes.BLOCK, state), px + ox, py, pz + oz, motionX, motionY, motionZ);
					}
				}
			}
		}

		if (this.isSpawningFinished()) {
			this.prevScreamTimer = this.screamTimer;
			if (this.isPreparing() && this.screamTimer == 0) {
				this.screaming = true;
				this.screamTimer = 1;
			}
			if (this.screamTimer > 0) {
				this.screamTimer++;
			}
			this.screaming = this.screamTimer < SCREAMING_TIMER_MAX && this.isPreparing();
			if (!this.isPreparing()) {
				this.screamTimer = 0;
			}

			if (!this.level().isClientSide()) {
				if (this.isPreparing()) {
					this.chargingPreparation++;
					if (this.getPreparationProgress() == 1.0F) {
						this.chargingPreparation = 0;
						this.setChargingState(2);
					}
				}

				if (this.isCharging()) {
					this.getAttribute(Attributes.ATTACK_DAMAGE).addOrUpdateTransientModifier(CHARGING_DAMAGE_MODIFIER);
					this.getAttribute(Attributes.MOVEMENT_SPEED).addOrUpdateTransientModifier(CHARGING_SPEED_MODIFIER);
				} else {
					this.getAttribute(Attributes.ATTACK_DAMAGE).removeModifier(CHARGING_DAMAGE_MODIFIER);
					this.getAttribute(Attributes.MOVEMENT_SPEED).removeModifier(CHARGING_SPEED_MODIFIER);
				}
			}
		}
	}

	/**
	 * Returns whether the spawning animation should be started
	 */
	public boolean shouldUpdateSpawningAnimation() {
		return this.getTarget() != null;
	}

	/**
	 * Returns whether the ground below the peat mummy is suitable for spawning
	 */
	public boolean isInValidSpawn() {
		int ebx = Mth.floor(this.getX());
		int eby = Mth.floor(this.getY());
		int ebz = Mth.floor(this.getZ());
		return this.inMud(ebx, eby, ebz);
	}

	private boolean inMud(int ebx, int eby, int ebz) {
		BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
		for (int y = -Mth.ceil(this.getMaxSpawnOffset()); y < 0; y++) {
			for (int x = -1; x <= 1; x++) {
				for (int z = -1; z <= 1; z++) {
					BlockState blockState = this.level().getBlockState(pos.set(ebx + x, eby + y, ebz + z));
					if (!(y == -1 ? (blockState.is(BLBlockTagProvider.PEAT_MUMMY_SPAWNABLE)) : (Block.isFaceFull(blockState.getCollisionShape(this.level(), pos.immutable()), Direction.UP) || blockState.is(BLBlockTagProvider.PEAT_MUMMY_SPAWNABLE)))) {
						return false;
					}
				}
			}
		}
		return true;
	}

	@Override
	public void push(Entity entity) {
		if (this.isSpawningFinished()) {
			super.push(entity);
		}
	}

	@Override
	public boolean isPushable() {
		return super.isPushable() && this.isSpawningFinished() && this.getChargingState() == 0;
	}

	@Override
	public int getMaxSpawnClusterSize() {
		return 2;
	}

	@Override
	public boolean hurt(DamageSource source, float amount) {
		return !source.is(DamageTypes.IN_WALL) && super.hurt(source, amount);
	}

	@Override
	public boolean doHurtTarget(Entity entity) {
		if (this.isCharging()) {
			this.stopCharging();
		}
		if (this.isSpawningFinished()) {
			return super.doHurtTarget(entity);
		}
		return false;
	}

	@Override
	public void playAmbientSound() {
		if (this.isSpawningFinished()) {
			super.playAmbientSound();
		}
	}

	/**
	 * Starts the charging progress
	 */
	public void startCharging() {
		this.playSound(SoundRegistry.PEAT_MUMMY_CHARGE.get(), 1.75F, (this.getRandom().nextFloat() * 0.4F + 0.8F) * 0.8F);
		this.setChargingState(1);
	}

	/**
	 * Stops the charging progress
	 */
	public void stopCharging() {
		this.setChargingState(0);
		this.chargingPreparation = 0;
	}

	/**
	 * Returns the spawning offset
	 */
	public float getSpawningOffset() {
		return (float) ((-this.getMaxSpawnOffset() + this.getSpawningProgress() * this.getMaxSpawnOffset()));
	}

	/**
	 * Returns the interpolated spawning offset
	 */
	public float getInterpolatedSpawningOffset(float partialTicks) {
		return this.prevSpawningOffset + (this.getSpawningOffset() - this.prevSpawningOffset) * partialTicks;
	}

	/**
	 * Returns the maximum spawning offset
	 */
	public double getMaxSpawnOffset() {
		return 2.0D;
	}

	/**
	 * Sets the spawning ticks
	 */
	public void setSpawningTicks(int ticks) {
		this.getEntityData().set(SPAWNING_TICKS, ticks);

		if (!this.level().isClientSide()) {
			if (this.isSpawningFinished()) {
				for (Goal task : this.inactiveTargetTasks) {
					this.targetSelector.removeGoal(task);
				}
				for (int i = 0; i < this.activeTargetTasks.size(); i++) {
					this.targetSelector.addGoal(i, this.activeTargetTasks.get(i));
				}
			} else {
				for (Goal task : this.activeTargetTasks) {
					this.targetSelector.removeGoal(task);
				}
				for (int i = 0; i < this.inactiveTargetTasks.size(); i++) {
					this.targetSelector.addGoal(i, this.inactiveTargetTasks.get(i));
				}
			}
		}
	}

	/**
	 * Returns the spawning ticks
	 */
	public int getSpawningTicks() {
		return this.getEntityData().get(SPAWNING_TICKS);
	}

	/**
	 * Returns the spawning animation duration
	 */
	public int getSpawningLength() {
		return 100;
	}

	/**
	 * Returns the range at which a buried peat mummy detects a player
	 */
	public double getSpawningRange() {
		return 8.0D;
	}

	/**
	 * Returns the relative spawning progress
	 */
	public float getSpawningProgress() {
		if (this.getSpawningLength() == 0) {
			return 1.0F;
		}
		return 1.0F / this.getSpawningLength() * this.getSpawningTicks();
	}

	/**
	 * Returns the interpolated relative spawning progress
	 */
	public float getInterpolatedSpawningProgress(float partialTicks) {
		return this.prevSpawningProgress + (this.getSpawningProgress() - this.prevSpawningProgress) * partialTicks;
	}

	/**
	 * Increments the spawning ticks
	 */
	public void updateSpawningTicks() {
		int spawningTicks = this.getSpawningTicks();
		if (spawningTicks < this.getSpawningLength()) {
			this.setSpawningTicks(spawningTicks + 1);
		}
	}

	/**
	 * Sets the spawning ticks to finished
	 */
	public void setSpawningFinished() {
		if (this.isSpawningFinished())
			return;

		this.setSpawningTicks(this.getSpawningLength());
	}

	/**
	 * Returns whether the spawning progress has finished
	 */
	public boolean isSpawningFinished() {
		return this.getSpawningTicks() == this.getSpawningLength();
	}

	/**
	 * Returns the maximum charge cooldown in ticks
	 */
	public int getMaxChargingCooldown() {
		return 160;
	}

	/**
	 * Sets the charging state:
	 * - 0: Not charging
	 * - 1: Preparing
	 * - 2: Charging
	 */
	public void setChargingState(int state) {
		this.getEntityData().set(CHARGING_STATE, (byte) state);
	}

	/**
	 * Returns the charging state
	 */
	public byte getChargingState() {
		return this.getEntityData().get(CHARGING_STATE);
	}

	/**
	 * Returns whether this entity is preparing for a charge attack
	 */
	public boolean isPreparing() {
		return this.getChargingState() == 1;
	}

	/**
	 * Returns whether this entity is charging
	 */
	public boolean isCharging() {
		return this.getChargingState() == 2;
	}

	/**
	 * Returns the relative charging preparation progress
	 */
	public float getPreparationProgress() {
		return 1.0F / 60.0F * this.chargingPreparation;
	}

	/**
	 * Returns whether the mummy is screaming
	 */
	public boolean isScreaming() {
		return this.screaming;
	}

	/**
	 * Returns the relative screaming progress
	 */
	public float getScreamingProgress() {
		return 1.0F / SCREAMING_TIMER_MAX * (this.prevScreamTimer + (this.screamTimer - this.prevScreamTimer));
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return SoundRegistry.PEAT_MUMMY_LIVING.get();
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return SoundRegistry.PEAT_MUMMY_HURT.get();
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundRegistry.PEAT_MUMMY_DEATH.get();
	}

	/**
	 * Sets whether the peat mummy is carrying a shimmer stone
	 */
	public void setCarryShimmerstone(boolean shimmerStone) {
		this.carryingShimmerstone = shimmerStone;
	}

	/**
	 * Returns whether the Peat Mummy is holding a Shimmerstone
	 */
	public boolean doesCarryShimmerstone() {
		return this.carryingShimmerstone;
	}

	/**
	 * Sets whether the peat mummy was spawned by a Dreadful Peat Mummy
	 */
	public void setBossMummy(boolean boss) {
		this.isMinion = boss;
	}

	/**
	 * Returns whether the Peat Mummy was spawned by a Dreadful Peat Mummy
	 */
	public boolean isBossMummy() {
		return this.isMinion;
	}

	@Override
	public AABB getBoundingBoxForCulling() {
		return this.getSpawningTicks() > 0 ? this.getBoundingBox() : new AABB(0, 0, 0, 0, 0, 0);
	}

	@Override
	public float getShakeIntensity(Entity viewer) {
		if (this.isScreaming()) {
			float dist = this.distanceTo(viewer);
			float screamMult = 1.0F - dist / 30.0F;
			if (dist >= 30.0F) {
				return 0.0F;
			}
			return (Mth.sin(this.getScreamingProgress() * Mth.PI) + 0.1F) * 0.15F * screamMult;
		} else {
			return 0.0F;
		}
	}

	@Override
	public float getWalkTargetValue(BlockPos pos) {
		return 0.5F;
	}

	@Nullable
	@Override
	public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType spawnType, @Nullable SpawnGroupData spawnGroupData) {
		spawnGroupData = super.finalizeSpawn(level, difficulty, spawnType, spawnGroupData);
		if (!level.isClientSide() && level.getRandom().nextInt(20) == 0) {
			SwampHag hag = new SwampHag(EntityRegistry.SWAMP_HAG.get(), this.level());
			hag.moveTo(this.getX(), this.getY(), this.getZ(), this.getYRot(), 0.0F);
			EventHooks.finalizeMobSpawn(hag, level, difficulty, spawnType, null);
			this.level().addFreshEntity(hag);
			hag.startRiding(this);
		}
		return spawnGroupData;
	}
}