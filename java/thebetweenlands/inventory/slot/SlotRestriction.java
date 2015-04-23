package thebetweenlands.inventory.slot;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import thebetweenlands.items.SwampTalisman;
import thebetweenlands.items.SwampTalisman.EnumTalisman;

public class SlotRestriction extends Slot {
	ItemStack item;

	public SlotRestriction(IInventory inventory, int slotIndex, int x, int y, ItemStack item) {
		super(inventory, slotIndex, x, y);
		this.item = item;
	}

	@Override
	public boolean isItemValid(ItemStack stack) {
		if (stack.getItem() == item.getItem() && stack.getItemDamage() == item.getItemDamage()) return true;
		return false;
	}
}
