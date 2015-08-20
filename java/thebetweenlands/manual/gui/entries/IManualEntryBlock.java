package thebetweenlands.manual.gui.entries;

import net.minecraft.block.Block;
import net.minecraft.item.Item;

/**
 * Created by Bart on 20-8-2015.
 */

//TODO implement this in all blocks
public interface IManualEntryBlock {

    //If any of these don't have to do with the block return null
    String manualStats(int meta);

    String manualLore(int meta);

    String manualTrivia(int meta);
}
