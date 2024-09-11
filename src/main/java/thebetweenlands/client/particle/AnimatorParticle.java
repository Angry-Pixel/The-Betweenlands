package thebetweenlands.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public class AnimatorParticle extends PathParticle {
	public AnimatorParticle(ClientLevel level, double x, double y, double z, double motionX, double motionY, double motionZ, List<Vec3> targetPoints) {
		super(level, x, y, z, motionX, motionY, motionZ, targetPoints);

		this.scale(level.getRandom().nextFloat() / 2.0F);
		this.lifetime = 200;
		this.age = 0;
	}

	@Override
	public void tick() {
		super.tick();

		if (this.targetPoints.isEmpty()) {
			this.remove();
		} else {
			double t = 1.0D / 200.0D * this.age;

			if (t >= 1.0D) {
				this.remove();
			}

			Vec3 pos = this.getPosition(t);

			this.setPos(pos.x, pos.y, pos.z);
			this.xd = 0;
			this.yd = 0;
			this.zd = 0;
		}
	}

	@Override
	public ParticleRenderType getRenderType() {
		return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
	}

	public static class Factory extends ParticleFactory<Factory, SimpleParticleType> {

		private final SpriteSet spriteSet;

		public Factory(SpriteSet spriteSet) {
			this.spriteSet = spriteSet;
		}

		@Override
		protected void setDefaultArguments(ClientLevel level, double x, double y, double z, ParticleArgs<?> args) {
			args.withData(new ArrayList<Vec3>());
		}

		@SuppressWarnings("unchecked")
		@Override
		public AnimatorParticle createParticle(SimpleParticleType type, ImmutableParticleArgs args) {
			var particle = new AnimatorParticle(args.level, args.x, args.y, args.z, args.motionX, args.motionY, args.motionZ, (ArrayList<Vec3>) args.data.getObject(0));
			particle.pickSprite(this.spriteSet);
			return particle;
		}
	}
}
