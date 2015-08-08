package thebetweenlands.event.render;

import java.lang.reflect.Method;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.relauncher.ReflectionHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.shader.Framebuffer;
import net.minecraftforge.client.event.RenderBlockOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderBlockOverlayEvent.OverlayType;
import net.minecraftforge.client.event.RenderHandEvent;
import thebetweenlands.client.render.shader.MainShader;
import thebetweenlands.client.render.shader.ShaderHelper;
import thebetweenlands.client.render.shader.effect.WarpEffect;
import thebetweenlands.event.debugging.DebugHandler;

public class ShaderHandler {
	public static final ShaderHandler INSTANCE = new ShaderHandler();
	private Method mERrenderHand;
	private boolean cancelOverlay = false;

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onRenderHand(RenderHandEvent event) {
		if(!ShaderHelper.INSTANCE.canUseShaders()) return;

		//Small fix for hand depth buffer issues because the hand is rendered after the depth buffer has been cleared
		if(this.mERrenderHand == null) {
			try {
				this.mERrenderHand = ReflectionHelper.findMethod(EntityRenderer.class, null, new String[]{"renderHand", "func_78476_b", "b"}, new Class[]{float.class, int.class});
				this.mERrenderHand.setAccessible(true);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		GL11.glPushMatrix();
		this.cancelOverlay = true;
		try {
			this.mERrenderHand.invoke(Minecraft.getMinecraft().entityRenderer, event.partialTicks, event.renderPass);
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.cancelOverlay = false;
		GL11.glPopMatrix();

		if(ShaderHelper.INSTANCE.canUseShaders()) {
			Minecraft mc = Minecraft.getMinecraft();
			if(!mc.gameSettings.fboEnable) {
				mc.gameSettings.fboEnable = true;
				mc.getFramebuffer().createBindFramebuffer(mc.displayWidth, mc.displayHeight);
			}
			ShaderHelper.INSTANCE.enableShader();
			ShaderHelper.INSTANCE.updateShader();
		}
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onPostRender(TickEvent.RenderTickEvent event) {
		if(ShaderHelper.INSTANCE.canUseShaders() && event.phase == Phase.END && ShaderHelper.INSTANCE.canUseShaders()) {
			ShaderHelper.INSTANCE.clearDynLights();
		}
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onRenderBlockOverlay(RenderBlockOverlayEvent event) {
		if(ShaderHelper.INSTANCE.canUseShaders()) {
			if(this.cancelOverlay && event.overlayType == OverlayType.WATER) {
				event.setCanceled(true);
			}
		}
	}

	//Shader textures
	private Framebuffer gasTextureBaseFBO = null;
	private Framebuffer gasTextureFBO = null;
	private WarpEffect gasWarpEffect = null;

	//Shader texture IDs
	public int getGasTextureID() {
		return this.gasTextureFBO.framebufferTexture;
	}

	@SubscribeEvent
	public void renderGui(RenderGameOverlayEvent.Post event) {
		if(DebugHandler.INSTANCE.debugDeferredEffect && ShaderHelper.INSTANCE.canUseShaders()) {
			MainShader shader = ShaderHelper.INSTANCE.getCurrentShader();
			if(shader != null) {

				//Update gas texture
				if(this.gasTextureFBO == null) {
					this.gasTextureFBO = new Framebuffer(128, 128, false);
					this.gasTextureBaseFBO = new Framebuffer(128, 128, false);

					this.gasWarpEffect = new WarpEffect().setTimeScale(0.00004F).setScale(40.0F).setMultiplier(3.55F);
					this.gasWarpEffect.init();
				} else {
					float warpX = (float)(Math.sin(System.nanoTime() / 20000000000.0D) / 80.0F) + (float)(Math.sin(System.nanoTime() / 5600000000.0D) / 15000.0F) - (float)(Math.cos(System.nanoTime() / 6800000000.0D) / 500.0F);
					float warpY = (float)(Math.sin(System.nanoTime() / 10000000000.0D) / 60.0F) - (float)(Math.cos(System.nanoTime() / 800000000.0D) / 6000.0F) + (float)(Math.cos(System.nanoTime() / 2000000000.0D) / 1000.0F);
					this.gasWarpEffect.setOffset((float)Math.sin(System.nanoTime() / 10000000000.0D) / 80.0F, (float)Math.sin(System.nanoTime() / 10000000000.0D) / 70.0F)
					.setWarpDir(warpX, warpY);
					
					this.gasTextureFBO.bindFramebuffer(false);
					GL11.glClearColor(1, 1, 1, 1);
					GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);

					this.gasTextureBaseFBO.bindFramebuffer(false);
					GL11.glClearColor(1, 1, 1, 1);
					GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);

					this.gasWarpEffect.apply(this.gasTextureBaseFBO.framebufferTexture, this.gasTextureFBO, null, Minecraft.getMinecraft().getFramebuffer(), 128.0F * 20.0F, 128.0F * 20.0F);
				}
				
			}
		}
	}
}