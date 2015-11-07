package thebetweenlands.items.food;

import net.minecraft.item.ItemFood;
import thebetweenlands.utils.IDecayFood;

public class ItemSapJello extends ItemFood implements IDecayFood
{
    public ItemSapJello()
    {
        super(4, 15f, false);
        setAlwaysEdible();
    }

    public int getDecayHealAmount()
    {
        return 4;
    }
}
