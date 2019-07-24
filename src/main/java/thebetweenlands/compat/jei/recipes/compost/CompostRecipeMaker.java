package thebetweenlands.compat.jei.recipes.compost;

import mezz.jei.api.recipe.IRecipeWrapper;
import thebetweenlands.api.recipes.ICompostBinRecipe;
import thebetweenlands.common.recipe.misc.CompostRecipe;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

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
