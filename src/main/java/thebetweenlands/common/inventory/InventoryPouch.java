package thebetweenlands.common.inventory;

import net.minecraft.item.ItemStack;
import thebetweenlands.common.item.equipment.ItemLurkerSkinPouch;
import thebetweenlands.util.InventoryUtils;

public class InventoryPouch extends InventoryItem {

	public InventoryPouch(ItemStack stack, int inventorySize, String inventoryName) {
		super(stack, inventorySize, inventoryName);
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack itemstack) {
		return !InventoryUtils.isDisallowedInInventories(itemstack);
	}

}
