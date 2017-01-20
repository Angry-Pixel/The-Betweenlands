package thebetweenlands.common.capability.summoning;

public interface ISummoningCapability {
	public void setActive(boolean active);
	
	public boolean isActive();
	
	public int getCooldownTicks();
	
	public void setCooldownTicks(int ticks);
	
	public int getActiveTicks();
	
	public void setActiveTicks(int ticks);
}
