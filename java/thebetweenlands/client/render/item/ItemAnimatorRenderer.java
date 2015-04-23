package thebetweenlands.client.render.item;

import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;

import org.lwjgl.opengl.GL11;

import thebetweenlands.client.render.tileentity.TileEntityAnimatorRenderer;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ItemAnimatorRenderer
        implements IItemRenderer
{
    @Override
    public boolean handleRenderType(ItemStack item, ItemRenderType type) {
        return type != ItemRenderType.FIRST_PERSON_MAP;
    }

    @Override
    public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack stack, ItemRendererHelper helper) {
        return true;
    }

    @Override
    public void renderItem(ItemRenderType type, ItemStack stack, Object... data) {
        switch( type ) {
            case ENTITY:
                GL11.glScaled(0.75, 0.75, 0.75);
                render(stack, -0.25F, -0.25F, -0.25F);
                break;
            case EQUIPPED:
                render(stack, 0.5F, 0.5F, 0.5F);
                break;
            case EQUIPPED_FIRST_PERSON:
                render(stack, -0.5F, 1.0F, 0.5F);
                break;
            case INVENTORY:
                render(stack, -0.5F, -0.55F, -0.5F);
                break;
            default:
                break;
        }
    }

    private void render(ItemStack stack, float x, float y, float z) {
        GL11.glPushMatrix();
        GL11.glTranslatef(x, y, z);
        GL11.glScaled(0.5, 0.5, 0.5);

        TileEntityAnimatorRenderer.instance.renderTileAsItem(0, 0, 0);

        GL11.glPopMatrix();
    }
}
