package thebetweenlands.common.inventory.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.*;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.inventory.slot.SlotBLFurnaceFuel;
import thebetweenlands.common.inventory.slot.SlotRestriction;
import thebetweenlands.common.item.misc.ItemMisc;
import thebetweenlands.common.item.misc.ItemMisc.EnumItemMisc;
import thebetweenlands.common.tile.TileEntityBLDualFurnace;

public class ContainerBLDualFurnace extends Container {
	private TileEntityBLDualFurnace tileFurnace;
	private int lastCookTime;
	private int lastBurnTime;
	private int lastItemBurnTime;

	private int lastCookTime2;
	private int lastBurnTime2;
	private int lastItemBurnTime2;

	public ContainerBLDualFurnace(InventoryPlayer inventory, TileEntityBLDualFurnace tile) {
		tileFurnace = tile;
		addSlotToContainer(new Slot(tile, 0, 56, 21));
		addSlotToContainer(new SlotBLFurnaceFuel(tile, 1, 56, 57));
		addSlotToContainer(new SlotFurnaceOutput(inventory.player, tile, 2, 116, 39));
		Slot fluxSlot1 = new SlotRestriction(tile, 3, 26, 39, EnumItemMisc.LIMESTONE_FLUX.create(1), 64, this);
		addSlotToContainer(fluxSlot1);

		addSlotToContainer(new Slot(tile, 4, 56, 92));
		addSlotToContainer(new SlotBLFurnaceFuel(tile, 5, 56, 128));
		addSlotToContainer(new SlotFurnaceOutput(inventory.player, tile, 6, 116, 110));
		Slot fluxSlot2 = new SlotRestriction(tile, 7, 26, 110, EnumItemMisc.LIMESTONE_FLUX.create(1), 64, this);
		addSlotToContainer(fluxSlot2);

		int i;

		for (i = 0; i < 3; ++i)
			for (int j = 0; j < 9; ++j)
				addSlotToContainer(new Slot(inventory, j + i * 9 + 9, 8 + j * 18, 163 + i * 18));

		for (i = 0; i < 9; ++i)
			addSlotToContainer(new Slot(inventory, i, 8 + i * 18, 221));
	}

	@Override
	public void addListener(IContainerListener listener) {
		super.addListener(listener);
		listener.sendWindowProperty(this, 0, tileFurnace.furnaceCookTime);
		listener.sendWindowProperty(this, 1, tileFurnace.furnaceBurnTime);
		listener.sendWindowProperty(this, 2, tileFurnace.currentItemBurnTime);

		listener.sendWindowProperty(this, 3, tileFurnace.furnaceCookTime2);
		listener.sendWindowProperty(this, 4, tileFurnace.furnaceBurnTime2);
		listener.sendWindowProperty(this, 5, tileFurnace.currentItemBurnTime2);
	}


	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();

        for (IContainerListener listener : listeners) {

			if (lastCookTime != tileFurnace.furnaceCookTime)
            	listener.sendWindowProperty(this, 0, tileFurnace.furnaceCookTime);

			if (lastBurnTime != tileFurnace.furnaceBurnTime)
            	listener.sendWindowProperty(this, 1, tileFurnace.furnaceBurnTime);

			if (lastItemBurnTime != tileFurnace.currentItemBurnTime)
            	listener.sendWindowProperty(this, 2, tileFurnace.currentItemBurnTime);

			if (lastCookTime2 != tileFurnace.furnaceCookTime2)
            	listener.sendWindowProperty(this, 3, tileFurnace.furnaceCookTime2);

			if (lastBurnTime2 != tileFurnace.furnaceBurnTime2)
            	listener.sendWindowProperty(this, 4, tileFurnace.furnaceBurnTime2);

			if (lastItemBurnTime2 != tileFurnace.currentItemBurnTime2)
            	listener.sendWindowProperty(this, 5, tileFurnace.currentItemBurnTime2);
		}

		lastCookTime = tileFurnace.furnaceCookTime;
		lastBurnTime = tileFurnace.furnaceBurnTime;
		lastItemBurnTime = tileFurnace.currentItemBurnTime;

		lastCookTime2 = tileFurnace.furnaceCookTime2;
		lastBurnTime2 = tileFurnace.furnaceBurnTime2;
		lastItemBurnTime2 = tileFurnace.currentItemBurnTime2;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void updateProgressBar(int id, int value) {
		if (id == 0)
			tileFurnace.furnaceCookTime = value;

		if (id == 1)
			tileFurnace.furnaceBurnTime = value;

		if (id == 2)
			tileFurnace.currentItemBurnTime = value;

		if (id == 3)
			tileFurnace.furnaceCookTime2 = value;

		if (id == 4)
			tileFurnace.furnaceBurnTime2 = value;

		if (id == 5)
			tileFurnace.currentItemBurnTime2 = value;
	}

	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return tileFurnace.isUsableByPlayer(player);
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int slotIndex) {
		ItemStack itemstack = ItemStack.EMPTY;
		Slot slot = (Slot)inventorySlots.get(slotIndex);

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
				else if (TileEntityBLDualFurnace.isItemFuel(itemstack1) || itemstack1.getItem() instanceof ItemMisc && itemstack.getItemDamage() == EnumItemMisc.SULFUR.getID()) {
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
