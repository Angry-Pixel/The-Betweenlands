package thebetweenlands.client.render.shader;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.vecmath.Matrix4f;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.client.shader.ShaderUniform;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import thebetweenlands.client.render.shader.base.CShader;
import thebetweenlands.client.render.shader.base.CShaderGroup;
import thebetweenlands.client.render.shader.base.CShaderInt;
import thebetweenlands.client.render.shader.effect.GodRayEffect;
import thebetweenlands.client.render.shader.effect.OcclusionExtractor;
import thebetweenlands.client.render.shader.effect.StarfieldEffect;
import thebetweenlands.client.render.shader.effect.SwirlEffect;
import thebetweenlands.client.render.shader.effect.WarpEffect;
import thebetweenlands.client.render.sky.BLSkyRenderer;
import thebetweenlands.event.debugging.DebugHandler;
import thebetweenlands.utils.GLUProjection;
import thebetweenlands.utils.GLUProjection.ClampMode;
import thebetweenlands.utils.GLUProjection.Projection;
import thebetweenlands.utils.GLUProjection.Vector3D;
import thebetweenlands.utils.confighandler.ConfigHandler;
import thebetweenlands.utils.GLUProjection.Projection.Type;
import thebetweenlands.world.WorldProviderBetweenlands;
import thebetweenlands.world.events.impl.EventBloodSky;

public class MainShader extends CShader {
	private Framebuffer depthBuffer;
	private List<LightSource> lightSources = new ArrayList<LightSource>();
	private Matrix4f INVMVP;
	private Matrix4f MVP;
	private Matrix4f MV;
	private Matrix4f PM;
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

	public void addLight(LightSource light) {
		this.lightSources.add(light);
	}

	public void clearLights() {
		this.lightSources.clear();
	}

	public Framebuffer getDepthBuffer() {
		if(this.depthBuffer == null) {
			this.depthBuffer = new Framebuffer(Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight, false);
		}
		return this.depthBuffer;
	}

	public void deleteBuffers() {
		this.depthBuffer.deleteFramebuffer();
		for(GeometryBuffer gBuffer : this.geometryBuffers.values()) {
			gBuffer.deleteBuffers();
		}
		this.geometryBuffers.clear();
		for(Framebuffer fbo : this.blitBuffers.values()) {
			fbo.deleteFramebuffer();
		}
		this.blitBuffers.clear();
	}

	public void updateBuffers(Framebuffer input) {
		if(this.depthBuffer == null) {
			this.depthBuffer = new Framebuffer(input.framebufferWidth, input.framebufferHeight, false);
			this.updateSampler("diffuse_depth", this.depthBuffer);
		}
		if(input.framebufferWidth != this.depthBuffer.framebufferWidth
				|| input.framebufferHeight != this.depthBuffer.framebufferHeight) {
			this.depthBuffer.deleteFramebuffer();
			this.depthBuffer = new Framebuffer(input.framebufferWidth, input.framebufferHeight, false);
			this.updateSampler("diffuse_depth", this.depthBuffer);
		}
		input.bindFramebuffer(false);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.depthBuffer.framebufferTexture);
		GL11.glCopyTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_DEPTH_COMPONENT, 0, 0, 
				this.depthBuffer.framebufferTextureWidth, 
				this.depthBuffer.framebufferTextureHeight, 
				0);

		for(Entry<String, GeometryBuffer> geomBufferEntry : this.geometryBuffers.entrySet()) {
			geomBufferEntry.getValue().update(input);
			String samplerName = geomBufferEntry.getKey();
			Framebuffer geomBuffer = geomBufferEntry.getValue().getGeometryBuffer();
			Framebuffer geomDepthBuffer = geomBufferEntry.getValue().getGeometryDepthBuffer();
			this.updateSampler(samplerName, geomBuffer);
			if(geomDepthBuffer != null) {
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

	@Override
	public void updateShader(CShaderInt shader) {
		this.uploadMatrices(shader);
		this.uploadLights(shader);
		this.uploadMisc(shader);
	}

	private void uploadMisc(CShaderInt shader) {
		{
			ShaderUniform uniform = shader.getUniform("u_zNear");
			if(uniform != null) {
				uniform.func_148090_a(0.05F);
			}
		}
		{
			ShaderUniform uniform = shader.getUniform("u_zFar");
			if(uniform != null) {
				uniform.func_148090_a((float)(Minecraft.getMinecraft().gameSettings.renderDistanceChunks * 16) * 2.0F);
			}
		}
		{
			ShaderUniform uniform = shader.getUniform("u_camPos");
			if(uniform != null) {
				uniform.func_148095_a(
						(float)(RenderManager.renderPosX),
						(float)(RenderManager.renderPosY),
						(float)(RenderManager.renderPosZ));
			}
		}
		{
			ShaderUniform uniform = shader.getUniform("u_msTime");
			if(uniform != null) {
				uniform.func_148090_a(System.nanoTime() / 1000000.0F);
			}
		}
	}

	private void uploadMatrices(CShaderInt shader) {
		{
			ShaderUniform uniform = shader.getUniform("u_INVMVP");
			if(uniform != null) {
				uniform.func_148088_a(this.INVMVP);
			}
		}
		{
			ShaderUniform uniform = shader.getUniform("u_MVP");
			if(uniform != null) {
				uniform.func_148088_a(this.MVP);
			}
		}
		{
			ShaderUniform uniform = shader.getUniform("u_MV");
			if(uniform != null) {
				uniform.func_148088_a(this.MV);
			}
		}
		{
			ShaderUniform uniform = shader.getUniform("u_PM");
			if(uniform != null) {
				uniform.func_148088_a(this.PM);
			}
		}
	}

	private static final Comparator<LightSource> lightSourceSorter = new Comparator<LightSource>(){
		@Override
		public int compare(LightSource o1, LightSource o2) {
			double dx1 = o1.x - RenderManager.renderPosX;
			double dy1 = o1.y - RenderManager.renderPosY;
			double dz1 = o1.z - RenderManager.renderPosZ;
			double dx2 = o2.x - RenderManager.renderPosX;
			double dy2 = o2.y - RenderManager.renderPosY;
			double dz2 = o2.z - RenderManager.renderPosZ;
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

		{
			ShaderUniform uniform = shader.getUniform("u_lightColorsR");
			if(uniform != null) {
				float[] posArray = new float[32];
				for(int i = 0; i < this.lightSources.size(); i++) {
					if(i >= 32) break;
					posArray[i] = (float)(this.lightSources.get(i).r);
				}
				uniform.func_148097_a(posArray);
			}
		}
		{
			ShaderUniform uniform = shader.getUniform("u_lightColorsG");
			if(uniform != null) {
				float[] posArray = new float[32];
				for(int i = 0; i < this.lightSources.size(); i++) {
					if(i >= 32) break;
					posArray[i] = (float)(this.lightSources.get(i).g);
				}
				uniform.func_148097_a(posArray);
			}
		}
		{
			ShaderUniform uniform = shader.getUniform("u_lightColorsB");
			if(uniform != null) {
				float[] posArray = new float[32];
				for(int i = 0; i < this.lightSources.size(); i++) {
					if(i >= 32) break;
					posArray[i] = (float)(this.lightSources.get(i).b);
				}
				uniform.func_148097_a(posArray);
			}
		}
		{
			ShaderUniform uniform = shader.getUniform("u_lightSourcesX");
			if(uniform != null) {
				float[] posArray = new float[32];
				for(int i = 0; i < this.lightSources.size(); i++) {
					if(i >= 32) break;
					posArray[i] = (float)(-RenderManager.renderPosX + this.lightSources.get(i).x);
				}
				uniform.func_148097_a(posArray);
			}
		}
		{
			ShaderUniform uniform = shader.getUniform("u_lightSourcesY");
			if(uniform != null) {
				float[] posArray = new float[32];
				for(int i = 0; i < this.lightSources.size(); i++) {
					if(i >= 32) break;
					posArray[i] = (float)(-RenderManager.renderPosY + this.lightSources.get(i).y);
				}
				uniform.func_148097_a(posArray);
			}
		}
		{
			ShaderUniform uniform = shader.getUniform("u_lightSourcesZ");
			if(uniform != null) {
				float[] posArray = new float[32];
				for(int i = 0; i < this.lightSources.size(); i++) {
					if(i >= 32) break;
					posArray[i] = (float)(-RenderManager.renderPosZ + this.lightSources.get(i).z);
				}
				uniform.func_148097_a(posArray);
			}
		}
		{
			ShaderUniform uniform = shader.getUniform("u_lightRadii");
			if(uniform != null) {
				float[] posArray = new float[32];
				for(int i = 0; i < this.lightSources.size(); i++) {
					if(i >= 32) break;
					posArray[i] = (float)(this.lightSources.get(i).radius);
				}
				uniform.func_148097_a(posArray);
			}
		}
		{
			ShaderUniform uniform = shader.getUniform("u_lightSources");
			if(uniform != null) {
				int count = this.lightSources.size();
				if(count > 32) {
					count = 32;
				}
				uniform.func_148090_a(count);
			}
		}
	}

	public void updateMatrices() {
		GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX, mvBuffer);
		GL11.glGetFloat(GL11.GL_PROJECTION_MATRIX, pmBuffer);
		org.lwjgl.util.vector.Matrix4f modelviewMatrix = (org.lwjgl.util.vector.Matrix4f) new org.lwjgl.util.vector.Matrix4f().load(mvBuffer.asReadOnlyBuffer());
		org.lwjgl.util.vector.Matrix4f invModelviewMatrix = (org.lwjgl.util.vector.Matrix4f) new org.lwjgl.util.vector.Matrix4f().load(mvBuffer.asReadOnlyBuffer()).invert();
		this.MV = this.toVecMathMatrix(modelviewMatrix);
		org.lwjgl.util.vector.Matrix4f projectionMatrix = (org.lwjgl.util.vector.Matrix4f) new org.lwjgl.util.vector.Matrix4f().load(pmBuffer.asReadOnlyBuffer());
		this.PM = this.toVecMathMatrix(projectionMatrix);
		org.lwjgl.util.vector.Matrix4f MVP = new org.lwjgl.util.vector.Matrix4f();
		org.lwjgl.util.vector.Matrix4f.mul(projectionMatrix, modelviewMatrix, MVP);
		this.MVP = this.toVecMathMatrix(MVP);
		MVP.invert();
		this.INVMVP = this.toVecMathMatrix(MVP);
	}

	private Matrix4f toVecMathMatrix(org.lwjgl.util.vector.Matrix4f mat) {
		Matrix4f vecMath = new Matrix4f();
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
		this.applyBloodSky(partialTicks);
		this.applySwirl(partialTicks);

		this.updateTextures(partialTicks);
	}

	private void applyBloodSky(float partialTicks) {
		float skyTransparency = 0.0F;

		World world = Minecraft.getMinecraft().theWorld;
		if(world != null) {
			if(world.provider instanceof WorldProviderBetweenlands) {
				WorldProviderBetweenlands provider = (WorldProviderBetweenlands) world.provider;
				EventBloodSky event = provider.getWorldData().getEnvironmentEventRegistry().BLOODSKY;
				skyTransparency = event.getSkyTransparency(partialTicks);
			}
		}

		if(skyTransparency <= 0.0F) {
			return;
		}

		if(this.occlusionExtractor == null) {
			this.occlusionExtractor = (OcclusionExtractor) new OcclusionExtractor().init();
		}
		if(this.godRayEffect == null) {
			this.godRayEffect = (GodRayEffect) new GodRayEffect().init();
		}

		ScaledResolution scaledResolution = new ScaledResolution(Minecraft.getMinecraft(), Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight);
		double renderWidth = scaledResolution.getScaledWidth();
		double renderHeight = scaledResolution.getScaledHeight();

		float rayX = 0.5F;
		float rayY = 1.0F;

		Vec3 lightPos = Vec3.createVectorHelper(45, 30, 30);

		//Get screen space coordinates of light source
		Projection p = GLUProjection.getInstance().project(lightPos.xCoord, lightPos.yCoord, lightPos.zCoord, ClampMode.NONE, false);

		//Get light source positions in texture coords
		rayX = (float)(p.getX() / renderWidth);
		rayY = 1.0F - (float)(p.getY() / renderHeight);

		//Calculate angle differences
		Vec3 lookVec = Minecraft.getMinecraft().thePlayer.getLook(partialTicks);
		lookVec.yCoord = 0;
		lookVec = lookVec.normalize();
		Vec3 sLightPos = Vec3.createVectorHelper(lightPos.xCoord, 0, lightPos.zCoord).normalize();
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

		//Extract occluding objects
		this.occlusionExtractor.setFBOs(this.getDepthBuffer(), BLSkyRenderer.INSTANCE.clipPlaneBuffer.getGeometryDepthBuffer());
		this.occlusionExtractor.apply(Minecraft.getMinecraft().getFramebuffer().framebufferTexture, this.getBlitBuffer("bloodSkyBlitBuffer1"), null, Minecraft.getMinecraft().getFramebuffer(), Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight);

		//Apply god's ray
		float density = 0.1F + Math.abs(((float)Math.sin(System.nanoTime() / 100000000.0D) / 3.0F) / (Math.abs((float)Math.sin(System.nanoTime() / 4000000000.0D) * (float)Math.sin(System.nanoTime() / 4000000000.0D) * (float)Math.sin(System.nanoTime() / 4000000000.0D + 0.05F) * 120.0F) * 180.0F + 15.5F) * 30.0F);
		this.godRayEffect.setOcclusionMap(this.getBlitBuffer("bloodSkyBlitBuffer1")).setParams(0.8F, decay, density * 4.0F, weight, illuminationDecay).setRayPos(rayX, rayY);
		this.godRayEffect.apply(this.getBlitBuffer("bloodSkyBlitBuffer1").framebufferTexture, this.getBlitBuffer("bloodSkyBlitBuffer0"), null, Minecraft.getMinecraft().getFramebuffer(), Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight);

		//Render to screen
		//Render blit buffer to main buffer
		GL11.glPushMatrix();

		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		GL11.glOrtho(0.0D, scaledResolution.getScaledWidth(), scaledResolution.getScaledHeight(), 0.0D, 1000.0D, 3000.0D);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glLoadIdentity();
		GL11.glTranslatef(0.0F, 0.0F, -2000.0F);

		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glEnable(GL11.GL_BLEND);

		//Render world to blit buffer
		this.getBlitBuffer("bloodSkyBlitBuffer2").bindFramebuffer(false);
		GL11.glClearColor(0, 0, 0, 0);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glColor4f(1, 1, 1, 1);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, Minecraft.getMinecraft().getFramebuffer().framebufferTexture);
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
		//Render blit buffer to screen
		Minecraft.getMinecraft().getFramebuffer().bindFramebuffer(false);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glColor4f(1.0F, 0.8F, 0.2F, skyTransparency);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.getBlitBuffer("bloodSkyBlitBuffer2").framebufferTexture);
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

		//Render god's ray overlay
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glColor4f(0.7F, 0.1F, 0.0F, skyTransparency / 2.5F);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.getBlitBuffer("bloodSkyBlitBuffer0").framebufferTexture);
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

	private void applySwirl(float partialTicks) {
		if(this.swirlEffect == null) {
			this.swirlEffect = (SwirlEffect) new SwirlEffect().init();
		}

		this.swirlAngle = this.swirlAngle + (this.swirlAngle - this.lastSwirlAngle) * partialTicks;
		this.lastSwirlAngle = this.swirlAngle;

		if(this.swirlAngle != 0.0F) {
			//Render swirl to blit buffer
			this.swirlEffect.setAngle(this.swirlAngle);
			this.swirlEffect.apply(Minecraft.getMinecraft().getFramebuffer().framebufferTexture, this.getBlitBuffer("swirlBlitBuffer"), null, Minecraft.getMinecraft().getFramebuffer(), Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight);

			ScaledResolution scaledResolution = new ScaledResolution(Minecraft.getMinecraft(), Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight);

			//Render blit buffer to main buffer
			GL11.glPushMatrix();

			GL11.glMatrixMode(GL11.GL_PROJECTION);
			GL11.glLoadIdentity();
			GL11.glOrtho(0.0D, scaledResolution.getScaledWidth_double(), scaledResolution.getScaledHeight_double(), 0.0D, 1000.0D, 3000.0D);
			GL11.glMatrixMode(GL11.GL_MODELVIEW);
			GL11.glLoadIdentity();
			GL11.glTranslatef(0.0F, 0.0F, -2000.0F);

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
		if(DebugHandler.INSTANCE.debugDeferredEffect) {
			//Update gas texture
			if(this.gasTextureFBO == null) {
				this.gasTextureFBO = new Framebuffer(128, 128, false);
				this.gasTextureBaseFBO = new Framebuffer(128, 128, false);

				this.gasWarpEffect = new WarpEffect().setTimeScale(0.00004F).setScale(40.0F).setMultiplier(3.55F);
				this.gasWarpEffect.init();
			} else {
				float warpX = (float)(Math.sin(System.nanoTime() / 20000000000.0D) / 80.0F) + (float)(Math.sin(System.nanoTime() / 5600000000.0D) / 15000.0F) - (float)(Math.cos(System.nanoTime() / 6800000000.0D) / 500.0F);
				float warpY = (float)(Math.sin(System.nanoTime() / 10000000000.0D) / 60.0F) - (float)(Math.cos(System.nanoTime() / 800000000.0D) / 6000.0F) + (float)(Math.cos(System.nanoTime() / 2000000000.0D) / 1000.0F);
				this.gasWarpEffect.setOffset((float)Math.sin(System.nanoTime() / 10000000000.0D) / 80.0F, (float)Math.sin(System.nanoTime() / 10000000000.0D) / 70.0F)
				.setWarpDir(warpX, warpY);

				this.gasTextureFBO.bindFramebuffer(false);
				GL11.glClearColor(1, 1, 1, 1);
				GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);

				this.gasTextureBaseFBO.bindFramebuffer(false);
				GL11.glClearColor(1, 1, 1, 1);
				GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);

				this.gasWarpEffect.apply(this.gasTextureBaseFBO.framebufferTexture, this.gasTextureFBO, null, Minecraft.getMinecraft().getFramebuffer(), 128.0F * 20.0F, 128.0F * 20.0F);
			}
		}

		//Update starfield texture
		if(this.starfieldTextureFBO == null) {
			this.starfieldTextureFBO = new Framebuffer(ConfigHandler.SKY_RESOLUTION, ConfigHandler.SKY_RESOLUTION, false);
			this.starfieldEffect = (StarfieldEffect) new StarfieldEffect().init();
		} else {
			float offX = (float)(Minecraft.getMinecraft().thePlayer.posX / 8000.0D);
			float offY = (float)(-Minecraft.getMinecraft().thePlayer.posZ / 8000.0D);
			float offZ = (float)(-Minecraft.getMinecraft().thePlayer.posY / 10000.0D);
			this.starfieldEffect.setTimeScale(0.00000025F).setZoom(0.8F).setOffset(offX, offY, offZ);
			this.starfieldEffect.apply(-1, this.starfieldTextureFBO, null, Minecraft.getMinecraft().getFramebuffer(), ConfigHandler.SKY_RESOLUTION, ConfigHandler.SKY_RESOLUTION);
		}
	}
}
