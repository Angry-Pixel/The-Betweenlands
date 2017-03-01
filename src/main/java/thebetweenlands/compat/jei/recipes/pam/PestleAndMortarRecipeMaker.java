package thebetweenlands.compat.jei.recipes.pam;

import thebetweenlands.common.recipe.misc.DruidAltarRecipe;
import thebetweenlands.common.recipe.misc.PestleAndMortarRecipe;
import thebetweenlands.compat.jei.recipes.druid_altar.DruidAltarRecipeJEI;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class PestleAndMortarRecipeMaker {
    @Nonnull
    public static List<PestleAndMortarRecipeJEI> getRecipes() {
        ArrayList<PestleAndMortarRecipeJEI> recipes = new ArrayList<PestleAndMortarRecipeJEI>();
        for (PestleAndMortarRecipe recipe : PestleAndMortarRecipe.getRecipeList()) {
            recipes.add(new PestleAndMortarRecipeJEI(recipe));
        }
        return recipes;
    }
}
