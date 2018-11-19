package thebetweenlands.compat.jei.recipes.pam;

import thebetweenlands.api.recipes.IPestleAndMortarRecipe;
import thebetweenlands.common.recipe.misc.PestleAndMortarRecipe;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class PestleAndMortarRecipeMaker {
    @Nonnull
    public static List<PestleAndMortarRecipeJEI> getRecipes() {
        ArrayList<PestleAndMortarRecipeJEI> recipes = new ArrayList<PestleAndMortarRecipeJEI>();
        for (IPestleAndMortarRecipe recipe : PestleAndMortarRecipe.getRecipes()) {
            recipes.add(new PestleAndMortarRecipeJEI(recipe));
        }
        return recipes;
    }
}
