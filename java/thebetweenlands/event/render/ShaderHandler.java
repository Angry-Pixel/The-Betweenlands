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
import net.minecraftforge.client.event.RenderHandEvent;
import thebetweenlands.client.render.shader.ShaderHelper;
import thebetweenlands.utils.confighandler.ConfigHandler;

public class ShaderHandler {
	public static final ShaderHandler INSTANCE = new ShaderHandler();

	private static final Method f_setupCameraTransform = ReflectionHelper.findMethod(EntityRenderer.class, null, new String[]{"setupCameraTransform", "func_78479_a", "a"}, float.class, int.class);

	static {
		f_setupCameraTransform.setAccessible(true);
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onRenderHand(RenderHandEvent event) {
		if(!(ConfigHandler.USE_SHADER && ShaderHelper.INSTANCE.isShaderSupported())) return;

		GL11.glPushMatrix();
		//Small fix for hand depth buffer issues because the hand is rendered after the depth buffer has been cleared
		//Also makes the overlays render to the depth buffer, preventing the world glowing through the overlays
		GL11.glColorMask(false, false, false, false);
		boolean prevCancel = event.isCanceled();
		OverlayHandler.INSTANCE.onRenderHand(event);
		event.setCanceled(prevCancel);
		GL11.glColorMask(true, true, true, true);
		GL11.glPopMatrix();

		//Restore MVP matrix
		try {
			f_setupCameraTransform.invoke(Minecraft.getMinecraft().entityRenderer, event.partialTicks, event.renderPass);
		} catch(Exception ex) {
			ex.printStackTrace();
		}

		Minecraft mc = Minecraft.getMinecraft();
		if(!mc.gameSettings.fboEnable) {
			mc.gameSettings.fboEnable = true;
			mc.getFramebuffer().createBindFramebuffer(mc.displayWidth, mc.displayHeight);
		}
		ShaderHelper.INSTANCE.enableShader();
		ShaderHelper.INSTANCE.updateShader();
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onPostRender(TickEvent.RenderTickEvent event) {
		if(ShaderHelper.INSTANCE.canUseShaders() && event.phase == Phase.END) {
			ShaderHelper.INSTANCE.clearDynLights();
		}
	}
}