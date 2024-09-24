package thebetweenlands.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.core.particles.SimpleParticleType;

public class SpiritButterflyParticle extends BugParticle {
	public SpiritButterflyParticle(ClientLevel level, double x, double y, double z, double mx, double my, double mz, int maxAge, float speed, float jitter, float scale, SpriteSet set) {
		super(level, x, y, z, mx, my, mz, maxAge, speed, jitter, scale, false, set, 3);
	}

	@Override
	protected int getLightColor(float partialTick) {
		return LightTexture.FULL_BLOCK;
	}

	public static final class Factory extends ParticleFactory<Factory, SimpleParticleType> {
		private final SpriteSet spriteSet;

		public Factory(SpriteSet spriteSet) {
			this.spriteSet = spriteSet;
		}

		@Override
		public BugParticle createParticle(SimpleParticleType type, ImmutableParticleArgs args) {
			var particle = new SpiritButterflyParticle(args.level, args.x, args.y, args.z, args.motionX, args.motionY, args.motionZ, args.data.getInt(0), args.data.getFloat(1), args.data.getFloat(2), args.scale, this.spriteSet);
			particle.setSprite(this.spriteSet.get(0, 1));
			return particle;
		}

		@Override
		protected void setBaseArguments(ParticleArgs<?> args) {
			args.withScale(0.4F).withData(400, 0.025F, 0.01F);
		}

		@Override
		protected void setDefaultArguments(ClientLevel level, double x, double y, double z, ParticleArgs<?> args) {
			args.withScale(0.4F * level.getRandom().nextFloat() + 0.1F);
		}
	}
}
