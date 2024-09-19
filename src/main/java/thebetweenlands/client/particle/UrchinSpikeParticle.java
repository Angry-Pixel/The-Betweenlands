package thebetweenlands.client.particle;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.FastColor;
import thebetweenlands.client.renderer.SpikeRenderer;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.registries.SoundRegistry;

import javax.annotation.Nullable;

public class UrchinSpikeParticle extends Particle {
	private final RenderType renderType = RenderType.entityTranslucent(TheBetweenlands.prefix("textures/entity/urchin_spike.png"));

	@Nullable
	private SpikeRenderer renderer;

	private final int length;
	private final float scale;
	private final float width;
	private final long seed;

	private double prevMotionX, prevMotionY, prevMotionZ;

	private boolean sound;

	protected UrchinSpikeParticle(ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, int length, float width, float scale, long seed) {
		super(level, x, y, z);
		this.prevMotionX = this.xd = xSpeed;
		this.prevMotionY = this.yd = ySpeed;
		this.prevMotionZ = this.zd = zSpeed;
		this.length = length;
		this.width = width;
		this.scale = scale;
		this.seed = seed;
		this.gravity = 1;
		this.lifetime = 20 * 3;
		this.sound = level.getRandom().nextInt(15) == 0;
	}

	@Override
	public void tick() {
		this.prevMotionX = this.xd;
		this.prevMotionY = this.yd;
		this.prevMotionZ = this.zd;

		super.tick();

		if (this.onGround) {
			this.xd *= 0.01F;
			this.yd *= 0.01F;
			this.zd *= 0.01F;

			if (this.sound) {
				this.sound = false;
				this.level.playLocalSound(this.x, this.y, this.z, SoundRegistry.ROOT_SPIKE_PARTICLE_HIT.get(), SoundSource.HOSTILE, 1, 0.9F + this.random.nextFloat() * 0.2F, false);
			}
		}
	}

	@Override
	public void render(VertexConsumer consumer, Camera camera, float partialTicks) {
		if (this.renderer == null) {
			this.renderer = new SpikeRenderer(this.length, this.width, 1.0F, 1, this.seed);
		}

		int i = this.getLightColor(partialTicks);

		float rx = (float) (this.xo + (this.x - this.xo) * (double) partialTicks - camera.getPosition().x());
		float ry = (float) (this.yo + (this.y - this.yo) * (double) partialTicks - camera.getPosition().y());
		float rz = (float) (this.zo + (this.z - this.zo) * (double) partialTicks - camera.getPosition().z());

		PoseStack stack = new PoseStack();

		float alpha = 1.0F;

		if (this.age >= this.lifetime - 10) {
			alpha = 1.0F - (this.age - (this.lifetime - 10)) / 10.0F;
		}

		stack.translate(rx, ry, rz);

		double mx = this.prevMotionX + (this.xd - this.prevMotionX) * partialTicks;
		double my = this.prevMotionY + (this.yd - this.prevMotionY) * partialTicks;
		double mz = this.prevMotionZ + (this.zd - this.prevMotionZ) * partialTicks;

		stack.mulPose(Axis.YP.rotationDegrees(-(float) Math.toDegrees(Math.atan2(mz, mx))));
		stack.mulPose(Axis.ZP.rotationDegrees((float) Math.toDegrees(Math.atan2(Math.sqrt(mx * mx + mz * mz), -my)) + 180));
		stack.translate(0, -0.5F * this.scale, 0);
		stack.scale(this.scale, this.scale, this.scale);
		MultiBufferSource.BufferSource source = Minecraft.getInstance().renderBuffers().bufferSource();
		VertexConsumer vertexconsumer = source.getBuffer(this.renderType);
		this.renderer.build(stack.last(), vertexconsumer, i, OverlayTexture.NO_OVERLAY, FastColor.ARGB32.colorFromFloat(alpha, 1.0F, 1.0F, 1.0F));
		source.endBatch();
	}

	@Override
	public ParticleRenderType getRenderType() {
		return ParticleRenderType.CUSTOM;
	}

	public static final class Factory extends ParticleFactory<Factory, SimpleParticleType> {

		@Override
		public UrchinSpikeParticle createParticle(SimpleParticleType type, ImmutableParticleArgs args) {
			return new UrchinSpikeParticle(args.level, args.x, args.y, args.z, args.motionX, args.motionY, args.motionZ, args.data.getInt(0), args.data.getFloat(2), args.scale, args.data.getLong(1));
		}

		@Override
		protected void setBaseArguments(ParticleArgs<?> args) {
			args.withData(2, 1234L, 0.25F).withScale(0.5F);
		}

		@Override
		protected void setDefaultArguments(ClientLevel level, double x, double y, double z, ParticleArgs<?> args) {
			args.withData(2, level.getRandom().nextLong(), 0.25F);
		}
	}
}
