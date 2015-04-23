package thebetweenlands.inventory.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import thebetweenlands.inventory.slot.SlotRestriction;
import thebetweenlands.items.BLItemRegistry;
import thebetweenlands.items.SwampTalisman.EnumTalisman;
import thebetweenlands.tileentities.TileEntityAnimator;

public class ContainerAnimator extends Container {

	private final int numRows = 2;

	public ContainerAnimator(InventoryPlayer playerInventory, TileEntityAnimator tile) {
		super();
		int i = (numRows - 4) * 18;

		addSlotToContainer(new Slot(tile, 0, 80, 24));
		addSlotToContainer(new SlotRestriction(tile, 1, 43, 54, new ItemStack(BLItemRegistry.materialsBL, 1, 11), 1));
		addSlotToContainer(new SlotRestriction(tile, 2, 116, 54, new ItemStack(BLItemRegistry.materialsBL, 1, 23), 64));

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
			if (slotIndex >= 3) {
				if (stack1.getItem() == BLItemRegistry.materialsBL && stack1.getItemDamage() == 11 && !slot.getHasStack()) {
					((Slot) inventorySlots.get(1)).putStack(new ItemStack(stack1.getItem(), 1, stack1.getItemDamage()));
					slot.decrStackSize(1);
				} else if (stack1.getItem() == BLItemRegistry.materialsBL && stack1.getItemDamage() == 23) {
					if (!mergeItemStack(stack1, 2, 3, false))
						return null;
				} else if (stack1.getItem() != null) {
					if (!mergeItemStack(stack1, 0, 1, false))
						return null;
				}
				// Moves items from hotbar to inventory and vice versa
				else {
					if (slotIndex < 30) {
						if (!mergeItemStack(stack1, 30, 39, false))
							return null;
					} else {
						if (!mergeItemStack(stack1, 3, 29, false))
							return null;
					}
				}
			} else if (!mergeItemStack(stack1, 3, inventorySlots.size(), false))
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
