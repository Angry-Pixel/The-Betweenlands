package thebetweenlands.common.tile;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import thebetweenlands.common.inventory.InventoryCustomCrafting;
import thebetweenlands.common.inventory.InventoryCustomCrafting.ICustomCraftingGrid;

public class TileEntityRuneCarvingTable extends TileEntityBasicInventory implements ICustomCraftingGrid {
	private Set<InventoryCustomCrafting> openInventories = new HashSet<>();

	public TileEntityRuneCarvingTable() {
		super(15, "rune_carving_table");
	}

	@Override
	public void openInventory(InventoryCustomCrafting inv) {
		this.openInventories.add(inv);
	}

	@Override
	public void closeInventory(InventoryCustomCrafting inv) {
		this.openInventories.remove(inv);
	}

	/**
	 * Notifies *all* open inventories of the changes, fixes dupe bug as in #532
	 */
	@Override
	public void onCraftMatrixChanged() {
		for(InventoryCustomCrafting inv : this.openInventories) {
			inv.eventHandler.onCraftMatrixChanged(inv);
		}
	}

	@Override
	public NonNullList<ItemStack> getCraftingGrid() {
		// TODO Return crafting grid view of inventory
		return null;
	}
}
