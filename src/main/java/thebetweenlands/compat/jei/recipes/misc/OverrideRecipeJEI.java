package thebetweenlands.compat.jei.recipes.misc;

import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IStackHelper;
import mezz.jei.api.recipe.wrapper.ICraftingRecipeWrapper;
import mezz.jei.api.recipe.wrapper.IShapedCraftingRecipeWrapper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.recipe.OverrideDummyRecipe;

import javax.annotation.Nullable;
import java.util.List;

public class OverrideRecipeJEI implements ICraftingRecipeWrapper, IShapedCraftingRecipeWrapper {

    private final IJeiHelpers jeiHelpers;
    protected final OverrideDummyRecipe recipe;
    private int width;
    private int height;

    public OverrideRecipeJEI(IJeiHelpers jeiHelpers, OverrideDummyRecipe recipe) {
        this.jeiHelpers = jeiHelpers;
        this.recipe = recipe;
        for(int i = 1; i <= 3; i++) {
            width = height = i;
            if (recipe.canFit(i, i))
                break;
            else if (recipe.canFit(i, i + 1)) {
                height = i + 1;
                break;
            } else if (recipe.canFit(i + 1, i)) {
                height = i;
                break;
            }
        }
    }

    @Override
    public void getIngredients(IIngredients ingredients) {
        ItemStack recipeOutput = recipe.getRecipeOutput();
        IStackHelper stackHelper = jeiHelpers.getStackHelper();

        try {
            List<List<ItemStack>> inputLists = stackHelper.expandRecipeItemStackInputs(recipe.getIngredients());
            ingredients.setInputLists(ItemStack.class, inputLists);
            ingredients.setOutput(ItemStack.class, recipeOutput);
        } catch (RuntimeException e) {
            TheBetweenlands.logger.error("Problem showing recipe in JEI for: " + recipe.getIngredients());
        }
    }

    @Nullable
    @Override
    public ResourceLocation getRegistryName() {
        return recipe.getRegistryName();
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }
}
