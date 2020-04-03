package thebetweenlands.common.inventory.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import thebetweenlands.common.entity.draeton.EntityDraeton;

public class ContainerDraetonBurner extends Container {

	private final EntityDraeton draeton;

	public ContainerDraetonBurner(InventoryPlayer playerInventory, IInventory entityInventory, EntityDraeton draeton) {
		this.draeton = draeton;

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
		return this.draeton.getDistanceSq(player) <= 64.0D;
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int slotIndex) {
		ItemStack is = ItemStack.EMPTY;
		Slot slot = (Slot) inventorySlots.get(slotIndex);

		if (slot != null && slot.getHasStack()) {
			ItemStack is1 = slot.getStack();
			is = is1.copy();

			if (slotIndex < 1) {
				if (!mergeItemStack(is1, 1, this.inventorySlots.size(), false))
					return ItemStack.EMPTY;
			} else if (!mergeItemStack(is1, 0, 1, false))
				return ItemStack.EMPTY;

			if (is1.getCount() == 0)
				slot.putStack(ItemStack.EMPTY);
			else
				slot.onSlotChanged();
		}

		return is;
	}
}