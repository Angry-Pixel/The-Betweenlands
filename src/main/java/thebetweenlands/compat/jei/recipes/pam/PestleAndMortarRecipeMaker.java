package thebetweenlands.compat.jei.recipes.pam;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import thebetweenlands.api.recipes.IPestleAndMortarRecipe;
import thebetweenlands.common.recipe.misc.PestleAndMortarRecipe;

public class PestleAndMortarRecipeMaker {
    @Nonnull
    public static List<PestleAndMortarRecipeJEI> getRecipes() {
        ArrayList<PestleAndMortarRecipeJEI> recipes = new ArrayList<PestleAndMortarRecipeJEI>();
        for (IPestleAndMortarRecipe recipe : PestleAndMortarRecipe.getRecipes()) {
        	if(recipe instanceof PestleAndMortarRecipe) {
        		recipes.add(new PestleAndMortarRecipeJEI((PestleAndMortarRecipe) recipe));
        	}
        }
        return recipes;
    }
}
