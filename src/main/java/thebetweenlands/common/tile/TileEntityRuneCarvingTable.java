package thebetweenlands.common.tile;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import thebetweenlands.common.inventory.InventoryCustomCrafting;
import thebetweenlands.common.inventory.InventoryCustomCrafting.ICustomCraftingGrid;
import thebetweenlands.util.NonNullDelegateList;

public class TileEntityRuneCarvingTable extends TileEntityBasicInventory implements ICustomCraftingGrid {
	private Set<InventoryCustomCrafting> openInventories = new HashSet<>();

	public TileEntityRuneCarvingTable() {
		//Slots 0-8: crafting grid, 9: crafting output, 10: aspect slot, 11-14: rune outputs
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
		return new NonNullDelegateList<ItemStack>(this.inventory.subList(0, 9), ItemStack.EMPTY);
	}
}
