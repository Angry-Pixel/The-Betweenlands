package thebetweenlands.compat.jei.recipes.compost;

import thebetweenlands.api.recipes.ICompostBinRecipe;
import thebetweenlands.common.recipe.misc.CompostRecipe;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

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
