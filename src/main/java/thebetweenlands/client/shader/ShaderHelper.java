package thebetweenlands.client.shader;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.server.packs.resources.ResourceProvider;
import org.lwjgl.opengl.*;
import thebetweenlands.client.shader.core.Rift;
import thebetweenlands.client.shader.postprocessing.Starfield;
import thebetweenlands.client.shader.postprocessing.Tonemapper;
import thebetweenlands.client.shader.postprocessing.WorldShader;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.config.BetweenlandsConfig;
import thebetweenlands.common.registries.DimensionRegistries;

import javax.annotation.Nullable;

public class ShaderHelper implements ResourceManagerReloadListener {

	public static final ShaderHelper INSTANCE = new ShaderHelper();

	public ResourceLocation AURORA_SHADER = TheBetweenlands.prefix("aurora/aurora");
	public ResourceLocation SKYFOG_SHADER = TheBetweenlands.prefix("sky_fog/skyfog");
	public ResourceLocation RIFT_SHADER = TheBetweenlands.prefix("rift/rift");

	private boolean checked = false;
	private boolean shadersSupported = false;
	private boolean gl30Supported = false;
	private boolean arbFloatBufferSupported = false;

	@Nullable
	private Exception shaderError = null;

	// Core shaders
	@Nullable
	private ShaderInstance auroraShader;
	@Nullable
	private ShaderInstance skyFogShader;
	@Nullable
	private Rift riftShader;

	// Post Processing shaders
	@Nullable
	private WorldShader worldShader = null;
	@Nullable
	private Tonemapper toneMappingShader = null;
	@Nullable
	public Starfield menuStarfieldEffect = null;

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

	@Nullable
	public ShaderInstance getAuroraShader() {
		return this.auroraShader;
	}

	@Nullable
	public ShaderInstance getSkyFogShader() {
		return this.skyFogShader;
	}

	@Nullable
	public Rift getRiftShader() {
		return this.riftShader;
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
		//return true;
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

	public boolean test = false;

	/**
	 * initializes the main shader if necessary
	 */
	public void initShaders(ResourceProvider resourceProvider) {
		// Core shaders
		try {
			auroraShader = new ShaderInstance(resourceProvider, AURORA_SHADER, DefaultVertexFormat.POSITION_TEX_COLOR);
		} catch(Exception ex) {
			this.shaderError = ex;
			ex.printStackTrace();
		}

		try {
			skyFogShader = new ShaderInstance(resourceProvider, SKYFOG_SHADER, DefaultVertexFormat.POSITION_TEX_COLOR);
		} catch(Exception ex) {
			this.shaderError = ex;
			ex.printStackTrace();
		}

		try {
			riftShader = new Rift(resourceProvider, RIFT_SHADER, DefaultVertexFormat.POSITION_TEX);
		} catch(Exception ex) {
			this.shaderError = ex;
			ex.printStackTrace();
		}

		// Post-processing shaders
		if(this.canUseShaders()) {
			try {
				if(this.worldShader == null) {
					this.worldShader = new WorldShader(Minecraft.getInstance().getTextureManager(), resourceProvider, Minecraft.getInstance().getMainRenderTarget());
					this.worldShader.resize(Minecraft.getInstance().getWindow().getWidth(), Minecraft.getInstance().getWindow().getHeight());
				}
				//if(this.toneMappingShader == null && this.isHDRActive()) {
				//	this.toneMappingShader = new Tonemapper().init();
				//}
			} catch(Exception ex) {
				this.shaderError = ex;
				ex.printStackTrace();
			}
		}

		try {
			if (this.menuStarfieldEffect == null) {
				this.menuStarfieldEffect = new Starfield(Minecraft.getInstance().getTextureManager(), resourceProvider, Minecraft.getInstance().getMainRenderTarget(), false, 1024, 1024);
				this.menuStarfieldEffect.setTimeScale(0.00000000005F).setZoom(4.8F);
				this.menuStarfieldEffect.resize(Minecraft.getInstance().getWindow().getWidth(), Minecraft.getInstance().getWindow().getHeight());
			}
		} catch(Exception ex) {
			this.shaderError = ex;
			ex.printStackTrace();
		}
	}

	/**
	 * Updates the main shader
	 */
	public void updateShaders(float partialTicks) {
		if(this.canUseShaders()) {
			try {
				if(this.isRequired()) {
					//this.worldShader.updateDepthBuffer();
					//this.worldShader.updateMatrices();
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
		RenderSystem.depthMask(false);
		ShaderHelper.INSTANCE.getWorldShader().uploadUniforms(partialTicks);
		ShaderHelper.INSTANCE.getWorldShader().process(partialTicks);
	}

	/**
	 * Deletes the main shader
	 */
	public void deleteShaders() {
		this.shaderError = null;

		//if(this.worldShader != null)
		//	this.worldShader.delete();
		//this.worldShader = null;

		//if(this.blitBuffer != null)
		//	this.blitBuffer.delete();
		//this.blitBuffer = null;

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
		return true; // Temp until bl dimension sets require
		//return mc.level != null && mc.level.dimension() == DimensionRegistries.DIMENSION_KEY;
	}

	@Override
	public void onResourceManagerReload(ResourceManager resourceManager) {
		this.deleteShaders();
	}
}
