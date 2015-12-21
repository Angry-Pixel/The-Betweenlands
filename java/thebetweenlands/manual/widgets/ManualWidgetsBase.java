package thebetweenlands.manual.widgets;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import thebetweenlands.manual.GuiManualBase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bart on 8-8-2015.
 */
public class ManualWidgetsBase {
    public static ResourceLocation icons = new ResourceLocation("thebetweenlands:textures/gui/manual/icons.png");

    public GuiManualBase manual;
    public int unchangedXStart;
    public int unchangedYStart;
    public int xStart;
    public int yStart;
    protected int mouseX;

    protected int mouseY;

    public boolean isPageRight = false;

    public boolean isEmpty = false;


    public static String processTimeString = StatCollector.translateToLocal("manual.widget.process.time");
    public static String processTimeMinutesString = StatCollector.translateToLocal("manual.widget.process.time.minutes");
    public static String processTimeSecondsString = StatCollector.translateToLocal("manual.widget.process.time.seconds");
    public static String burnTimeString = StatCollector.translateToLocal("manual.widget.burn.time");


    public ManualWidgetsBase(int xStart, int yStart) {
        this.unchangedXStart = xStart;
        this.unchangedYStart = yStart;
    }

    public void init(GuiManualBase manual) {
        this.manual = manual;
        resize();
    }

    public void setPageToRight() {
        this.isPageRight = true;
    }

    public static int renderTooltip(int x, int y, List<String> tooltipData, int color, int color2) {
        boolean lighting = GL11.glGetBoolean(GL11.GL_LIGHTING);
        if (lighting)
            RenderHelper.disableStandardItemLighting();

        if (!tooltipData.isEmpty()) {
            int var5 = 0;
            int var6;
            int var7;
            FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;
            for (var6 = 0; var6 < tooltipData.size(); ++var6) {
                var7 = fontRenderer.getStringWidth(tooltipData.get(var6));
                if (var7 > var5)
                    var5 = var7;
            }
            var6 = x + 12;
            var7 = y - 12;
            int var9 = 8;
            if (tooltipData.size() > 1)
                var9 += 2 + (tooltipData.size() - 1) * 10;
            float z = 300F;
            drawGradientRect(var6 - 3, var7 - 4, z, var6 + var5 + 3, var7 - 3, color2, color2);
            drawGradientRect(var6 - 3, var7 + var9 + 3, z, var6 + var5 + 3, var7 + var9 + 4, color2, color2);
            drawGradientRect(var6 - 3, var7 - 3, z, var6 + var5 + 3, var7 + var9 + 3, color2, color2);
            drawGradientRect(var6 - 4, var7 - 3, z, var6 - 3, var7 + var9 + 3, color2, color2);
            drawGradientRect(var6 + var5 + 3, var7 - 3, z, var6 + var5 + 4, var7 + var9 + 3, color2, color2);
            int var12 = (color & 0xFFFFFF) >> 1 | color & -16777216;
            drawGradientRect(var6 - 3, var7 - 3 + 1, z, var6 - 3 + 1, var7 + var9 + 3 - 1, color, var12);
            drawGradientRect(var6 + var5 + 2, var7 - 3 + 1, z, var6 + var5 + 3, var7 + var9 + 3 - 1, color, var12);
            drawGradientRect(var6 - 3, var7 - 3, z, var6 + var5 + 3, var7 - 3 + 1, color, color);
            drawGradientRect(var6 - 3, var7 + var9 + 2, z, var6 + var5 + 3, var7 + var9 + 3, var12, var12);

            GL11.glDisable(GL11.GL_DEPTH_TEST);
            for (int var13 = 0; var13 < tooltipData.size(); ++var13) {
                String var14 = tooltipData.get(var13);
                fontRenderer.drawStringWithShadow(var14, var6, var7, -1);
                if (var13 == 0)
                    var7 += 2;
                var7 += 10;
            }
            GL11.glEnable(GL11.GL_DEPTH_TEST);

            return var7 + 12;
        }
        if (!lighting)
            RenderHelper.disableStandardItemLighting();
        GL11.glColor4f(1F, 1F, 1F, 1F);

        return 0;
    }

    public static void drawGradientRect(int x, int y, float z, int par3, int par4, int par5, int par6) {
        float var7 = (par5 >> 24 & 255) / 255F;
        float var8 = (par5 >> 16 & 255) / 255F;
        float var9 = (par5 >> 8 & 255) / 255F;
        float var10 = (par5 & 255) / 255F;
        float var11 = (par6 >> 24 & 255) / 255F;
        float var12 = (par6 >> 16 & 255) / 255F;
        float var13 = (par6 >> 8 & 255) / 255F;
        float var14 = (par6 & 255) / 255F;
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glShadeModel(GL11.GL_SMOOTH);
        Tessellator var15 = Tessellator.instance;
        var15.startDrawingQuads();
        var15.setColorRGBA_F(var8, var9, var10, var7);
        var15.addVertex(par3, y, z);
        var15.addVertex(x, y, z);
        var15.setColorRGBA_F(var12, var13, var14, var11);
        var15.addVertex(x, par4, z);
        var15.addVertex(par3, par4, z);
        var15.draw();
        GL11.glShadeModel(GL11.GL_FLAT);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
    }

    @SideOnly(Side.CLIENT)
    public void draw(int mouseX, int mouseY) {
        this.mouseX = mouseX;
        this.mouseY = mouseY;
        drawBackGround();
        drawForeGround();
    }

    @SideOnly(Side.CLIENT)
    public void resize() {
        this.xStart = (isPageRight ? manual.xStartRightPage : manual.xStart) + unchangedXStart;
        this.yStart = manual.yStart + unchangedYStart;
    }

    @SideOnly(Side.CLIENT)
    public void drawBackGround() {

    }

    public void changeXStart(int xStart) {
        this.unchangedXStart = xStart;
        //this.xStart = manual.xStart + unchangedXStart;
    }

    public void changeYStart(int yStart) {
        this.unchangedYStart = yStart;
        //this.yStart = manual.yStart + unchangedYStart;
    }

    @SideOnly(Side.CLIENT)
    public void drawForeGround() {
    }

    public void keyTyped(char c, int key) {
    }

    @SideOnly(Side.CLIENT)
    public void updateScreen() {
        if (manual.untilUpdate % 5 == 0)
            resize();
    }

    public void mouseClicked(int x, int y, int mouseButton) {
    }

    @SideOnly(Side.CLIENT)
    public void renderItem(int xPos, int yPos, ItemStack stack, boolean hasSpecialTooltip) {
        RenderItem render = new RenderItem();
        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        RenderHelper.enableGUIStandardItemLighting();
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        render.renderItemAndEffectIntoGUI(Minecraft.getMinecraft().fontRenderer, Minecraft.getMinecraft().getTextureManager(), stack, xPos, yPos);
        render.renderItemOverlayIntoGUI(Minecraft.getMinecraft().fontRenderer, Minecraft.getMinecraft().getTextureManager(), stack, xPos, yPos);
        RenderHelper.disableStandardItemLighting();
        GL11.glPopMatrix();
        if (!hasSpecialTooltip && mouseX >= xPos && mouseY >= yPos && mouseX <= xPos + 16 && mouseY <= yPos + 16) {
            if (stack != null) {
                List<String> tooltipData = stack.getTooltip(Minecraft.getMinecraft().thePlayer, false);
                List<String> parsedTooltip = new ArrayList();
                boolean first = true;

                for (String s : tooltipData) {
                    String s_ = s;
                    if (!first)
                        s_ = EnumChatFormatting.GRAY + s;
                    parsedTooltip.add(s_);
                    first = false;
                }
                renderTooltip(mouseX, mouseY, parsedTooltip, 0xffffff, 0xf0100010);
            }
        }

        GL11.glDisable(GL11.GL_LIGHTING);
    }

    public void addSpecialItemTooltip(int xPos, int yPos, ItemStack stack, ArrayList<String> toolTips) {
        if (mouseX >= xPos && mouseY >= yPos && mouseX <= xPos + 16 && mouseY <= yPos + 16) {
            if (stack != null) {
                List<String> tooltipData = stack.getTooltip(Minecraft.getMinecraft().thePlayer, false);
                List<String> parsedTooltip = new ArrayList();
                boolean first = true;

                for (String tip : toolTips)
                    tooltipData.add(tip);

                for (String s : tooltipData) {
                    String s_ = s;
                    if (!first)
                        s_ = EnumChatFormatting.GRAY + s;
                    parsedTooltip.add(s_);
                    first = false;
                }
                renderTooltip(mouseX, mouseY, parsedTooltip, 0xffffff, 0xf0100010);
            }
        }
    }
}
