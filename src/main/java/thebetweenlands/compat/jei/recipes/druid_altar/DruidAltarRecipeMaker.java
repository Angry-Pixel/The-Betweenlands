package thebetweenlands.compat.jei.recipes.druid_altar;

import thebetweenlands.common.recipe.misc.DruidAltarRecipe;
import thebetweenlands.compat.jei.recipes.animator.AnimatorRecipeJEI;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class DruidAltarRecipeMaker {
    @Nonnull
    public static List<DruidAltarRecipeJEI> getRecipes() {
        ArrayList<DruidAltarRecipeJEI> recipes = new ArrayList<DruidAltarRecipeJEI>();
        for (DruidAltarRecipe recipe : DruidAltarRecipe.druidAltarRecipes) {
            recipes.add(new DruidAltarRecipeJEI(recipe));
        }
        return recipes;
    }
}
