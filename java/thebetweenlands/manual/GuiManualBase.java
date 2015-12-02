package thebetweenlands.manual;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;

import java.util.UUID;

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
    public UUID player;

    public int untilUpdate = 0;
    public ManualCategory currentCategory;

    public GuiManualBase(EntityPlayer player) {
        this.player = player.getUniqueID();
    }

    @Override
    public void initGui() {
        xStart = width / 2 - 146;
        xStartRightPage = xStart + 146;
        yStart = (height - HEIGHT) / 2;
        untilUpdate = 0;
        changeCategory(ManualEntryRegistry.itemsCategory);
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
            drawTexture(xStart + 279 , yStart + 10 + 22 * currentCategory.number, 14, 154 - 22 * currentCategory.number, 512.0D, 512.0D, 306.0D, 293.0D, 18.0D + 22.0D * currentCategory.number, 174.0D);

            currentCategory.draw(mouseX, mouseY);
        }
    }


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
            if (mouseX >= xStart + 15 && mouseX <= xStart + 15 + 19 && mouseY >= yStart + 160 && mouseY <= yStart + 160 + 8 && button == 0)
                currentCategory.previousPage(this);
            if (mouseX >= xStart + 256 && mouseX <= xStart + 256 + 19 && mouseY >= yStart + 160 && mouseY <= yStart + 160 + 8 && button == 0)
                currentCategory.nextPage(this);
            if (mouseX >= xStart + (currentCategory.number >= 1?0:279) && mouseX <= xStart + (currentCategory.number >= 1?0:279) + 14 && mouseY >= yStart + 11 && mouseY <= yStart + 10 + 20 && button == 0)
                changeCategory(ManualEntryRegistry.itemsCategory);
            if (mouseX >= xStart + (currentCategory.number >= 2?0:279) && mouseX <= xStart + (currentCategory.number >= 2?0:279) + 14 && mouseY >= yStart + 33 && mouseY <= yStart + 32 + 20 && button == 0)
                changeCategory(ManualEntryRegistry.machineCategory);
            if (mouseX >= xStart + (currentCategory.number >= 3?0:279) && mouseX <= xStart + (currentCategory.number >= 3?0:279) + 14 && mouseY >= yStart + 55 && mouseY <= yStart + 54 + 20 && button == 0)
               changeCategory(ManualEntryRegistry.entitiesCategory);
//            if (mouseX >= xStart + (currentCategory.number >= 4?0:279) && mouseX <= xStart + (currentCategory.number >= 4?0:279) + 14 && mouseY >= yStart + 77 && mouseY <= yStart + 76 + 20 && button == 0)
//                changeCategory(ManualEntryRegistry.category4);
//            if (mouseX >= xStart + (currentCategory.number >= 5?0:279) && mouseX <= xStart + (currentCategory.number >= 5?0:279) + 14 && mouseY >= yStart + 99 && mouseY <= yStart + 98 + 20 && button == 0)
//                changeCategory(ManualEntryRegistry.category5);
//            if (mouseX >= xStart + (currentCategory.number >= 6?0:279) && mouseX <= xStart + (currentCategory.number >= 6?0:279) + 14 && mouseY >= yStart + 131 && mouseY <= yStart + 130 + 20 && button == 0)
//                changeCategory(ManualEntryRegistry.category6);
//            if (mouseX >= xStart + (currentCategory.number >= 7?0:279) && mouseX <= xStart + (currentCategory.number >= 7?0:279) + 14 && mouseY >= yStart + 153 && mouseY <= yStart + 152 + 20 && button == 0)
//                changeCategory(ManualEntryRegistry.category7);

            currentCategory.mouseClicked(mouseX, mouseY, button);
        }
    }

    public void changeCategory(ManualCategory category){
        currentCategory = category;
        currentCategory.init(this, true);
        currentCategory.setPage(1, this);
    }

    public void changeCategory(ManualCategory category, int page){
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


    public void changeTo(int pageNumber) {
        currentCategory.setPage(pageNumber + currentCategory.indexPages, this);
    }

    public boolean matches(ItemStack itemStack1, ItemStack itemStack2) {
        return itemStack2.getItem() == itemStack1.getItem() && itemStack2.getItemDamage() == itemStack1.getItemDamage();
    }
}
