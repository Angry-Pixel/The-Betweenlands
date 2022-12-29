package thebetweenlands.common.herblore.book.widgets;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.api.aspect.IAspectType;
import thebetweenlands.common.herblore.book.GuiManualHerblore;
import thebetweenlands.common.herblore.book.HLEntryRegistry;
import thebetweenlands.common.herblore.book.ManualCategory;
import thebetweenlands.common.herblore.book.Page;
import thebetweenlands.common.registries.ItemRegistry;

@SideOnly(Side.CLIENT)
public class ManualWidgetBase {
    public static ResourceLocation icons = new ResourceLocation("thebetweenlands:textures/gui/manual/icons.png");
    public static String processTimeString = I18n.translateToLocal("manual.widget.process.time");
    public static String processTimeMinutesString = I18n.translateToLocal("manual.widget.process.time.minutes");
    public static String processTimeSecondsString = I18n.translateToLocal("manual.widget.process.time.seconds");
    public static String burnTimeString = I18n.translateToLocal("manual.widget.burn.time");
    public ArrayList<PageLink> pageLinks = new ArrayList<>();
    public GuiManualHerblore manual;
    public int unchangedXStart;
    public int unchangedYStart;
    public int xStart;
    public int yStart;
    public boolean isPageRight = false;
    public boolean isEmpty = false;
    protected int mouseX;
    protected int mouseY;

    @SideOnly(Side.CLIENT)
    public ManualWidgetBase(int xStart, int yStart) {
        this.unchangedXStart = xStart;
        this.unchangedYStart = yStart;
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

        int var7 = -12;
        if (!tooltipData.isEmpty()) {
            int var5 = 0;
            int var6;

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

            GlStateManager.disableDepth();
            for (int var13 = 0; var13 < tooltipData.size(); ++var13) {
                String var14 = tooltipData.get(var13);
                fontRenderer.drawStringWithShadow(var14, var6, var7, -1);
                if (var13 == 0)
                    var7 += 2;
                var7 += 10;
            }
            GlStateManager.enableDepth();
        }
        if (!lighting)
            RenderHelper.disableStandardItemLighting();
        GlStateManager.color(1F, 1F, 1F, 1F);
        return var7 + 12;
    }

    /**
     * To be honest I have no idea how this works
     *
     * @param left
     * @param top
     * @param z
     * @param right
     * @param bottom
     * @param startColor
     * @param endColor
     */
    @SideOnly(Side.CLIENT)
    public static void drawGradientRect(int left, int top, float z, int right, int bottom, int startColor, int endColor) {
        float var7 = (startColor >> 24 & 255) / 255F;
        float var8 = (startColor >> 16 & 255) / 255F;
        float var9 = (startColor >> 8 & 255) / 255F;
        float var10 = (startColor & 255) / 255F;
        float var11 = (endColor >> 24 & 255) / 255F;
        float var12 = (endColor >> 16 & 255) / 255F;
        float var13 = (endColor >> 8 & 255) / 255F;
        float var14 = (endColor & 255) / 255F;
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GlStateManager.shadeModel(GL11.GL_SMOOTH);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder vertexBuffer = tessellator.getBuffer();
        vertexBuffer.begin(7, DefaultVertexFormats.POSITION_COLOR);
        vertexBuffer.pos(right, top, z).color(var8, var9, var10, var7).endVertex();
        vertexBuffer.pos(left, top, z).color(var8, var9, var10, var7).endVertex();
        vertexBuffer.pos(left, bottom, z).color(var12, var13, var14, var11).endVertex();
        vertexBuffer.pos(right, bottom, z).color(var12, var13, var14, var11).endVertex();
        tessellator.draw();
        GlStateManager.shadeModel(GL11.GL_FLAT);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
    }

    @SideOnly(Side.CLIENT)
    public void init(GuiManualHerblore manual) {
        this.manual = manual;
        resize();
    }

    @SideOnly(Side.CLIENT)
    public void setPageToLeft() {
        this.isPageRight = false;
    }

    @SideOnly(Side.CLIENT)
    public void setPageToRight() {
        this.isPageRight = true;
    }

    @SideOnly(Side.CLIENT)
    public void draw(int mouseX, int mouseY) {
        this.mouseX = mouseX;
        this.mouseY = mouseY;
        drawBackGround();
        pageLinks.clear();
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
    public void drawToolTip() {
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
        if (mouseButton == 0) {
            for (PageLink link : pageLinks)
                if (x >= link.x && y >= link.y && x <= link.x + link.width && y <= link.y + link.height)
                    manual.changeCategory(link.category, link.pageNumber + link.category.getIndexPages());
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
    public void renderItem(int xPos, int yPos, ItemStack stack, boolean hasSpecialTooltip, boolean addPageLink, Item book) {
        RenderItem render = Minecraft.getMinecraft().getRenderItem();
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        RenderHelper.enableGUIStandardItemLighting();
        GlStateManager.enableRescaleNormal();
        GlStateManager.enableDepth();
        render.renderItemAndEffectIntoGUI(stack, xPos, yPos);
        render.renderItemOverlays(Minecraft.getMinecraft().fontRenderer, stack, xPos, yPos);
        RenderHelper.disableStandardItemLighting();
        GlStateManager.popMatrix();

        boolean shouldShowTooltip = false;
        if (addPageLink) {
            int lengthBefore = pageLinks.size();
            PageLink link = new PageLink(xPos, yPos, 16, 16, stack, book);
            if (link.category != null)
                pageLinks.add(link);
            shouldShowTooltip = pageLinks.size() > lengthBefore;
        }
        if (!hasSpecialTooltip && mouseX >= xPos && mouseY >= yPos && mouseX <= xPos + 16 && mouseY <= yPos + 16) {
            if (!stack.isEmpty()) {
                List<String> tooltipData = stack.getTooltip(Minecraft.getMinecraft().player, ITooltipFlag.TooltipFlags.NORMAL);
                List<String> parsedTooltip = new ArrayList<>();
                boolean first = true;
                if (addPageLink && shouldShowTooltip)
                    tooltipData.add(I18n.canTranslate("manual.open_entry") ? I18n.translateToLocal("manual.open_entry"): I18n.translateToFallback("manual.open_entry"));
                for (String s : tooltipData) {
                    String s_ = s;
                    if (!first)
                        s_ = TextFormatting.GRAY + s;
                    parsedTooltip.add(s_);
                    first = false;
                }
                renderTooltip(mouseX, mouseY, parsedTooltip, 0xffffff, 0xf0100010);
            }
        }
        GlStateManager.enableAlpha();
        GlStateManager.color(1F, 1F, 1F, 1F);
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
                List<String> tooltipData = stack.getTooltip(Minecraft.getMinecraft().player, ITooltipFlag.TooltipFlags.NORMAL);
                List<String> parsedTooltip = new ArrayList<>();
                boolean first = true;

                for (String tip : toolTips)
                    tooltipData.add(tip);

                for (String s : tooltipData) {
                    String s_ = s;
                    if (!first)
                        s_ = TextFormatting.GRAY + s;
                    parsedTooltip.add(s_);
                    first = false;
                }
                renderTooltip(mouseX, mouseY, parsedTooltip, 0xffffff, 0xf0100010);
            }
        }
    }

    public static class PageLink {
        public int x;
        public int y;
        public int width;
        public int height;
        public int pageNumber;
        public ManualCategory category;

        public PageLink(int x, int y, int width, int height, ItemStack item, Item book) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            if (item != null) {
                if (book == ItemRegistry.MANUAL_HL) {
                    for (ManualCategory category : HLEntryRegistry.CATEGORIES) {
                        for (Page page : category.getVisiblePages()) {
                            if (page.pageItems.size() > 0) {
                                for (ItemStack stack : page.pageItems) {
                                    if (stack != null && stack.getItem() == item.getItem() && (stack.getItemDamage() == item.getItemDamage() || stack.getItemDamage() == item.getItemDamage() - 1)) {
                                        this.pageNumber = page.pageNumber;
                                        this.category = category;
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }


        public PageLink(int x, int y, int width, int height, IAspectType aspectType) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            if (aspectType != null) {
                for (Page page : HLEntryRegistry.aspectCategory.getVisiblePages()) {
                    if (page.pageAspects.size() > 0) {
                        if (page.pageAspects.contains(aspectType)) {
                            this.pageNumber = page.pageNumber;
                            this.category = HLEntryRegistry.aspectCategory;
                            break;
                        }
                    }
                }
            }
        }

        @Override
        public String toString() {
            return "page number: " + pageNumber + ", category name: " + category.getName() + ", xStart: " + x + ", yStart: " + y + ", width: " + width + ", height: " + height;
        }

    }
}
