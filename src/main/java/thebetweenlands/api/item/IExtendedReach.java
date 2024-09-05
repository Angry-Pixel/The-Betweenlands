package thebetweenlands.api.item;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.world.entity.Entity;
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
     * Called when the item is swung, return false on the server to cancel extra attack processing
     * @param player	The player swinging the weapon
     * @param stack		The held item
     * @param entities	List of entities in the direct line of the player's swing
     * @return false to cancel extra attack processing
     */
    default boolean onSwing(@Nonnull Player player, ItemStack stack, @Nullable List<Entity> entities) {
    	// Pass
    	return true;
    }
}
