package thebetweenlands.compat.jei.recipes.druid_altar;

import mezz.jei.api.recipe.IRecipeWrapper;
import thebetweenlands.api.recipes.IDruidAltarRecipe;
import thebetweenlands.common.recipe.misc.DruidAltarRecipe;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

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
