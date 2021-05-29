package thebetweenlands.api.capability;

public interface IRotSmellCapability {
	/**
	 * Returns whether the entity smells bad (i.e. also not immune)
	 * @return
	 */
	public boolean isSmellingBad();

	/**
	 * Returns for how many ticks the entity will remain smelly (ignoring immunity)
	 * @return
	 */
	public int getRemainingSmellyTicks();
	
	/**
	 * Returns for how many ticks the entity will remain immune to smelliness
	 * @return
	 */
	public int getRemainingImmunityTicks();
	
	/**
	 * Makes the entity smell bad
	 * @param duration Duration in ticks
	 */
	public void setSmellingBad(int duration);
	
	/**
	 * Makes the entity no longer smell bad
	 */
	public void setNotSmellingBad();
	
	/**
	 * Makes the entity immune to smelliness
	 * @param duration Duration in ticks
	 */
	public void setImmune(int duration);
}
