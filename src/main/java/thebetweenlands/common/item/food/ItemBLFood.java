package thebetweenlands.common.item.food;

import net.minecraft.item.ItemFood;

/**
 * Created by Bart on 03/04/2016.
 */
public class ItemBLFood extends ItemFood {
    public ItemBLFood(int healAmount, float saturationModifier, boolean isWolfsFavoriteMeat) {
        super(healAmount, saturationModifier, isWolfsFavoriteMeat);
        this.setCreativeTab(null);
    }
}
