package thebetweenlands.api.capability;

import thebetweenlands.common.capability.decay.DecayStats;

public interface IDecayCapability {
	/**
	 * Returns the decay stats
	 * @return
	 */
	public DecayStats getDecayStats();

	/**
	 * Returns the maximum player health with the specified decay level
	 * @param decayLevel
	 * @return
	 */
	public float getMaxPlayerHealth(int decayLevel);

	/**
	 * Returns the percentage of the maximum player health with the specified decay level
	 * @param decayLevel
	 * @return
	 */
	public default float getMaxPlayerHealthPercentage(int decayLevel) {
		return this.getMaxPlayerHealth(decayLevel) / 20.0f;
	}
	
	/**
	 * Returns whether decay is currently enabled for this player
	 * @return
	 */
	public boolean isDecayEnabled();
	
	/**
	 * Returns how much health has been removed from the player
	 * @return
	 */
	public int getRemovedHealth();
	
	/**
	 * Sets how much health has been removed from the player
	 * @param health
	 */
	public void setRemovedHealth(int health);
}
