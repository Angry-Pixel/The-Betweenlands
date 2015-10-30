package thebetweenlands.manual.gui.entries;

import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;

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

    Item getItem();
}
