package thebetweenlands.common.entity.monster.spirit_tree;

import com.google.common.collect.ImmutableMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ItemParticleOption;
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
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.EventHooks;
import thebetweenlands.api.entity.EntityWithLootMultiplier;
import thebetweenlands.api.entity.bossbar.BetweenlandsBoss;
import thebetweenlands.api.entity.bossbar.BetweenlandsServerBossBar;
import thebetweenlands.api.storage.IWorldStorage;
import thebetweenlands.client.particle.ParticleFactory;
import thebetweenlands.client.particle.SpikeParticle;
import thebetweenlands.client.particle.options.SpikeParticleOptions;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.entity.RootGrabber;
import thebetweenlands.common.entity.SpikeWave;
import thebetweenlands.common.entity.boss.DreadfulPeatMummy;
import thebetweenlands.common.registries.*;
import thebetweenlands.common.world.gen.SurfaceType;
import thebetweenlands.common.world.gen.structure.SpiritTreePiece;
import thebetweenlands.common.world.storage.BetweenlandsWorldStorage;
import thebetweenlands.common.world.storage.SpiritTreeKillToken;
import thebetweenlands.common.world.storage.WorldStorageGetter;
import thebetweenlands.common.world.storage.location.LocationSpiritTree;
import thebetweenlands.util.BlockShapeUtils;

import javax.annotation.Nullable;
import java.util.*;

public class LargeSpiritTreeFace extends AbstractSpiritTreeFace implements EntityWithLootMultiplier, BetweenlandsBoss, Enemy {
	private final BetweenlandsServerBossBar bossInfo;

	public static final byte EVENT_BLOW_ATTACK = 40;

	private static final EntityDimensions ANCHORED_DIMENSIONS = EntityDimensions.scalable(1.8F, 1.8F);
	private static final EntityDimensions UNANCHORED_DIMENSIONS = EntityDimensions.scalable(1.8F, 0.2F);

	private static final EntityDataAccessor<Integer> BLOW_STATE = SynchedEntityData.defineId(LargeSpiritTreeFace.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<Integer> SPIT_STATE = SynchedEntityData.defineId(LargeSpiritTreeFace.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<Integer> ROTATING_WAVE_STATE = SynchedEntityData.defineId(LargeSpiritTreeFace.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<Integer> CRAWLING_WAVE_STATE = SynchedEntityData.defineId(LargeSpiritTreeFace.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<Float> WISP_STRENGTH_MODIFIER = SynchedEntityData.defineId(LargeSpiritTreeFace.class, EntityDataSerializers.FLOAT);
	private static final EntityDataAccessor<BlockPos> LAST_LOCKED_WISP = SynchedEntityData.defineId(LargeSpiritTreeFace.class, EntityDataSerializers.BLOCK_POS);
	private static final EntityDataAccessor<Optional<UUID>> BOSS_BAR_ID = SynchedEntityData.defineId(LargeSpiritTreeFace.class, EntityDataSerializers.OPTIONAL_UUID);

	private int blowTicks = 0;

	private float rotatingWaveStart = 0;
	private int rotatingWaveTicks = 0;

	protected static final int CRAWLING_WAVE_RANGE = 36;

	private float crawlingWaveAngle = 0;
	private int crawlingWaveTicks = 0;

	protected static final int DEFAULT_XP_DROPPED = 300;

	protected static final int DEFAULT_SPIT_DELAY = 10;
	protected static final int DEFAULT_BLOW_DELAY = 30;
	protected static final int DEFAULT_ROTATING_WAVE_DELAY = 40;
	protected static final int DEFAULT_CRAWLING_WAVE_DELAY = 40;

	protected int spitDelay = DEFAULT_SPIT_DELAY;
	protected int blowDelay = DEFAULT_BLOW_DELAY;
	protected int rotatingWaveDelay = DEFAULT_ROTATING_WAVE_DELAY;
	protected int crawlingWaveDelay = DEFAULT_CRAWLING_WAVE_DELAY;

	public LargeSpiritTreeFace(EntityType<? extends AbstractSpiritTreeFace> type, Level level) {
		super(type, level);
		this.xpReward = DEFAULT_XP_DROPPED;
		this.bossInfo  = new BetweenlandsServerBossBar(this.getDisplayName(), BossType.MINI_BOSS);
	}

	@Override
	protected void registerGoals() {
		this.goalSelector.addGoal(0, new SpiritTreeTrackTargetGoal(this));
		this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1, true));
		this.goalSelector.addGoal(2, new SpitGoal(this, 4.5F) {
			@Override
			protected float getSpitDamage() {
				return (float) LargeSpiritTreeFace.this.getAttributeValue(Attributes.ATTACK_DAMAGE) / 3.0F;
			}
		});
		this.goalSelector.addGoal(3, new BlowAttackGoal(this));
		this.goalSelector.addGoal(4, new RotatingWaveAttackGoal(this));
		this.goalSelector.addGoal(5, new CrawlingWaveAttackGoal(this));
		this.goalSelector.addGoal(6, new GrabAttackGoal(this));
		this.goalSelector.addGoal(7, new RespawnSmallFacesGoal(this));

		this.targetSelector.addGoal(0, new HurtByTargetGoal(this));
		this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, false));
	}

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder) {
		super.defineSynchedData(builder);

		builder.define(BLOW_STATE, 0);
		builder.define(SPIT_STATE, 0);
		builder.define(ROTATING_WAVE_STATE, 0);
		builder.define(CRAWLING_WAVE_STATE, 0);
		builder.define(WISP_STRENGTH_MODIFIER, 1.0F);
		builder.define(LAST_LOCKED_WISP, BlockPos.ZERO);
		builder.define(BOSS_BAR_ID, Optional.empty());
	}

	public static AttributeSupplier.Builder createAttributes() {
		return Mob.createMobAttributes()
			.add(Attributes.MAX_HEALTH, 600.0D)
			.add(Attributes.MOVEMENT_SPEED, 0.15D)
			.add(Attributes.ATTACK_DAMAGE, 12.0D)
			.add(Attributes.FOLLOW_RANGE, 48.0D);
	}

	@Nullable
	@Override
	public Map<String, Float> getLootModifiers(@Nullable LootContext context, boolean isEntityProperty) {
		ImmutableMap.Builder<String, Float> builder = ImmutableMap.builder();
		builder.put("strength", this.getWispStrengthModifier());
		return builder.build();
	}

	@Override
	public List<BlockPos> findNearbyBlocksForMovement() {
		IWorldStorage storage = WorldStorageGetter.getNullable(this.level());
		if (storage != null) {
			List<LocationSpiritTree> locations = storage.getLocalStorageHandler().getLocalStorages(this.level(), LocationSpiritTree.class, this.getBoundingBox(), loc -> loc.isInside(this));
			if (!locations.isEmpty()) {
				List<BlockPos> positions = new ArrayList<>(locations.getFirst().getLargeFacePositions());
				if (!positions.isEmpty()) {
					return positions;
				}
			}
		}
		return super.findNearbyBlocksForMovement();
	}

	protected List<BlockPos> findSmallFacesBlocks() {
		IWorldStorage storage = WorldStorageGetter.getNullable(this.level());
		if (storage != null) {
			List<LocationSpiritTree> locations = storage.getLocalStorageHandler().getLocalStorages(this.level(), LocationSpiritTree.class, this.getBoundingBox(), loc -> loc.isInside(this));
			if (!locations.isEmpty()) {
				List<BlockPos> positions = new ArrayList<>(locations.getFirst().getSmallFacePositions());
				if (!positions.isEmpty()) {
					return positions;
				}
			}
		}
		return this.findNearbyBlocksForMovement();
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundRegistry.SPIRIT_TREE_FACE_LARGE_DEATH.get();
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return SoundRegistry.SPIRIT_TREE_FACE_LARGE_LIVING.get();
	}

	@Override
	public void handleEntityEvent(byte id) {
		super.handleEntityEvent(id);

		if (id == EVENT_ATTACKED) {
			Vec3 frontCenter = this.getFrontCenter();
			for (int i = 0; i < 16; i++) {
				float rx = this.level().getRandom().nextFloat() * 2.0F - 1.0F;
				float ry = this.level().getRandom().nextFloat() * 2.0F - 1.0F;
				float rz = this.level().getRandom().nextFloat() * 2.0F - 1.0F;
				Vec3 vec = new Vec3(rx, ry, rz);
				vec = vec.normalize();
				this.level().addParticle(new BlockParticleOption(ParticleTypes.BLOCK, BlockRegistry.SPIRIT_TREE_LOG.get().defaultBlockState()), frontCenter.x + rx, frontCenter.y + ry, frontCenter.z + rz, vec.x * 1.5F, vec.y * 1.5F, vec.z * 1.5F);
			}
		} else if (id == EVENT_BLOW_ATTACK) {
			Vec3 frontCenter = this.getFrontCenter();
			for (int i = 0; i < 64; i++) {
				RandomSource rnd = this.level().getRandom();
				float rx = rnd.nextFloat() * 6.0F - 3.0F + this.getFacing().getStepX() * 2;
				float ry = rnd.nextFloat() * 6.0F - 3.0F + this.getFacing().getStepY() * 2;
				float rz = rnd.nextFloat() * 6.0F - 3.0F + this.getFacing().getStepZ() * 2;
				Vec3 vec = new Vec3(rx, ry, rz);
				vec = vec.normalize();
				TheBetweenlands.createParticle(new SpikeParticleOptions(SpikeParticle.ROOT_TEXTURE, this.getRandom().nextInt(15) == 0), this.level(), frontCenter.x, frontCenter.y - 0.25D, frontCenter.z, ParticleFactory.ParticleArgs.get().withMotion(vec.x * 0.45F, vec.y * 0.45F + 0.2F, vec.z * 0.45F));
			}
			frontCenter = this.getFrontCenter();
			for (int i = 0; i < 32; i++) {
				RandomSource rnd = level().getRandom();
				float rx = rnd.nextFloat() - 0.5F + this.getFacing().getStepX() * 0.5F;
				float ry = rnd.nextFloat() - 0.5F + this.getFacing().getStepY() * 0.5F;
				float rz = rnd.nextFloat() - 0.5F + this.getFacing().getStepZ() * 0.5F;
				Vec3 vec = new Vec3(rx, ry, rz);
				vec = vec.normalize();
				this.level().addParticle(new ItemParticleOption(ParticleTypes.ITEM, new ItemStack(ItemRegistry.SAP_SPIT.get())), frontCenter.x, frontCenter.y - 0.25D, frontCenter.z, vec.x * 0.25F, vec.y * 0.25F, vec.z * 0.25F);
			}
		}
	}

	@Override
	public boolean hurt(DamageSource source, float amount) {
		if (this.getWispStrengthModifier() > 1.0F) {
			amount /= 1.0F + (this.getWispStrengthModifier() - 1.0F) * 2.0F;
		}
		return super.hurt(source, amount);
	}

	@Override
	public void die(DamageSource cause) {
		super.die(cause);

		if (!this.level().isClientSide()) {
			BetweenlandsWorldStorage storage = WorldStorageGetter.getNullable(this.level());
			if (storage != null) {
				List<SpiritTreeKillToken> killTokens = storage.getSpiritTreeKillTokens();
				if (killTokens.size() > 32) {
					killTokens.removeFirst();
				}
				killTokens.add(new SpiritTreeKillToken(this.blockPosition(), this.getWispStrengthModifier()));

				List<LocationSpiritTree> locations = storage.getLocalStorageHandler().getLocalStorages(this.level(), LocationSpiritTree.class, this.getBoundingBox(), loc -> loc.isInside(this));
				if (!locations.isEmpty()) {
					LocationSpiritTree location = locations.getFirst();

					List<SmallSpiritTreeFace> smallFaces = this.level().getEntitiesOfClass(SmallSpiritTreeFace.class, location.getEnclosingBounds());
					for (SmallSpiritTreeFace face : smallFaces) {
						face.discard();
					}

					List<BlockPos> wispPositions = new ArrayList<>();
					wispPositions.addAll(location.getGeneratedWispPositions());
					wispPositions.addAll(location.getNotGeneratedWispPositions());
					for (BlockPos wisp : wispPositions) {
						if (this.level().getBlockState(wisp).is(BlockRegistry.WISP)) {
							this.level().removeBlock(wisp, false);
						}
					}

					List<BlockPos> positions = location.getLargeFacePositions();
					BlockPos lowest = null;
					for (BlockPos pos : positions) {
						if (lowest == null || lowest.getY() > pos.getY()) {
							lowest = pos;
						}
					}
					if (lowest != null) {
						int radius = 5;
						for (int xo = -radius; xo <= radius; xo++) {
							for (int yo = -radius; yo <= radius; yo++) {
								for (int zo = -radius; zo <= radius; zo++) {
									if (xo * xo + yo * yo + zo * zo <= radius * radius) {
										BlockPos pos = lowest.offset(xo, yo, zo);
										BlockState state = this.level().getBlockState(pos);

										if (SurfaceType.GRASS_AND_DIRT.matches(state) && !this.level().getBlockState(pos.above()).isRedstoneConductor(this.level(), pos) && this.level().getRandom().nextInt(3) == 0) {
											this.level().setBlockAndUpdate(pos, BlockRegistry.SPREADING_SLUDGY_DIRT.get().defaultBlockState());
										}

										if (state.is(BlockRegistry.SPIRIT_TREE_LOG) && this.level().getRandom().nextInt(5) == 0) {
											this.level().setBlockAndUpdate(pos, BlockRegistry.SPREADING_ROTTEN_BARK.get().withPropertiesOf(state));
										}
									}
								}
							}
						}
					}

					for (BlockPos pos : location.getSmallFacePositions()) {
						BlockState state = this.level().getBlockState(pos);
						if (state.is(BlockRegistry.SPIRIT_TREE_LOG) && this.level().getRandom().nextInt(10) == 0) {
							this.level().setBlockAndUpdate(pos, BlockRegistry.SPREADING_ROTTEN_BARK.get().withPropertiesOf(state));
						}
					}

					location.getGuard().clear(this.level());
					location.setVisible(false);
					location.setDirty(true);
				}
			}
		}
	}

	@Override
	public boolean isAttacking() {
		return super.isAttacking() || this.blowTicks > 0;
	}

	@Override
	protected void playSpitSound() {
		this.playSound(SoundRegistry.SPIRIT_TREE_FACE_LARGE_SPIT.get(), 1, 0.8F + this.getRandom().nextFloat() * 0.3F);
	}

	@Override
	protected void playEmergeSound() {
		this.playSound(SoundRegistry.SPIRIT_TREE_FACE_LARGE_EMERGE.get(), 1, 0.8F + this.getRandom().nextFloat() * 0.3F);
	}

	@Override
	protected EntityDimensions getDefaultDimensions(Pose pose) {
		return this.isAnchored() ? ANCHORED_DIMENSIONS : UNANCHORED_DIMENSIONS;
	}

	@Override
	public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
		super.onSyncedDataUpdated(key);

		if (ANCHORED.equals(key)) {
			this.refreshDimensions();
		}

		if (LAST_LOCKED_WISP.equals(key) && this.level().isClientSide()) {
			this.wispLockEffect(this.getEntityData().get(LAST_LOCKED_WISP));
		}
	}

	protected void wispLockEffect(BlockPos pos) {
		for (int i = 0; i < 64; i++) {
			Vec3 dir = new Vec3(this.getRandom().nextFloat() - 0.5F, this.getRandom().nextFloat() - 0.5F + 0.25F, this.getRandom().nextFloat() - 0.5F);
			dir = dir.normalize().scale(2);
			this.level().addParticle(ParticleRegistry.CORRUPTED.get(), pos.getX() + 0.5D + this.getRandom().nextFloat() / 2.0F - 0.25F, pos.getY() + 0.5D + this.getRandom().nextFloat() / 2.0F - 0.25F, pos.getZ() + 0.5D + this.getRandom().nextFloat() / 2.0F - 0.25F, dir.x, dir.y, dir.z);
		}
		this.level().playSound(null, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, SoundRegistry.DAMAGE_REDUCTION.get(), SoundSource.HOSTILE, 0.65F, 0.5F);
	}

	@Override
	public boolean isActive() {
		return this.level().getDifficulty() != Difficulty.PEACEFUL;
	}

	@Override
	protected void customServerAiStep() {
		super.customServerAiStep();
		this.bossInfo.setProgress(this.getHealth() / this.getMaxHealth());
		if (this.bossInfo.getId() != this.getBossBarId()) {
			this.getEntityData().set(BOSS_BAR_ID, Optional.of(this.bossInfo.getId()));
		}
	}

	@Override
	protected void tickDeath() {
		super.tickDeath();
		this.bossInfo.setProgress(0.0F);
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

	@Nullable
	@Override
	public UUID getBossBarId() {
		return this.getEntityData().get(BOSS_BAR_ID).orElse(null);
	}

	@Override
	public void tick() {
		super.tick();

		if (this.getEntityData().get(BLOW_STATE) != 0 || this.getEntityData().get(ROTATING_WAVE_STATE) != 0 || this.getEntityData().get(CRAWLING_WAVE_STATE) != 0 || this.getEntityData().get(SPIT_STATE) != 0) {
			this.setGlowTicks(20);
		}

		if (!this.level().isClientSide()) {
			if (this.isActive() && this.isAlive() && this.getTarget() != null) {
				if (this.tickCount % 20 == 0) {
					this.updateWispStrengthModifier();
				}

				if (this.tickCount % 10 == 0) {
					BetweenlandsWorldStorage storage = WorldStorageGetter.getNullable(this.level());
					if (storage != null) {
						List<LocationSpiritTree> locations = storage.getLocalStorageHandler().getLocalStorages(this.level(), LocationSpiritTree.class, this.getBoundingBox(), loc -> loc.isInside(this));
						if (!locations.isEmpty()) {
							LocationSpiritTree location = locations.getFirst();

							List<BlockPos> wispPositions = new ArrayList<>(location.getNotGeneratedWispPositions());

							if (this.getHealth() < this.getMaxHealth() / 2) {
								wispPositions.addAll(location.getGeneratedWispPositions());
							}

							for (BlockPos wispPosition : wispPositions) {
								if (!location.getGuard().isGuarded(this.level(), null, wispPosition) && this.level().getBlockState(wispPosition).is(BlockRegistry.WISP)) {
									location.getGuard().setGuarded(this.level(), wispPosition, true);
									this.getEntityData().set(LAST_LOCKED_WISP, wispPosition);
									break;
								}
							}
						}
					}
				}
			}

			float strengthModifier = this.getWispStrengthModifier();
			if (strengthModifier <= 1.0F) {
				this.xpReward = (int) (DEFAULT_XP_DROPPED * strengthModifier);
			} else {
				this.xpReward = (int) (DEFAULT_XP_DROPPED * (1 + (strengthModifier - 1) * 6));
			}

			AttributeInstance attackAttribute = this.getAttribute(Attributes.ATTACK_DAMAGE);
			attackAttribute.addOrReplacePermanentModifier(new AttributeModifier(TheBetweenlands.prefix("wisp_strength"), strengthModifier - 1.0F, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));

			if (this.blowTicks > 0) {
				if (this.blowTicks > 20 + this.blowDelay) {
					this.getEntityData().set(BLOW_STATE, 3);

					if ((this.blowTicks - (21 + this.blowDelay)) % 15 == 0) {
						this.doBlowAttack();
						this.level().broadcastEntityEvent(this, EVENT_BLOW_ATTACK);
						this.playSound(SoundRegistry.SPIRIT_TREE_FACE_SPIT_ROOT_SPIKES.get(), 1, 0.9F + this.getRandom().nextFloat() * 0.2F);
					}
				} else {
					if (this.blowTicks > this.blowDelay) {
						this.getEntityData().set(BLOW_STATE, 2);
						if (this.blowTicks == this.blowDelay + 1) {
							this.playSound(SoundRegistry.SPIRIT_TREE_FACE_SUCK.get(), 1, 1);
						}
					} else {
						this.getEntityData().set(BLOW_STATE, 1);
					}
				}

				if (this.blowTicks > 160 + this.blowDelay) {
					this.getEntityData().set(BLOW_STATE, 0);
					this.blowTicks = 0;
				} else {
					this.blowTicks++;
				}
			}

			if (this.rotatingWaveTicks > 0) {
				if (this.rotatingWaveTicks > this.rotatingWaveDelay) {
					this.getEntityData().set(ROTATING_WAVE_STATE, 2);

					if ((this.rotatingWaveTicks - 1 - this.rotatingWaveDelay) % 3 == 0) {
						for (int i = 0; i < 2; i++) {
							double increment = Math.PI * 2 / 20;

							double a1 = (this.rotatingWaveStart + (this.rotatingWaveTicks - this.rotatingWaveDelay) / 3.0F * increment) * (i == 0 ? 1 : -1);
							double a2 = (this.rotatingWaveStart + ((this.rotatingWaveTicks - this.rotatingWaveDelay) / 3.0F + 1) * increment) * (i == 0 ? 1 : -1);

							double start = Math.min(a1, a2);
							double end = Math.max(a1, a2);

							List<BlockPos> blocks = BlockShapeUtils.getRingSegment(this.getAnchor(), start, end, SpiritTreePiece.RADIUS_INNER_CIRCLE + 0.5D, SpiritTreePiece.RADIUS_OUTER_CIRCLE + 0.5D, false, new ArrayList<>());

							List<BlockPos> spawnBlocks = new ArrayList<>();

							for (BlockPos pos : blocks) {
								BlockPos spawnPos = this.getWaveGroundPos(pos, 0);

								if (spawnPos != null) {
									spawnBlocks.add(spawnPos);
								}
							}

							if (!spawnBlocks.isEmpty()) {
								SpikeWave spikeWave = new SpikeWave(EntityRegistry.SPIKE_WAVE.get(), this.level());
								spikeWave.delay = 2;
								spikeWave.setAttackDamage(10.0F * this.getWispStrengthModifier());
								for (BlockPos pos : spawnBlocks) {
									spikeWave.addPosition(pos);
								}
								this.level().addFreshEntity(spikeWave);
							}
						}
					}
				} else {
					this.getEntityData().set(ROTATING_WAVE_STATE, 1);
				}

				if (this.rotatingWaveTicks >= 3 * 20 * 3 + this.rotatingWaveDelay) {
					this.rotatingWaveTicks = 0;
					this.getEntityData().set(ROTATING_WAVE_STATE, 0);
				} else {
					this.rotatingWaveTicks++;
				}
			}

			if (this.crawlingWaveTicks > 0) {
				final int ticksPerWave = CRAWLING_WAVE_RANGE / 3 * 4;

				if (this.crawlingWaveTicks > this.crawlingWaveDelay) {
					this.getEntityData().set(CRAWLING_WAVE_STATE, 2);

					if (this.getTarget() != null) {
						if ((this.crawlingWaveTicks - 1 - this.crawlingWaveDelay) % ticksPerWave == 0) {
							this.crawlingWaveAngle = (float) Math.atan2(this.getTarget().getZ() - this.getZ(), this.getTarget().getX() - this.getX());
						}

						if ((this.crawlingWaveTicks - 1 - this.crawlingWaveDelay) % 4 == 0) {
							int dist = SpiritTreePiece.RADIUS_OUTER_CIRCLE + ((this.crawlingWaveTicks - 1 - this.crawlingWaveDelay) / 4 * 3) % CRAWLING_WAVE_RANGE;

							double a1 = this.crawlingWaveAngle - Math.PI / 16;
							double a2 = this.crawlingWaveAngle + Math.PI / 16;

							List<BlockPos> blocks = BlockShapeUtils.getRingSegment(this.getAnchor(), a1, a2, dist, dist + 3, false, new ArrayList<>());

							List<BlockPos> spawnBlocks = new ArrayList<>();

							for (BlockPos pos : blocks) {
								BlockPos spawnPos = this.getWaveGroundPos(pos, 4);

								if (spawnPos != null) {
									spawnBlocks.add(spawnPos);
								}
							}

							if (!spawnBlocks.isEmpty()) {
								SpikeWave spikeWave = new SpikeWave(EntityRegistry.SPIKE_WAVE.get(), this.level());
								spikeWave.delay = 2;
								spikeWave.setAttackDamage(10.0F * this.getWispStrengthModifier());
								for (BlockPos pos : spawnBlocks) {
									spikeWave.addPosition(pos);
								}
								this.level().addFreshEntity(spikeWave);
							}
						}
					}
				} else {
					this.getEntityData().set(CRAWLING_WAVE_STATE, 1);
				}

				if (this.crawlingWaveTicks >= ticksPerWave * 3 + this.crawlingWaveDelay) {
					this.crawlingWaveTicks = 0;
					this.getEntityData().set(CRAWLING_WAVE_STATE, 0);
				} else {
					this.crawlingWaveTicks++;
				}
			}
		} else {
			int blowState = this.getEntityData().get(BLOW_STATE);
			if (blowState == 2) {
				Vec3 frontCenter = this.getFrontCenter();
				for (int i = 0; i < 4; i++) {
					RandomSource rnd = this.level().getRandom();
					float rx = rnd.nextFloat() * 4.0F - 2.0F + this.getFacing().getStepX() * 4;
					float ry = rnd.nextFloat() * 4.0F - 2.0F + this.getFacing().getStepY() * 4;
					float rz = rnd.nextFloat() * 4.0F - 2.0F + this.getFacing().getStepZ() * 4;
					Vec3 vec = new Vec3(rx, ry, rz);
					vec = vec.normalize();
					this.level().addParticle(ParticleTypes.SMOKE, frontCenter.x + rx, frontCenter.y - 0.75D + ry, frontCenter.z + rz, -vec.x * 0.5F, -vec.y * 0.5F, -vec.z * 0.5F);
				}
			}
		}
	}

	@Override
	public void addAdditionalSaveData(CompoundTag tag) {
		super.addAdditionalSaveData(tag);
		tag.putFloat("wisp_strength", this.getWispStrengthModifier());
	}

	@Override
	public void readAdditionalSaveData(CompoundTag tag) {
		super.readAdditionalSaveData(tag);
		this.getEntityData().set(WISP_STRENGTH_MODIFIER, tag.getFloat("wisp_strength"));

		if (this.hasCustomName()) {
			this.bossInfo.setName(this.getDisplayName());
		}
	}

	@Nullable
	protected BlockPos getWaveGroundPos(BlockPos pos, int yOff) {
		BlockPos.MutableBlockPos checkPos = new BlockPos.MutableBlockPos();

		for (int yo = 0; yo < 16; yo++) {
			checkPos.set(pos.getX(), pos.getY() - yo + yOff, pos.getZ());

			BlockState state = this.level().getBlockState(checkPos);

			if (!state.getCollisionShape(this.level(), checkPos).isEmpty()) {
				if (Block.isFaceFull(state.getCollisionShape(this.level(), checkPos), Direction.UP)) {
					return checkPos.immutable();
				}
				break;
			}
		}

		return null;
	}

	public boolean isTargetInBlowRange(LivingEntity target) {
		if (target.distanceToSqr(this) <= 5 * 5) {
			Vec3 center = this.getFrontCenter();
			Vec3 targetCenter = new Vec3(target.getX(), target.getEyeY(), target.getZ());
			Vec3 dir = targetCenter.subtract(center).normalize();
			Vec3 facing = new Vec3(this.getFacing().getStepX(), this.getFacing().getStepY(), this.getFacing().getStepZ());
			float angle = (float) (Math.acos(facing.dot(dir)) / 2 / Mth.PI) * 360.0F;
			return angle >= 0.0F && angle <= 90.0F;
		}
		return false;
	}

	public void doBlowAttack() {
		Vec3 frontCenter = this.getFrontCenter();
		List<LivingEntity> targets = this.level().getEntitiesOfClass(LivingEntity.class, new AABB(frontCenter.x - 5, frontCenter.y - 5, frontCenter.z - 5, frontCenter.x + 5, frontCenter.y + 5, frontCenter.z + 5));
		for (LivingEntity target : targets) {
			if (target != this && this.isTargetInBlowRange(target)) {
				this.doHurtTarget(target);
			}
		}
	}

	public void startBlowAttack() {
		this.blowTicks = 1;
		if (this.getWispStrengthModifier() > 1.0F) {
			this.blowDelay = (int) (DEFAULT_BLOW_DELAY / (1.0F + (this.getWispStrengthModifier() - 1.0F) * 2.0F));
		} else {
			this.blowDelay = DEFAULT_BLOW_DELAY;
		}
	}

	public boolean isTargetInRotatingWaveAttackRange(LivingEntity target) {
		double dx = this.getX() - target.getX();
		double dz = this.getZ() - target.getZ();
		double dstSq = dx * dx + dz * dz;
		int innerSq = SpiritTreePiece.RADIUS_INNER_CIRCLE * SpiritTreePiece.RADIUS_INNER_CIRCLE;
		int outerSq = SpiritTreePiece.RADIUS_OUTER_CIRCLE * SpiritTreePiece.RADIUS_OUTER_CIRCLE;
		return dstSq >= innerSq && dstSq <= outerSq;
	}

	public void startRotatingWaveAttack() {
		this.rotatingWaveTicks = 1;
		this.rotatingWaveStart = this.getRandom().nextFloat() * Mth.TWO_PI;
		if (this.getWispStrengthModifier() > 1.0F) {
			this.rotatingWaveDelay = (int) (DEFAULT_ROTATING_WAVE_DELAY / (1.0F + (this.getWispStrengthModifier() - 1.0F) * 2.0F));
		} else {
			this.rotatingWaveDelay = DEFAULT_ROTATING_WAVE_DELAY;
		}
		this.getEntityData().set(ROTATING_WAVE_STATE, 1);
	}

	public boolean isTargetInCrawlingWaveAttackRange(LivingEntity target) {
		double dx = this.getX() - target.getX();
		double dz = this.getZ() - target.getZ();
		double dstSq = dx * dx + dz * dz;
		int innerSq = SpiritTreePiece.RADIUS_OUTER_CIRCLE * SpiritTreePiece.RADIUS_OUTER_CIRCLE;
		int outerSq = (SpiritTreePiece.RADIUS_OUTER_CIRCLE + CRAWLING_WAVE_RANGE) * (SpiritTreePiece.RADIUS_OUTER_CIRCLE + CRAWLING_WAVE_RANGE);
		return dstSq >= innerSq && dstSq <= outerSq;
	}

	public void startCrawlingWaveAttack() {
		this.crawlingWaveTicks = 1;
		if (this.getWispStrengthModifier() > 1.0F) {
			this.crawlingWaveDelay = (int) (DEFAULT_CRAWLING_WAVE_DELAY / (1.0F + (this.getWispStrengthModifier() - 1.0F) * 2.0F));
		} else {
			this.crawlingWaveDelay = DEFAULT_CRAWLING_WAVE_DELAY;
		}
		this.getEntityData().set(CRAWLING_WAVE_STATE, 1);
	}

	public boolean isTargetInGrabAttackRange(LivingEntity target) {
		double dx = this.getX() - target.getX();
		double dz = this.getZ() - target.getZ();
		double dstSq = dx * dx + dz * dz;
		int outerSq = (SpiritTreePiece.RADIUS_OUTER_CIRCLE + CRAWLING_WAVE_RANGE) * (SpiritTreePiece.RADIUS_OUTER_CIRCLE + CRAWLING_WAVE_RANGE);
		return dstSq <= outerSq;
	}

	public boolean startGrabAttack() {
		if (this.getTarget() != null) {
			LivingEntity target = this.getTarget();

			for (int i = 0; i < 6; i++) {
				BlockPos pos = BlockPos.containing(target.getX() + this.getRandom().nextInt(3) - 1, target.getY() - 1, target.getZ() + this.getRandom().nextInt(3) - 1);

				if (this.level().isEmptyBlock(pos.above()) && this.level().isEmptyBlock(pos.above(2))) {
					boolean validPos = true;
					for (int xo = -1; xo <= 1; xo++) {
						for (int zo = -1; zo <= 1; zo++) {
							if (!this.level().getBlockState(pos.offset(xo, 0, zo)).isRedstoneConductor(this.level(), pos.offset(xo, 0, zo))) {
								validPos = false;
							}
						}
					}

					if (validPos) {
						RootGrabber grabber = new RootGrabber(EntityRegistry.ROOT_GRABBER.get(), this.level());
						grabber.setPosAndDelay(pos, (int) (40 / this.getWispStrengthModifier()));
						this.level().addFreshEntity(grabber);
						return true;
					}
				}
			}
		}

		return false;
	}

	@Override
	public void startSpit(float spitDamage) {
		super.startSpit(spitDamage);
		if (this.getWispStrengthModifier() > 1.0F) {
			this.spitDelay = (int) (DEFAULT_SPIT_DELAY / (1.0F + (this.getWispStrengthModifier() - 1.0F) * 2.0F));
		} else {
			this.spitDelay = DEFAULT_SPIT_DELAY;
		}
		this.getEntityData().set(SPIT_STATE, 1);
	}

	@Override
	protected void updateSpitAttack() {
		if (this.spitTicks == this.spitDelay) {
			this.level().broadcastEntityEvent(this, EVENT_SPIT);
			this.setGlowTicks(10);
			this.playSpitSound();
		}

		if (this.spitTicks > 6 + this.spitDelay) {
			this.doSpitAttack();
			this.getEntityData().set(SPIT_STATE, 0);
			this.spitTicks = 0;
		} else {
			this.spitTicks++;
		}
	}

	protected void updateWispStrengthModifier() {
		BetweenlandsWorldStorage storage = WorldStorageGetter.getNullable(this.level());
		if (storage != null) {
			List<LocationSpiritTree> locations = storage.getLocalStorageHandler().getLocalStorages(this.level(), LocationSpiritTree.class, this.getBoundingBox(), loc -> loc.isInside(this));
			if (!locations.isEmpty()) {
				LocationSpiritTree location = locations.getFirst();

				int activeWisps = location.getActiveWisps(this.level());
				int generatedWisps = location.getGeneratedWispPositions().size();

				float newModifier;
				if (activeWisps < generatedWisps) {
					newModifier = 0.5F + activeWisps / (float) generatedWisps * 0.5F;
				} else {
					newModifier = Math.min(1.0F + (activeWisps - generatedWisps) / 6.0F, 2.0F);
				}

				float decay = (float) Math.pow(this.getHealth() / this.getMaxHealth(), 6) * 0.33F;

				this.getEntityData().set(WISP_STRENGTH_MODIFIER, decay * newModifier + (1 - decay) * this.getWispStrengthModifier());
			}
		} else {
			this.getEntityData().set(WISP_STRENGTH_MODIFIER, 1.0F);
		}
	}

	public float getWispStrengthModifier() {
		return this.getEntityData().get(WISP_STRENGTH_MODIFIER);
	}

	@Override
	public Vec3 getMiniBossTagOffset(float partialTicks) {
		return new Vec3(this.getFacing().getStepX() * (this.getBbWidth() / 2), this.getBbHeight() + 0.5D, this.getFacing().getStepZ() * (this.getBbWidth() / 2));
	}

	public static class RespawnSmallFacesGoal extends Goal {
		protected final LargeSpiritTreeFace entity;

		protected int executeCheckCooldown = 0;

		protected boolean shouldContinue = true;

		protected int spawnCooldown = 0;

		public RespawnSmallFacesGoal(LargeSpiritTreeFace entity) {
			this.entity = entity;
		}

		protected int countSmallFaces() {
			return this.entity.level().getEntitiesOfClass(SmallSpiritTreeFace.class, this.entity.getBoundingBox().inflate(40.0D)).size();
		}

		protected boolean hasEnoughSmallFaces() {
			return this.countSmallFaces() >= 8;
		}

		@Override
		public boolean canUse() {
			if (this.entity.isAlive() && this.entity.isActive()) {
				if (this.executeCheckCooldown <= 0) {
					this.executeCheckCooldown = 20 + this.entity.getRandom().nextInt(20);
					return !this.hasEnoughSmallFaces();
				}
				this.executeCheckCooldown--;
			}
			return false;
		}

		@Override
		public void start() {
			this.shouldContinue = true;
		}

		@Override
		public void tick() {
			if (this.spawnCooldown <= 0) {
				this.spawnCooldown = (int) ((180 + this.entity.getRandom().nextInt(100)) / this.entity.getWispStrengthModifier());
				List<BlockPos> blocks = this.entity.findSmallFacesBlocks();

				if (!blocks.isEmpty()) {
					SmallSpiritTreeFace face = new SmallSpiritTreeFace(EntityRegistry.SMALL_SPIRIT_TREE_FACE.get(), this.entity.level());

					spawnLoop:
					for (int i = 0; i < 16; i++) {
						BlockPos anchor = blocks.get(this.entity.getRandom().nextInt(blocks.size()));

						List<Direction> facings = new ArrayList<>(Arrays.asList(Direction.values()));
						Collections.shuffle(facings);

						for (Direction facing : facings) {
							Direction facingUp = facing.getAxis().isVertical() ? Direction.Plane.HORIZONTAL.getRandomDirection(this.entity.getRandom()) : Direction.UP;
							if (face.checkAnchorAt(anchor, facing, facingUp, AnchorChecks.ALL) == 0) {
								EventHooks.finalizeMobSpawn(face, (ServerLevel)this.entity.level(), this.entity.level().getCurrentDifficultyAt(anchor), MobSpawnType.EVENT, null);
								face.setPositionToAnchor(anchor, facing, facingUp);
								this.entity.level().addFreshEntity(face);
								break spawnLoop;
							}
						}
					}

					if (this.hasEnoughSmallFaces()) {
						this.shouldContinue = false;
					}
				}
			}
			this.spawnCooldown--;
		}

		@Override
		public boolean canContinueToUse() {
			return this.entity.isActive() && this.shouldContinue;
		}
	}

	public static class BlowAttackGoal extends Goal {
		protected final LargeSpiritTreeFace entity;

		protected int cooldown = 30;

		public BlowAttackGoal(LargeSpiritTreeFace entity) {
			this.entity = entity;
		}

		@Override
		public boolean canUse() {
			if (this.entity.isActive() && !this.entity.isAttacking() && this.entity.getTarget() != null && this.entity.isTargetInBlowRange(this.entity.getTarget())) {
				if (this.cooldown <= 0) {
					this.cooldown = (int) ((30 + this.entity.getRandom().nextInt(30)) / this.entity.getWispStrengthModifier());
					return true;
				}
				this.cooldown--;
			}
			return false;
		}

		@Override
		public void start() {
			this.entity.startBlowAttack();
		}

		@Override
		public boolean canContinueToUse() {
			return false;
		}
	}

	public static class RotatingWaveAttackGoal extends Goal {
		protected final LargeSpiritTreeFace entity;

		protected int cooldown = 30;

		public RotatingWaveAttackGoal(LargeSpiritTreeFace entity) {
			this.entity = entity;
		}

		@Override
		public boolean canUse() {
			if (this.entity.isActive() && this.entity.rotatingWaveTicks == 0 && this.entity.getTarget() != null && this.entity.isTargetInRotatingWaveAttackRange(this.entity.getTarget())) {
				if (this.cooldown <= 0) {
					this.cooldown = (int) ((60 + this.entity.getRandom().nextInt(80)) / this.entity.getWispStrengthModifier());
					return true;
				}
				this.cooldown--;
			}
			return false;
		}

		@Override
		public void start() {
			this.entity.startRotatingWaveAttack();
		}

		@Override
		public boolean canContinueToUse() {
			return false;
		}
	}

	public static class CrawlingWaveAttackGoal extends Goal {
		protected final LargeSpiritTreeFace entity;

		protected int cooldown = 30;

		public CrawlingWaveAttackGoal(LargeSpiritTreeFace entity) {
			this.entity = entity;
		}

		@Override
		public boolean canUse() {
			if (this.entity.isActive() && this.entity.crawlingWaveTicks == 0 && this.entity.getTarget() != null && this.entity.isTargetInCrawlingWaveAttackRange(this.entity.getTarget())) {
				if (this.cooldown <= 0) {
					this.cooldown = (int) ((60 + this.entity.getRandom().nextInt(80)) / this.entity.getWispStrengthModifier());
					return true;
				}
				this.cooldown--;
			}
			return false;
		}

		@Override
		public void start() {
			this.entity.startCrawlingWaveAttack();
		}

		@Override
		public boolean canContinueToUse() {
			return false;
		}
	}

	public static class GrabAttackGoal extends Goal {
		protected final LargeSpiritTreeFace entity;

		protected int cooldown = 30;

		public GrabAttackGoal(LargeSpiritTreeFace entity) {
			this.entity = entity;
		}

		@Override
		public boolean canUse() {
			if (this.entity.isActive() && !this.entity.isAttacking() && this.entity.getTarget() != null && this.entity.getTarget().onGround() && this.entity.isTargetInGrabAttackRange(this.entity.getTarget())) {
				if (this.cooldown <= 0) {
					this.cooldown = (int) ((60 + this.entity.getRandom().nextInt(80)) / this.entity.getWispStrengthModifier());
					return true;
				}
				this.cooldown--;
			}
			return false;
		}

		@Override
		public void start() {
			this.entity.startGrabAttack();
		}

		@Override
		public boolean canContinueToUse() {
			return false;
		}
	}
}
