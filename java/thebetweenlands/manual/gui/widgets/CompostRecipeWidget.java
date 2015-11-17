package thebetweenlands.manual.gui.widgets;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.items.BLItemRegistry;
import thebetweenlands.items.misc.ItemGeneric;
import thebetweenlands.manual.gui.GuiManualBase;
import thebetweenlands.recipes.CompostRecipe;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Bart on 9-8-2015.
 */
public class CompostRecipeWidget extends ManualWidgetsBase {
    int currentRecipe;

    public static int width = 62;
    public static int height = 38;

    int untilUpdate = 0;

    public CompostRecipeWidget(GuiManualBase manual, int xStart, int yStart) {
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
        manual.drawTexturedModalRect(xStart + 38, yStart + 22, 0, 0, 22, 16);
        renderItem(xStart, yStart, new ItemStack(recipe.compostItem, 1, recipe.itemDamage), false);
        renderItem(xStart + 18, yStart + 22, new ItemStack(Item.getItemFromBlock(BLBlockRegistry.compostBin)), false);
        renderItem(xStart + 62, yStart + 22, ItemGeneric.createStack(ItemGeneric.EnumItemGeneric.COMPOST), false);
        if (mouseX >= xStart + 18 && mouseX <= xStart + 34 && mouseY >= yStart + 4 && mouseY <= yStart + 20) {
            ArrayList<String> processTooltip = new ArrayList<>();
            processTooltip.add(StatCollector.translateToLocal("manual.widget.compost.recipe"));
            int minutes = (recipe.compostTime / 20) / 60;
            int seconds = (recipe.compostTime / 20) % 60;
            processTooltip.add(seconds > 0?processTimeString.replace(".minutes.", "" + minutes).replace(".seconds.", "" + seconds):processTimeMinutesString.replace(".minutes.", "" + minutes));
            processTooltip.add(StatCollector.translateToLocal("manual.widget.compost.amount").replace(".amount.", "" + recipe.compostAmount));
            renderTooltip(mouseX, mouseY, processTooltip, 0xffffff, 0xf0100010);
        }
        if (mouseX >= xStart + 38 && mouseX <= xStart + 60 && mouseY >= yStart + 22 && mouseY <= yStart + 38){
            ArrayList<String> processTooltip = new ArrayList<>();
            processTooltip.add(StatCollector.translateToLocal("manual.widget.compost.amount.per"));
            renderTooltip(mouseX, mouseY, processTooltip, 0xffffff, 0xf0100010);
        }
    }

    @Override
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
