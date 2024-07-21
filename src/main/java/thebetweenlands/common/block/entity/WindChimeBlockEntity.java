package thebetweenlands.common.block.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import thebetweenlands.api.environment.IEnvironmentEvent;
import thebetweenlands.api.environment.IPredictableEnvironmentEvent;
import thebetweenlands.common.registries.BlockEntityRegistry;
import thebetweenlands.common.registries.SoundRegistry;
import thebetweenlands.common.world.event.BLEnvironmentEventRegistry;
import thebetweenlands.common.world.storage.BetweenlandsWorldStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class WindChimeBlockEntity extends SyncedBlockEntity {

	public int renderTicks;

	public int ticksUntilChimes;
	public int prevChimeTicks;
	public int chimeTicks;

	@Nullable
	private ParticleBatch particleBatch;

	private int fadeOutTimer = 0;

	@Nullable
	private IPredictableEnvironmentEvent predictedEvent;
	private int predictedTimeUntilActivation;
	@Nullable
	private ResourceLocation predictedEventVision;
	@Nullable
	private ResourceLocation attunedEvent;

	public WindChimeBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityRegistry.WIND_CHIME.get(), pos, state);
	}

	public static void tick(Level level, BlockPos pos, BlockState state, WindChimeBlockEntity entity) {
		if (level.isClientSide()) {
			entity.renderTicks++;

			if (entity.ticksUntilChimes > 0) {
				entity.ticksUntilChimes--;

				if (entity.ticksUntilChimes <= 4) {
					entity.chimeTicks += 25;
				}
			}

			entity.prevChimeTicks = entity.chimeTicks;
			entity.chimeTicks = Math.max(entity.chimeTicks - 1, 0);
		}

		BLEnvironmentEventRegistry registry = BetweenlandsWorldStorage.forWorld(level).getEnvironmentEventRegistry();

		int maxPredictionTime = entity.getMaxPredictionTime();

		int nextPrediction = Integer.MAX_VALUE;
		IPredictableEnvironmentEvent nextEvent = null;
		ResourceLocation[] nextEventVisions = null;

		for (IEnvironmentEvent event : registry.getEvents().values()) {
			if (event instanceof IPredictableEnvironmentEvent predictable && (entity.attunedEvent == null || entity.attunedEvent.equals(event.getEventName()))) {

				ResourceLocation[] visions = predictable.getVisionTextures();

				if (visions != null) {
					int prediction = predictable.estimateTimeUntil(IPredictableEnvironmentEvent.State.ACTIVE);

					if (prediction > 0 && prediction < nextPrediction && prediction < maxPredictionTime) {
						nextPrediction = prediction;
						nextEvent = predictable;
						nextEventVisions = visions;
					}
				}
			}
		}

		if (!level.isClientSide()) {
			if (entity.predictedEvent != null && entity.predictedEvent != nextEvent) {
				entity.predictedEvent = nextEvent;
				entity.predictedTimeUntilActivation = -1;
			} else if (entity.predictedEvent == null) {
				entity.predictedEvent = nextEvent;
			}

			if (nextEvent != null) {
				if (entity.predictedTimeUntilActivation == -1 || entity.predictedTimeUntilActivation >= maxPredictionTime) {
					entity.triggerAdvancement(level, pos);
				} else if (entity.predictedTimeUntilActivation >= maxPredictionTime * 0.4f && nextPrediction < maxPredictionTime * 0.4f) {
					entity.triggerAdvancement(level, pos);
				} else if (entity.predictedTimeUntilActivation >= maxPredictionTime * 0.2f && nextPrediction < maxPredictionTime * 0.2f) {
					entity.triggerAdvancement(level, pos);
				}

				entity.predictedTimeUntilActivation = nextPrediction;
			} else {
				entity.predictedTimeUntilActivation = -1;
			}
		} else {
			entity.updateParticles(level, pos, maxPredictionTime, nextPrediction, nextEvent, nextEventVisions);
		}
	}

	private void triggerAdvancement(Level level, BlockPos pos) {
		AABB aabb = new AABB(pos).inflate(16);
		for(ServerPlayer player : level.getEntitiesOfClass(ServerPlayer.class, aabb, EntitySelector.NO_SPECTATORS)) {
			if(player.distanceToSqr(Vec3.atCenterOf(pos)) <= 256) {
//				AdvancementCriterionRegistry.WIND_CHIME_PREDICTION.trigger(player);
			}
		}
	}

	private void updateParticles(Level level, BlockPos pos, int maxPredictionTime, int nextPrediction, IPredictableEnvironmentEvent nextEvent, ResourceLocation[] nextEventVisions) {
		if(this.predictedEvent != null && this.predictedEvent != nextEvent && this.fadeOutTimer < 20) {
			this.fadeOutTimer++;

			if(this.fadeOutTimer >= 20) {
				this.fadeOutTimer = 0;
				this.predictedEvent = nextEvent;
				this.predictedTimeUntilActivation = -1;

				this.predictedEventVision = nextEventVisions != null && nextEventVisions.length >= 1 ? nextEventVisions[level.getRandom().nextInt(nextEventVisions.length)] : null;

				if(this.predictedEventVision != null) {
					this.particleBatch = ParticleVisionOrb.createParticleBatch(() -> this.predictedEventVision);
				} else {
					this.particleBatch = null;
				}
			}
		} else if(this.predictedEvent == null) {
			this.predictedEvent = nextEvent;

			if(nextEvent != null) {
				this.predictedEventVision = nextEventVisions != null && nextEventVisions.length >= 1 ? nextEventVisions[level.getRandom().nextInt(nextEventVisions.length)] : null;

				if(this.predictedEventVision != null) {
					this.particleBatch = ParticleVisionOrb.createParticleBatch(() -> this.predictedEventVision);
				} else {
					this.particleBatch = null;
				}
			} else {
				this.predictedEventVision = null;
				this.particleBatch = null;
			}
		}

		if(this.predictedEvent == nextEvent && nextEvent != null) {
			if(this.predictedTimeUntilActivation == -1 || this.predictedTimeUntilActivation >= maxPredictionTime && nextPrediction < maxPredictionTime) {
				this.playChimes(level, pos, nextEvent);
			} else if(this.predictedTimeUntilActivation >= maxPredictionTime * 0.4f && nextPrediction < maxPredictionTime * 0.4f) {
				this.playChimes(level, pos, nextEvent);
			} else if(this.predictedTimeUntilActivation >= maxPredictionTime * 0.2f && nextPrediction < maxPredictionTime * 0.2f) {
				this.playChimes(level, pos, nextEvent);
			}

			this.predictedTimeUntilActivation = nextPrediction;

			if(this.particleBatch != null) {
				Entity view = Minecraft.getInstance().getCameraEntity();

				if(view != null && view.distanceToSqr(pos) < 256) {
					double cx = pos.getX() + 0.5f;
					double cy = pos.getY() + 0.2f;
					double cz = pos.getZ() + 0.5f;

					double rx = level.getRandom().nextFloat() - 0.5f;
					double ry = level.getRandom().nextFloat() - 0.5f;
					double rz = level.getRandom().nextFloat() - 0.5f;
					double len = Math.sqrt(rx * rx + ry * ry + rz * rz);
					rx /= len;
					ry /= len;
					rz /= len;

					int size = level.getRandom().nextInt(3);
					rx *= 0.6f + size * 0.1f;
					ry *= 0.6f + size * 0.1f;
					rz *= 0.6f + size * 0.1f;

					ParticleVisionOrb particle = (ParticleVisionOrb) BLParticles.WIND_CHIME_VISION
						.create(len, cx + rx, cy + ry, cz + rz, ParticleFactory.ParticleArgs.get()
							.withData(cx, cy, cz, 150)
							.withMotion(0, 0, 0)
							.withColor(1.0f, 1.0f, 1.0f, 0.85f)
							.withScale(0.85f));

					particle.setAlphaFunction(() -> 1.0f - this.fadeOutTimer / 20.0f);

					BatchedParticleRenderer.INSTANCE.addParticle(this.particleBatch, particle);
				}
			}
		} else {
			this.predictedTimeUntilActivation = -1;
		}

		if(this.particleBatch != null) {
			BatchedParticleRenderer.INSTANCE.updateBatch(this.particleBatch);
		}
	}

	private void playChimes(Level level, BlockPos pos, IPredictableEnvironmentEvent event) {
		this.ticksUntilChimes = 15;

		SoundEvent chimes = event.getChimesSound();

		if(chimes != null) {
			level.playLocalSound(pos.getX() + 0.5f, pos.getY() + 0.5f, pos.getZ() + 0.5f, chimes, SoundSource.BLOCKS, 2.0F, 1.0F, false);
		}

		level.playLocalSound(pos.getX() + 0.5f, pos.getY() + 0.5f, pos.getZ() + 0.5f, SoundRegistry.CHIMES_WIND.get(), SoundSource.BLOCKS, 2.0F, 1.0F, false);
	}

	public int getMaxPredictionTime() {
		return this.attunedEvent != null ? 12000 : 6000;
	}

	@Nullable
	public ResourceLocation getAttunedEvent() {
		return this.attunedEvent;
	}

	public void setAttunedEvent(@Nullable ResourceLocation event) {
		this.attunedEvent = event;
		this.setChanged();
		if (this.getLevel() != null) {
			this.getLevel().sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 3);
		}
	}

	@Nullable
	public ResourceLocation cycleAttunedEvent(Level level) {
		BLEnvironmentEventRegistry registry = BetweenlandsWorldStorage.forWorld(level).getEnvironmentEventRegistry();

		List<IPredictableEnvironmentEvent> choices = new ArrayList<>();

		int currentAttunedIndex = -1;

		int i = 0;
		for(Map.Entry<ResourceLocation, IEnvironmentEvent> entry : registry.getEvents().entrySet()) {
			if(entry.getValue() instanceof IPredictableEnvironmentEvent) {
				choices.add((IPredictableEnvironmentEvent) entry.getValue());

				if(this.attunedEvent != null && this.attunedEvent.equals(entry.getKey())) {
					currentAttunedIndex = i;
				}

				i++;
			}
		}

		ResourceLocation newAttunement;

		if(currentAttunedIndex >= 0) {
			if(currentAttunedIndex == choices.size() - 1) {
				newAttunement = null;
			} else {
				newAttunement = choices.get(currentAttunedIndex + 1).getEventName();
			}
		} else if(choices.size() > 0) {
			newAttunement = choices.get(0).getEventName();
		} else {
			newAttunement = null;
		}

		this.setAttunedEvent(newAttunement);

		return newAttunement;
	}

	@Nullable
	public ParticleBatch getParticleBatch() {
		return this.particleBatch;
	}

	public IPredictableEnvironmentEvent getPredictedEvent() {
		return this.predictedEvent;
	}

	public int getPredictedTimeUntilActivation() {
		return this.predictedTimeUntilActivation;
	}

	@Nullable
	public ResourceLocation getPredictedEventVision() {
		return this.predictedEventVision;
	}

	@Override
	protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
		super.saveAdditional(tag, registries);
		if (this.attunedEvent != null) {
			tag.putString("attuned_event", this.attunedEvent.toString());
		}
	}

	@Override
	protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
		super.loadAdditional(tag, registries);
		if (tag.contains("attuned_event", Tag.TAG_STRING)) {
			this.setAttunedEvent(ResourceLocation.parse(tag.getString("attuned_event")));
		}
	}
}
