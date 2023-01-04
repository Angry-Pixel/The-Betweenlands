package thebetweenlands.api.capability;

public interface IMudWalkerCapability {
	/**
	 * Returns whether the entity is effected
	 * @return
	 */
	public boolean isActive();

	/**
	 * Returns for how many ticks the entity will be immune to sinking
	 * @return
	 */
	public int getRemainingActiveTicks();
	
	/**
	 * Makes the entity able to mimic rubber boots' ability
	 * @param duration Duration in ticks
	 */
	public void setActive(int duration);
	
	/**
	 * Makes the entity no longer effected
	 */
	public void setNotActive();
	
}