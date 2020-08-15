package thebetweenlands.api.rune.impl;

import java.util.function.Predicate;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import thebetweenlands.api.rune.IRuneItemStackAccess;

public class InventoryRuneItemStackAccess implements IRuneItemStackAccess {
	private final IInventory inventory;
	private final int slot;
	private final Predicate<ItemStack> predicate;


	public InventoryRuneItemStackAccess(IInventory inventory, int slot, Predicate<ItemStack> predicate) {
		this.inventory = inventory;
		this.slot = slot;
		this.predicate = predicate;
	}

	@Override
	public boolean isAccessValid() {
		return this.predicate.test(this.inventory.getStackInSlot(this.slot));
	}

	@Override
	public ItemStack get() {
		if(!this.isAccessValid()) {
			return ItemStack.EMPTY;
		}
		return this.inventory.getStackInSlot(this.slot);
	}

	@Override
	public boolean set(ItemStack stack) {
		if(this.isAccessValid() && this.inventory.isItemValidForSlot(this.slot, stack)) {
			this.inventory.setInventorySlotContents(this.slot, stack);
			return true;
		}
		return false;
	}

	@Override
	public boolean isItemValid(ItemStack stack) {
		return this.isAccessValid() && this.inventory.isItemValidForSlot(this.slot, stack);
	}
}
