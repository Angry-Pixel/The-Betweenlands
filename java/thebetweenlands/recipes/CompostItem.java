package thebetweenlands.recipes;

import net.minecraft.item.Item;

/**
 * Created by Bart on 13-7-2015.
 */
public class CompostItem{
    int compostAmount;
    Item compostItem;
    int itemDamage;

    public CompostItem(int compostAmount, Item compostItem, int itemDamage){
        this.compostAmount = compostAmount;
        this.compostItem = compostItem;
        this.itemDamage = itemDamage;
    }

    public CompostItem(int compostAmount, Item compostItem){
        this.compostAmount = compostAmount;
        this.compostItem = compostItem;
        this.itemDamage = 0;
    }
}
