package thebetweenlands.common.inventory.container;

import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import thebetweenlands.common.inventory.InventoryItem;

public class ContainerAmphibiousArmour extends Container {
	@Nullable
	private final InventoryItem inventory;

	public ContainerAmphibiousArmour(EntityPlayer player, InventoryItem inventoryItem) {
		InventoryPlayer playerInventory = player.inventory;
		this.inventory = inventoryItem;

		addSlotToContainer(new Slot(inventoryItem, 0, 43, 43));
		addSlotToContainer(new Slot(inventoryItem, 1, 79, 43));
		addSlotToContainer(new Slot(inventoryItem, 2, 115, 43));
		
		for (int l = 0; l < 3; ++l)
            for (int j1 = 0; j1 < 9; ++j1)
                this.addSlotToContainer(new Slot(playerInventory, j1 + (l + 1) * 9, 7 + j1 * 18, 101 + l * 18));

        for (int i1 = 0; i1 < 9; ++i1)
            this.addSlotToContainer(new Slot(playerInventory, i1, 7 + i1 * 18, 159));
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int slotIndex) {
		ItemStack stack = ItemStack.EMPTY;
		Slot slot = (Slot) inventorySlots.get(slotIndex);

		if (slot != null && slot.getHasStack()) {
			ItemStack stack1 = slot.getStack();
			stack = stack1.copy();

			if (slotIndex > 2) {
				if (!this.mergeItemStack(stack1, 0, 3, false))
					return ItemStack.EMPTY;

			} else if (!mergeItemStack(stack1, 3, inventorySlots.size(), false))
				return ItemStack.EMPTY;

			if (stack1.isEmpty())
				slot.putStack(ItemStack.EMPTY);
			else
				slot.onSlotChanged();

			if (stack1.getCount() == stack.getCount())
				return ItemStack.EMPTY;
		}
		return stack;
	}

	public InventoryItem getItemInventory() {
		return this.inventory;
	}

	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return true;
	}
}