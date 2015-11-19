package thebetweenlands.event.render;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderHandEvent;
import thebetweenlands.client.render.shader.ShaderHelper;

public class ShaderHandler {
	public static final ShaderHandler INSTANCE = new ShaderHandler();

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onRenderHand(RenderHandEvent event) {
		if(!ShaderHelper.INSTANCE.canUseShaders()) return;

		GL11.glPushMatrix();
		//Small fix for hand depth buffer issues because the hand is rendered after the depth buffer has been cleared
		GL11.glColorMask(false, false, false, false);
		OverlayHandler.INSTANCE.renderHand(event.partialTicks, event.renderPass, false);
		GL11.glColorMask(true, true, true, true);
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
}