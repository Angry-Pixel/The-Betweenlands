package thebetweenlands.common.inventory.slot;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import thebetweenlands.common.item.misc.ItemSwampTalisman;
import thebetweenlands.common.recipe.misc.DruidAltarRecipe;

public class SlotDruidAltar extends Slot {

    public SlotDruidAltar(IInventory inventory, int slotIndex, int x, int y) {
        super(inventory, slotIndex, x, y);
        slotNumber = slotIndex;
    }

    @Override
    public boolean isItemValid(ItemStack stack) {
        //Player should not be able to put the talisman back in
        return slotNumber != 0 && (slotNumber > 0 && slotNumber <= 4 && ((stack.getItem() instanceof ItemSwampTalisman && stack.getItemDamage() != ItemSwampTalisman.EnumTalisman.SWAMP_TALISMAN_5.ordinal() && stack.getItemDamage() != ItemSwampTalisman.EnumTalisman.SWAMP_TALISMAN_0.ordinal()) || DruidAltarRecipe.isValidItem(stack)));
    }
}
