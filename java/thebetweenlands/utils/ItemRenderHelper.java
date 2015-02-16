package thebetweenlands.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer.ItemRenderType;
import org.lwjgl.opengl.GL11;

public class ItemRenderHelper
{
    /**
     * Renders an item with the size of a normal block.
     *
     * @param item       ItemStack
     * @param renderPass int
     */
    public static void renderItem(ItemStack item, int renderPass) {
        GL11.glTranslated(0.5, 0, 1.0D / 16.0D);
        GL11.glRotatef(-335.0F, 0.0F, 0.0F, 1.0F);
        GL11.glRotatef(-50.0F, 0.0F, 1.0F, 0.0F);
        RenderManager.instance.itemRenderer.renderItem(Minecraft.getMinecraft().thePlayer, item, renderPass, ItemRenderType.ENTITY);
    }
}
