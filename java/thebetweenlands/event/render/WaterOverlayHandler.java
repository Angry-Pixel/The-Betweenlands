package thebetweenlands.event.render;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderBlockOverlayEvent;
import net.minecraftforge.client.event.RenderBlockOverlayEvent.OverlayType;
import thebetweenlands.blocks.BLBlockRegistry;

public class WaterOverlayHandler {
	public static final WaterOverlayHandler INSTANCE = new WaterOverlayHandler();

	private static final ResourceLocation RES_UNDERWATER_OVERLAY = new ResourceLocation("textures/misc/underwater.png");

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onRenderBlockOverlay(RenderBlockOverlayEvent event) {
		if(event.overlayType == OverlayType.WATER) {
			event.setCanceled(true);
			this.renderWarpedTextureOverlay(event.renderPartialTicks);
		}
	}

	/**
	 * Renders the water overlay.
	 */
	private void renderWarpedTextureOverlay(float partialTicks) {
		Minecraft mc = Minecraft.getMinecraft();
		mc.getTextureManager().bindTexture(RES_UNDERWATER_OVERLAY);
		Tessellator tessellator = Tessellator.instance;
		float brightness = mc.thePlayer.getBrightness(partialTicks);
		int colorMultiplier = BLBlockRegistry.swampWater.colorMultiplier(mc.theWorld, MathHelper.floor_double(mc.thePlayer.posX), MathHelper.floor_double(mc.thePlayer.posY), MathHelper.floor_double(mc.thePlayer.posZ));
		float r = (float)(colorMultiplier >> 16 & 255) / 255.0F;
		float g = (float)(colorMultiplier >> 8 & 255) / 255.0F;
		float b = (float)(colorMultiplier & 255) / 255.0F;
		GL11.glColor4f(brightness * r, brightness * g, brightness * b, 0.5F);
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
