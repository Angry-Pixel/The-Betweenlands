package thebetweenlands.compat.jei.recipes.pam;

import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.BlankRecipeWrapper;
import net.minecraft.item.ItemStack;
import thebetweenlands.common.recipe.misc.PestleAndMortarRecipe;
import thebetweenlands.common.registries.ItemRegistry;

import java.util.ArrayList;

public class PestleAndMortarRecipeJEI extends BlankRecipeWrapper {
    private final ItemStack output;
    private final ItemStack input;

    public PestleAndMortarRecipeJEI(PestleAndMortarRecipe recipe){
        output = recipe.getOutput();
        input = recipe.getInputs();
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
