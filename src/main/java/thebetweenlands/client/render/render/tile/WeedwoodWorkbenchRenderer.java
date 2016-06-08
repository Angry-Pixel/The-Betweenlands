package thebetweenlands.client.render.render.tile;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import org.lwjgl.opengl.GL11;
import thebetweenlands.common.tile.TileEntityWeedwoodWorkbench;

public class WeedwoodWorkbenchRenderer extends TileEntitySpecialRenderer<TileEntityWeedwoodWorkbench> {
    private final RenderItem renderItem = Minecraft.getMinecraft().getRenderItem();

    @Override
    public void renderTileEntityAt(TileEntityWeedwoodWorkbench table, double x, double y, double z, float partialTicks, int destroyStage) {
        GL11.glPushMatrix();
        GL11.glTranslated(x + 0.25D, y + 0.875D, z + 0.375D);
        GL11.glScalef(0.25F, 0.25F, 0.25F);
        GL11.glTranslatef(1.25F, 0.0F, 0.75F);
        GL11.glRotatef(90.0F * (-table.rotation + 3), 0.0F, 1.0F, 0.0F);
        GL11.glTranslatef(-1.25F, -0.0F, -0.75F);

        Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        float prevLGTX = OpenGlHelper.lastBrightnessX;
        float prevLGTY = OpenGlHelper.lastBrightnessY;
        BlockPos pos = table.getPos();
        int bright = table.getWorld().getCombinedLight(pos.up(), 0);
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, bright % 65536, bright / 65536);

        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 3; column++) {
                ItemStack stack = table.craftingSlots[column * 3 + row];
                if (stack != null) {
                    GL11.glPushMatrix();
                    GL11.glTranslated(row * 0.75F, 0.0D, column * 0.75F);
                    GL11.glTranslatef(0.75F, 0.52F, 0.25F);
                    GL11.glScalef(0.5F, 0.5F, 0.5F);
                    GL11.glRotatef(90.0F, 1.0F, 0.0F, 0.0F);
                    GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F);
                    renderItem.renderItem(stack, renderItem.getItemModelMesher().getItemModel(stack));
                    GL11.glPopMatrix();
                }
            }
        }
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, prevLGTX, prevLGTY);

        GL11.glPopMatrix();
    }
}
