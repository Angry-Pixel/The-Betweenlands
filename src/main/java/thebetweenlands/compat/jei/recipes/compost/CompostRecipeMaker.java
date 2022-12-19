package thebetweenlands.compat.jei.recipes.compost;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import mezz.jei.api.recipe.IRecipeWrapper;
import thebetweenlands.api.recipes.ICompostBinRecipe;
import thebetweenlands.common.recipe.misc.CompostRecipe;

public class CompostRecipeMaker {
    @Nonnull
    public static List<IRecipeWrapper> getRecipes() {
        ArrayList<IRecipeWrapper> recipes = new ArrayList<>();
        for (ICompostBinRecipe recipe : CompostRecipe.RECIPES) {
            if (recipe instanceof CompostRecipe)
                recipes.add(new CompostRecipeJEI((CompostRecipe)recipe));
        }
        return recipes;
    }
}
