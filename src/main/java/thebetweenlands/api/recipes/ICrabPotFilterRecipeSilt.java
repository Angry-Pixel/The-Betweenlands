package thebetweenlands.api.recipes;

import net.minecraft.item.ItemStack;

public interface ICrabPotFilterRecipeSilt {

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

}
