package thebetweenlands.common.inventory;

import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public class InventoryRuneletCrafting extends InventoryCustomCrafting {
	public InventoryRuneletCrafting(Container eventHandler, ICustomCraftingGridChangeHandler tile, NonNullList<ItemStack> inventory) {
		super(eventHandler, tile, inventory, 3, 3, "container.bl.rune_carving_table");
	}
}
