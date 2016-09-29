package thebetweenlands.common.capability.corrosion;

public interface ICorrosionCapability {
	/**
	 * Returns the item corrosion (0 - 255)
	 * @return
	 */
	public int getCorrosion();

	/**
	 * Sets the item corrosion (0 - 255)
	 * @param corrosion
	 */
	public void setCorrosion(int corrosion);

	/**
	 * Returns the anti-corrosion coating on this item
	 * @return
	 */
	public int getCoating();

	/**
	 * Sets the anti-corrosion coating of this item
	 * @param coating
	 */
	public void setCoating(int coating);
}
