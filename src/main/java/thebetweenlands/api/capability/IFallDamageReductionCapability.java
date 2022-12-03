package thebetweenlands.api.capability;

public interface IFallDamageReductionCapability {
	/**
	 * Returns whether the entity is effected
	 * @return
	 */
	public boolean isActive();

	/**
	 * Returns for how many ticks the entity will have a reduced rate of damage
	 * @return
	 */
	public int getRemainingActiveTicks();
	
	/**
	 * Makes the entity fall damage reduced
	 * @param duration Duration in ticks
	 */
	public void setActive(int duration);
	
	/**
	 * Makes the entity no longer effected
	 */
	public void setNotActive();
	
}