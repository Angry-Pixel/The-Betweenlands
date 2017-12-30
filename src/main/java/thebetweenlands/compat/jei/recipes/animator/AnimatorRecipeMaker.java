package thebetweenlands.compat.jei.recipes.animator;

import net.minecraft.world.World;
import thebetweenlands.api.recipes.IAnimatorRecipe;
import thebetweenlands.common.recipe.animator.ToolRepairAnimatorRecipe;
import thebetweenlands.common.recipe.misc.AnimatorRecipe;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class AnimatorRecipeMaker {
    @Nonnull
    public static List<AnimatorRecipeJEI> getRecipes() {
        ArrayList<AnimatorRecipeJEI> recipes = new ArrayList<AnimatorRecipeJEI>();
        for (IAnimatorRecipe recipe : AnimatorRecipe.getRecipes()) {
            if (recipe instanceof AnimatorRecipe || recipe instanceof ToolRepairAnimatorRecipe)
                //if (((AnimatorRecipe) recipe).getLootTable() == null)
                    recipes.add(new AnimatorRecipeJEI(recipe));
        }
        return recipes;
    }

    @Nonnull
    public static List<AnimatorRecipeJEI> getRecipesRuntime(World world) {
        ArrayList<AnimatorRecipeJEI> recipes = new ArrayList<AnimatorRecipeJEI>();
        for (IAnimatorRecipe recipe : AnimatorRecipe.getRecipes()) {
            if (recipe instanceof AnimatorRecipe)
                if (((AnimatorRecipe) recipe).getLootTable() != null)
                    recipes.add(new AnimatorRecipeJEI((AnimatorRecipe) recipe, world));
        }
        return recipes;
    }
}
