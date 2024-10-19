package thebetweenlands.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.SimpleParticleType;

public class DruidCastingParticle extends TextureSheetParticle {

	protected DruidCastingParticle(ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, float scale) {
		super(level, x, y, z, xSpeed, ySpeed, zSpeed);
		this.xd = xSpeed;
		this.yd = ySpeed;
		this.zd = zSpeed;
		float colorMulti = level.getRandom().nextFloat() * 0.3F;
		this.scale(scale);
		this.rCol = this.gCol = this.bCol = colorMulti;
		this.lifetime = (int) (Math.random() * 10.0D) + 40;
		//this.hasPhysics = true;
	}

	@Override
	protected int getLightColor(float partialTick) {
		int i = super.getLightColor(partialTick);
		float f1 = (float) this.age / (float) this.lifetime;
		f1 *= f1;
		f1 *= f1;
		int j = i & 255;
		int k = i >> 16 & 255;
		k += (int) (f1 * 15.0F * 16.0F);

		if( k > 240 ) {
			k = 240;
		}

		return j | k << 16;
	}

	@Override
	public void tick() {
		this.xo = this.x;
		this.yo = this.y;
		this.zo = this.z;

		if( this.age++ >= this.lifetime ) {
			this.remove();
		}

		this.move(this.xd, this.yd, this.zd);
		this.xd *= 0.8599999785423279D;
		this.yd *= 0.2599999785423279D;
		this.zd *= 0.8599999785423279D;
	}

	@Override
	public ParticleRenderType getRenderType() {
		return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
	}

	public static class Factory extends ParticleFactory<Factory, SimpleParticleType> {

		private final SpriteSet spriteSet;

		public Factory(SpriteSet spriteSet) {
			this.spriteSet = spriteSet;
		}

		@Override
		public DruidCastingParticle createParticle(SimpleParticleType type, ImmutableParticleArgs args) {
			var particle = new DruidCastingParticle(args.level, args.x, args.y, args.z, args.motionX, args.motionY, args.motionZ, args.scale);
			particle.pickSprite(this.spriteSet);
			return particle;
		}
	}
}
