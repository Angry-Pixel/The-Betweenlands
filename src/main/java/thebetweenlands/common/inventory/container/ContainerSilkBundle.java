package thebetweenlands.common.inventory.container;

import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import thebetweenlands.common.inventory.InventoryItem;
import thebetweenlands.common.inventory.slot.SlotInvRestriction;
import thebetweenlands.util.InventoryUtils;

public class ContainerSilkBundle extends Container {
	@Nullable
	private final InventoryItem inventory;

	private int numRows = 3;

	public ContainerSilkBundle(EntityPlayer player, InventoryPlayer playerInventory, @Nullable InventoryItem itemInventory) {
		this.inventory = itemInventory;

		if(this.inventory == null || this.inventory.isEmpty()) {
			return;
		}

		this.numRows = this.inventory.getSizeInventory() / 9;
		int yOffset = (this.numRows - 4) * 18;

	
		//new slot restriction list with meta needed here
		this.addSlotToContainer(new SlotInvRestriction(itemInventory, 0, 98, 4));
		this.addSlotToContainer(new SlotInvRestriction(itemInventory, 1, 116, 4));
		this.addSlotToContainer(new SlotInvRestriction(itemInventory, 2, 134, 4));
		this.addSlotToContainer(new SlotInvRestriction(itemInventory, 3, 152, 4));
				

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
		return true;
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int slotIndex) {
		ItemStack stack = ItemStack.EMPTY;
		Slot slot = this.inventorySlots.get(slotIndex);

		if (slot != null && slot.getHasStack()) {
			ItemStack slotStack = slot.getStack();
			stack = slotStack.copy();

			if (InventoryUtils.isDisallowedInInventories(slotStack)) {
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