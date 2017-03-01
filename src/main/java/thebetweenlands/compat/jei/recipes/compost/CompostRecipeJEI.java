package thebetweenlands.compat.jei.recipes.compost;

import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.BlankRecipeWrapper;
import net.minecraft.item.ItemStack;
import thebetweenlands.api.recipes.ICompostBinRecipe;
import thebetweenlands.common.item.misc.ItemMisc;
import thebetweenlands.common.recipe.misc.CompostRecipe;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.util.TranslationHelper;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class CompostRecipeJEI extends BlankRecipeWrapper {
    private ItemStack input;
    private int compostAmount;
    private int compostTime;

    public static String processTimeString = "manual.widget.process.time";
    public static String processTimeMinutesString = "manual.widget.process.time.minutes";

    public CompostRecipeJEI(CompostRecipe recipe) {
        this.input = recipe.getInput();
        this.compostAmount = recipe.getCompostAmount(input);
        this.compostTime = recipe.getCompostingTime(input);
    }

    @Override
    public void getIngredients(IIngredients ingredients) {
        ingredients.setInput(ItemStack.class, input);
        ingredients.setOutput(ItemStack.class, new ItemStack(ItemRegistry.ITEMS_MISC, 1, ItemMisc.EnumItemMisc.COMPOST.getID()));
    }

    @Nullable
    @Override
    public List<String> getTooltipStrings(int mouseX, int mouseY) {
        ArrayList<String> processTooltip = new ArrayList<>();
        if (mouseX >= 18 && mouseX <= 39 && mouseY >= 3 && mouseY <= 18) {
            int minutes = (compostTime / 20) / 60;
            int seconds = (compostTime / 20) % 60;

            processTooltip.add(seconds > 0 ? String.format(TranslationHelper.translateToLocal(processTimeString), minutes, seconds) : String.format(TranslationHelper.translateToLocal(processTimeMinutesString), minutes));
            processTooltip.add(String.format(TranslationHelper.translateToLocal("manual.widget.compost.amount"), compostAmount));
        }
        return processTooltip;
    }
}
