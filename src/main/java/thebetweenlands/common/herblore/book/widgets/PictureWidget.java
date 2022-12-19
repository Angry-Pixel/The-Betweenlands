package thebetweenlands.common.herblore.book.widgets;

import java.util.ArrayList;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class PictureWidget extends ManualWidgetBase {
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

        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GlStateManager.color(1F, 1F, 1F, 1F);
            manual.drawTexture(xStart, yStart, width, height, textureWidth, textureHeight, xIndex, xIndex + width, yIndex, yIndex + height);
        GlStateManager.disableBlend();

        if (toolTips.size() > 0 && mouseX >= xStart && mouseX <= xStart + width && mouseY >= yStart && mouseY <= yStart + height) {
            renderTooltip(mouseX, mouseY, toolTips, 0xffffff, 0xf0100010);
        }
    }
}
