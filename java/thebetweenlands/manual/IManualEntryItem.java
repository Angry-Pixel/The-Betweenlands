package thebetweenlands.manual;

import net.minecraft.item.Item;


/**
 * Created by Bart on 20-8-2015.
 */
//TODO implement this in all items
public interface IManualEntryItem {

    String manualName(int meta);

    Item getItem();


    /*
    0 = no recipe
    1 = furnace
    2 = crafting
    3 = pam
    4 = compost
    5 = purifier
    6 = loot
     */
    int[] recipeType(int meta);


    int metas();
}
