package thebetweenlands.common.tile;

import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.api.environment.IEnvironmentEvent;
import thebetweenlands.api.environment.IPredictableEnvironmentEvent;
import thebetweenlands.api.environment.IPredictableEnvironmentEvent.State;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.client.render.particle.BatchedParticleRenderer;
import thebetweenlands.client.render.particle.BatchedParticleRenderer.ParticleBatch;
import thebetweenlands.client.render.particle.ParticleFactory;
import thebetweenlands.client.render.particle.entity.ParticleVisionOrb;
import thebetweenlands.common.registries.SoundRegistry;
import thebetweenlands.common.world.event.BLEnvironmentEventRegistry;
import thebetweenlands.common.world.storage.BetweenlandsWorldStorage;

public class TileEntityWindChime extends TileEntity implements ITickable {
	public int renderTicks;

	public int ticksUntilChimes;
	public int prevChimeTicks;
	public int chimeTicks;

	@SideOnly(Side.CLIENT)
	private ParticleBatch particleBatch;

	private int fadeOutTimer = 0;

	private IPredictableEnvironmentEvent predictedEvent;
	private int predictedTimeUntilActivation;
	private ResourceLocation predictedEventVision;

	protected int maxPredictionTime = 9600;

	@Nullable
	@SideOnly(Side.CLIENT)
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
	public void update() {
		if(this.world.isRemote) {
			this.renderTicks++;

			if(this.ticksUntilChimes > 0) {
				this.ticksUntilChimes--;

				if(this.ticksUntilChimes <= 0) {
					this.ticksUntilChimes = 0;
				} else if(this.ticksUntilChimes <= 4) {
					this.chimeTicks += 25;
				}
			}

			this.prevChimeTicks = this.chimeTicks;
			this.chimeTicks = Math.max(this.chimeTicks - 1, 0);

			this.updateParticles();
		}
	}

	@SideOnly(Side.CLIENT)
	private void updateParticles() {
		BLEnvironmentEventRegistry registry = BetweenlandsWorldStorage.forWorld(this.world).getEnvironmentEventRegistry();

		int nextPrediction = Integer.MAX_VALUE;
		IPredictableEnvironmentEvent nextEvent = null;
		ResourceLocation[] nextEventVisions = null;

		for(IEnvironmentEvent event : registry.getEvents().values()) {
			if(event instanceof IPredictableEnvironmentEvent) {
				IPredictableEnvironmentEvent predictable = (IPredictableEnvironmentEvent) event;

				ResourceLocation[] visions = predictable.getVisionTextures();

				if(visions != null) {
					int prediction = predictable.estimateTimeUntil(State.ACTIVE);

					if(prediction > 0 && prediction < nextPrediction && prediction < this.maxPredictionTime) {
						nextPrediction = prediction;
						nextEvent = predictable;
						nextEventVisions = visions;
					}
				}
			}
		}

		if(this.predictedEvent != null && this.predictedEvent != nextEvent && this.fadeOutTimer < 20) {
			this.fadeOutTimer++;

			if(this.fadeOutTimer >= 20) {
				this.fadeOutTimer = 0;
				this.predictedEvent = nextEvent;
				this.predictedTimeUntilActivation = -1;

				this.predictedEventVision = nextEventVisions != null && nextEventVisions.length >= 1 ? nextEventVisions[this.world.rand.nextInt(nextEventVisions.length)] : null;

				if(this.predictedEventVision != null) {
					this.particleBatch = ParticleVisionOrb.createParticleBatch(() -> this.predictedEventVision);
				} else {
					this.particleBatch = null;
				}
			}
		} else if(this.predictedEvent == null) {
			this.predictedEvent = nextEvent;

			if(nextEvent != null) {
				this.predictedEventVision = nextEventVisions != null && nextEventVisions.length >= 1 ? nextEventVisions[this.world.rand.nextInt(nextEventVisions.length)] : null;

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
			if(this.predictedTimeUntilActivation == -1 || this.predictedTimeUntilActivation >= this.maxPredictionTime && nextPrediction < this.maxPredictionTime) {
				this.playChimes(nextEvent);
			} else if(this.predictedTimeUntilActivation >= this.maxPredictionTime * 0.4f && nextPrediction < this.maxPredictionTime * 0.4f) {
				this.playChimes(nextEvent);
			} else if(this.predictedTimeUntilActivation >= this.maxPredictionTime * 0.2f && nextPrediction < this.maxPredictionTime * 0.2f) {
				this.playChimes(nextEvent);
			}

			this.predictedTimeUntilActivation = nextPrediction;

			if(this.particleBatch != null) {
				Entity view = Minecraft.getMinecraft().getRenderViewEntity();

				if(view != null && view.getDistanceSq(this.getPos()) < 256) {
					double cx = this.pos.getX() + 0.5f;
					double cy = this.pos.getY() + 0.2f;
					double cz = this.pos.getZ() + 0.5f;

					double rx = this.world.rand.nextFloat() - 0.5f;
					double ry = this.world.rand.nextFloat() - 0.5f;
					double rz = this.world.rand.nextFloat() - 0.5f;
					double len = MathHelper.sqrt(rx * rx + ry * ry + rz * rz);
					rx /= len;
					ry /= len;
					rz /= len;

					int size = this.world.rand.nextInt(3);
					rx *= 0.6f + size * 0.1f;
					ry *= 0.6f + size * 0.1f;
					rz *= 0.6f + size * 0.1f;

					ParticleVisionOrb particle = (ParticleVisionOrb) BLParticles.WIND_CHIME_VISION
							.create(this.world, cx + rx, cy + ry, cz + rz, ParticleFactory.ParticleArgs.get()
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

	private void playChimes(IPredictableEnvironmentEvent event) {
		this.ticksUntilChimes = 15;

		SoundEvent chimes = event.getChimesSound();

		if(chimes != null) {
			this.world.playSound(this.getPos().getX() + 0.5f, this.getPos().getY() + 0.5f, this.getPos().getZ() + 0.5f, chimes, SoundCategory.BLOCKS, 2, 1, false);
		}

		this.world.playSound(this.getPos().getX() + 0.5f, this.getPos().getY() + 0.5f, this.getPos().getZ() + 0.5f, SoundRegistry.CHIMES_WIND, SoundCategory.BLOCKS, 2, 1, false);
	}
}
