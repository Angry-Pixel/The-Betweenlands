package thebetweenlands.common.item.food;

import net.minecraft.item.ItemStack;
import thebetweenlands.api.item.IDecayFood;

public class ItemSapJello extends ItemBLFood implements IDecayFood {
    public ItemSapJello() {
        super(4, 0.9f, false);
        setAlwaysEdible();
    }

    @Override
    public int getDecayHealAmount(ItemStack stack) {
        return 4;
    }
}
