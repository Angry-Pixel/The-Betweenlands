package thebetweenlands.common.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.SlotCrafting;
import net.minecraft.item.ItemStack;

public class InventoryPassthroughCraftingInput extends InventoryCrafting {
	private final SlotCrafting sourceSlot;

	private boolean isBatchCrafting = false;
	private boolean batchCraftingGridChange = false;
	
	public InventoryPassthroughCraftingInput(Container eventHandlerIn, SlotCrafting sourceSlot) {
		super(eventHandlerIn, 1, 1);
		this.sourceSlot = sourceSlot;
	}

	public void startBatchCrafting() {
		this.isBatchCrafting = true;
		this.batchCraftingGridChange = false;
	}

	public void stopBatchCrafting() {
		this.isBatchCrafting = false;

		if(this.batchCraftingGridChange) {
			this.batchCraftingGridChange = false;

			this.eventHandler.onCraftMatrixChanged(this);
		}
	}
	
	@Override
	public int getSizeInventory() {
		return 1;
	}

	@Override
	public boolean isEmpty() {
		return this.sourceSlot.getHasStack();
	}

	@Override
	public ItemStack getStackInSlot(int index) {
		return this.sourceSlot.getStack();
	}

	@Override
	public ItemStack decrStackSize(int index, int count) {
		ItemStack result = this.sourceSlot.decrStackSize(count);
		if(!result.isEmpty()) {
			this.batchCraftingGridChange = true;
			if(!this.isBatchCrafting) {
				this.eventHandler.onCraftMatrixChanged(this);
			}
		}
		return result;
	}

	@Override
	public ItemStack removeStackFromSlot(int index) {
		return this.sourceSlot.decrStackSize(this.sourceSlot.getStack().getCount());
	}

	@Override
	public void setInventorySlotContents(int index, ItemStack stack) {
	}

	@Override
	public int getInventoryStackLimit() {
		return this.sourceSlot.getSlotStackLimit();
	}

	@Override
	public void markDirty() {
	}

	@Override
	public boolean isUsableByPlayer(EntityPlayer player) {
		return true;
	}

	@Override
	public void openInventory(EntityPlayer player) {
	}

	@Override
	public void closeInventory(EntityPlayer player) {
	}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack) {
		return this.sourceSlot.isItemValid(stack);
	}

	@Override
	public int getField(int id) {
		return 0;
	}

	@Override
	public void setField(int id, int value) {
	}

	@Override
	public int getFieldCount() {
		return 0;
	}

	@Override
	public void clear() {
	}
}
