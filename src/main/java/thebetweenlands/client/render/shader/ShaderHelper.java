package thebetweenlands.client.render.shader;

import org.lwjgl.opengl.ContextCapabilities;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLContext;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.util.math.MathHelper;
import thebetweenlands.client.render.shader.postprocessing.WorldShader;
import thebetweenlands.util.config.ConfigHandler;

public class ShaderHelper {
	private ShaderHelper() { }

	public static final ShaderHelper INSTANCE = new ShaderHelper();

	private boolean checked = false;
	private boolean shadersSupported = false;

	private WorldShader worldShader = null;
	private ResizableFramebuffer blitBuffer = null;

	public WorldShader getWorldShader() {
		return this.worldShader;
	}

	/**
	 * Returns whether shaders are supported and enabled
	 * @return
	 */
	public boolean canUseShaders() {
		//TODO: Remove once complete
		if(true)
			return false;
		return OpenGlHelper.isFramebufferEnabled() && this.isShaderSupported() && ConfigHandler.useShader;
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
	 * Updates and initializes the main shader if necessary
	 */
	public void updateShaders(float partialTicks) {
		if(this.isRequired() && this.canUseShaders()) {
			if(this.worldShader == null) {
				this.worldShader = new WorldShader();
				this.worldShader.init();
				this.blitBuffer = new ResizableFramebuffer(false);
			}

			this.worldShader.updateDepthBuffer();
			this.worldShader.updateMatrices();
			this.worldShader.updateTextures(partialTicks);
		}
	}

	/**
	 * Renders the main shader to the screen
	 */
	public void renderShaders() {
		if(this.worldShader != null && this.isRequired() && this.canUseShaders()) {
			/*if(ShaderHelper2.INSTANCE.getWorldShader() != null) {
				for(int x = -10; x < 10; x++) {
					for(int z = -10; z < 10; z++) {
						double posX = Minecraft.getMinecraft().getRenderManager().viewerPosX + x*4;
						double posY = 4;//Minecraft.getMinecraft().getRenderManager().viewerPosY;
						double posZ = Minecraft.getMinecraft().getRenderManager().viewerPosZ + z*4;
						ShaderHelper2.INSTANCE.getWorldShader().addLight(new LightSource(posX, posY, posZ, 
								2f,
								5.0f / 255.0f * 13.0F, 
								40.0f / 255.0f * 13.0F, 
								60.0f / 255.0f * 13.0F));
					}
				}
			}*/

			Framebuffer mainFramebuffer = Minecraft.getMinecraft().getFramebuffer();
			Framebuffer blitFramebuffer = this.blitBuffer.getFramebuffer(mainFramebuffer.framebufferWidth, mainFramebuffer.framebufferHeight);

			int renderPasses = MathHelper.floor_double(this.worldShader.getLightSourcesAmount() / WorldShader.MAX_LIGHT_SOURCES_PER_PASS) + 1;
			renderPasses = 1; //Multiple render passes are currently not recommended

			Minecraft.getMinecraft().entityRenderer.setupOverlayRendering();

			blitFramebuffer.framebufferClear();

			for(int i = 0; i < renderPasses; i++) {
				//Renders the shader to the blitBuffer
				this.worldShader.setBackgroundColor(1, 1, 1, 1);
				this.worldShader.setLightIndex(i);
				this.worldShader.create(blitFramebuffer)
				.setSource(mainFramebuffer.framebufferTexture)
				.setRestoreGlState(true)
				.setMirrorY(false)
				.setClearDepth(true)
				.setClearColor(false)
				.render();

				//Ping-pong FBOs
				Framebuffer previous = blitFramebuffer;
				blitFramebuffer = mainFramebuffer;
				mainFramebuffer = previous;
			}

			Framebuffer targetFramebuffer = Minecraft.getMinecraft().getFramebuffer();

			//Render last pass to the target framebuffer if necessary
			if(mainFramebuffer != targetFramebuffer) {
				Minecraft.getMinecraft().entityRenderer.setupOverlayRendering();

				targetFramebuffer.bindFramebuffer(false);

				float renderWidth = (float)mainFramebuffer.framebufferTextureWidth;
				float renderHeight = (float)mainFramebuffer.framebufferTextureHeight;
				GlStateManager.viewport(0, 0, (int)renderWidth, (int)renderHeight);

				GlStateManager.color(1, 1, 1, 1);
				GlStateManager.enableTexture2D();
				mainFramebuffer.bindFramebufferTexture();
				GlStateManager.depthMask(false);
				GlStateManager.colorMask(true, true, true, true);
				Tessellator tessellator = Tessellator.getInstance();
				VertexBuffer vb = tessellator.getBuffer();
				vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
				vb.pos(0.0D, (double)mainFramebuffer.framebufferTextureHeight / 2.0D, 500.0D).tex(0, 0).endVertex();
				vb.pos((double)mainFramebuffer.framebufferTextureWidth / 2.0D, (double)mainFramebuffer.framebufferTextureHeight / 2.0D, 500.0D).tex(1, 0).endVertex();
				vb.pos((double)mainFramebuffer.framebufferTextureWidth / 2.0D, 0.0D, 500.0D).tex(1, 1).endVertex();
				vb.pos(0.0D, 0.0D, 500.0D).tex(0, 1).endVertex();
				tessellator.draw();
				GlStateManager.depthMask(true);
				GlStateManager.colorMask(true, true, true, true);
			}
		}
	}

	/**
	 * Deletes the main shader
	 */
	public void deleteShaders() {
		if(this.worldShader != null)
			this.worldShader.delete();
		if(this.blitBuffer != null)
			this.blitBuffer.delete();
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
}
