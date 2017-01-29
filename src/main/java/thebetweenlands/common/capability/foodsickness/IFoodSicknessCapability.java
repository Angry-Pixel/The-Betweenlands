package thebetweenlands.common.capability.foodsickness;

import net.minecraft.item.ItemFood;

public interface IFoodSicknessCapability {
	public FoodSickness getLastSickness();

	public FoodSickness getSickness(ItemFood food);

	public void decreaseHatredForAllExcept(ItemFood food, int decrease);

	public void increaseFoodHatred(ItemFood food, int amount, int decreaseForOthers);

	public int getFoodHatred(ItemFood food);
}
