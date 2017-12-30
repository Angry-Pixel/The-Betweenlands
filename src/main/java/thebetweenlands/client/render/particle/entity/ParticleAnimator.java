package thebetweenlands.client.render.particle.entity;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import thebetweenlands.client.render.particle.ParticleFactory;

public class ParticleAnimator extends PathParticle {
	public ParticleAnimator(World world, double x, double y, double z, double motionX, double motionY, double motionZ,
			List<Vec3d> targetPoints) {
		super(world, x, y, z, motionX, motionY, motionZ, targetPoints);

		this.particleScale = world.rand.nextFloat() / 2.0F;
		this.particleMaxAge = 200;
		this.particleAge = 0;

		this.setParticleTextureIndex((int)(Math.random() * 26.0D + 1.0D + 224.0D));
	}

	@Override
	public void onUpdate() {
		super.onUpdate();

		if(this.targetPoints.isEmpty()) {
			this.setExpired();
		} else {
			double t = 1.0D / 200.0D * this.particleAge;

			if(t >= 1.0D) {
				this.setExpired();
			}

			Vec3d pos = this.getPosition(t);

			this.setPosition(pos.x, pos.y, pos.z);
			this.motionX = 0;
			this.motionY = 0;
			this.motionZ = 0;
		}
	}

	public static final class Factory extends ParticleFactory<Factory, ParticleAnimator> {
		public Factory() {
			super(ParticleAnimator.class);
		}

		@Override
		protected void setDefaultArguments(World world, double x, double y, double z, ParticleArgs<?> args) { 
			args.withData(new ArrayList<Vec3d>());
		}

		@SuppressWarnings("unchecked")
		@Override
		public ParticleAnimator createParticle(ImmutableParticleArgs args) {
			return new ParticleAnimator(args.world, args.x, args.y, args.z, args.motionX, args.motionY, args.motionZ, (ArrayList<Vec3d>)args.data.getObject(0));
		}
	}
}
