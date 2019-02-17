package thebetweenlands.compat.jei.recipes.compost;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import thebetweenlands.api.recipes.ICompostBinRecipe;
import thebetweenlands.common.recipe.misc.CompostRecipe;

public class CompostRecipeMaker {
    @Nonnull
    public static List<CompostRecipeJEI> getRecipes() {
        ArrayList<CompostRecipeJEI> recipes = new ArrayList<CompostRecipeJEI>();
        for (ICompostBinRecipe recipe : CompostRecipe.RECIPES) {
            if (recipe instanceof CompostRecipe)
                recipes.add(new CompostRecipeJEI((CompostRecipe)recipe));
        }
        return recipes;
    }
}
