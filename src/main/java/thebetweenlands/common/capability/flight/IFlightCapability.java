package thebetweenlands.common.capability.flight;

public interface IFlightCapability {
	public boolean isFlying();
	
	public void setFlying(boolean flying);
	
	public int getFlightTime();
	
	public void setFlightTime(int ticks);
	
	public void setFlightRing(boolean ring);
	
	public boolean getFlightRing();
}
