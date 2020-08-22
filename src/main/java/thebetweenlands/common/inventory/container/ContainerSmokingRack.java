package thebetweenlands.common.inventory.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import thebetweenlands.common.inventory.slot.SlotOutput;
import thebetweenlands.common.tile.TileEntitySmokingRack;

public class ContainerSmokingRack extends Container {

	private final EntityPlayer player;

	public ContainerSmokingRack(EntityPlayer player, TileEntitySmokingRack tile) {
		InventoryPlayer playerInventory = player.inventory;
		this.player = player;
		
		//fuel
		addSlotToContainer(new Slot(tile, 0, 26, 54));
		
		//input
		addSlotToContainer(new Slot(tile, 1, 62, 18));
		addSlotToContainer(new Slot(tile, 2, 62, 36));
		addSlotToContainer(new Slot(tile, 3, 62, 54));
		
		//output
		addSlotToContainer(new SlotOutput(tile, 4, 134, 18, this));
		addSlotToContainer(new SlotOutput(tile, 5, 134, 36, this));
		addSlotToContainer(new SlotOutput(tile, 6, 134, 54, this));
		
		for (int l = 0; l < 3; ++l)
            for (int j1 = 0; j1 < 9; ++j1)
                this.addSlotToContainer(new Slot(playerInventory, j1 + (l + 1) * 9, 8 + j1 * 18, 86 + l * 18));

        for (int i1 = 0; i1 < 9; ++i1)
            this.addSlotToContainer(new Slot(playerInventory, i1, 8 + i1 * 18, 144));
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int slotIndex) {
		// TODO fix this shit 
		ItemStack is = ItemStack.EMPTY;
		Slot slot = (Slot) inventorySlots.get(slotIndex);

		if (slot != null && slot.getHasStack()) {
			ItemStack is1 = slot.getStack();
			is = is1.copy();

			if (slotIndex == 0) {
				if (!this.mergeItemStack(is1, 6, 42, true))
					return ItemStack.EMPTY;
				slot.onSlotChange(is1, is);
			} else if (slotIndex >= 1 && slotIndex < 6) {
				if (!this.mergeItemStack(is1, 6, 42, false))
					return ItemStack.EMPTY;
			} else if (slotIndex >= 6 && slotIndex < 42) {
				if (!mergeItemStack(is1, 42, inventorySlots.size(), true))
					return ItemStack.EMPTY;
			}

			if (is1.isEmpty())
				slot.putStack(ItemStack.EMPTY);
			else
				slot.onSlotChanged();

			if (is1.getCount() == is.getCount())
				return ItemStack.EMPTY;

			ItemStack is2 = slot.onTake(player, is1);

			if (slotIndex == 4 || slotIndex == 5 || slotIndex == 6)
				player.dropItem(is2, false);
		}

		return is;
	}

	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return true;
	}
}