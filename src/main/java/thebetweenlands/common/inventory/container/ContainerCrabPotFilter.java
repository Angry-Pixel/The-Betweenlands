package thebetweenlands.common.inventory.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import thebetweenlands.common.inventory.slot.SlotExclusion;
import thebetweenlands.common.inventory.slot.SlotInvRestriction;
import thebetweenlands.common.inventory.slot.SlotOutput;
import thebetweenlands.common.inventory.slot.SlotRestriction;
import thebetweenlands.common.item.misc.ItemMisc.EnumItemMisc;
import thebetweenlands.common.registries.AdvancementCriterionRegistry;
import thebetweenlands.common.tile.TileEntityCrabPotFilter;

public class ContainerCrabPotFilter extends Container {

	private final EntityPlayer player;
	public ItemStack anadia_remains = EnumItemMisc.ANADIA_REMAINS.create(1);

	private final TileEntityCrabPotFilter tile;

	public ContainerCrabPotFilter(EntityPlayer player, TileEntityCrabPotFilter tile) {
		InventoryPlayer playerInventory = player.inventory;
		this.player = player;
		this.tile = tile;

		//fuel
		addSlotToContainer(new SlotRestriction(tile, 0, 43, 61, anadia_remains, 64, this));

		//input
		addSlotToContainer(new SlotInvRestriction(tile, 1, 43, 25));

		//output
		addSlotToContainer(new SlotOutput(tile, 2, 112, 43, this) {
			@Override
			public ItemStack onTake(EntityPlayer thePlayer, ItemStack stack) {
				if(thePlayer instanceof EntityPlayerMP) {
					AdvancementCriterionRegistry.CRAB_FILTER.trigger((EntityPlayerMP) thePlayer);
				}
				return super.onTake(thePlayer, stack);
			}
		});

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
				if (EnumItemMisc.ANADIA_REMAINS.isItemOf(stack1)) {
					if (!this.mergeItemStack(stack1, 0, 1, false))
						return ItemStack.EMPTY;
				} else {
					if (!this.mergeItemStack(stack1, 1, 2, false))
						return ItemStack.EMPTY;
				}
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
		BlockPos pos = this.tile.getPos();
		if(playerIn.world.getTileEntity(pos) != this.tile) {
			return false;
		} else {
			return playerIn.getDistanceSq(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D) <= 64.0D;
		}
	}
}