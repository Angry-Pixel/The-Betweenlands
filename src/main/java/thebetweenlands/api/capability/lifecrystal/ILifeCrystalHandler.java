package thebetweenlands.api.capability.lifecrystal;

public interface ILifeCrystalHandler {

	/**
	 * Returns the amount of life power available
	 * 
	 * @return The amount of life power available
	 */
	public int getLifePower();
	
	/**
	 * Returns the maximum amount of life power that could be stored
	 * 
	 * @return The maximum amount of life power that could be stored
	 */
	public int getMaxLifePower();

	/**
	 * <p>
	 * Increases life power by the given amount and returns the remainder.
	 * </p>
	 * @param power    The amount to charge the crystal by
	 * @param simulate If true, the charge is only simulated
	 * @return The remaining charge that was not accepted
	 *         (if all the charge was accepted, return 0)
	 */
	public int chargeLifePower(int power, boolean simulate);
	
	/**
	 * <p>
	 * Drains life power from the crystal and returns the amount drained.
	 * </p>
	 * @param power    The amount of life power to drain
	 * @param simulate If true, the drain is only simulated
	 * @return The amount of life power that was drained from the crystal.
	 */
	public int drainLifePower(int power, boolean simulate);
	
}
