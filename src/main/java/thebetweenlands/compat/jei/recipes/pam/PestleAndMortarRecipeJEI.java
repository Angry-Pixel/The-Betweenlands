package thebetweenlands.compat.jei.recipes.pam;

import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.BlankRecipeWrapper;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.item.ItemStack;
import thebetweenlands.api.recipes.IPestleAndMortarRecipe;
import thebetweenlands.common.recipe.misc.PestleAndMortarRecipe;
import thebetweenlands.common.registries.ItemRegistry;

import java.util.ArrayList;

public class PestleAndMortarRecipeJEI implements IRecipeWrapper {
    private final ItemStack output;
    private final ItemStack input;

    public PestleAndMortarRecipeJEI(IPestleAndMortarRecipe recipe){
        input = recipe.getInputs();
        output = recipe.getOutput(input);
    }

    @Override
    public void getIngredients(IIngredients ingredients) {
        ArrayList<ItemStack> list = new ArrayList<>();
        list.add(input);
        list.add(new ItemStack(ItemRegistry.PESTLE));
        ingredients.setInputs(ItemStack.class, list);
        ingredients.setOutput(ItemStack.class, output);
    }
}
