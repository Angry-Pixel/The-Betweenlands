package thebetweenlands.common.inventory.slot;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import thebetweenlands.common.item.tools.ItemPestle;

public class SlotPestle extends Slot {

    public SlotPestle(IInventory inventory, int slotIndex, int x, int y) {
        super(inventory, slotIndex, x, y);
        slotNumber = slotIndex;
    }

    @Override
    public boolean isItemValid(ItemStack stack) {
        return stack.getItem() instanceof ItemPestle;
    }

    @Override
    public ItemStack onTake(EntityPlayer player, ItemStack stack) {
        if (stack != null && slotNumber == 1)
            if(stack.getTagCompound().getBoolean("active"))
                stack.getTagCompound().setBoolean("active", false);
        return super.onTake(player, stack);
    }
}