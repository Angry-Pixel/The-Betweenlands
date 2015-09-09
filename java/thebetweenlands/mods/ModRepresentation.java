package thebetweenlands.mods;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.item.Item;

/**
 * Created by lukas on 09.09.15.
 */
public class ModRepresentation
{
    public static Block block(String modID, String id)
    {
        return GameRegistry.findBlock(modID, id);
    }

    public static String id(Block block)
    {
        return Block.blockRegistry.getNameForObject(block);
    }

    public static Item item(String modID, String id)
    {
        return GameRegistry.findItem(modID, id);
    }

    public static String id(Item item)
    {
        return Item.itemRegistry.getNameForObject(item);
    }
}
