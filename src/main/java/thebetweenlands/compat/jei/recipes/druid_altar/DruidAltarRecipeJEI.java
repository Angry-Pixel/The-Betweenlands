package thebetweenlands.compat.jei.recipes.druid_altar;

import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.BlankRecipeWrapper;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.item.ItemStack;
import thebetweenlands.common.recipe.misc.DruidAltarRecipe;

import java.util.ArrayList;

public class DruidAltarRecipeJEI implements IRecipeWrapper {
    private ArrayList<ItemStack> inputs;
    private ItemStack output;
    public DruidAltarRecipeJEI(DruidAltarRecipe recipe){
        inputs = recipe.getInputs();
        output = recipe.getDefaultOutput();
    }

    @Override
    public void getIngredients(IIngredients ingredients) {
        ingredients.setInputs(ItemStack.class, inputs);
        ingredients.setOutput(ItemStack.class, output);
    }
}
