package thebetweenlands.client.render.tileentity;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import org.lwjgl.opengl.GL11;
import thebetweenlands.tileentities.TileEntityBLCraftingTable;
import thebetweenlands.utils.ItemRenderHelper;

public class TileEntityBLWorkbenchRenderer
        extends TileEntitySpecialRenderer
{
    @Override
    public void renderTileEntityAt(TileEntity tile, double x, double y, double z, float partTicks) {
        this.renderBLWorkbenchAt((TileEntityBLCraftingTable) tile, x, y, z, partTicks);
    }

    // synthetic bridge method!e
    public void renderBLWorkbenchAt(TileEntityBLCraftingTable table, double x, double y, double z, float partTicks) {
        GL11.glPushMatrix();
        GL11.glTranslated(x + 0.1875D, y + 0.875D, z + 0.3125D);
        GL11.glScalef(0.25F, 0.25F, 0.25F);
        GL11.glTranslatef(1.25F, 0.0F, 0.75F);
        GL11.glRotatef(90.0F * (-table.rotation + 3), 0.0F, 1.0F, 0.0F);
        GL11.glTranslatef(-1.25F, -0.0F, -0.75F);

        float prevLGTX = OpenGlHelper.lastBrightnessX;
        float prevLGTY = OpenGlHelper.lastBrightnessY;
        int bright = table.getWorldObj().getLightBrightnessForSkyBlocks(table.xCoord, table.yCoord+1, table.zCoord, 0);
        int brightX = bright % 65536;
        int brightY = bright / 65536;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, brightX / 1.0F, brightY / 1.0F);

        for( int row = 0; row < 3; row++ ) {
            for( int col = 0; col < 3; col++ ) {
                ItemStack slot = table.crfSlots[col * 3 + row];
                if( slot != null ) {
                    GL11.glPushMatrix();
                    GL11.glTranslated(0.75 * row, 0.0D, 0.75 * col);

                    if( slot.getItemSpriteNumber() != 0 || !(slot.getItem() instanceof ItemBlock)
                            || !RenderBlocks.renderItemIn3d(Block.getBlockFromItem(slot.getItem()).getRenderType()) )
                    {
                        GL11.glTranslatef(0.75F, 0.5F, 0.25F);
                        GL11.glScalef(0.5F, 0.5F, 0.5F);
                        GL11.glRotatef(90.0F, 1.0F, 0.0F, 0.0F);
                        GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F);
                    }
                    ItemRenderHelper.renderItemIn3D(slot);
                    GL11.glPopMatrix();
                }
            }
        }
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, prevLGTX, prevLGTY);

        GL11.glPopMatrix();
    }
}
