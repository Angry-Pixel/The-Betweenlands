package thebetweenlands.compat.jei.recipes.druid_altar;

import java.util.ArrayList;

import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.item.ItemStack;
import thebetweenlands.common.recipe.misc.DruidAltarRecipe;

public class DruidAltarRecipeJEI implements IRecipeWrapper {
    private ArrayList<ItemStack> inputs;
    private ItemStack output;
    public DruidAltarRecipeJEI(DruidAltarRecipe recipe){
        inputs = recipe.getInputs();
        output = recipe.getDefaultOutput();
    }

    @Override
    public void getIngredients(IIngredients ingredients) {
        ingredients.setInputs(VanillaTypes.ITEM, inputs);
        ingredients.setOutput(VanillaTypes.ITEM, output);
    }
}
