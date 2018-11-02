package thebetweenlands.compat.jei.recipes.misc;

import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.recipe.wrapper.IShapedCraftingRecipeWrapper;
import thebetweenlands.common.recipe.ShapelessOverrideDummyRecipe.ShapedOverrideDummyRecipe;

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
