package thebetweenlands.api.capability;

public interface IRotSmellCapability {
	/**
	 * Returns whether the entity smells bad
	 * @return
	 */
	public boolean isSmellingBad();

	/**
	 * Sets whether the entity smells bad
	 * @param isSmelly
	 */
	public void setIsSmellingBad(boolean isSmelly);
}
