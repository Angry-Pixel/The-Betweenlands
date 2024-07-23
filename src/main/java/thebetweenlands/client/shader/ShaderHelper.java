package thebetweenlands.client.shader;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.util.Mth;
import org.lwjgl.opengl.*;

import net.minecraft.client.Minecraft;
import thebetweenlands.client.shader.postprocessing.Tonemapper;
import thebetweenlands.client.shader.postprocessing.WorldShader;
import thebetweenlands.common.config.BetweenlandsConfig;
import thebetweenlands.common.registries.DimensionRegistries;
import thebetweenlands.util.FramebufferStack;

import javax.annotation.Nullable;

public class ShaderHelper implements ResourceManagerReloadListener {

	public static final ShaderHelper INSTANCE = new ShaderHelper();

	private boolean checked = false;
	private boolean shadersSupported = false;
	private boolean gl30Supported = false;
	private boolean arbFloatBufferSupported = false;

	@Nullable
	private Exception shaderError = null;
	@Nullable
	private WorldShader worldShader = null;
	@Nullable
	private Tonemapper toneMappingShader = null;
	@Nullable
	private ResizableFramebuffer blitBuffer = null;

	private boolean shadersUpdated = false;
	private boolean required = false;

	/**
	 * The minumum amount of required texture units for the shaders to work properly
	 */
	public static final int MIN_REQUIRED_TEX_UNITS = 6;

	@Nullable
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
			if(BetweenlandsConfig.dimensionShaderOnly) {
				canUseInWorld = Minecraft.getInstance().level != null && Minecraft.getInstance().level.dimension() == DimensionRegistries.DIMENSION_KEY;
			}
			return this.shaderError == null && BetweenlandsConfig.useShader && canUseInWorld;
		} else {
			//Shaders not supported, disable in config
			BetweenlandsConfig.useShader = false;
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
		return this.isGL30Supported() && (this.isARBFloatBufferSupported() || this.isFloatBufferSupported());
	}

	/**
	 * Returns whether GL 3.0 is supported
	 * @return
	 */
	public boolean isGL30Supported() {
		this.checkCapabilities();
		return this.gl30Supported;
	}

	/**
	 * Returns whether float buffers are supported
	 * @return
	 */
	public boolean isFloatBufferSupported() {
		return this.isGL30Supported();
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
			GLCapabilities contextCapabilities = GL.getCapabilities();
			boolean supported = contextCapabilities.OpenGL21 || (contextCapabilities.GL_ARB_vertex_shader && contextCapabilities.GL_ARB_fragment_shader && contextCapabilities.GL_ARB_shader_objects);
			boolean arbMultitexture = contextCapabilities.GL_ARB_multitexture && !contextCapabilities.OpenGL13;
			int maxTextureUnits = arbMultitexture ? GL11.glGetInteger(ARBMultitexture.GL_MAX_TEXTURE_UNITS_ARB) : (!contextCapabilities.OpenGL20 ? GL11.glGetInteger(GL13.GL_MAX_TEXTURE_UNITS) : GL11.glGetInteger(GL20.GL_MAX_TEXTURE_IMAGE_UNITS));
			this.shadersSupported = maxTextureUnits >= MIN_REQUIRED_TEX_UNITS;
			this.gl30Supported = contextCapabilities.OpenGL30;
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
			RenderTarget mainFramebuffer = Minecraft.getInstance().getMainRenderTarget();

			RenderTarget blitFramebuffer;
			RenderTarget targetFramebuffer1;
			RenderTarget targetFramebuffer2;

			//try(FramebufferStack.State ignored = FramebufferStack.push()) {
				blitFramebuffer = this.blitBuffer.getFramebuffer(mainFramebuffer.viewWidth, mainFramebuffer.viewHeight);
				targetFramebuffer1 = mainFramebuffer;
				targetFramebuffer2 = blitFramebuffer;

				int renderPasses = Mth.floor(this.worldShader.getLightSourcesAmount() / WorldShader.MAX_LIGHT_SOURCES_PER_PASS) + 1;
				renderPasses = 1; //Multiple render passes are currently not recommended

				//TODO verify
				Minecraft.getInstance().levelRenderer.doEntityOutline(); //Minecraft.getInstance().entityRenderer.setupOverlayRendering();

				targetFramebuffer2.clear(Minecraft.ON_OSX);

				for(int i = 0; i < renderPasses; i++) {
					//Renders the shader to the blitBuffer
					this.worldShader.setRenderPass(i);
					this.worldShader.create(targetFramebuffer2)
					.setSource(targetFramebuffer1.getColorTextureId())
					.setRestoreGlState(true)
					.setMirrorY(false)
					.setClearDepth(true)
					.setClearColor(false)
					.render(partialTicks);

					//Ping-pong FBOs
					RenderTarget previous = targetFramebuffer2;
					targetFramebuffer2 = targetFramebuffer1;
					targetFramebuffer1 = previous;
				}

				//Make sure texture unit is set to default
				RenderSystem.activeTexture(33984);
			//}

			//Render last pass to the main framebuffer if necessary
			if(targetFramebuffer1 != mainFramebuffer) {
				float renderWidth = (float)targetFramebuffer1.viewWidth;
				float renderHeight = (float)targetFramebuffer1.viewHeight;

				RenderSystem.viewport(0, 0, (int)renderWidth, (int)renderHeight);
				GL11.glMatrixMode(GL11.GL_PROJECTION);
				GL11.glLoadIdentity();
				GL11.glOrtho(0.0D, renderWidth, renderHeight, 0.0D, 1000.0D, 3000.0D);
				GL11.glMatrixMode(GL11.GL_MODELVIEW);
				GL11.glLoadIdentity();
				GL11.glTranslatef(0.0F, 0.0F, -2000.0F);

				RenderSystem.setShaderColor(1, 1, 1, 1);
				targetFramebuffer1.bindRead();
				RenderSystem.depthMask(false);
				RenderSystem.colorMask(true, true, true, true);
				Tesselator tessellator = Tesselator.getInstance();
				BufferBuilder builder = tessellator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
				builder.addVertex(0.0F, targetFramebuffer1.viewHeight, 500.0F).setUv(0, 0);
				builder.addVertex(targetFramebuffer1.viewWidth, targetFramebuffer1.viewHeight, 500.0F).setUv(1, 0);
				builder.addVertex(targetFramebuffer1.viewWidth, 0.0F, 500.0F).setUv(1, 1);
				builder.addVertex(0.0F, 0.0F, 500.0F).setUv(0, 1);
				BufferUploader.drawWithShader(builder.buildOrThrow());
				RenderSystem.depthMask(true);
				RenderSystem.colorMask(true, true, true, true);
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
				.setSource(mainFramebuffer.getColorTextureId())
				.setBlitFramebuffer(blitFramebuffer)
				.setRestoreGlState(true)
				.setMirrorY(false)
				.setClearDepth(false)
				.setClearColor(false)
				.render(partialTicks);
			}

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
		Minecraft mc = Minecraft.getInstance();
		if(mc.player != null) {
//			IPortalCapability cap = mc.player.getCapability(CapabilityRegistry.CAPABILITY_PORTAL, null);
//			if (cap != null && cap.isInPortal()) {
//				return true;
//			}
		}
		return mc.level != null && mc.level.dimension() == DimensionRegistries.DIMENSION_KEY;
	}

	@Override
	public void onResourceManagerReload(ResourceManager resourceManager) {
		this.deleteShaders();
	}
}
