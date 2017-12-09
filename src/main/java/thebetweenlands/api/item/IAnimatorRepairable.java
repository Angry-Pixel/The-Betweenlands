package thebetweenlands.api.item;

import net.minecraft.item.ItemStack;

public interface IAnimatorRepairable {
	/**
	 * Returns whether the specified stack is repairable
	 * @param stack
	 * @return
	 */
	public default boolean isRepairableByAnimator(ItemStack stack) {
		return true;
	}
	
	/**
	 * Returns the minimum fuel cost to repair the specified stack
	 * @param stack
	 * @return
	 */
	public int getMinRepairFuelCost(ItemStack stack);
	
	/**
	 * Returns the maximum fuel cost to fully repair the specified stack if it were completely broken
	 * @param stack
	 * @return
	 */
	public int getFullRepairFuelCost(ItemStack stack);
	
	/**
	 * Returns the minimum life cost to repair the specified stack
	 * @param stack
	 * @return
	 */
	public int getMinRepairLifeCost(ItemStack stack);

	/**
	 * Returns the maximum life cost to fully repair the specified stack if it were completely broken
	 * @param stack
	 * @return
	 */
	public int getFullRepairLifeCost(ItemStack stack);
}
