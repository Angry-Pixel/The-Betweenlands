package thebetweenlands.client.render.tile;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemSkull;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import thebetweenlands.common.tile.TileEntityGroundItem;

public class RenderGroundItem extends TileEntitySpecialRenderer<TileEntityGroundItem> {

    @Override
    public void render(TileEntityGroundItem te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        if (te != null) {
            ItemStack stack = te.getStack();
            if (!stack.isEmpty()) {
                RenderItem renderItem = Minecraft.getMinecraft().getRenderItem();
                GlStateManager.pushMatrix();
                GlStateManager.pushAttrib();
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);


                long i = MathHelper.getCoordinateRandom(te.getPos().getX(), te.getPos().getY(), te.getPos().getZ());
                double offX = (double)((float)(i >> 16 & 15L) / 15.0F);
                double offZ = (double)((float)(i >> 24 & 15L) / 15.0F);
                double offY = (double)((float)(i >> 20 & 15L) / 15.0F);
                
                if(te.hasRandomOffset()) {
                	GlStateManager.translate(x + 0.5F + (offX - 0.5D) * 0.5D, y + te.getYOffset(), z + 0.5F + (offZ - 0.5D) * 0.5D);
                } else {
                	GlStateManager.translate(x + 0.5F, y + te.getYOffset(), z + 0.5F);
                }

                GlStateManager.rotate(te.getYRotation((MathHelper.abs((float) offX) * 120F + MathHelper.abs((float) offZ) * 60F + MathHelper.abs((float) offY) * 180F) % 360F), 0.0F, 1.0F, 0.0F);
            	GlStateManager.rotate(te.getTiltRotation(), 0.0F, 0.0F, 1.0F);
                
            	float scale = te.getItemScale();
                GlStateManager.scale(scale, scale, scale);
                RenderHelper.enableStandardItemLighting();

                if (!renderItem.shouldRenderItemIn3D(stack) || stack.getItem() instanceof ItemSkull) {
                    GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
                }

                renderItem.renderItem(stack, ItemCameraTransforms.TransformType.FIXED);

                RenderHelper.disableStandardItemLighting();
                GlStateManager.popAttrib();
                GlStateManager.popMatrix();
            }
        }

    }
}
