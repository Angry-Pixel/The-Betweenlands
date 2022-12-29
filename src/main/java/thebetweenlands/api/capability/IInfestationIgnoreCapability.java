package thebetweenlands.api.capability;

public interface IInfestationIgnoreCapability {
	/**
	 * Returns whether the entity is immune (pheromones are active)
	 * @return
	 */
	public boolean isImmune();

	/**
	 * Returns for how many ticks the entity will remain immune to aggro from infestations
	 * @return
	 */
	public int getRemainingImmunityTicks();
	
	/**
	 * Makes the entity immune to infestation aggro
	 * @param duration Duration in ticks
	 */
	public void setImmune(int duration);
	
	/**
	 * Makes the entity no longer immune to infestation aggro
	 */
	public void setNotImmune();
	
}