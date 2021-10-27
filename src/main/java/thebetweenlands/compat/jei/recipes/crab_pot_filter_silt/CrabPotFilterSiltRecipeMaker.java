package thebetweenlands.compat.jei.recipes.crab_pot_filter_silt;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import mezz.jei.api.recipe.IRecipeWrapper;
import thebetweenlands.api.recipes.ICrabPotFilterRecipeSilt;
import thebetweenlands.common.recipe.misc.CrabPotFilterRecipeSilt;

public class CrabPotFilterSiltRecipeMaker {

    @Nonnull
    public static List<IRecipeWrapper> getRecipes() {
        ArrayList<IRecipeWrapper> recipes = new ArrayList<>();
        for (ICrabPotFilterRecipeSilt recipe : CrabPotFilterRecipeSilt.getRecipeList()) {
            if (recipe instanceof ICrabPotFilterRecipeSilt)
                recipes.add(new CrabPotFilterSiltRecipeJEI(recipe));
        }
        return recipes;
    }
}
