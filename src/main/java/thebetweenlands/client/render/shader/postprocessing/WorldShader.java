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
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import thebetweenlands.client.event.handler.FogHandler;
import thebetweenlands.client.render.shader.DepthBuffer;
import thebetweenlands.client.render.shader.LightSource;
import thebetweenlands.client.render.shader.ResizableFramebuffer;
import thebetweenlands.common.world.WorldProviderBetweenlands;
import thebetweenlands.util.GLUProjection;
import thebetweenlands.util.GLUProjection.ClampMode;
import thebetweenlands.util.GLUProjection.Projection;
import thebetweenlands.util.config.ConfigHandler;

public class WorldShader extends PostProcessingEffect {
	private final DepthBuffer depthBuffer = new DepthBuffer();
	private final ResizableFramebuffer blitBuffer = new ResizableFramebuffer(false);

	private FloatBuffer mvBuffer = GLAllocation.createDirectFloatBuffer(16);
	private FloatBuffer pmBuffer = GLAllocation.createDirectFloatBuffer(16);

	private Matrix4f invertedModelviewProjectionMatrix;
	private Matrix4f modelviewProjectionMatrix;
	private Matrix4f modelviewMatrix;
	private Matrix4f projectionMatrix;

	public static final int MAX_LIGHT_SOURCES_PER_PASS = 32;
	private List<LightSource> lightSources = new ArrayList<LightSource>();

	//Uniforms
	private int depthUniformID = -1;
	private int invMVPUniformID = -1;
	private int fogModeUniformID = -1;
	private int[] lightSourcePositionUniformIDs = new int[MAX_LIGHT_SOURCES_PER_PASS];
	private int[] lightSourceColorUniformIDs = new int[MAX_LIGHT_SOURCES_PER_PASS];
	private int[] lightSourceRadiusUniformIDs = new int[MAX_LIGHT_SOURCES_PER_PASS];
	private int lightSourceAmountUniformID = -1;

	private int currentLightIndex = 0;

	@Override
	protected ResourceLocation[] getShaders() {
		return new ResourceLocation[] {new ResourceLocation("thebetweenlands:shaders/postprocessing/world/world.vsh"), new ResourceLocation("thebetweenlands:shaders/postprocessing/world/world.fsh")};
	}

	@Override
	protected void deleteEffect() {
		this.depthBuffer.deleteBuffer();
	}

	@Override
	protected boolean initEffect() {
		this.depthUniformID = this.getUniform("s_diffuse_depth");
		this.invMVPUniformID = this.getUniform("u_INVMVP");
		this.fogModeUniformID = this.getUniform("u_fogMode");
		for(int i = 0; i < MAX_LIGHT_SOURCES_PER_PASS; i++) {
			this.lightSourcePositionUniformIDs[i] = this.getUniform("u_lightSources[" + i + "].position");
		}
		for(int i = 0; i < MAX_LIGHT_SOURCES_PER_PASS; i++) {
			this.lightSourceColorUniformIDs[i] = this.getUniform("u_lightSources[" + i + "].color");
		}
		for(int i = 0; i < MAX_LIGHT_SOURCES_PER_PASS; i++) {
			this.lightSourceRadiusUniformIDs[i] = this.getUniform("u_lightSources[" + i + "].radius");
		}
		this.lightSourceAmountUniformID = this.getUniform("u_lightSourcesAmount");
		return true;
	}

	private static final Comparator<LightSource> LIGHT_SOURCE_SORTER = new Comparator<LightSource>(){
		@Override
		public int compare(LightSource o1, LightSource o2) {
			double dx1 = o1.x - Minecraft.getMinecraft().getRenderManager().viewerPosX;
			double dy1 = o1.y - Minecraft.getMinecraft().getRenderManager().viewerPosY;
			double dz1 = o1.z - Minecraft.getMinecraft().getRenderManager().viewerPosZ;
			double dx2 = o2.x - Minecraft.getMinecraft().getRenderManager().viewerPosX;
			double dy2 = o2.y - Minecraft.getMinecraft().getRenderManager().viewerPosY;
			double dz2 = o2.z - Minecraft.getMinecraft().getRenderManager().viewerPosZ;
			double d1 = Math.sqrt(dx1*dx1 + dy1*dy1 + dz1*dz1);
			double d2 = Math.sqrt(dx2*dx2 + dy2*dy2 + dz2*dz2);
			if(d1 > d2) {
				return 1;
			} else if(d1 < d2) {
				return -1;
			}
			return 0;
		}
	};

	@Override
	protected void uploadUniforms() {
		this.uploadSampler(this.depthUniformID, this.depthBuffer.getTexture(), 1);
		this.uploadMatrix4f(this.invMVPUniformID, this.invertedModelviewProjectionMatrix);
		this.uploadInt(this.fogModeUniformID, FogHandler.getCurrentFogMode());

		//Sort lights by distance
		Collections.sort(this.lightSources, LIGHT_SOURCE_SORTER);

		final int renderedLightSources = Math.min(MAX_LIGHT_SOURCES_PER_PASS, this.lightSources.size() - this.currentLightIndex * MAX_LIGHT_SOURCES_PER_PASS);

		final double renderPosX = Minecraft.getMinecraft().getRenderManager().viewerPosX;
		final double renderPosY = Minecraft.getMinecraft().getRenderManager().viewerPosY;
		final double renderPosZ = Minecraft.getMinecraft().getRenderManager().viewerPosZ;

		for(int i = 0; i < renderedLightSources; i++) {
			LightSource lightSource = this.lightSources.get(this.currentLightIndex * MAX_LIGHT_SOURCES_PER_PASS + i);
			this.uploadFloat(this.lightSourcePositionUniformIDs[i], (float)(lightSource.x - renderPosX), (float)(lightSource.y - renderPosY), (float)(lightSource.z - renderPosZ));
			this.uploadFloat(this.lightSourceColorUniformIDs[i], lightSource.r, lightSource.g, lightSource.b);
			this.uploadFloat(this.lightSourceRadiusUniformIDs[i], lightSource.radius);
		}

		this.uploadInt(this.lightSourceAmountUniformID, renderedLightSources);
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
	 * @return
	 */
	protected final Framebuffer getMainFramebuffer() {
		return Minecraft.getMinecraft().getFramebuffer();
	}

	/**
	 * Returns the depth buffer
	 * @return
	 */
	public DepthBuffer getDepthBuffer() {
		return this.depthBuffer;
	}

	/**
	 * Updates following matrices: MV (Modelview), PM (Projection), MVP (Modelview x Projection), INVMVP (Inverted MVP)
	 */
	public void updateMatrices() {
		GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX, mvBuffer);
		GL11.glGetFloat(GL11.GL_PROJECTION_MATRIX, pmBuffer);
		Matrix4f modelviewMatrix = (Matrix4f) new Matrix4f().load(mvBuffer.asReadOnlyBuffer());
		this.modelviewMatrix = modelviewMatrix;
		Matrix4f projectionMatrix = (Matrix4f) new Matrix4f().load(pmBuffer.asReadOnlyBuffer());
		this.projectionMatrix = projectionMatrix;
		Matrix4f MVP = new Matrix4f();
		Matrix4f.mul(projectionMatrix, modelviewMatrix, MVP);
		this.modelviewProjectionMatrix = MVP;
		this.invertedModelviewProjectionMatrix = Matrix4f.invert(MVP, new Matrix4f());
	}

	/**
	 * Returns the inverted MVP matrix
	 * @return
	 */
	public Matrix4f getInvertedModelviewProjectionMatrix() {
		return this.invertedModelviewProjectionMatrix;
	}

	/**
	 * Returns the MVP matrix
	 * @return
	 */
	public Matrix4f getModelviewProjectionMatrix() {
		return this.modelviewProjectionMatrix;
	}

	/**
	 * Returns the MV matrix
	 * @return
	 */
	public Matrix4f getModelviewMatrix() {
		return this.modelviewMatrix;
	}

	/**
	 * Returns the projection matrix
	 * @return
	 */
	public Matrix4f getProjectionMatrix() {
		return this.projectionMatrix;
	}

	/**
	 * Adds a dynamic light source for this frame
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
	 * @return
	 */
	public int getLightSourcesAmount() {
		return this.lightSources.size();
	}

	/**
	 * Sets the current light index
	 * @param index
	 */
	public void setLightIndex(int index) {
		this.currentLightIndex = index;
	}

	//Shader textures
	private Framebuffer gasTextureBaseFBO = null;
	private Framebuffer gasTextureFBO = null;
	private Warp gasWarpEffect = null;

	private Framebuffer starfieldTextureFBO = null;
	private Starfield starfieldEffect = null;

	//Shader texture IDs
	public int getGasTextureID() {
		return this.gasTextureFBO != null ? this.gasTextureFBO.framebufferTexture : -1;
	}
	public int getStarfieldTextureID() {
		return this.starfieldTextureFBO != null ? this.starfieldTextureFBO.framebufferTexture : -1;
	}

	public void updateTextures(float partialTicks) {
		World world = Minecraft.getMinecraft().theWorld;
		if(world != null) {
			boolean hasCloud = false;
			//TODO: Requires Gas Cloud
			//			for(Entity e : (List<Entity>) Minecraft.getMinecraft().theWorld.loadedEntityList) {
			//				if(e instanceof EntityGasCloud) {
			//					hasCloud = true;
			//					break;
			//				}
			//			}
			if(hasCloud) {
				//Update gas texture
				if(this.gasTextureFBO == null) {
					this.gasTextureFBO = new Framebuffer(64, 64, false);
					this.gasTextureBaseFBO = new Framebuffer(64, 64, false);

					this.gasWarpEffect = new Warp().setTimeScale(0.00004F).setScale(40.0F).setMultiplier(3.55F);
					this.gasWarpEffect.init();
				}

				float worldTimeInterp = world.getWorldTime() + partialTicks;
				float offsetX = ((float)Math.sin((worldTimeInterp / 20.0F) % (Math.PI * 2.0D)) + 1.0F) / 800.0F;
				float offsetY = ((float)Math.cos((worldTimeInterp / 20.0F) % (Math.PI * 2.0D)) + 1.0F) / 800.0F;
				this.gasWarpEffect.setOffset(offsetX, offsetY)
				.setWarpDir(0.75F, 0.75F);

				this.gasTextureFBO.bindFramebuffer(false);
				GL11.glClearColor(1, 1, 1, 1);
				GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);

				this.gasTextureBaseFBO.bindFramebuffer(false);
				GL11.glClearColor(1, 1, 1, 1);
				GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);

				this.gasWarpEffect.create(this.gasTextureFBO)
				.setSource(this.gasTextureBaseFBO.framebufferTexture)
				.setPreviousFramebuffer(Minecraft.getMinecraft().getFramebuffer())
				.setRenderDimensions(64.0F * 20.0F, 64.0F * 20.0F).render();
			}
		}

		//Update starfield texture
		if(this.starfieldTextureFBO == null) {
			this.starfieldTextureFBO = new Framebuffer(ConfigHandler.skyResolution, ConfigHandler.skyResolution, false);
			this.starfieldEffect = (Starfield) new Starfield(true).init();
		}

		float offX = (float)(Minecraft.getMinecraft().getRenderManager().viewerPosX / 8000.0D);
		float offY = (float)(-Minecraft.getMinecraft().getRenderManager().viewerPosZ / 8000.0D);
		float offZ = (float)(-Minecraft.getMinecraft().getRenderManager().viewerPosY / 10000.0D);
		this.starfieldEffect.setTimeScale(0.00000025F).setZoom(0.8F).setOffset(offX, offY, offZ);
		this.starfieldEffect.create(this.starfieldTextureFBO)
		.setPreviousFramebuffer(Minecraft.getMinecraft().getFramebuffer())
		.setRenderDimensions(ConfigHandler.skyResolution, ConfigHandler.skyResolution).render();
	}

	private OcclusionExtractor occlusionExtractor = null;
	private GodRay godRayEffect = null;

	private void applyBloodSky(float partialTicks) {
		float skyTransparency = 0.0F;

		boolean hasBeat = false;

		World world = Minecraft.getMinecraft().theWorld;
		if(world != null) {
			if(world.provider instanceof WorldProviderBetweenlands) {
				WorldProviderBetweenlands provider = (WorldProviderBetweenlands) world.provider;
				skyTransparency += provider.getWorldData().getEnvironmentEventRegistry().BLOODSKY.getSkyTransparency(partialTicks);
				if(skyTransparency > 0.01F) {
					hasBeat = true;
				}
				skyTransparency += provider.getWorldData().getEnvironmentEventRegistry().SPOOPY.getSkyTransparency(partialTicks);
			}
		}

		if(skyTransparency <= 0.01F) {
			return;
		} else if(skyTransparency > 1.0F) {
			skyTransparency = 1.0F;
		}

		if(this.occlusionExtractor == null) {
			this.occlusionExtractor = (OcclusionExtractor) new OcclusionExtractor().init();
		}
		if(this.godRayEffect == null) {
			this.godRayEffect = (GodRay) new GodRay().init();
		}

		ScaledResolution scaledResolution = new ScaledResolution(Minecraft.getMinecraft());
		double renderWidth = scaledResolution.getScaledWidth();
		double renderHeight = scaledResolution.getScaledHeight();

		float rayX = 0.5F;
		float rayY = 1.0F;

		Vec3d lightPos = new Vec3d(45, 30, 30);

		//Get screen space coordinates of light source
		Projection p = GLUProjection.getInstance().project(lightPos.xCoord, lightPos.yCoord, lightPos.zCoord, ClampMode.NONE, false);

		//Get light source positions in texture coords
		rayX = (float)(p.getX() / renderWidth);
		rayY = 1.0F - (float)(p.getY() / renderHeight);

		//Calculate angle differences
		Vec3d lookVec = Minecraft.getMinecraft().thePlayer.getLook(partialTicks);
		lookVec = new Vec3d(lookVec.xCoord, 0, lookVec.zCoord);
		lookVec = lookVec.normalize();
		Vec3d sLightPos = new Vec3d(lightPos.xCoord, 0, lightPos.zCoord).normalize();
		float lightXZAngle = (float) Math.toDegrees(Math.acos(sLightPos.dotProduct(lookVec)));
		float fovX = GLUProjection.getInstance().getFovX() / 2.0F;
		float angDiff = Math.abs(lightXZAngle);

		float decay = 0.96F;
		float illuminationDecay = 0.44F;
		float weight = 0.12F;

		//Lower decay if outside of view
		if(angDiff > fovX) {
			float mult = ((angDiff - fovX) / 400.0F);
			decay *= 1.0F - mult;
			illuminationDecay *= 1.0F - mult;
			weight *= 1.0F - mult;
		}

		int depthTexture = this.depthBuffer.getTexture();
		//TODO: Requires Sky implementation
		//		int clipPlaneBuffer = BLSkyRenderer.INSTANCE.clipPlaneBuffer.getDepthTexture();
		//
		//		if(depthTexture < 0 || clipPlaneBuffer < 0) return; //FBOs not yet ready
		//
		//		//Extract occluding objects
		//		this.occlusionExtractor.setDepthTextures(depthTexture, clipPlaneBuffer);
		//		this.occlusionExtractor.create(this.getBlitBuffer("bloodSkyBlitBuffer1"))
		//		.setSource(Minecraft.getMinecraft().getFramebuffer().framebufferTexture)
		//		.setPreviousFBO(Minecraft.getMinecraft().getFramebuffer())
		//		.setRenderDimensions(Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight)
		//		.render();
		//
		//		//Apply god's ray
		//		float beat = 0.0F;
		//		if(hasBeat) {
		//			beat = Math.abs(((float)Math.sin(System.nanoTime() / 100000000.0D) / 3.0F) / (Math.abs((float)Math.sin(System.nanoTime() / 4000000000.0D) * (float)Math.sin(System.nanoTime() / 4000000000.0D) * (float)Math.sin(System.nanoTime() / 4000000000.0D + 0.05F) * 120.0F) * 180.0F + 15.5F) * 30.0F);
		//		}
		//		float density = 0.1F + beat;
		//		this.godRayEffect.setOcclusionMap(this.getBlitBuffer("bloodSkyBlitBuffer1")).setParams(0.8F, decay, density * 4.0F, weight, illuminationDecay).setRayPos(rayX, rayY);
		//		this.godRayEffect.create(this.getBlitBuffer("bloodSkyBlitBuffer0"))
		//		.setSource(this.getBlitBuffer("bloodSkyBlitBuffer1").framebufferTexture)
		//		.setPreviousFBO(Minecraft.getMinecraft().getFramebuffer())
		//		.setRenderDimensions(Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight)
		//		.render();
		//
		//		//Render to screen
		//		//Render blit buffer to main buffer
		//		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		//		GL11.glEnable(GL11.GL_BLEND);
		//
		//		//Render world to blit buffer
		//		this.getBlitBuffer("bloodSkyBlitBuffer2").bindFramebuffer(false);
		//		GL11.glClearColor(0, 0, 0, 0);
		//		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
		//		GL11.glEnable(GL11.GL_TEXTURE_2D);
		//		GL11.glColor4f(1, 1, 1, 1);
		//		GL11.glBindTexture(GL11.GL_TEXTURE_2D, Minecraft.getMinecraft().getFramebuffer().framebufferTexture);
		//		GL11.glBegin(GL11.GL_TRIANGLES);
		//		GL11.glTexCoord2d(0.0D, 1.0D);
		//		GL11.glVertex2d(0, 0);
		//		GL11.glTexCoord2d(0.0D, 0.0D);
		//		GL11.glVertex2d(0, renderHeight);
		//		GL11.glTexCoord2d(1.0D, 0.0D);
		//		GL11.glVertex2d(renderWidth, renderHeight);
		//		GL11.glTexCoord2d(1.0D, 0.0D);
		//		GL11.glVertex2d(renderWidth, renderHeight);
		//		GL11.glTexCoord2d(1.0D, 1.0D);
		//		GL11.glVertex2d(renderWidth, 0);
		//		GL11.glTexCoord2d(0.0D, 1.0D);
		//		GL11.glVertex2d(0, 0);
		//		GL11.glEnd();
		//		//Render blit buffer to screen
		//		Minecraft.getMinecraft().getFramebuffer().bindFramebuffer(false);
		//		GL11.glEnable(GL11.GL_TEXTURE_2D);
		//		GL11.glColor4f(1.0F, 0.8F, 0.2F, skyTransparency);
		//		GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.getBlitBuffer("bloodSkyBlitBuffer2").framebufferTexture);
		//		GL11.glBegin(GL11.GL_TRIANGLES);
		//		GL11.glTexCoord2d(0.0D, 1.0D);
		//		GL11.glVertex2d(0, 0);
		//		GL11.glTexCoord2d(0.0D, 0.0D);
		//		GL11.glVertex2d(0, renderHeight);
		//		GL11.glTexCoord2d(1.0D, 0.0D);
		//		GL11.glVertex2d(renderWidth, renderHeight);
		//		GL11.glTexCoord2d(1.0D, 0.0D);
		//		GL11.glVertex2d(renderWidth, renderHeight);
		//		GL11.glTexCoord2d(1.0D, 1.0D);
		//		GL11.glVertex2d(renderWidth, 0);
		//		GL11.glTexCoord2d(0.0D, 1.0D);
		//		GL11.glVertex2d(0, 0);
		//		GL11.glEnd();
		//
		//		//Render god's ray overlay
		//		GL11.glEnable(GL11.GL_TEXTURE_2D);
		//		GL11.glColor4f(0.7F, 0.1F, 0.0F, skyTransparency / 2.5F);
		//		GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.getBlitBuffer("bloodSkyBlitBuffer0").framebufferTexture);
		//		GL11.glBegin(GL11.GL_TRIANGLES);
		//		GL11.glTexCoord2d(0.0D, 1.0D);
		//		GL11.glVertex2d(0, 0);
		//		GL11.glTexCoord2d(0.0D, 0.0D);
		//		GL11.glVertex2d(0, renderHeight);
		//		GL11.glTexCoord2d(1.0D, 0.0D);
		//		GL11.glVertex2d(renderWidth, renderHeight);
		//		GL11.glTexCoord2d(1.0D, 0.0D);
		//		GL11.glVertex2d(renderWidth, renderHeight);
		//		GL11.glTexCoord2d(1.0D, 1.0D);
		//		GL11.glVertex2d(renderWidth, 0);
		//		GL11.glTexCoord2d(0.0D, 1.0D);
		//		GL11.glVertex2d(0, 0);
		//		GL11.glEnd();
	}

	private Swirl swirlEffect = null;
	private float swirlAngle = 0.0F;
	private float lastSwirlAngle = 0.0F;

	public void setSwirlAngle(float swirlAngle) {
		this.swirlAngle = swirlAngle;
		this.lastSwirlAngle = swirlAngle;
	}

	public float getSwirlAngle() {
		return this.swirlAngle;
	}

	private void applySwirl(float partialTicks) {
		if(this.swirlEffect == null) {
			this.swirlEffect = (Swirl) new Swirl().init();
		}

		this.swirlAngle = this.swirlAngle + (this.swirlAngle - this.lastSwirlAngle) * partialTicks;
		this.lastSwirlAngle = this.swirlAngle;

		if(this.swirlAngle != 0.0F) {
			Framebuffer mainFramebuffer = Minecraft.getMinecraft().getFramebuffer();
			Framebuffer blitFramebuffer = this.blitBuffer.getFramebuffer(mainFramebuffer.framebufferWidth, mainFramebuffer.framebufferHeight);

			//Render swirl to blit buffer
			this.swirlEffect.setAngle(this.swirlAngle);
			this.swirlEffect.create(mainFramebuffer)
			.setSource(mainFramebuffer.framebufferTexture)
			.setBlitFramebuffer(blitFramebuffer)
			.setPreviousFramebuffer(mainFramebuffer)
			.setRenderDimensions(Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight)
			.render();

			/*ScaledResolution scaledResolution = new ScaledResolution(Minecraft.getMinecraft());

			//Render blit buffer to main buffer
			GL11.glPushMatrix();

			double renderWidth = scaledResolution.getScaledWidth();
			double renderHeight = scaledResolution.getScaledHeight();
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.getBlitBuffer("swirlBlitBuffer").framebufferTexture);
			GL11.glBegin(GL11.GL_TRIANGLES);
			GL11.glTexCoord2d(0.0D, 1.0D);
			GL11.glVertex2d(0, 0);
			GL11.glTexCoord2d(0.0D, 0.0D);
			GL11.glVertex2d(0, renderHeight);
			GL11.glTexCoord2d(1.0D, 0.0D);
			GL11.glVertex2d(renderWidth, renderHeight);
			GL11.glTexCoord2d(1.0D, 0.0D);
			GL11.glVertex2d(renderWidth, renderHeight);
			GL11.glTexCoord2d(1.0D, 1.0D);
			GL11.glVertex2d(renderWidth, 0);
			GL11.glTexCoord2d(0.0D, 1.0D);
			GL11.glVertex2d(0, 0);
			GL11.glEnd();

			GL11.glPopMatrix();*/
		}
	}
}
