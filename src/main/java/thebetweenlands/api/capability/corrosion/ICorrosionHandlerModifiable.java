package thebetweenlands.api.capability.corrosion;

public interface ICorrosionHandlerModifiable extends ICorrosionHandler {

	/**
	 * Sets the coating of this handler.
	 * 
	 * @param coating the new amount of coating, in the range [{@code 0}, {@linkplain #getMaxCoating()}]
	 */
	public void setCoating(int coating);

	/**
	 * Sets the corrosion of this handler.
	 * 
	 * @param corrosion the new amount of corrosion, in the range [{@code 0}, {@linkplain #getMaxCorrosion()}]
	 */
	public void setCorrosion(int corrosion);
	
}
