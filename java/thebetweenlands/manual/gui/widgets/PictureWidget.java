package thebetweenlands.manual.gui.widgets;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import thebetweenlands.manual.gui.GuiManualBase;

import java.util.ArrayList;

/**
 * Created by Bart on 9-8-2015.
 */
public class PictureWidget extends ManualWidgetsBase {

    ResourceLocation recourseLocation;
    int width;
    int height;

    ArrayList<String> toolTips = new ArrayList<>();

    public PictureWidget(GuiManualBase manual, int xStart, int yStart, String recourseLocation, int width, int height) {
        super(manual, xStart, yStart);
        this.recourseLocation = new ResourceLocation(recourseLocation);
        this.width = width;
        this.height = height;
    }

    public PictureWidget(GuiManualBase manual, int xStart, int yStart, String recourseLocation, int width, int height, ArrayList<String> toolTips) {
        super(manual, xStart, yStart);
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
        manual.drawTexturedModalRect(xStart, yStart, 0, 0, width, height);
        GL11.glDisable(GL11.GL_BLEND);

        if (toolTips.size() > 0 && mouseX >= xStart && mouseX <= xStart + width && mouseY >= yStart && mouseY <= yStart + height) {
            renderTooltip(mouseX, mouseY, toolTips, 0xffffff, 0xf0100010);
        }
    }
}
