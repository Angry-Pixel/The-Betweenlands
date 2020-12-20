package thebetweenlands.common.tile;

import javax.annotation.Nullable;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.ResourceLocation;
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
import thebetweenlands.common.world.event.BLEnvironmentEventRegistry;
import thebetweenlands.common.world.storage.BetweenlandsWorldStorage;

public class TileEntityWindChime extends TileEntity implements ITickable {
	@SideOnly(Side.CLIENT)
	private ParticleBatch particleBatch;

	private int fadeOutTimer = 0;

	private IPredictableEnvironmentEvent predictedEvent;
	private int predictedTimeUntilActivation;

	protected int maxPredictionTime = 6000;

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

	@Override
	public void update() {
		if(this.world.isRemote) {
			this.updateParticles();
		}
	}

	@SideOnly(Side.CLIENT)
	private void updateParticles() {
		BLEnvironmentEventRegistry registry = BetweenlandsWorldStorage.forWorld(this.world).getEnvironmentEventRegistry();

		int nextPrediction = Integer.MAX_VALUE;
		IPredictableEnvironmentEvent nextEvent = null;
		ResourceLocation nextEventVision = null;

		for(IEnvironmentEvent event : registry.getEvents().values()) {
			if(event instanceof IPredictableEnvironmentEvent) {
				IPredictableEnvironmentEvent predictable = (IPredictableEnvironmentEvent) event;

				ResourceLocation vision = predictable.getVisionTexture();

				if(vision != null) {
					int prediction = predictable.estimateTimeUntil(State.ACTIVE);

					if(prediction > 0 && prediction < nextPrediction && prediction < this.maxPredictionTime) {
						nextPrediction = prediction;
						nextEvent = predictable;
						nextEventVision = vision;
					}
				}
			}
		}

		final ResourceLocation eventVision = nextEventVision;

		if(this.predictedEvent != null && this.predictedEvent != nextEvent && this.fadeOutTimer < 20) {
			this.fadeOutTimer++;

			if(this.fadeOutTimer >= 20) {
				this.fadeOutTimer = 0;
				this.predictedEvent = nextEvent;

				if(eventVision != null) {
					this.particleBatch = ParticleVisionOrb.createParticleBatch(() -> eventVision);
				} else {
					this.particleBatch = null;
				}
			}
		} else if(this.predictedEvent == null) {
			this.predictedEvent = nextEvent;

			if(eventVision != null) {
				this.particleBatch = ParticleVisionOrb.createParticleBatch(() -> eventVision);
			} else {
				this.particleBatch = null;
			}
		}

		if(this.predictedEvent == nextEvent && nextEvent != null) {
			this.predictedTimeUntilActivation = nextPrediction;

			if(this.particleBatch != null) {
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
		} else if(this.predictedEvent == null) {
			this.predictedTimeUntilActivation = -1;
		}

		if(this.particleBatch != null) {
			BatchedParticleRenderer.INSTANCE.updateBatch(this.particleBatch);
		}
	}
}
