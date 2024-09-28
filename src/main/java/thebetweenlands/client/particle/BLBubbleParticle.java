package thebetweenlands.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.BubbleParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;

public class BLBubbleParticle extends BubbleParticle {
	protected BLBubbleParticle(ClientLevel level, double x, double y, double z, double vecX, double vecY, double vecZ, float scale) {
		super(level, x, y, z, vecX, vecY, vecZ);
		this.scale(scale);
		this.xd = vecX + (Math.random() * 2.0D - 1.0D) * 0.4000000059604645D;
		this.yd = vecY + (Math.random() * 2.0D - 1.0D) * 0.4000000059604645D;
		this.zd = vecZ + (Math.random() * 2.0D - 1.0D) * 0.4000000059604645D;
		float f = (float)(Math.random() + Math.random() + 1.0D) * 0.15F;
		float f1 = Mth.sqrt((float) (this.xd * this.xd + this.yd * this.yd + this.zd * this.zd));
		this.xd = this.xd / (double)f1 * (double)f * 0.3D;
		this.yd = this.yd / (double)f1 * (double)f * 0.4D + 0.3D;
		this.zd = this.zd / (double)f1 * (double)f * 0.3D;
	}

	@Override
	public void tick() {
		this.xo = this.x;
		this.yo = this.y;
		this.zo = this.z;
		if (this.lifetime-- <= 0) {
			this.remove();
		} else {
			this.yd += 0.002;
			this.move(this.xd, this.yd, this.zd);
			this.xd *= 0.85F;
			this.yd *= 0.85F;
			this.zd *= 0.85F;
		}
	}

	public static final class Factory extends ParticleFactory<InfuserFactory, SimpleParticleType> {

		private final SpriteSet spriteSet;

		public Factory(SpriteSet spriteSet) {
			this.spriteSet = spriteSet;
		}

		@Override
		public BLBubbleParticle createParticle(SimpleParticleType type, ImmutableParticleArgs args) {
			var particle = new BLBubbleParticle(args.level, args.x, args.y, args.z, args.motionX, args.motionY, args.motionZ, args.scale);
			particle.pickSprite(this.spriteSet);
			return particle;
		}
	}

	public static final class InfuserFactory extends ParticleFactory<InfuserFactory, SimpleParticleType> {

		private final SpriteSet spriteSet;

		public InfuserFactory(SpriteSet spriteSet) {
			this.spriteSet = spriteSet;
		}

		@Override
		public BLBubbleParticle createParticle(SimpleParticleType type, ImmutableParticleArgs args) {
			var particle = new BLBubbleParticle(args.level, args.x, args.y, args.z, args.motionX, args.motionY, args.motionZ, args.scale);
			particle.pickSprite(this.spriteSet);
			return particle;
		}

		@Override
		protected void setDefaultArguments(ClientLevel level, double x, double y, double z, ParticleArgs<?> args) {
			args.withColor(0.5F, 0F, 0.125F, 1.0F);
		}
	}

	public static final class PurifierFactory extends ParticleFactory<InfuserFactory, SimpleParticleType> {

		private final SpriteSet spriteSet;

		public PurifierFactory(SpriteSet spriteSet) {
			this.spriteSet = spriteSet;
		}

		@Override
		public BLBubbleParticle createParticle(SimpleParticleType type, ImmutableParticleArgs args) {
			var particle = new BLBubbleParticle(args.level, args.x, args.y, args.z, args.motionX, args.motionY, args.motionZ, args.scale);
			particle.pickSprite(this.spriteSet);
			return particle;
		}

		@Override
		protected void setDefaultArguments(ClientLevel level, double x, double y, double z, ParticleArgs<?> args) {
			args.withColor(0.306F, 0.576F, 0.192F, 1.0F);
		}
	}

	public static final class TarFactory extends ParticleFactory<InfuserFactory, SimpleParticleType> {

		private final SpriteSet spriteSet;

		public TarFactory(SpriteSet spriteSet) {
			this.spriteSet = spriteSet;
		}

		@Override
		public BLBubbleParticle createParticle(SimpleParticleType type, ImmutableParticleArgs args) {
			var particle = new BLBubbleParticle(args.level, args.x, args.y, args.z, args.motionX, args.motionY, args.motionZ, args.scale);
			particle.pickSprite(this.spriteSet);
			return particle;
		}

		@Override
		protected void setDefaultArguments(ClientLevel level, double x, double y, double z, ParticleArgs<?> args) {
			args.withColor(0, 0, 0, 1.0F);
		}
	}
}
