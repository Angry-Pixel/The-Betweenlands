package thebetweenlands.common.inventory;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;

public class InventoryCustomCrafting extends InventoryCrafting {
	public static interface ICustomCraftingGrid {
		public NonNullList<ItemStack> getCraftingGrid();

		public void openInventory(InventoryCustomCrafting inv);

		public void closeInventory(InventoryCustomCrafting inv);

		public void onCraftMatrixChanged();

		public default int getGridWidth() {
			return 3;
		}

		public default int getGridHeight() {
			return 3;
		}
	}

	private NonNullList<ItemStack> stackList;
	private final TileEntity tile;
	private final ICustomCraftingGrid grid;
	private final String name;

	private boolean isBatchCrafting = false;
	private boolean batchCraftingGridChange = false;

	public InventoryCustomCrafting(Container eventHandler, ICustomCraftingGrid tile, String name) {
		super(eventHandler, tile.getGridWidth(), tile.getGridHeight());
		this.name = name;
		this.stackList = tile.getCraftingGrid();
		this.tile = (TileEntity)tile;
		this.grid = tile;
	}

	public void startBatchCrafting() {
		this.isBatchCrafting = true;
		this.batchCraftingGridChange = false;
	}

	public void stopBatchCrafting() {
		this.isBatchCrafting = false;

		if(this.batchCraftingGridChange) {
			this.batchCraftingGridChange = false;

			this.grid.onCraftMatrixChanged();
		}
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
		if (row >= 0 && row < this.grid.getGridWidth()) {
			int k = row + col * this.grid.getGridWidth();
			return this.getStackInSlot(k);
		} else {
			return ItemStack.EMPTY;
		}
	}

	@Override
	public String getName() {
		return this.name;
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
			this.batchCraftingGridChange = true;
			if(!this.isBatchCrafting) {
				this.grid.onCraftMatrixChanged();
			}
		}

		return itemstack;
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack stack) {
		this.stackList.set(slot, stack);
		this.batchCraftingGridChange = true;
		if(!this.isBatchCrafting) {
			this.grid.onCraftMatrixChanged();
		}
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

		this.grid.openInventory(this);
	}

	@Override
	public void closeInventory(EntityPlayer player) {
		super.closeInventory(player);

		this.grid.closeInventory(this);
	}
}
