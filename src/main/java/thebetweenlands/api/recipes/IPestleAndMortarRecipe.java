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
}
