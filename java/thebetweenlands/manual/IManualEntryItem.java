package thebetweenlands.manual;

import net.minecraft.item.Item;


/**
 * Created by Bart on 20-8-2015.
 */
//TODO implement this in all items
public interface IManualEntryItem {
    /**
     * The manual name of the item
     *
     * @param meta the meta of the item
     * @return
     */
    String manualName(int meta);

    /**
     * The acctual item
     *
     * @return
     */
    Item getItem();

    /**
     * @param meta 0 = no recipe
     *             1 = furnace
     *             2 = crafting
     *             3 = pam
     *             4 = compost
     *             5 = purifier
     *             6 = loot
     * @return
     */
    int[] recipeType(int meta);

    /**
     * The amount of meta values the item has
     *
     * @return
     */
    int metas();
}
