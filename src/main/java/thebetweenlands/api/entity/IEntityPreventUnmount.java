package thebetweenlands.api.entity;

import net.minecraft.entity.player.EntityPlayer;

public interface IEntityPreventUnmount {
	/**
	 * Returns whether player controlled unmounting should be blocked
	 * @param rider
	 * @return
	 */
	public boolean isUnmountBlocked(EntityPlayer rider);
	
	/**
	 * Returns whether the unmount status bar text should be prevented
	 * @param rider
	 * @return
	 */
	public default boolean shouldPreventStatusBarText(EntityPlayer rider) {
		return true;
	}
	
	/**
	 * Called when player controlled unmounting was blocked.
	 * This may not necessarily be called during the player's update.
	 * @param rider
	 */
	public default void onUnmountBlocked(EntityPlayer rider) {
		
	}
}
