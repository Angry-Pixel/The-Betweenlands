package thebetweenlands.api.capability;

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
}
