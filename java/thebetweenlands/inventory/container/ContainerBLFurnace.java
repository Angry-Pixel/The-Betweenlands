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
import net.minecraft.tileentity.TileEntityFurnace;
import thebetweenlands.items.ItemMaterialsBL;
import thebetweenlands.items.ItemMaterialsBL.EnumMaterialsBL;
import thebetweenlands.tileentities.TileEntityBLFurnace;

public class ContainerBLFurnace extends Container {
    private TileEntityBLFurnace tileFurnace;
    private int lastCookTime;
    private int lastBurnTime;
    private int lastItemBurnTime;

    public ContainerBLFurnace(InventoryPlayer inventory, TileEntityBLFurnace tile) {
        tileFurnace = tile;
        addSlotToContainer(new Slot(tile, 0, 56, 17));
        addSlotToContainer(new Slot(tile, 1, 56, 53));
        addSlotToContainer(new SlotFurnace(inventory.player, tile, 2, 116, 35));
        int i;

        for (i = 0; i < 3; ++i)
            for (int j = 0; j < 9; ++j)
                addSlotToContainer(new Slot(inventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));

        for (i = 0; i < 9; ++i)
            addSlotToContainer(new Slot(inventory, i, 8 + i * 18, 142));
    }

	@Override
    public void addCraftingToCrafters(ICrafting crafter) {
        super.addCraftingToCrafters(crafter);
        crafter.sendProgressBarUpdate(this, 0, tileFurnace.furnaceCookTime);
        crafter.sendProgressBarUpdate(this, 1, tileFurnace.furnaceBurnTime);
        crafter.sendProgressBarUpdate(this, 2, tileFurnace.currentItemBurnTime);
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
        }

        lastCookTime = tileFurnace.furnaceCookTime;
        lastBurnTime = tileFurnace.furnaceBurnTime;
        lastItemBurnTime = tileFurnace.currentItemBurnTime;
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

            if (slotIndex == 2) {
                if (!mergeItemStack(itemstack1, 3, 39, true))
                    return null;
                slot.onSlotChange(itemstack1, itemstack);
            }
            else if (slotIndex != 1 && slotIndex != 0) {
                if (FurnaceRecipes.smelting().getSmeltingResult(itemstack1) != null) {
                    if (!mergeItemStack(itemstack1, 0, 1, false))
                        return null;
                }
                else if (TileEntityFurnace.isItemFuel(itemstack1) || itemstack1.getItem() instanceof ItemMaterialsBL && itemstack.getItemDamage() == EnumMaterialsBL.SULFUR.ordinal()) {
                    if (!mergeItemStack(itemstack1, 1, 2, false))
                        return null;
                }
                else if (slotIndex >= 3 && slotIndex < 30) {
                    if (!mergeItemStack(itemstack1, 30, 39, false))
                        return null;
                }
                else if (slotIndex >= 30 && slotIndex < 39 && !mergeItemStack(itemstack1, 3, 30, false))
                    return null;
            }
            else if (!mergeItemStack(itemstack1, 3, 39, false))
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
