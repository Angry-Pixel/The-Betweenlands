package thebetweenlands.api.capability;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import thebetweenlands.common.item.equipment.ItemRing;

public interface IFlightCapability {
	/**
	 * Returns whether the entity is flying
	 * @return
	 */
	public boolean isFlying();
	
	/**
	 * Sets whether the entity is allowed to fly
	 * @param flying
	 */
	public void setFlying(boolean flying);
	
	/**
	 * Returns how long the entity has been in flight
	 * @return
	 */
	public int getFlightTime();
	
	/**
	 * Sets how long the entity has been in flight
	 * @param ticks
	 */
	public void setFlightTime(int ticks);
	
	/**
	 * Sets whether the entity has the Ring of Flight
	 * @param ring
	 */
	public void setFlightRing(boolean ring);
	
	/**
	 * Returns whether the entity has the Ring of Flight
	 * @return
	 */
	public boolean getFlightRing();
	
	/**
	 * Returns whether the player can fly using the ring
	 * @param player
	 * @param ring
	 * @return
	 */
	public default boolean canFlyWithRing(EntityPlayer player, ItemStack ring) {
		return (player.experienceTotal > 0 || player.experience > 0 || player.experienceLevel > 0) && !player.isRiding();
	}
	
	/**
	 * Returns whether the player can fly without a ring
	 * @param player
	 * @param stack
	 * @return
	 */
	public default boolean canFlyWithoutRing(EntityPlayer player) {
		return player.capabilities.isCreativeMode || player.isSpectator();
	}
	
	/**
	 * Called every tick when the player is flying with the ring
	 * @param player
	 * @param ring
	 */
	public default void onFlightTick(EntityPlayer player, ItemStack ring, boolean firstTick) {
		if(!player.world.isRemote && (firstTick || player.ticksExisted % 20 == 0)) {
			ItemRing.removeXp(player, 2);
		}
	}
}
