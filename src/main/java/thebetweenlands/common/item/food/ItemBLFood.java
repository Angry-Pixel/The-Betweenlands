package thebetweenlands.common.item.food;

import net.minecraft.item.ItemFood;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.item.IFoodSickness;


public class ItemBLFood extends ItemFood implements IFoodSickness {
    public ItemBLFood(int healAmount, float saturationModifier, boolean isWolfsFavoriteMeat) {
        super(healAmount, saturationModifier, isWolfsFavoriteMeat);
        this.setCreativeTab(BLCreativeTabs.ITEMS);
    }
}
