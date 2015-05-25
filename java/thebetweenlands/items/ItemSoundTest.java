package thebetweenlands.items;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

/**
 * Created by Bart on 25-5-2015.
 */
public class ItemSoundTest extends Item {

    @Override
    public boolean onItemUse(ItemStack is, EntityPlayer player, World world, int x, int y, int z, int meta, float hitX, float hitY, float hitZ) {
        if (!world.isRemote) {
            world.playSoundAtEntity(player, "thebetweenlands:portal", 1f, 1f);
            return true;
        }
        return false;
    }
}
