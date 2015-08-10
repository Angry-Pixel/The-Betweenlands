package thebetweenlands.manual.gui.widgets;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.manual.gui.GuiManualBase;
import thebetweenlands.recipes.CompostRecipe;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Bart on 9-8-2015.
 */
public class ComporstRecipeWidget extends ManualWidgetsBase {
    int currentRecipe;

    int width = 34;
    int height = 38;

    int untilUpdate = 0;

    public ComporstRecipeWidget(GuiManualBase manual, int xStart, int yStart) {
        super(manual, xStart, yStart);
        Random random = new Random();
        currentRecipe = random.nextInt(CompostRecipe.compostRecipes.size());
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void drawForeGround() {
        CompostRecipe recipe = CompostRecipe.compostRecipes.get(currentRecipe);

        manual.mc.renderEngine.bindTexture(icons);
        manual.drawTexturedModalRect(xStart + 18, yStart + 4, 0, 17, 16, 16);
        renderItem(xStart, yStart, new ItemStack(recipe.compostItem, 1, recipe.itemDamage), false);
        renderItem(xStart + 18, yStart + 22, new ItemStack(Item.getItemFromBlock(BLBlockRegistry.compostBin)), false);
        if (mouseX >= xStart + 18 && mouseX <= xStart + 34 && mouseY >= yStart + 4 && mouseY <= yStart + 20) {
            ArrayList<String> processTooltip = new ArrayList<>();
            processTooltip.add("Compost recipe");
            int minutes = (int) (recipe.compostTime / 20 / 60);
            int seconds = (int) ((recipe.compostTime / 20) % 60);
            processTooltip.add("Process time: " + minutes + " minutes" + (seconds > 0 ? " and " + seconds + " seconds" : ""));
            processTooltip.add("Compost amount: " + recipe.compostAmount + "%");
            renderTooltip(mouseX, mouseY, processTooltip, 0xffffff, 0xf0100010);
        }
    }

    public void mouseClicked(int x, int y, int mouseButton) {
        if (mouseButton == 2 && x >= xStart && x <= xStart + width && y >= yStart && y <= yStart + height) {
            if (currentRecipe + 1 < CompostRecipe.compostRecipes.size()) {
                currentRecipe++;
            } else
                currentRecipe = 0;
            drawForeGround();
            untilUpdate = 0;
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void updateScreen() {
        if (untilUpdate >= 20) {
            if (currentRecipe + 1 < CompostRecipe.compostRecipes.size()) {
                currentRecipe++;
            } else
                currentRecipe = 0;
            drawForeGround();
            untilUpdate = 0;
        } else
            untilUpdate++;
    }
}
