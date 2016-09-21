package thebetweenlands.client.event.handler;

import java.lang.reflect.Method;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.event.PreRenderShadersEvent;
import thebetweenlands.client.render.shader.ShaderHelper;
import thebetweenlands.util.config.ConfigHandler;

public class ShaderHandler {
	public static final ShaderHandler INSTANCE = new ShaderHandler();

	//TODO: Mappings!
	private static final Method f_setupCameraTransform = ReflectionHelper.findMethod(EntityRenderer.class, null, new String[]{"setupCameraTransform", "func_78479_a", "a"}, float.class, int.class);

	static {
		f_setupCameraTransform.setAccessible(true);
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onPreRenderShaders(PreRenderShadersEvent event) {
		if(ShaderHelper.INSTANCE.canUseShaders())
			ShaderHelper.INSTANCE.renderShaders(event.getPartialTicks());
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onRenderWorldLast(RenderWorldLastEvent event) {
		Minecraft mc = Minecraft.getMinecraft();

		if(ShaderHelper.INSTANCE.isShaderSupported() && ConfigHandler.useShader && !mc.gameSettings.fboEnable) {
			//Enable FBOs
			mc.gameSettings.fboEnable = true;
			mc.getFramebuffer().createBindFramebuffer(mc.displayWidth, mc.displayHeight);
		}

		if(ShaderHelper.INSTANCE.canUseShaders()) {
			//TODO: Render hand/items/overlays with GL11.glColorMask(false, false, false, false)

			//Initialize and update shaders and textures
			ShaderHelper.INSTANCE.updateShaders(event.getPartialTicks());
		}
	}

	//	@SideOnly(Side.CLIENT)
	//	@SubscribeEvent(priority = EventPriority.LOWEST)
	//	//TODO: This used to listen to RenderHandEvent, but now it clears the depth before calling this event. Will have to be replaced with an ASM hook... :(
	//	public void onRenderHand(RenderWorldLastEvent event) {
	//		if(!(ConfigHandler.useShader && ShaderHelper.INSTANCE.isShaderSupported())) return;
	//
	//		//TODO: Remove once shaders are implemented
	//		if(true) return;
	//		
	//		//TODO: Requires overlayes
	//		/*GL11.glPushMatrix();
	//		//Small fix for hand depth buffer issues because the hand is rendered after the depth buffer has been cleared
	//		//Also makes the overlays render to the depth buffer, preventing the world glowing through the overlays
	//		GL11.glColorMask(false, false, false, false);
	//		boolean prevCancel = event.isCanceled();
	//		OverlayHandler.INSTANCE.renderHand(event.partialTicks, event.renderPass, true);
	//		event.setCanceled(prevCancel);
	//		GL11.glColorMask(true, true, true, true);
	//		GL11.glPopMatrix();*/
	//
	//		//Restore MVP matrix
	//		/*try {
	//			f_setupCameraTransform.invoke(Minecraft.getMinecraft().entityRenderer, event.getPartialTicks(), event.getRenderPass());
	//		} catch(Exception ex) {
	//			ex.printStackTrace();
	//		}*/
	//
	//		Minecraft mc = Minecraft.getMinecraft();
	//		if(!mc.gameSettings.fboEnable) {
	//			mc.gameSettings.fboEnable = true;
	//			mc.getFramebuffer().createBindFramebuffer(mc.displayWidth, mc.displayHeight);
	//		}
	//		ShaderHelper.INSTANCE.enableWorldShader();
	//		ShaderHelper.INSTANCE.updateShader();
	//
	//
	//		//ShaderHelper.INSTANCE.getCurrentShader().getDepthBuffer().bindFramebufferTexture();
	//		/*Minecraft.getMinecraft().getFramebuffer().bindFramebufferTexture();
	//		Tessellator tessellator = Tessellator.getInstance();
	//        VertexBuffer vertexbuffer = tessellator.getBuffer();
	//        float f = Minecraft.getMinecraft().thePlayer.getBrightness(0);
	//        GlStateManager.color(f, f, f, 1F);
	//        GlStateManager.enableBlend();
	//        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
	//        GlStateManager.pushMatrix();
	//        float f1 = 4.0F;
	//        float f2 = -1.0F;
	//        float f3 = 1.0F;
	//        float f4 = -1.0F;
	//        float f5 = 1.0F;
	//        float f6 = -0.5F;
	//        float f7 = -Minecraft.getMinecraft().thePlayer.rotationYaw / 64.0F;
	//        float f8 = Minecraft.getMinecraft().thePlayer.rotationPitch / 64.0F;
	//        vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX);
	//        vertexbuffer.pos(-1.0D, -1.0D, -0.5D).tex((double)(4.0F + f7), (double)(4.0F + f8)).endVertex();
	//        vertexbuffer.pos(1.0D, -1.0D, -0.5D).tex((double)(0.0F + f7), (double)(4.0F + f8)).endVertex();
	//        vertexbuffer.pos(1.0D, 1.0D, -0.5D).tex((double)(0.0F + f7), (double)(0.0F + f8)).endVertex();
	//        vertexbuffer.pos(-1.0D, 1.0D, -0.5D).tex((double)(4.0F + f7), (double)(0.0F + f8)).endVertex();
	//        tessellator.draw();
	//        GlStateManager.popMatrix();
	//        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
	//        GlStateManager.disableBlend();*/
	//	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onPostRender(TickEvent.RenderTickEvent event) {
		if(ShaderHelper.INSTANCE.getWorldShader() != null)
			ShaderHelper.INSTANCE.getWorldShader().clearLights();
	}
}