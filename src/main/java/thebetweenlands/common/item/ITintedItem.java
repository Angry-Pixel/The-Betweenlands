package thebetweenlands.common.item;

import net.minecraft.item.ItemStack;

public interface ITintedItem {
	int getColorMultiplier(ItemStack stack, int tintIndex);
}
