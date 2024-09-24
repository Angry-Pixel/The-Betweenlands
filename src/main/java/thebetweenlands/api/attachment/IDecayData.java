package thebetweenlands.api.attachment;

import net.minecraft.world.entity.player.Player;

/// Here for compatibility(?) reasons, may not be usable by mods other than Betweenlands, due to the capability/attachment changes
public interface IDecayData {

	/**
	 * Returns the maximum player health with the specified decay level
	 * @param player
	 * @param decayLevel
	 * @return
	 */
	float getPlayerMaxHealthPenalty(Player player, int decayLevel);

	/**
	 * Returns the percentage of the maximum player health removed by the specified decay level
	 * @param player
	 * @param decayLevel
	 * @return
	 */
	default float getPlayerMaxHealthPenaltyPercentage(Player player, int decayLevel) {
		return this.getPlayerMaxHealthPenalty(player, decayLevel) / player.getMaxHealth();
	}

	/**
	 * Returns whether decay is currently enabled for this player
	 * @param player
	 * @return
	 */
	boolean isDecayEnabled(Player player);

//
//	These methods are unused, so I disabled them to avoid confusion.
//	TODO Talk to team about defining behaviour for them.
//	(There may already be behaviour as simply a getter, but then the name could lead to deceiving the reader)
//
//	/**
//	 * Returns how much health has been removed from the player
//	 * @return
//	 */
//	public int getRemovedHealth();
//
//	/**
//	 * Sets how much health has been removed from the player
//	 * @param health
//	 */
//	public void setRemovedHealth(int health);
//

	/**
	 * Adds or removes decay.
	 * Negative decay increases "decay saturation" (a buffer before you lose decay levels) and decreases the decay level, while positive decay decreases decay saturation and increases the decay level.
	 * @param player
	 * @param decay Decay to be added
	 * @param decaySaturation
	 */
	void addStats(Player player, int decay, float decaySaturation);

	/**
	 * Update decay stats.
	 * @param player
	 */
	void tick(Player player);


	/**
	 * Returns the decay level (0 = no decay, 20 = maximum decay)
	 * @return
	 **/
	int getDecayLevel(Player player);

	/**
	 * Returns the decay level in the previous tick
	 * @return
	 */
	int getPrevDecayLevel();

	/**
	 * Returns the decay saturation level (higher = slower decay rate)
	 * @return
	 */
	float getSaturationLevel();

	/**
	 * Returns the decay acceleration level
	 * @return
	 */
	float getAccelerationLevel();

	/**
	 * Sets the decay level
	 * @param decay
	 */
	void setDecayLevel(Player player, int decay);

	/**
	 * Sets the decay saturation level (higher = slower decay rate)
	 * @param saturation
	 */
	void setDecaySaturationLevel(Player player, float saturation);

	/**
	 * Adds decay acceleration (once >= 4 is accumulated the decay level is increased by one)
	 * @param acceleration
	 */
	void addDecayAcceleration(Player player, float acceleration);

}
