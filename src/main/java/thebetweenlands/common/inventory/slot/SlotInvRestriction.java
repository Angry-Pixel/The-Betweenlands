package thebetweenlands.common.inventory.slot;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotInvRestriction extends Slot {
	public SlotInvRestriction(IInventory inventory, int slotIndex, int x, int y) {
		super(inventory, slotIndex, x, y);
	}

	@Override
	public boolean isItemValid(ItemStack stack) {
		return this.inventory.isItemValidForSlot(this.getSlotIndex(), stack);
	}
}
