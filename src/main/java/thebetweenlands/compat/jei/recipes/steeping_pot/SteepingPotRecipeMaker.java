package thebetweenlands.compat.jei.recipes.steeping_pot;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import mezz.jei.api.recipe.IRecipeWrapper;
import thebetweenlands.api.recipes.ISteepingPotRecipe;
import thebetweenlands.common.recipe.misc.SteepingPotRecipes;

public class SteepingPotRecipeMaker {

    @Nonnull
    public static List<IRecipeWrapper> getRecipes() {
        ArrayList<IRecipeWrapper> recipes = new ArrayList<>();
        for (ISteepingPotRecipe recipe : SteepingPotRecipes.getRecipeList()) {
            if (recipe instanceof ISteepingPotRecipe)
                recipes.add(new SteepingPotRecipeJEI(recipe));
        }
        return recipes;
    }
}
