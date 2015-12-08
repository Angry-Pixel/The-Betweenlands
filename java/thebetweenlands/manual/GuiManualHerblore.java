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
        xStart = width / 2 - 146;
        xStartRightPage = xStart + 146;
        yStart = (height - HEIGHT) / 2;
        untilUpdate = 0;
        changeCategory(HLEntryRegistry.aspectCategory);
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

}
