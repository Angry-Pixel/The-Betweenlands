package thebetweenlands.common.entity.creature.frog;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.navigation.AmphibiousPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.fluids.FluidType;
import org.jetbrains.annotations.Nullable;
import thebetweenlands.api.BLRegistries;
import thebetweenlands.common.entity.BLEntity;
import thebetweenlands.common.registries.EntityDataSerializerRegistry;
import thebetweenlands.common.registries.FrogVariantRegistry;
import thebetweenlands.common.registries.SoundRegistry;

import java.util.Optional;

public class Frog extends PathfinderMob implements BLEntity, VariantHolder<Holder<FrogVariant>> {

	private static final EntityDataAccessor<Byte> DW_SWIM_STROKE = SynchedEntityData.defineId(Frog.class, EntityDataSerializers.BYTE);
	private static final EntityDataAccessor<Holder<FrogVariant>> VARIANT = SynchedEntityData.defineId(Frog.class, EntityDataSerializerRegistry.FROG_VARIANT.get());

	public int jumpAnimationTicks;
	public int prevJumpAnimationTicks;
	private int ticksOnGround = 0;
	private int strokeTicks = 0;

	public Frog(EntityType<? extends PathfinderMob> type, Level level) {
		super(type, level);
		this.setPathfindingMalus(PathType.WATER, 0.0F);
		this.xpReward = 3;
	}

	@Override
	protected PathNavigation createNavigation(Level level) {
		return new AmphibiousPathNavigation(this, level);
	}

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder) {
		super.defineSynchedData(builder);
		builder.define(DW_SWIM_STROKE, (byte) 0);
		RegistryAccess registryaccess = this.registryAccess();
		Registry<FrogVariant> registry = registryaccess.registryOrThrow(BLRegistries.Keys.FROG_VARIANT);
		builder.define(VARIANT, registry.getHolder(FrogVariantRegistry.GREEN).or(registry::getAny).orElseThrow());
	}

	@Override
	protected void registerGoals() {
		this.goalSelector.addGoal(0, new PanicGoal(this, 1.0D));
		this.goalSelector.addGoal(1, new RandomStrollGoal(this, 1.0D, 40));
		this.goalSelector.addGoal(2, new LookAtPlayerGoal(this, Player.class, 6.0F));
		this.goalSelector.addGoal(3, new RandomLookAroundGoal(this));
	}

	public static AttributeSupplier.Builder registerAttributes() {
		return Mob.createMobAttributes()
			.add(Attributes.MAX_HEALTH, 3.0D)
			.add(Attributes.MOVEMENT_SPEED, 0.05D)
			.add(Attributes.STEP_HEIGHT, 1.0D);
	}

	@Override
	public void tick() {
		this.prevJumpAnimationTicks = this.jumpAnimationTicks;
		super.tick();
		if (this.onGround() || (this.strokeTicks == 0 && this.isInWater())) {
			this.ticksOnGround++;
			if (this.jumpAnimationTicks > 0)
				this.jumpAnimationTicks = 0;
		} else {
			this.ticksOnGround = 0;
			this.jumpAnimationTicks++;
		}
		if (this.strokeTicks > 0)
			this.strokeTicks--;
		if (!this.level().isClientSide()) {
			if (this.strokeTicks > 0) {
				this.strokeTicks--;
				this.getEntityData().set(DW_SWIM_STROKE, (byte) 1);
			} else {
				this.getEntityData().set(DW_SWIM_STROKE, (byte) 0);
			}
		} else {
			if (this.getEntityData().get(DW_SWIM_STROKE) == 1) {
				if (this.strokeTicks < 20)
					this.strokeTicks++;
			} else {
				this.strokeTicks = 0;
			}
		}
		if (!this.level().isClientSide()) {
			this.setAirSupply(20);

			Path path = this.getNavigation().getPath();
			if (path != null && !path.isDone() && (this.onGround() || this.isInWater()) && !this.isImmobile()) {
				int index = path.getNextNodeIndex();
				if (index < path.getNodeCount()) {
					Node nextHopSpot = path.getNode(index);
					float x = (float) (nextHopSpot.x - this.getX());
					float z = (float) (nextHopSpot.z - this.getZ());
					float angle = (float) (Math.atan2(z, x));
					float distance = (float) Math.sqrt(x * x + z * z);
					double speedMultiplier = Math.min(distance / 2.0D, 1);
					if (distance > 0.5D) {
						if (!this.isInWater()) {
							if (this.ticksOnGround > 5) {
								this.setDeltaMovement(this.getDeltaMovement().add(0.4D * Mth.cos(angle) * speedMultiplier, 0.4D, 0.4D * Mth.sin(angle) * speedMultiplier));
								this.hurtMarked = true;
							}
						} else {
							if (this.strokeTicks == 0) {
								this.setDeltaMovement(this.getDeltaMovement().add(0.45D * Mth.cos(angle) * speedMultiplier, (nextHopSpot.y < this.getY() ? -0.2D : 0.2D) * speedMultiplier, 0.45D * Mth.sin(angle) * speedMultiplier));
								this.hurtMarked = true;
								this.strokeTicks = 40;
								this.level().broadcastEntityEvent(this, (byte) 8);
							} else if (this.horizontalCollision) {
								this.setDeltaMovement(this.getDeltaMovement().add(0.01D * Mth.cos(angle), 0.0D, 0.01D * Mth.sin(angle)));
							}
						}
					} else {
						path.advance();
					}
				}
			}

			if (!this.level().isClientSide()) {
				if (this.getDeltaMovement().y() < 0.0F && this.level().getBlockState(BlockPos.containing(this.getX(), this.getY() + 0.4D, this.getZ())).liquid()) {
					this.setDeltaMovement(this.getDeltaMovement().multiply(1.0D, 0.1D, 1.0D));
					this.hurtMarked = true;
				}

				if ((path == null || path.isDone()) && this.level().getBlockState(BlockPos.containing(this.getX(), this.getY() + 0.5D, this.getZ())).liquid()) {
					this.setDeltaMovement(this.getDeltaMovement().add(0.0D, 0.04D, 0.0D));
					this.hurtMarked = true;
				}
			}
		}

		if (this.level().isClientSide() && this.getVariant().value().particle().isPresent() && this.level().getGameTime() % 10 == 0) {
			this.level().addParticle(this.getVariant().value().particle().get(), this.getX(), this.getY() + 0.5D, this.getZ(), 0.0D, 0.0D, 0.0D);
		}
	}

	@Override
	public void travel(Vec3 travelVector) {
		super.travel(Vec3.ZERO);
	}

	@Override
	public void playerTouch(Player player) {
		super.playerTouch(player);
		if (this.getVariant().value().touchEffect().isPresent()) {
			if (!this.level().isClientSide() && !player.isCreative()) {
				player.addEffect(this.getVariant().value().touchEffect().get());
			}
		}
	}

	@Override
	public void addAdditionalSaveData(CompoundTag compound) {
		super.addAdditionalSaveData(compound);
		this.getVariant().unwrapKey().ifPresent(key -> compound.putString("variant", key.location().toString()));
	}

	@Override
	public void readAdditionalSaveData(CompoundTag compound) {
		super.readAdditionalSaveData(compound);
		Optional.ofNullable(ResourceLocation.tryParse(compound.getString("variant")))
			.map(location -> ResourceKey.create(BLRegistries.Keys.FROG_VARIANT, location))
			.flatMap(key -> this.registryAccess().holder(key)).ifPresent(this::setVariant);
	}

	@Override
	public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType spawnType, @Nullable SpawnGroupData spawnGroupData) {
		this.setVariant(FrogVariantRegistry.getSpawnVariant(this.registryAccess(), this.getRandom(), level.getBiome(this.blockPosition())));
		return super.finalizeSpawn(level, difficulty, spawnType, spawnGroupData);
	}

	@Override
	public Holder<FrogVariant> getVariant() {
		return this.getEntityData().get(VARIANT);
	}

	@Override
	public void setVariant(Holder<FrogVariant> variant) {
		this.getEntityData().set(VARIANT, variant);
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return SoundRegistry.FROG_LIVING.get();
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return SoundRegistry.FROG_HURT.get();
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundRegistry.FROG_DEATH.get();
	}

	@Override
	public void handleEntityEvent(byte id) {
		super.handleEntityEvent(id);
		if (id == 8) {
			this.strokeTicks = 0;
		}
	}

	@Override
	public boolean isPushedByFluid(FluidType type) {
		return false;
	}

	@Override
	public boolean removeWhenFarAway(double distanceToClosestPlayer) {
		return false;
	}
}
