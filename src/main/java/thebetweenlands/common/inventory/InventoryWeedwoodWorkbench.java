package thebetweenlands.common.inventory;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import thebetweenlands.common.tile.TileEntityWeedwoodWorkbench;

public class InventoryWeedwoodWorkbench extends InventoryCrafting {
	private NonNullList<ItemStack> stackList;
	private static final int INV_WIDTH = 3;
	private final TileEntityWeedwoodWorkbench tile;

	public InventoryWeedwoodWorkbench(Container eventHandler, TileEntityWeedwoodWorkbench tile) {
		super(eventHandler, 3, 3);
		this.stackList = tile.craftingSlots;
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
        return "container.bl.weedwood_workbench";
    }
    
    @Override
    public ITextComponent getDisplayName() {
        return (ITextComponent)(this.hasCustomName() ? new TextComponentString(this.getName()) : new TextComponentTranslation(this.getName(), new Object[0]));
    }

	@Override
	public boolean hasCustomName() {
		return false;
	}

	@Override
	public ItemStack decrStackSize(int slot, int amount) {
		ItemStack itemstack = ItemStackHelper.getAndSplit(this.stackList, slot, amount);

		if(!itemstack.isEmpty()) {
			this.tile.onCraftMatrixChanged();
		}

		return itemstack;
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack stack) {
		this.stackList.set(slot, stack);
		this.tile.onCraftMatrixChanged();
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

	@Override
	public void openInventory(EntityPlayer player) {
		super.openInventory(player);

		this.tile.openInventory(this);
	}

	@Override
	public void closeInventory(EntityPlayer player) {
		super.closeInventory(player);

		this.tile.closeInventory(this);
	}
}
