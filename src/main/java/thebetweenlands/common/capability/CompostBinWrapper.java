package thebetweenlands.common.capability;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import thebetweenlands.common.block.entity.CompostBinBlockEntity;

public class CompostBinWrapper implements IItemHandler {

	private final CompostBinBlockEntity compostBin;
	
	public CompostBinWrapper(CompostBinBlockEntity compostBin) {
		this.compostBin = compostBin;
	}

	@Override
	public int getSlots() {
		return 1;
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		this.validateSlotIndex(slot);
		return this.compostBin.getCompostStack();
	}

	@Override
	public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
		return stack.isEmpty() ? ItemStack.EMPTY : stack;
	}

	@Override
	public ItemStack extractItem(int slot, int amount, boolean simulate) {
		// Validate inputs
		if(amount <= 0) {
			return ItemStack.EMPTY;
		}
		this.validateSlotIndex(slot);
		
		// Do not allow extraction when the lid is closed
		if(!this.compostBin.isLidOpen()) {
			return ItemStack.EMPTY;
		}
		
		ItemStack stack = this.compostBin.extractCompostStack(amount, simulate);
		if(!stack.isEmpty() && !simulate) {
			this.compostBin.setChanged();
		}
		return stack;
	}

	@Override
	public int getSlotLimit(int slot) {
		return Item.ABSOLUTE_MAX_STACK_SIZE;
	}

	@Override
	public boolean isItemValid(int slot, ItemStack stack) {
		return false;
	}

	protected void validateSlotIndex(int slot) {
		if (slot < 0 || slot >= 1) {
			throw new RuntimeException("Slot " + slot + " not in valid range - [0,1)");
		}
	}
}
