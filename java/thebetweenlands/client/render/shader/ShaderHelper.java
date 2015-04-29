package thebetweenlands.client.render.shader;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.shader.ShaderGroup;
import net.minecraft.client.shader.ShaderLinkHelper;
import net.minecraft.client.util.JsonException;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.ContextCapabilities;
import org.lwjgl.opengl.GLContext;

import thebetweenlands.client.render.shader.impl.LightSource;
import thebetweenlands.client.render.shader.impl.MainShader;
import thebetweenlands.utils.confighandler.ConfigHandler;

public class ShaderHelper {
	public static final ShaderHelper INSTANCE = new ShaderHelper();

	private MainShader currentShader;
	private boolean failedLoading = false;
	private ShaderGroup currentShaderGroup;
	private boolean checked = false;
	private boolean shadersSupported = false;
	
	public boolean isShaderSupported() {
		if(!this.checked){
			this.checked = true;
			ContextCapabilities contextCapabilities = GLContext.getCapabilities();
			boolean supportsGL21 = contextCapabilities.OpenGL21;
	        boolean supported = supportsGL21 || (contextCapabilities.GL_ARB_vertex_shader && contextCapabilities.GL_ARB_fragment_shader && contextCapabilities.GL_ARB_shader_objects);
			this.shadersSupported = OpenGlHelper.func_153193_b() && supported && OpenGlHelper.framebufferSupported;
		}
		return this.shadersSupported;
	}

	public void enableShader() {
		if(!this.isShaderSupported() || this.failedLoading || !ConfigHandler.USE_SHADER) {
			if(!this.isShaderSupported()) {
				this.failedLoading = true;
				System.err.println("Your system does not support shaders. Please disable shaders in the betweenlands config file.");
			}
			return;
		}
		Minecraft mc = Minecraft.getMinecraft();
		if((this.currentShader == null || mc.entityRenderer.theShaderGroup == null || mc.entityRenderer.theShaderGroup != this.currentShaderGroup)
				&& mc.getFramebuffer() != null && mc.getResourceManager() != null && mc.getTextureManager() != null) {
			MainShader shaderWrapper = this.currentShader;
			if(this.currentShader == null) {
				shaderWrapper = new MainShader(
						mc.getTextureManager(),
						mc.getResourceManager(), mc.getFramebuffer(),
						new ResourceLocation("thebetweenlands:shaders/config/blmain.json"),
						new ResourceLocation("thebetweenlands:shaders/program/"),
						new ResourceLocation("thebetweenlands:textures/shader/")
						);
			}
			try {
				if(ShaderLinkHelper.getStaticShaderLinkHelper() == null) {
					ShaderLinkHelper.setNewStaticShaderLinkHelper();
				}
				mc.entityRenderer.theShaderGroup = shaderWrapper.getShaderGroup();
				this.currentShaderGroup = mc.entityRenderer.theShaderGroup;
				this.currentShader = shaderWrapper;
				mc.entityRenderer.theShaderGroup.createBindFramebuffers(mc.displayWidth, mc.displayHeight);
			} catch (JsonException e) {
				this.failedLoading = true;
				System.err.println("Failed loading shader files!");
				e.printStackTrace();
			}
		}
	}

	public void updateShader() {
		if(!this.isShaderSupported() || this.failedLoading || !ConfigHandler.USE_SHADER) {
			return;
		}
		if(this.currentShader != null) {
			this.currentShader.updateMatrices();
			this.currentShader.updateBuffers(Minecraft.getMinecraft().getFramebuffer());
		}
	}
	
	public void clearDynLights() {
		if(ConfigHandler.USE_SHADER && !this.failedLoading && this.isShaderSupported()) {
			if(this.currentShader != null) {
				this.currentShader.clearLights();
			}
		}
	}
	
	public void addDynLight(LightSource light) {
		if(ConfigHandler.USE_SHADER && !this.failedLoading && this.isShaderSupported()) {
			if(this.currentShader != null) {
				this.currentShader.addLight(light);
			}
		}
	}
	
	public MainShader getCurrentShader() {
		return this.currentShader;
	}
}
