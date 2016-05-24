package thebetweenlands.common.item.food;

import net.minecraft.item.ItemFood;


public class ItemBLFood extends ItemFood {
    public ItemBLFood(int healAmount, float saturationModifier, boolean isWolfsFavoriteMeat, String name) {
        super(healAmount, saturationModifier, isWolfsFavoriteMeat);
        this.setCreativeTab(null);
        setRegistryName(name);
        setUnlocalizedName(getRegistryName().toString());
    }
}
