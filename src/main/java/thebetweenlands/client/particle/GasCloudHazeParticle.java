package thebetweenlands.client.particle;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import thebetweenlands.client.renderer.BLParticleRenderType;
import thebetweenlands.client.shader.ShaderHelper;

// Haze buffer particle
public class GasCloudHazeParticle extends GasCloudParticle {

	protected GasCloudHazeParticle(ClientLevel level, double x, double y, double z, double vx, double vy, double vz, float startRotation, float scale) {
		super(level, x, y, z ,vx, vy, vz, startRotation, scale);
	}

	public ParticleRenderType getRenderType() {
		return BLParticleRenderType.BL_HAZE_CLOUD;
	}

	public void render(VertexConsumer buffer, Camera renderInfo, float partialTicks) {
		if (ShaderHelper.INSTANCE.canUseShaders()) super.render(buffer, renderInfo, partialTicks);
	}

	public static final class GasCloudHazeFactory extends ParticleFactory<GasCloudHazeFactory, SimpleParticleType> {
		private final SpriteSet spriteSet;

		public GasCloudHazeFactory(SpriteSet spriteSet) {
			this.spriteSet = spriteSet;
		}

		@Override
		public GasCloudHazeParticle createParticle(SimpleParticleType type, ImmutableParticleArgs args) {
			var particle = new GasCloudHazeParticle(args.level, args.x, args.y, args.z, args.motionX, args.motionY, args.motionZ, args.data.getFloat(1), args.scale);
			particle.pickSprite(this.spriteSet);
			return particle;
		}

		@Override
		protected void setBaseArguments(ParticleArgs<?> args) {
			args.withData(0.0F, 0.025F, 0.01F).withScale(0.0F);
		}

		@Override
		protected void setDefaultArguments(ClientLevel level, double x, double y, double z, ParticleArgs<?> args) {
			args.withData(level, (level.random.nextFloat() * 2.0F * Mth.PI) * 2.0F - 2.0F * Mth.PI).withScale((level.random.nextFloat() * 0.75F + 0.6F));
		}
	}
}
