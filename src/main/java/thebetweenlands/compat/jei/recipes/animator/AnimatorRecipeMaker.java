package thebetweenlands.compat.jei.recipes.animator;

import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.item.ItemStack;
import thebetweenlands.api.recipes.IAnimatorRecipe;
import thebetweenlands.common.recipe.animator.ToolRepairAnimatorRecipe;
import thebetweenlands.common.recipe.misc.AnimatorRecipe;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class AnimatorRecipeMaker {
    @Nonnull
    public static List<IRecipeWrapper> getRecipes() {
        ArrayList<IRecipeWrapper> recipes = new ArrayList<>();
        for (IAnimatorRecipe recipe : AnimatorRecipe.getRecipes()) {
            if (recipe instanceof AnimatorRecipe || (recipe instanceof ToolRepairAnimatorRecipe && isValidToolRecipe((ToolRepairAnimatorRecipe) recipe)))
                //if (((AnimatorRecipe) recipe).getLootTable() == null)
                    recipes.add(new AnimatorRecipeJEI(recipe));
        }
        return recipes;
    }

    private static boolean isValidToolRecipe(ToolRepairAnimatorRecipe recipe) {
        ItemStack stack = new ItemStack(recipe.getTool());
        stack.setItemDamage(stack.getMaxDamage() - 1);
        return recipe.matchesInput(stack);
    }

    /*@Nonnull
    public static List<AnimatorRecipeJEI> getRecipesRuntime(World world) {
        ArrayList<AnimatorRecipeJEI> recipes = new ArrayList<AnimatorRecipeJEI>();
        for (IAnimatorRecipe recipe : AnimatorRecipe.getRecipes()) {
            if (recipe instanceof AnimatorRecipe)
                if (((AnimatorRecipe) recipe).getLootTable() != null)
                    recipes.add(new AnimatorRecipeJEI((AnimatorRecipe) recipe, world));
        }
        return recipes;
    }*/
}
