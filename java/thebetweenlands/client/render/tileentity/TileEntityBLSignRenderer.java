package thebetweenlands.client.render.tileentity;

import net.minecraft.block.Block;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.model.ModelSign;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.tileentities.TileEntityBLSign;

/**
 * Created by Bart on 16/01/2016.
 */
public class TileEntityBLSignRenderer extends TileEntitySpecialRenderer {
    private static final ResourceLocation field_147513_b = new ResourceLocation("thebetweenlands:textures/tiles/weedwoodSign.png");
    private final ModelSign field_147514_c = new ModelSign();

    public void renderTileEntityAt(TileEntityBLSign tileEntity, double x, double y, double z, float p_147500_8_) {
        Block block = tileEntity.getBlockType();
        GL11.glPushMatrix();
        float f1 = 0.6666667F;
        float f3;

        if (block == BLBlockRegistry.weedwoodSignStanding) {
            GL11.glTranslatef((float) x + 0.5F, (float) y + 0.75F * f1, (float) z + 0.5F);
            float f2 = (float) (tileEntity.getBlockMetadata() * 360) / 16.0F;
            GL11.glRotatef(-f2, 0.0F, 1.0F, 0.0F);
            this.field_147514_c.signStick.showModel = true;
        } else {
            int j = tileEntity.getBlockMetadata();
            f3 = 0.0F;

            if (j == 2) {
                f3 = 180.0F;
            }

            if (j == 4) {
                f3 = 90.0F;
            }

            if (j == 5) {
                f3 = -90.0F;
            }

            GL11.glTranslatef((float) x + 0.5F, (float) y + 0.75F * f1, (float) z + 0.5F);
            GL11.glRotatef(-f3, 0.0F, 1.0F, 0.0F);
            GL11.glTranslatef(0.0F, -0.3125F, -0.4375F);
            this.field_147514_c.signStick.showModel = false;
        }

        this.bindTexture(field_147513_b);
        GL11.glPushMatrix();
        GL11.glScalef(f1, -f1, -f1);
        this.field_147514_c.renderSign();
        GL11.glPopMatrix();
        FontRenderer fontrenderer = this.func_147498_b();
        f3 = 0.016666668F * f1;
        GL11.glTranslatef(0.0F, 0.5F * f1, 0.07F * f1);
        GL11.glScalef(f3, -f3, f3);
        GL11.glNormal3f(0.0F, 0.0F, -1.0F * f3);
        GL11.glDepthMask(false);
        byte b0 = 0;

        for (int i = 0; i < tileEntity.signText.length; ++i) {
            String s = tileEntity.signText[i];

            if (i == tileEntity.lineBeingEdited) {
                s = "> " + s + " <";
                fontrenderer.drawString(s, -fontrenderer.getStringWidth(s) / 2, i * 10 - tileEntity.signText.length * 5, b0);
            } else {
                fontrenderer.drawString(s, -fontrenderer.getStringWidth(s) / 2, i * 10 - tileEntity.signText.length * 5, b0);
            }
        }

        GL11.glDepthMask(true);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glPopMatrix();
    }

    @Override
    public void renderTileEntityAt(TileEntity tileEntity, double x, double y, double z, float p_147500_8_)
    {
        this.renderTileEntityAt((TileEntityBLSign)tileEntity, x, y, z, p_147500_8_);
    }
}
