package thebetweenlands.compat.jei.recipes.druid_altar;

import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.BlankRecipeWrapper;
import net.minecraft.item.ItemStack;
import thebetweenlands.common.recipe.misc.DruidAltarRecipe;

import java.util.ArrayList;

public class DruidAltarRecipeJEI extends BlankRecipeWrapper {
    private ArrayList<ItemStack> inputs;
    private ItemStack output;
    public DruidAltarRecipeJEI(DruidAltarRecipe recipe){
        inputs = recipe.getInputs();
        output = recipe.getOutput();
    }

    @Override
    public void getIngredients(IIngredients ingredients) {
        ingredients.setInputs(ItemStack.class, inputs);
        ingredients.setOutput(ItemStack.class, output);
    }
}
