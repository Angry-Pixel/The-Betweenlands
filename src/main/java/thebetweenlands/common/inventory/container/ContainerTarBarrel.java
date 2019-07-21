package thebetweenlands.common.inventory.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import thebetweenlands.common.tile.TileEntityTarBarrel;

public class ContainerTarBarrel extends Container {
	public static final int SLOT_FUEL = 0;
	public static final int SLOT_INPUT = 1;
	public static final int SLOT_INTERNAL = 2;

	protected TileEntityTarBarrel barrel;

	public ContainerTarBarrel(InventoryPlayer inventory, TileEntityTarBarrel tileentity) {
		barrel = tileentity;

		int yOffset = 0;

		for (int i = 0; i < 3; ++i) {
			for (int j = 0; j < 9; ++j) {
				addSlotToContainer(new Slot(inventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18 + yOffset));
			}
		}
		for (int i = 0; i < 9; ++i) {
			addSlotToContainer(new Slot(inventory, i, 8 + i * 18, 142 + yOffset));
		}
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
		ItemStack itemstack = ItemStack.EMPTY;
		Slot slot = this.inventorySlots.get(index);

		if (slot != null && slot.getHasStack()) {
			ItemStack itemstack1 = slot.getStack();
			itemstack = itemstack1.copy();

			if(index >= 27 && index < 36) {
				if (!this.mergeItemStack(itemstack1, 0, 27, false)) {
					return ItemStack.EMPTY;
				}
			} else if (index >= 0 && index < 27) {
				if (!this.mergeItemStack(itemstack1, 27, 36, false)) {
					return ItemStack.EMPTY;
				}
			}

			if (itemstack1.isEmpty()) {
				slot.putStack(ItemStack.EMPTY);
			} else {
				slot.onSlotChanged();
			}

			if(itemstack1.getCount() == itemstack.getCount()) {
				return ItemStack.EMPTY;
			}

			ItemStack itemstack2 = slot.onTake(playerIn, itemstack1);

			if(index == 0) {
				playerIn.dropItem(itemstack2, false);
			}
		}

		return itemstack;
	}

	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		BlockPos pos = this.barrel.getPos();
		if(playerIn.world.getTileEntity(pos) != this.barrel) {
			return false;
		} else {
			return playerIn.getDistanceSq(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D) <= 64.0D;
		}
	}
}
