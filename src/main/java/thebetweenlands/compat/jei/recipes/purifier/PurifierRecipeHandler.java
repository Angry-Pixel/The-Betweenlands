package thebetweenlands.compat.jei.recipes.purifier;

import mezz.jei.api.recipe.IRecipeHandler;
import mezz.jei.api.recipe.IRecipeWrapper;
import thebetweenlands.common.lib.ModInfo;
import thebetweenlands.compat.jei.recipes.animator.AnimatorRecipeJEI;

public class PurifierRecipeHandler implements IRecipeHandler<PurifierRecipeJEI> {
    @Override
    public Class<PurifierRecipeJEI> getRecipeClass() {
        return PurifierRecipeJEI.class;
    }


    @Override
    public String getRecipeCategoryUid(PurifierRecipeJEI recipe) {
        return ModInfo.ID + ":purifier";
    }

    @Override
    public IRecipeWrapper getRecipeWrapper(PurifierRecipeJEI recipe) {
        return recipe;
    }

    @Override
    public boolean isRecipeValid(PurifierRecipeJEI recipe) {
        return true;
    }
}
