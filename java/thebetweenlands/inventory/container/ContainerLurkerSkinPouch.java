package thebetweenlands.inventory.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import thebetweenlands.inventory.slot.SlotLurkerSkinPouch;
import thebetweenlands.items.equipment.ItemBasicInventory;
import thebetweenlands.items.equipment.ItemLurkerSkinPouch;

public class ContainerLurkerSkinPouch extends Container {
	public final ItemBasicInventory inventory;
	public int numRows = 3;

	public ContainerLurkerSkinPouch(EntityPlayer player, InventoryPlayer playerInventory, ItemBasicInventory itemInventory) {
		inventory = itemInventory;
		numRows = inventory.getSizeInventory() / 9;
        int i = (this.numRows - 4) * 18;
        int j;
        int k;

        for (j = 0; j < this.numRows; ++j)
            for (k = 0; k < 9; ++k)
                addSlotToContainer(new SlotLurkerSkinPouch(itemInventory, k + j * 9, 8 + k * 18, 18 + j * 18));

        for (j = 0; j < 3; ++j)
            for (k = 0; k < 9; ++k)
                addSlotToContainer(new Slot(playerInventory, k + j * 9 + 9, 8 + k * 18, 103 + j * 18 + i));

        for (j = 0; j < 9; ++j)
            addSlotToContainer(new Slot(playerInventory, j, 8 + j * 18, 161 + i));
	}

	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return true;
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int slotIndex) {
		ItemStack stack = null;
		Slot slot = (Slot) inventorySlots.get(slotIndex);

		if (slot != null && slot.getHasStack()) {
			ItemStack stack1 = slot.getStack();
			stack = stack1.copy();

			if (stack1.getItem() instanceof ItemLurkerSkinPouch)
				return null;

			if (slotIndex < numRows * 9) {
				if (!mergeItemStack(stack1, numRows * 9, inventorySlots.size(), true))
					return null;
			} else if (!mergeItemStack(stack1, 0, numRows * 9, false))
				return null;

			if (stack1.stackSize == 0)
				slot.putStack(null);
			else
				slot.onSlotChanged();
		}

		return stack;
	}
}