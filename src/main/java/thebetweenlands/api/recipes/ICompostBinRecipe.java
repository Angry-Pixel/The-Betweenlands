package thebetweenlands.api.recipes;

import net.minecraft.item.ItemStack;

public interface ICompostBinRecipe {
	/**
	 * Returns whether this recipe matches the item stack
	 * @param stack
	 * @return
	 */
	public boolean matchesInput(ItemStack stack);

	/**
	 * Returns the amount of compost produced
	 * @param stack
	 * @return
	 */
	public int getCompostAmount(ItemStack stack);

	/**
	 * Returns the time it takes for the item to turn into compost (in ticks)
	 * @param stack
	 * @return
	 */
	public int getCompostingTime(ItemStack stack);
}
