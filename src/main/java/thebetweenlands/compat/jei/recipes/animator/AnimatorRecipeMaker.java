package thebetweenlands.compat.jei.recipes.animator;

import thebetweenlands.api.recipes.IAnimatorRecipe;
import thebetweenlands.common.recipe.misc.AnimatorRecipe;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class AnimatorRecipeMaker {
    @Nonnull
    public static List<AnimatorRecipeJEI> getRecipes() {
        ArrayList<AnimatorRecipeJEI> recipes = new ArrayList<AnimatorRecipeJEI>();
        for (IAnimatorRecipe recipe : AnimatorRecipe.getRecipes()) {
            if (recipe instanceof AnimatorRecipe)
                recipes.add(new AnimatorRecipeJEI((AnimatorRecipe) recipe));
        }
        return recipes;
    }
}
