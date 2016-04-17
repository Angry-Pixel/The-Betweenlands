package thebetweenlands.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
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

    public void drawFireFly(Minecraft minecraft) {
        updateCounter += 0.0075F;

        posX -= 0.1F;

        posY += motionY;
        posX += motionX;

        if (motionY > 0) {
            motionY -= 0.001F;
        } else {
            motionY += 0.001F;
        }
        motionX -= 0.0002F;

        GL11.glPushMatrix();
        GL11.glScalef(0.1F, 0.1F, 0.1F);
        GL11.glDepthMask(false);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 1);
        minecraft.getTextureManager().bindTexture(FIREFLY_TEXTURE);
        GL11.glTranslatef(posX * 10 + (float) (Math.sin(updateCounter) * 500), posY * 10, 0);

        GL11.glScalef(0.1F + (float) (Math.sin(updateCounter) * Math.sin(updateCounter)) / 3, 0.1F + (float) (Math.sin(updateCounter) * Math.sin(updateCounter)) / 3, 0.1F + (float) (Math.sin(updateCounter) * Math.sin(updateCounter)) / 3);

        this.drawTexturedModalRectWithColor(-125, -125, 0, 0, 250, 250, 0xFFEC810E, this.zLevel);
        this.drawTexturedModalRectWithColor(-125, -125, 0, 0, 250, 250, 0xFFEC810E, this.zLevel);
        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        GL11.glDisable(3042);
        GL11.glDepthMask(true);
        GL11.glPopMatrix();
    }

    public void drawTexturedModalRectWithColor(double x, double y, double u, double v, double width, double height, int color, double zLevel) {
        float f = 0.00390625F;
        float f1 = 0.00390625F;
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();

        float o = (float) (color >> 24 & 0xff) / 255F;
        float r = (float) (color >> 16 & 0xff) / 255F;
        float g = (float) (color >> 8 & 0xff) / 255F;
        float b = (float) (color & 0xff) / 255F;

        tessellator.setColorRGBA_F(r, g, b, o);

        tessellator.addVertexWithUV(x + 0, y + height, zLevel, (u) * f, (v + height) * f1);
        tessellator.addVertexWithUV(x + width, y + height, zLevel, (u + width) * f, (v + height) * f1);
        tessellator.addVertexWithUV(x + width, y + 0, zLevel, (u + width) * f, v * f1);
        tessellator.addVertexWithUV(x + 0, y + 0, zLevel, u * f, v * f1);
        tessellator.draw();
    }
}
