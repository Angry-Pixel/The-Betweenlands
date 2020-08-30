package thebetweenlands.common.inventory.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import thebetweenlands.common.inventory.slot.SlotOutput;
import thebetweenlands.common.inventory.slot.SlotRestriction;
import thebetweenlands.common.inventory.slot.SlotSizeRestriction;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.tile.TileEntitySmokingRack;

public class ContainerSmokingRack extends Container {

	private final EntityPlayer player;
	public ItemStack leaves = new ItemStack(Item.getItemFromBlock(BlockRegistry.FALLEN_LEAVES));

	public ContainerSmokingRack(EntityPlayer player, TileEntitySmokingRack tile) {
		InventoryPlayer playerInventory = player.inventory;
		this.player = player;
		
		//fuel
		addSlotToContainer(new SlotRestriction(tile, 0, 26, 54, leaves, 64, this));
		
		//input
		addSlotToContainer(new SlotSizeRestriction(tile, 1, 62, 18, 1));
		addSlotToContainer(new SlotSizeRestriction(tile, 2, 62, 36, 1));
		addSlotToContainer(new SlotSizeRestriction(tile, 3, 62, 54, 1));
		
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
		ItemStack stack = ItemStack.EMPTY;
		Slot slot = (Slot) inventorySlots.get(slotIndex);

		if (slot != null && slot.getHasStack()) {
			ItemStack stack1 = slot.getStack();
			stack = stack1.copy();

			if (slotIndex > 6) {
				if (stack1.getItem() == Item.getItemFromBlock(BlockRegistry.FALLEN_LEAVES) && stack1.getItemDamage() == 0) {
					if (!this.mergeItemStack(stack1, 0, 1, false))
						return ItemStack.EMPTY;
				}
					if (!this.mergeItemStack(stack1, 1, 4, false))
						return ItemStack.EMPTY;

			} else if (!mergeItemStack(stack1, 7, inventorySlots.size(), false))
				return ItemStack.EMPTY;

			if (stack1.isEmpty())
				slot.putStack(ItemStack.EMPTY);
			else
				slot.onSlotChanged();

			if (stack1.getCount() == stack.getCount())
				return ItemStack.EMPTY;

			ItemStack stack2 = slot.onTake(player, stack1);

			if (slotIndex == 4 || slotIndex == 5 || slotIndex == 6)
				player.dropItem(stack2, false);
		}

		return stack;
	}

	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return true;
	}
}