package thebetweenlands.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import thebetweenlands.client.particle.options.DripParticleOptions;
import thebetweenlands.common.TheBetweenlands;

public class FancyBubbleParticle extends BLBubbleParticle {

	private final boolean floating;

	protected FancyBubbleParticle(ClientLevel level, double x, double y, double z, double vecX, double vecY, double vecZ, float scale, boolean floating) {
		super(level, x, y, z, vecX, vecY, vecZ, scale);
		this.floating = floating;
		if(floating) {
			this.xd = vecX;
			this.yd = vecY;
			this.zd = vecZ;
			this.gravity = 0.0f;
			this.lifetime = level.getRandom().nextInt(40) + 20;
		}
	}

	@Override
	public void tick() {
		double prevMotionX = this.xd;
		double prevMotionY = this.yd;
		double prevMotionZ = this.zd;

		super.tick();

		if(this.floating) {
			this.xd = prevMotionX * 0.95f;
			this.yd = prevMotionY * 0.95f;
			this.zd = prevMotionZ * 0.95f;
		}

		if (this.lifetime <= 0) {
			for(int j = 0; j < 2; ++j) {
				double velX = (this.random.nextFloat() - 0.5f) * 0.15f;
				double velY = 0.075f + (this.random.nextFloat() - 0.5f) * 0.15f;
				double velZ = (this.random.nextFloat() - 0.5f) * 0.15f;
				TheBetweenlands.createParticle(new DripParticleOptions(true, true), this.level, this.x, this.y, this.z, ParticleFactory.ParticleArgs.get().withMotion(velX, velY, velZ).withScale(this.quadSize * (0.75f + 0.5f * this.level.getRandom().nextFloat())).withColor(this.rCol * 0.8f, this.gCol * 0.8f, this.bCol * 0.8f, this.alpha));
			}
		}
	}

	public static final class Factory extends ParticleFactory<Factory, SimpleParticleType> {

		private final SpriteSet sprite;

		public Factory(SpriteSet sprites) {
			this.sprite = sprites;
		}

		@Override
		public FancyBubbleParticle createParticle(SimpleParticleType type, ImmutableParticleArgs args) {
			var bubble = new FancyBubbleParticle(args.level, args.x, args.y, args.z, args.motionX, args.motionY, args.motionZ, args.scale, args.data.getBool(0));
			bubble.pickSprite(this.sprite);
			return bubble;
		}

		@Override
		protected void setBaseArguments(ParticleArgs<?> args) {
			args.withData(false);
		}

		@Override
		protected void setDefaultArguments(ClientLevel level, double x, double y, double z, ParticleArgs<?> args) {
			args.withData(false);
		}
	}
}
