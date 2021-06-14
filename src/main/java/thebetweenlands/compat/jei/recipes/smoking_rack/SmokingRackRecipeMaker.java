package thebetweenlands.compat.jei.recipes.smoking_rack;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import mezz.jei.api.recipe.IRecipeWrapper;
import thebetweenlands.api.recipes.ISmokingRackRecipe;
import thebetweenlands.common.recipe.misc.SmokingRackRecipe;

public class SmokingRackRecipeMaker {

    @Nonnull
    public static List<IRecipeWrapper> getRecipes() {
        ArrayList<IRecipeWrapper> recipes = new ArrayList<>();
        for (ISmokingRackRecipe recipe : SmokingRackRecipe.getRecipeList()) {
            if (recipe instanceof SmokingRackRecipe)
                recipes.add(new SmokingRackRecipeJEI((SmokingRackRecipe) recipe));
        }
        return recipes;
    }
}
