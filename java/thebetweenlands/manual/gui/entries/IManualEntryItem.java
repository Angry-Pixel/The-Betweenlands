package thebetweenlands.manual.gui.entries;

import net.minecraft.item.Item;

/**
 * Created by Bart on 20-8-2015.
 */
//TODO implement this in all items
public interface IManualEntryItem {
    //If any of these don't have to do with the Item return null


    /* UNUSED
    String manualStats(int meta);

    String manualLore(int meta);

    String manualTrivia(int meta);
    */

    String manualName(int meta);

    //don't return null plz
    Item getItem();


    /*
    0 = furnace
    1 = purifier
    2 = pam
    3 = compost
    4 = crafting/no recipe
     */
    int recipeType(int meta);
}
