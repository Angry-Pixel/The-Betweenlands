package thebetweenlands.client.render.particle.entity;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import thebetweenlands.client.render.particle.ParticleFactory;
import thebetweenlands.client.render.particle.ParticleTextureStitcher;

public class ParticleSpiritButterfly extends ParticleBug {
	protected ParticleSpiritButterfly(World world, double x, double y, double z, double mx, double my, double mz, int maxAge, float speed, float jitter, float scale) {
		super(world, x, y, z, mx, my, mz, maxAge, speed, jitter, scale, false);
	}

	@Override
	public int getBrightnessForRender(float partialTicks) {
		return 15 << 20 | 15 << 4;
	}

	public static final class Factory extends ParticleFactory<Factory, ParticleSpiritButterfly> {
		public Factory() {
			super(ParticleSpiritButterfly.class, ParticleTextureStitcher.create(ParticleSpiritButterfly.class, new ResourceLocation("thebetweenlands:particle/spirit_butterfly")).setSplitAnimations(true));
		}

		@Override
		public ParticleSpiritButterfly createParticle(ImmutableParticleArgs args) {
			return new ParticleSpiritButterfly(args.world, args.x, args.y, args.z, args.motionX, args.motionY, args.motionZ, args.data.getInt(0), args.data.getFloat(1), args.data.getFloat(2), args.scale);
		}

		@Override
		protected void setBaseArguments(ParticleArgs<?> args) {
			args.withScale(0.4F).withData(400, 0.025F, 0.01F);
		}

		@Override
		protected void setDefaultArguments(World world, double x, double y, double z, ParticleArgs<?> args) {
			args.withScale(0.4F * world.rand.nextFloat() + 0.1F);
		}
	}
}
