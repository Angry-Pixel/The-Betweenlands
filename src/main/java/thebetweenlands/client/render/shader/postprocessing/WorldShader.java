package thebetweenlands.client.render.shader.postprocessing;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Matrix4f;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import thebetweenlands.client.handler.FogHandler;
import thebetweenlands.client.render.particle.DefaultParticleBatches;
import thebetweenlands.client.render.shader.DepthBuffer;
import thebetweenlands.client.render.shader.GeometryBuffer;
import thebetweenlands.client.render.shader.LightSource;
import thebetweenlands.client.render.shader.ResizableFramebuffer;
import thebetweenlands.client.render.shader.postprocessing.GroundFog.GroundFogVolume;
import thebetweenlands.client.render.sky.BLSkyRenderer;
import thebetweenlands.client.render.sprite.GLTextureObjectWrapper;
import thebetweenlands.common.config.BetweenlandsConfig;
import thebetweenlands.common.entity.mobs.EntityGasCloud;
import thebetweenlands.common.lib.ModInfo;
import thebetweenlands.common.world.event.BLEnvironmentEventRegistry;
import thebetweenlands.common.world.storage.BetweenlandsWorldStorage;
import thebetweenlands.util.GLUProjection;
import thebetweenlands.util.GLUProjection.ClampMode;
import thebetweenlands.util.GLUProjection.Projection;
import thebetweenlands.util.RenderUtils;

/**
 * TODO: Make lighting and other spacial effects use "correct" deferred rendering
 */
public class WorldShader extends PostProcessingEffect<WorldShader> {
	public static final ResourceLocation WORLD_DEPTH_TEXTURE = new ResourceLocation(ModInfo.ID, "world_depth");
	public static final ResourceLocation REPELLER_DIFFUSE_TEXTURE = new ResourceLocation(ModInfo.ID, "repeller_diffuse");
	public static final ResourceLocation REPELLER_DEPTH_TEXTURE = new ResourceLocation(ModInfo.ID, "repeller_depth");
	public static final ResourceLocation GAS_PARTICLES_DIFFUSE_TEXTURE = new ResourceLocation(ModInfo.ID, "gas_particles_diffuse");
	public static final ResourceLocation GAS_PARTICLES_DEPTH_TEXTURE = new ResourceLocation(ModInfo.ID, "gas_particles_depth");
	public static final ResourceLocation CLIP_PLANE_DIFFUSE_TEXTURE = new ResourceLocation(ModInfo.ID, "clip_plane_diffuse");
	public static final ResourceLocation CLIP_PLANE_DEPTH_TEXTURE = new ResourceLocation(ModInfo.ID, "clip_plane_depth");

	public static final ResourceLocation GAS_PARTICLE_TEXTURE = new ResourceLocation(ModInfo.ID, "gas_particle");
	
	private DepthBuffer depthBuffer;
	private ResizableFramebuffer blitBuffer;
	private ResizableFramebuffer occlusionBuffer;
	private GeometryBuffer repellerShieldBuffer;
	private GeometryBuffer gasParticlesBuffer;

	private static final FloatBuffer MODELVIEW = GLAllocation.createDirectFloatBuffer(16);
	private static final FloatBuffer PROJECTION = GLAllocation.createDirectFloatBuffer(16);

	private Matrix4f invertedModelviewProjectionMatrix;
	private Matrix4f modelviewProjectionMatrix;
	private Matrix4f modelviewMatrix;
	private Matrix4f projectionMatrix;

	public static final int MAX_LIGHT_SOURCES_PER_PASS = 32;
	private List<LightSource> lightSources = new ArrayList<LightSource>();
	private List<GroundFogVolume> groundFogVolumes = new ArrayList<GroundFogVolume>();

	//Uniforms
	private int depthUniformID = -1;
	private int repellerDiffuseUniformID = -1;
	private int repellerDepthUniformID = -1;
	private int gasParticlesDiffuseUniformID = -1;
	private int gasParticlesDepthUniformID = -1;
	private int invMVPUniformID = -1;
	private int fogModeUniformID = -1;
	private int[] lightSourcePositionUniformIDs = new int[MAX_LIGHT_SOURCES_PER_PASS];
	private int[] lightSourceColorUniformIDs = new int[MAX_LIGHT_SOURCES_PER_PASS];
	private int[] lightSourceRadiusUniformIDs = new int[MAX_LIGHT_SOURCES_PER_PASS];
	private int lightSourceAmountUniformID = -1;
	private int msTimeUniformID = -1;
	private int worldTimeUniformID = -1;
	private int renderPosUniformID = -1;
	private int viewPosUniformID = -1;

	//Shader textures
	private Framebuffer gasTextureBaseFramebuffer = null;
	private Framebuffer gasTextureFramebuffer = null;
	private Framebuffer starfieldTextureFramebuffer = null;

	//Effects
	private Warp gasWarpEffect = null;
	private Starfield starfieldEffect = null;
	private OcclusionExtractor occlusionExtractor = null;
	private GodRay godRayEffect = null;
	private Swirl swirlEffect = null;
	private GroundFog groundFogEffect = null;
	private float swirlAngle = 0.0F;
	private float lastSwirlAngle = 0.0F;

	private int currentRenderPass = 0;

	@Override
	protected ResourceLocation[] getShaders() {
		return new ResourceLocation[]{new ResourceLocation("thebetweenlands:shaders/postprocessing/world/world.vsh"), new ResourceLocation("thebetweenlands:shaders/postprocessing/world/world.fsh")};
	}

	@Override
	protected void deleteEffect() {
		if (this.depthBuffer != null)
			this.depthBuffer.deleteBuffer();

		if (this.blitBuffer != null)
			this.blitBuffer.delete();

		if (this.occlusionBuffer != null)
			this.occlusionBuffer.delete();

		if (this.repellerShieldBuffer != null)
			this.repellerShieldBuffer.deleteBuffers();

		if (this.gasParticlesBuffer != null)
			this.gasParticlesBuffer.deleteBuffers();

		if (this.gasTextureBaseFramebuffer != null)
			this.gasTextureBaseFramebuffer.deleteFramebuffer();

		if (this.gasTextureFramebuffer != null)
			this.gasTextureFramebuffer.deleteFramebuffer();

		if (this.starfieldTextureFramebuffer != null)
			this.starfieldTextureFramebuffer.deleteFramebuffer();

		if (this.gasWarpEffect != null)
			this.gasWarpEffect.delete();

		if (this.starfieldEffect != null)
			this.starfieldEffect.delete();

		if (this.occlusionExtractor != null)
			this.occlusionExtractor.delete();

		if (this.godRayEffect != null)
			this.godRayEffect.delete();

		if (this.groundFogEffect != null)
			this.groundFogEffect.delete();
	}

	@Override
	protected boolean initEffect() {
		//Get uniforms
		this.depthUniformID = this.getUniform("s_diffuse_depth");
		this.repellerDiffuseUniformID = this.getUniform("s_repellerShield");
		this.repellerDepthUniformID = this.getUniform("s_repellerShield_depth");
		this.gasParticlesDiffuseUniformID = this.getUniform("s_gasParticles");
		this.gasParticlesDepthUniformID = this.getUniform("s_gasParticles_depth");
		this.invMVPUniformID = this.getUniform("u_INVMVP");
		this.fogModeUniformID = this.getUniform("u_fogMode");
		this.msTimeUniformID = this.getUniform("u_msTime");
		this.worldTimeUniformID = this.getUniform("u_worldTime");
		this.viewPosUniformID = this.getUniform("u_viewPos");
		this.renderPosUniformID = this.getUniform("u_renderPos");

		for (int i = 0; i < MAX_LIGHT_SOURCES_PER_PASS; i++) {
			this.lightSourcePositionUniformIDs[i] = this.getUniform("u_lightSources[" + i + "].position");
		}

		for (int i = 0; i < MAX_LIGHT_SOURCES_PER_PASS; i++) {
			this.lightSourceColorUniformIDs[i] = this.getUniform("u_lightSources[" + i + "].color");
		}

		for (int i = 0; i < MAX_LIGHT_SOURCES_PER_PASS; i++) {
			this.lightSourceRadiusUniformIDs[i] = this.getUniform("u_lightSources[" + i + "].radius");
		}

		this.lightSourceAmountUniformID = this.getUniform("u_lightSourcesAmount");

		TextureManager textureManager = Minecraft.getMinecraft().getTextureManager();

		//Initialize framebuffers
		this.depthBuffer = new DepthBuffer(textureManager, WORLD_DEPTH_TEXTURE);
		this.blitBuffer = new ResizableFramebuffer(false);
		this.occlusionBuffer = new ResizableFramebuffer(false);
		this.repellerShieldBuffer = new GeometryBuffer(textureManager, REPELLER_DIFFUSE_TEXTURE, REPELLER_DEPTH_TEXTURE, true);
		this.gasParticlesBuffer = new GeometryBuffer(textureManager, GAS_PARTICLES_DIFFUSE_TEXTURE, GAS_PARTICLES_DEPTH_TEXTURE, true);

		//Initialize gas textures and effect
		this.gasTextureFramebuffer = new Framebuffer(64, 64, false);
		Minecraft.getMinecraft().getTextureManager().loadTexture(GAS_PARTICLE_TEXTURE, new GLTextureObjectWrapper(this.gasTextureFramebuffer.framebufferTexture));
		this.gasTextureBaseFramebuffer = new Framebuffer(64, 64, false);
		this.gasWarpEffect = new Warp().setTimeScale(0.00004F).setScale(40.0F).setMultiplier(3.55F).init();

		//Initialize starfield texture and effect
		this.starfieldTextureFramebuffer = new Framebuffer(BetweenlandsConfig.RENDERING.skyResolution, BetweenlandsConfig.RENDERING.skyResolution, false);
		this.starfieldEffect = new Starfield(true).init();

		//Initialize occlusion extractor and god's ray effect
		this.occlusionExtractor = (OcclusionExtractor) new OcclusionExtractor().init();
		this.godRayEffect = new GodRay().init();

		//Initialize swirl effect
		this.swirlEffect = new Swirl().init();

		//Initialize ground fog effect
		this.groundFogEffect = new GroundFog().init().setFogVolumes(this.groundFogVolumes);

		return true;
	}

	private static final Comparator<LightSource> LIGHT_SOURCE_SORTER = new Comparator<LightSource>() {
		@Override
		public int compare(LightSource o1, LightSource o2) {
			double dx1 = o1.x - Minecraft.getMinecraft().getRenderManager().viewerPosX;
			double dy1 = o1.y - Minecraft.getMinecraft().getRenderManager().viewerPosY;
			double dz1 = o1.z - Minecraft.getMinecraft().getRenderManager().viewerPosZ;
			double dx2 = o2.x - Minecraft.getMinecraft().getRenderManager().viewerPosX;
			double dy2 = o2.y - Minecraft.getMinecraft().getRenderManager().viewerPosY;
			double dz2 = o2.z - Minecraft.getMinecraft().getRenderManager().viewerPosZ;
			double d1 = Math.sqrt(dx1 * dx1 + dy1 * dy1 + dz1 * dz1);
			double d2 = Math.sqrt(dx2 * dx2 + dy2 * dy2 + dz2 * dz2);
			if (d1 > d2) {
				return 1;
			} else if (d1 < d2) {
				return -1;
			}
			return 0;
		}
	};

	@Override
	protected void uploadUniforms(float partialTicks) {
		this.uploadSampler(this.depthUniformID, this.depthBuffer.getGlTextureId(), 1);
		this.uploadSampler(this.repellerDiffuseUniformID, this.repellerShieldBuffer.getDiffuseTexture(), 2);
		this.uploadSampler(this.repellerDepthUniformID, this.repellerShieldBuffer.getDepthTexture(), 3);
		this.uploadSampler(this.gasParticlesDiffuseUniformID, this.gasParticlesBuffer.getDiffuseTexture(), 4);
		this.uploadSampler(this.gasParticlesDepthUniformID, this.gasParticlesBuffer.getDepthTexture(), 5);

		this.uploadMatrix4f(this.invMVPUniformID, this.invertedModelviewProjectionMatrix);
		this.uploadInt(this.fogModeUniformID, FogHandler.getCurrentFogMode());

		//Sort lights by distance
		Collections.sort(this.lightSources, LIGHT_SOURCE_SORTER);

		final int renderedLightSources = Math.min(MAX_LIGHT_SOURCES_PER_PASS, this.lightSources.size() - this.currentRenderPass * MAX_LIGHT_SOURCES_PER_PASS);

		final double renderPosX = Minecraft.getMinecraft().getRenderManager().viewerPosX;
		final double renderPosY = Minecraft.getMinecraft().getRenderManager().viewerPosY;
		final double renderPosZ = Minecraft.getMinecraft().getRenderManager().viewerPosZ;

		for (int i = 0; i < renderedLightSources; i++) {
			LightSource lightSource = this.lightSources.get(this.currentRenderPass * MAX_LIGHT_SOURCES_PER_PASS + i);
			this.uploadFloat(this.lightSourcePositionUniformIDs[i], (float) (lightSource.x - renderPosX), (float) (lightSource.y - renderPosY), (float) (lightSource.z - renderPosZ));
			this.uploadFloat(this.lightSourceColorUniformIDs[i], lightSource.r, lightSource.g, lightSource.b);
			this.uploadFloat(this.lightSourceRadiusUniformIDs[i], lightSource.radius);
		}

		this.uploadInt(this.lightSourceAmountUniformID, renderedLightSources);
		this.uploadFloat(this.msTimeUniformID, System.nanoTime() / 1000000.0F);
		this.uploadFloat(this.worldTimeUniformID, RenderUtils.getRenderTickCounter() + partialTicks);

		Entity renderView = Minecraft.getMinecraft().getRenderViewEntity();
		Vec3d camPos = renderView != null ? ActiveRenderInfo.projectViewFromEntity(Minecraft.getMinecraft().getRenderViewEntity(), partialTicks) : Vec3d.ZERO;
		this.uploadFloat(this.viewPosUniformID, (float)(camPos.x - renderPosX), (float)(camPos.y - renderPosY), (float)(camPos.z - renderPosZ));

		this.uploadFloat(this.renderPosUniformID, (float)renderPosX, (float)renderPosY, (float)renderPosZ);
	}

	/**
	 * Updates the depth buffer
	 */
	public void updateDepthBuffer() {
		this.depthBuffer.blitDepthBuffer(this.getMainFramebuffer());
		this.getMainFramebuffer().bindFramebuffer(false);
		GlStateManager.bindTexture(0);
	}

	/**
	 * Returns the main FBO
	 *
	 * @return
	 */
	protected final Framebuffer getMainFramebuffer() {
		return Minecraft.getMinecraft().getFramebuffer();
	}

	/**
	 * Returns the depth buffer
	 *
	 * @return
	 */
	public DepthBuffer getDepthBuffer() {
		return this.depthBuffer;
	}

	/**
	 * Updates following matrices: MV (Modelview), PM (Projection), MVP (Modelview x Projection), INVMVP (Inverted MVP)
	 */
	public void updateMatrices() {
		GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX, MODELVIEW);
		GL11.glGetFloat(GL11.GL_PROJECTION_MATRIX, PROJECTION);
		Matrix4f modelviewMatrix = (Matrix4f) new Matrix4f().load(MODELVIEW.asReadOnlyBuffer());
		this.modelviewMatrix = modelviewMatrix;
		Matrix4f projectionMatrix = (Matrix4f) new Matrix4f().load(PROJECTION.asReadOnlyBuffer());
		this.projectionMatrix = projectionMatrix;
		Matrix4f MVP = new Matrix4f();
		Matrix4f.mul(projectionMatrix, modelviewMatrix, MVP);
		this.modelviewProjectionMatrix = MVP;
		this.invertedModelviewProjectionMatrix = Matrix4f.invert(MVP, new Matrix4f());
	}

	/**
	 * Returns the inverted MVP matrix
	 *
	 * @return
	 */
	public Matrix4f getInvertedModelviewProjectionMatrix() {
		return this.invertedModelviewProjectionMatrix;
	}

	/**
	 * Returns the MVP matrix
	 *
	 * @return
	 */
	public Matrix4f getModelviewProjectionMatrix() {
		return this.modelviewProjectionMatrix;
	}

	/**
	 * Returns the MV matrix
	 *
	 * @return
	 */
	public Matrix4f getModelviewMatrix() {
		return this.modelviewMatrix;
	}

	/**
	 * Returns the modelview buffer
	 *
	 * @return
	 */
	public FloatBuffer getModelviewBuffer() {
		return MODELVIEW;
	}

	/**
	 * Returns the projection matrix
	 *
	 * @return
	 */
	public Matrix4f getProjectionMatrix() {
		return this.projectionMatrix;
	}

	/**
	 * Returns the projection buffer
	 *
	 * @return
	 */
	public FloatBuffer getProjectionBuffer() {
		return PROJECTION;
	}

	/**
	 * Returns the gas particle geometry buffer
	 * @return
	 */
	public GeometryBuffer getGasParticleBuffer() {
		return this.gasParticlesBuffer;
	}

	/**
	 * Returns the repeller shield geometry buffer
	 * @return
	 */
	public GeometryBuffer getRepellerShieldBuffer() {
		return this.repellerShieldBuffer;
	}

	/**
	 * Adds a dynamic light source for this frame
	 *
	 * @param light
	 */
	public void addLight(LightSource light) {
		this.lightSources.add(light);
	}

	/**
	 * Clears all dynamic light sources
	 */
	public void clearLights() {
		this.lightSources.clear();
	}

	/**
	 * Returns the amount of light sources
	 *
	 * @return
	 */
	public int getLightSourcesAmount() {
		return this.lightSources.size();
	}

	/**
	 * Adds a ground fog volume for this frame
	 * 
	 * @param volume
	 */
	public void addGroundFogVolume(GroundFogVolume volume) {
		this.groundFogVolumes.add(volume);
	}

	/**
	 * Clears all ground fog volumes
	 */
	public void clearGroundFogVolumes() {
		this.groundFogVolumes.clear();
	}

	/**
	 * Returns the amount of ground fog volumes
	 *
	 * @return
	 */
	public int getGroundFogVolumesAmount() {
		return this.groundFogVolumes.size();
	}

	/**
	 * Sets the current render pass
	 *
	 * @param pass
	 */
	public void setRenderPass(int pass) {
		this.currentRenderPass = pass;
	}

	/**
	 * Returns the gas texture
	 *
	 * @return
	 */
	public int getGasTexture() {
		return this.gasTextureFramebuffer != null ? this.gasTextureFramebuffer.framebufferTexture : -1;
	}

	/**
	 * Returns the starfield texture
	 *
	 * @return
	 */
	public int getStarfieldTexture() {
		return this.starfieldTextureFramebuffer != null ? this.starfieldTextureFramebuffer.framebufferTexture : -1;
	}

	/**
	 * Updates the shader textures
	 *
	 * @param partialTicks
	 */
	public void updateTextures(float partialTicks) {
		World world = Minecraft.getMinecraft().world;
		if (world != null && !Minecraft.getMinecraft().isGamePaused()) {
			//Update gas particles
			this.updateGasParticlesTexture(world, partialTicks);

			//Update starfield texture
			this.updateStarfieldTexture(partialTicks);
		}
	}

	/**
	 * Renders additional post processing effects such as god's rays, swirl etc.
	 *
	 * @param partialTicks
	 */
	public void renderPostEffects(float partialTicks) {
		ScaledResolution scaledResolution = new ScaledResolution(Minecraft.getMinecraft());
		GlStateManager.matrixMode(GL11.GL_PROJECTION);
		GlStateManager.loadIdentity();
		GlStateManager.ortho(0.0D, scaledResolution.getScaledWidth(), scaledResolution.getScaledHeight(), 0.0D, 1000.0D, 3000.0D);
		GlStateManager.matrixMode(GL11.GL_MODELVIEW);
		GlStateManager.loadIdentity();
		GlStateManager.translate(0.0F, 0.0F, -2000.0F);

		this.applyGroundFog(partialTicks);
		this.applyBloodSky(partialTicks);
		this.applySwirl(partialTicks);
	}

	private void applyGroundFog(float partialTicks) {
		if(!this.groundFogVolumes.isEmpty()) {
			Framebuffer mainFramebuffer = Minecraft.getMinecraft().getFramebuffer();
			Framebuffer blitFramebuffer = this.blitBuffer.getFramebuffer(mainFramebuffer.framebufferWidth, mainFramebuffer.framebufferHeight);

			this.groundFogEffect.setDepthBufferTexture(this.getDepthBuffer().getGlTextureId());
			this.groundFogEffect.create(mainFramebuffer)
			.setSource(mainFramebuffer.framebufferTexture)
			.setBlitFramebuffer(blitFramebuffer)
			.setPreviousFramebuffer(mainFramebuffer)
			.setMirrorY(true)
			.render(partialTicks);
		}
	}

	private void applyBloodSky(float partialTicks) {
		float skyTransparency = 0.0F;

		boolean hasBeat = false;

		World world = Minecraft.getMinecraft().world;
		if (world != null) {
			BLEnvironmentEventRegistry eeRegistry = BetweenlandsWorldStorage.forWorld(world).getEnvironmentEventRegistry();
			skyTransparency += eeRegistry.bloodSky.getSkyTransparency(partialTicks);
			if (skyTransparency > 0.01F) {
				hasBeat = true;
			}
			skyTransparency += eeRegistry.spoopy.getSkyTransparency(partialTicks);
		}

		if (skyTransparency <= 0.01F) {
			return;
		} else if (skyTransparency > 1.0F) {
			skyTransparency = 1.0F;
		}

		ScaledResolution scaledResolution = new ScaledResolution(Minecraft.getMinecraft());
		double renderWidth = scaledResolution.getScaledWidth();
		double renderHeight = scaledResolution.getScaledHeight();

		Vec3d lightPos = new Vec3d(45, 40, 30);

		//Get screen space coordinates of light source
		Projection projection = GLUProjection.getInstance().project(lightPos.x, lightPos.y, lightPos.z, ClampMode.ORTHOGONAL, false);
		Projection projectionUnclamped = GLUProjection.getInstance().project(lightPos.x, lightPos.y, lightPos.z, ClampMode.NONE, false);

		//Get light source positions in texture coords
		float rayX = (float) (projection.getX() / renderWidth);
		float rayY = (float) (projection.getY() / renderHeight);

		float rayYUnclamped = (float) (projectionUnclamped.getY() / renderWidth);

		//Calculate angle differences
		Vec3d lookVec = Minecraft.getMinecraft().player.getLook(partialTicks);
		lookVec = new Vec3d(lookVec.x + 0.0001D, 0, lookVec.z + 0.0001D);
		lookVec = lookVec.normalize();
		Vec3d sLightPos = new Vec3d(lightPos.x + 0.0001D, 0, lightPos.z + 0.0001D).normalize();
		float lightXZAngle = (float) Math.toDegrees(Math.acos(sLightPos.dotProduct(lookVec)));
		float fovX = GLUProjection.getInstance().getFovX() / 2.0F;
		float angDiff = Math.abs(lightXZAngle);

		float decay = 0.96F;
		float illuminationDecay = 0.44F;
		float weight = 0.12F;

		//Lower effect strength if outside of view
		if (rayYUnclamped <= 0.0F) {
			float mult = 1 + MathHelper.clamp(rayYUnclamped / 5.0F * (1.0F - angDiff / fovX), -1, 0.0F);
			decay *= mult;
			illuminationDecay *= mult;
			weight *= mult;
		}
		if (angDiff > fovX) {
			float mult = 1.0F - ((angDiff - fovX) / 400.0F);
			decay *= mult;
			illuminationDecay *= mult;
			weight *= mult;
		}

		int depthTexture = this.depthBuffer.getGlTextureId();
		int clipPlaneBuffer = BLSkyRenderer.clipPlaneBuffer.getDepthTexture();

		if (depthTexture < 0 || clipPlaneBuffer < 0) return; //FBOs not yet ready

		Framebuffer mainFramebuffer = Minecraft.getMinecraft().getFramebuffer();
		Framebuffer blitFramebuffer = this.blitBuffer.getFramebuffer(mainFramebuffer.framebufferWidth, mainFramebuffer.framebufferHeight);
		Framebuffer occlusionFramebuffer = this.occlusionBuffer.getFramebuffer(mainFramebuffer.framebufferWidth, mainFramebuffer.framebufferHeight);

		//Extract occluding objects
		this.occlusionExtractor.setDepthTextures(depthTexture, clipPlaneBuffer);
		this.occlusionExtractor.create(occlusionFramebuffer)
		.setSource(Minecraft.getMinecraft().getFramebuffer().framebufferTexture)
		.setPreviousFramebuffer(mainFramebuffer)
		//.setRenderDimensions(Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight)
		.render(partialTicks);

		//Render god's ray to blitFramebuffer
		float beat = 0.0F;
		if (hasBeat) {
			beat = Math.abs(((float) Math.sin(System.nanoTime() / 100000000.0D) / 3.0F) / (Math.abs((float) Math.sin(System.nanoTime() / 4000000000.0D) * (float) Math.sin(System.nanoTime() / 4000000000.0D) * (float) Math.sin(System.nanoTime() / 4000000000.0D + 0.05F) * 120.0F) * 180.0F + 15.5F) * 30.0F) / 4.0F;
		}
		float density = 0.1F + beat;
		this.godRayEffect.setOcclusionMap(occlusionFramebuffer)
		.setParams(0.8F, decay * 1.01F, density * 1.5F, weight * 0.8F, illuminationDecay * 1.25F)
		.setRayPos(rayX, rayY)
		.create(blitFramebuffer)
		.setSource(mainFramebuffer.framebufferTexture)
		.setPreviousFramebuffer(mainFramebuffer)
		.render(partialTicks);

		//Render blitFramebuffer to main framebuffer
		GlStateManager.alphaFunc(GL11.GL_GREATER, 0.0F);
		GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
		GlStateManager.enableBlend();
		GlStateManager.enableTexture2D();
		GlStateManager.color(0.7F, 0.1F, 0.0F, skyTransparency / 2.5F);
		GlStateManager.bindTexture(blitFramebuffer.framebufferTexture);
		GlStateManager.glBegin(GL11.GL_TRIANGLES);
		GlStateManager.glTexCoord2f(0.0F, 1.0F);
		GlStateManager.glVertex3f(0, 0, 0);
		GlStateManager.glTexCoord2f(0.0F, 0.0F);
		GlStateManager.glVertex3f(0, (float)renderHeight, 0);
		GlStateManager.glTexCoord2f(1.0F, 0.0F);
		GlStateManager.glVertex3f((float)renderWidth, (float)renderHeight, 0);
		GlStateManager.glTexCoord2f(1.0F, 0.0F);
		GlStateManager.glVertex3f((float)renderWidth, (float)renderHeight, 0);
		GlStateManager.glTexCoord2f(1.0F, 1.0F);
		GlStateManager.glVertex3f((float)renderWidth, 0, 0);
		GlStateManager.glTexCoord2f(0.0F, 1.0F);
		GlStateManager.glVertex3f(0, 0, 0);
		GlStateManager.glEnd();
		GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
	}

	/**
	 * Sets the swirl effect angle
	 *
	 * @param swirlAngle
	 */
	public void setSwirlAngle(float swirlAngle) {
		this.lastSwirlAngle = this.swirlAngle;
		this.swirlAngle = swirlAngle;
	}

	/**
	 * Returns the swirl effect angle
	 *
	 * @param partialTicks
	 * @return
	 */
	public float getSwirlAngle(float partialTicks) {
		return this.lastSwirlAngle + (this.swirlAngle - this.lastSwirlAngle) * partialTicks;
	}

	private void applySwirl(float partialTicks) {
		float interpolatedSwirlAngle = this.getSwirlAngle(partialTicks);

		if (interpolatedSwirlAngle != 0.0F) {
			Framebuffer mainFramebuffer = Minecraft.getMinecraft().getFramebuffer();
			Framebuffer blitFramebuffer = this.blitBuffer.getFramebuffer(mainFramebuffer.framebufferWidth, mainFramebuffer.framebufferHeight);

			//Render swirl
			this.swirlEffect.setAngle(interpolatedSwirlAngle);
			this.swirlEffect.create(mainFramebuffer)
			.setSource(mainFramebuffer.framebufferTexture)
			.setBlitFramebuffer(blitFramebuffer)
			.setPreviousFramebuffer(mainFramebuffer)
			.render(partialTicks);
		}
	}


	private void updateGasParticlesTexture(World world, float partialTicks) {
		boolean hasCloud = DefaultParticleBatches.GAS_CLOUDS_TEXTURED.getParticles().size() > 0 || DefaultParticleBatches.GAS_CLOUDS_HEAT_HAZE.getParticles().size() > 0;
		if(!hasCloud) {
			for (Entity entity : Minecraft.getMinecraft().world.loadedEntityList) {
				if (entity instanceof EntityGasCloud) {
					hasCloud = true;
					break;
				}
			}
		}
		if (hasCloud) {
			//Update gas texture
			float worldTimeInterp = RenderUtils.getRenderTickCounter() + partialTicks;
			float offsetX = ((float) Math.sin((worldTimeInterp / 20.0F) % (Math.PI * 2.0D)) + 1.0F) / 600.0F;
			float offsetY = ((float) Math.cos((worldTimeInterp / 20.0F) % (Math.PI * 2.0D)) + 1.0F) / 600.0F;
			this.gasWarpEffect.setOffset(offsetX, offsetY)
			.setWarpDir(0.75F, 0.75F).setScale(1.8F);

			this.gasTextureFramebuffer.bindFramebuffer(false);
			GlStateManager.clearColor(1, 1, 1, 1);
			GlStateManager.clear(GL11.GL_COLOR_BUFFER_BIT);

			this.gasTextureBaseFramebuffer.bindFramebuffer(false);
			GlStateManager.clearColor(1, 1, 1, 1);
			GlStateManager.clear(GL11.GL_COLOR_BUFFER_BIT);

			this.gasWarpEffect.create(this.gasTextureFramebuffer)
			.setSource(this.gasTextureBaseFramebuffer.framebufferTexture)
			.setPreviousFramebuffer(Minecraft.getMinecraft().getFramebuffer())
			.render(partialTicks);
		}
	}

	private void updateStarfieldTexture(float partialTicks) {
		float offX = (float) (Minecraft.getMinecraft().getRenderManager().viewerPosX / 8000.0D);
		float offY = (float) (Minecraft.getMinecraft().getRenderManager().viewerPosZ / 8000.0D);
		GlStateManager.alphaFunc(GL11.GL_GREATER, 0.0F);
		this.starfieldEffect.setTimeScale(0.00000025F).setZoom(0.8F).setOffset(offX, offY, 0);
		this.starfieldEffect.create(this.starfieldTextureFramebuffer)
		.setPreviousFramebuffer(Minecraft.getMinecraft().getFramebuffer())
		.setRenderDimensions(BetweenlandsConfig.RENDERING.skyResolution, BetweenlandsConfig.RENDERING.skyResolution)
		.render(partialTicks);
		GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
	}
}
