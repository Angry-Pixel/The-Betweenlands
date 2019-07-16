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
import thebetweenlands.common.tile.TileEntityBLFurnace;

public class ContainerBLFurnace extends ContainerAbstractBLFurnace {

    public ContainerBLFurnace(InventoryPlayer inventory, TileEntityBLFurnace tile) {
        super(tile);

        addSlotToContainer(new Slot(tile, 0, 56, 17));
        addSlotToContainer(new SlotBLFurnaceFuel(tile, 1, 56, 53));
        addSlotToContainer(new SlotFurnaceOutput(inventory.player, tile, 2, 116, 35));
        addSlotToContainer(new SlotRestriction(tile, 3, 26, 35, EnumItemMisc.LIMESTONE_FLUX.create(1), 64, this));

        int i;
        for (i = 0; i < 3; ++i)
            for (int j = 0; j < 9; ++j)
                addSlotToContainer(new Slot(inventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));

        for (i = 0; i < 9; ++i)
            addSlotToContainer(new Slot(inventory, i, 8 + i * 18, 142));
    }

	@Override
    public ItemStack transferStackInSlot(EntityPlayer player, int slotIndex) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = inventorySlots.get(slotIndex);

        if (slot != null && slot.getHasStack()) {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();

            if (slotIndex == 2) {
                if (!mergeItemStack(itemstack1, 3, 39, true))
                    return ItemStack.EMPTY;
                slot.onSlotChange(itemstack1, itemstack);
            }
            else if (slotIndex != 1 && slotIndex != 0 && slotIndex != 3) {
                if (!FurnaceRecipes.instance().getSmeltingResult(itemstack1).isEmpty()) {
                    if (!mergeItemStack(itemstack1, 0, 1, false))
                        return ItemStack.EMPTY;
                }
                else if (TileEntityFurnace.isItemFuel(itemstack1) || itemstack1.getItem() instanceof ItemMisc && itemstack.getItemDamage() == EnumItemMisc.SULFUR.getID()) {
                    if (!mergeItemStack(itemstack1, 1, 2, false))
                        return ItemStack.EMPTY;
                }
                else if (itemstack1.getItem() instanceof ItemMisc && itemstack.getItemDamage() == EnumItemMisc.LIMESTONE_FLUX.getID()) {
                    if (!mergeItemStack(itemstack1, 3, 4, false))
                        return ItemStack.EMPTY;
                }
                else if (slotIndex >= 3 && slotIndex <= 30) {
                    if (!mergeItemStack(itemstack1, 31, 40, false))
                        return ItemStack.EMPTY;
                }
                else if (slotIndex >= 31 && slotIndex < 40 && !mergeItemStack(itemstack1, 4, 30, false))
                    return ItemStack.EMPTY;
            }
            else if (!mergeItemStack(itemstack1, 4, 39, false))
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
