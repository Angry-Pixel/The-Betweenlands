package thebetweenlands.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import thebetweenlands.lib.ModInfo;

public class GuiFirefly extends Gui {
    public static final ResourceLocation FIREFLY_TEXTURE = new ResourceLocation(ModInfo.ID, "textures/gui/main/firefly.png");

    private float updateCounter = 0;
    public float posX = 0;
    public float posY = 0;
    private float motionY = 0;
    private float motionX = 0;

    public GuiFirefly(float x, float y, float mX, float mY) {
        posX = x;
        posY = y;
        motionX = mX;
        motionY = mY;
    }

    public void drawFireFly(Minecraft minecraft, GuiScreen gui) {
        updateCounter += 0.0075F;

        posY += 0.1F;

        posY += motionY;
        posX += motionX;

        if (motionX > 0) {
            motionX -= 0.001F;
        } else {
            motionX += 0.001F;
        }
        motionY += 0.0002F;

        GL11.glPushMatrix();
        GL11.glScalef(0.1F, 0.1F, 0.1F);
        GL11.glDepthMask(false);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 1);
        minecraft.getTextureManager().bindTexture(FIREFLY_TEXTURE);
        GL11.glTranslatef(posX * 10 + (float) (Math.sin(updateCounter) * 500), posY * 10, 0);

        GL11.glScalef(0.25F + (float) (Math.sin(updateCounter) * Math.sin(updateCounter)) / 2, 0.25F + (float) (Math.sin(updateCounter) * Math.sin(updateCounter)) / 2, 0.25F + (float) (Math.sin(updateCounter) * Math.sin(updateCounter)) / 2);

        this.drawTexturedModalRectWithColor(-125, -125, 0, 0, 250, 250, 0xFFEC810E);
        this.drawTexturedModalRectWithColor(-125, -125, 0, 0, 250, 250, 0xFFEC810E);
        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        GL11.glDisable(3042);
        GL11.glDepthMask(true);
        GL11.glPopMatrix();
    }

    public void drawTexturedModalRectWithColor(int par1, int par2, int par3, int par4, int par5, int par6, int color) {
        float var7 = 0.00390625F;
        float var8 = 0.00390625F;
        Tessellator var9 = Tessellator.instance;
        var9.startDrawingQuads();

        float o = (float) (color >> 24 & 0xff) / 255F;
        float r = (float) (color >> 16 & 0xff) / 255F;
        float g = (float) (color >> 8 & 0xff) / 255F;
        float b = (float) (color & 0xff) / 255F;

        var9.setColorRGBA_F(r, g, b, o);

        var9.addVertexWithUV((double) (par1 + 0), (double) (par2 + par6), (double) this.zLevel, (double) ((float) (par3 + 0) * var7), (double) ((float) (par4 + par6) * var8));
        var9.addVertexWithUV((double) (par1 + par5), (double) (par2 + par6), (double) this.zLevel, (double) ((float) (par3 + par5) * var7), (double) ((float) (par4 + par6) * var8));
        var9.addVertexWithUV((double) (par1 + par5), (double) (par2 + 0), (double) this.zLevel, (double) ((float) (par3 + par5) * var7), (double) ((float) (par4 + 0) * var8));
        var9.addVertexWithUV((double) (par1 + 0), (double) (par2 + 0), (double) this.zLevel, (double) ((float) (par3 + 0) * var7), (double) ((float) (par4 + 0) * var8));
        var9.draw();
    }
}
