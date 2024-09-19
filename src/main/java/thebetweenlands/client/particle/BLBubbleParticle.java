package thebetweenlands.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.BubbleParticle;
import net.minecraft.util.Mth;

public class BLBubbleParticle extends BubbleParticle {
	protected BLBubbleParticle(ClientLevel level, double x, double y, double z, double vecX, double vecY, double vecZ, float scale) {
		super(level, x, y, z, vecX, vecY, vecZ);
		this.quadSize = scale;
		this.xd = vecX + (Math.random() * 2.0D - 1.0D) * 0.4000000059604645D;
		this.yd = vecY + (Math.random() * 2.0D - 1.0D) * 0.4000000059604645D;
		this.zd = vecZ + (Math.random() * 2.0D - 1.0D) * 0.4000000059604645D;
		float f = (float)(Math.random() + Math.random() + 1.0D) * 0.15F;
		float f1 = Mth.sqrt((float) (this.xd * this.xd + this.yd * this.yd + this.zd * this.zd));
		this.xd = this.xd / (double)f1 * (double)f * 0.3D;
		this.yd = this.yd / (double)f1 * (double)f * 0.4D + 0.3D;
		this.zd = this.zd / (double)f1 * (double)f * 0.3D;
		this.quadSize = scale;
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
}
