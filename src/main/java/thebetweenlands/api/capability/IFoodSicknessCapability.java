package thebetweenlands.api.capability;

import net.minecraft.item.Item;
import thebetweenlands.common.capability.foodsickness.FoodSickness;

public interface IFoodSicknessCapability {
	/**
	 * Returns the previous food sickness
	 * @return
	 */
	public FoodSickness getLastSickness();

	/**
	 * Sets the previous food sickness
	 * @param sickness
	 * @return
	 */
	public void setLastSickness(FoodSickness sickness);
	
	/**
	 * Returns the current food sickness for the specified item
	 * @param food
	 * @return
	 */
	public FoodSickness getSickness(Item food);

	/**
	 * Decreases the hatred for all food items the specified item
	 * @param food
	 * @param decrease
	 */
	public void decreaseHatredForAllExcept(Item food, int decrease);

	/**
	 * Increases the food hatred for the specified item
	 * @param food
	 * @param amount
	 * @param decreaseForOthers
	 */
	public void increaseFoodHatred(Item food, int amount, int decreaseForOthers);

	/**
	 * Returns the food hatred for the specified item
	 * @param food
	 * @return
	 */
	public int getFoodHatred(Item food);
}
