package thebetweenlands.client.render.particle.entity;

import net.minecraft.client.particle.Particle;
import net.minecraft.world.World;
import thebetweenlands.client.render.particle.ParticleFactory;

public class ParticleDruidCasting extends Particle {
	public float portalParticleScale;

	protected ParticleDruidCasting(World world, double x, double y, double z, double motionX, double motionY, double motionZ, float scale) {
		super(world, x, y, z, motionX, motionY, motionZ);
		this.motionX = motionX;
		this.motionY = motionY;
		this.motionZ = motionZ;
		float colorMulti = this.rand.nextFloat() * 0.3F;
		this.portalParticleScale = this.particleScale = scale;
		this.particleRed = this.particleGreen = this.particleBlue = 1.0F * colorMulti;
		this.particleMaxAge = (int) (Math.random() * 10.0D) + 40;
		//this.noClip = true;
		this.setParticleTextureIndex((int) (Math.random() * 26.0D + 1.0D + 224.0D));
	}

	@Override
	public int getBrightnessForRender(float partTicks) {
		int i = super.getBrightnessForRender(partTicks);
		float f1 = (float) this.particleAge / (float) this.particleMaxAge;
		f1 *= f1;
		f1 *= f1;
		int j = i & 255;
		int k = i >> 16 & 255;
		k += (int) (f1 * 15.0F * 16.0F);

		if( k > 240 ) {
			k = 240;
		}

		return j | k << 16;
	}

	@Override
	public void onUpdate() {
		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;

		if( this.particleAge++ >= this.particleMaxAge ) {
			this.setExpired();
		}

		this.move(this.motionX, this.motionY, this.motionZ);
		this.motionX *= 0.8599999785423279D;
		this.motionY *= 0.2599999785423279D;
		this.motionZ *= 0.8599999785423279D;
	}

	public static final class Factory extends ParticleFactory<Factory, ParticleDruidCasting> {
		public Factory() {
			super(ParticleDruidCasting.class);
		}

		@Override
		public ParticleDruidCasting createParticle(ImmutableParticleArgs args) {
			return new ParticleDruidCasting(args.world, args.x, args.y, args.z, args.motionX, args.motionY, args.motionZ, args.scale);
		}
	}
}
