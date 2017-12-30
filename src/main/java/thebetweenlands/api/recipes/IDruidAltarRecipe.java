package thebetweenlands.api.recipes;

import net.minecraft.item.ItemStack;

public interface IDruidAltarRecipe {
	/**
	 * Returns whether this recipe contains the specified input item
	 * @param input
	 * @return
	 */
	public boolean containsInputItem(ItemStack input);

	/**
	 * Returns whether this recipe matches the 4 input item stacks
	 * @param input
	 * @return
	 */
	public boolean matchesInput(ItemStack[] input);

	/**
	 * Returns the output for the specified 4 input item stacks
	 * @param input
	 * @return
	 */
	public ItemStack getOutput(ItemStack[] input);
}
