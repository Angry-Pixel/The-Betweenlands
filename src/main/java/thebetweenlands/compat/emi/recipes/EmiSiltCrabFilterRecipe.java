package thebetweenlands.compat.emi.recipes;

import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import thebetweenlands.common.item.recipe.BubblerCrabPotFilterRecipe;
import thebetweenlands.common.item.recipe.SiltCrabPotFilterRecipe;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.compat.emi.BetweenlandsEMICompat;

public class EmiSiltCrabFilterRecipe extends BLEmiRecipe<SiltCrabPotFilterRecipe> {

	private final EmiTexture ARROW = new EmiTexture(this.getBackgroundTexture().texture, 91, 18, 22, 16);
	private final EmiTexture REMAINS = new EmiTexture(this.getBackgroundTexture().texture, 92, 4, 16, 10);

	public EmiSiltCrabFilterRecipe(RecipeHolder<SiltCrabPotFilterRecipe> recipe) {
		super(BetweenlandsEMICompat.SILT_CRAB_FILTER, recipe, 91, 83);
	}

	@Override
	public void addWidgets(WidgetHolder widgets) {
		widgets.addTexture(this.getBackgroundTexture(), 0, 0);
		widgets.addSlot(this.getInputs().getFirst(), 0, 29).drawBack(false);
		widgets.addSlot(EmiIngredient.of(Ingredient.of(ItemRegistry.ANADIA_REMAINS)), 0, 65).drawBack(false);
		widgets.addSlot(this.getOutputs().getFirst(), 65, 43).recipeContext(this).drawBack(false).large(true);

		widgets.addAnimatedTexture(ARROW, 31, 47, 4000, true, false, false);
		widgets.addAnimatedTexture(REMAINS, 1, 51, 4000, false, true, true);
	}
}
