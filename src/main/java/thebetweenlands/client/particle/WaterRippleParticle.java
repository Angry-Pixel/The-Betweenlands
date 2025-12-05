package thebetweenlands.client.particle;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import thebetweenlands.client.renderer.BLParticleRenderType;

public class WaterRippleParticle extends TextureSheetParticle {

	private final SpriteSet set;

	protected WaterRippleParticle(ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, SpriteSet set) {
		super(level, x, y, z, xSpeed, ySpeed, zSpeed);
		this.xd = 0.0D;
		this.yd = 0.0D;
		this.zd = 0.0D;
		this.set = set;
		this.lifetime = 6;
		this.quadSize = 0.5F;
		this.setSpriteFromAge(this.set);
		this.x = Math.round(this.x / (1F / 16F)) * (1F / 16F);
		this.z = Math.round(this.z / (1F / 16F)) * (1F / 16F);
	}

	@Override
	public void tick() {
		this.xo = this.x;
		this.yo = this.y;
		this.zo = this.z;
		this.setSpriteFromAge(this.set);
		if (this.age++ >= this.lifetime) {
			this.remove();
		}
	}

	@Override
	public void render(VertexConsumer consumer, Camera camera, float partialTicks) {
		Vec3 camPos = camera.getPosition();
		float x = (float) (Mth.lerp(partialTicks, this.xo, this.x) - camPos.x());
		float y = (float) (Mth.lerp(partialTicks, this.yo, this.y) - camPos.y());
		float z = (float) (Mth.lerp(partialTicks, this.zo, this.z) - camPos.z());

		this.renderRotatedQuad(consumer, Axis.XP.rotationDegrees(-90.0F), x, y, z, partialTicks);
		this.renderRotatedQuad(consumer, Axis.XP.rotationDegrees(90.0F), x, y, z, partialTicks);
	}

	@Override
	public ParticleRenderType getRenderType() {
		return BLParticleRenderType.TRANSLUCENT_ALPHA_LENIENT;
	}

	public static final class Factory extends ParticleFactory<WaterRippleParticle.Factory, SimpleParticleType> {
		private final SpriteSet spriteSet;

		public Factory(SpriteSet spriteSet) {
			this.spriteSet = spriteSet;
		}

		@Override
		public WaterRippleParticle createParticle(SimpleParticleType type, ParticleFactory.ImmutableParticleArgs args) {
			return new WaterRippleParticle(args.level, args.x, args.y, args.z, args.motionX, args.motionY, args.motionZ, this.spriteSet);
		}
	}
}
