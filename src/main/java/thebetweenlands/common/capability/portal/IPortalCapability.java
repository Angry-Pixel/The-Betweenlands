package thebetweenlands.common.capability.portal;

public interface IPortalCapability {
	public boolean isInPortal();
	
	public void setInPortal(boolean inPortal);
	
	public int getTicksUntilTeleport();
	
	public void setTicksUntilTeleport(int ticks);
	
	public boolean wasTeleported();
	
	public void setWasTeleported(boolean wasTeleported);
}
