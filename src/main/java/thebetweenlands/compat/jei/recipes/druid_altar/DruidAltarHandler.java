package thebetweenlands.compat.jei.recipes.druid_altar;

import mezz.jei.api.recipe.IRecipeHandler;
import mezz.jei.api.recipe.IRecipeWrapper;
import thebetweenlands.common.lib.ModInfo;

public class DruidAltarHandler implements IRecipeHandler<DruidAltarRecipeJEI> {
    @Override
    public Class<DruidAltarRecipeJEI> getRecipeClass() {
        return DruidAltarRecipeJEI.class;
    }

    @Override
    public String getRecipeCategoryUid(DruidAltarRecipeJEI recipe) {
        return ModInfo.ID + ":druid_altar";
    }

    @Override
    public IRecipeWrapper getRecipeWrapper(DruidAltarRecipeJEI recipe) {
        return recipe;
    }

    @Override
    public boolean isRecipeValid(DruidAltarRecipeJEI recipe) {
        return true;
    }
}
