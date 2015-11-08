package thebetweenlands.items.misc;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import thebetweenlands.TheBetweenlands;
import thebetweenlands.proxy.CommonProxy;

import java.util.List;

/**
 * Created by Bart on 8-8-2015.
 */
public class ItemManual extends Item {

    @Override
    public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer player) {
        player.openGui(TheBetweenlands.instance, CommonProxy.GUI_MANUAL, world, (int) player.posX, (int) player.posY, (int) player.posZ);
        return itemStack;
    }
    


}
