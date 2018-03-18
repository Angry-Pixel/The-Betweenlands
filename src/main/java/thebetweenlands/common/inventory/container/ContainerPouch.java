package thebetweenlands.common.inventory.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import thebetweenlands.common.inventory.InventoryItem;
import thebetweenlands.common.inventory.slot.SlotPouch;
import thebetweenlands.common.item.equipment.ItemLurkerSkinPouch;

public class ContainerPouch extends Container {
	private final InventoryItem inventory;
	private int numRows = 3;

	public ContainerPouch(EntityPlayer player, InventoryPlayer playerInventory, InventoryItem itemInventory) {
		this.inventory = itemInventory;

		if(this.inventory == null || this.inventory.isEmpty()) {
			return;
		}

		this.numRows = this.inventory.getSizeInventory() / 9;
		int yOffset = (this.numRows - 4) * 18;

		for (int row = 0; row < this.numRows; ++row) {
			for (int column = 0; column < 9; ++column) {
				this.addSlotToContainer(new SlotPouch(itemInventory, column + row * 9, 8 + column * 18, 18 + row * 18));
			}
		}

		for (int row = 0; row < 3; ++row) {
			for (int column = 0; column < 9; ++column) {
				this.addSlotToContainer(new Slot(playerInventory, column + row * 9 + 9, 8 + column * 18, 103 + row * 18 + yOffset));
			}
		}

		for (int column = 0; column < 9; ++column) {
			this.addSlotToContainer(new Slot(playerInventory, column, 8 + column * 18, 161 + yOffset));
		}
	}
	
	public InventoryItem getItemInventory() {
		return this.inventory;
	}

	@Override
	public boolean canInteractWith(EntityPlayer player) {
		for(int i = 0; i < player.inventory.getSizeInventory(); i++) {
			if(player.inventory.getStackInSlot(i) == this.inventory.getInventoryItemStack()) {
				return true;
			}
		}
		return false;
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int slotIndex) {
		ItemStack stack = ItemStack.EMPTY;
		Slot slot = (Slot) this.inventorySlots.get(slotIndex);

		if (slot != null && slot.getHasStack()) {
			ItemStack slotStack = slot.getStack();
			stack = slotStack.copy();

			if (slotStack.getItem() instanceof ItemLurkerSkinPouch) {
				return ItemStack.EMPTY;
			}

			if (slotIndex < this.numRows * 9) {
				if (!mergeItemStack(slotStack, this.numRows * 9, this.inventorySlots.size(), true)) {
					return ItemStack.EMPTY;
				}
			} else if (!mergeItemStack(slotStack, 0, this.numRows * 9, false)) {
				return ItemStack.EMPTY;
			}

			if (slotStack.getCount() == 0) {
				slot.putStack(ItemStack.EMPTY);
			} else {
				slot.onSlotChanged();
			}
		}

		return stack;
	}
}