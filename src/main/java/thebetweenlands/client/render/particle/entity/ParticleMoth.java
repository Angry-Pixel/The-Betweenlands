package thebetweenlands.client.render.particle.entity;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import thebetweenlands.client.handler.TextureStitchHandler.Frame;
import thebetweenlands.client.render.particle.ParticleFactory;
import thebetweenlands.client.render.particle.ParticleTextureStitcher;

public class ParticleMoth extends ParticleBug {
	public final int mothTexture;

	protected ParticleMoth(World world, double x, double y, double z, double mx, double my, double mz, int maxAge, float speed, float jitter, float scale, int texture) {
		super(world, x, y, z, mx, my, mz, maxAge, speed, jitter, scale, false);
		this.mothTexture = texture;
	}

	@Override
	public void setStitchedSprites(Frame[][] frames) {
		this.animation.setFrames(frames[this.mothTexture]);
		if(this.particleTexture == null) {
			this.setParticleTexture(frames[this.mothTexture][0].getSprite());
		}
	}

	public static final class Factory extends ParticleFactory<Factory, ParticleMoth> {
		public Factory() {
			super(ParticleMoth.class, ParticleTextureStitcher.create(ParticleMoth.class, 
					new ResourceLocation[] {
							new ResourceLocation("thebetweenlands:particle/moth_1"),
							new ResourceLocation("thebetweenlands:particle/moth_2")
			}));
		}

		@Override
		public ParticleMoth createParticle(ImmutableParticleArgs args) {
			return new ParticleMoth(args.world, args.x, args.y, args.z, args.motionX, args.motionY, args.motionZ, args.data.getInt(0), args.data.getFloat(1), args.data.getFloat(2), args.scale, args.data.getInt(3));
		}

		@Override
		protected void setBaseArguments(ParticleArgs<?> args) {
			args.withScale(1F).withData(400, 0.02F, 0.005F, 0);
		}

		@Override
		protected void setDefaultArguments(World world, double x, double y, double z, ParticleArgs<?> args) {
			args.withScale(1F * world.rand.nextFloat()).withDataBuilder().setData(3, world.rand.nextInt(2)).buildData();
		}
	}
}
