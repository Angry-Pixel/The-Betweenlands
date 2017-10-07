package thebetweenlands.common.inventory;

import net.minecraft.block.state.IBlockState;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import thebetweenlands.common.tile.TileEntityWeedwoodWorkbench;

public class InventoryWeedwoodWorkbench extends InventoryCrafting {
	private NonNullList<ItemStack> stackList;
	private Container container;
	private static final int INV_WIDTH = 3;
	private final TileEntityWeedwoodWorkbench tile;

	public InventoryWeedwoodWorkbench(Container container, TileEntityWeedwoodWorkbench tile) {
		super(container, 3, 3);
		this.stackList = tile.craftingSlots;
		this.container = container;
		this.tile = tile;
	}

	@Override
	public int getSizeInventory() {
		return this.stackList.size();
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		return slot >= this.getSizeInventory() ? ItemStack.EMPTY : this.stackList.get(slot);
	}

	@Override
	public ItemStack getStackInRowAndColumn(int row, int col) {
		if (row >= 0 && row < INV_WIDTH) {
			int k = row + col * INV_WIDTH;
			return this.getStackInSlot(k);
		} else {
			return ItemStack.EMPTY;
		}
	}

	@Override
	public String getName() {
		return "container.crafting";
	}

	@Override
	public boolean hasCustomName() {
		return false;
	}

	@Override
	public ItemStack decrStackSize(int slot, int amount) {
		if (!this.stackList.get(slot).isEmpty()) {
			ItemStack stack;

			if (this.stackList.get(slot).getCount() <= amount) {
				stack = this.stackList.get(slot);
				this.stackList.set(slot, ItemStack.EMPTY);
				this.container.onCraftMatrixChanged(this);
				return stack;
			} else {
				stack = this.stackList.get(slot).splitStack(amount);

				if (this.stackList.get(slot).getCount() == 0) {
					this.stackList.set(slot, ItemStack.EMPTY);
				}

				this.container.onCraftMatrixChanged(this);
				return stack;
			}
		} else {
			return ItemStack.EMPTY;
		}
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack stack) {
		this.stackList.set(slot, stack);
		this.container.onCraftMatrixChanged(this);
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public void markDirty() {
		this.tile.markDirty();
		IBlockState state = this.tile.getWorld().getBlockState(this.tile.getPos());
		this.tile.getWorld().notifyBlockUpdate(this.tile.getPos(), state, state, 3);
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack) {
		return true;
	}
}
