package thebetweenlands.compat.jei.recipes.compost;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.item.ItemStack;
import thebetweenlands.common.item.misc.ItemMisc;
import thebetweenlands.common.recipe.misc.CompostRecipe;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.util.TranslationHelper;

public class CompostRecipeJEI implements IRecipeWrapper {
    private ItemStack input;
    private int compostAmount;
    private int compostTime;

    public static String processTimeString = "jei.thebetweenlands.time";
    public static String processTimeMinutesString = "jei.thebetweenlands.time.minutes";
    public static String processTimeSecondsString = "jei.thebetweenlands.time.seconds";

    public CompostRecipeJEI(CompostRecipe recipe) {
        this.input = recipe.getInput();
        this.compostAmount = recipe.getCompostAmount(input);
        this.compostTime = recipe.getCompostingTime(input);
    }

    @Override
    public void getIngredients(IIngredients ingredients) {
        ingredients.setInput(VanillaTypes.ITEM, input);
        ingredients.setOutput(VanillaTypes.ITEM, new ItemStack(ItemRegistry.ITEMS_MISC, 1, ItemMisc.EnumItemMisc.COMPOST.getID()));
    }

    @Nullable
    @Override
    public List<String> getTooltipStrings(int mouseX, int mouseY) {
        ArrayList<String> processTooltip = new ArrayList<>();
        if (mouseX >= 18 && mouseX <= 39 && mouseY >= 3 && mouseY <= 18) {
            int minutes = (compostTime / 20) / 60;
            int seconds = (compostTime / 20) % 60;

            processTooltip.add(seconds > 0 && minutes > 0 ? TranslationHelper.translateToLocal(processTimeString, minutes, seconds) : minutes > 0? TranslationHelper.translateToLocal(processTimeMinutesString, minutes):TranslationHelper.translateToLocal(processTimeSecondsString, seconds));
            processTooltip.add(TranslationHelper.translateToLocal("jei.thebetweenlands.compost.amount", compostAmount));
        }
        return processTooltip;
    }
}
