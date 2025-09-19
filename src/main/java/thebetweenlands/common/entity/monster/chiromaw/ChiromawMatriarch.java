package thebetweenlands.common.entity.monster.chiromaw;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.GlobalPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import thebetweenlands.api.entity.NonDismountable;
import thebetweenlands.client.model.ControlledAnimation;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.entity.ai.goals.*;
import thebetweenlands.common.entity.monster.FlyingMonster;
import thebetweenlands.common.registries.SoundRegistry;

import javax.annotation.Nullable;
import java.util.List;

public class ChiromawMatriarch extends FlyingMonster implements NonDismountable {

	private static final EntityDataAccessor<Boolean> IS_NESTING = SynchedEntityData.defineId(ChiromawMatriarch.class, EntityDataSerializers.BOOLEAN);
	private static final EntityDataAccessor<Boolean> RETURN_TO_NEST = SynchedEntityData.defineId(ChiromawMatriarch.class, EntityDataSerializers.BOOLEAN);
	private static final EntityDataAccessor<Boolean> IS_LANDING = SynchedEntityData.defineId(ChiromawMatriarch.class, EntityDataSerializers.BOOLEAN);
	private static final EntityDataAccessor<Boolean> IS_SPINNING = SynchedEntityData.defineId(ChiromawMatriarch.class, EntityDataSerializers.BOOLEAN);
	private static final EntityDataAccessor<Integer> TARGET_ENTITY_ID = SynchedEntityData.defineId(ChiromawMatriarch.class, EntityDataSerializers.INT);

	public int broodCount;
	public double pickupHeight;
	public int droppingTimer; // makes sure player is always dropped
	@Nullable
	private GlobalPos boundOrigin;
	public float previousSpinAngle, spinAngle;
	public float animTime, prevAnimTime;
	public float flapSpeed = 0.5f;
	public int flapTicks;
	public int landingAbortTime;
	public boolean returnFast; //whether matriarch should return to nest fast, e.g. when player is near it

	public final ControlledAnimation landingTimer = new ControlledAnimation(10);
	public final ControlledAnimation nestingTimer = new ControlledAnimation(20);
	public final ControlledAnimation spinningTimer = new ControlledAnimation(10);

	public ChiromawMatriarch(EntityType<? extends Monster> type, Level level) {
		super(type, level);
		this.setNesting(false);
		this.xpReward = 150;
		this.setPathfindingMalus(PathType.WATER, -8.0F);
		this.setPathfindingMalus(PathType.BLOCKED, -8.0F);
		this.setPathfindingMalus(PathType.OPEN, 8.0F);
		this.setPathfindingMalus(PathType.FENCE, -8.0F);
	}

	public static AttributeSupplier.Builder registerAttributes() {
		return Mob.createMobAttributes()
			.add(Attributes.MOVEMENT_SPEED, 0.095D)
			.add(Attributes.FLYING_SPEED, 0.095D)
			.add(Attributes.MAX_HEALTH, 200.0D)
			.add(Attributes.ATTACK_DAMAGE, 5.0D)
			.add(Attributes.FOLLOW_RANGE, 64.0D);
	}

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder) {
		super.defineSynchedData(builder);
		builder.define(IS_NESTING, false);
		builder.define(RETURN_TO_NEST, false);
		builder.define(IS_LANDING, false);
		builder.define(IS_SPINNING, false);
		builder.define(TARGET_ENTITY_ID, -1);
	}

	@Override
	protected void registerGoals() {
		this.goalSelector.addGoal(0, new FloatGoal(this));
		this.goalSelector.addGoal(1, new MatriarchGrabAndReleaseTargetGoal(this, 2.0D, true));
		this.goalSelector.addGoal(2, new MatriarchReturnToNestGoal(this, 1.25D, 2.15D));
		this.goalSelector.addGoal(3, new MatriarchWanderGoal(this));
		this.goalSelector.addGoal(4, new MatriarchChangeNestGoal(this));
		this.goalSelector.addGoal(5, new MatriarchPoopGoal(this));
		this.targetSelector.addGoal(1, new MatriarchTargetGoal<>(this, Player.class, true, 16D).setUnseenMemoryTicks(160));
		this.targetSelector.addGoal(1, new MatriarchTargetGoal<>(this, Villager.class, true, 16D).setUnseenMemoryTicks(160));
		this.targetSelector.addGoal(1, new MatriarchTargetGoal<>(this, ChiromawGreeblingRider.class, true, 16D).setUnseenMemoryTicks(160));
	}

	@Override
	public void tick() {
		super.tick();

		if (!this.level().isClientSide() && this.getTarget() == null) {
			if (this.getBroodCount() <= 0) {
				this.setReturnToNest(!this.isReturningToNest());
				this.setBroodCount(240);
			}
			if (this.getBroodCount() > 0)
				this.setBroodCount(this.getBroodCount() - 1);
		}

		if (!this.level().isClientSide() && this.getTarget() != null) {
			if (this.isNesting())
				this.setNesting(false);
			if (this.isReturningToNest())
				this.setReturnToNest(false);
			if (this.getBroodCount() < 240)
				this.setBroodCount(240);
		}

		if (!this.level().isClientSide()) {
			Entity target = this.getTarget();
			this.getEntityData().set(TARGET_ENTITY_ID, target != null ? target.getId() : -1);
		}

		if (this.jumping && this.isInWater()) {
			this.getMoveControl().setWantedPosition(this.getX(), this.getY() + 1, this.getZ(), 1.0D);
		}

		if (!this.level().isClientSide() && this.tickCount % 20 == 0 && this.getBoundOrigin() != null) {
			this.setBoundOrigin(GlobalPos.of(this.level().dimension(), this.level().getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, this.getBoundOrigin().pos())));
		}

		if (!this.level().isClientSide() && this.isLanding()) {
			if (this.landingAbortTime > 0)
				this.landingAbortTime--;
			if (this.landingAbortTime <= 0) {
				if (this.isLanding())
					this.setLanding(false);
				if (this.isReturningToNest())
					this.setReturnToNest(false);
				if (this.getBroodCount() < 240)
					this.setBroodCount(240);
			}
		}
		if (this.getBroodCount() > 0 && this.getTarget() == null && this.isReturningToNest() && !this.isNesting()) {
			if (this.getNestBox() != null && this.getBoundingBox().intersects(this.getNestBox())) {
				double d0 = this.getBoundOrigin().pos().getX() + 0.5D - this.getX();
				double d1 = this.getBoundOrigin().pos().getY() - this.getY();
				double d2 = this.getBoundOrigin().pos().getZ() + 0.5D - this.getZ();
				this.setDeltaMovement(this.getDeltaMovement().add(
					(Math.signum(d0) - this.getDeltaMovement().x()) * 0.0000000003D,
					(Math.signum(d1) - this.getDeltaMovement().y()) * 0.03125D,
					(Math.signum(d2) - this.getDeltaMovement().z()) * 0.0000000003D
				));

				if (this.getBoundingBox().minY > this.getNestBox().minY + 0.0625D && this.getDeltaMovement().length() < 0.1D) {
					if (!this.level().isClientSide()) {
						if (!this.isLanding()) {
							this.setLanding(true);
							this.landingAbortTime = 40;
						}
					}
				}
				if (this.getBoundingBox().minY <= this.getNestBox().minY + 0.0625D) {
					if (!this.level().isClientSide()) {
						if (this.isLanding()) {
							this.setLanding(false);
							this.playSound(SoundRegistry.CHIROMAW_MATRIARCH_LAND.get(), 0.5F, this.getVoicePitch());
						}
						if (!this.isNesting())
							this.setNesting(true);
						this.setPosRaw(this.getBoundOrigin().pos().getX() + 0.5D, this.getBoundOrigin().pos().getY(), this.getBoundOrigin().pos().getZ() + 0.5D);
					}
				}
			}
		}

		if (this.level().getBlockState(this.blockPosition().below()).isFaceSturdy(this.level(), this.blockPosition().below(), Direction.UP)) {
			if (!this.isLanding() && !this.isNesting())
				this.getMoveControl().setWantedPosition(this.getX(), this.getY() + 2, this.getZ(), 1.0D);
		}

		if (this.isSpinning()) {
			if (this.level().isClientSide()) {
				this.previousSpinAngle = this.spinAngle;
				this.spinAngle += 30;
				this.spinAngle %= 360;
			}

			this.setDeltaMovement(this.getDeltaMovement().add(0.0D, 0.05D, 0.0D));
		}

		if (this.level().isClientSide()) {
			this.nestingTimer.updateTimer();
			this.landingTimer.updateTimer();
			this.spinningTimer.updateTimer();

			if (this.isNesting()) {
				this.nestingTimer.increaseTimer();
				this.landingTimer.decreaseTimer();
				this.spinningTimer.decreaseTimer();
			} else if (this.isLanding()) {
				this.landingTimer.increaseTimer();
				this.nestingTimer.decreaseTimer();
				this.spinningTimer.decreaseTimer();
			} else if (this.isSpinning()) {
				this.spinningTimer.increaseTimer();
				this.landingTimer.decreaseTimer();
				this.nestingTimer.decreaseTimer();
			} else {
				this.landingTimer.decreaseTimer();
				this.nestingTimer.decreaseTimer();
				this.spinningTimer.decreaseTimer();
			}
		}
	}

	@Nullable
	@Override
	public LivingEntity getTarget() {
		if (this.level().isClientSide()) {
			int targetId = this.getEntityData().get(TARGET_ENTITY_ID);
			if (targetId >= 0) {
				Entity target = this.level().getEntity(targetId);
				if (target instanceof LivingEntity living) {
					return living;
				}
			}
		}
		return super.getTarget();
	}

	@Override
	public void push(double x, double y, double z) {
		if (this.isNesting()) {
			this.setDeltaMovement(this.getDeltaMovement().add(x, 0.0D, z));
			this.hasImpulse = true;
		} else {
			super.push(x, y, z);
		}
	}

	//moved outside of AI because it may be useful
	public boolean homeisOccupied() {
		if (this.getNestBox() != null) {
			List<ChiromawMatriarch> entityList = this.level().getEntitiesOfClass(ChiromawMatriarch.class, this.getNestBox(), matriarch -> matriarch != this);
			return !entityList.isEmpty();
		}
		return false;
	}

	@Override
	public void aiStep() {
		super.aiStep();
		if (this.level().isClientSide()) {
			this.flapTicks++;

			if (!this.isSilent() && !this.isNesting()) {
				float flapAngle1 = Mth.cos(this.flapTicks * this.flapSpeed);
				float flapAngle2 = Mth.cos((this.flapTicks + 1) * this.flapSpeed);
				if (flapAngle1 <= 0.3f && flapAngle2 > 0.3f) {
					if (this.isLanding()) {
						this.playSound(this.getFlySound(), 0.25F, 1.5F + this.getRandom().nextFloat() * 0.3F);
					} else {
						this.playSound(this.getFlySound(), 0.5F, 0.8F + this.getRandom().nextFloat() * 0.3F);
					}
				}
			}

			this.prevAnimTime = this.animTime;
			float flaptimer = 0.079F;
			if (this.isLanding())
				this.animTime += flaptimer * 2F;
			if (this.isNesting())
				this.animTime = 0;
			else
				this.animTime += flaptimer;

			if (this.isNoAi())
				this.animTime = 0.5F;

		}
	}

	@Override
	protected void customServerAiStep() {
		if (this.isNesting()) {
			if (!this.level().isClientSide()) {
				if (!this.level().getBlockState(this.blockPosition().below()).isRedstoneConductor(this.level(), this.blockPosition().below())) {
					this.setNesting(false);
				} else if (this.getTarget() != null) {
					this.setNesting(false);
				}
			}
		}
	}

	@Override
	protected Vec3 getPassengerAttachmentPoint(Entity entity, EntityDimensions dimensions, float partialTick) {
		double a = Math.toRadians(this.yBodyRot);
		double offSetX = -Math.sin(a) * 0.6D;
		double offSetZ = Math.cos(a) * 0.6D;
		return new Vec3(offSetX, Mth.sin(this.tickCount * 0.5F) * 0.3F, offSetZ);
	}
	@Override
	public boolean shouldRiderSit() {
		return false;
	}

	public boolean isReturningToNest() {
		return this.getEntityData().get(RETURN_TO_NEST);
	}

	public void setReturnToNest(boolean broody) {
		this.getEntityData().set(RETURN_TO_NEST, broody);
	}

	public boolean isLanding() {
		return this.getEntityData().get(IS_LANDING);
	}

	public void setLanding(boolean landing) {
		this.getEntityData().set(IS_LANDING, landing);
	}

	public void setBroodCount(int count) {
		this.broodCount = count;
	}

	public int getBroodCount() {
		return this.broodCount;
	}

	public void setDroppingTimer(int count) {
		this.droppingTimer = count;
	}

	public int getDroppingTimer() {
		return this.droppingTimer;
	}

	public boolean isNesting() {
		return this.getEntityData().get(IS_NESTING);
	}

	public void setNesting(boolean nesting) {
		this.getEntityData().set(IS_NESTING, nesting);
	}

	public boolean isSpinning() {
		return this.getEntityData().get(IS_SPINNING);
	}

	public void setSpinning(boolean spinning) {
		this.getEntityData().set(IS_SPINNING, spinning);
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return SoundRegistry.CHIROMAW_MATRIARCH_LIVING.get();
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return SoundRegistry.CHIROMAW_MATRIARCH_HURT.get();
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundRegistry.CHIROMAW_MATRIARCH_DEATH.get();
	}

	protected SoundEvent getFlySound() {
		return SoundRegistry.CHIROMAW_MATRIARCH_FLAP.get();
	}

	@Override
	public boolean canAttackType(EntityType<?> type) {
		return this.getType() != type;
	}

	@Override
	public int getMaxSpawnClusterSize() {
		return 1;
	}

	@Override
	public float getWalkTargetValue(BlockPos pos, LevelReader level) {
		return 0.5F;
	}

	@Override
	public void checkDespawn() {
	}

	@Override
	public @Nullable SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType spawnType, @Nullable SpawnGroupData data) {
		data = super.finalizeSpawn(level, difficulty, spawnType, data);
		this.setBoundOrigin(GlobalPos.of(level.getLevel().dimension(), this.blockPosition()));
		return data;
	}

	@Override
	public void addAdditionalSaveData(CompoundTag tag) {
		super.addAdditionalSaveData(tag);
		if (this.boundOrigin != null) {
			GlobalPos.CODEC.encodeStart(NbtOps.INSTANCE, this.getBoundOrigin()).resultOrPartial(TheBetweenlands.LOGGER::error).ifPresent(tag1 -> tag.put("bound", tag1));
		}
		tag.putInt("brood_count", this.getBroodCount());
	}

	@Override
	public void readAdditionalSaveData(CompoundTag tag) {
		super.readAdditionalSaveData(tag);

		if (tag.contains("bound")) {
			this.setBoundOrigin(GlobalPos.CODEC.parse(NbtOps.INSTANCE, tag.get("bound")).resultOrPartial(TheBetweenlands.LOGGER::error).orElse(null));
		}

		this.setBroodCount(tag.getInt("brood_count"));
	}

	@Override
	public boolean hurt(DamageSource source, float amount) {
		if (source.is(DamageTypes.IN_WALL) || source.is(DamageTypes.DROWN)) {
			return false;
		}

		if (source.getEntity() == this) return false;
		if (source.getEntity() instanceof LivingEntity entity) {
			this.setTarget(entity);
		}

		if (source.getEntity() instanceof Player player) {
			if (this.isVehicle() && this.hasPassenger(player)) {
				if (!this.level().isClientSide()) {
					this.setDroppingTimer(0);
				}
			}
		}
		return super.hurt(source, amount);
	}

	@Override
	public boolean shouldRenderAtSqrDistance(double distance) {
		return distance < 16384 * getViewScale();
	}

	@Nullable
	public GlobalPos getBoundOrigin() {
		return this.boundOrigin;
	}

	public void setBoundOrigin(@Nullable GlobalPos bound) {
		this.boundOrigin = bound;
	}

	@Nullable
	public AABB getNestBox() {
		return this.boundOrigin != null ? AABB.encapsulatingFullBlocks(this.boundOrigin.pos(), this.boundOrigin.pos().above(5)).inflate(0.0625D, 0F, 0.0625D) : null;
	}

	@Override
	public boolean isUnmountBlocked(Player rider) {
		return !this.level().isClientSide() && this.getDroppingTimer() > 0;
	}
}
