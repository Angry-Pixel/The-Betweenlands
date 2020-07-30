package thebetweenlands.common.tile;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thebetweenlands.common.block.container.BlockRuneCarvingTable;
import thebetweenlands.common.inventory.InventoryCustomCrafting;
import thebetweenlands.common.inventory.InventoryCustomCrafting.ICustomCraftingGridChangeHandler;
import thebetweenlands.util.NonNullDelegateList;
import thebetweenlands.util.StatePropertyHelper;

public class TileEntityRuneCarvingTable extends TileEntityBasicInventory implements ICustomCraftingGridChangeHandler {
	private Set<InventoryCustomCrafting> openInventories = new HashSet<>();

	public TileEntityRuneCarvingTable() {
		//Slots 0-8: crafting grid, 9: crafting output, 10: aspect slot, 11-14: rune outputs
		super(15, "rune_carving_table");
	}

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate) {
		return oldState.getBlock() != newSate.getBlock();
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

	public NonNullList<ItemStack> getCraftingGrid() {
		return new NonNullDelegateList<ItemStack>(this.inventory.subList(0, 9), ItemStack.EMPTY);
	}

	public NonNullList<ItemStack> getAspectGrid() {
		return new NonNullDelegateList<ItemStack>(this.inventory.subList(10, 11), ItemStack.EMPTY);
	}

	public boolean isFullGrid() {
		return StatePropertyHelper.getStatePropertySafely(this, BlockRuneCarvingTable.class, BlockRuneCarvingTable.FULL_GRID, false);
	}
}
