package thebetweenlands.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;

public class MothParticle extends BugParticle {

	private final int type;

	protected MothParticle(ClientLevel level, double x, double y, double z, double mx, double my, double mz, int maxAge, float speed, float jitter, float scale, SpriteSet set, int type) {
		super(level, x, y, z, mx, my, mz, maxAge, speed, jitter, scale, false, set, 3);
		this.type = type;
	}

	@Override
	public void animateBug() {
		int sprite = this.type * 2 + (this.age % (this.frametime * 2) >= this.frametime ? 1 : 0);
		this.setSprite(this.spriteSet.get(sprite, this.type * 2 + 1));
	}

	public static final class Factory extends ParticleFactory<Factory, SimpleParticleType> {
		private final SpriteSet spriteSet;

		public Factory(SpriteSet spriteSet) {
			this.spriteSet = spriteSet;
		}

		@Override
		public BugParticle createParticle(ImmutableParticleArgs args) {
			int type = args.data.getInt(3);
			var particle = new MothParticle(args.level, args.x, args.y, args.z, args.motionX, args.motionY, args.motionZ, args.data.getInt(0), args.data.getFloat(1), args.data.getFloat(2), args.scale, this.spriteSet, type);
			particle.setSprite(this.spriteSet.get(type * 2, type * 2 + 1));
			return particle;
		}

		@Override
		protected void setBaseArguments(ParticleArgs<?> args) {
			args.withScale(1.0F).withData(400, 0.02F, 0.005F, 0);
		}

		@Override
		protected void setDefaultArguments(ClientLevel level, double x, double y, double z, ParticleArgs<?> args) {
			args.withScale(level.getRandom().nextFloat()).withDataBuilder().setData(3, level.getRandom().nextInt(2)).buildData();
		}
	}
}
