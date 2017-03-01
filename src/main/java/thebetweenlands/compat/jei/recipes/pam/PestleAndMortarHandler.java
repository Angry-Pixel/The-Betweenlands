package thebetweenlands.compat.jei.recipes.pam;

import mezz.jei.api.recipe.IRecipeHandler;
import mezz.jei.api.recipe.IRecipeWrapper;
import thebetweenlands.common.lib.ModInfo;
import thebetweenlands.compat.jei.recipes.druid_altar.DruidAltarRecipeJEI;

public class PestleAndMortarHandler implements IRecipeHandler<PestleAndMortarRecipeJEI> {
    @Override
    public Class<PestleAndMortarRecipeJEI> getRecipeClass() {
        return PestleAndMortarRecipeJEI.class;
    }

    @Override
    public String getRecipeCategoryUid() {
        return null;
    }

    @Override
    public String getRecipeCategoryUid(PestleAndMortarRecipeJEI recipe) {
        return ModInfo.ID + ":pestle_and_mortar";
    }

    @Override
    public IRecipeWrapper getRecipeWrapper(PestleAndMortarRecipeJEI recipe) {
        return recipe;
    }

    @Override
    public boolean isRecipeValid(PestleAndMortarRecipeJEI recipe) {
        return true;
    }
}