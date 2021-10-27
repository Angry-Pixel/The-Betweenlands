package thebetweenlands.common.inventory.slot;

import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotExclusion extends Slot {
	private ItemStack item;
	private int maxItems;
	private Container container;

	public SlotExclusion(IInventory inventory, int slotIndex, int x, int y, ItemStack item, int maxItems, Container container) {
		super(inventory, slotIndex, x, y);
		this.item = item;
		this.maxItems = maxItems;
		this.container = container;
	}

	@Override
	public boolean isItemValid(ItemStack stack) {
		if (stack.getItem() == item.getItem() && stack.getItemDamage() == item.getItemDamage())
			return false;
		return true;
	}
	
    @Override
	public int getSlotStackLimit() {
        return maxItems;
    }
}