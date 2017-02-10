package thebetweenlands.api.capability;

public interface ISummoningCapability {
	/**
	 * Sets whether the entity is summoning
	 * @param active
	 */
	public void setActive(boolean active);

	/**
	 * Returns whether the entity is summoning
	 * @return
	 */
	public boolean isActive();

	/**
	 * Returns the cooldown
	 * @return
	 */
	public int getCooldownTicks();

	/**
	 * Sets the cooldown
	 * @param ticks
	 */
	public void setCooldownTicks(int ticks);

	/**
	 * Returns how long the summoning is active
	 * @return
	 */
	public int getActiveTicks();

	/**
	 * Sets how long the summoning is active
	 * @param ticks
	 */
	public void setActiveTicks(int ticks);
}
