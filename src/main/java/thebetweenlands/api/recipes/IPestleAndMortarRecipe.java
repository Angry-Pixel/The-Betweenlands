package thebetweenlands.api.recipes;

import net.minecraft.item.ItemStack;

public interface IPestleAndMortarRecipe {
	/**
	 * Returns the output for the specified item stack
	 * @param input
	 * @return
	 */
	public ItemStack getOutput(ItemStack input);
	
	/**
	 * Returns the output slot for the given input slot stack and output slot stack, can be used to create recipes
	 * that need a certain item in the output slot
	 * @param inputStack
	 * @param outputStack
	 * @return
	 */
	public default ItemStack getOutput(ItemStack inputStack, ItemStack outputStack) {
		return this.getOutput(inputStack);
	}

	/**
	 * Returns the inputs of the recipe
	 * @return
	 */
	public ItemStack getInputs();

	/**
	 * Returns whether the output matches the item stack
	 * @param stack
	 * @return
	 */
	public boolean matchesOutput(ItemStack stack);

	/**
	 * Returns whether this recipe matches the item stack
	 * @param stack
	 * @return
	 */
	public boolean matchesInput(ItemStack stack);
	
	/**
	 * Returns whether this recipe matches the given input slot stack and output slot stack, can be used to create recipes
	 * that need a certain item in the output slot
	 * @param inputStack
	 * @param outputStack
	 * @param inputOnly
	 * @return
	 */
	public default boolean matchesInput(ItemStack inputStack, ItemStack outputStack, boolean inputOnly) {
		return this.matchesInput(inputStack);
	}
	
	/**
	 * Returns whether this recipe requires or uses the given stack in the output slot.
	 * If {@link #matchesInput(ItemStack, ItemStack, boolean)} depends on an output slot stack then this method should 
	 * reflect that.
	 * @param outputStack
	 * @return
	 */
	public default boolean isOutputUsed(ItemStack outputStack) {
		return false;
	}
	
	public default boolean replacesOutput() {
		return false;
	}
}
