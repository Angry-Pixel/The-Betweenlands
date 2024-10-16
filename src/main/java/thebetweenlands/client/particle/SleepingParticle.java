package thebetweenlands.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.util.Mth;

public class SleepingParticle extends TextureSheetParticle {
	public SleepingParticle(ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
		super(level, x, y, z, 0.0D, 0.0D, 0.0D);
		this.xd = 0.025D;
		this.yd = 0.0D;
		this.zd = 0.025D;
		this.gravity = -0.02F;
		this.quadSize = 0.0F;
		this.lifetime = 60;
	}

	@Override
	public void tick() {
		super.tick();
		this.quadSize = ((float) this.age / 100) * 0.25F;
		this.setAlpha(1.0F - ((float) this.age / this.lifetime));
		this.oRoll = roll;
		this.roll = Mth.sin((float) this.age / 4) / 5;
	}

	@Override
	public FacingCameraMode getFacingCameraMode() {
		return FacingCameraMode.LOOKAT_Y;
	}

	@Override
	public ParticleRenderType getRenderType() {
		return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
	}
}
