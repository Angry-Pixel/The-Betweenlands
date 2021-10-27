package thebetweenlands.common.inventory.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import thebetweenlands.common.inventory.slot.SlotOutput;
import thebetweenlands.common.inventory.slot.SlotRestriction;
import thebetweenlands.common.inventory.slot.SlotSizeRestriction;
import thebetweenlands.common.registries.AdvancementCriterionRegistry;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.tile.TileEntitySmokingRack;

public class ContainerSmokingRack extends Container {

	private static class SlotSmokingRackOutput extends SlotOutput {
		public SlotSmokingRackOutput(IInventory inventoryIn, int index, int xPosition, int yPosition, Container container) {
			super(inventoryIn, index, xPosition, yPosition, container);
		}

		@Override
		public ItemStack onTake(EntityPlayer thePlayer, ItemStack stack) {
			if(thePlayer instanceof EntityPlayerMP) {
				AdvancementCriterionRegistry.SMOKE_ITEM.trigger((EntityPlayerMP) thePlayer);
			}
			return super.onTake(thePlayer, stack);
		}
	}

	private final EntityPlayer player;
	public ItemStack leaves = new ItemStack(Item.getItemFromBlock(BlockRegistry.FALLEN_LEAVES));

	private final TileEntitySmokingRack tile;

	public ContainerSmokingRack(EntityPlayer player, TileEntitySmokingRack tile) {
		InventoryPlayer playerInventory = player.inventory;
		this.player = player;
		this.tile = tile;

		//fuel
		addSlotToContainer(new SlotRestriction(tile, 0, 26, 70, leaves, 64, this));

		//input
		addSlotToContainer(new SlotSizeRestriction(tile, 1, 62, 34, 1));
		addSlotToContainer(new SlotSizeRestriction(tile, 2, 62, 52, 1));
		addSlotToContainer(new SlotSizeRestriction(tile, 3, 62, 70, 1));

		//output
		addSlotToContainer(new SlotSmokingRackOutput(tile, 4, 134, 34, this));
		addSlotToContainer(new SlotSmokingRackOutput(tile, 5, 134, 52, this));
		addSlotToContainer(new SlotSmokingRackOutput(tile, 6, 134, 70, this));

		for (int l = 0; l < 3; ++l)
			for (int j1 = 0; j1 < 9; ++j1)
				this.addSlotToContainer(new Slot(playerInventory, j1 + (l + 1) * 9, 8 + j1 * 18, 102 + l * 18));

		for (int i1 = 0; i1 < 9; ++i1)
			this.addSlotToContainer(new Slot(playerInventory, i1, 8 + i1 * 18, 160));
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
		BlockPos pos = this.tile.getPos();
		if(playerIn.world.getTileEntity(pos) != this.tile) {
			return false;
		} else {
			return playerIn.getDistanceSq(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D) <= 64.0D;
		}
	}
}