package thebetweenlands.compat.jei.recipes.animator;

import mezz.jei.api.recipe.IRecipeHandler;
import mezz.jei.api.recipe.IRecipeWrapper;
import thebetweenlands.common.lib.ModInfo;

public class AnimatorRecipeHandler implements IRecipeHandler<AnimatorRecipeJEI> {
    @Override
    public Class<AnimatorRecipeJEI> getRecipeClass() {
        return AnimatorRecipeJEI.class;
    }

    @Override
    public String getRecipeCategoryUid(AnimatorRecipeJEI recipe) {
        return ModInfo.ID + ":animator";
    }

    @Override
    public IRecipeWrapper getRecipeWrapper(AnimatorRecipeJEI recipe) {
        return recipe;
    }

    @Override
    public boolean isRecipeValid(AnimatorRecipeJEI recipe) {
        return true;
    }
}
