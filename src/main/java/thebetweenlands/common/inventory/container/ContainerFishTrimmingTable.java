package thebetweenlands.common.inventory.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.Constants;
import thebetweenlands.common.inventory.slot.SlotOutput;
import thebetweenlands.common.inventory.slot.SlotRestriction;
import thebetweenlands.common.inventory.slot.SlotRestrictionNoMeta;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.tile.TileEntityFishTrimmingTable;

public class ContainerFishTrimmingTable extends Container {

	private final EntityPlayer player;
	public ItemStack anadia = new ItemStack(ItemRegistry.ANADIA);
	public ItemStack axe = new ItemStack(ItemRegistry.BONE_AXE);
	public TileEntityFishTrimmingTable fish_trimming_table;

	public ContainerFishTrimmingTable(EntityPlayer player, TileEntityFishTrimmingTable tile) {
		InventoryPlayer playerInventory = player.inventory;
		this.player = player;
		fish_trimming_table = tile;

		//input
		addSlotToContainer(new SlotRestriction(fish_trimming_table, 0, 80, 22, anadia, 1, this));

		//output
		addSlotToContainer(new SlotOutput(fish_trimming_table, 1, 44, 72, this));
		addSlotToContainer(new SlotOutput(fish_trimming_table, 2, 80, 72, this));
		addSlotToContainer(new SlotOutput(fish_trimming_table, 3, 116, 72, this));
		addSlotToContainer(new SlotOutput(fish_trimming_table, 4, 152, 108, this));

		//tool
		addSlotToContainer(new SlotRestrictionNoMeta(fish_trimming_table, 5, 8, 108, axe, 1));
		
		for (int l = 0; l < 3; ++l)
            for (int j1 = 0; j1 < 9; ++j1)
                this.addSlotToContainer(new Slot(playerInventory, j1 + (l + 1) * 9, 8 + j1 * 18, 140 + l * 18));

        for (int i1 = 0; i1 < 9; ++i1)
            this.addSlotToContainer(new Slot(playerInventory, i1, 8 + i1 * 18, 198));
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int slotIndex) {
		ItemStack stack = ItemStack.EMPTY;
		Slot slot = (Slot) inventorySlots.get(slotIndex);

		if (slot != null && slot.getHasStack()) {
			ItemStack stack1 = slot.getStack();
			stack = stack1.copy();

			if (slotIndex > 5) {
				if (stack1.getItem() == ItemRegistry.ANADIA && stack1.getTagCompound() != null && stack1.getTagCompound().hasKey("Entity", Constants.NBT.TAG_COMPOUND)) {
					if (!this.mergeItemStack(stack1, 0, 1, false))
						return ItemStack.EMPTY;
				}
				
				if (stack1.getItem() == ItemRegistry.BONE_AXE) {
					if (!this.mergeItemStack(stack1, 5, 6, false))
						return ItemStack.EMPTY;
				}

				if (!this.mergeItemStack(stack1, 1, 5, false))
					return ItemStack.EMPTY;

			} else if (!mergeItemStack(stack1, 6, inventorySlots.size(), false))
				return ItemStack.EMPTY;

			if (stack1.isEmpty())
				slot.putStack(ItemStack.EMPTY);
			else
				slot.onSlotChanged();

			if (stack1.getCount() == stack.getCount())
				return ItemStack.EMPTY;

			ItemStack stack2 = slot.onTake(player, stack1);

			if (slotIndex == 1 || slotIndex == 2 || slotIndex == 3 || slotIndex == 4)
				player.dropItem(stack2, false);
		}

		return stack;
	}

	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return true;
	}
}