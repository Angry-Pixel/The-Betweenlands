package thebetweenlands.manual.gui.widgets;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import org.lwjgl.opengl.GL11;
import thebetweenlands.items.BLItemRegistry;
import thebetweenlands.manual.gui.GuiManualBase;
import thebetweenlands.recipes.PestleAndMortarRecipe;

import java.util.ArrayList;

/**
 * Created by Bart on 9-8-2015.
 */
public class PestleAndMortarRecipeWidget extends ManualWidgetsBase {
    private static ResourceLocation pamGrid = new ResourceLocation("thebetweenlands:textures/gui/manual/pamGrid.png");

    ArrayList<ItemStack> outputs = new ArrayList<>();

    public static int width = 106;
    public static int height = 69;

    int progress = 0;
    int untilUpdate = 0;
    int currentRecipe = 0;

    public PestleAndMortarRecipeWidget(GuiManualBase manual, ItemStack output, int xStart, int yStart) {
        super(manual, xStart, yStart);
        if (PestleAndMortarRecipe.getInput(output) != null)
            outputs.add(output);
    }

    public PestleAndMortarRecipeWidget(GuiManualBase manual, ArrayList<ItemStack> outputs, int xStart, int yStart) {
        super(manual, xStart, yStart);
        for (ItemStack output : outputs)
            if (PestleAndMortarRecipe.getInput(output) != null)
                this.outputs.add(output);
    }


    @Override
    @SideOnly(Side.CLIENT)
    public void drawForeGround() {
        if (outputs.size() > 0) {
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            manual.mc.renderEngine.bindTexture(pamGrid);
            manual.drawTexturedModalRect(xStart, yStart, 0, 0, width, height);
            manual.mc.renderEngine.bindTexture(icons);
            manual.drawTexturedModalRect(xStart + 11, yStart + 62, 172, 250, progress * 4, 6);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

            renderItem(xStart + 1, yStart + 29, PestleAndMortarRecipe.getInput(outputs.get(currentRecipe)), false);
            renderItem(xStart + 89, yStart + 29, outputs.get(currentRecipe), false);
            renderItem(xStart + 45, yStart + 1, new ItemStack(BLItemRegistry.lifeCrystal), true);
            ArrayList<String> extraToolTips = new ArrayList<>();
            extraToolTips.add(StatCollector.translateToLocal("manual.widget.pam.optional"));
            addSpecialItemTooltip(xStart + 45, yStart + 1, new ItemStack(BLItemRegistry.lifeCrystal), extraToolTips);
            renderItem(xStart + 45, yStart + 29, new ItemStack(BLItemRegistry.pestle), false);
            if (mouseX >= xStart + 11 && mouseX <= xStart + 85 && mouseY >= yStart + 62 && mouseY <= yStart + 68) {
                ArrayList<String> recipeToolTips = new ArrayList<>();
                recipeToolTips.add(StatCollector.translateToLocal("manual.widget.pam.recipe"));
                renderTooltip(mouseX, mouseY, recipeToolTips, 0xffffff, 0xf0100010);
            }
        } else
            isEmpty = true;
    }


    @Override
    @SideOnly(Side.CLIENT)
    public void updateScreen() {
        if (untilUpdate >= 20) {
            if (progress <= 21) {
                progress++;
                drawForeGround();
            } else {
                progress = 0;
                if (currentRecipe + 1 < outputs.size())
                    currentRecipe++;
                else
                    currentRecipe = 0;
            }
            untilUpdate = 0;
        } else untilUpdate++;
    }

    @Override
    public void mouseClicked(int x, int y, int mouseButton) {
        if (mouseButton == 2 && x >= xStart && x <= xStart + width && y >= yStart && y <= yStart + height) {
            if (currentRecipe + 1 < outputs.size()) {
                currentRecipe++;
            } else
                currentRecipe = 0;
            drawForeGround();
            untilUpdate = 0;
            progress = 0;
        }
    }
}
