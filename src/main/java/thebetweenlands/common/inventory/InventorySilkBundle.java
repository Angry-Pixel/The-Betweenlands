package thebetweenlands.common.inventory;

import net.minecraft.item.ItemStack;

public class InventorySilkBundle extends InventoryPouch {

	public InventorySilkBundle(ItemStack stack, int inventorySize, String inventoryName) {
		super(stack, inventorySize, inventoryName);
	}

	@Override
	public int getInventoryStackLimit() {
		return 1;
	}
}
