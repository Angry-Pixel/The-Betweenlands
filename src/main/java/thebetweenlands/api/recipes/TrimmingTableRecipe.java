package thebetweenlands.api.recipes;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import thebetweenlands.common.registries.RecipeRegistry;

public interface TrimmingTableRecipe extends Recipe<SingleRecipeInput> {

	@Override
	default ItemStack assemble(SingleRecipeInput input, HolderLookup.Provider registries) {
		return ItemStack.EMPTY;
	}

	NonNullList<ItemStack> assembleRecipe(SingleRecipeInput input, Level level);

	@Override
	default ItemStack getResultItem(HolderLookup.Provider registries) {
		return ItemStack.EMPTY;
	}

	NonNullList<ItemStack> getResultItems(HolderLookup.Provider registries);

	ItemStack getRemains();

	@Override
	default boolean canCraftInDimensions(int width, int height) {
		return true;
	}

	@Override
	default RecipeType<?> getType() {
		return RecipeRegistry.TRIMMING_TABLE_RECIPE.get();
	}
}
