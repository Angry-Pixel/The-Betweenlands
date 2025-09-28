package thebetweenlands.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.SimpleParticleType;

public class ScrapParticle extends TextureSheetParticle {

	public static final int LEAF_COLOR = 0XFF3F652D;

	public ScrapParticle(ClientLevel level, double x, double y, double z, double mx, double my, double mz, int maxAge, float scale) {
		super(level, x, y, z);
		this.x = this.xo = x;
		this.y = this.yo = y;
		this.z = this.zo = z;
		this.xd = mx;
		this.yd = my;
		this.zd = mz;
		this.lifetime = maxAge;
		this.scale(scale);
		this.gravity = 0.16F;
	}

	@Override
	public void tick() {
		this.xo = this.x;
		this.yo = this.y;
		this.zo = this.z;
		this.yd -= 0.04D * this.gravity;
		this.move(this.xd, this.yd, this.zd);
		this.xd *= 0.98D;
		this.yd *= 0.98D;
		this.zd *= 0.98D;
		this.removed = this.yo == this.y;

		if (this.lifetime-- <= 40) {
			this.setAlpha((float)this.lifetime / 40.0F);

			if (this.lifetime-- <= 0) {
				this.remove();
			}
		}

		if (this.removed) {
			this.xd *= 0.7D;
			this.zd *= 0.7D;

			if(this.lifetime > 120) {
				this.lifetime = 120;
			}
		}
	}

	@Override
	public ParticleRenderType getRenderType() {
		return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
	}

	public static final class Factory extends ParticleFactory<Factory, SimpleParticleType> {

		private final SpriteSet spriteSet;

		public Factory(SpriteSet spriteSet) {
			this.spriteSet = spriteSet;
		}

		@Override
		public ScrapParticle createParticle(SimpleParticleType options, ImmutableParticleArgs args) {
			var particle = new ScrapParticle(args.level, args.x, args.y, args.z, args.motionX, args.motionY, args.motionZ, args.data.getInt(0), args.scale);
			particle.pickSprite(this.spriteSet);
			return particle;
		}

		@Override
		protected void setBaseArguments(ParticleArgs<?> args) {
			args.withData(400);
		}
	}
}
