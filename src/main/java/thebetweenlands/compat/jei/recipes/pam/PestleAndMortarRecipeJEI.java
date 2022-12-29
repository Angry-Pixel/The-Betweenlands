package thebetweenlands.compat.jei.recipes.pam;

import java.util.ArrayList;

import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.item.ItemStack;
import thebetweenlands.api.recipes.IPestleAndMortarRecipe;
import thebetweenlands.common.registries.ItemRegistry;

public class PestleAndMortarRecipeJEI implements IRecipeWrapper {
    private final ItemStack output;
    private final ItemStack input;

    public PestleAndMortarRecipeJEI(IPestleAndMortarRecipe recipe){
        input = recipe.getInputs();
        output = recipe.getOutput(input, ItemStack.EMPTY);
    }

    @Override
    public void getIngredients(IIngredients ingredients) {
        ArrayList<ItemStack> list = new ArrayList<>();
        list.add(input);
        list.add(new ItemStack(ItemRegistry.PESTLE));
        ingredients.setInputs(VanillaTypes.ITEM, list);
        ingredients.setOutput(VanillaTypes.ITEM, output);
    }
}
