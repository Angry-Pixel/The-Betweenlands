package thebetweenlands.api.recipes;

import net.minecraft.item.ItemStack;

public interface IPurifierRecipe {
	/**
	 * Returns the output for the specified item stack
	 * @param input
	 * @return
	 */
	public ItemStack getOutput(ItemStack input);

	/**
	 * Returns whether this recipe matches the item stack
	 * @param stack
	 * @return
	 */
	public boolean matchesInput(ItemStack stack);
}
