package thebetweenlands.common.item.food;

import net.minecraft.item.ItemFood;


public class ItemBLFood extends ItemFood {
    public ItemBLFood(int healAmount, float saturationModifier, boolean isWolfsFavoriteMeat) {
        super(healAmount, saturationModifier, isWolfsFavoriteMeat);
        this.setCreativeTab(null);
    }
}
