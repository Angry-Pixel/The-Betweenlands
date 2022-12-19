package thebetweenlands.compat.jei.recipes.misc;

import java.util.List;

import javax.annotation.Nullable;

import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IStackHelper;
import mezz.jei.api.recipe.wrapper.ICraftingRecipeWrapper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.recipe.ShapelessOverrideDummyRecipe;

public class ShapelessOverrideRecipeJEI implements ICraftingRecipeWrapper {

    private final IJeiHelpers jeiHelpers;
    protected final ShapelessOverrideDummyRecipe recipe;

    public ShapelessOverrideRecipeJEI(IJeiHelpers jeiHelpers, ShapelessOverrideDummyRecipe recipe) {
        this.jeiHelpers = jeiHelpers;
        this.recipe = recipe;
    }

    @Override
    public void getIngredients(IIngredients ingredients) {
        ItemStack recipeOutput = recipe.getRecipeOutput();
        IStackHelper stackHelper = jeiHelpers.getStackHelper();

        try {
            List<List<ItemStack>> inputLists = stackHelper.expandRecipeItemStackInputs(recipe.getIngredients());
            ingredients.setInputLists(VanillaTypes.ITEM, inputLists);
            ingredients.setOutput(VanillaTypes.ITEM, recipeOutput);
        } catch (RuntimeException e) {
            TheBetweenlands.logger.error("Problem showing recipe in JEI for: " + recipe.getIngredients(), e);
        }
    }

    @Nullable
    @Override
    public ResourceLocation getRegistryName() {
        return recipe.getRegistryName();
    }
}
