package thebetweenlands.api.item;

import net.minecraft.item.ItemStack;

public interface IDecayFood {
	/**
	 * Returns the amount of healed decay when eating this item
	 * @param stack
	 * @return
	 */
	int getDecayHealAmount(ItemStack stack);

	/**
	 * Returns the saturation multiplier gained when eating this item
	 * @param stack
	 * @return
	 */
	default float getDecayHealSaturation(ItemStack stack) {
		return 0.2F;
	}
}
