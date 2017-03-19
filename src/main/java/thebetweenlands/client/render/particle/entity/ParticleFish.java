package thebetweenlands.client.render.particle.entity;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import thebetweenlands.client.handler.TextureStitchHandler.Frame;
import thebetweenlands.client.render.particle.ParticleFactory;
import thebetweenlands.client.render.particle.ParticleTextureStitcher;

public class ParticleFish extends ParticleBug {
	public final int fishTexture;

	protected ParticleFish(World world, double x, double y, double z, double mx, double my, double mz, int maxAge, float speed, float jitter, float scale, int texture) {
		super(world, x, y, z, mx, my, mz, maxAge, speed, jitter, scale, true);
		this.fishTexture = texture;
		this.animation = null;
	}

	@Override
	public void setStitchedSprites(Frame[][] frames) {
		this.setParticleTexture(frames[this.fishTexture][0].getSprite());
	}

	public static final class Factory extends ParticleFactory<Factory, ParticleFish> {
		public Factory() {
			super(ParticleFish.class, ParticleTextureStitcher.create(ParticleFish.class, 
					new ResourceLocation[] {
							new ResourceLocation("thebetweenlands:particle/fish_1"),
							new ResourceLocation("thebetweenlands:particle/fish_2"),
							new ResourceLocation("thebetweenlands:particle/fish_3")
			}));
		}

		@Override
		public ParticleFish createParticle(ImmutableParticleArgs args) {
			return new ParticleFish(args.world, args.x, args.y, args.z, args.motionX, args.motionY, args.motionZ, args.data.getInt(0), args.data.getFloat(1), args.data.getFloat(2), args.scale, args.data.getInt(3));
		}

		@Override
		protected void setBaseArguments(ParticleArgs<?> args) {
			args.withScale(0.18F).withData(400, 0.02F, 0.005F, 0);
		}

		@Override
		protected void setDefaultArguments(World world, double x, double y, double z, ParticleArgs<?> args) {
			args.withScale(0.18F * world.rand.nextFloat() + 0.1F).withDataBuilder().setData(3, world.rand.nextInt(3)).buildData();
		}
	}
}
