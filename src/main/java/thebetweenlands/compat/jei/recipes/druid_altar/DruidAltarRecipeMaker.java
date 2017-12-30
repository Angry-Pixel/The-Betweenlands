package thebetweenlands.compat.jei.recipes.druid_altar;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import thebetweenlands.api.recipes.IDruidAltarRecipe;
import thebetweenlands.common.recipe.misc.DruidAltarRecipe;

public class DruidAltarRecipeMaker {
    @Nonnull
    public static List<DruidAltarRecipeJEI> getRecipes() {
        ArrayList<DruidAltarRecipeJEI> recipes = new ArrayList<DruidAltarRecipeJEI>();
        for (IDruidAltarRecipe recipe : DruidAltarRecipe.getRecipes()) {
        	if(recipe instanceof DruidAltarRecipe) {
        		recipes.add(new DruidAltarRecipeJEI((DruidAltarRecipe)recipe));
        	}
        }
        return recipes;
    }
}
