package thebetweenlands.api.capability.corrosion;

import thebetweenlands.api.item.CorrosionHelper;

public interface ICorrosionHandler {

	/**
	 * Returns the maximum amount of coating points this capability can support (default = {@linkplain CorrosionHelper#MAX_CORROSION}).
	 * 
	 * <p>Calls to this method should never modify the handler.</p>
	 * @return the maximum amount of coating
	 */
	public int getMaxCoating();

	/**
	 * Returns the maximum amount of corrosion points this capability can support (default = {@linkplain CorrosionHelper#MAX_COATING}).
	 * 
	 * <p>Calls to this method should never modify the handler.</p>
	 * @return the maximum amount of coating
	 */
	public int getMaxCorrosion();

	/**
	 * Returns the current amount of coating points.
	 * 
	 * <p>Values returned should be in the range {@code 0} to {@linkplain #getMaxCoating()}, inclusive.</p>
	 * 
	 * <p>Calls to this method should never modify the handler.</p>
	 * @return the current amount of coating
	 */
	public int getCoating();
	
	/**
	 * Returns the current amount of corrosion points.
	 * 
	 * <p>Values returned should be in the range {@code 0} to {@linkplain #getMaxCorrosion()}, inclusive.</p>
	 * 
	 * <p>Calls to this method should never modify the handler.</p>
	 * @return the current amount of corrosion
	 */
	public int getCorrosion();

	/**
	 * Attempts to add the specified amount of coating points (ignoring corrosion), and returns the amount of coating points applied.
	 * 
	 * <p>Negative {@code amount}s should be ignored, and always return 0</p>
	 * 
	 * @param amount   The amount of coating points to add
	 * @param simulate If true, the operation is only simulated.
	 * @return How many coating points were added to this handler.
	 */
	public int addCoating(int amount, boolean simulate);

	/**
	 * Attempts to add the specified amount of corrosion points (ignoring coating), and returns the amount of corrosion points applied.
	 * 
	 * <p>Negative {@code amount}s should be ignored, and always return 0</p>
	 * 
	 * @param amount   The amount of corrosion points to add
	 * @param simulate If true, the operation is only simulated.
	 * @return How many corrosion points were added to this handler.
	 */
	public int addCorrosion(int amount, boolean simulate);

	/**
	 * Attempts to remove the specified amount of coating points (ignoring corrosion), and returns the amount of coating points removed.
	 * 
	 * <p>Negative {@code amount}s should be ignored, and always return 0</p>
	 * 
	 * @param amount   The amount of coating points to remove
	 * @param simulate If true, the operation is only simulated.
	 * @return How many coating points were removed from this handler.
	 */
	public int removeCoating(int amount, boolean simulate);

	/**
	 * Attempts to remove the specified amount of corrosion points (ignoring coating), and returns the amount of corrosion points removed.
	 * 
	 * <p>Negative {@code amount}s should be ignored, and always return 0</p>
	 * 
	 * @param amount   The amount of corrosion points to remove
	 * @param simulate If true, the operation is only simulated.
	 * @return How many corrosion points were removed from this handler.
	 */
	public int removeCorrosion(int amount, boolean simulate);
	
	/**
	 * Attempts to corrode this handler, and returns the amount of corrosion points applied.
	 * 
	 * <p>
	 *   The default implementation reduces coating to {@code 0} before increasing corrosion up to {@linkplain #getMaxCorrosion()}.
	 *   <br/>
	 *   For example: an item has 5 coating points (max 255) and 0 corrosion points (max 600);
	 *                the default {@code corrode(12, false)} would return 12, and the item would have 0 coating points and 7 corrosion points
	 * </p>
	 * 
	 * <p>Negative {@code amount}s should be ignored, and always return {@code 0}</p>
	 * 
	 * @param amount   The amount of points to corrode by. Negative values should be ignored.
	 * @param simulate If true, the corrosion is only simulated
	 * @return How many points were applied.
	 */
	public default int corrode(int amount, boolean simulate) {
		if(amount <= 0) {
			return 0;
		}
		
		// Remove coating if present
		int coatingRemoved;
		if(this.getCoating() > 0) {
			coatingRemoved = this.removeCoating(amount, simulate);
			// If all of the points went into removing coating
			if(coatingRemoved >= amount) {
				return amount;
			}
			amount -= coatingRemoved;
		} else {
			coatingRemoved = 0;
		}
		
		// Add to corrosion
		int corrosionAdded = this.addCorrosion(amount, simulate);
		
		return coatingRemoved + corrosionAdded;
	}

}
