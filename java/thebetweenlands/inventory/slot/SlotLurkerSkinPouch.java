package thebetweenlands.inventory.slot;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import thebetweenlands.items.equipment.ItemLurkerSkinPouchLarge;
import thebetweenlands.items.equipment.ItemLurkerSkinPouchSmall;

public class SlotLurkerSkinPouch extends Slot {

	public SlotLurkerSkinPouch(IInventory inventory, int slotIndex, int x, int y) {
		super(inventory, slotIndex, x, y);
	}

	@Override
	public boolean isItemValid(ItemStack stack) {
		return !(stack.getItem() instanceof ItemLurkerSkinPouchSmall) && !(stack.getItem() instanceof ItemLurkerSkinPouchLarge);
	}
}