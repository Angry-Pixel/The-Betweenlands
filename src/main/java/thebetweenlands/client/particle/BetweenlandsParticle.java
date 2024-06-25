package thebetweenlands.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.SimpleParticleType;

public class BetweenlandsParticle extends TextureSheetParticle {

	protected final SpriteSet spriteSet;

	protected BetweenlandsParticle(ClientLevel level, SpriteSet spriteSet, double X, double Y, double Z, double XSpeed, double YSpeed, double ZSpeed) {
		super(level, X, Y, Z, XSpeed, YSpeed, ZSpeed);
		this.spriteSet = spriteSet;
		this.xd = XSpeed;
		this.yd = YSpeed;
		this.zd = ZSpeed;

		this.gravity = -0.01f;

		this.setSize(0.02f, 0.02f);
		this.lifetime = 10;

	}

	@Override
	public void tick() {

		// Ajust and apply momentem
		this.xo = this.x;
		this.yo = this.y;
		this.zo = this.z;

		this.move(this.xd, this.yd, this.zd);

		// Apply slow down effect on particle
		this.xd -= this.xd * 0.3f;
		this.yd -= this.yd * 0.2f;
		this.zd -= this.zd * 0.3f;

		// Apply gravity effect on particle
		this.yd -= this.gravity;

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
			BetweenlandsParticle betweenlandsParticle = new BetweenlandsParticle(level, spriteSet, x, y, z, XSpeed, YSpeed, ZSpeed);
			betweenlandsParticle.setColor(24.0f, 8.0f, 0.0f);
			betweenlandsParticle.setSpriteFromAge(this.spriteSet);
			return betweenlandsParticle;
		}

	}

}
