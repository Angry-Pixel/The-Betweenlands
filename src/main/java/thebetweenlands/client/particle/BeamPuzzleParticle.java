package thebetweenlands.client.particle;

import org.joml.Quaternionf;

import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SingleQuadParticle.FacingCameraMode;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import thebetweenlands.client.particle.options.BeamPuzzleParticleOptions;
import thebetweenlands.client.renderer.BLParticleRenderType;
import thebetweenlands.client.renderer.BeamRenderer;
import thebetweenlands.client.renderer.BeamRenderer.BeamVertexConsumer;
import thebetweenlands.client.shader.LightSource;
import thebetweenlands.client.shader.ShaderHelper;

public class BeamPuzzleParticle extends Particle {
	
	private static final ThreadLocal<BeamConsumerImpl> BEAM_VERTEX_CONSUMER = ThreadLocal.withInitial(BeamConsumerImpl::new);
	
	private final Vec3 targetOffset;
	private final int fadeTime;
	private final boolean useShaders;
	private final float rotationFactor;
	private final float initalRotation;
	
	private float maxAlpha;

	private float renderScale = 0.1f;
	private float texUScale = 4.0f;
	
	private float texUOffset = 0.0f;
	private float prevTexUOffset = 0.0f;

	private float rotation = 0.0f;
	private float prevRotation = 0.0f;
	
	protected BeamPuzzleParticle(
			BeamPuzzleParticleOptions options,
			ClientLevel level,
			double x, double y, double z,
			int lifetime, Vec3 targetOffset) {
		super(level, x, y, z);
		
		this.maxAlpha = options.maxAlpha();
		this.fadeTime = options.fadeTime();
		this.useShaders = options.useShaders();
		this.rotationFactor = options.rotationFactor();
		this.initalRotation = options.intialRotation() ? (float) (this.random.nextFloat() * 2 * Math.PI) : 0.0f;
		
		this.targetOffset = targetOffset;
		
		this.gravity = 0;
		this.hasPhysics = false;
		this.setParticleSpeed(0, 0, 0);
		
		this.setAlpha(this.fadeTime == 0 ? this.maxAlpha : 0.0F);
		this.setLifetime(lifetime);
	}
	
	protected BeamPuzzleParticle(
			BeamPuzzleParticleOptions options,
			ClientLevel level,
			double x, double y, double z,
			double xSpeed, double ySpeed, double zSpeed,
			int lifetime, Vec3 targetOffset) {
		super(level, x, y, z, xSpeed, ySpeed, zSpeed);
		
		this.maxAlpha = options.maxAlpha();
		this.fadeTime = options.fadeTime();
		this.useShaders = options.useShaders();
		this.rotationFactor = options.rotationFactor();
		this.initalRotation = options.intialRotation() ? (float) (this.random.nextFloat() * 2 * Math.PI) : 0.0f;
		
		this.targetOffset = targetOffset;
		
		this.gravity = 0;
		this.hasPhysics = false;
		this.setParticleSpeed(xSpeed, ySpeed, zSpeed);
		
		this.setAlpha(this.fadeTime == 0 ? this.maxAlpha : 0.0F);
		this.setLifetime(lifetime);
	}
	
	@Override
	public void setAlpha(float alpha) {
		// Update max alpha
		this.maxAlpha = alpha;
		this.alpha = getFadedAlpha(this.fadeTime, alpha, this.age, this.lifetime);
	}
	
	@Override
	public Particle scale(float scale) {
		this.renderScale = 0.1F * scale;
		this.texUScale = scale * 4.0F;
		return super.scale(scale);
	}

	@Override
	public AABB getRenderBoundingBox(float partialTicks) {
		return super.getRenderBoundingBox(partialTicks).expandTowards(this.targetOffset);
	}
	
	@Override
	public void tick() {
		/*
		// Sometimes we die faster
		if (this.random.nextInt(6) == 0) {
			this.age++;
		}
		*/
		
		super.tick();
		
		// Update alpha from fade time
		this.alpha = getFadedAlpha(this.fadeTime, this.maxAlpha, this.age, this.lifetime);
		
		this.prevRotation = this.rotation;
		this.rotation = (float) ((this.age / (float)this.lifetime) * (Math.PI * this.rotationFactor));
		
		// Update tex offset
		this.prevTexUOffset = this.texUOffset;
		this.texUOffset = -this.age / (float)this.lifetime * 4;
	}

	/**
	 * Linearly fades {@code maxAlpha} to zero as {@code age} approaches {@code 0} or {@code lifetime}.
	 * @param fadeTime
	 * @param maxAlpha
	 * @param age
	 * @param lifetime
	 * @return
	 */
	protected static float getFadedAlpha(int fadeTime, float maxAlpha, int age, int lifetime) {
		if (fadeTime == 0) {
			// If fade time is 0, then alpha always equals maxAlpha
			return maxAlpha;
		}
		
		final int fadeAge = Math.min(age, lifetime - age);

		if (fadeAge < fadeTime) {
			// Fade alpha linearly as it approaches start/end
			return (float) fadeAge / (float) fadeTime * maxAlpha;
		} else {
			// If we're not near the end of our lifetime, do not fade at all
			return maxAlpha;
		}
	}
	
	@Override
	public void render(VertexConsumer buffer, Camera renderInfo, float partialTicks) {
		if (this.removed) {
			return;
		}
		
		// Find the particle's lerped x, y, and z
		final double px = Mth.lerp((double)partialTicks, this.xo, this.x);
		final double py = Mth.lerp((double)partialTicks, this.yo, this.y);
		final double pz = Mth.lerp((double)partialTicks, this.zo, this.z);

		final float texOffset = Mth.lerp(partialTicks, this.prevTexUOffset, this.texUOffset);
		
		Quaternionf quat = new Quaternionf();
		FacingCameraMode.LOOKAT_XYZ.setRotation(quat, renderInfo, partialTicks);
		
		BeamConsumerImpl beamConsumerImpl = BEAM_VERTEX_CONSUMER.get();
		
		try {
			beamConsumerImpl.buffer = buffer;
			beamConsumerImpl.rCol = this.rCol;
			beamConsumerImpl.gCol = this.gCol;
			beamConsumerImpl.bCol = this.bCol;
			beamConsumerImpl.alpha = this.alpha;
			
			Vec3 pos = renderInfo.getPosition();
			
			final float rotationOffset = this.initalRotation + Mth.lerp(partialTicks, this.prevRotation, this.rotation);
			
			BeamRenderer.buildNewBeam(
					px - pos.x(), py - pos.y(), pz - pos.z(),
					this.targetOffset.x, this.targetOffset.y, this.targetOffset.z,
					this.renderScale, rotationOffset,
					texOffset, this.texUScale,
					quat, beamConsumerImpl);
		} finally {
			beamConsumerImpl.buffer = null;
		}
		
		// Adds a light source at the beam's origin if useShaders is set
		if (this.useShaders && ShaderHelper.INSTANCE.isWorldShaderActive()) {
			final Vec3 cameraPos = renderInfo.getPosition();

			double distFromCamSq = cameraPos.distanceToSqr(px, py, pz);
			if (distFromCamSq < 8.0 * 8.0) {
				ShaderHelper.INSTANCE.require();
				
				float strength = 1.0f * Math.min(1, (2.0f - (float)(Math.sqrt(distFromCamSq) - 6.0f)) / 2.0f) * this.alpha * 3.5f;

				ShaderHelper.INSTANCE.getWorldShader().addLight(new LightSource(
						px, py, pz,
						2.6f,
						this.rCol * strength, this.gCol * strength, this.bCol * strength));
			}
		}
	}
	
	@Override
	public ParticleRenderType getRenderType() {
		return BLParticleRenderType.BEAM;
	}
	
	public static final class Factory extends ParticleFactory<Factory, BeamPuzzleParticleOptions> {

		@Override
		public BeamPuzzleParticle createParticle(BeamPuzzleParticleOptions options, ImmutableParticleArgs args) {
			BeamPuzzleParticle particle = new BeamPuzzleParticle(
					options,
					args.level,
					args.x, args.y, args.z,
					args.motionX, args.motionY, args.motionZ,
					args.data.getInt(0), args.data.getObject(Vec3.class, 1)
				);
			particle.scale(args.scale);
			return particle;
		}

		@Override
		protected void setBaseArguments(ParticleArgs<?> args) {
			// Arg 0: lifetime (ticks)
			// Arg 1: target offset
			args.withData(20, Vec3.ZERO);
			
			// Default zero motion
			args.withMotion(Vec3.ZERO);
		}
	}
	
	private static final class BeamConsumerImpl implements BeamVertexConsumer {

		private VertexConsumer buffer;
		private float rCol, gCol, bCol, alpha;
		
		@Override
		public void emit(float x, float y, float z, float u, float v) {
			buffer.addVertex(x, y, z).setUv(u, v).setColor(this.rCol, this.gCol, this.bCol, this.alpha).setLight(LightTexture.FULL_BRIGHT).setOverlay(OverlayTexture.NO_OVERLAY);
		}
		
	}
}
