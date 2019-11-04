package thebetweenlands.client.render.tile;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import thebetweenlands.common.tile.TileEntityWeedwoodWorkbench;

public class RenderWeedwoodWorkbench extends TileEntitySpecialRenderer<TileEntityWeedwoodWorkbench> {

	@Override
	public void render(TileEntityWeedwoodWorkbench table, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
		GlStateManager.pushMatrix();
		GlStateManager.translate(x + 0.5D, y + 0.875D, z + 0.5D);
		GlStateManager.scale(0.25F, 0.25F, 0.25F);
		GlStateManager.rotate(90.0F * (-table.rotation + 3), 0.0F, 1.0F, 0.0F);
		GlStateManager.translate(-1.5F, -0.0F, -1.0F);
		GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		
		Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
		float prevLGTX = OpenGlHelper.lastBrightnessX;
		float prevLGTY = OpenGlHelper.lastBrightnessY;
		BlockPos pos = table.getPos();
		int bright = table.getWorld().getCombinedLight(pos.up(), 0);
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, bright % 65536, bright / 65536);

		for (int row = 0; row < 3; row++) {
			for (int column = 0; column < 3; column++) {
				ItemStack stack = table.craftingSlots.get(column * 3 + row);
				if (!stack.isEmpty()) {
					GlStateManager.pushMatrix();
					GlStateManager.translate(row * 0.75F, 0.0D, column * 0.75F);
					GlStateManager.translate(0.75F, 0.52F, 0.25F);
					GlStateManager.scale(0.5F, 0.5F, 0.5F);
					GlStateManager.rotate(-90.0F, 1.0F, 0.0F, 0.0F);
					RenderHelper.disableStandardItemLighting();
					RenderItem renderItem = Minecraft.getMinecraft().getRenderItem();
					renderItem.renderItem(stack, renderItem.getItemModelWithOverrides(stack, null, null));
					GlStateManager.popMatrix();
				}
			}
		}
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, prevLGTX, prevLGTY);

		GlStateManager.popMatrix();
	}
}
