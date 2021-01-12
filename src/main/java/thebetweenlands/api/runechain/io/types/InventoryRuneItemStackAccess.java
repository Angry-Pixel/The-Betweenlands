package thebetweenlands.api.runechain.io.types;

import java.util.function.Predicate;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public class InventoryRuneItemStackAccess implements IRuneItemStackAccess {
	private final IInventory inventory;
	private final int slot;
	private final Predicate<ItemStack> outputPredicate;
	private final Predicate<ItemStack> inputPredicate;


	public InventoryRuneItemStackAccess(IInventory inventory, int slot, Predicate<ItemStack> outputPredicate, Predicate<ItemStack> inputPredicate) {
		this.inventory = inventory;
		this.slot = slot;
		this.outputPredicate = outputPredicate;
		this.inputPredicate = inputPredicate;
	}

	@Override
	public Pair<IInventory, Integer> getDelegatedSlot() {
		return Pair.of(this.inventory, this.slot);
	}

	@Override
	public boolean isAccessValid() {
		ItemStack stack = this.inventory.getStackInSlot(this.slot);
		return this.outputPredicate.test(stack) || this.inputPredicate.test(stack);
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
