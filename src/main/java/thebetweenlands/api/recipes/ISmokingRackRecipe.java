package thebetweenlands.api.recipes;

import net.minecraft.item.ItemStack;

public interface ISmokingRackRecipe {
	
	/**
	 * Returns whether this recipe matches the item stack
	 * @param stack
	 * @return
	 */
	public boolean matchesInput(ItemStack stack);
	
	/**
	 * Returns the input item stack
	 * @return
	 */
	public ItemStack getInput();

	/**
	 * Returns the output for item stack
	 * @return
	 */
	public ItemStack getOutput(ItemStack stack);

	/**
	 * Returns the time it takes for the item to smoke (in ticks)
	 * @param stack
	 * @return
	 */
	public int getSmokingTime(ItemStack stack);

}
