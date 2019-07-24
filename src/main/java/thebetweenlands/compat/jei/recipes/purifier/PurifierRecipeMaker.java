package thebetweenlands.compat.jei.recipes.purifier;

import mezz.jei.api.recipe.IRecipeWrapper;
import thebetweenlands.api.recipes.IPurifierRecipe;
import thebetweenlands.common.recipe.purifier.PurifierRecipe;
import thebetweenlands.common.recipe.purifier.PurifierRecipeStandard;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class PurifierRecipeMaker {

    @Nonnull
    public static List<IRecipeWrapper> getRecipes() {
        ArrayList<IRecipeWrapper> recipes = new ArrayList<>();
        for (IPurifierRecipe recipe : PurifierRecipe.getRecipeList()) {
            if (recipe instanceof PurifierRecipeStandard)
                recipes.add(new PurifierRecipeJEI((PurifierRecipeStandard) recipe));
        }
        return recipes;
    }
}
