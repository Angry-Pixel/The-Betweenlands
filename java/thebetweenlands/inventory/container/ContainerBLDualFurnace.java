package thebetweenlands.inventory.container;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.inventory.SlotFurnace;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import thebetweenlands.inventory.slot.SlotRestriction;
import thebetweenlands.items.misc.ItemGeneric;
import thebetweenlands.items.misc.ItemGeneric.EnumItemGeneric;
import thebetweenlands.tileentities.TileEntityBLDualFurnace;

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
		addSlotToContainer(new Slot(tile, 1, 56, 57));
		addSlotToContainer(new SlotFurnace(inventory.player, tile, 2, 116, 39));
		Slot fluxSlot1 = new SlotRestriction(tile, 6, 26, 39, ItemGeneric.createStack(EnumItemGeneric.LIMESTONE_FLUX), 64);
		addSlotToContainer(fluxSlot1);

		addSlotToContainer(new Slot(tile, 3, 56, 92));
		addSlotToContainer(new Slot(tile, 4, 56, 128));
		addSlotToContainer(new SlotFurnace(inventory.player, tile, 5, 116, 110));
		Slot fluxSlot2 = new SlotRestriction(tile, 7, 26, 110, ItemGeneric.createStack(EnumItemGeneric.LIMESTONE_FLUX), 64);
		addSlotToContainer(fluxSlot2);

		int i;

		for (i = 0; i < 3; ++i)
			for (int j = 0; j < 9; ++j)
				addSlotToContainer(new Slot(inventory, j + i * 9 + 9, 8 + j * 18, 163 + i * 18));

		for (i = 0; i < 9; ++i)
			addSlotToContainer(new Slot(inventory, i, 8 + i * 18, 221));
	}

	@Override
	public void addCraftingToCrafters(ICrafting crafter) {
		super.addCraftingToCrafters(crafter);
		crafter.sendProgressBarUpdate(this, 0, tileFurnace.furnaceCookTime);
		crafter.sendProgressBarUpdate(this, 1, tileFurnace.furnaceBurnTime);
		crafter.sendProgressBarUpdate(this, 2, tileFurnace.currentItemBurnTime);

		crafter.sendProgressBarUpdate(this, 3, tileFurnace.furnaceCookTime2);
		crafter.sendProgressBarUpdate(this, 4, tileFurnace.furnaceBurnTime2);
		crafter.sendProgressBarUpdate(this, 5, tileFurnace.currentItemBurnTime2);
	}


	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();

		for (int i = 0; i < crafters.size(); ++i) {
			ICrafting icrafting = (ICrafting)crafters.get(i);

			if (lastCookTime != tileFurnace.furnaceCookTime)
				icrafting.sendProgressBarUpdate(this, 0, tileFurnace.furnaceCookTime);

			if (lastBurnTime != tileFurnace.furnaceBurnTime)
				icrafting.sendProgressBarUpdate(this, 1, tileFurnace.furnaceBurnTime);

			if (lastItemBurnTime != tileFurnace.currentItemBurnTime)
				icrafting.sendProgressBarUpdate(this, 2, tileFurnace.currentItemBurnTime);

			if (lastCookTime2 != tileFurnace.furnaceCookTime2)
				icrafting.sendProgressBarUpdate(this, 3, tileFurnace.furnaceCookTime2);

			if (lastBurnTime2 != tileFurnace.furnaceBurnTime2)
				icrafting.sendProgressBarUpdate(this, 4, tileFurnace.furnaceBurnTime2);

			if (lastItemBurnTime2 != tileFurnace.currentItemBurnTime2)
				icrafting.sendProgressBarUpdate(this, 5, tileFurnace.currentItemBurnTime2);
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
		return tileFurnace.isUseableByPlayer(player);
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int slotIndex) {
		ItemStack itemstack = null;
		Slot slot = (Slot)inventorySlots.get(slotIndex);

		if (slot != null && slot.getHasStack()) {
			ItemStack itemstack1 = slot.getStack();
			itemstack = itemstack1.copy();

			if (slotIndex == 2 || slotIndex == 5) {
				if (!mergeItemStack(itemstack1, 6, 42, true))
					return null;
				slot.onSlotChange(itemstack1, itemstack);
			}
			else if (slotIndex != 1 && slotIndex != 0 && slotIndex != 4 && slotIndex != 3) {
				if (FurnaceRecipes.smelting().getSmeltingResult(itemstack1) != null) {
					if (!mergeItemStack(itemstack1, 0, 1, false) && !mergeItemStack(itemstack1, 3, 4, false))
						return null;
				}
				else if (TileEntityBLDualFurnace.isItemFuel(itemstack1) || itemstack1.getItem() instanceof ItemGeneric && itemstack.getItemDamage() == EnumItemGeneric.SULFUR.id) {
					if (!mergeItemStack(itemstack1, 1, 2, false) && !mergeItemStack(itemstack1, 4, 5, false))
						return null;
				}
				else if (slotIndex >= 6 && slotIndex < 33) {
					if (!mergeItemStack(itemstack1, 33, 42, false))
						return null;
				}
				else if (slotIndex >= 33 && slotIndex < 42 && !mergeItemStack(itemstack1, 6, 33, false))
					return null;
			}
			else if (!mergeItemStack(itemstack1, 6, 42, false))
				return null;
			if (itemstack1.stackSize == 0)
				slot.putStack((ItemStack)null);
			else
				slot.onSlotChanged();
			if (itemstack1.stackSize == itemstack.stackSize)
				return null;

			slot.onPickupFromSlot(player, itemstack1);
		}
		return itemstack;
	}
}
