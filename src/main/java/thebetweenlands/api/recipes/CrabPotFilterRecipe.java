package thebetweenlands.api.recipes;

import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import thebetweenlands.common.items.recipe.ItemAndEntityInput;
import thebetweenlands.common.registries.RecipeRegistry;

public interface CrabPotFilterRecipe extends Recipe<ItemAndEntityInput> {

	int filterTime();

	@Override
	default boolean canCraftInDimensions(int width, int height) {
		return true;
	}

	@Override
	default RecipeType<?> getType() {
		return RecipeRegistry.CRAB_POT_FILTER_RECIPE.get();
	}
}
