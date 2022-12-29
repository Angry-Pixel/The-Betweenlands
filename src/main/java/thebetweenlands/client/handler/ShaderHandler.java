package thebetweenlands.client.handler;

import java.nio.IntBuffer;

import org.lwjgl.opengl.ARBTextureFloat;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.shader.Framebuffer;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.event.RenderBlockOverlayEvent;
import net.minecraftforge.client.event.RenderBlockOverlayEvent.OverlayType;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import thebetweenlands.api.event.PreRenderShadersEvent;
import thebetweenlands.client.render.shader.ShaderHelper;
import thebetweenlands.common.config.BetweenlandsConfig;

public class ShaderHandler {
	public static final ShaderHandler INSTANCE = new ShaderHandler();

	private boolean cancelTransparentOverlays = false;

	@SubscribeEvent
	public void onPreRenderShaders(PreRenderShadersEvent event) {
		if(ShaderHelper.INSTANCE.canUseShaders()) {
			ShaderHelper.INSTANCE.renderShaders(event.getPartialTicks());
		}
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onPreRenderWorld(TickEvent.RenderTickEvent event) {
		if(event.phase == Phase.START) {
			Minecraft mc = Minecraft.getMinecraft();

			boolean canUseInWorld = true;
			if(BetweenlandsConfig.RENDERING.dimensionShaderOnly) {
				canUseInWorld = mc.world != null && mc.world.provider.getDimension() == BetweenlandsConfig.WORLD_AND_DIMENSION.dimensionId;
			}
			
			if(ShaderHelper.INSTANCE.isShaderSupported() && BetweenlandsConfig.RENDERING.useShader && canUseInWorld) {
				ShaderHelper.INSTANCE.initShaders();
				
				Framebuffer framebuffer = mc.getFramebuffer();

				if(!mc.gameSettings.fboEnable) {
					//Enable FBOs
					mc.gameSettings.fboEnable = true;
					framebuffer.createBindFramebuffer(mc.displayWidth, mc.displayHeight);
				}

				//Use float buffer for HDR
				if(ShaderHelper.INSTANCE.isHDRActive()) {
					int colorFormat = ShaderHelper.INSTANCE.isFloatBufferSupported() ? GL30.GL_RGBA16F : ShaderHelper.INSTANCE.isARBFloatBufferSupported() ? ARBTextureFloat.GL_RGB16F_ARB : -1;
					if(colorFormat != -1) {
						//Use float buffers for HDR
						GlStateManager.bindTexture(framebuffer.framebufferTexture);
						GlStateManager.glTexImage2D(GL11.GL_TEXTURE_2D, 0, colorFormat, framebuffer.framebufferTextureWidth, framebuffer.framebufferTextureHeight, 0, GL11.GL_RGBA, GL11.GL_FLOAT, (IntBuffer)null);
					}
				}
			}
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onRenderWorldLastHighest(RenderWorldLastEvent event) {
		if(BetweenlandsConfig.RENDERING.shaderPriority == 2) {
			this.updateShaders(event);
		}
	}
	
	@SubscribeEvent(priority = EventPriority.NORMAL)
	public void onRenderWorldLastNormal(RenderWorldLastEvent event) {
		if(BetweenlandsConfig.RENDERING.shaderPriority == 1) {
			this.updateShaders(event);
		}
	}
	
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onRenderWorldLastLowest(RenderWorldLastEvent event) {
		if(BetweenlandsConfig.RENDERING.shaderPriority == 0) {
			this.updateShaders(event);
		}
	}
	
	private void updateShaders(RenderWorldLastEvent event) {
		if(ShaderHelper.INSTANCE.canUseShaders()) {
			//Make sure proper matrix is used
			GL11.glMatrixMode(GL11.GL_MODELVIEW);
			GlStateManager.pushMatrix();
			
			//Render hands and item to depth buffer
			GlStateManager.colorMask(false, false, false, false);
			//Don't render water overlays so they don't write to the depth buffer
			this.cancelTransparentOverlays = true;
			Minecraft.getMinecraft().entityRenderer.renderHand(event.getPartialTicks(), MinecraftForgeClient.getRenderPass());
			Minecraft.getMinecraft().entityRenderer.setupCameraTransform(event.getPartialTicks(), MinecraftForgeClient.getRenderPass());
			this.cancelTransparentOverlays = false;
			GlStateManager.colorMask(true, true, true, true);
			
			//Initialize and update shaders and textures
			ShaderHelper.INSTANCE.updateShaders(event.getPartialTicks());
			
			GL11.glMatrixMode(GL11.GL_MODELVIEW);
			GlStateManager.popMatrix();
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onRenderBlockOverlay(RenderBlockOverlayEvent event) {
		if(this.cancelTransparentOverlays && event.getOverlayType() == OverlayType.WATER) {
			event.setCanceled(true);
		}
	}

	@SubscribeEvent
	public void onPostRender(TickEvent.RenderTickEvent event) {
		if(event.phase == Phase.START && ShaderHelper.INSTANCE.getWorldShader() != null) {
			ShaderHelper.INSTANCE.getWorldShader().clearLights();
			ShaderHelper.INSTANCE.getWorldShader().clearGroundFogVolumes();
		}
	}
}