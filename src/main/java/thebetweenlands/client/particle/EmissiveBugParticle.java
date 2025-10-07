package thebetweenlands.client.particle;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.core.particles.SimpleParticleType;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class EmissiveBugParticle extends BugParticle {

	private final SpriteSet spriteSet;

	public EmissiveBugParticle(ClientLevel level, double x, double y, double z, double mx, double my, double mz, int maxAge, float speed, float jitter, float scale, boolean underwater, SpriteSet spriteSet) {
		super(level, x, y, z, mx, my, mz, maxAge, speed, jitter, scale, underwater);
		this.spriteSet = spriteSet;
	}

	@Override
	protected void renderRotatedQuad(VertexConsumer consumer, Quaternionf quaternion, float x, float y, float z, float partialTicks) {
		this.setSprite(this.spriteSet.get(0, 1));
		super.renderRotatedQuad(consumer, quaternion, x, y, z, partialTicks);
		this.setSprite(this.spriteSet.get(1, 1));
		float f = this.getQuadSize(partialTicks);
		float f1 = this.getU0();
		float f2 = this.getU1();
		float f3 = this.getV0();
		float f4 = this.getV1();
		int i = LightTexture.FULL_BRIGHT;
		this.renderVertex(consumer, quaternion, x, y, z, 1.0F, -1.0F, f, f2, f4, i);
		this.renderVertex(consumer, quaternion, x, y, z, 1.0F, 1.0F, f, f2, f3, i);
		this.renderVertex(consumer, quaternion, x, y, z, -1.0F, 1.0F, f, f1, f3, i);
		this.renderVertex(consumer, quaternion, x, y, z, -1.0F, -1.0F, f, f1, f4, i);
	}

	private void renderVertex(
		VertexConsumer buffer,
		Quaternionf quaternion,
		float x,
		float y,
		float z,
		float xOffset,
		float yOffset,
		float quadSize,
		float u,
		float v,
		int packedLight
	) {
		Vector3f vector3f = new Vector3f(xOffset, yOffset, 0.0F).rotate(quaternion).mul(quadSize).add(x, y, z);
		buffer.addVertex(vector3f.x(), vector3f.y(), vector3f.z())
			.setUv(u, v)
			.setColor(this.rCol, this.gCol, this.bCol, this.alpha)
			.setLight(packedLight);
	}

	public static class Factory extends ParticleFactory<EmissiveBugParticle.Factory, SimpleParticleType> {

		private final SpriteSet spriteSet;

		public Factory(SpriteSet spriteSet) {
			this.spriteSet = spriteSet;
		}

		@Override
		public EmissiveBugParticle createParticle(SimpleParticleType type, ImmutableParticleArgs args) {
			return new EmissiveBugParticle(args.level, args.x, args.y, args.z, args.motionX, args.motionY, args.motionZ, args.data.getInt(0), args.data.getFloat(1), args.data.getFloat(2), args.scale, args.data.getBool(3), this.spriteSet);
		}

		@Override
		protected void setBaseArguments(ParticleArgs<?> args) {
			args.withScale(0.5F).withData(40, 0.01F, 0.0025F, false);
		}
	}
}
