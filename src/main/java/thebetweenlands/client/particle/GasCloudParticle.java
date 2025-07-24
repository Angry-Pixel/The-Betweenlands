package thebetweenlands.client.particle;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import org.joml.Quaternionf;
import thebetweenlands.client.renderer.BLParticleRenderType;

// Standard render particle
public class GasCloudParticle extends TextureSheetParticle {

	public float particleAngle;
	public float prevParticleAngle = 0;
	public boolean rotateReversed = false;

	protected GasCloudParticle(ClientLevel level, double x, double y, double z, double vx, double vy, double vz, float startRotation, float scale) {
		super(level, x + (level.random.nextFloat() - level.random.nextFloat()) * 0.05F,
			y + (level.random.nextFloat() - level.random.nextFloat()) * 0.05F,
			z + (level.random.nextFloat() - level.random.nextFloat()) * 0.05F);
		this.xd = xd * 0.01D + (vx*0.75);
		this.yd = yd * 0.01D + (vy*0.75);
		this.zd = zd * 0.01D + (vz*0.75);
		this.roll = this.oRoll = startRotation;
		this.hasPhysics = false;
		this.quadSize = scale == 0.0F ? (level.random.nextFloat() * 0.75F + 0.6F) * 0.75F : scale;
		this.lifetime = 60;
		// (level.random.nextFloat() * 0.75F + 0.6F) * 0.7F
		if(startRotation < 0.0F) {
			rotateReversed = true;
		}
	}

	public ParticleRenderType getRenderType() {
		return BLParticleRenderType.BL_GAS_CLOUD;
	}

	protected float getU0() {
		return 0.0f;
	}

	protected float getU1() {
		return 1.0f;
	}

	protected float getV0() {
		return 0.0f;
	}

	protected float getV1() {
		return 1.0f;
	}

	/**
	 * Renders this particle with the UVs [0, 0] to [1, 1]
	 */
	public void render(VertexConsumer buffer, Camera renderInfo, float partialTicks) {
		Quaternionf quaternionf = new Quaternionf();
		this.getFacingCameraMode().setRotation(quaternionf, renderInfo, partialTicks);
		if (this.roll != 0.0F) {
			quaternionf.rotateZ(Mth.lerp(partialTicks, this.oRoll, this.roll));
		}

		int fadeInDuration = 15;
		int fadeOutStart = 45;
		int fadeOutDuration = this.lifetime - fadeOutStart;
		float a = this.alpha;

		if(this.age > fadeOutStart) {
			this.alpha *= (1.0F - Math.min((float)(this.age - fadeOutStart) / (float)fadeOutDuration, 1.0F));
		}
		else {
			this.alpha *=  Math.min((float)this.age / (float)fadeInDuration, 1.0F);
		}
		this.renderRotatedQuad(buffer, renderInfo, quaternionf, partialTicks);

		this.alpha = a;
	}

	@Override
	public void tick() {
		super.tick();
		this.oRoll = this.roll;
		this.roll += this.rotateReversed ? -0.015F : 0.015F;
	}

	public static final class GasCloudFactory extends ParticleFactory<GasCloudFactory, SimpleParticleType> {
		private final SpriteSet spriteSet;

		public GasCloudFactory(SpriteSet spriteSet) {
			this.spriteSet = spriteSet;
		}

		@Override
		public GasCloudParticle createParticle(SimpleParticleType type, ImmutableParticleArgs args) {
			var particle = new GasCloudParticle(args.level, args.x, args.y, args.z, args.motionX, args.motionY, args.motionZ, args.data.getFloat(1), args.scale);
			particle.pickSprite(this.spriteSet);
			return particle;
		}

		@Override
		protected void setBaseArguments(ParticleArgs<?> args) {
			args.withData(0.0F, 0.025F, 0.01F).withScale(0.0F);
			//args.withData(null, (level.random.nextFloat() * 2.0F * (float)Math.PI) * 2.0F - 2.0F * (float)Math.PI).withScale((level.random.nextFloat() * 0.75F + 0.6F) * 10.0F);
		}

		@Override
		protected void setDefaultArguments(ClientLevel level, double x, double y, double z, ParticleArgs<?> args) {
			args.withData(level, (level.random.nextFloat() * 2.0F * (float)Math.PI) * 2.0F - 2.0F * (float)Math.PI).withScale((level.random.nextFloat() * 0.75F + 0.6F) * 0.75F);
		}
	}
}
