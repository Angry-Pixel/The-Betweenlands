package thebetweenlands.common.inventory.slot;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotSizeRestriction extends Slot {
    int maxItems;

    public SlotSizeRestriction(IInventory inventory, int slotIndex, int x, int y, int maxItems) {
        super(inventory, slotIndex, x, y);
        this.maxItems = maxItems;
    }

    @Override
    public boolean isItemValid(ItemStack stack) {
        return true;
    }

    @Override
	public int getSlotStackLimit()
    {
        return maxItems;
    }
}