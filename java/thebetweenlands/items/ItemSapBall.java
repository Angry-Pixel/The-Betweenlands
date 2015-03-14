package thebetweenlands.items;

import net.minecraft.item.ItemFood;
import thebetweenlands.utils.IDecayFood;

public class ItemSapBall extends ItemFood implements IDecayFood
{
    public ItemSapBall()
    {
        super(0, 0f, false);
        setAlwaysEdible();
    }

    public int getDecayHealAmount()
    {
        return 4;
    }
}
