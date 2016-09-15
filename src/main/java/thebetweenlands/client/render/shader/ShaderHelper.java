package thebetweenlands.client.render.shader;

import java.lang.reflect.Field;

import org.lwjgl.opengl.ContextCapabilities;
import org.lwjgl.opengl.GLContext;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.client.shader.ShaderGroup;
import net.minecraft.client.shader.ShaderLinkHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import thebetweenlands.client.render.shader.base.CShaderGroup;
import thebetweenlands.util.config.ConfigHandler;

public class ShaderHelper implements IResourceManagerReloadListener {
	public static final ShaderHelper INSTANCE = new ShaderHelper();

	//TODO: Mappings!
	private static final Field f_theShaderGroup = ReflectionHelper.findField(EntityRenderer.class, "theShaderGroup");

	private MainShader currentShader;
	private boolean failedLoading = false;
	private ShaderGroup currentShaderGroup;
	private boolean checked = false;
	private boolean shadersSupported = false;
	private boolean needsReload = false;

	/**
	 * Returns whether the world shader is supported and active
	 * @return
	 */
	public boolean isWorldShaderActive() {
		return this.canUseShaders() && this.currentShader != null && Minecraft.getMinecraft().entityRenderer.getShaderGroup() != null
				&& Minecraft.getMinecraft().entityRenderer.getShaderGroup() == this.currentShader.getShaderGroup();
	}

	/**
	 * Returns whether shaders are supported and enabled
	 * @return
	 */
	public boolean canUseShaders() {
		return OpenGlHelper.isFramebufferEnabled() && this.isShaderSupported() && ConfigHandler.useShader;
	}

	/**
	 * Returns whether shaders are supported
	 * @return
	 */
	public boolean isShaderSupported() {
		if(!this.checked){
			this.checked = true;
			ContextCapabilities contextCapabilities = GLContext.getCapabilities();
			boolean supportsGL21 = contextCapabilities.OpenGL21;
			boolean supported = supportsGL21 || (contextCapabilities.GL_ARB_vertex_shader && contextCapabilities.GL_ARB_fragment_shader && contextCapabilities.GL_ARB_shader_objects);
			this.shadersSupported = OpenGlHelper.areShadersSupported() && supported && OpenGlHelper.framebufferSupported;
		}
		return this.shadersSupported;
	}

	/**
	 * Loads and enables the world shader
	 */
	public void enableWorldShader() {
		if(!this.failedLoading && !this.isShaderSupported() && ConfigHandler.useShader) {
			this.failedLoading = true;
			System.err.println("Your system does not support shaders. Please disable shaders in the betweenlands config file.");
			return;
		}
		Minecraft mc = Minecraft.getMinecraft();
		if(mc.getFramebuffer() != null && mc.getResourceManager() != null && mc.getTextureManager() != null) {
			if(this.isRequired()) {
				if(this.currentShader == null || mc.entityRenderer.getShaderGroup() == null || mc.entityRenderer.getShaderGroup() != this.currentShaderGroup || this.needsShaderReload()) {
					MainShader shaderWrapper = this.currentShader;
					if(shaderWrapper != null) {
						shaderWrapper.delete();
					}
					shaderWrapper = new MainShader(
							mc.getTextureManager(),
							mc.getResourceManager(), mc.getFramebuffer(),
							new ResourceLocation("thebetweenlands:shaders/mc/config/blmain.json"),
							new ResourceLocation("thebetweenlands:shaders/mc/program/"),
							new ResourceLocation("thebetweenlands:textures/shader/")
							);
					try {
						if(ShaderLinkHelper.getStaticShaderLinkHelper() == null) {
							ShaderLinkHelper.setNewStaticShaderLinkHelper();
						}
						//TODO: Set EntityRenderer#useShader to true
						this.setShaderGroup(mc.entityRenderer, shaderWrapper.createShaderGroup());
						this.currentShaderGroup = mc.entityRenderer.getShaderGroup();
						this.currentShader = shaderWrapper;
						mc.entityRenderer.getShaderGroup().createBindFramebuffers(mc.displayWidth, mc.displayHeight);
						if(this.needsShaderReload()) {
							this.needsReload = false;
						}
					} catch (Exception e) {
						this.failedLoading = true;
						throw new RuntimeException("Failed loading shader files!", e);
					}
				}
			} else if(mc.entityRenderer.getShaderGroup() instanceof CShaderGroup) {
				this.setShaderGroup(mc.entityRenderer, null);
			}
		}
	}

	private void setShaderGroup(EntityRenderer er, ShaderGroup group) {
		try {
			f_theShaderGroup.set(er, group);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			throw new RuntimeException("Failed to set shader group", e);
		}
	}

	private boolean isRequired() {
		//		Minecraft mc = Minecraft.getMinecraft();
		//		boolean inPortal = false;
		//		if(mc.thePlayer != null){
		//			EntityPropertiesPortal props = BLEntityPropertiesRegistry.HANDLER.getProperties(mc.thePlayer, EntityPropertiesPortal.class);
		//			inPortal = props.inPortal;
		//		}
		//		return inPortal || (mc.theWorld != null && mc.theWorld.provider instanceof WorldProviderBetweenlands && mc.thePlayer.dimension == ConfigHandler.DIMENSION_ID);
		//TODO: Requires dimension and portal
		return true;
	}

	private boolean needsShaderReload() {
		return this.needsReload;
	}

	public void scheduleShaderReload() {
		this.needsReload = true;
	}

	public void updateShader() {
		if(!this.isShaderSupported() || this.failedLoading || !ConfigHandler.useShader) {
			return;
		}
		if(this.currentShader != null) {
			this.currentShader.updateMatrices();
			this.currentShader.updateBuffers(Minecraft.getMinecraft().getFramebuffer());
		}
	}

	public void clearDynLights() {
		if(ConfigHandler.useShader && !this.failedLoading && this.isShaderSupported()) {
			if(this.currentShader != null) {
				this.currentShader.clearLights();
			}
		}
	}

	public void addDynLight(LightSource light) {
		if(ConfigHandler.useShader && !this.failedLoading && this.isShaderSupported()) {
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
