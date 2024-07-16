package thebetweenlands.api.recipes;


import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;

public interface MortarRecipe extends Recipe<SingleRecipeInput> {

	/**
	 * Returns whether this recipe matches the given input slot stack and output slot stack, can be used to create recipes
	 * that need a certain item in the output slot
	 * @param input
	 * @param output
	 * @param level
	 * @param useInputOnly
	 * @return
	 */
	default boolean matchesInput(SingleRecipeInput input, ItemStack output, Level level, boolean useInputOnly) {
		return this.matches(input, level);
	}

	/**
	 * Returns the output slot for the given input slot stack and output slot stack, can be used to create recipes
	 * that need a certain item in the output slot
	 * @param input
	 * @param outputStack
	 * @return
	 */
	default ItemStack getOutput(SingleRecipeInput input, ItemStack outputStack, HolderLookup.Provider registries) {
		return this.assemble(input, registries);
	}

	/**
	 * Returns whether this recipe requires or uses the given stack in the output slot.
	 * If {@link #matchesInput(SingleRecipeInput, ItemStack, Level, boolean)} depends on an output slot stack then this method should
	 * reflect that.
	 * @param output
	 * @return
	 */
	default boolean isOutputUsed(ItemStack output) {
		return false;
	}

	default boolean replacesOutput() {
		return false;
	}
}
