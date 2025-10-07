package thebetweenlands.client.particle;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.function.Supplier;

public class EmissiveSwarmParticle extends SwarmParticle {

	private final SpriteSet spriteSet;

	public EmissiveSwarmParticle(ClientLevel level, double x, double y, double z, double mx, double my, double mz, Direction face, float scale, int maxAge, Vec3 start, Supplier<Vec3> end, SpriteSet spriteSet) {
		super(level, x, y, z, mx, my, mz, face, scale * 2, maxAge, start, end);
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

	public static class Factory extends ParticleFactory<EmissiveSwarmParticle.Factory, SimpleParticleType> {

		private final SpriteSet spriteSet;

		public Factory(SpriteSet spriteSet) {
			this.spriteSet = spriteSet;
		}

		@Override
		public EmissiveSwarmParticle createParticle(SimpleParticleType type, ImmutableParticleArgs args) {
			return new EmissiveSwarmParticle(args.level, args.x, args.y, args.z, args.motionX, args.motionY, args.motionZ, args.data.getObject(Direction.class, 0), args.scale, args.data.getInt(1), args.data.getObject(Vec3.class, 2), args.data.getObject(Supplier.class, 3), this.spriteSet);
		}

		@Override
		protected void setBaseArguments(ParticleArgs<?> args) {
			args.withData(Direction.UP, 40, Vec3.ZERO, (Supplier<Vec3>) () -> Vec3.ZERO);
		}
	}
}
