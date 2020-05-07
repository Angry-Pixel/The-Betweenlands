package thebetweenlands.client.render.particle.entity;

import net.minecraft.client.particle.ParticleRain;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import thebetweenlands.client.handler.TextureStitchHandler.Frame;
import thebetweenlands.client.render.particle.ParticleFactory;
import thebetweenlands.client.render.particle.ParticleTextureStitcher;
import thebetweenlands.client.render.particle.ParticleTextureStitcher.IParticleSpriteReceiver;

public class ParticleBLRain extends ParticleRain implements IParticleSpriteReceiver {
	protected ParticleBLRain(World worldIn, double xCoordIn, double yCoordIn, double zCoordIn) {
		super(worldIn, xCoordIn, yCoordIn, zCoordIn);
	}

	@Override
	public void setParticleTextureIndex(int particleTextureIndex) {
		//not needed
	}

	@Override
	public int getFXLayer() {
		return 1;
	}

	@Override
	public void setStitchedSprites(Frame[][] frames) {
		this.setParticleTexture(frames[this.world.rand.nextInt(frames.length)][0].getSprite());
	}

	public static final class Factory extends ParticleFactory<Factory, ParticleBLRain> {
		public Factory() {
			super(ParticleBLRain.class, ParticleTextureStitcher.create(ParticleBLRain.class, 
					new ResourceLocation[] {
							new ResourceLocation("thebetweenlands:particle/rain_1"),
							new ResourceLocation("thebetweenlands:particle/rain_2"),
							new ResourceLocation("thebetweenlands:particle/rain_3"),
							new ResourceLocation("thebetweenlands:particle/rain_4")
			}));
		}

		@Override
		public ParticleBLRain createParticle(ImmutableParticleArgs args) {
			return new ParticleBLRain(args.world, args.x, args.y, args.z);
		}
	}
}
