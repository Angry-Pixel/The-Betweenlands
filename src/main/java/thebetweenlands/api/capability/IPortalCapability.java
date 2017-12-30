package thebetweenlands.api.capability;

public interface IPortalCapability {
	/**
	 * Returns whether the entity is in the BL portal
	 * @return
	 */
	public boolean isInPortal();

	/**
	 * Sets whether the entity is in the BL portal
	 * @param inPortal
	 */
	public void setInPortal(boolean inPortal);

	/**
	 * Returns how many ticks until the entity is teleported
	 * @return
	 */
	public int getTicksUntilTeleport();

	/**
	 * Sets how many ticks until the entity is teleported
	 * @param ticks
	 */
	public void setTicksUntilTeleport(int ticks);

	/**
	 * Returns whether the entity was teleported
	 * @return
	 */
	public boolean wasTeleported();

	/**
	 * Sets whether the entity was teleported
	 * @param wasTeleported
	 */
	public void setWasTeleported(boolean wasTeleported);
}
