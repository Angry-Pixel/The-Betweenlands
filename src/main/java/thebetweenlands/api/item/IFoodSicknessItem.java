package thebetweenlands.api.item;

import java.util.List;

import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import thebetweenlands.common.capability.foodsickness.FoodSickness;

public interface IFoodSicknessItem {
	/**
	 * Returns whether the player can get sick of the specified item stack
	 * @param stack
	 * @return
	 */
	default boolean canGetSickOf(ItemStack stack) {
		return true;
	}

	/**
	 * Adds additional tooltips for the specified sickness
	 * @param stack
	 * @param sickness
	 * @param hatred
	 * @param advancedTooltips
	 * @param toolTip
	 * @return
	 */
	default void getSicknessTooltip(ItemStack stack, FoodSickness sickness, int hatred, boolean advancedTooltips, List<String> toolTip) {
		String debug = "";
		if(advancedTooltips) {
			debug = " (" + hatred + "/" + sickness.maxHatred + ")";
		}
		toolTip.add(I18n.format("tooltip.food_sickness.state." + sickness.name().toLowerCase()) + debug);
	}
}
