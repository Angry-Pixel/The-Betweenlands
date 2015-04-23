package thebetweenlands.inventory.slot;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import thebetweenlands.items.SwampTalisman;
import thebetweenlands.items.SwampTalisman.EnumTalisman;

public class SlotRestriction extends Slot {
	ItemStack item;
	int maxItems;

	public SlotRestriction(IInventory inventory, int slotIndex, int x, int y, ItemStack item, int maxItems) {
		super(inventory, slotIndex, x, y);
		this.item = item;
		this.maxItems = maxItems;
	}

	@Override
	public boolean isItemValid(ItemStack stack) {
		if (stack.getItem() == item.getItem() && stack.getItemDamage() == item.getItemDamage()) return true;
		return false;
	}
	
    public int getSlotStackLimit()
    {
        return maxItems;
    }
}
