package thebetweenlands.common.entity.boss;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.level.pathfinder.PathfindingContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.event.EventHooks;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;
import thebetweenlands.api.entity.bossbar.BetweenlandsBossBar;
import thebetweenlands.api.entity.CameraOffsetter;
import thebetweenlands.api.entity.MusicPlayer;
import thebetweenlands.api.entity.ScreenShaker;
import thebetweenlands.api.entity.bossbar.BetweenlandsServerBossBar;
import thebetweenlands.client.audio.EntityMusicLayers;
import thebetweenlands.common.datagen.tags.BLDamageTagProvider;
import thebetweenlands.common.entity.BLEntity;
import thebetweenlands.common.entity.monster.MummyArm;
import thebetweenlands.common.entity.monster.PeatMummy;
import thebetweenlands.common.entity.movement.climb.ObstructionAwareGroundNavigation;
import thebetweenlands.common.entity.movement.climb.PathObstructionAwareEntity;
import thebetweenlands.common.entity.projectile.SludgeBall;
import thebetweenlands.common.network.clientbound.SummonPeatMummyParticlesPacket;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.EntityRegistry;
import thebetweenlands.common.registries.SoundRegistry;

import java.util.List;
import java.util.function.Predicate;

public class DreadfulPeatMummy extends Monster implements BLEntity, BetweenlandsBossBar, ScreenShaker, CameraOffsetter, MusicPlayer, PathObstructionAwareEntity {

	private final BetweenlandsServerBossBar bossInfo = new BetweenlandsServerBossBar(this.getDisplayName(), BossType.NORMAL_BOSS);

	private static final EntityDataAccessor<Integer> SPAWNING_STATE_DW = SynchedEntityData.defineId(DreadfulPeatMummy.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<Boolean> SPEW = SynchedEntityData.defineId(DreadfulPeatMummy.class, EntityDataSerializers.BOOLEAN);
	private static final EntityDataAccessor<Integer> PREY = SynchedEntityData.defineId(DreadfulPeatMummy.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<Float> Y_OFFSET = SynchedEntityData.defineId(DreadfulPeatMummy.class, EntityDataSerializers.FLOAT);

	private int prevSpawningState;

	private int spewTicks = 0;

	private static final int BREAK_COUNT = 20;

	private static final int SPAWN_MUMMY_COOLDOWN = 350;
	private int untilSpawnMummy = 0;
	private static final int SPAWN_SLUDGE_COOLDOWN = 150;
	private int untilSpawnSludge = 0;
	private float prevYOffset;

	private int eatPreyTimer = 60;
	@Nullable
	public LivingEntity currentEatPrey;

	public int deathTicks = 0;

	private int outOfRangeCounter = 0;
	private int outOfRangeCycleCounter = 0;
	private int obstructedCounter = 0;
	private boolean breakBlocksBelow = false;
	private int blockBreakCounter = 0;

	public DreadfulPeatMummy(EntityType<? extends Monster> type, Level level) {
		super(type, level);
	}

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder) {
		super.defineSynchedData(builder);
		builder.define(SPAWNING_STATE_DW, 0);
		builder.define(SPEW, false);
		builder.define(PREY, -1);
		builder.define(Y_OFFSET, 0.0F);
	}

	@Override
	protected void registerGoals() {
		super.registerGoals();
		this.goalSelector.addGoal(0, new FloatGoal(this));
		this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.0D, false));
		this.goalSelector.addGoal(2, new MoveTowardsRestrictionGoal(this, 1.0D));
		this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 16.0F));
		this.goalSelector.addGoal(4, new RandomStrollGoal(this, 1.0D));
		this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));
		this.targetSelector.addGoal(0, new HurtByTargetGoal(this));
		this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, false));
	}

	public static AttributeSupplier.Builder registerAttributes() {
		return Monster.createMonsterAttributes()
			.add(Attributes.MAX_HEALTH, 550.0D)
			.add(Attributes.MOVEMENT_SPEED, 0.3D)
			.add(Attributes.ATTACK_DAMAGE, 8.0D)
			.add(Attributes.FOLLOW_RANGE, 40.0D)
			.add(Attributes.KNOCKBACK_RESISTANCE, 1.0D);
	}

	@Override
	protected PathNavigation createNavigation(Level level) {
		ObstructionAwareGroundNavigation<DreadfulPeatMummy> navigate = new ObstructionAwareGroundNavigation<>(this, level, true);
		navigate.setCanFloat(true);
		return navigate;
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return SoundRegistry.DREADFUL_PEAT_MUMMY_LIVING.get();
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return SoundRegistry.DREADFUL_PEAT_MUMMY_HURT.get();
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundRegistry.DREADFUL_PEAT_MUMMY_DEATH.get();
	}

	public double getYOffset() {
		return this.getEntityData().get(Y_OFFSET);
	}

	public void setYOffset(float yOffset) {
		this.getEntityData().set(Y_OFFSET, yOffset);
	}

	public float getCurrentOffset() {
		return (float) ((-this.getSpawnOffset() + this.getSpawningProgress() * this.getSpawnOffset()));
	}

	public double getSpawnOffset() {
		return 3.0D;
	}

	public int getSpawningState() {
		return this.getEntityData().get(SPAWNING_STATE_DW);
	}

	public int getSpawningLength() {
		return 180;
	}

	public float getSpawningProgress() {
		if (this.getSpawningLength() == 0) {
			return 1.0F;
		}
		return 1.0F / this.getSpawningLength() * this.getSpawningState();
	}

	public float getSpawningProgress(float delta) {
		if (this.getSpawningLength() == 0) {
			return 1.0F;
		}
		return 1.0F / this.getSpawningLength() * (this.prevSpawningState + (this.getSpawningState() - this.prevSpawningState) * delta);
	}

	/**
	 * Returns the interpolated relative spawning progress
	 */
	public float getInterpolatedYOffsetProgress(float partialTicks) {
		return this.prevYOffset + (((float) this.getYOffset()) - this.prevYOffset) * partialTicks;
	}

	public void updateSpawningState() {
		int spawningState = getSpawningState();

		this.prevSpawningState = spawningState;

		if (this.getEntityData().get(SPEW)) {
			int targetState = Mth.ceil(this.getSpawningLength() * 0.6f);

			if (spawningState > targetState) {
				this.getEntityData().set(SPAWNING_STATE_DW, spawningState - 1);
			}
		} else if (spawningState < this.getSpawningLength()) {
			this.getEntityData().set(SPAWNING_STATE_DW, spawningState + 1);
		}
	}

	@Override
	public boolean removeWhenFarAway(double distanceToClosestPlayer) {
		return false;
	}

	@Override
	protected float getWaterSlowDown() {
		return 1.0F;
	}

	protected boolean isTargetOutOfAttackRange(LivingEntity target) {
		if (target.getY() > this.getBoundingBox().maxY - 0.25D) {
			Path path = this.getNavigation().getPath();

			//Check if there is a path to the target
			if (path != null) {
				Node finalPoint = path.getEndNode();

				if (finalPoint != null) {
					PathType type = this.getNavigation().getNodeEvaluator().getPathType(new PathfindingContext(this.level(), this), finalPoint.x, finalPoint.y - 1, finalPoint.z);
					boolean isEndpointInAir = type != PathType.BLOCKED;
					return isEndpointInAir || finalPoint.y + this.getBbHeight() - 0.25D < target.getY();
				}
			}

			return true;
		}

		return this.distanceTo(target) > 24;
	}

	protected boolean isTargetInCloseRange(LivingEntity target) {
		return !this.isTargetOutOfAttackRange(target) && target.distanceTo(this) < 4;
	}

	protected boolean isTargetObstructed(LivingEntity target) {
		Path path = this.getNavigation().getPath();

		//Check if there is a path to the target
		if (path != null && path.getEndNode() != null) {
			Node finalPoint = path.getEndNode();

			if (finalPoint != null && target.distanceToSqr(finalPoint.x, finalPoint.y, finalPoint.z) < 0.9f) {
				PathType type = this.getNavigation().getNodeEvaluator().getPathType(new PathfindingContext(this.level(), this), finalPoint.x, finalPoint.y, finalPoint.z);

				if (type == PathType.OPEN || type == PathType.WALKABLE) {
					return false;
				}
			}
		}

		BlockPos.MutableBlockPos checkPos = new BlockPos.MutableBlockPos();

		BlockPos center = target.blockPosition();

		for (int yo = Mth.floor(target.getBbHeight()); yo < 3; yo++) {
			for (int xo = -1; xo <= 1; xo++) {
				for (int zo = -1; zo <= 1; zo++) {
					checkPos.set(center.getX() + xo, center.getY() + yo, center.getZ() + zo);

					if (!this.level().getEntityCollisions(null, new AABB(checkPos)).isEmpty()) {
						return true;
					}
				}
			}
		}

		return false;
	}

	protected boolean isBridgableSpot(BlockPos pos) {
		if (this.level().isEmptyBlock(pos)) {
			return true;
		}
		PathType nodeType = this.getNavigation().getNodeEvaluator().getPathType(new PathfindingContext(this.level(), this), pos.getX(), pos.getY(), pos.getZ());
		return nodeType != PathType.BLOCKED && nodeType != PathType.WATER;
	}

	protected void placeBridge() {
		Path path = this.getNavigation().getPath();

		if (path != null && path.getNextNodeIndex() < path.getNodeCount()) {
			Node nextPoint = path.getNode(path.getNextNodeIndex());

			if (nextPoint.y == Mth.floor(this.getY() + 0.5f)) {
				BlockPos pos = this.blockPosition();
				pos = pos.offset(0, nextPoint.y - pos.getY() - 1, 0);

				if (this.distanceToSqr(Vec3.atCenterOf(pos)) <= 2 && this.isBridgableSpot(pos)) {
					if (EventHooks.canEntityGrief(this.level(), this)) {

						int nx = -2;
						int nz = -2;

						if (path.getNextNodeIndex() < path.getNodeCount() - 1) {
							Node secondNextPoint = path.getNode(path.getNextNodeIndex() + 1);
							nx = Mth.clamp(secondNextPoint.x - nextPoint.x, -1, 1);
							nz = Mth.clamp(secondNextPoint.z - nextPoint.z, -1, 1);
							if (nx == 0) nx = -2;
							if (nz == 0) nz = -2;
						}

						for (int xo = -1; xo <= 1; xo++) {
							for (int zo = -1; zo <= 1; zo++) {
								if (xo != nx && zo != nz) {
									BlockPos offsetPos = pos.offset(xo, 0, zo);

									if (this.level().getEntitiesOfClass(LivingEntity.class, new AABB(offsetPos), e -> e != this).isEmpty() && this.isBridgableSpot(offsetPos)) {

										boolean canReplace = false;

										if (this.level().getBlockState(offsetPos).canBeReplaced()) {
											canReplace = true;
										} else {
											BlockState hitState = this.level().getBlockState(offsetPos);
											float hardness = hitState.getDestroySpeed(this.level(), offsetPos);

											if (hardness >= 0
												&& hitState.getBlock().canEntityDestroy(hitState, this.level(), offsetPos, this)
												&& EventHooks.onEntityDestroyBlock(this, offsetPos, hitState)) {

												canReplace = true;
												if (!hitState.liquid() && !hitState.getCollisionShape(this.level(), pos).isEmpty()) {
													this.level().levelEvent(2001, offsetPos, Block.getId(hitState));
												}
												this.level().removeBlock(offsetPos, false);
											}
										}

										if (canReplace) {
											this.level().setBlockAndUpdate(offsetPos, BlockRegistry.PEAT.get().defaultBlockState());
										}

									}
								}
							}
						}
					}
				}
			}
		}
	}

	@Override
	public float getBridgePathingMalus(Mob entity, BlockPos pos, @Nullable Node fallPathPoint) {
		return fallPathPoint == null || fallPathPoint.y < pos.getY() ? 2.0f : -1.0f;
	}

	@Override
	public float getPathingMalus(BlockGetter cache, Mob entity, PathType nodeType, BlockPos pos, Vec3i direction, Predicate<Direction> sides) {
		if (nodeType == PathType.BLOCKED) {
			return 10.0f;
		}
		return super.getPathfindingMalus(nodeType);
	}

	@Override
	public void onPathingObstructed(Direction facing) {
		if (this.getTarget() != null) {
			this.breakBlocksBelow = facing == Direction.DOWN;
			this.blockBreakCounter = 40;
		}
	}

	@Override
	public void tick() {
		super.tick();
		this.prevYOffset = (float) getYOffset();

		if ((this.getPrey() != null && this.getEntityData().get(SPEW)) || !this.isAlive()) {
			this.setPrey(null);
		}

		this.currentEatPrey = this.getPrey();
		if (this.currentEatPrey != null) {
			this.updateEatPrey();
		}

		this.updateSpawningState();

		if (this.level().isClientSide()) {
			if (this.getSpawningProgress() < 1.0F) {
				this.setYOffset(this.getCurrentOffset());
				this.setDeltaMovement(Vec3.ZERO);
				if (this.getSpawningState() == this.getSpawningLength() - 1) {
					this.setPos(this.position());
				}
				int breakPoint = this.getSpawningLength() / BREAK_COUNT;
				if ((this.getSpawningState() - breakPoint / 2 - 1) % breakPoint == 0) {
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
			} else if (this.deathTicks == 0) {
				this.setYOffset(0F);
			} else if (this.deathTicks > 60) {
				this.setDeltaMovement(Vec3.ZERO);
				if (this.deathTicks % 5 == 0) {
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
		} else {
			LivingEntity target = this.getTarget();

			if (target != null && (this.blockBreakCounter == 0 || !this.breakBlocksBelow) && !this.jumping) {
				this.placeBridge();
			}

			double targetMotionX = 0.0D;
			double targetMotionY = 0.0D;
			double targetMotionZ = 0.0D;

			if (target instanceof Player) {
				CompoundTag nbt = target.getPersistentData();

				targetMotionX = (float) target.getX() - nbt.getDouble("thebetweenlands.dpm.lastX");
				targetMotionY = (float) target.getY() - nbt.getDouble("thebetweenlands.dpm.lastY");
				targetMotionZ = (float) target.getZ() - nbt.getDouble("thebetweenlands.dpm.lastZ");

				nbt.putDouble("thebetweenlands.dpm.lastX", target.getX());
				nbt.putDouble("thebetweenlands.dpm.lastY", target.getY());
				nbt.putDouble("thebetweenlands.dpm.lastZ", target.getZ());
			} else if (target != null) {
				targetMotionX = (float) target.getDeltaMovement().x();
				targetMotionY = (float) target.getDeltaMovement().y();
				targetMotionZ = (float) target.getDeltaMovement().z();
			}

			if (target != null && this.isTargetOutOfAttackRange(target)) {
				if (this.obstructedCounter > 0 && this.getEntityData().get(SPEW)) {
					this.outOfRangeCycleCounter++;

					//Make DPM cycle between ranged and melee if both obstructed and out of range
					if (this.outOfRangeCycleCounter > 200) {
						this.outOfRangeCycleCounter = 0;
						this.outOfRangeCounter = 0;
						this.getEntityData().set(SPEW, false);
					}
				} else {
					this.outOfRangeCounter += 2;
				}
			} else {
				if (target != null && this.isTargetInCloseRange(target)) {
					this.outOfRangeCounter = Math.max(0, this.outOfRangeCounter - 2);
				} else {
					this.outOfRangeCounter = Math.max(0, this.outOfRangeCounter - 1);
				}
			}

			if (this.tickCount % 10 == 0) {
				if (target != null && this.isTargetObstructed(target)) {
					this.obstructedCounter += 2;
				} else {
					this.obstructedCounter = Math.max(0, this.obstructedCounter - 1);
				}
			}

			if (this.obstructedCounter <= 0) {
				this.outOfRangeCycleCounter = Math.max(0, this.outOfRangeCycleCounter - 1);
			}

			if (this.obstructedCounter > 10) {
				this.obstructedCounter = 0;

				if (target != null) {
					BlockPos pos = target.blockPosition();

					if (!this.level().isEmptyBlock(pos.below()) && this.level().getEntitiesOfClass(MummyArm.class, new AABB(pos)).isEmpty()) {
						MummyArm arm = new MummyArm(EntityRegistry.MUMMY_ARM.get(), this.level());
						arm.moveTo(pos.getX() + 0.5f, pos.getY(), pos.getZ() + 0.5f, 0, 0);

						this.level().addFreshEntity(arm);
					}

					this.breakBlocksBelow = false;
					this.blockBreakCounter = 40;
				}
			}

			if (this.blockBreakCounter > 0) {
				this.blockBreakCounter--;

				if (EventHooks.canEntityGrief(this.level(), this)) {
					boolean broken = false;

					for (int xo = -2; xo <= 2; xo++) {
						for (int yo = (this.breakBlocksBelow ? -2 : 0); yo <= 2; yo++) {
							for (int zo = -2; zo <= 2; zo++) {
								if (this.level().getRandom().nextInt(6) == 0) {
									Vec3 center = new Vec3(this.getX() + xo, this.getY() + yo, this.getZ() + zo);

									if (center.subtract(this.position()).dot(this.breakBlocksBelow ? new Vec3(0, -1, 0) : this.getLookAngle()) > 0.3) {
										BlockPos pos = BlockPos.containing(center);

										BlockState hitState = this.level().getBlockState(pos);
										float hardness = hitState.getDestroySpeed(this.level(), pos);

										if (!hitState.isAir() && hardness >= 0
											&& hitState.getBlock().canEntityDestroy(hitState, this.level(), pos, this)
											&& EventHooks.onEntityDestroyBlock(this, pos, hitState)) {
											this.level().destroyBlock(pos, false);
											broken = true;
										}
									}
								}
							}
						}
					}

					if (broken) {
						this.playSound(SoundRegistry.DREADFUL_PEAT_MUMMY_SWIPE.get(), 1, 1);
						this.playSound(SoundRegistry.DREADFUL_PEAT_MUMMY_BITE.get(), 1, 1);
					}
				}
			}

			if (this.outOfRangeCounter > 300) {
				this.outOfRangeCounter = 300;
				this.getEntityData().set(SPEW, true);
			} else if (this.outOfRangeCounter <= 0) {
				this.getEntityData().set(SPEW, false);
			}

			if (this.getEntityData().get(SPEW)) {
				this.spewTicks++;

				if (this.spewTicks > 60 && (this.spewTicks / 5) % 5 == 0) {
					if (target != null) {
						int numBalls = 1 + this.getRandom().nextInt(3);
						for (int i = 1; i < numBalls; i++) {
							float g = -0.03f;

							float vy0 = 0.5f + this.getRandom().nextFloat() * 1.3f;

							float tmax = -vy0 / g;

							float s = vy0 * tmax + 0.5f * g * tmax * tmax;

							float h = (float) (target.getY() - this.getY());

							float fall = h - s;

							if (fall < 0) {
								float tmin = Mth.sqrt(fall * 2 / g);

								float t = tmax + tmin;

								double dx = (target.getX() - this.getX()) + targetMotionX * t / 3 + (this.getRandom().nextFloat() - 0.5f) * 2;
								double dz = (target.getZ() - this.getZ()) + targetMotionZ * t / 3 + (this.getRandom().nextFloat() - 0.5f) * 2;

								float len = Mth.sqrt((float) (dx * dx + dz * dz));

								dx /= len;
								dz /= len;

								float speed = Math.min(len / t * 3.0f /*constant adjustment for MC physics*/, 1.5f);

								this.spawnRangedSludge(dx * speed, vy0, dz * speed);
							}
						}

						if (this.spewTicks % 5 == 0) {
							this.playSound(SoundRegistry.DREADFUL_PEAT_MUMMY_RETCH.get(), 1F, 0.7F + this.getRandom().nextFloat() * 0.6F);
							this.playSound(SoundRegistry.DREADFUL_PEAT_MUMMY_LICK.get());
						}
					}
				}
			} else {
				this.spewTicks = 0;
			}

			if (this.deathTicks > 60) {
				this.setYOffset(-(this.deathTicks - 60) * 0.05F);
			}

			if (this.getSpawningProgress() < 1.0F) {
				if (this.getSpawningState() == 0) {
					this.playSound(SoundRegistry.DREADFUL_PEAT_MUMMY_EMERGE.get(), 1.2F, 1.0F);
				}

				this.setYOffset(this.getCurrentOffset());
				this.setDeltaMovement(Vec3.ZERO);
				this.hurtMarked = true;

				int breakPoint = getSpawningLength() / BREAK_COUNT;
				if ((getSpawningState() - breakPoint / 2 - 1) % breakPoint == 0) {
					BlockPos pos = this.blockPosition().below();
					BlockState blockState = this.level().getBlockState(pos);
					this.playSound(blockState.getSoundType(this.level(), pos, this).getBreakSound(), this.getRandom().nextFloat() * 0.3F + 0.3F, this.getRandom().nextFloat() * 0.15F + 0.7F);
				}

				if (this.getTarget() != null) this.lookAt(this.getTarget(), 360, 360);
				if (this.getSpawningState() == this.getSpawningLength() - 1) {
					this.setPos(this.position());
				}
			} else if (this.deathTicks < 60) {
				this.setYOffset(0);
				this.prevYOffset = 0;
			}
		}

		if (!this.level().isClientSide() && this.isAlive() && this.getSpawningProgress() >= 1) {
			//Heal when no player is nearby
			if (this.tickCount % 12 == 0 && this.getHealth() < this.getMaxHealth() && this.level().getNearestPlayer(this, 32) == null) {
				this.heal(1);
			}

			if (this.getTarget() != null) {
				AABB checkAABB = this.getBoundingBox().inflate(64, 64, 64);
				List<PeatMummy> peatMummies = this.level().getEntitiesOfClass(PeatMummy.class, checkAABB);
				int mummies = 0;
				for (PeatMummy mummy : peatMummies) {
					if (mummy.distanceTo(this) <= 64.0D && mummy.isSpawningFinished())
						mummies++;
				}
				//Max. 4 peat mummies
				if (mummies < 4 && this.untilSpawnMummy <= 0)
					this.spawnMummy();

				if (this.untilSpawnSludge <= 0)
					this.spawnSludge();
			}

			if (this.untilSpawnMummy > 0)
				this.untilSpawnMummy--;

			if (this.untilSpawnSludge > 0)
				this.untilSpawnSludge--;

			if (this.eatPreyTimer > 0 && this.currentEatPrey != null)
				this.eatPreyTimer--;

			if (this.eatPreyTimer <= 0) {
				this.setPrey(null);
				this.eatPreyTimer = 60;
			}
		}
	}

	@Override
	public void aiStep() {
		super.aiStep();
		this.bossInfo.setProgress(this.getHealth() / this.getMaxHealth());
	}

	private void spawnMummy() {
		PeatMummy mummy = new PeatMummy(EntityRegistry.PEAT_MUMMY.get(), this.level());
		mummy.setPos(this.getX() + (this.getRandom().nextInt(6) - 3), this.getY(), this.getZ() + (this.getRandom().nextInt(6) - 3));
		if (mummy.level().noCollision(mummy.getBoundingBox()) && mummy.level().getEntityCollisions(mummy, mummy.getBoundingBox()).isEmpty()) {
			this.untilSpawnMummy = SPAWN_MUMMY_COOLDOWN;
			mummy.setTarget(this.getTarget());
			mummy.setHealth(30);
			this.level().addFreshEntity(mummy);
			mummy.setCarryShimmerstone(false);
			mummy.setBossMummy(true);
			mummy.skipDropExperience();
			this.playSound(SoundRegistry.DREADFUL_PEAT_MUMMY_SCREAM.get());
			PacketDistributor.sendToPlayersNear((ServerLevel) this.level(), null, mummy.getX(), mummy.getY(), mummy.getZ(), 64, new SummonPeatMummyParticlesPacket(mummy.getId()));
		} else {
			//Try again the next tick
			this.untilSpawnMummy = 1;
		}
	}

	private void spawnSludge() {
		this.untilSpawnSludge = SPAWN_SLUDGE_COOLDOWN;
		if (this.getTarget() != null)
			this.lookAt(this.getTarget(), 360.0F, 360.0F);
		Vec3 look = this.getLookAngle();
		double direction = Math.toRadians(this.yBodyRot);
		SludgeBall sludge = new SludgeBall(this.level(), this, false);
		sludge.setPos(this.getX() - Math.sin(direction) * 3.5, this.getY() + this.getBbHeight(), this.getZ() + Math.cos(direction) * 3.5);
		sludge.setDeltaMovement(look.x * 0.5D, look.y, look.z * 0.5D);
		this.playSound(SoundRegistry.DREADFUL_PEAT_MUMMY_RETCH.get(), 1F, 0.7F + this.getRandom().nextFloat() * 0.6F);
		this.level().addFreshEntity(sludge);
	}

	private void spawnRangedSludge(double dx, double dy, double dz) {
		double direction = Math.toRadians(this.yBodyRot);
		SludgeBall sludge = new SludgeBall(this.level(), this, true);
		sludge.setPos(this.getX() - Math.sin(direction) * 0.75, this.getY() + 0.3f, this.getZ() + Math.cos(direction) * 0.75);
		sludge.setDeltaMovement(dx, dy, dz);
		this.level().addFreshEntity(sludge);
	}

	@Override
	public boolean doHurtTarget(Entity entity) {
		if ((this.getSpawningProgress() < 0.95F && !this.getEntityData().get(SPEW)) || this.getHealth() <= 0.0F) {
			return false;
		}

		boolean attacked = super.doHurtTarget(entity);
		if (attacked && this.isAlive() && this.getRandom().nextInt(6) == 0 && entity != this.currentEatPrey && entity instanceof LivingEntity living && !(entity instanceof Player player && player.isCreative()) && !this.level().isClientSide() && !this.getEntityData().get(SPEW)) {
			this.setPrey(living);
		}

		if (attacked) {
			this.playSound(SoundRegistry.DREADFUL_PEAT_MUMMY_SWIPE.get(), 1F, 0.7F + this.getRandom().nextFloat() * 0.6F);
		}

		return attacked;
	}

	private void updateEatPrey() {
		double direction = Math.toRadians(this.yBodyRot);
		this.currentEatPrey.moveTo(this.getX() - Math.sin(direction) * 1.7, this.getY() + 0.25, this.getZ() + Math.cos(direction) * 1.7, (float) (Math.toDegrees(direction) + 180), 0);
		this.currentEatPrey.setYRot(this.currentEatPrey.yHeadRot = this.currentEatPrey.yHeadRotO = this.currentEatPrey.yRotO = ((float) (Math.toDegrees(direction) + 180)));
		this.currentEatPrey.fallDistance = 0;
		if (this.tickCount % 10 == 0) {
			if (!this.level().isClientSide()) {
				this.currentEatPrey.hurt(this.damageSources().mobAttack(this), 3);
				this.playSound(SoundRegistry.DREADFUL_PEAT_MUMMY_BITE.get(), 1F, 0.7F + this.getRandom().nextFloat() * 0.6F);
				this.playSound(SoundRegistry.DREADFUL_PEAT_MUMMY_LICK.get(), 1F, 1F);
			}
		}
		if (!this.currentEatPrey.isAlive() && !this.level().isClientSide())
			this.setPrey(null);
	}

	private void setPrey(@Nullable LivingEntity prey) {
		if (prey == null) {
			this.getEntityData().set(PREY, -1);
		} else {
			this.getEntityData().set(PREY, prey.getId());
		}
	}

	@Nullable
	private LivingEntity getPrey() {
		int id = this.getEntityData().get(PREY);
		Entity prey = id >= 0 ? this.level().getEntity(id) : null;
		if (prey instanceof LivingEntity living)
			return living;
		return null;
	}

	@Override
	public boolean hurt(DamageSource source, float amount) {
		if (source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
			return super.hurt(source, amount);
		}

		if (source.is(BLDamageTagProvider.DREADFUL_PEAT_MUMMY_IMMUNE)) {
			return false;
		}

		if (this.currentEatPrey != null && source.getEntity() == this.currentEatPrey) {
			return false;
		}

		if (this.getSpawningProgress() < 0.95F && !this.getEntityData().get(SPEW)) {
			return false;
		}

		if (super.hurt(source, this.getEntityData().get(SPEW) ? amount * 0.25f : amount)) {
			if (!this.level().isClientSide()) {
				if (source.getEntity() instanceof LivingEntity living) {
					if (this.isTargetOutOfAttackRange(living)) {
						this.outOfRangeCounter += 60;
					} else if (this.isTargetInCloseRange(living)) {
						this.outOfRangeCounter = Math.max(0, this.outOfRangeCounter - 40);
					}
				}
			}

			return true;
		}

		return false;
	}

	@Override
	public boolean canUsePortal(boolean allowPassengers) {
		return false;
	}

	@Override
	protected void tickDeath() {
		this.bossInfo.setProgress(0);
		if (this.deathTicks == 0) {
			if (!this.level().isClientSide()) {
				this.playSound(this.getDeathSound());
			}
		}

		++this.deathTicks;

		if (!this.level().isClientSide()) {
			this.setPos(this.xo, this.yo, this.zo);
			this.setDeltaMovement(Vec3.ZERO);

			if (this.deathTicks > 40 && this.deathTicks % 5 == 0) {
				int xp = 100;
				while (xp > 0) {
					int dropXP = ExperienceOrb.getExperienceValue(xp);
					xp -= dropXP;
					this.level().addFreshEntity(new ExperienceOrb(this.level(), this.getX(), this.getY() + this.getBbHeight() / 2.0D, this.getZ(), dropXP));
				}
			}

			if (this.deathTicks == 80) {
				int xp = 1200;
				while (xp > 0) {
					int dropXP = ExperienceOrb.getExperienceValue(xp);
					xp -= dropXP;
					this.level().addFreshEntity(new ExperienceOrb(this.level(), this.getX(), this.getY() + this.getBbHeight() / 2.0D, this.getZ(), dropXP));
				}
			}

			if (this.deathTicks > 120) {
				this.discard();
			}
		}

		if (this.deathTicks > 80) {
			if (this.level().isClientSide() && this.deathTicks % 5 == 0) {
				for (int xo = -1; xo <= 1; xo++) {
					for (int zo = -1; zo <= 1; zo++) {
						int x = Mth.floor(this.getX()) + xo, y = Mth.floor(this.getY() - getYOffset() - 0.1D), z = Mth.floor(this.getZ()) + zo;
						BlockState state = this.level().getBlockState(new BlockPos(x, y, z));
						if (!state.isAir()) {
							double px = this.getX() + this.getRandom().nextDouble() - 0.5F;
							double py = this.getY() - this.getYOffset() + this.getRandom().nextDouble() * 0.2 + 0.075;
							double pz = this.getZ() + this.getRandom().nextDouble() - 0.5F;
							this.level().playSound(null, this.blockPosition(), state.getSoundType(this.level(), this.blockPosition().below(), null).getBreakSound(), SoundSource.BLOCKS, this.getRandom().nextFloat() * 0.3F + 0.3F, this.getRandom().nextFloat() * 0.15F + 0.7F);
							for (int i = 0, amount = this.getRandom().nextInt(20) + 10; i < amount; i++) {
								double ox = this.getRandom().nextDouble() * 0.1F - 0.05F;
								double oz = this.getRandom().nextDouble() * 0.1F - 0.05F;
								double motionX = this.getRandom().nextDouble() * 0.2 - 0.1;
								double motionY = this.getRandom().nextDouble() * 0.25 + 0.1;
								double motionZ = this.getRandom().nextDouble() * 0.2 - 0.1;
								this.level().addParticle(new BlockParticleOption(ParticleTypes.BLOCK, state), px + ox + xo, py, pz + oz + zo, motionX, motionY, motionZ);
							}
						}
					}
				}
			}
		}
	}

	@Override
	public void playerTouch(Player player) {
		if (this.isAlive()) {
			super.playerTouch(player);
		}
	}

	@Override
	public boolean isPushable() {
		return this.isAlive() && this.getSpawningProgress() >= 1 && super.isPushable();
	}

	@Override
	public boolean isPickable() {
		return this.isAlive();
	}

	@Override
	protected boolean isImmobile() {
		return this.isAlive() && super.isImmobile();
	}

	@Override
	public float getShakeIntensity(Entity viewer) {
		if (this.deathTicks > 0) {
			float dist = this.distanceTo(viewer);
			float screamMult = 1.0F - dist / 30.0F;
			if (dist >= 30.0F) {
				return 0.0F;
			}
			return (Mth.sin(this.deathTicks / 120.0F * Mth.PI) + 0.1F) * 0.15F * screamMult;
		} else {
			return 0.0F;
		}
	}

	@Override
	public boolean applyOffset(Entity view, float partialTicks) {
		if (this.currentEatPrey == view) {
			double direction = Math.toRadians(this.yBodyRotO + (this.yBodyRot - this.yBodyRotO) * partialTicks);
			view.setYRot(view.yRotO = (float) (Math.toDegrees(direction) + 180));
			view.setXRot(view.xRotO = 0);
			view.setYHeadRot((float) (Math.toDegrees(direction) + 180));
			return true;
		}
		return false;
	}

	@Override
	public void addAdditionalSaveData(CompoundTag tag) {
		super.addAdditionalSaveData(tag);
		tag.putInt("spawningState", this.getSpawningState());
		tag.putInt("deathTicks", this.deathTicks);
		tag.putFloat("previousYOffset", this.prevYOffset);
	}

	@Override
	public void readAdditionalSaveData(CompoundTag tag) {
		super.readAdditionalSaveData(tag);
		this.deathTicks = tag.getInt("deathTicks");
		this.setYOffset(tag.getFloat("previousYOffset"));
		this.getEntityData().set(SPAWNING_STATE_DW, tag.getInt("spawningState"));
		if (this.hasCustomName()) {
			this.bossInfo.setName(this.getDisplayName());
		}
	}

	@Override
	public void setCustomName(@Nullable Component name) {
		super.setCustomName(name);
		this.bossInfo.setName(this.getDisplayName());
	}

	@Override
	public void startSeenByPlayer(ServerPlayer player) {
		super.startSeenByPlayer(player);
		this.bossInfo.addPlayer(player);
	}

	@Override
	public void stopSeenByPlayer(ServerPlayer player) {
		super.stopSeenByPlayer(player);
		this.bossInfo.removePlayer(player);
	}

	@Override
	public float getWalkTargetValue(BlockPos pos) {
		return 0.5F;
	}

	@Override
	public SoundEvent getMusicFile(Player listener) {
		return SoundRegistry.DREADFUL_PEAT_MUMMY_LOOP.get();
	}

	@Override
	public double getMusicRange(Player listener) {
		return 32.0D;
	}

	@Override
	public boolean isMusicActive(Player listener) {
		return this.isAlive();
	}

	@Override
	public int getMusicLayer(Player listener) {
		return EntityMusicLayers.BOSS;
	}

	@Override
	public BetweenlandsServerBossBar getBar() {
		return this.bossInfo;
	}
}
