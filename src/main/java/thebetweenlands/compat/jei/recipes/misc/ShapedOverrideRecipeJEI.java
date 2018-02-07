package thebetweenlands.compat.jei.recipes.misc;

import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IStackHelper;
import mezz.jei.api.recipe.wrapper.ICraftingRecipeWrapper;
import mezz.jei.api.recipe.wrapper.IShapedCraftingRecipeWrapper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.recipe.ShapelessOverrideDummyRecipe;
import thebetweenlands.common.recipe.ShapelessOverrideDummyRecipe.ShapedOverrideDummyRecipe;

import javax.annotation.Nullable;
import java.util.List;

public class ShapedOverrideRecipeJEI extends ShapelessOverrideRecipeJEI implements IShapedCraftingRecipeWrapper {

    private final IJeiHelpers jeiHelpers;
    private int width;
    private int height;

    public ShapedOverrideRecipeJEI(IJeiHelpers jeiHelpers, ShapedOverrideDummyRecipe recipe) {
    	super(jeiHelpers, recipe);
        this.jeiHelpers = jeiHelpers;
        this.width = recipe.getRecipeWidth();
        this.height = recipe.getRecipeHeight();
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
