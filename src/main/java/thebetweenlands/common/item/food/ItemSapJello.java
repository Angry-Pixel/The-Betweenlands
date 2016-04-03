package thebetweenlands.common.item.food;

import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;

public class ItemSapJello extends ItemFood {
    public ItemSapJello() {
        super(4, 15f, false);
        setAlwaysEdible();
    }

    public int getDecayHealAmount(ItemStack stack) {
        return 4;
    }
}
