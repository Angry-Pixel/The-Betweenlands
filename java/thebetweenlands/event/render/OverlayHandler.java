package thebetweenlands.event.render;

import java.lang.reflect.Method;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.ReflectionHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderBlockOverlayEvent;
import net.minecraftforge.client.event.RenderBlockOverlayEvent.OverlayType;
import net.minecraftforge.client.event.RenderHandEvent;
import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.recipes.BLMaterials;

public class OverlayHandler {
	public static final OverlayHandler INSTANCE = new OverlayHandler();

	private Method mERrenderHand;
	private boolean cancelOverlay = false;


	private static final ResourceLocation RES_UNDERWATER_OVERLAY = new ResourceLocation("textures/misc/underwater.png");

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onRenderBlockOverlay(RenderBlockOverlayEvent event) {
		if(this.cancelOverlay) {
			event.setCanceled(true);
			return;
		}
		if(event.overlayType == OverlayType.WATER) {
			event.setCanceled(true);

			Minecraft mc = Minecraft.getMinecraft();
			mc.getTextureManager().bindTexture(RES_UNDERWATER_OVERLAY);
			int colorMultiplier = BLBlockRegistry.swampWater.colorMultiplier(mc.theWorld, MathHelper.floor_double(mc.thePlayer.posX), MathHelper.floor_double(mc.thePlayer.posY), MathHelper.floor_double(mc.thePlayer.posZ));
			float r = (float)(colorMultiplier >> 16 & 255) / 255.0F;
			float g = (float)(colorMultiplier >> 8 & 255) / 255.0F;
			float b = (float)(colorMultiplier & 255) / 255.0F;
			GL11.glColor4f(r, g, b, 0.5F);
			this.renderWarpedTextureOverlay(event.renderPartialTicks);
		}
	}

	public void renderHand(float partialTicks, int renderPass) {
		if(this.mERrenderHand == null) {
			try {
				this.mERrenderHand = ReflectionHelper.findMethod(EntityRenderer.class, null, new String[]{"renderHand", "func_78476_b", "b"}, new Class[]{float.class, int.class});
				this.mERrenderHand.setAccessible(true);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		this.cancelOverlay = true;
		try {
			this.mERrenderHand.invoke(Minecraft.getMinecraft().entityRenderer, partialTicks, renderPass);
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.cancelOverlay = false;
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onRenderHand(RenderHandEvent event) {
		EntityPlayer player = Minecraft.getMinecraft().thePlayer;
		World world = Minecraft.getMinecraft().theWorld;
		Block block = ActiveRenderInfo.getBlockAtEntityViewpoint(world, player, (float) event.partialTicks);
		if(player != null && block.getMaterial() == BLMaterials.tar && !this.cancelOverlay) {
			event.setCanceled(true);

			GL11.glPushMatrix();
			GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);

			//Just to set up the necessary GL matrices
			this.renderHand(event.partialTicks, event.renderPass);

			GL11.glDisable(GL11.GL_DEPTH_TEST);

			Minecraft mc = Minecraft.getMinecraft();
			mc.getTextureManager().bindTexture(RES_UNDERWATER_OVERLAY);
			GL11.glColor4f(1, 1, 1, 1);

			this.renderWarpedTextureOverlay(event.partialTicks);

			GL11.glEnable(GL11.GL_DEPTH_TEST);
			GL11.glPopMatrix();
		}
	}

	/**
	 * Renders the water overlay.
	 * Set color and bind texture before calling this method.
	 */
	private void renderWarpedTextureOverlay(float partialTicks) {
		Minecraft mc = Minecraft.getMinecraft();
		Tessellator tessellator = Tessellator.instance;
		float brightness = mc.thePlayer.getBrightness(partialTicks);
		GL11.glEnable(GL11.GL_BLEND);
		OpenGlHelper.glBlendFunc(770, 771, 1, 0);
		GL11.glPushMatrix();
		float tu = 4.0F;
		float minX = -1.0F;
		float maxX = 1.0F;
		float minY = -1.0F;
		float maxY = 1.0F;
		float z = -0.5F;
		float tuOffset = -mc.thePlayer.rotationYaw / 64.0F;
		float tvOffset = mc.thePlayer.rotationPitch / 64.0F;
		tessellator.setBrightness((int)(brightness*255.0F));
		tessellator.startDrawingQuads();
		tessellator.addVertexWithUV((double)minX, (double)minY, (double)z, (double)(tu + tuOffset), (double)(tu + tvOffset));
		tessellator.addVertexWithUV((double)maxX, (double)minY, (double)z, (double)(0.0F + tuOffset), (double)(tu + tvOffset));
		tessellator.addVertexWithUV((double)maxX, (double)maxY, (double)z, (double)(0.0F + tuOffset), (double)(0.0F + tvOffset));
		tessellator.addVertexWithUV((double)minX, (double)maxY, (double)z, (double)(tu + tuOffset), (double)(0.0F + tvOffset));
		tessellator.draw();
		GL11.glPopMatrix();
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glDisable(GL11.GL_BLEND);
	}
}
