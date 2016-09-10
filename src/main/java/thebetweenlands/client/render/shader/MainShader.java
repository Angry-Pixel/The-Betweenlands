package thebetweenlands.client.render.shader;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import thebetweenlands.client.render.shader.base.CShaderGroup;
import thebetweenlands.client.render.shader.base.CShaderInt;
import thebetweenlands.client.render.shader.base.WorldShader;
import thebetweenlands.client.render.shader.effect.GodRayEffect;
import thebetweenlands.client.render.shader.effect.OcclusionExtractor;
import thebetweenlands.client.render.shader.effect.StarfieldEffect;
import thebetweenlands.client.render.shader.effect.SwirlEffect;
import thebetweenlands.client.render.shader.effect.WarpEffect;
import thebetweenlands.common.world.WorldProviderBetweenlands;
import thebetweenlands.util.GLUProjection;
import thebetweenlands.util.GLUProjection.ClampMode;
import thebetweenlands.util.GLUProjection.Projection;
import thebetweenlands.util.config.ConfigHandler;

public class MainShader extends WorldShader {
	private DepthBuffer depthBuffer;
	private List<LightSource> lightSources = new ArrayList<LightSource>();
	private net.minecraft.client.renderer.Matrix4f INVMVP;
	private net.minecraft.client.renderer.Matrix4f MVP;
	private net.minecraft.client.renderer.Matrix4f MV;
	private net.minecraft.client.renderer.Matrix4f PM;
	private FloatBuffer mvBuffer = GLAllocation.createDirectFloatBuffer(16);
	private FloatBuffer pmBuffer = GLAllocation.createDirectFloatBuffer(16);
	private Map<String, GeometryBuffer> geometryBuffers = new HashMap<String, GeometryBuffer>();
	private Map<String, Framebuffer> blitBuffers = new HashMap<String, Framebuffer>();

	public MainShader(TextureManager textureManager,
			IResourceManager resourceManager, Framebuffer frameBuffer,
			ResourceLocation shaderDescription, ResourceLocation shaderPath,
			ResourceLocation assetsPath) {
		super(textureManager, resourceManager, frameBuffer, shaderDescription,
				shaderPath, assetsPath);
	}

	/**
	 * Returns a geometry buffer for the specified sampler name
	 * @param samplerName
	 * @return
	 */
	public GeometryBuffer getGeometryBuffer(String samplerName) {
		GeometryBuffer geomBuffer = this.geometryBuffers.get(samplerName);
		if(geomBuffer == null) {
			geomBuffer = new GeometryBuffer(true);
			this.geometryBuffers.put(samplerName, geomBuffer);
		}
		return geomBuffer;
	}

	/**
	 * Returns a fullscreen blit FBO
	 * @return
	 */
	public Framebuffer getBlitBuffer(String name) {
		Framebuffer fbo = this.blitBuffers.get(name);
		if(fbo == null) {
			fbo = new Framebuffer(Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight, true);
			this.blitBuffers.put(name, fbo);
		}
		return fbo;
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
	 * Returns the depth buffer
	 * @return
	 */
	public int getDepthTexture() {
		if(this.depthBuffer != null)
			return this.depthBuffer.getTexture();
		return -1;
	}

	/**
	 * Deletes the shader group and all buffers to free memory
	 */
	@Override
	public void delete() {
		super.delete();
		this.deleteBuffers();
	}

	private void deleteBuffers() {
		this.depthBuffer.deleteBuffer();
		for(GeometryBuffer gBuffer : this.geometryBuffers.values()) {
			gBuffer.deleteBuffers();
		}
		this.geometryBuffers.clear();
		for(Framebuffer fbo : this.blitBuffers.values()) {
			fbo.deleteFramebuffer();
		}
		this.blitBuffers.clear();
	}

	/**
	 * Updates all buffers
	 * @param input
	 */
	public void updateBuffers(Framebuffer input) {
		if(this.depthBuffer == null)
			this.depthBuffer = new DepthBuffer();
		if(this.depthBuffer.blitDepthBuffer(input)) {
			this.updateSampler("diffuse_depth", this.depthBuffer.getTexture());
		}

		for(Entry<String, GeometryBuffer> geomBufferEntry : this.geometryBuffers.entrySet()) {
			GeometryBuffer geometryBuffer = geomBufferEntry.getValue();
			geometryBuffer.updateGeometryBuffer(input.framebufferWidth, input.framebufferHeight);
			if(geometryBuffer.hasDepthBuffer())
				geometryBuffer.updateDepthBuffer();
			String samplerName = geomBufferEntry.getKey();
			int geomBuffer = geometryBuffer.getDiffuseTexture();
			int geomDepthBuffer = geometryBuffer.getDepthTexture();
			this.updateSampler(samplerName, geomBuffer);
			if(geomDepthBuffer >= 0) {
				this.updateSampler(samplerName + "_depth", geomDepthBuffer);
			}
		}

		for(Entry<String, Framebuffer> fboEntry : this.blitBuffers.entrySet()) {
			Framebuffer fbo = fboEntry.getValue();
			if(input.framebufferWidth != fbo.framebufferWidth
					|| input.framebufferHeight != fbo.framebufferHeight) {
				fbo.deleteFramebuffer();
				fbo = new Framebuffer(input.framebufferWidth, input.framebufferHeight, true);
				this.blitBuffers.put(fboEntry.getKey(), fbo);
			}
		}

		input.bindFramebuffer(false);
	}

	private static double renderPosX;
	private static double renderPosY;
	private static double renderPosZ;

	@Override
	public void updateShader(CShaderInt shader) {
		RenderManager manager = Minecraft.getMinecraft().getRenderManager();
		renderPosX = manager.viewerPosX;
		renderPosY = manager.viewerPosY;
		renderPosZ = manager.viewerPosZ;
		this.uploadMatrices(shader);
		this.uploadLights(shader);
		this.uploadMisc(shader);
	}

	private void uploadMisc(CShaderInt shader) {
		shader.updateUniform("u_zNear", (uniform) -> uniform.set(0.05F));
		shader.updateUniform("u_zFar", (uniform) -> uniform.set((float)(Minecraft.getMinecraft().gameSettings.renderDistanceChunks * 16) * 2.0F));
		shader.updateUniform("u_camPos", (uniform) -> uniform.set((float)renderPosX, (float)renderPosY, (float)renderPosZ));
		shader.updateUniform("u_msTime", (uniform) -> uniform.set(System.nanoTime() / 1000000.0F));
	}

	private void uploadMatrices(CShaderInt shader) {
		shader.updateUniform("u_INVMVP", (uniform) -> uniform.set(this.INVMVP));
		shader.updateUniform("u_MVP", (uniform) -> uniform.set(this.MVP));
		shader.updateUniform("u_MV", (uniform) -> uniform.set(this.MV));
		shader.updateUniform("u_PM", (uniform) -> uniform.set(this.PM));
	}

	private static final Comparator<LightSource> lightSourceSorter = new Comparator<LightSource>(){
		@Override
		public int compare(LightSource o1, LightSource o2) {
			double dx1 = o1.x - renderPosX;
			double dy1 = o1.y - renderPosY;
			double dz1 = o1.z - renderPosZ;
			double dx2 = o2.x - renderPosX;
			double dy2 = o2.y - renderPosY;
			double dz2 = o2.z - renderPosZ;
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

	private void uploadLights(CShaderInt shader) {
		//Sorts lights by distance
		Collections.sort(this.lightSources, lightSourceSorter);

		shader.updateUniform("u_lightColorsR", (uniform) -> {
			float[] posArray = new float[32];
			for(int i = 0; i < this.lightSources.size(); i++) {
				if(i >= 32) break;
				posArray[i] = (float)(this.lightSources.get(i).r);
			}
			uniform.set(posArray);
		});
		shader.updateUniform("u_lightColorsG", (uniform) -> {
			float[] posArray = new float[32];
			for(int i = 0; i < this.lightSources.size(); i++) {
				if(i >= 32) break;
				posArray[i] = (float)(this.lightSources.get(i).g);
			}
			uniform.set(posArray);
		});
		shader.updateUniform("u_lightColorsB", (uniform) -> {
			float[] posArray = new float[32];
			for(int i = 0; i < this.lightSources.size(); i++) {
				if(i >= 32) break;
				posArray[i] = (float)(this.lightSources.get(i).b);
			}
			uniform.set(posArray);
		});
		shader.updateUniform("u_lightSourcesX", (uniform) -> {
			float[] posArray = new float[32];
			for(int i = 0; i < this.lightSources.size(); i++) {
				if(i >= 32) break;
				posArray[i] = (float)(-renderPosX + this.lightSources.get(i).x);
			}
			uniform.set(posArray);
		});
		shader.updateUniform("u_lightSourcesY", (uniform) -> {
			float[] posArray = new float[32];
			for(int i = 0; i < this.lightSources.size(); i++) {
				if(i >= 32) break;
				posArray[i] = (float)(-renderPosY + this.lightSources.get(i).y);
			}
			uniform.set(posArray);
		});
		shader.updateUniform("u_lightSourcesZ", (uniform) -> {
			float[] posArray = new float[32];
			for(int i = 0; i < this.lightSources.size(); i++) {
				if(i >= 32) break;
				posArray[i] = (float)(-renderPosZ + this.lightSources.get(i).z);
			}
			uniform.set(posArray);
		});
		shader.updateUniform("u_lightRadii", (uniform) -> {
			float[] posArray = new float[32];
			for(int i = 0; i < this.lightSources.size(); i++) {
				if(i >= 32) break;
				posArray[i] = (float)(this.lightSources.get(i).radius);
			}
			uniform.set(posArray);
		});
		shader.updateUniform("u_lightSources", (uniform) -> {
			int count = this.lightSources.size();
			if(count > 32) {
				count = 32;
			}
			uniform.set(count);
		});
	}

	/**
	 * Updates following matrices: MV (Modelview), PM (Projection), MVP (Modelview x Projection), INVMVP (Inverted MVP)
	 */
	public void updateMatrices() {
		GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX, mvBuffer);
		GL11.glGetFloat(GL11.GL_PROJECTION_MATRIX, pmBuffer);
		org.lwjgl.util.vector.Matrix4f modelviewMatrix = (org.lwjgl.util.vector.Matrix4f) new org.lwjgl.util.vector.Matrix4f().load(mvBuffer.asReadOnlyBuffer());
		org.lwjgl.util.vector.Matrix4f invModelviewMatrix = (org.lwjgl.util.vector.Matrix4f) new org.lwjgl.util.vector.Matrix4f().load(mvBuffer.asReadOnlyBuffer()).invert();
		this.MV = this.toMinecraftMatrix(modelviewMatrix);
		org.lwjgl.util.vector.Matrix4f projectionMatrix = (org.lwjgl.util.vector.Matrix4f) new org.lwjgl.util.vector.Matrix4f().load(pmBuffer.asReadOnlyBuffer());
		this.PM = this.toMinecraftMatrix(projectionMatrix);
		org.lwjgl.util.vector.Matrix4f MVP = new org.lwjgl.util.vector.Matrix4f();
		org.lwjgl.util.vector.Matrix4f.mul(projectionMatrix, modelviewMatrix, MVP);
		this.MVP = this.toMinecraftMatrix(MVP);
		this.INVMVP = this.toMinecraftMatrix((org.lwjgl.util.vector.Matrix4f) MVP.invert());
	}

	private net.minecraft.client.renderer.Matrix4f toMinecraftMatrix(org.lwjgl.util.vector.Matrix4f mat) {
		net.minecraft.client.renderer.Matrix4f vecMath = new net.minecraft.client.renderer.Matrix4f();
		vecMath.m00 = mat.m00;
		vecMath.m01 = mat.m01;
		vecMath.m02 = mat.m02;
		vecMath.m03 = mat.m03;
		vecMath.m10 = mat.m10;
		vecMath.m11 = mat.m11;
		vecMath.m12 = mat.m12;
		vecMath.m13 = mat.m13;
		vecMath.m20 = mat.m20;
		vecMath.m21 = mat.m21;
		vecMath.m22 = mat.m22;
		vecMath.m23 = mat.m23;
		vecMath.m30 = mat.m30;
		vecMath.m31 = mat.m31;
		vecMath.m32 = mat.m32;
		vecMath.m33 = mat.m33;
		return vecMath;
	} 

	private SwirlEffect swirlEffect = null;
	private float swirlAngle = 0.0F;
	private float lastSwirlAngle = 0.0F;

	public void setSwirlAngle(float swirlAngle) {
		this.swirlAngle = swirlAngle;
		this.lastSwirlAngle = swirlAngle;
	}

	public float getSwirlAngle() {
		return this.swirlAngle;
	}

	private OcclusionExtractor occlusionExtractor = null;
	private GodRayEffect godRayEffect = null;

	@Override
	public void postShader(CShaderGroup shaderGroup, float partialTicks) {
		ScaledResolution scaledResolution = new ScaledResolution(Minecraft.getMinecraft());
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		GL11.glOrtho(0.0D, scaledResolution.getScaledWidth(), scaledResolution.getScaledHeight(), 0.0D, 1000.0D, 3000.0D);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glLoadIdentity();
		GL11.glTranslatef(0.0F, 0.0F, -2000.0F);

		this.applyBloodSky(partialTicks);
		this.applySwirl(partialTicks);

		this.updateTextures(partialTicks);
	}

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
			this.godRayEffect = (GodRayEffect) new GodRayEffect().init();
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

		int depthTexture = this.getDepthTexture();
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

	private void applySwirl(float partialTicks) {
		if(this.swirlEffect == null) {
			this.swirlEffect = (SwirlEffect) new SwirlEffect().init();
		}

		this.swirlAngle = this.swirlAngle + (this.swirlAngle - this.lastSwirlAngle) * partialTicks;
		this.lastSwirlAngle = this.swirlAngle;

		if(this.swirlAngle != 0.0F) {
			//Render swirl to blit buffer
			this.swirlEffect.setAngle(this.swirlAngle);
			this.swirlEffect.create(this.getBlitBuffer("swirlBlitBuffer"))
			.setSource(Minecraft.getMinecraft().getFramebuffer().framebufferTexture)
			.setPreviousFBO(Minecraft.getMinecraft().getFramebuffer())
			.setRenderDimensions(Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight)
			.render();

			ScaledResolution scaledResolution = new ScaledResolution(Minecraft.getMinecraft());

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

			GL11.glPopMatrix();
		}
	}

	//Shader textures
	private Framebuffer gasTextureBaseFBO = null;
	private Framebuffer gasTextureFBO = null;
	private WarpEffect gasWarpEffect = null;

	private Framebuffer starfieldTextureFBO = null;
	private StarfieldEffect starfieldEffect = null;

	//Shader texture IDs
	public int getGasTextureID() {
		return this.gasTextureFBO != null ? this.gasTextureFBO.framebufferTexture : -1;
	}
	public int getStarfieldTextureID() {
		return this.starfieldTextureFBO != null ? this.starfieldTextureFBO.framebufferTexture : -1;
	}

	private void updateTextures(float partialTicks) {
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

					this.gasWarpEffect = new WarpEffect().setTimeScale(0.00004F).setScale(40.0F).setMultiplier(3.55F);
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
				.setPreviousFBO(Minecraft.getMinecraft().getFramebuffer())
				.setRenderDimensions(64.0F * 20.0F, 64.0F * 20.0F).render();
			}
		}

		//Update starfield texture
		if(this.starfieldTextureFBO == null) {
			this.starfieldTextureFBO = new Framebuffer(ConfigHandler.skyResolution, ConfigHandler.skyResolution, false);
			this.starfieldEffect = (StarfieldEffect) new StarfieldEffect(true).init();
		}

		float offX = (float)(renderPosX / 8000.0D);
		float offY = (float)(-renderPosZ / 8000.0D);
		float offZ = (float)(-renderPosY / 10000.0D);
		this.starfieldEffect.setTimeScale(0.00000025F).setZoom(0.8F).setOffset(offX, offY, offZ);
		this.starfieldEffect.create(this.starfieldTextureFBO)
		.setPreviousFBO(Minecraft.getMinecraft().getFramebuffer())
		.setRenderDimensions(ConfigHandler.skyResolution, ConfigHandler.skyResolution).render();
	}
}
