package thebetweenlands.api.capability;

import net.minecraft.item.ItemFood;
import thebetweenlands.common.capability.foodsickness.FoodSickness;

public interface IFoodSicknessCapability {
	/**
	 * Returns the previous food sickness
	 * @return
	 */
	public FoodSickness getLastSickness();

	/**
	 * Returns the current food sickness for the specified item
	 * @param food
	 * @return
	 */
	public FoodSickness getSickness(ItemFood food);

	/**
	 * Decreases the hatred for all food items the specified item
	 * @param food
	 * @param decrease
	 */
	public void decreaseHatredForAllExcept(ItemFood food, int decrease);

	/**
	 * Increases the food hatred for the specified item
	 * @param food
	 * @param amount
	 * @param decreaseForOthers
	 */
	public void increaseFoodHatred(ItemFood food, int amount, int decreaseForOthers);

	/**
	 * Returns the food hatred for the specified item
	 * @param food
	 * @return
	 */
	public int getFoodHatred(ItemFood food);
}
