package thebetweenlands.common.item.food;

import net.minecraft.item.ItemStack;

public class ItemSapJello extends ItemBLFood {
    public ItemSapJello() {
        super(4, 15f, false);
        setAlwaysEdible();
    }

    public int getDecayHealAmount(ItemStack stack) {
        return 4;
    }
}
