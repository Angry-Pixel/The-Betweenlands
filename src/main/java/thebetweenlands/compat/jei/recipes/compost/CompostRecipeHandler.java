package thebetweenlands.compat.jei.recipes.compost;

import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeHandler;
import mezz.jei.api.recipe.IRecipeWrapper;
import thebetweenlands.common.lib.ModInfo;

public class CompostRecipeHandler implements IRecipeHandler<CompostRecipeJEI> {
    @Override
    public Class<CompostRecipeJEI> getRecipeClass() {
        return CompostRecipeJEI.class;
    }

    @Override
    public String getRecipeCategoryUid(CompostRecipeJEI recipe) {
        return ModInfo.ID + ":compost";
    }

    @Override
    public IRecipeWrapper getRecipeWrapper(CompostRecipeJEI recipe) {
        return recipe;
    }

    @Override
    public boolean isRecipeValid(CompostRecipeJEI recipe) {

        return true;
    }
}
