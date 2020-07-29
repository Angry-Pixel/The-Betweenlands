package thebetweenlands.common.inventory;

import net.minecraft.inventory.Container;

public class InventoryRuneletCrafting extends InventoryCustomCrafting {
	public InventoryRuneletCrafting(Container eventHandler, ICustomCraftingGrid tile) {
		super(eventHandler, tile, "container.bl.rune_carving_table");
	}
}
