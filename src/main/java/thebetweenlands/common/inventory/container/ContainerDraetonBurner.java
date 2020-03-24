package thebetweenlands.common.inventory.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerDraetonBurner extends Container {

	public ContainerDraetonBurner(InventoryPlayer playerInventory, IInventory entityInventory) {
		addSlotToContainer(new Slot(entityInventory, 0, 8 + 4 * 18, 36));

		int i = -18;
		
		for (int y = 0; y < 3; ++y)
			for (int x = 0; x < 9; ++x)
				addSlotToContainer(new Slot(playerInventory, x + y * 9 + 9, 8 + x * 18, 104 + y * 18 + i));

		for (int x = 0; x < 9; ++x)
			addSlotToContainer(new Slot(playerInventory, x, 8 + x * 18, 162 + i));
	}

	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return true;
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int slotIndex) {
		ItemStack is = ItemStack.EMPTY;
		Slot slot = (Slot) inventorySlots.get(slotIndex);

		if (slot != null && slot.getHasStack()) {
			ItemStack is1 = slot.getStack();
			is = is1.copy();
			
			if (slotIndex > 0) {
				if (!mergeItemStack(is1, 0, 1, true))
					return ItemStack.EMPTY;
			} else if (!mergeItemStack(is1, 0, 3 * 9 + 9, false))
				return ItemStack.EMPTY;

			if (is1.getCount() == 0)
				slot.putStack(ItemStack.EMPTY);
			else
				slot.onSlotChanged();
		}

		return is;
	}
}