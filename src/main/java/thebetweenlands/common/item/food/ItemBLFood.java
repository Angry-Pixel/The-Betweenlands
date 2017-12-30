package thebetweenlands.common.item.food;

import net.minecraft.item.ItemFood;
import thebetweenlands.api.item.IFoodSicknessItem;
import thebetweenlands.client.tab.BLCreativeTabs;


public class ItemBLFood extends ItemFood implements IFoodSicknessItem {
    public ItemBLFood(int healAmount, float saturationModifier, boolean isWolfsFavoriteMeat) {
        super(healAmount, saturationModifier, isWolfsFavoriteMeat);
        this.setCreativeTab(BLCreativeTabs.ITEMS);
    }
}
