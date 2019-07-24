package thebetweenlands.compat.jei.recipes.pam;

import mezz.jei.api.recipe.IRecipeWrapper;
import thebetweenlands.api.recipes.IPestleAndMortarRecipe;
import thebetweenlands.common.recipe.misc.PestleAndMortarRecipe;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class PestleAndMortarRecipeMaker {
    @Nonnull
    public static List<IRecipeWrapper> getRecipes() {
        ArrayList<IRecipeWrapper> recipes = new ArrayList<>();
        for (IPestleAndMortarRecipe recipe : PestleAndMortarRecipe.getRecipes()) {
            recipes.add(new PestleAndMortarRecipeJEI(recipe));
        }
        return recipes;
    }
}
