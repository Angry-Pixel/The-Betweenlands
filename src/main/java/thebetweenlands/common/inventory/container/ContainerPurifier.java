package thebetweenlands.common.inventory.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import thebetweenlands.common.item.misc.ItemMisc.EnumItemMisc;
import thebetweenlands.common.tile.TileEntityPurifier;

public class ContainerPurifier extends Container {
	protected TileEntityPurifier purifier;

	public ContainerPurifier(InventoryPlayer inventory, TileEntityPurifier tileentity) {
		purifier = tileentity;

		addSlotToContainer(new Slot(tileentity, 0, 61, 54));
		addSlotToContainer(new Slot(tileentity, 1, 61, 14));
		addSlotToContainer(new Slot(tileentity, 2, 121, 34));

		for (int i = 0; i < 3; ++i) {
			for (int j = 0; j < 9; ++j) {
				addSlotToContainer(new Slot(inventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
			}
		}
		for (int i = 0; i < 9; ++i) {
			addSlotToContainer(new Slot(inventory, i, 8 + i * 18, 142));
		}
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int slotIndex) {
		ItemStack newStack = ItemStack.EMPTY;
		Slot slot = inventorySlots.get(slotIndex);
		if (slot != null && slot.getHasStack()) {
			ItemStack slotStack = slot.getStack();
			newStack = slotStack.copy();
			if (slotIndex > 2) {
				if (EnumItemMisc.SULFUR.isItemOf(slotStack)) {
					if (!mergeItemStack(slotStack, 0, 1, false)) {
						return ItemStack.EMPTY;
					}
				} else if (!mergeItemStack(slotStack, 1, 2, true)) {
					return ItemStack.EMPTY;
				}
			} else if (!mergeItemStack(slotStack, 3, inventorySlots.size(), false))
				return ItemStack.EMPTY;
			if (slotStack.getCount() == 0)
				slot.putStack(ItemStack.EMPTY);
			else
				slot.onSlotChanged();
			if (slotStack.getCount() != newStack.getCount())
				slot.onTake(player, slotStack);
			else
				return ItemStack.EMPTY;
		}
		return newStack;
	}

	@Override
	public void addListener(IContainerListener listener) {
		super.addListener(listener);
		purifier.sendGUIData(this, listener);
	}

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		for (IContainerListener listener : listeners) {
			purifier.sendGUIData(this, listener);
		}
	}

	@Override
	public void updateProgressBar(int id, int value) {
		purifier.getGUIData(id, value);
	}

	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return true;
	}

}
