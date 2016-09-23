package thebetweenlands.common.entity.capability;

import thebetweenlands.common.entity.capability.base.ISerializableEntityCapability;

public interface IDecayCapability extends ISerializableEntityCapability {
	/**
	 * Returns the decay stats
	 * @return
	 */
	public DecayStats getDecayStats();

	/**
	 * Returns the maximum player health with the current decay level
	 * @return
	 */
	public float getMaxPlayerHealth();

	/**
	 * Returns whether decay is currently enabled for this player
	 * @return
	 */
	public boolean isDecayEnabled();
}
