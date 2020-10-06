package thebetweenlands.api.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public interface IExtendedReach {

    /**
     * Returns the reach in blocks that the item will be able to hit
     * @return
     */
    double getReach();

    default void onLeftClick(EntityPlayer player, ItemStack stack) {
    	
    }
}

