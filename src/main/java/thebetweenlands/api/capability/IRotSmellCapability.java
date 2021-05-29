package thebetweenlands.api.capability;

public interface IRotSmellCapability {
	/**
	 * Returns whether the entity smells bad
	 * @return
	 */
	public boolean isSmellingBad();

	/**
	 * Returns for how many ticks the entity will remain smelly
	 * @return
	 */
	public int getRemainingSmellyTicks();
	
	/**
	 * Makes the entity smell bad
	 * @param duration Duration in ticks
	 */
	public void setSmellingBad(int duration);
	
	/**
	 * Makes the entity no longer smell bad
	 */
	public void setNotSmellingBad();
}
