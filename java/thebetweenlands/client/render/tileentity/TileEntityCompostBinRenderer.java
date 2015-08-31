package thebetweenlands.client.render.tileentity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import thebetweenlands.client.model.block.ModelCompostBin;
import thebetweenlands.tileentities.TileEntityCompostBin;
import thebetweenlands.utils.ItemRenderHelper;

import java.util.Random;

@SideOnly(Side.CLIENT)
public class TileEntityCompostBinRenderer extends TileEntitySpecialRenderer
{
    public static ResourceLocation TEXTURE = new ResourceLocation("thebetweenlands:textures/tiles/compostBin.png");
    private final ModelCompostBin model = new ModelCompostBin();

    @Override
    public void renderTileEntityAt(TileEntity tile, double x, double y, double z, float partialTickTime)
    {
        TileEntityCompostBin bin = (TileEntityCompostBin) tile;
        int meta = bin.getBlockMetadata();
        GL11.glPushMatrix();
        GL11.glTranslatef((float) x + .5f, (float) y, (float) z + .5f);
        GL11.glRotatef(getRotation(meta), 0.0F, 1F, 0F);

        for (int i = 0; i < bin.getSizeInventory(); i++)
        {
            ItemStack stack = bin.getStackInSlot(i);

            if (stack != null)
            {
                GL11.glPushMatrix();

                GL11.glTranslatef(0, .1f + i * 0.7f / bin.getSizeInventory(), .08f);
                GL11.glScalef(.36f, .36f, .36f);
                GL11.glRotatef(new Random(i * 12315).nextFloat() * 360f, 0, 1, 0);
                GL11.glRotatef(90.0f, 1, 0, 0);
                ItemRenderHelper.renderItem(stack, 0);

                GL11.glPopMatrix();
            }
        }

        GL11.glTranslatef(0, 1.5f, 0);
        GL11.glScalef(1F, -1F, -1F);
        GL11.glDisable(GL11.GL_CULL_FACE);
        bindTexture(TEXTURE);
        model.render(bin.getLitAngle(partialTickTime));
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glPopMatrix();
    }

    public static float getRotation(int meta)
    {
        switch (meta)
        {
            case 5:
                return 180F;
            case 4:
            default:
                return 0F;
            case 3:
                return 90F;
            case 2:
                return -90F;
        }
    }
}