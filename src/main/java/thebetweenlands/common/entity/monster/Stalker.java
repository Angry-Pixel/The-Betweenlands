package thebetweenlands.common.entity.monster;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.*;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Nullable;
import thebetweenlands.client.particle.ParticleFactory;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.entity.ClimbingMob;
import thebetweenlands.common.entity.ai.goals.StalkerBreakLightGoal;
import thebetweenlands.common.entity.ai.goals.StalkerDropAttackGoal;
import thebetweenlands.common.entity.ai.goals.StalkerScreechGoal;
import thebetweenlands.common.entity.ai.goals.StalkerScurryGoal;
import thebetweenlands.common.entity.movement.climb.*;
import thebetweenlands.common.herblore.elixir.ElixirEffectRegistry;
import thebetweenlands.common.registries.DimensionRegistries;
import thebetweenlands.common.registries.ParticleRegistry;
import thebetweenlands.common.registries.SoundRegistry;

import java.util.function.Predicate;

//FIXME needs a little more pathing work done, but im tired and it mostly works
public class Stalker extends ClimbingMob implements Enemy {

	public static final EntityDataAccessor<Boolean> SCREECHING = SynchedEntityData.defineId(Stalker.class, EntityDataSerializers.BOOLEAN);
	public static final EntityDataAccessor<Boolean> DROP = SynchedEntityData.defineId(Stalker.class, EntityDataSerializers.BOOLEAN);

	protected boolean restrictToPitstone = false;

	protected int maxPathingTargetHeight;

	public boolean isStalking = true;

	protected float farAnglePathingPenalty = 1.0f;
	public float farAngle = 90.0f;

	protected float nearAnglePathingPenalty = 2.0f;
	protected float nearAngle = 65.0f;

	protected float stalkingDistanceNear = 4.0f;
	public float stalkingDistanceFar = 8.0f;
	protected float stalkingDistancePenalty = 4.0f;

	protected boolean isFleeingFromView;

	protected int inViewTimer = 0;

	protected int checkSeenTimer = 20;
	protected boolean canStalkerBeSeen = false;

	public Vec3 eyeRotation;
	public Vec3 prevEyeRotation;
	public Vec3 eyeRotationTarget;
	private int nextEyeRotate;

	public int animationOffset;

	private boolean canCallAllies = true;

	public int screechingTicks = 0;
	public int prevScreechingTicks = 0;

	public Stalker(EntityType<? extends ClimbingMob> type, Level level) {
		super(type, level);
		this.xpReward = 30;
		this.maxPathingTargetHeight = 10;
		this.eyeRotation = new Vec3(0, 0, 0);
		this.prevEyeRotation = new Vec3(0, 0, 0);
		this.eyeRotationTarget = new Vec3(0, 0, 0);
		this.animationOffset = this.getRandom().nextInt(200);
		this.nextEyeRotate = 1;
	}

	@Override
	protected void registerGoals() {
		this.goalSelector.addGoal(0, new FloatGoal(this));
		this.goalSelector.addGoal(1, new StalkerDropAttackGoal(this));
		this.goalSelector.addGoal(2, new StalkerScurryGoal(this, 1.75f));
		this.goalSelector.addGoal(3, new StalkerScreechGoal(this, 80, 160, 1, 3));
		this.goalSelector.addGoal(4, new StalkerBreakLightGoal(this));
		this.goalSelector.addGoal(5, new MeleeAttackGoal(this, 1.1D, false));
		this.goalSelector.addGoal(6, new MoveTowardsRestrictionGoal(this, 1.0D));
		this.goalSelector.addGoal(7, new RandomStrollGoal(this, 0.75D));
		this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 8.0F));
		this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));

		this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, 1, false, false, null));
	}

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder) {
		super.defineSynchedData(builder);
		builder.define(SCREECHING, false);
		builder.define(DROP, false);
	}

	public static AttributeSupplier.Builder registerAttributes() {
		return Monster.createMonsterAttributes()
			.add(Attributes.MAX_HEALTH, 40.0D)
			.add(Attributes.MOVEMENT_SPEED, 0.25D)
			.add(Attributes.ATTACK_DAMAGE, 10.0D)
			.add(Attributes.FOLLOW_RANGE, 40.0D)
			.add(Attributes.KNOCKBACK_RESISTANCE, 1.0D);
	}

	@Override
	public @Nullable SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType spawnType, @Nullable SpawnGroupData spawnGroupData) {
		if (this.getY() < TheBetweenlands.PITSTONE_HEIGHT + 3 && this.level().dimension() == DimensionRegistries.DIMENSION_KEY) {
			this.restrictToPitstone = true;
		}
		return super.finalizeSpawn(level, difficulty, spawnType, spawnGroupData);
	}

	@Override
	public void addAdditionalSaveData(CompoundTag tag) {
		super.addAdditionalSaveData(tag);
		tag.putBoolean("restrict_to_pitstone", this.restrictToPitstone);
		tag.putBoolean("can_call_allies", this.canCallAllies);
	}

	@Override
	public void readAdditionalSaveData(CompoundTag tag) {
		super.readAdditionalSaveData(tag);
		this.restrictToPitstone = tag.getBoolean("restrict_to_pitstone");
		this.canCallAllies = tag.getBoolean("can_call_allies");
	}

	public void setCanCallAllies(boolean canCall) {
		this.canCallAllies = canCall;
	}

	public boolean canCallAllies() {
		return this.canCallAllies;
	}

	public void setScreeching(boolean screeching) {
		this.getEntityData().set(SCREECHING, screeching);
	}

	public boolean isScreeching() {
		return this.getEntityData().get(SCREECHING);
	}

	public void setDropping(boolean dropping) {
		this.getEntityData().set(DROP, dropping);
	}

	public boolean isDropping() {
		return this.getEntityData().get(DROP);
	}

	@Override
	public float getMovementSpeed() {
		return super.getMovementSpeed() + (this.isStalking && this.isFleeingFromView ? 0.25F : 0.0F);
	}

	@Override
	public float getPathingMalus(BlockGetter cache, Mob entity, PathType type, BlockPos pos, Vec3i direction, Predicate<Direction> sides) {
		float priority = super.getPathfindingMalus(type);

		float penalty = 0;

		if (this.restrictToPitstone && this.getY() > TheBetweenlands.PITSTONE_HEIGHT + 3 && pos.getY() >= this.getY()) {
			penalty += (float) Math.min((pos.getY() - this.getY()) * 0.5f, 8);
		}

		if (priority >= 0.0f && this.isStalking) {
			int height = 0;

			while (pos.getY() - height > 0) {
				height++;

				if (!this.level().isEmptyBlock(pos.relative(Direction.DOWN, height))) {
					break;
				}
			}

			penalty += Math.max(0, 6 - height) * 0.1f;

			LivingEntity target = this.getTarget();

			if (target != null) {
				Vec3 look = target.getViewVector(1);
				Vec3 dir = new Vec3(pos.getX() + 0.5f, pos.getY() + 0.5f, pos.getZ() + 0.5f).subtract(target.getEyePosition().subtract(look)).normalize();
				float dot = (float) look.dot(dir);

				float angle = (float) Math.toDegrees(Math.acos(dot));

				penalty += Math.max(0, this.nearAngle - angle) / this.nearAngle * this.nearAnglePathingPenalty;
				penalty += Math.max(0, (this.farAngle - this.nearAngle) - (angle - this.nearAngle)) / (this.farAngle - this.nearAngle) * this.farAnglePathingPenalty;

				double dst = new Vec3(pos.getX() + 0.5f, pos.getY() + 0.5f, pos.getZ() + 0.5f).subtract(target.getEyePosition()).length();

				penalty += (float) ((1.0F - (Math.clamp(dst, this.stalkingDistanceNear, this.stalkingDistanceFar) - this.stalkingDistanceNear) / (this.stalkingDistanceFar - this.stalkingDistanceNear)) * this.stalkingDistancePenalty);
			}
		}

		return priority + penalty;
	}

	protected boolean isPathNodeAllowed(int x, int y, int z) {
		if (this.restrictToPitstone && this.getY() <= TheBetweenlands.PITSTONE_HEIGHT + 3 && y >= TheBetweenlands.PITSTONE_HEIGHT + 2) {
			return false;
		}

		if (this.isStalking) {
			LivingEntity target = this.getTarget();

			if (target != null) {
				if (target.distanceToSqr(x + 0.5f, y + 0.5f, z + 0.5f) > this.stalkingDistanceNear) {
					return true;
				}

				Vec3 look = target.getViewVector(1);
				Vec3 dir = this.position().subtract(target.getEyePosition().subtract(look)).normalize();
				float dot = (float) look.dot(dir);
				float angle = (float) Math.toDegrees(Math.acos(dot));
				if (angle < this.nearAngle) {
					return new Vec3(x + 0.5f, y + 0.5f, z + 0.5f).subtract(target.position()).length() > this.position().subtract(target.position()).length();
				}

				return false;
			}
		}

		return true;
	}

	@Override
	protected PathNavigation createNavigation(Level level) {
		ObstructionAwareGroundNavigation<Stalker> navigate = new ObstructionAwareClimberNavigation<>(this, level, false, true, true) {

			@Override
			public Path createPath(Entity entity, int accuracy) {
				BlockPos pos = entity.blockPosition();

				if (Stalker.this.isStalking && !Stalker.this.isFleeingFromView) {
					//Path to ceiling above target if possible
					for (int i = 1; i <= Stalker.this.maxPathingTargetHeight; i++) {
						if (this.getNodeEvaluator().getPathType(new PathfindingContext(this.level, Stalker.this), pos.getX(), pos.getY() + i, pos.getZ()) == PathType.WALKABLE) {
							pos = pos.above(i);
							break;
						}
					}
				}

				return this.createPath(pos, accuracy);
			}

			@Override
			protected PathFinder createPathFinder(int maxVisitedNodes) {
				this.nodeEvaluator = new ObstructionAwareNodeEvaluator() {
					@Override
					public PathType getPathType(PathfindingContext context, int x, int y, int z) {
						if (!Stalker.this.isPathNodeAllowed(x, y, z)) {
							return PathType.BLOCKED;
						}
						return super.getPathType(context, x, y, z);
					}
				};

				ClimberPathFinder pathFinder = new ClimberPathFinder(this.nodeEvaluator, maxVisitedNodes);

				pathFinder.setMaxExpansions(400);

				pathFinder.setHeuristic((start, end, isTargetHeuristic) -> {
					if (isTargetHeuristic && Stalker.this.isFleeingFromView) {
						LivingEntity target = Stalker.this.getTarget();

						if (target != null) {
							Vec3 look = target.getViewVector(1);
							Vec3 diff = new Vec3(start.x + 0.5f - target.getX(), start.y + 0.5f - target.getY(), start.z + 0.5f - target.getZ());
							float dot = (float) look.dot(diff);
							diff = diff.subtract(look.scale(dot));
							return 8.0f - (float) Math.abs(diff.x) - (float) Math.abs(diff.y) - (float) Math.abs(diff.z);
						}
					}

					return ClimberPathFinder.DEFAULT_HEURISTIC.compute(start, end, isTargetHeuristic);
				});

				return pathFinder;
			}
		};
		navigate.setCanFloat(true);
		return navigate;
	}

	@Override
	public Vec3 getStickingForce(Pair<Direction, Vec3> walkingSide) {
		if (this.isDropping()) {
			return new Vec3(0, -0.08D, 0);
		}

		return super.getStickingForce(walkingSide);
	}

	@Override
	protected boolean canAttachToWalls() {
		return super.canAttachToWalls() && !this.isDropping();
	}

	private boolean canSeePosition(Vec3 start, Vec3 end) {
		return this.level().clip(new ClipContext(start, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this)).getType() == HitResult.Type.MISS;
	}

	@Override
	protected boolean shouldDespawnInPeaceful() {
		return true;
	}

	@Override
	public SoundSource getSoundSource() {
		return SoundSource.HOSTILE;
	}

	@Override
	public void tick() {
		if (this.isDropping() && this.onGround()) {
			this.setDropping(false);
		}

		super.tick();

		this.prevScreechingTicks = this.screechingTicks;
		if (this.isScreeching()) {
			this.screechingTicks++;

			if (this.screechingTicks == 40) {
				this.playSound(SoundRegistry.STALKER_SCREECH.get(), this.getSoundVolume(), 1);
			}
		} else {
			this.screechingTicks = Math.min(Math.max(this.screechingTicks - 2, 0), 40);
		}

		if (!this.level().isClientSide()) {
			boolean wasFleeingFromView = this.isFleeingFromView;
			this.isFleeingFromView = false;

			LivingEntity target = this.getTarget();

			boolean isPotentiallySeen = false;

			if (this.isStalking) {
				if (target != null) {
					Vec3 look = target.getViewVector(1);
					Vec3 dir = this.position().subtract(target.getEyePosition().subtract(look)).normalize();
					float dot = (float) look.dot(dir);
					float angle = (float) Math.toDegrees(Math.acos(dot));

					if (angle < this.nearAngle) {
						isPotentiallySeen = true;

						AABB aabb = this.getBoundingBox();
						Vec3 center = new Vec3((aabb.minX + aabb.maxX) * 0.5D, (aabb.minY + aabb.maxY) * 0.5D, (aabb.minZ + aabb.maxZ) * 0.5D);

						if (this.checkSeenTimer++ >= 20) {
							this.checkSeenTimer = 0;

							this.canStalkerBeSeen = false;

							if (this.canSeePosition(target.getEyePosition(), center)) {
								this.canStalkerBeSeen = true;
							} else {
								AABB checkAabb = this.getBoundingBox().inflate(1);

								for (int xo = 0; xo <= 1; xo++) {
									for (int yo = 0; yo <= 1; yo++) {
										for (int zo = 0; zo <= 1; zo++) {
											Vec3 checkPos = new Vec3(
												(xo == 0 ? checkAabb.minX : checkAabb.maxX),
												(yo == 0 ? checkAabb.minY : checkAabb.maxY),
												(zo == 0 ? checkAabb.minZ : checkAabb.maxZ)
											);

											if (this.canSeePosition(target.getEyePosition(), checkPos)) {
												this.canStalkerBeSeen = true;
												break;
											}
										}
									}
								}
							}
						}

						if (this.canStalkerBeSeen) {
							this.isFleeingFromView = true;

							if (this.getNavigation().isDone() && this.tickCount % 10 == 0) {
								Vec3 right = look.cross(new Vec3(0, 1, 0));
								Vec3 up = right.cross(look);

								float rot = this.getRandom().nextFloat() * Mth.TWO_PI;
								float radius = 8.0f;

								Vec3 offset = right.scale(Math.cos(rot) * radius).add(up.scale(Math.sin(rot) * radius)).add(look.scale(-radius * 0.5f));

								HitResult result = this.level().clip(new ClipContext(center, center.add(offset), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
								if (result.getType() != HitResult.Type.MISS) {
									offset = result.getLocation();
								}

								this.getNavigation().moveTo(this.getX() + offset.x, this.getY() + offset.y, this.getZ() + offset.z, 1.0f);
							}
						}
					}
				}
			} else if (target == null) {
				this.isStalking = true;
			}

			if (!isPotentiallySeen) {
				this.checkSeenTimer = 20;
			}

			if (wasFleeingFromView && !this.isFleeingFromView) {
				this.getNavigation().stop();
			}

			if (this.isFleeingFromView && (target == null || this.hasLineOfSight(target))) {
				this.inViewTimer++;

				if (this.inViewTimer > 200) {
					this.isStalking = false;

					if (target != null && this.distanceTo(target) < 16.0f) {
						this.useParalysisAttack(target);
					}
				}
			} else {
				this.inViewTimer = Math.max(0, this.inViewTimer - 3);
			}
		} else {
			if (this.screechingTicks > 40 && this.tickCount % 2 == 0) {
				this.spawnScreechingParticles();
			}
		}

		// Eye animation
		this.prevEyeRotation = this.eyeRotation;
		if ((this.tickCount + this.animationOffset) % this.nextEyeRotate == 0) {
			this.eyeRotationTarget = new Vec3(this.getRandom().nextFloat() - 0.5, this.getRandom().nextFloat() - 0.5, this.getRandom().nextFloat() - 0.5);
			this.nextEyeRotate = this.getRandom().nextInt(15) + 20;
		}
		this.eyeRotation = this.eyeRotation.add(this.eyeRotationTarget.subtract(this.eyeRotation).scale(0.5));
	}

	protected void spawnScreechingParticles() {
		float angle = (float) Math.toRadians(-this.yBodyRot);
		float ox = Mth.sin(angle) * 0.725f;
		float oz = Mth.cos(angle) * 0.725f;

		float screechingStrength = Mth.clamp((this.screechingTicks - 20) / 30.0f, 0, 1);
		float screechingHeadRotationStrength = Mth.sin(this.tickCount * 0.4f) * screechingStrength;
		float osx = Mth.sin(angle + Mth.PI * 0.5f) * 0.05f * screechingHeadRotationStrength;
		float osz = Mth.cos(angle + Mth.PI * 0.5f) * 0.05f * screechingHeadRotationStrength;

		TheBetweenlands.createParticle(ParticleRegistry.SONIC_SCREAM.get(), this.level(), this.getX() + ox, this.getY() + 1.5f, this.getZ() + oz,
			ParticleFactory.ParticleArgs.get().withMotion(osx, 0.3f, osz).withScale(10).withData(30, Mth.floor(this.tickCount * 3.3f))
				.withColor(1.0f, 0.9f, 0.8f, 1.0f));

//		BatchedParticleRenderer.INSTANCE.addParticle(DefaultParticleBatches.TRANSLUCENT_GLOWING, particle);
	}

	@Override
	public boolean hurt(DamageSource source, float amount) {
		if (super.hurt(source, amount)) {
			if (this.getTarget() != null && source.getEntity() == this.getTarget()) {
				this.isStalking = false;
			}

			if (amount >= 5 && this.isScreeching()) {
				this.setScreeching(false);
			}

			return true;
		}

		return false;
	}

	public void useParalysisAttack(LivingEntity target) {
		target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 80, 3));
		target.addEffect(new MobEffectInstance(ElixirEffectRegistry.ROOT_BOUND, 80, 10));

		this.playSound(SoundRegistry.STALKER_SCREAM.get(), 0.6f, 1.0f);
	}

	@Override
	protected void playStepSound(BlockPos pos, BlockState state) {
		this.playSound(SoundRegistry.STALKER_STEP.get(), 1.5f, 1.0f);
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return !this.isStalking ? SoundRegistry.STALKER_LIVING.get() : null;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return SoundRegistry.STALKER_HURT.get();
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundRegistry.STALKER_DEATH.get();
	}
}