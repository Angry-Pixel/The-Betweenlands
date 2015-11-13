package thebetweenlands.items.food;

import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import thebetweenlands.manual.gui.entries.IManualEntryItem;
import thebetweenlands.utils.IDecayFood;

public class ItemSapJello extends ItemFood implements IDecayFood, IManualEntryItem
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

    @Override
    public String manualName(int meta) {
        return "sapJello";
    }

    @Override
    public Item getItem() {
        return this;
    }

    @Override
    public int[] recipeType(int meta) {
        return new int[]{2};
    }

    @Override
    public int metas() {
        return 0;
    }
}
