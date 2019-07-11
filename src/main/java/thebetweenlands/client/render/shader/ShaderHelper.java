package thebetweenlands.client.render.shader;

import org.lwjgl.opengl.ARBMultitexture;
import org.lwjgl.opengl.ContextCapabilities;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GLContext;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.util.math.MathHelper;
import thebetweenlands.api.capability.IPortalCapability;
import thebetweenlands.client.render.shader.postprocessing.Tonemapper;
import thebetweenlands.client.render.shader.postprocessing.WorldShader;
import thebetweenlands.common.config.BetweenlandsConfig;
import thebetweenlands.common.registries.CapabilityRegistry;
import thebetweenlands.util.FramebufferStack;

public class ShaderHelper implements IResourceManagerReloadListener {
	private ShaderHelper() { }

	public static final ShaderHelper INSTANCE = new ShaderHelper();

	private boolean checked = false;
	private boolean shadersSupported = false;
	private boolean floatBufferSupported = false;
	private boolean arbFloatBufferSupported = false;

	private Exception shaderError = null;
	private WorldShader worldShader = null;
	private Tonemapper toneMappingShader = null;
	private ResizableFramebuffer blitBuffer = null;

	private boolean shadersUpdated = false;
	private boolean required = false;

	/**
	 * The minumum amount of required texture units for the shaders to work properly
	 */
	public static final int MIN_REQUIRED_TEX_UNITS = 6;

	public WorldShader getWorldShader() {
		return this.worldShader;
	}

	/**
	 * Returns whether shaders are supported and enabled
	 * @return
	 */
	public boolean canUseShaders() {
		if(this.isShaderSupported()) {
			boolean canUseInWorld = true;
			if(BetweenlandsConfig.RENDERING.dimensionShaderOnly) {
				canUseInWorld = Minecraft.getMinecraft().world != null && Minecraft.getMinecraft().world.provider.getDimension() == BetweenlandsConfig.WORLD_AND_DIMENSION.dimensionId;
			}
			return this.shaderError == null && OpenGlHelper.isFramebufferEnabled() && BetweenlandsConfig.RENDERING.useShader && canUseInWorld;
		} else {
			//Shaders not supported, disable in config
			BetweenlandsConfig.RENDERING.useShader = false;
			return false;
		}
	}

	/**
	 * Returns whether the world shader is active
	 * @return
	 */
	public boolean isWorldShaderActive() {
		return this.canUseShaders() && this.worldShader != null;
	}

	/**
	 * Returns whether shaders are supported
	 * @return
	 */
	public boolean isShaderSupported() {
		this.checkCapabilities();
		return this.shadersSupported;
	}

	/**
	 * Returns whether HDR is active
	 * @return
	 */
	public boolean isHDRActive() {
		//return this.isHDRSupported();
		return false;
	}

	/**
	 * Returns whether HDR is supported
	 * @return
	 */
	public boolean isHDRSupported() {
		return this.floatBufferSupported && this.arbFloatBufferSupported;
	}

	/**
	 * Returns whether float buffers are supported
	 * @return
	 */
	public boolean isFloatBufferSupported() {
		this.checkCapabilities();
		return this.floatBufferSupported;
	}

	/**
	 * Returns whether ARB float buffers are supported
	 * @return
	 */
	public boolean isARBFloatBufferSupported() {
		this.checkCapabilities();
		return this.arbFloatBufferSupported;
	}

	/**
	 * Updates the capabilities
	 */
	private void checkCapabilities() {
		if(!this.checked){
			this.checked = true;
			ContextCapabilities contextCapabilities = GLContext.getCapabilities();
			boolean supported = contextCapabilities.OpenGL21 || (contextCapabilities.GL_ARB_vertex_shader && contextCapabilities.GL_ARB_fragment_shader && contextCapabilities.GL_ARB_shader_objects);
			boolean arbMultitexture = contextCapabilities.GL_ARB_multitexture && !contextCapabilities.OpenGL13;
			int maxTextureUnits = arbMultitexture ? GL11.glGetInteger(ARBMultitexture.GL_MAX_TEXTURE_UNITS_ARB) : (!contextCapabilities.OpenGL20 ? GL11.glGetInteger(GL13.GL_MAX_TEXTURE_UNITS) : GL11.glGetInteger(GL20.GL_MAX_TEXTURE_IMAGE_UNITS));
			boolean textureUnitsSupported = maxTextureUnits >= MIN_REQUIRED_TEX_UNITS;
			this.shadersSupported = OpenGlHelper.areShadersSupported() && supported && OpenGlHelper.framebufferSupported && textureUnitsSupported;
			this.floatBufferSupported = contextCapabilities.OpenGL30;
			this.arbFloatBufferSupported = contextCapabilities.GL_ARB_texture_float;
		}
	}

	/**
	 * initializes the main shader if necessary
	 */
	public void initShaders() {
		if(this.canUseShaders()) {
			try {
				if(this.worldShader == null) {
					this.worldShader = new WorldShader().init();
				}
				if(this.blitBuffer == null) {
					this.blitBuffer = new ResizableFramebuffer(false);
				}
				if(this.toneMappingShader == null && this.isHDRActive()) {
					this.toneMappingShader = new Tonemapper().init();
				}
			} catch(Exception ex) {
				this.shaderError = ex;
				ex.printStackTrace();
			}
		}
	}

	/**
	 * Updates the main shader
	 */
	public void updateShaders(float partialTicks) {
		if(this.canUseShaders()) {
			try {
				if(this.isRequired()) {
					this.worldShader.updateDepthBuffer();
					this.worldShader.updateMatrices();
					this.worldShader.updateTextures(partialTicks);

					this.shadersUpdated = true;
				}
			} catch(Exception ex) {
				this.shaderError = ex;
				ex.printStackTrace();
			}
		}
	}

	/**
	 * Renders the main shader to the screen
	 */
	public void renderShaders(float partialTicks) {
		if(this.shadersUpdated && this.worldShader != null && this.isRequired() && this.canUseShaders()) {
			Framebuffer mainFramebuffer = Minecraft.getMinecraft().getFramebuffer();

			Framebuffer blitFramebuffer;
			Framebuffer targetFramebuffer1;
			Framebuffer targetFramebuffer2;

			try(FramebufferStack.State state = FramebufferStack.push()) {
				blitFramebuffer = this.blitBuffer.getFramebuffer(mainFramebuffer.framebufferWidth, mainFramebuffer.framebufferHeight);;
				targetFramebuffer1 = mainFramebuffer;
				targetFramebuffer2 = blitFramebuffer;

				int renderPasses = MathHelper.floor(this.worldShader.getLightSourcesAmount() / WorldShader.MAX_LIGHT_SOURCES_PER_PASS) + 1;
				renderPasses = 1; //Multiple render passes are currently not recommended

				Minecraft.getMinecraft().entityRenderer.setupOverlayRendering();

				targetFramebuffer2.framebufferClear();

				GlStateManager.disableAlpha();

				for(int i = 0; i < renderPasses; i++) {
					//Renders the shader to the blitBuffer
					this.worldShader.setRenderPass(i);
					this.worldShader.create(targetFramebuffer2)
					.setSource(targetFramebuffer1.framebufferTexture)
					.setRestoreGlState(true)
					.setMirrorY(false)
					.setClearDepth(true)
					.setClearColor(false)
					.render(partialTicks);

					//Ping-pong FBOs
					Framebuffer previous = targetFramebuffer2;
					targetFramebuffer2 = targetFramebuffer1;
					targetFramebuffer1 = previous;
				}

				//Make sure texture unit is set to default
				GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
			}

			//Render last pass to the main framebuffer if necessary
			if(targetFramebuffer1 != mainFramebuffer) {
				float renderWidth = (float)targetFramebuffer1.framebufferTextureWidth;
				float renderHeight = (float)targetFramebuffer1.framebufferTextureHeight;

				GlStateManager.viewport(0, 0, (int)renderWidth, (int)renderHeight);
				GlStateManager.matrixMode(GL11.GL_PROJECTION);
				GlStateManager.loadIdentity();
				GlStateManager.ortho(0.0D, renderWidth, renderHeight, 0.0D, 1000.0D, 3000.0D);
				GlStateManager.matrixMode(GL11.GL_MODELVIEW);
				GlStateManager.loadIdentity();
				GlStateManager.translate(0.0F, 0.0F, -2000.0F);

				GlStateManager.color(1, 1, 1, 1);
				GlStateManager.enableTexture2D();
				targetFramebuffer1.bindFramebufferTexture();
				GlStateManager.depthMask(false);
				GlStateManager.colorMask(true, true, true, true);
				Tessellator tessellator = Tessellator.getInstance();
				BufferBuilder vb = tessellator.getBuffer();
				vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
				vb.pos(0.0D, (double)targetFramebuffer1.framebufferTextureHeight, 500.0D).tex(0, 0).endVertex();
				vb.pos((double)targetFramebuffer1.framebufferTextureWidth, (double)targetFramebuffer1.framebufferTextureHeight, 500.0D).tex(1, 0).endVertex();
				vb.pos((double)targetFramebuffer1.framebufferTextureWidth, 0.0D, 500.0D).tex(1, 1).endVertex();
				vb.pos(0.0D, 0.0D, 500.0D).tex(0, 1).endVertex();
				tessellator.draw();
				GlStateManager.depthMask(true);
				GlStateManager.colorMask(true, true, true, true);
			}

			//Render additional post processing effects
			this.worldShader.setRenderPass(0);
			this.worldShader.renderPostEffects(partialTicks);

			//Apply Tonemapping if supported
			if(!this.isHDRActive() && this.toneMappingShader != null) {
				this.toneMappingShader.delete();
				this.toneMappingShader = null;
			}
			if(this.toneMappingShader != null) {
				this.toneMappingShader.setExposure(1.0F);
				this.toneMappingShader.setGamma(1.0F);
				this.toneMappingShader.create(mainFramebuffer)
				.setSource(mainFramebuffer.framebufferTexture)
				.setBlitFramebuffer(blitFramebuffer)
				.setRestoreGlState(true)
				.setMirrorY(false)
				.setClearDepth(false)
				.setClearColor(false)
				.render(partialTicks);
			}

			GlStateManager.enableAlpha();

			this.shadersUpdated = false;
			this.required = false;
		}
	}

	/**
	 * Deletes the main shader
	 */
	public void deleteShaders() {
		this.shaderError = null;

		if(this.worldShader != null)
			this.worldShader.delete();
		this.worldShader = null;

		if(this.blitBuffer != null)
			this.blitBuffer.delete();
		this.blitBuffer = null;

		if(this.toneMappingShader != null)
			this.toneMappingShader.delete();
		this.toneMappingShader = null;
	}

	/**
	 * Enables the shaders to be used in the next/current render tick.
	 * The shaders are always rendered in the BL dimension, but if something in another
	 * dimension requires the shaders this must be called every render tick
	 */
	public void require() {
		this.required = true;
	}

	private boolean isRequired() {
		if(this.required) {
			return true;
		}
		Minecraft mc = Minecraft.getMinecraft();
		if(mc.player != null) {
			IPortalCapability cap = mc.player.getCapability(CapabilityRegistry.CAPABILITY_PORTAL, null);
			if (cap != null && cap.isInPortal()) {
				return true;
			}
		}
		return mc.world != null && mc.world.provider.getDimension() == BetweenlandsConfig.WORLD_AND_DIMENSION.dimensionId;
	}

	@Override
	public void onResourceManagerReload(IResourceManager resourceManager) {
		this.deleteShaders();
	}
}
