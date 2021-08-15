package thebetweenlands.client.render.particle.entity;

import net.minecraft.client.particle.ParticleBubble;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import thebetweenlands.client.render.particle.ParticleFactory;
import thebetweenlands.client.render.particle.ParticleTextureStitcher;
import thebetweenlands.client.render.particle.ParticleTextureStitcher.IParticleSpriteReceiver;

public class ParticleBubbleBL extends ParticleBubble implements IParticleSpriteReceiver {
	protected ParticleBubbleBL(World world, double x, double y, double z, double vecX, double vecY, double vecZ, float scale) {
		super(world, x, y, z, vecX, vecY, vecZ);
		this.particleScale = scale;
		this.motionX = vecX + (Math.random() * 2.0D - 1.0D) * 0.4000000059604645D;
		this.motionY = vecY + (Math.random() * 2.0D - 1.0D) * 0.4000000059604645D;
		this.motionZ = vecZ + (Math.random() * 2.0D - 1.0D) * 0.4000000059604645D;
		float f = (float)(Math.random() + Math.random() + 1.0D) * 0.15F;
		float f1 = MathHelper.sqrt(this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ);
		this.motionX = this.motionX / (double)f1 * (double)f * 0.3D;
		this.motionY = this.motionY / (double)f1 * (double)f * 0.4D + 0.3D;
		this.motionZ = this.motionZ / (double)f1 * (double)f * 0.3D;
		this.particleScale = scale;
	}

	@Override
	public void onUpdate() {
		prevPosX = posX;
		prevPosY = posY;
		prevPosZ = posZ;
		motionY += 0.002D;
		move(motionX, motionY, motionZ);
		motionX *= 0.8500000238418579D;
		motionY *= 0.3500000238418579D;
		motionZ *= 0.8500000238418579D;

		if (particleMaxAge-- <= 0)
			setExpired();
	}

	@Override
	public int getFXLayer() {
		return 1;
	}
	
	@Override
	public void setParticleTextureIndex(int particleTextureIndex) {
		//nope.avi
    }
	
	public static final class InfuserFactory extends ParticleFactory<InfuserFactory, ParticleBubbleBL> {
		public InfuserFactory() {
			super(ParticleBubbleBL.class, ParticleTextureStitcher.create(ParticleBubbleBL.class, new ResourceLocation("thebetweenlands:particle/bubble_infuser")));
		}

		@Override
		public ParticleBubbleBL createParticle(ImmutableParticleArgs args) {
			return new ParticleBubbleBL(args.world, args.x, args.y, args.z, args.motionX, args.motionY, args.motionZ, args.scale);
		}
	}
	
	public static final class WaterFactory extends ParticleFactory<WaterFactory, ParticleBubbleBL> {
		public WaterFactory() {
			super(ParticleBubbleBL.class, ParticleTextureStitcher.create(ParticleBubbleBL.class, new ResourceLocation("thebetweenlands:particle/bubble")));
		}

		@Override
		public ParticleBubbleBL createParticle(ImmutableParticleArgs args) {
			return new ParticleBubbleBL(args.world, args.x, args.y, args.z, args.motionX, args.motionY, args.motionZ, args.scale);
		}
	}
}
