package thebetweenlands.inventory.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import thebetweenlands.tileentities.TileEntityDruidAltar;

public class ContainerDruidAltar extends Container {

	private final int numRows = 2;

	public ContainerDruidAltar(InventoryPlayer playerInventory, TileEntityDruidAltar tile) {
		super();
		int i = (numRows - 4) * 18;
		
		addSlotToContainer(new Slot(tile, 0, 81, 35));
		addSlotToContainer(new Slot(tile, 1, 53, 7));
		addSlotToContainer(new Slot(tile, 2, 109, 7));
		addSlotToContainer(new Slot(tile, 3, 53, 63));
		addSlotToContainer(new Slot(tile, 4, 109, 63));

		for (int j = 0; j < 3; j++)
			for (int k = 0; k < 9; k++)
				addSlotToContainer(new Slot(playerInventory, k + j * 9 + 9, 8 + k * 18, 120 + j * 18 + i));
		for (int j = 0; j < 9; j++)
			addSlotToContainer(new Slot(playerInventory, j, 8 + j * 18, 178 + i));
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int slotIndex) {
		ItemStack stack = null;
		Slot slot = (Slot) inventorySlots.get(slotIndex);
		if (slot != null && slot.getHasStack()) {
			ItemStack stack1 = slot.getStack();
			stack = stack1.copy();
			if (slotIndex > 5) {
				if (!mergeItemStack(stack1, 1, 5, false))
					return null;
			} else if (!mergeItemStack(stack1, 5, inventorySlots.size(), false))
				return null;
			if (stack1.stackSize == 0)
				slot.putStack(null);
			else
				slot.onSlotChanged();
			if (stack1.stackSize != stack.stackSize)
				slot.onPickupFromSlot(player, stack1);
			else
				return null;
		}
		return stack;
	}

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
	}

	@Override
	public boolean canInteractWith(EntityPlayer p_75145_1_) {
		return true;
	}
}