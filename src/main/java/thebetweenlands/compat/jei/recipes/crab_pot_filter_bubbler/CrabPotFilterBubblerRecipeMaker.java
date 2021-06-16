package thebetweenlands.compat.jei.recipes.crab_pot_filter_bubbler;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import mezz.jei.api.recipe.IRecipeWrapper;
import thebetweenlands.api.recipes.ICrabPotFilterRecipeBubbler;
import thebetweenlands.common.recipe.misc.CrabPotFilterRecipeBubbler;

public class CrabPotFilterBubblerRecipeMaker {

    @Nonnull
    public static List<IRecipeWrapper> getRecipes() {
        ArrayList<IRecipeWrapper> recipes = new ArrayList<>();
        for (ICrabPotFilterRecipeBubbler recipe : CrabPotFilterRecipeBubbler.getRecipeList()) {
            if (recipe instanceof ICrabPotFilterRecipeBubbler)
                recipes.add(new CrabPotFilterBubblerRecipeJEI(recipe));
        }
        return recipes;
    }
}
