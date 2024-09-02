package thebetweenlands.api.item;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public interface IExtendedReach {
	
	/**
     * Returns the entity reach modifier in blocks<br/>
     * Note: Negative modifiers won't reduce a player's reach distance
     * @return
     */
	double getReachModifier(@Nullable Player player, ItemStack stack);

    /**
     * Returns the reach in blocks that the player will be able to hit entities from.<br/>
     * Note: Ranges less than {@code player.entityInteractionRange()} will have no effect
     * @return
     */
    default double getReach(@Nullable Player player, ItemStack stack) {
    	if(player != null) {
    		// blockInteractionRange() may be the better option in terms of comparing to the original code
    		return player.entityInteractionRange() + this.getReachModifier(player, stack);
    	} else {
    		// 4.5 was assumed previously, maintain parity here
    		return 4.5 + this.getReachModifier(player, stack);
    	}
    }
	
    /**
     * Called when the item is swung, return true on the server to cancel extra attack processing
     * @param player
     * @param stack
     * @return cancel
     */
    default boolean onSwing(@Nonnull Player player, ItemStack stack) {
    	// Pass
    	return false;
    }
}
