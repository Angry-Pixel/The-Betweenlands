package thebetweenlands.client.render.shader.impl;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import javax.vecmath.Matrix4f;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.client.shader.ShaderUniform;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import thebetweenlands.client.render.shader.CShader;
import thebetweenlands.client.render.shader.CShaderInt;

public class MainShader extends CShader {
	private Framebuffer depthBuffer;
	private List<LightSource> lightSources = new ArrayList<LightSource>();
	private Matrix4f INVMVP;
	private Matrix4f MVP;
	private Matrix4f MV;
	private Matrix4f PM;
	private FloatBuffer mvBuffer = GLAllocation.createDirectFloatBuffer(16);
	private FloatBuffer pmBuffer = GLAllocation.createDirectFloatBuffer(16);

	public MainShader(TextureManager textureManager,
			IResourceManager resourceManager, Framebuffer frameBuffer,
			ResourceLocation shaderDescription, ResourceLocation shaderPath,
			ResourceLocation assetsPath) {
		super(textureManager, resourceManager, frameBuffer, shaderDescription,
				shaderPath, assetsPath);
	}

	public void addLight(LightSource light) {
		this.lightSources.add(light);
	}

	public void clearLights() {
		this.lightSources.clear();
	}

	public Framebuffer getDepthBuffer() {
		return this.depthBuffer;
	}

	public void deleteBuffers() {
		this.depthBuffer.deleteFramebuffer();
	}

	public void updateDepthBuffer(Framebuffer input) {
		if(this.depthBuffer == null) {
			this.depthBuffer = new Framebuffer(input.framebufferWidth, input.framebufferHeight, false);
			this.updateSampler("DepthSampler", this.depthBuffer);
		}
		if(input.framebufferWidth != this.depthBuffer.framebufferWidth
				|| input.framebufferHeight != this.depthBuffer.framebufferHeight) {
			this.depthBuffer.deleteFramebuffer();
			this.depthBuffer = new Framebuffer(input.framebufferWidth, input.framebufferHeight, false);
			this.updateSampler("DepthSampler", this.depthBuffer);
		}
		input.bindFramebuffer(false);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.depthBuffer.framebufferTexture);
		GL11.glCopyTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_DEPTH_COMPONENT, 0, 0, 
				this.depthBuffer.framebufferTextureWidth, 
				this.depthBuffer.framebufferTextureHeight, 
				0);
	}

	@Override
	public void updateShader(CShaderInt shader) {
		this.uploadMatrices(shader);
		this.uploadLights(shader);
		this.uploadMisc(shader);
	}

	private void uploadMisc(CShaderInt shader) {
		{
			ShaderUniform uniform = shader.getUniform("zNear");
			if(uniform != null) {
				uniform.func_148090_a(0.05F);
			}
		}
		{
			ShaderUniform uniform = shader.getUniform("zFar");
			if(uniform != null) {
				uniform.func_148090_a((float)(Minecraft.getMinecraft().gameSettings.renderDistanceChunks * 16));
			}
		}
		{
			ShaderUniform uniform = shader.getUniform("CamPos");
			if(uniform != null) {
				uniform.func_148095_a(
						(float)(RenderManager.renderPosX*2),
						(float)(RenderManager.renderPosY*2),
						(float)(RenderManager.renderPosZ*2));
			}
		}
	}

	private void uploadMatrices(CShaderInt shader) {
		{
			ShaderUniform uniform = shader.getUniform("INVMVP");
			if(uniform != null) {
				uniform.func_148088_a(this.INVMVP);
			}
		}
		{
			ShaderUniform uniform = shader.getUniform("MVP");
			if(uniform != null) {
				uniform.func_148088_a(this.MVP);
			}
		}
		{
			ShaderUniform uniform = shader.getUniform("MV");
			if(uniform != null) {
				uniform.func_148088_a(this.MV);
			}
		}
		{
			ShaderUniform uniform = shader.getUniform("PM");
			if(uniform != null) {
				uniform.func_148088_a(this.PM);
			}
		}
	}

	private void uploadLights(CShaderInt shader) {
		{
			ShaderUniform uniform = shader.getUniform("LightColorsR");
			if(uniform != null) {
				float[] posArray = new float[64];
				for(int i = 0; i < this.lightSources.size(); i++) {
					if(i >= 64) break;
					posArray[i] = (float)(this.lightSources.get(i).r);
				}
				uniform.func_148097_a(posArray);
			}
		}
		{
			ShaderUniform uniform = shader.getUniform("LightColorsG");
			if(uniform != null) {
				float[] posArray = new float[64];
				for(int i = 0; i < this.lightSources.size(); i++) {
					if(i >= 64) break;
					posArray[i] = (float)(this.lightSources.get(i).g);
				}
				uniform.func_148097_a(posArray);
			}
		}
		{
			ShaderUniform uniform = shader.getUniform("LightColorsB");
			if(uniform != null) {
				float[] posArray = new float[64];
				for(int i = 0; i < this.lightSources.size(); i++) {
					if(i >= 64) break;
					posArray[i] = (float)(this.lightSources.get(i).b);
				}
				uniform.func_148097_a(posArray);
			}
		}
		{
			ShaderUniform uniform = shader.getUniform("LightSourcesX");
			if(uniform != null) {
				float[] posArray = new float[64];
				for(int i = 0; i < this.lightSources.size(); i++) {
					if(i >= 64) break;
					posArray[i] = (float)(-RenderManager.renderPosX*2 + this.lightSources.get(i).x*2);
				}
				uniform.func_148097_a(posArray);
			}
		}
		{
			ShaderUniform uniform = shader.getUniform("LightSourcesY");
			if(uniform != null) {
				float[] posArray = new float[64];
				for(int i = 0; i < this.lightSources.size(); i++) {
					if(i >= 64) break;
					posArray[i] = (float)(-RenderManager.renderPosY*2 + this.lightSources.get(i).y*2);
				}
				uniform.func_148097_a(posArray);
			}
		}
		{
			ShaderUniform uniform = shader.getUniform("LightSourcesZ");
			if(uniform != null) {
				float[] posArray = new float[64];
				for(int i = 0; i < this.lightSources.size(); i++) {
					if(i >= 64) break;
					posArray[i] = (float)(-RenderManager.renderPosZ*2 + this.lightSources.get(i).z*2);
				}
				uniform.func_148097_a(posArray);
			}
		}
		{
			ShaderUniform uniform = shader.getUniform("LightRadii");
			if(uniform != null) {
				float[] posArray = new float[64];
				for(int i = 0; i < this.lightSources.size(); i++) {
					if(i >= 64) break;
					posArray[i] = (float)(this.lightSources.get(i).radius * 2);
				}
				uniform.func_148097_a(posArray);
			}
		}
		{
			ShaderUniform uniform = shader.getUniform("LightSources");
			if(uniform != null) {
				int count = this.lightSources.size();
				if(count > 64) {
					count = 64;
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
}
