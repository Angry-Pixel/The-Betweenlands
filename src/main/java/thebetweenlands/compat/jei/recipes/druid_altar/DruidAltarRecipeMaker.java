package thebetweenlands.compat.jei.recipes.druid_altar;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import mezz.jei.api.recipe.IRecipeWrapper;
import thebetweenlands.api.recipes.IDruidAltarRecipe;
import thebetweenlands.common.recipe.misc.DruidAltarRecipe;

public class DruidAltarRecipeMaker {
    @Nonnull
    public static List<IRecipeWrapper> getRecipes() {
        ArrayList<IRecipeWrapper> recipes = new ArrayList<>();
        for (IDruidAltarRecipe recipe : DruidAltarRecipe.getRecipes()) {
        	if(recipe instanceof DruidAltarRecipe) {
        		recipes.add(new DruidAltarRecipeJEI((DruidAltarRecipe)recipe));
        	}
        }
        recipes.add(new DruidAltarReactivationRecipeJEI());
        return recipes;
    }
}
