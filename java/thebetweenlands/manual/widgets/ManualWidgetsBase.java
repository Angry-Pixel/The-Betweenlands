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
import thebetweenlands.items.BLItemRegistry;
import thebetweenlands.manual.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bart on 8-8-2015.
 */
@SideOnly(Side.CLIENT)
public class ManualWidgetsBase {
    public static class PageLink {
        public int x;
        public int y;
        public int width;
        public int height;
        public int pageNumber;
        public ManualCategory category;

        public PageLink(int x, int y, int width, int height, ItemStack item) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            if (item != null) {
                if (!(item.getItem() == BLItemRegistry.elixir)) {
                    for (Page page : GuideBookEntryRegistry.itemsCategory.visiblePages) {
                        if (page.pageItems != null) {
                            for (ItemStack stack : page.pageItems) {
                                if (stack != null && stack.getItem() == item.getItem() && stack.getItemDamage() == item.getItemDamage()) {
                                    pageNumber = page.pageNumber;
                                    category = page.parentCategory;
                                }
                            }
                        }
                    }
                } else {
                    for (Page page : HLEntryRegistry.elixirCategory.visiblePages) {
                        if (page.pageItems != null) {
                            for (ItemStack stack : page.pageItems) {

                                if (stack != null && stack.getItem() == item.getItem() && (stack.getItemDamage() == item.getItemDamage() || stack.getItemDamage() == item.getItemDamage() - 1)) {
                                    pageNumber = page.pageNumber;
                                    category = page.parentCategory;
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public static ResourceLocation icons = new ResourceLocation("thebetweenlands:textures/gui/manual/icons.png");

    public ArrayList<PageLink> pageLinks = new ArrayList<>();
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

    @SideOnly(Side.CLIENT)
    public ManualWidgetsBase(int xStart, int yStart) {
        this.unchangedXStart = xStart;
        this.unchangedYStart = yStart;
    }

    @SideOnly(Side.CLIENT)
    public void init(GuiManualBase manual) {
        this.manual = manual;
        resize();
    }

    @SideOnly(Side.CLIENT)
    public void setPageToRight() {
        this.isPageRight = true;
    }

    /**
     * Renders a tooltip at the cursor
     *
     * @param x           the x coordinate to render the tooltip
     * @param y           the y coordinate to render the tooltip
     * @param tooltipData a list of the tooltip lines that need to be displayed
     * @param color       the color of the inside
     * @param color2      the color of the outlining
     * @return some kind of number?
     */
    @SideOnly(Side.CLIENT)
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

    /**
     * To be honest I have no idea how this works
     *
     * @param x
     * @param y
     * @param z
     * @param par3
     * @param par4
     * @param par5
     * @param par6
     */
    @SideOnly(Side.CLIENT)
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

    /**
     * Changes the starting coordinate
     *
     * @param xStart the new x start
     */
    @SideOnly(Side.CLIENT)
    public void changeXStart(int xStart) {
        this.unchangedXStart = xStart;
        //this.xStart = manual.xStart + unchangedXStart;
    }

    /**
     * Changes the starting coordinate
     *
     * @param yStart the new y start
     */
    @SideOnly(Side.CLIENT)
    public void changeYStart(int yStart) {
        this.unchangedYStart = yStart;
        //this.yStart = manual.yStart + unchangedYStart;
    }

    @SideOnly(Side.CLIENT)
    public void drawForeGround() {
    }

    @SideOnly(Side.CLIENT)
    public void keyTyped(char c, int key) {
    }

    @SideOnly(Side.CLIENT)
    public void updateScreen() {
        if (manual.untilUpdate % 5 == 0)
            resize();
    }

    @SideOnly(Side.CLIENT)
    public void mouseClicked(int x, int y, int mouseButton) {
        if (mouseButton == 0)
            for (PageLink link : pageLinks) {
                if (x >= link.x && y >= link.y && x <= link.x + link.width && y <= link.y + link.height)
                    manual.changeCategory(link.category, link.pageNumber + link.category.indexPages);
            }
    }

    /**
     * Renders an item
     *
     * @param xPos              the x coordinate to start drawing the item
     * @param yPos              the y coordinate to start drawing the item
     * @param stack             the item stack to draw
     * @param hasSpecialTooltip whether or not the item has a special tooltip
     */
    @SideOnly(Side.CLIENT)
    public void renderItem(int xPos, int yPos, ItemStack stack, boolean hasSpecialTooltip, boolean addPageLink) {
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
        boolean shouldShowTooltip = false;
        if (addPageLink) {
            int lengthBefore = pageLinks.size();
            PageLink link = new PageLink(xPos, yPos, 16, 16, stack);
            if (link.category != null)
                pageLinks.add(link);
            shouldShowTooltip = pageLinks.size() > lengthBefore;
        }
        if (!hasSpecialTooltip && mouseX >= xPos && mouseY >= yPos && mouseX <= xPos + 16 && mouseY <= yPos + 16) {
            if (stack != null) {
                List<String> tooltipData = stack.getTooltip(Minecraft.getMinecraft().thePlayer, false);
                List<String> parsedTooltip = new ArrayList();
                boolean first = true;
                if (addPageLink && shouldShowTooltip)
                    tooltipData.add("Open guide book entry");
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

    /**
     * Adds a special tooltip in case you want to
     *
     * @param xPos     the x coordinate to start drawing the tooltip
     * @param yPos     the y coordinate to start drawing the tooltip
     * @param stack    the item stack the tooltip is for
     * @param toolTips the tooltip lines
     */
    @SideOnly(Side.CLIENT)
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
