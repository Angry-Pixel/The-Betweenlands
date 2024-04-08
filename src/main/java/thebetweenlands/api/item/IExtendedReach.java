package thebetweenlands.api.item;

import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public interface IExtendedReach {

    /**
     * Returns the reach modifier in blocks
     * @return
     */
    double getReachModifier(@Nullable EntityPlayer player, ItemStack stack);
	
    /**
     * Returns the reach in blocks that the item will be able to hit
     * @return
     */
    default double getReach(@Nullable EntityPlayer player, ItemStack stack) {
    	if(player != null) {
    		//PlayerController#attackEntity() is called from Minecraft#clickMouse()
    		//Minecraft#clickMouse() uses Minecraft.objectMouseOver to determine if you're looking at an entity
    		//EntityRenderer#getMouseOver() is the only place Minecraft.objectMouseOver is assigned
    		//The raycast distance for Minecraft.objectMouseOver uses PlayerController#getBlockReachDistance()
    		final double reach = player.getEntityAttribute(EntityPlayer.REACH_DISTANCE).getAttributeValue();
    		return (player.isCreative() ? reach : reach - 0.5) + this.getReachModifier(player, stack);
    	} else {
    		//Assume survival mode (4.5 was assumed before this was introduced)
    		return 4.5 + this.getReachModifier(player, stack);
    	}
    }

    default void onLeftClick(EntityPlayer player, ItemStack stack) {
    	
    }
}

