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
import net.minecraftforge.client.event.RenderBlockOverlayEvent;
import net.minecraftforge.client.event.RenderBlockOverlayEvent.OverlayType;
import net.minecraftforge.client.event.RenderHandEvent;
import thebetweenlands.client.render.shader.ShaderHelper;

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
}