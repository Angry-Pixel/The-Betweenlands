package thebetweenlands.items.food;

import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import thebetweenlands.manual.gui.entries.IManualEntryItem;

/**
 * Created by Bart on 9-11-2015.
 */
public class ItemBLFood extends ItemFood implements IManualEntryItem{
    public ItemBLFood(int healAmount, float saturationModifier, boolean isWolfsFavoriteMeat) {
        super(healAmount, saturationModifier, isWolfsFavoriteMeat);
    }

    @Override
    public String manualName(int meta) {
        return "itemFood";
    }

    @Override
    public Item getItem() {
        return this;
    }

    @Override
    public int[] recipeType(int meta) {
        return new int[]{1, 2};
    }

    @Override
    public int[] metas() {
        return new int[0];
    }
}
