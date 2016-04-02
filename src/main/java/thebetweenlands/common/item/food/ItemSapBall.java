package thebetweenlands.common.item.food;

import net.minecraft.item.ItemStack;

/**
 * Created by Bart on 03/04/2016.
 */
public class ItemSapBall extends ItemBLFood {
    public ItemSapBall() {
        super(0, 0f, false);
        setAlwaysEdible();
    }

    public int getDecayHealAmount(ItemStack stack) {
        return 2;
    }
}
