package thebetweenlands.common.inventory.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.inventory.SlotFurnaceOutput;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.tileentity.TileEntityFurnace;
import thebetweenlands.common.inventory.slot.SlotBLFurnaceFuel;
import thebetweenlands.common.inventory.slot.SlotRestriction;
import thebetweenlands.common.item.misc.ItemMisc;
import thebetweenlands.common.item.misc.ItemMisc.EnumItemMisc;
import thebetweenlands.common.tile.TileEntityBLDualFurnace;

public class ContainerBLDualFurnace extends ContainerAbstractBLFurnace {

	public ContainerBLDualFurnace(InventoryPlayer inventory, TileEntityBLDualFurnace tile) {
		super(tile);

		addSlotToContainer(new Slot(tile, 0, 56, 21));
		addSlotToContainer(new SlotBLFurnaceFuel(tile, 1, 56, 57));
		addSlotToContainer(new SlotFurnaceOutput(inventory.player, tile, 2, 116, 39));
		addSlotToContainer(new SlotRestriction(tile, 3, 26, 39, EnumItemMisc.LIMESTONE_FLUX.create(1), 64, this));

		addSlotToContainer(new Slot(tile, 4, 56, 92));
		addSlotToContainer(new SlotBLFurnaceFuel(tile, 5, 56, 128));
		addSlotToContainer(new SlotFurnaceOutput(inventory.player, tile, 6, 116, 110));
		addSlotToContainer(new SlotRestriction(tile, 7, 26, 110, EnumItemMisc.LIMESTONE_FLUX.create(1), 64, this));

		int i;
		for (i = 0; i < 3; ++i)
			for (int j = 0; j < 9; ++j)
				addSlotToContainer(new Slot(inventory, j + i * 9 + 9, 8 + j * 18, 163 + i * 18));

		for (i = 0; i < 9; ++i)
			addSlotToContainer(new Slot(inventory, i, 8 + i * 18, 221));
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int slotIndex) {
		ItemStack itemstack = ItemStack.EMPTY;
		Slot slot = inventorySlots.get(slotIndex);

		if (slot != null && slot.getHasStack()) {
			ItemStack itemstack1 = slot.getStack();
			itemstack = itemstack1.copy();

			if (slotIndex == 2 || slotIndex == 6) {
				if (!mergeItemStack(itemstack1, 8, 44, true))
					return ItemStack.EMPTY;
				slot.onSlotChange(itemstack1, itemstack);
			}
			else if (slotIndex != 1 && slotIndex != 0 && slotIndex != 3 && slotIndex != 4 && slotIndex != 5 && slotIndex != 7) {
				if (!FurnaceRecipes.instance().getSmeltingResult(itemstack1).isEmpty()) {
					if (!mergeItemStack(itemstack1, 0, 1, false) && !mergeItemStack(itemstack1, 4, 5, false))
						return ItemStack.EMPTY;
				}
				else if (TileEntityFurnace.isItemFuel(itemstack1) || itemstack1.getItem() instanceof ItemMisc && itemstack.getItemDamage() == EnumItemMisc.SULFUR.getID()) {
					if (!mergeItemStack(itemstack1, 1, 2, false) && !mergeItemStack(itemstack1, 5, 6, false))
						return ItemStack.EMPTY;
				}
				 else if (itemstack1.getItem() instanceof ItemMisc && itemstack.getItemDamage() == EnumItemMisc.LIMESTONE_FLUX.getID()) {
	                    if (!mergeItemStack(itemstack1, 3, 4, false) && !mergeItemStack(itemstack1, 7, 8, false))
	                        return ItemStack.EMPTY;
	                }
				else if (slotIndex >= 8 && slotIndex < 35) {
					if (!mergeItemStack(itemstack1, 35, 44, false))
						return ItemStack.EMPTY;
				}
				else if (slotIndex >= 35 && slotIndex < 44 && !mergeItemStack(itemstack1, 8, 35, false))
					return ItemStack.EMPTY;
			}
			else if (!mergeItemStack(itemstack1, 8, 44, false))
				return ItemStack.EMPTY;
			if (itemstack1.getCount() == 0)
				slot.putStack(ItemStack.EMPTY);
			else
				slot.onSlotChanged();
			if (itemstack1.getCount() == itemstack.getCount())
				return ItemStack.EMPTY;

			slot.onTake(player, itemstack1);
		}
		return itemstack;
	}
}
