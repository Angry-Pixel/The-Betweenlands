package thebetweenlands.client.render.particle.entity;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.client.render.particle.ParticleFactory;
import thebetweenlands.client.render.particle.ParticleFactory.ParticleArgs;
import thebetweenlands.client.render.particle.ParticleTextureStitcher;

public class ParticleFancyBubble extends ParticleBubbleBL {

	private boolean floating = false;
	
	protected ParticleFancyBubble(World world, double x, double y, double z, double vecX, double vecY, double vecZ, float scale, boolean floating) {
		super(world, x, y, z, vecX, vecY, vecZ, scale);
		this.floating = floating;
		if(floating) {
			this.motionX = vecX;
			this.motionY = vecY;
			this.motionZ = vecZ;
			this.particleGravity = 0.0f;
			this.particleMaxAge = world.rand.nextInt(40) + 20;
		}
	}

	@Override
	public void onUpdate() {
		double prevMotionX = this.motionX;
		double prevMotionY = this.motionY;
		double prevMotionZ = this.motionZ;
		
		super.onUpdate();

		if(this.floating) {
			this.motionX = prevMotionX * 0.95f;
			this.motionY = prevMotionY * 0.95f;
			this.motionZ = prevMotionZ * 0.95f;
		}
		
		if (particleMaxAge <= 0) {
			for(int j = 0; j < 2; ++j) {
				double velX = (rand.nextFloat() - 0.5f) * 0.15f;
				double velY = 0.075f + (rand.nextFloat() - 0.5f) * 0.15f;
				double velZ = (rand.nextFloat() - 0.5f) * 0.15f;
				BLParticles.FANCY_DRIP.spawn(this.world, this.posX, this.posY, this.posZ, ParticleArgs.get().withMotion(velX, velY, velZ).withScale(this.particleScale * (0.75f + 0.5f * this.world.rand.nextFloat())).withColor(this.particleRed * 0.8f, this.particleGreen * 0.8f, this.particleBlue * 0.8f, this.particleAlpha));
			}
		}
	}

	public static final class Factory extends ParticleFactory<Factory, ParticleFancyBubble> {
		public Factory() {
			super(ParticleFancyBubble.class, ParticleTextureStitcher.create(ParticleFancyBubble.class, new ResourceLocation("thebetweenlands:particle/bubble")));
		}

		@Override
		public ParticleFancyBubble createParticle(ImmutableParticleArgs args) {
			return new ParticleFancyBubble(args.world, args.x, args.y, args.z, args.motionX, args.motionY, args.motionZ, args.scale, args.data.getBool(0));
		}
		
		@Override
		protected void setBaseArguments(ParticleArgs<?> args) {
			args.withData(false);
		}
		
		@Override
		protected void setDefaultArguments(World world, double x, double y, double z, ParticleArgs<?> args) {
			args.withData(false);
		}
	}
}
