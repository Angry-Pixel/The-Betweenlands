package thebetweenlands.manual;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

/**
 * Created by Bart on 06/12/2015.
 */
public class GuiManualHerblore extends GuiManualBase {
    private static ResourceLocation book = new ResourceLocation("thebetweenlands:textures/gui/manual/manual.png");

    public GuiManualHerblore(EntityPlayer player) {
        super(player);
    }


    @Override
    public void initGui() {
        manualType = ManualManager.EnumManual.HL;
        xStart = width / 2 - 146;
        xStartRightPage = xStart + 146;
        yStart = (height - HEIGHT) / 2;
        untilUpdate = 0;
        changeCategory(ManualManager.getCurrentCategory(manualType, player), ManualManager.getCurrentPageNumber(manualType, player));
    }


    @Override
    @SideOnly(Side.CLIENT)
    public void drawScreen(int mouseX, int mouseY, float renderPartials) {
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
            drawTexture(xStart + 279 , yStart + 10 + 22 * currentCategory.number, 14, 44 - 22 * currentCategory.number, 512.0D, 512.0D, 306.0D, 293.0D, 18.0D + 22.0D * currentCategory.number, 62.0D);

            currentCategory.draw(mouseX, mouseY);
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int button) {
        if (currentCategory != null) {
            if (mouseX >= xStart + 15 && mouseX <= xStart + 15 + 19 && mouseY >= yStart + 160 && mouseY <= yStart + 160 + 8 && button == 0) {
                currentCategory.previousPage(this);
                ManualManager.setCurrentPage(currentCategory.name, currentCategory.currentPage, manualType, player);
            }
            if (mouseX >= xStart + 256 && mouseX <= xStart + 256 + 19 && mouseY >= yStart + 160 && mouseY <= yStart + 160 + 8 && button == 0) {
                currentCategory.nextPage(this);
                ManualManager.setCurrentPage(currentCategory.name, currentCategory.currentPage, manualType, player);
            }
            if (mouseX >= xStart + (currentCategory.number >= 1?0:279) && mouseX <= xStart + (currentCategory.number >= 1?0:279) + 14 && mouseY >= yStart + 11 && mouseY <= yStart + 10 + 20 && button == 0)
                changeCategory(HLEntryRegistry.aspectCategory);
            //if (mouseX >= xStart + (currentCategory.number >= 2?0:279) && mouseX <= xStart + (currentCategory.number >= 2?0:279) + 14 && mouseY >= yStart + 33 && mouseY <= yStart + 32 + 20 && button == 0)
            //    changeCategory(GuideBookEntryRegistry.machineCategory);
            currentCategory.mouseClicked(mouseX, mouseY, button);
        }
    }

}
