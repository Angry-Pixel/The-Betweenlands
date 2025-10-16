package thebetweenlands.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.SimpleParticleType;
import thebetweenlands.client.renderer.BLParticleRenderType;

public class SimpleParticle extends TextureSheetParticle {

	private float startAlpha = 1.0F;
	private boolean fade = false;

	protected SimpleParticle(ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, int maxAge, float scale, boolean fade, float gravity, boolean exactMotion) {
		super(level, x, y, z, xSpeed, ySpeed, zSpeed);
		this.lifetime = maxAge;
		this.scale(scale);
		this.gravity = gravity;
		this.fade = fade;
		if (exactMotion) {
			this.xd = xSpeed;
			this.yd = ySpeed;
			this.zd = zSpeed;
		}
		if (fade) {
			this.alpha = 0;
		}
	}

	@Override
	public void setAlpha(float alpha) {
		super.setAlpha(alpha);
		this.startAlpha = alpha;
		if (this.fade) {
			this.alpha = 0;
		}
	}

	@Override
	public void tick() {
		super.tick();

		if (this.fade) {
			int fadeOutTime = Math.max(1, Math.min(40, (this.lifetime - 10) / 2));
			if (this.age > this.lifetime - fadeOutTime) {
				this.alpha = (this.startAlpha * (this.lifetime - this.age) / (float) fadeOutTime);
			} else if (this.age <= 10) {
				this.alpha = this.startAlpha * this.age / 10.0f;
			}
		}
	}

	@Override
	public ParticleRenderType getRenderType() {
		return BLParticleRenderType.TRANSLUCENT_ALPHA_LENIENT;
	}

	public static final class Factory extends ParticleFactory<SimpleParticle.Factory, SimpleParticleType> {

		private final SpriteSet spriteSet;

		public Factory(SpriteSet spriteSet) {
			this.spriteSet = spriteSet;
		}

		@Override
		public SimpleParticle createParticle(SimpleParticleType options, ImmutableParticleArgs args) {
			var particle = new SimpleParticle(args.level, args.x, args.y, args.z, args.motionX, args.motionY, args.motionZ, args.data.getInt(0), args.scale, args.data.getBool(1), args.data.getFloat(2), args.data.getBool(3));
			particle.pickSprite(this.spriteSet);
			return particle;
		}

		@Override
		protected void setBaseArguments(ParticleArgs<?> args) {
			args.withData(80, true, 0.0F, false);
		}
	}

	public static final class CorruptedFactory extends ParticleFactory<SimpleParticle.Factory, SimpleParticleType> {

		private final SpriteSet spriteSet;

		public CorruptedFactory(SpriteSet spriteSet) {
			this.spriteSet = spriteSet;
		}

		@Override
		public SimpleParticle createParticle(SimpleParticleType options, ImmutableParticleArgs args) {
			var particle = new SimpleParticle(args.level, args.x, args.y, args.z, args.motionX, args.motionY, args.motionZ, args.data.getInt(0), args.scale, args.data.getBool(1), args.data.getFloat(2), args.data.getBool(3));
			particle.pickSprite(this.spriteSet);
			return particle;
		}

		@Override
		protected void setBaseArguments(ParticleArgs<?> args) {
			args.withData(80, true, 1.0F, false);
		}
	}
}
