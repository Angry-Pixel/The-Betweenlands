package thebetweenlands.common.particles;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;

public class BetweenlandsPortalParticle extends BetweenlandsParticle {

	protected BetweenlandsPortalParticle(ClientLevel level, SpriteSet spriteSet, double X, double Y, double Z, double XSpeed, double YSpeed, double ZSpeed) {
		super(level, spriteSet, X, Y, Z, XSpeed, YSpeed, ZSpeed);
		this.xd = XSpeed;
		this.yd = YSpeed;
		this.zd = ZSpeed;

		this.gravity = 0;

		this.setSize(0.02f, 0.02f);
		this.lifetime = 20;
	}

	@Override
	public void tick() {

		// Ajust and apply momentem
		this.xo = this.x;
		this.yo = this.y;
		this.zo = this.z;

		this.move(this.xd, this.yd, this.zd);

		// Change particle texture
		this.setSpriteFromAge(this.spriteSet);

		// Iterate age and check lifetime left
		this.age++;

		if (this.lifetime <= this.age) {
			this.remove();
		}
	}

	@Override
	public ParticleRenderType getRenderType() {
		return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
	}

	public static class Helper implements ParticleProvider<SimpleParticleType> {
		private final SpriteSet spriteSet;

		public Helper(SpriteSet sprite) {
			this.spriteSet = sprite;
		}

		@Override
		public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double XSpeed, double YSpeed, double ZSpeed) {
			BetweenlandsPortalParticle BetweenlandsPortalParticle = new BetweenlandsPortalParticle(level, spriteSet, x, y, z, XSpeed, YSpeed, ZSpeed);
			BetweenlandsPortalParticle.setSpriteFromAge(this.spriteSet);
			return BetweenlandsPortalParticle;
		}

	}
}
