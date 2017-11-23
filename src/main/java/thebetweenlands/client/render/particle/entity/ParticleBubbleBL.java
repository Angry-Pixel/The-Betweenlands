package thebetweenlands.client.render.particle.entity;

import net.minecraft.client.particle.Particle;
import net.minecraft.world.World;
import thebetweenlands.client.render.particle.ParticleFactory;

public class ParticleBubbleBL extends Particle {
	protected ParticleBubbleBL(World world, double x, double y, double z, double vecX, double vecY, double vecZ, float scale) {
		super(world, x, y, z, vecX, vecY, vecZ);
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

	public static final class Factory extends ParticleFactory<Factory, ParticleBubbleBL> {
		public Factory() {
			super(ParticleBubbleBL.class);
		}

		@Override
		public ParticleBubbleBL createParticle(ImmutableParticleArgs args) {
			return new ParticleBubbleBL(args.world, args.x, args.y, args.z, args.motionX, args.motionY, args.motionZ, args.scale);
		}
	}
}
