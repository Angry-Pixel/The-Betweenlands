package thebetweenlands.common.item.food;

import net.minecraft.item.ItemStack;

public class ItemSapBall extends ItemBLFood {
    public ItemSapBall() {
        super(0, 0f, false, "sapBall");
        setAlwaysEdible();
    }

    public int getDecayHealAmount(ItemStack stack) {
        return 2;
    }
}
