package thebetweenlands.manual.widgets;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Bart on 9-8-2015.
 */
public class PictureWidget extends ManualWidgetsBase {

    ResourceLocation recourseLocation;
    public int width;
    public int height;
    private int xIndex = 0;
    private int yIndex = 0;

    ArrayList<String> toolTips = new ArrayList<>();

    public PictureWidget(int xStart, int yStart, String recourseLocation, int width, int height) {
        super(xStart, yStart);
        this.recourseLocation = new ResourceLocation(recourseLocation);
        this.width = width;
        this.height = height;
    }

    public PictureWidget(int xStart, int yStart, String recourseLocation, int width, int height, int xIndex, int yIndex) {
        super( xStart, yStart);
        this.recourseLocation = new ResourceLocation(recourseLocation);
        this.width = width;
        this.height = height;
        this.xIndex = xIndex;
        this.yIndex = yIndex;
    }

    public PictureWidget(int xStart, int yStart, String recourseLocation, int width, int height, ArrayList<String> toolTips) {
        super(xStart, yStart);
        this.recourseLocation = new ResourceLocation(recourseLocation);
        this.width = width;
        this.height = height;
        this.toolTips = toolTips;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void drawForeGround() {
        TextureManager render = Minecraft.getMinecraft().renderEngine;
        render.bindTexture(recourseLocation);

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glColor4f(1F, 1F, 1F, 1F);
        manual.drawTexturedModalRect(xStart, yStart, xIndex, yIndex, width, height);
        GL11.glDisable(GL11.GL_BLEND);

        if (toolTips.size() > 0 && mouseX >= xStart && mouseX <= xStart + width && mouseY >= yStart && mouseY <= yStart + height) {
            renderTooltip(mouseX, mouseY, toolTips, 0xffffff, 0xf0100010);
        }
    }
}
