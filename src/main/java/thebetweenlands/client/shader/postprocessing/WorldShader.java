package thebetweenlands.client.shader.postprocessing;

import com.google.gson.JsonSyntaxException;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.shaders.Uniform;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceProvider;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import thebetweenlands.client.renderer.GLTextureObjectWrapper;
import thebetweenlands.client.shader.LightSource;
import thebetweenlands.client.shader.postprocessing.GroundFog.GroundFogVolume;
import thebetweenlands.common.TheBetweenlands;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * TODO: Finish and add Starfield, OcclusionExtractor, Godrays, and Swirl shaders.
 */
public class WorldShader extends PostChain implements AutoCloseable {
	public static final ResourceLocation WORLD_DEPTH_TEXTURE = TheBetweenlands.prefix("world_depth");
	public static final ResourceLocation REPELLER_DIFFUSE_TEXTURE = TheBetweenlands.prefix("repeller_diffuse");
	public static final ResourceLocation REPELLER_DEPTH_TEXTURE = TheBetweenlands.prefix("repeller_depth");
	public static final ResourceLocation GAS_PARTICLES_DIFFUSE_TEXTURE = TheBetweenlands.prefix("gas_particles_diffuse");
	public static final ResourceLocation GAS_PARTICLES_DEPTH_TEXTURE = TheBetweenlands.prefix("gas_particles_depth");
	public static final ResourceLocation CLIP_PLANE_DIFFUSE_TEXTURE = TheBetweenlands.prefix("clip_plane_diffuse");
	public static final ResourceLocation CLIP_PLANE_DEPTH_TEXTURE = TheBetweenlands.prefix("clip_plane_depth");

	public static final ResourceLocation GAS_PARTICLE_TEXTURE = TheBetweenlands.prefix("gas_particle");

	private RenderTarget depthBuffer;
	private RenderTarget blitBuffer;
	private RenderTarget occlusionBuffer;
	private RenderTarget repellerShieldBuffer;
	private RenderTarget gasParticlesBuffer;

	private Matrix4f invertedModelviewProjectionMatrix;
	private Matrix4f modelviewProjectionMatrix;
	private Matrix4f modelviewMatrix;
	private Matrix4f projectionMatrix;

	public static final int MAX_LIGHT_SOURCES_PER_PASS = 32;
	private final List<LightSource> lightSources = new ArrayList<>();
	private final List<GroundFogVolume> groundFogVolumes = new ArrayList<>();
	public Vector3f cameraPos = new Vector3f();

	private static final int WORLD_INDEX = 0;	// The world shader pass index
	private static final int BLIT_INDEX = 1;

	//Uniforms
	@Nullable
	public Uniform MVPUniform;
	@Nullable
	public Uniform invMVPUniform;
	@Nullable
	public Uniform projectionUniform;
	@Nullable
	public  Uniform viewPosUniform;
	@Nullable
	public  Uniform renderPosUniform;
	@Nullable
	private  Uniform lightSourcePositionUniforms;
	@Nullable
	private  Uniform lightSourceColorUniforms;
	@Nullable
	private  Uniform lightSourceRadiusUniforms;
	@Nullable
	private  Uniform lightSourceAmountUniform;
	@Nullable
	private  Uniform screenSize;
	@Nullable
	public Uniform worldTimeUniform;

	//Effects
	@Nullable
	private Warp gasWarpEffect = null;
	@Nullable
	private Starfield starfieldEffect = null;
	@Nullable
	private OcclusionExtractor occlusionExtractor = null;
	@Nullable
	private GodRay godRayEffect = null;
	@Nullable
	private Swirl swirlEffect = null;
	@Nullable
	private GroundFog groundFogEffect = null;
	private float swirlAngle = 0.0F;
	private float lastSwirlAngle = 0.0F;

	private int currentRenderPass = 0;


	public WorldShader(TextureManager textureManager, ResourceProvider resourceProvider, RenderTarget screenTarget) throws IOException, JsonSyntaxException {
		super(textureManager, resourceProvider, screenTarget, ResourceLocation.fromNamespaceAndPath(TheBetweenlands.ID, "shaders/post/world.json"));

		/* 	PostChain structure:
		 * 		0 - 	World shader 		= WORLD_INDEX
		 * 		1 -	 	Blit to screen		= BLIT_INDEX
		 */

		// Get Uniforms
		this.invMVPUniform = this.getUniform(WORLD_INDEX, "u_INVMVP");
		this.viewPosUniform = this.getUniform(WORLD_INDEX, "u_viewPos");
		this.renderPosUniform = this.getUniform(WORLD_INDEX, "u_renderPos");
		Uniform fogModeUniform = this.getUniform(WORLD_INDEX, "u_fogMode");	// temp
		this.screenSize = this.getUniform(WORLD_INDEX, "u_screenSize");
		this.MVPUniform = this.getUniform(WORLD_INDEX, "u_MVP");
		this.worldTimeUniform = this.getUniform(WORLD_INDEX, "u_worldTime");

		this.lightSourcePositionUniforms = this.getUniform(WORLD_INDEX, "u_lightSources_position");
		this.lightSourceColorUniforms = this.getUniform(WORLD_INDEX, "u_lightSources_color");
		this.lightSourceRadiusUniforms = this.getUniform(WORLD_INDEX, "u_lightSources_radius");
		this.lightSourceAmountUniform = this.getUniform(WORLD_INDEX, "u_lightSourcesAmount");

		// Set samplers
		this.depthBuffer = this.getTempTarget("s_diffuse_depth");
		this.repellerShieldBuffer = this.getTempTarget("s_repellerShield");
		this.gasParticlesBuffer = this.getTempTarget("s_gasParticles");

		// Setup additional effects
		// TODO: consider separating from WorldShader into separate PostChain instances.
		this.gasWarpEffect = new Warp(textureManager, resourceProvider, screenTarget);
		this.starfieldEffect = new Starfield(textureManager, resourceProvider, screenTarget, true, 1024, 1024);	// TODO: config file manager to set height & width
		//this.occlusionExtractor;
		//this.godRayEffect;
		//this.swirlEffect;
		//this.groundFogEffect;

		Minecraft.getInstance().getTextureManager().register(GAS_PARTICLE_TEXTURE, new GLTextureObjectWrapper(this.gasWarpEffect.gasTextureTarget.getColorTextureId()));
	}

	public void cleanUp() {
		clearLights();
	}

	/**
	 * TODO: cleanup
	 */
	protected void deleteEffect() {
		if (this.depthBuffer != null)
			this.depthBuffer.destroyBuffers();

		if (this.blitBuffer != null)
			this.blitBuffer.destroyBuffers();

		if (this.occlusionBuffer != null)
			this.occlusionBuffer.destroyBuffers();

		if (this.repellerShieldBuffer != null)
			this.repellerShieldBuffer.destroyBuffers();

		if (this.gasParticlesBuffer != null)
			this.gasParticlesBuffer.destroyBuffers();

		if (this.gasWarpEffect != null)
			this.gasWarpEffect.close();

		if (this.starfieldEffect != null)
			this.starfieldEffect.close();

		if (this.occlusionExtractor != null)
			this.occlusionExtractor.delete();

		if (this.godRayEffect != null)
			this.godRayEffect.delete();

		if (this.groundFogEffect != null)
			this.groundFogEffect.delete();
	}


	/**
	 * TODO: move into constructor once additional effects are ported. <br>
	 * DEV NOTE: Keep for reference.
	 */
	protected boolean initEffect() {
		TextureManager textureManager = Minecraft.getInstance().getTextureManager();

		//Initialize gas textures and effect
		//this.gasTextureFramebuffer = new TextureTarget(64, 64, false, Minecraft.ON_OSX);
		//Minecraft.getInstance().getTextureManager().register(GAS_PARTICLE_TEXTURE, new GLTextureObjectWrapper(this.gasTextureFramebuffer.getColorTextureId()));
		//this.gasTextureBaseFramebuffer = new TextureTarget(64, 64, false, Minecraft.ON_OSX);
		//this.gasWarpEffect = new Warp().setTimeScale(0.00004F).setScale(40.0F).setMultiplier(3.55F).init();

		//Initialize starfield texture and effect
		//this.starfieldTextureFramebuffer = new TextureTarget(BetweenlandsConfig.skyResolution, BetweenlandsConfig.skyResolution, false, Minecraft.ON_OSX);
		//this.starfieldEffect = new Starfield(true).init();

		//Initialize occlusion extractor and god's ray effect
		this.occlusionExtractor = new OcclusionExtractor().init();
		this.godRayEffect = new GodRay().init();

		//Initialize swirl effect
		this.swirlEffect = new Swirl().init();

		//Initialize ground fog effect
		this.groundFogEffect = new GroundFog().init().setFogVolumes(this.groundFogVolumes);

		return true;
	}

	private static final Comparator<LightSource> LIGHT_SOURCE_SORTER = (o1, o2) -> {
		double dx1 = o1.x - Minecraft.getInstance().gameRenderer.getMainCamera().getPosition().x();
		double dy1 = o1.y - Minecraft.getInstance().gameRenderer.getMainCamera().getPosition().y();
		double dz1 = o1.z - Minecraft.getInstance().gameRenderer.getMainCamera().getPosition().z();
		double dx2 = o2.x - Minecraft.getInstance().gameRenderer.getMainCamera().getPosition().x();
		double dy2 = o2.y - Minecraft.getInstance().gameRenderer.getMainCamera().getPosition().y();
		double dz2 = o2.z - Minecraft.getInstance().gameRenderer.getMainCamera().getPosition().z();
		double d1 = Math.sqrt(dx1 * dx1 + dy1 * dy1 + dz1 * dz1);
		double d2 = Math.sqrt(dx2 * dx2 + dy2 * dy2 + dz2 * dz2);
		if (d1 > d2) {
			return 1;
		} else if (d1 < d2) {
			return -1;
		}
		return 0;
	};

	public void uploadUniforms(float partialTicks) {
		this.invMVPUniform.set(invertedModelviewProjectionMatrix);
		//this.MVPUniform.set(modelviewProjectionMatrix);
		this.viewPosUniform.set(this.cameraPos);
		//this.screenSize.set((float)Minecraft.getInstance().getWindow().getWidth(), (float)Minecraft.getInstance().getWindow().getHeight());

		float[] positionBuff = new float[96];
		float[] colorBuff = new float[96];

		//Sort lights by distance
		Collections.sort(this.lightSources, LIGHT_SOURCE_SORTER);

		if (!lightSources.isEmpty()) {
			for (int i = 0; i < MAX_LIGHT_SOURCES_PER_PASS && i < lightSources.size(); i++) {
				positionBuff[(i*3)] = ((float) this.lightSources.get(i).x - this.cameraPos.x);
				positionBuff[(i*3)+1] = ((float) this.lightSources.get(i).y - this.cameraPos.y);
				positionBuff[(i*3)+2] = ((float) this.lightSources.get(i).z - this.cameraPos.z);
				colorBuff[(i*3)] = (this.lightSources.get(i).r);
				colorBuff[(i*3)+1] = (this.lightSources.get(i).g);
				colorBuff[(i*3)+2] = (this.lightSources.get(i).b);

				lightSourceRadiusUniforms.set(i, this.lightSources.get(i).radius);
			}

			lightSourcePositionUniforms.set(positionBuff);
			lightSourceColorUniforms.set(colorBuff);
		}

		lightSourceAmountUniform.set(Math.min(this.lightSources.size(), 32));
	}

	/**
	 * Returns the main FBO
	 *
	 * @return
	 */
	protected final RenderTarget getMainFramebuffer() {
		return Minecraft.getInstance().getMainRenderTarget();
	}

	/**
	 * Returns the depth buffer
	 *
	 * @return
	 */
	public RenderTarget getDepthBuffer() {
		return this.depthBuffer;
	}

	/**
	 * Updates following matrices: MV (Modelview), PM (Projection), MVP (Modelview x Projection), INVMVP (Inverted MVP)
	 */
	public void updateMatrices(final RenderLevelStageEvent event) {
		this.cameraPos = event.getCamera().getPosition().toVector3f();
		this.modelviewMatrix = event.getModelViewMatrix().transpose(new Matrix4f());
		this.projectionMatrix = event.getProjectionMatrix().transpose(new Matrix4f());
		this.invertedModelviewProjectionMatrix = new Matrix4f();

		Matrix4f MVP = this.modelviewMatrix.mul(this.projectionMatrix);
		MVP.invert(this.invertedModelviewProjectionMatrix);

		this.invertedModelviewProjectionMatrix = invertedModelviewProjectionMatrix.assume(0);
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
	 * Returns the projection matrix
	 *
	 * @return
	 */
	public Matrix4f getProjectionMatrix() {
		return this.projectionMatrix;
	}

	/**
	 * Returns the gas particle geometry buffer
	 * @return
	 */
	public RenderTarget getGasParticleBuffer() {
		return this.gasParticlesBuffer;
	}

	/**
	 * Returns the repeller shield geometry buffer
	 * @return
	 */
	public RenderTarget getRepellerShieldBuffer() {
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
		return this.gasWarpEffect.gasTextureTarget != null ? this.gasWarpEffect.gasTextureTarget.getColorTextureId() : -1;
	}

	/**
	 * Returns the starfield texture
	 *
	 * @return
	 */
	public int getStarfieldTexture() {
		return this.starfieldEffect.starfieldTexture != null ? this.starfieldEffect.starfieldTexture.getColorTextureId() : -1;
	}

	/**
	 * Updates the shader textures
	 *
	 * @param partialTicks
	 */
	public void updateTextures(float partialTicks) {
		ClientLevel level = Minecraft.getInstance().level;
		if (level != null && !Minecraft.getInstance().isPaused()) {
			//Update gas particles
			this.updateGasParticlesTexture(level, partialTicks);

			//Update starfield texture
			this.updateStarfieldTexture(partialTicks);

			// Reset main render target
			Minecraft.getInstance().getMainRenderTarget().bindWrite(true);
		}
	}

	/**
	 * Renders additional post processing effects such as god's rays, swirl etc.
	 *
	 * @param partialTicks
	 */
	public void renderPostEffects(float partialTicks) {
		/*
		Window window = Minecraft.getInstance().getWindow();
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		GL11.glOrtho(0.0D, window.getWidth(), window.getHeight(), 0.0D, 1000.0D, 3000.0D);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glLoadIdentity();
		GL11.glTranslatef(0.0F, 0.0F, -2000.0F);

		//this.applyGroundFog(partialTicks);
		//this.applyBloodSky(partialTicks);
		this.applySwirl(partialTicks);
		*/
	}


	private void applyGroundFog(float partialTicks) {
		/*
		if(!this.groundFogVolumes.isEmpty()) {
			RenderTarget mainFramebuffer = Minecraft.getInstance().getMainRenderTarget();
			RenderTarget blitFramebuffer = this.blitBuffer.getFramebuffer(mainFramebuffer.viewWidth, mainFramebuffer.viewHeight);

			this.groundFogEffect.setDepthBufferTexture(this.getDepthBuffer().getId());
			this.groundFogEffect.create(mainFramebuffer)
			.setSource(mainFramebuffer.getColorTextureId())
			.setBlitFramebuffer(blitFramebuffer)
			.setPreviousFramebuffer(mainFramebuffer)
			.setMirrorY(true)
			.render(partialTicks);
		}
		*/
	}

	private void applyBloodSky(float partialTicks) {
		/*
		float skyTransparency = 0.0F;

		boolean hasBeat = false;

		Level world = Minecraft.getInstance().level;
		if (world != null && world.getExistingData(AttachmentRegistry.WORLD_STORAGE).isPresent()) {
			skyTransparency += EnvironmentEventRegistry.BLOOD_SKY.get().getSkyTransparency(partialTicks);
			if (skyTransparency > 0.01F) {
				hasBeat = true;
			}
			skyTransparency += EnvironmentEventRegistry.SPOOPY.get().getSkyTransparency(partialTicks);
		}

		if (skyTransparency <= 0.01F) {
			return;
		} else if (skyTransparency > 1.0F) {
			skyTransparency = 1.0F;
		}

		Window window = Minecraft.getInstance().getWindow();
		double renderWidth = window.getWidth();
		double renderHeight = window.getHeight();

		Vec3 lightPos = new Vec3(45, 40, 30);

		//Get screen space coordinates of light source
		Projection projection = GLUProjection.getInstance().project(lightPos.x, lightPos.y, lightPos.z, ClampMode.ORTHOGONAL, false);
		Projection projectionUnclamped = GLUProjection.getInstance().project(lightPos.x, lightPos.y, lightPos.z, ClampMode.NONE, false);

		//Get light source positions in texture coords
		float rayX = (float) (projection.getX() / renderWidth);
		float rayY = (float) (projection.getY() / renderHeight);

		float rayYUnclamped = (float) (projectionUnclamped.getY() / renderWidth);

		//Calculate angle differences
		Vec3 lookVec = Minecraft.getInstance().player.getViewVector(partialTicks);
		lookVec = new Vec3(lookVec.x + 0.0001D, 0, lookVec.z + 0.0001D);
		lookVec = lookVec.normalize();
		Vec3 sLightPos = new Vec3(lightPos.x + 0.0001D, 0, lightPos.z + 0.0001D).normalize();
		float lightXZAngle = (float) Math.toDegrees(Math.acos(sLightPos.dot(lookVec)));
		float fovX = GLUProjection.getInstance().getFovX() / 2.0F;
		float angDiff = Math.abs(lightXZAngle);

		float decay = 0.96F;
		float illuminationDecay = 0.44F;
		float weight = 0.12F;

		//Lower effect strength if outside of view
		if (rayYUnclamped <= 0.0F) {
			float mult = 1 + Mth.clamp(rayYUnclamped / 5.0F * (1.0F - angDiff / fovX), -1, 0.0F);
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

		int depthTexture = this.depthBuffer.getId();
		int clipPlaneBuffer = BLSkyRenderer.clipPlaneBuffer.getDepthTexture();

		if (depthTexture < 0 || clipPlaneBuffer < 0) return; //FBOs not yet ready

		RenderTarget mainFramebuffer = Minecraft.getInstance().getMainRenderTarget();
		RenderTarget blitFramebuffer = this.blitBuffer.getFramebuffer(mainFramebuffer.viewWidth, mainFramebuffer.viewHeight);
		RenderTarget occlusionFramebuffer = this.occlusionBuffer.getFramebuffer(mainFramebuffer.viewWidth, mainFramebuffer.viewHeight);

		//Extract occluding objects
		this.occlusionExtractor.setDepthTextures(depthTexture, clipPlaneBuffer);
		this.occlusionExtractor.create(occlusionFramebuffer)
		.setSource(Minecraft.getInstance().getMainRenderTarget().getColorTextureId())
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
		.setSource(mainFramebuffer.getColorTextureId())
		.setPreviousFramebuffer(mainFramebuffer)
		.render(partialTicks);

		//Render blitFramebuffer to main framebuffer
		GL11.glAlphaFunc(GL11.GL_GREATER, 0.0F);
		RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
		RenderSystem.enableBlend();
		RenderSystem.setShaderColor(0.7F, 0.1F, 0.0F, skyTransparency / 2.5F);
		RenderSystem.bindTexture(blitFramebuffer.getColorTextureId());
		GL11.glBegin(GL11.GL_TRIANGLES);
		GL11.glTexCoord2f(0.0F, 1.0F);
		GL11.glVertex3f(0, 0, 0);
		GL11.glTexCoord2f(0.0F, 0.0F);
		GL11.glVertex3f(0, (float)renderHeight, 0);
		GL11.glTexCoord2f(1.0F, 0.0F);
		GL11.glVertex3f((float)renderWidth, (float)renderHeight, 0);
		GL11.glTexCoord2f(1.0F, 0.0F);
		GL11.glVertex3f((float)renderWidth, (float)renderHeight, 0);
		GL11.glTexCoord2f(1.0F, 1.0F);
		GL11.glVertex3f((float)renderWidth, 0, 0);
		GL11.glTexCoord2f(0.0F, 1.0F);
		GL11.glVertex3f(0, 0, 0);
		GL11.glEnd();
		GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
		*/
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
		return Mth.lerp(partialTicks, this.lastSwirlAngle, this.swirlAngle);
	}

	private void applySwirl(float partialTicks) {
		/*
		float interpolatedSwirlAngle = this.getSwirlAngle(partialTicks);

		if (interpolatedSwirlAngle != 0.0F) {
			RenderTarget mainFramebuffer = Minecraft.getInstance().getMainRenderTarget();
			RenderTarget blitFramebuffer = this.blitBuffer.getFramebuffer(mainFramebuffer.viewWidth, mainFramebuffer.viewHeight);

			//Render swirl
			this.swirlEffect.setAngle(interpolatedSwirlAngle);
			this.swirlEffect.create(mainFramebuffer)
			.setSource(mainFramebuffer.getColorTextureId())
			.setBlitFramebuffer(blitFramebuffer)
			.setPreviousFramebuffer(mainFramebuffer)
			.render(partialTicks);
		}
		*/
	}


	private void updateGasParticlesTexture(ClientLevel level, float partialTicks) {
		// No easy way to check for this yet sorry :(
		boolean hasCloud = true;//DefaultParticleBatches.GAS_CLOUDS_TEXTURED.getParticles().size() > 0 || DefaultParticleBatches.GAS_CLOUDS_HEAT_HAZE.getParticles().size() > 0;
//		if(!hasCloud) {
//			for (Entity entity : level.entitiesForRendering()) {
//				if (entity instanceof EntityGasCloud) {
//					hasCloud = true;
//					break;
//				}
//			}
//		}
		if (hasCloud) {
			//Update gas texture
			this.gasWarpEffect.uploadUniforms(partialTicks);
			this.gasWarpEffect.process(partialTicks);

			// Retarget Minecraft MainRenderTarget
			Minecraft.getInstance().getMainRenderTarget().bindWrite(false);
		}
	}

	private void updateStarfieldTexture(float partialTicks) {
		float offX = (float) (Minecraft.getInstance().gameRenderer.getMainCamera().getPosition().x() / 8000.0D);
		float offY = (float) (Minecraft.getInstance().gameRenderer.getMainCamera().getPosition().z() / 8000.0D);
		this.starfieldEffect.setTimeScale(0.00000025F).setZoom(0.8F).setOffset(offX, offY, 0);
		this.starfieldEffect.process(partialTicks);
		Minecraft.getInstance().getMainRenderTarget().bindWrite(true);
	}

	public static Vec3 projectViewFromEntity(Entity entity, double partialTicks) {
		double d0 = Mth.lerp(partialTicks, entity.xo, entity.getX());
		double d1 = Mth.lerp(partialTicks, entity.yo, entity.getY());
		double d2 = Mth.lerp(partialTicks, entity.zo, entity.getZ());
		return new Vec3(d0, d1, d2);
	}

	/**
	 * Used to target a specific PostPass uniform value.
	 * @param index
	 * @param name
	 * @return uniform in PostPass index (index) with key of (name)
	 */
	public Uniform getUniform(int index, String name) {
		if (passes.isEmpty()) return null;
		return this.passes.get(index).getEffect().getUniform(name);
	}
}
