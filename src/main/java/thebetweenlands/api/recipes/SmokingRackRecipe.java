package thebetweenlands.api.recipes;

import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import thebetweenlands.common.registries.RecipeRegistry;

public interface SmokingRackRecipe extends Recipe<SingleRecipeInput> {

	int smokingTime();

	@Override
	default boolean canCraftInDimensions(int width, int height) {
		return true;
	}

	@Override
	default RecipeType<?> getType() {
		return RecipeRegistry.SMOKING_RECIPE.get();
	}
}
