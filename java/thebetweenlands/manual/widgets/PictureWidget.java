package thebetweenlands.manual.widgets;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;

/**
 * Created by Bart on 9-8-2015.
 */
public class PictureWidget extends ManualWidgetsBase {

    ResourceLocation recourseLocation;
    public int width;
    public int height;
    private int xIndex = 0;
    private int yIndex = 0;
    double textureWidth;
    double textureHeight;

    ArrayList<String> toolTips = new ArrayList<>();

    public PictureWidget(int xStart, int yStart, String recourseLocation, int width, int height, double textureWidth, double textureHeight) {
        super(xStart, yStart);
        this.recourseLocation = new ResourceLocation(recourseLocation);
        this.width = width;
        this.height = height;
        this.textureWidth = textureWidth;
        this.textureHeight = textureHeight;
    }

    public PictureWidget(int xStart, int yStart, String recourseLocation, int width, int height, int xIndex, int yIndex, double textureWidth, double textureHeight) {
        super(xStart, yStart);
        this.recourseLocation = new ResourceLocation(recourseLocation);
        this.width = width;
        this.height = height;
        this.xIndex = xIndex;
        this.yIndex = yIndex;
        this.textureWidth = textureWidth;
        this.textureHeight = textureHeight;
    }

    public PictureWidget(int xStart, int yStart, String recourseLocation, int width, int height, ArrayList<String> toolTips, double textureWidth, double textureHeight) {
        super(xStart, yStart);
        this.recourseLocation = new ResourceLocation(recourseLocation);
        this.width = width;
        this.height = height;
        this.toolTips = toolTips;
        this.textureWidth = textureWidth;
        this.textureHeight = textureHeight;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void drawForeGround() {
        TextureManager render = Minecraft.getMinecraft().renderEngine;
        render.bindTexture(recourseLocation);

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glColor4f(1F, 1F, 1F, 1F);
        manual.drawTexture(xStart, yStart, width, height, textureWidth, textureHeight, xIndex, xIndex + width, yIndex, yIndex + height);
        GL11.glDisable(GL11.GL_BLEND);

        if (toolTips.size() > 0 && mouseX >= xStart && mouseX <= xStart + width && mouseY >= yStart && mouseY <= yStart + height) {
            renderTooltip(mouseX, mouseY, toolTips, 0xffffff, 0xf0100010);
        }
    }
}
