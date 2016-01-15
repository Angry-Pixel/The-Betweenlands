package thebetweenlands.manual;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;
import thebetweenlands.items.BLItemRegistry;

/**
 * Created by Bart on 22/11/2015.
 */
public class GuiManualBase extends GuiScreen {
    private static ResourceLocation book = new ResourceLocation("thebetweenlands:textures/gui/manual/manual.png");
    public int xStart;
    public int xStartRightPage;
    public int yStart;
    public int WIDTH = 292;
    public int HEIGHT = 180;
    public EntityPlayer player;

    public int untilUpdate = 0;
    public ManualCategory currentCategory;

    public Item manualType;

    public GuiManualBase(EntityPlayer player) {
        this.player = player;
    }

    @Override
    public void initGui() {
        manualType = BLItemRegistry.manualGuideBook;
        xStart = width / 2 - 146;
        xStartRightPage = xStart + 146;
        yStart = (height - HEIGHT) / 2;
        untilUpdate = 0;
        changeCategory(ManualManager.getCurrentCategory(manualType, player), ManualManager.getCurrentPageNumber(manualType, player));
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
        ManualManager.setCurrentPage(currentCategory.name, currentCategory.currentPage, manualType, player);
        ManualManager.setCurrentPage(currentCategory.name, currentCategory.currentPage, manualType, player);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void drawScreen(int mouseX, int mouseY, float renderPartials) {
        super.drawScreen(mouseX, mouseY, renderPartials);

        mc.renderEngine.bindTexture(book);

        drawTexture(xStart, yStart, WIDTH, HEIGHT, 512.0D, 512.0D, 0.0D, 292.0D, 0.0D, 180.0D);
        if (currentCategory != null) {
            if (currentCategory.currentPage - 2 >= 1) {
                if (mouseX >= xStart + 15 && mouseX <= xStart + 15 + 19 && mouseY >= yStart + 160 && mouseY <= yStart + 160 + 8)
                    drawTexture(xStart + 15, yStart + 160, 19, 8, 512.0D, 512.0D, 292.0D, 311.0D, 9.0D, 18.0D);
                else
                    drawTexture(xStart + 15, yStart + 160, 19, 8, 512.0D, 512.0D, 292.0D, 311.0D, 0.0D, 9.0D);
            }
            if (currentCategory.currentPage + 2 <= currentCategory.visiblePages.size()) {
                if (mouseX >= xStart + 256 && mouseX <= xStart + 256 + 19 && mouseY >= yStart + 160 && mouseY <= yStart + 160 + 8)
                    drawTexture(xStart + 256, yStart + 160, 19, 8, 512.0D, 512.0D, 311.0D, 330.0D, 9.0D, 18.0D);
                else
                    drawTexture(xStart + 256, yStart + 160, 19, 8, 512.0D, 512.0D, 311.0D, 330.0D, 0.0D, 9.0D);
            }
            drawTexture(xStart, yStart + 10, 14, 22 * currentCategory.number, 512.0D, 512.0D, 293.0D, 306.0D, 18.0D, 18.0D + 22.0D * currentCategory.number);
            drawTexture(xStart + 279, yStart + 10 + 22 * currentCategory.number, 14, 154 - 22 * currentCategory.number, 512.0D, 512.0D, 306.0D, 293.0D, 18.0D + 22.0D * currentCategory.number, 174.0D);

            currentCategory.draw(mouseX, mouseY);
        }
    }

    /**
     * Drawing a scalable texture
     *
     * @param xStart        the x coordinate to start drawing
     * @param yStart        the y coordinate to start drawing
     * @param width         the width for drawing
     * @param height        the height for drawing
     * @param textureWidth  the width of the texture
     * @param textureHeight the height of the texture
     * @param textureXStart the x start in the texture
     * @param textureXEnd   the x end in the texture
     * @param textureYStart the y start in the texture
     * @param textureYEnd   the y end in the texture
     */
    public void drawTexture(int xStart, int yStart, int width, int height, double textureWidth, double textureHeight, double textureXStart, double textureXEnd, double textureYStart, double textureYEnd) {
        double umin = 1.0D / textureWidth * textureXStart;
        double umax = 1.0D / textureWidth * textureXEnd;
        double vmin = 1.0D / textureHeight * textureYStart;
        double vmax = 1.0D / textureHeight * textureYEnd;
        Tessellator.instance.startDrawingQuads();
        Tessellator.instance.addVertexWithUV(xStart, yStart, 0, umin, vmin);
        Tessellator.instance.addVertexWithUV(xStart, yStart + height, 0, umin, vmax);
        Tessellator.instance.addVertexWithUV(xStart + width, yStart + height, 0, umax, vmax);
        Tessellator.instance.addVertexWithUV(xStart + width, yStart, 0, umax, vmin);
        Tessellator.instance.draw();
    }


    @Override
    protected void keyTyped(char c, int key) {
        switch (key) {
            case Keyboard.KEY_ESCAPE: {
                mc.displayGuiScreen(null);
                break;
            }
        }
        if (currentCategory != null)
            currentCategory.keyTyped(c, key);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int button) {
        if (currentCategory != null) {
            if (mouseX >= xStart + 15 && mouseX <= xStart + 15 + 19 && mouseY >= yStart + 160 && mouseY <= yStart + 160 + 8 && button == 0) {
                currentCategory.previousPage(this);
            }
            if (mouseX >= xStart + 256 && mouseX <= xStart + 256 + 19 && mouseY >= yStart + 160 && mouseY <= yStart + 160 + 8 && button == 0) {
                currentCategory.nextPage(this);
            }
            if (mouseX >= xStart + (currentCategory.number >= 1 ? 0 : 279) && mouseX <= xStart + (currentCategory.number >= 1 ? 0 : 279) + 14 && mouseY >= yStart + 11 && mouseY <= yStart + 10 + 20 && button == 0)
                changeCategory(GuideBookEntryRegistry.itemsCategory);
            if (mouseX >= xStart + (currentCategory.number >= 2 ? 0 : 279) && mouseX <= xStart + (currentCategory.number >= 2 ? 0 : 279) + 14 && mouseY >= yStart + 33 && mouseY <= yStart + 32 + 20 && button == 0)
                changeCategory(GuideBookEntryRegistry.machineCategory);
            if (mouseX >= xStart + (currentCategory.number >= 3 ? 0 : 279) && mouseX <= xStart + (currentCategory.number >= 3 ? 0 : 279) + 14 && mouseY >= yStart + 55 && mouseY <= yStart + 54 + 20 && button == 0)
                changeCategory(GuideBookEntryRegistry.entitiesCategory);
            if (mouseX >= xStart + (currentCategory.number >= 4 ? 0 : 279) && mouseX <= xStart + (currentCategory.number >= 4 ? 0 : 279) + 14 && mouseY >= yStart + 77 && mouseY <= yStart + 76 + 20 && button == 0)
                changeCategory(GuideBookEntryRegistry.category4);
            if (mouseX >= xStart + (currentCategory.number >= 5 ? 0 : 279) && mouseX <= xStart + (currentCategory.number >= 5 ? 0 : 279) + 14 && mouseY >= yStart + 99 && mouseY <= yStart + 98 + 20 && button == 0)
                changeCategory(GuideBookEntryRegistry.category5);
            if (mouseX >= xStart + (currentCategory.number >= 6 ? 0 : 279) && mouseX <= xStart + (currentCategory.number >= 6 ? 0 : 279) + 14 && mouseY >= yStart + 121 && mouseY <= yStart + 120 + 20 && button == 0)
                changeCategory(GuideBookEntryRegistry.category6);
            if (mouseX >= xStart + (currentCategory.number >= 7 ? 0 : 279) && mouseX <= xStart + (currentCategory.number >= 7 ? 0 : 279) + 14 && mouseY >= yStart + 143 && mouseY <= yStart + 142 + 20 && button == 0)
                changeCategory(GuideBookEntryRegistry.category7);

            currentCategory.mouseClicked(mouseX, mouseY, button);
        }
    }

    /**
     * Change to a page
     *
     * @param pageNumber the number of a page
     */
    public void changeTo(int pageNumber) {
        currentCategory.setPage(pageNumber + currentCategory.indexPages, this);
    }

    /**
     * Change the current category
     *
     * @param category a guidebook category
     */
    public void changeCategory(ManualCategory category) {
        currentCategory = category;
        currentCategory.init(this, true);
        currentCategory.setPage(1, this);
    }

    /**
     * Change the current category and page
     *
     * @param category a guidebook category
     * @param page     the number of a page
     */
    public void changeCategory(ManualCategory category, int page) {
        currentCategory = category;
        currentCategory.init(this, false);
        currentCategory.setPage(page, this);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void updateScreen() {
        untilUpdate++;
        if (currentCategory != null)
            currentCategory.updateScreen();
    }

    public boolean matches(ItemStack itemStack1, ItemStack itemStack2) {
        return itemStack2.getItem() == itemStack1.getItem() && itemStack2.getItemDamage() == itemStack1.getItemDamage();
    }
}
