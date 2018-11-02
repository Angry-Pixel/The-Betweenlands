package thebetweenlands.compat.jei.recipes.druid_altar;

import thebetweenlands.api.recipes.IDruidAltarRecipe;
import thebetweenlands.common.recipe.misc.DruidAltarRecipe;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

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
