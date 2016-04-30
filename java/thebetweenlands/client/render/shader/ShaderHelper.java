package thebetweenlands.client.render.shader;

import org.lwjgl.opengl.ContextCapabilities;
import org.lwjgl.opengl.GLContext;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.client.shader.ShaderGroup;
import net.minecraft.client.shader.ShaderLinkHelper;
import net.minecraft.client.util.JsonException;
import net.minecraft.util.ResourceLocation;
import thebetweenlands.client.render.shader.base.CShaderGroup;
import thebetweenlands.entities.properties.BLEntityPropertiesRegistry;
import thebetweenlands.entities.properties.list.EntityPropertiesPortal;
import thebetweenlands.utils.confighandler.ConfigHandler;
import thebetweenlands.world.WorldProviderBetweenlands;

public class ShaderHelper implements IResourceManagerReloadListener {
	public static final ShaderHelper INSTANCE = new ShaderHelper();

	private MainShader currentShader;
	private boolean failedLoading = false;
	private ShaderGroup currentShaderGroup;
	private boolean checked = false;
	private boolean shadersSupported = false;
	private boolean needsReload = false;

	public boolean canUseShaders() {
		return OpenGlHelper.isFramebufferEnabled() && this.isShaderSupported() && ConfigHandler.USE_SHADER;
	}

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
		if(mc.getFramebuffer() != null && mc.getResourceManager() != null && mc.getTextureManager() != null) {
			if(this.isRequired()) {
				if(this.currentShader == null || mc.entityRenderer.theShaderGroup == null || mc.entityRenderer.theShaderGroup != this.currentShaderGroup || this.needsShaderReload()) {
					MainShader shaderWrapper = this.currentShader;
					if(shaderWrapper != null && this.needsShaderReload()) {
						try {
							shaderWrapper.getShaderGroup().deleteShaderGroup();
							shaderWrapper.deleteBuffers();
						} catch(Exception ex) {
							System.err.println("Failed deleting shader group!");
							ex.printStackTrace();
						}
					}
					if(shaderWrapper == null || this.needsShaderReload()) {
						shaderWrapper = new MainShader(
								mc.getTextureManager(),
								mc.getResourceManager(), mc.getFramebuffer(),
								new ResourceLocation("thebetweenlands:shaders/mc/config/blmain.json"),
								new ResourceLocation("thebetweenlands:shaders/mc/program/"),
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
						if(this.needsShaderReload()) {
							this.needsReload = false;
						}
					} catch (JsonException e) {
						this.failedLoading = true;
						System.err.println("Failed loading shader files!");
						e.printStackTrace();
					}
				}
			} else if(mc.entityRenderer.theShaderGroup instanceof CShaderGroup) {
				mc.entityRenderer.theShaderGroup = null;
			}
		}
	}

	private boolean isRequired() {
		Minecraft mc = Minecraft.getMinecraft();
		boolean inPortal = false;
		if(mc.thePlayer != null){
			EntityPropertiesPortal props = BLEntityPropertiesRegistry.HANDLER.getProperties(mc.thePlayer, EntityPropertiesPortal.class);
			inPortal = props.inPortal;
		}
		return inPortal || (mc.theWorld != null && mc.theWorld.provider instanceof WorldProviderBetweenlands && mc.thePlayer.dimension == ConfigHandler.DIMENSION_ID);
	}

	private boolean needsShaderReload() {
		return this.needsReload;
	}

	public void scheduleShaderReload() {
		this.needsReload = true;
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

	@Override
	public void onResourceManagerReload(IResourceManager resourceManager) {
		this.scheduleShaderReload();
	}
}
