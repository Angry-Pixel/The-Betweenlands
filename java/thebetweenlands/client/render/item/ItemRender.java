package thebetweenlands.client.render.item;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.client.IItemRenderer;
import org.lwjgl.opengl.GL11;

public class ItemRender implements IItemRenderer
{
    TileEntitySpecialRenderer render;
    public TileEntity entity;
    public ItemRender(TileEntitySpecialRenderer render, TileEntity entity)
    {
        this.entity = entity;
        this.render = render;
    }
    
    @Override
    public boolean handleRenderType(ItemStack item, ItemRenderType type)
    {
        return true;
    }
    
    @Override
    public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper)
    {
        return true;
    }
    
    @Override
    public void renderItem(ItemRenderType type, ItemStack item, Object ... data)
    {
        if (type == IItemRenderer.ItemRenderType.ENTITY){
            GL11.glRotatef(180f, 0f, 1f, 0f);
            GL11.glTranslatef(-0.5f, -0.5f, -0.4f);
        }
        render.renderTileEntityAt(entity, 0.0D, 0.0D, 0.0D, 0.0F);
    }
}