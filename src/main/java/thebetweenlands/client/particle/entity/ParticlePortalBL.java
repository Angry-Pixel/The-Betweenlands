package thebetweenlands.client.particle.entity;

import net.minecraft.client.particle.Particle;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import thebetweenlands.client.particle.ParticleFactory;
import thebetweenlands.client.particle.ParticleTextureStitcher;
import thebetweenlands.client.particle.ParticleTextureStitcher.IParticleSpriteReceiver;

public class ParticlePortalBL extends Particle implements IParticleSpriteReceiver {
	public ParticlePortalBL(World world, double x, double y, double z, double mx, double my, double mz, int maxAge, float scale) {
		super(world, x, y, z);
		this.posX = this.prevPosX = x;
		this.posY = this.prevPosY = y;
		this.posZ = this.prevPosZ = z;
		this.motionX = mx;
		this.motionY = my;
		this.motionZ = mz;
		this.particleMaxAge = maxAge;
		//this.noClip = false;
		this.particleScale = scale;
	}

	@Override
	public int getFXLayer() {
		return 1;
	}

	public static final class Factory extends ParticleFactory<ParticlePortalBL> {
		public Factory() {
			super(ParticlePortalBL.class, ParticleTextureStitcher.create(ParticlePortalBL.class, new ResourceLocation("thebetweenlands:particle/portal")));
		}

		@Override
		public ParticlePortalBL createParticle(ImmutableParticleArgs args) {
			return new ParticlePortalBL(args.world, args.x, args.y, args.z, args.motionX, args.motionY, args.motionZ, (int)args.data[0], args.scale);
		}

		@Override
		protected void setBaseArguments(ParticleArgs args) {
			args.withData(40);
		}
	}
}
