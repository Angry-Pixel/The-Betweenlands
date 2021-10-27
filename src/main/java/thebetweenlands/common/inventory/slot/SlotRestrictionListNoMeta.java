package thebetweenlands.common.inventory.slot;

import java.util.List;

import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotRestrictionListNoMeta extends Slot {
	public List<ItemStack> acceptedItems;
	private int maxItems;
	private Container container;

	public SlotRestrictionListNoMeta(IInventory inventory, int slotIndex, int x, int y, List<ItemStack> list, int maxItems, Container container) {
		super(inventory, slotIndex, x, y);
		this.acceptedItems = list;
		this.maxItems = maxItems;
		this.container = container;
	}

	@Override
	public boolean isItemValid(ItemStack stack) {
		for(ItemStack stuff : acceptedItems)
			if(stuff.getItem() == stack.getItem())
				return true;
		return false;
	}
	
    @Override
	public int getSlotStackLimit()
    {
        return maxItems;
    }
}
