package thebetweenlands.common.inventory.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import thebetweenlands.common.inventory.slot.SlotOutput;
import thebetweenlands.common.inventory.slot.SlotRestriction;
import thebetweenlands.common.inventory.slot.SlotSizeRestriction;
import thebetweenlands.common.item.misc.ItemMisc.EnumItemMisc;
import thebetweenlands.common.tile.TileEntityCrabPotFilter;

public class ContainerCrabPotFilter extends Container {

	private final EntityPlayer player;
	public ItemStack anadia_remains = EnumItemMisc.ANADIA_REMAINS.create(1);

	public ContainerCrabPotFilter(EntityPlayer player, TileEntityCrabPotFilter tile) {
		InventoryPlayer playerInventory = player.inventory;
		this.player = player;
		
		//fuel
		addSlotToContainer(new SlotRestriction(tile, 0, 43, 61, anadia_remains, 64, this));
		
		//input
		addSlotToContainer(new SlotSizeRestriction(tile, 1, 43, 25, 64));
		
		//output
		addSlotToContainer(new SlotOutput(tile, 2, 112, 43, this));
		
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
				if (stack1.getItem() == EnumItemMisc.ANADIA_REMAINS.getItem()) {
					if (!this.mergeItemStack(stack1, 0, 1, false))
						return ItemStack.EMPTY;
				}
					if (!this.mergeItemStack(stack1, 1, 2, false))
						return ItemStack.EMPTY;

			} else if (!mergeItemStack(stack1, 3, inventorySlots.size(), false))
				return ItemStack.EMPTY;

			if (stack1.isEmpty())
				slot.putStack(ItemStack.EMPTY);
			else
				slot.onSlotChanged();

			if (stack1.getCount() == stack.getCount())
				return ItemStack.EMPTY;

			ItemStack stack2 = slot.onTake(player, stack1);

			if (slotIndex == 2)
				player.dropItem(stack2, false);
		}

		return stack;
	}

	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return true;
	}
}