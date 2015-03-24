package thebetweenlands.event.debugging;

import java.lang.reflect.Method;
import java.nio.FloatBuffer;

import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3d;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.shader.ShaderUniform;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderHandEvent;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import thebetweenlands.TheBetweenlands;
import thebetweenlands.client.render.shader.CShader;
import thebetweenlands.client.render.shader.CShaderInt;
import thebetweenlands.client.render.shader.ShaderHelper;
import thebetweenlands.manager.DecayManager;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent.KeyInputEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class DebugHandler {
	public static final DebugHandler INSTANCE = new DebugHandler();

	/////// DEBUG ///////
	private boolean fullBright = false;
	private boolean fastFlight = false;
	public boolean denseFog = false;
	public boolean useShader = false;
	private float lightTable[];
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onKeyInput(KeyInputEvent event) {
		if(!TheBetweenlands.DEBUG || Minecraft.getMinecraft().theWorld == null) return;
		if(Keyboard.isKeyDown(Keyboard.KEY_F)){
			this.fullBright = !this.fullBright;
			if(this.fullBright) {
				if(this.lightTable == null) {
					this.lightTable = new float[Minecraft.getMinecraft().theWorld.provider.lightBrightnessTable.length];
				}
				for(int i = 0; i < Minecraft.getMinecraft().theWorld.provider.lightBrightnessTable.length; i++) {
					this.lightTable[i] = Minecraft.getMinecraft().theWorld.provider.lightBrightnessTable[i];
				}
				for(int i = 0; i < Minecraft.getMinecraft().theWorld.provider.lightBrightnessTable.length; i++) {
					Minecraft.getMinecraft().theWorld.provider.lightBrightnessTable[i] = 1.0f;
				}
			} else {
				for(int i = 0; i < Minecraft.getMinecraft().theWorld.provider.lightBrightnessTable.length; i++) {
					Minecraft.getMinecraft().theWorld.provider.lightBrightnessTable[i] = this.lightTable[i];
				}
			}
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_C)) {
			this.fastFlight = !this.fastFlight;
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_R)) {
			this.denseFog = !this.denseFog;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_G)) {
			DecayManager.resetDecay(Minecraft.getMinecraft().thePlayer);
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_Z)) {
			try {
				Minecraft mc = Minecraft.getMinecraft();
				if(mc.entityRenderer.theShaderGroup == null) {
					CShader shaderWrapper = new CShader(
							mc.getTextureManager(),
							mc.getResourceManager(), mc.getFramebuffer(),
							new ResourceLocation("thebetweenlands:shaders/post/lighting.json"),
							new ResourceLocation("thebetweenlands:shaders/program/"),
							new ResourceLocation("thebetweenlands:textures/")
							) {
						@Override
						public void updateShader(CShaderInt shader) {
							{
								ShaderUniform uniform = shader.getUniform("InverseMatrix");
								if(uniform != null) {
									uniform.func_148088_a(DebugHandler.INSTANCE.inverseMatrix);
								}
							}
							{
								ShaderUniform uniform = shader.getUniform("MVP");
								if(uniform != null) {
									uniform.func_148088_a(DebugHandler.INSTANCE.MVP);
								}
							}
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
											(float)(-RenderManager.renderPosX*2),
											(float)(-RenderManager.renderPosY*2),
											(float)(-RenderManager.renderPosZ*2));
								}
							}
							{
								ShaderUniform uniform = shader.getUniform("LightSourcesX");
								if(uniform != null) {
									float[] posArray = new float[64];
									for(int i = 0; i < ShaderHelper.INSTANCE.lightSources.size(); i++) {
										if(i >= 64) break;
										posArray[i] = (float)(-RenderManager.renderPosX*2 + ShaderHelper.INSTANCE.lightSources.get(i).x*2);
									}
									uniform.func_148097_a(posArray);
								}
							}
							{
								ShaderUniform uniform = shader.getUniform("LightSourcesY");
								if(uniform != null) {
									float[] posArray = new float[64];
									for(int i = 0; i < ShaderHelper.INSTANCE.lightSources.size(); i++) {
										if(i >= 64) break;
										posArray[i] = (float)(-RenderManager.renderPosY*2 + ShaderHelper.INSTANCE.lightSources.get(i).y*2);
									}
									uniform.func_148097_a(posArray);
								}
							}
							{
								ShaderUniform uniform = shader.getUniform("LightSourcesZ");
								if(uniform != null) {
									float[] posArray = new float[64];
									for(int i = 0; i < ShaderHelper.INSTANCE.lightSources.size(); i++) {
										if(i >= 64) break;
										posArray[i] = (float)(-RenderManager.renderPosZ*2 + ShaderHelper.INSTANCE.lightSources.get(i).z*2);
									}
									uniform.func_148097_a(posArray);
								}
							}
							{
								ShaderUniform uniform = shader.getUniform("LightSources");
								if(uniform != null) {
									int count = ShaderHelper.INSTANCE.lightSources.size();
									if(count > 64) {
										count = 64;
									}
									uniform.func_148090_a(count);
								}
							}
						}
					}.updateSampler("DepthSampler", ShaderHelper.INSTANCE.getDepthBuffer());
					this.currentShader = shaderWrapper;
					mc.entityRenderer.theShaderGroup = shaderWrapper.getShaderGroup();
					mc.entityRenderer.theShaderGroup.createBindFramebuffers(mc.displayWidth, mc.displayHeight);
					this.useShader = true;
				} else {
					mc.entityRenderer.deactivateShader();
					this.useShader = false;
				}
			} catch(Exception ex) {
				this.useShader = false;
				ex.printStackTrace();
			}
		}
	}
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onTick(TickEvent event) {
		if(!TheBetweenlands.DEBUG || Minecraft.getMinecraft().thePlayer == null) return;
		if(this.fastFlight) {
			Minecraft.getMinecraft().thePlayer.capabilities.setFlySpeed(1.0f);
		} else {
			Minecraft.getMinecraft().thePlayer.capabilities.setFlySpeed(0.1f);
		}
	}
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onRenderOverlay(RenderGameOverlayEvent.Text event) {
		if (TheBetweenlands.DEBUG) {
			Minecraft.getMinecraft().fontRenderer.drawString("Debug", 2, 2, 0xFFFFFFFF);
			Minecraft.getMinecraft().fontRenderer.drawString("Decay: " + DecayManager.getDecayLevel(Minecraft.getMinecraft().thePlayer), 2, 10, 0xFFFFFFFF);
			Minecraft.getMinecraft().fontRenderer.drawString("Corruption: " + DecayManager.getCorruptionLevel(Minecraft.getMinecraft().thePlayer), 2, 18, 0xFFFFFFFF);
		}
		
		ShaderHelper.INSTANCE.lightSources.clear();
	}

	private Method renderHandMethod = null;
	private CShader currentShader = null;
	public Matrix4f inverseMatrix;
	public Matrix4f MVP;
	public org.lwjgl.util.vector.Matrix4f modelView;
	private FloatBuffer modelviewBuffer = GLAllocation.createDirectFloatBuffer(16);
	private FloatBuffer projectionBuffer = GLAllocation.createDirectFloatBuffer(16);

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void renderHand(RenderHandEvent event) {
		if(this.useShader) {
			GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX, modelviewBuffer);
			GL11.glGetFloat(GL11.GL_PROJECTION_MATRIX, projectionBuffer);

			/*Matrix4f modelviewMatrix = new Matrix4f();
			for(int x = 0; x < 4; x++) {
				for(int y = 0; y < 4; y++) {
					modelviewMatrix.setElement(x, y, modelviewBuffer.get(x + y * 4));
				}
			}
			Matrix4f projectionMatrix = new Matrix4f();
			for(int x = 0; x < 4; x++) {
				for(int y = 0; y < 4; y++) {
					projectionMatrix.setElement(x, y, projectionBuffer.get(x + y * 4));
				}
			}

			modelviewMatrix.mul(projectionMatrix);
			projectionMatrix.invert();
			
			this.inverseMatrix = projectionMatrix;*/

			org.lwjgl.util.vector.Matrix4f modelviewMatrix = (org.lwjgl.util.vector.Matrix4f) new org.lwjgl.util.vector.Matrix4f().load(modelviewBuffer.asReadOnlyBuffer());
			org.lwjgl.util.vector.Matrix4f invModelviewMatrix = (org.lwjgl.util.vector.Matrix4f) new org.lwjgl.util.vector.Matrix4f().load(modelviewBuffer.asReadOnlyBuffer()).invert();
			this.modelView = invModelviewMatrix;
			org.lwjgl.util.vector.Matrix4f projectionMatrix = (org.lwjgl.util.vector.Matrix4f) new org.lwjgl.util.vector.Matrix4f().load(projectionBuffer.asReadOnlyBuffer());
			org.lwjgl.util.vector.Matrix4f MVP = new org.lwjgl.util.vector.Matrix4f();
			org.lwjgl.util.vector.Matrix4f.mul(projectionMatrix, modelviewMatrix, MVP);
			
			Matrix4f vecMathMVP = new Matrix4f();
			vecMathMVP.m00 = MVP.m00;
			vecMathMVP.m01 = MVP.m01;
			vecMathMVP.m02 = MVP.m02;
			vecMathMVP.m03 = MVP.m03;
			vecMathMVP.m10 = MVP.m10;
			vecMathMVP.m11 = MVP.m11;
			vecMathMVP.m12 = MVP.m12;
			vecMathMVP.m13 = MVP.m13;
			vecMathMVP.m20 = MVP.m20;
			vecMathMVP.m21 = MVP.m21;
			vecMathMVP.m22 = MVP.m22;
			vecMathMVP.m23 = MVP.m23;
			vecMathMVP.m30 = MVP.m30;
			vecMathMVP.m31 = MVP.m31;
			vecMathMVP.m32 = MVP.m32;
			vecMathMVP.m33 = MVP.m33;
			
			this.MVP = vecMathMVP;
			
			MVP.invert();
			
			Matrix4f vecMathInvMVP = new Matrix4f();
			vecMathInvMVP.m00 = MVP.m00;
			vecMathInvMVP.m01 = MVP.m01;
			vecMathInvMVP.m02 = MVP.m02;
			vecMathInvMVP.m03 = MVP.m03;
			vecMathInvMVP.m10 = MVP.m10;
			vecMathInvMVP.m11 = MVP.m11;
			vecMathInvMVP.m12 = MVP.m12;
			vecMathInvMVP.m13 = MVP.m13;
			vecMathInvMVP.m20 = MVP.m20;
			vecMathInvMVP.m21 = MVP.m21;
			vecMathInvMVP.m22 = MVP.m22;
			vecMathInvMVP.m23 = MVP.m23;
			vecMathInvMVP.m30 = MVP.m30;
			vecMathInvMVP.m31 = MVP.m31;
			vecMathInvMVP.m32 = MVP.m32;
			vecMathInvMVP.m33 = MVP.m33;
			
			this.inverseMatrix = vecMathInvMVP;
			
			//Render hand to depth buffer to prevent bugs
			if(this.renderHandMethod == null) {
				try {
					this.renderHandMethod = EntityRenderer.class.getDeclaredMethod("renderHand", float.class, int.class);
					this.renderHandMethod.setAccessible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			try {
				this.renderHandMethod.invoke(Minecraft.getMinecraft().entityRenderer, event.partialTicks, event.renderPass);
			} catch (Exception e) {
				e.printStackTrace();
			}

			ShaderHelper.INSTANCE.updateDepthBuffer(Minecraft.getMinecraft().getFramebuffer());

			if(this.currentShader != null) {
				this.currentShader.updateSampler("DepthSampler", ShaderHelper.INSTANCE.getDepthBuffer());
			}
		}
	}
}
