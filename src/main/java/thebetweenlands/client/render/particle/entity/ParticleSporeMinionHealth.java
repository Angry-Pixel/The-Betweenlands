package thebetweenlands.client.render.particle.entity;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import thebetweenlands.client.render.particle.ParticleFactory;
import thebetweenlands.client.render.particle.ParticleTextureStitcher;

public class ParticleSporeMinionHealth extends ParticleBubbleBL {

	public ParticleSporeMinionHealth(World world, double x, double y, double z, double vecX, double vecY, double vecZ, float scale) {
		super(world, x, y, z, vecX, vecY, vecZ, scale);
		this.motionX = vecX;
		this.motionY = vecY;
		this.motionZ = vecZ;
		this.particleGravity = 0.0f;
		float colorMulti = this.rand.nextFloat() * 0.3F;
		this.particleScale = scale;
		this.particleRed = this.particleGreen = this.particleBlue = 1.0F * colorMulti;
		this.particleMaxAge = 2000;
	}

	@Override
	public void onUpdate() {
		double prevMotionX = this.motionX;
		double prevMotionY = this.motionY;
		double prevMotionZ = this.motionZ;

		super.onUpdate();

		this.motionX = prevMotionX * 0.95f;
		this.motionY = prevMotionY * 0.95f;
		this.motionZ = prevMotionZ * 0.95f;

		if (this.particleAge++ >= this.particleMaxAge) {
			this.setExpired();
		}

	}

	public static final class Factory extends ParticleFactory<Factory, ParticleSporeMinionHealth> {
		
		public Factory() {
			super(ParticleSporeMinionHealth.class, ParticleTextureStitcher.create(ParticleSporeMinionHealth.class, new ResourceLocation("thebetweenlands:particle/yeowynn_growth")));
		}

		@Override
		public ParticleSporeMinionHealth createParticle(ImmutableParticleArgs args) {
			return new ParticleSporeMinionHealth(args.world, args.x, args.y, args.z, args.motionX, args.motionY, args.motionZ, args.scale);
		}

		@Override
		protected void setBaseArguments(ParticleArgs<?> args) {
			args.withData(ParticleFactory.EMPTY_ARG);
		}
	}
}
