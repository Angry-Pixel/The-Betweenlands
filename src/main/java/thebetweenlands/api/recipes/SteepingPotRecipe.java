package thebetweenlands.api.recipes;

import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.fluids.FluidStack;
import thebetweenlands.common.items.recipe.FluidRecipeInput;
import thebetweenlands.common.registries.RecipeRegistry;

public interface SteepingPotRecipe extends Recipe<FluidRecipeInput> {

	FluidStack getResultFluid(HolderLookup.Provider registries);

	@Override
	default RecipeType<?> getType() {
		return RecipeRegistry.STEEPING_POT_RECIPE.get();
	}
}
