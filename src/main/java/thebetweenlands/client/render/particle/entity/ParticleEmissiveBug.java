package thebetweenlands.client.render.particle.entity;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import thebetweenlands.client.handler.TextureStitchHandler.Frame;
import thebetweenlands.client.render.particle.ParticleFactory;
import thebetweenlands.client.render.particle.ParticleTextureStitcher;
import thebetweenlands.client.render.particle.entity.ParticleSwarm.ResourceLocationWithScale;
import thebetweenlands.client.render.sprite.TextureAnimation;

public class ParticleEmissiveBug extends ParticleBug {
	private TextureAnimation emissiveAnimation;

	protected ParticleEmissiveBug(World world, double x, double y, double z, double mx, double my, double mz, int maxAge, float speed, float jitter, float scale, boolean underwater) {
		super(world, x, y, z, mx, my, mz, maxAge, speed, jitter, scale, underwater);
		this.emissiveAnimation = new TextureAnimation();
	}

	@Override
	public void setStitchedSprites(Frame[][] frames) {
		if (this.animation != null && frames != null) {
			if(frames.length % 2 != 0) {
				throw new IllegalStateException("Emissive particle requires a multiple of two number of sprites");
			}

			int variant = this.rand.nextInt(frames.length / 2);

			this.animation.setFrames(frames[variant * 2]);
			this.emissiveAnimation.setFrames(frames[variant * 2 + 1]);

			ResourceLocation location = frames[variant * 2][0].getLocation();
			if(location instanceof ResourceLocationWithScale) {
				this.particleScale *= ((ResourceLocationWithScale) location).scale;
			}

			if(this.particleMaxAge < 0) {
				this.particleMaxAge = this.animation.getTotalDuration() - 1;
			}
			if (this.particleTexture == null) {
				this.setParticleTexture(frames[variant * 2][0].getSprite());
			}
		}
	}

	@Override
	public void onUpdate() {
		super.onUpdate();

		this.emissiveAnimation.update();

		if(this.particleAge > this.particleMaxAge - 10) {
			this.particleAlpha = ((this.particleMaxAge - this.particleAge) / 10.0f);
		}
	}

	@Override
	public void renderParticle(BufferBuilder buff, Entity entityIn, float partialTicks, float rx, float rz, float ryz, float rxy, float rxz) {
		if(this.particleAge < 10) {
			this.particleAlpha = this.particleAge / 10.0f;
		}

		this.setParticleTexture(this.animation.getCurrentSprite());
		super.renderParticle(buff, entityIn, partialTicks, rx, rz, ryz, rxy, rxz);

		this.setParticleTexture(this.emissiveAnimation.getCurrentSprite());
		super.renderParticle(buff, entityIn, partialTicks, rx, rz, ryz, rxy, rxz);
	}

	@Override
	public int getBrightnessForRender(float partialTick) {
		if(this.particleTexture == this.emissiveAnimation.getCurrentSprite()) {
			return 15 << 20 | 15 << 4;
		}
		return super.getBrightnessForRender(partialTick);
	}

	public static final class Swarm extends ParticleFactory<Swarm, ParticleEmissiveBug> {
		public Swarm() {
			super(ParticleEmissiveBug.class, ParticleTextureStitcher.create(ParticleEmissiveBug.class,
					new ResourceLocationWithScale("thebetweenlands:particle/swarm_5", 2), new ResourceLocationWithScale("thebetweenlands:particle/swarm_5_emissive", 2)
					).setSplitAnimations(true));
		}

		@Override
		public ParticleEmissiveBug createParticle(ImmutableParticleArgs args) {
			return new ParticleEmissiveBug(args.world, args.x, args.y, args.z, args.motionX, args.motionY, args.motionZ, args.data.getInt(0), args.data.getFloat(1), args.data.getFloat(2), args.scale, args.data.getBool(3));
		}

		@Override
		protected void setBaseArguments(ParticleArgs<?> args) {
			args.withScale(0.5f).withData(40, 0.01F, 0.0025F, false);
		}
	}
}
