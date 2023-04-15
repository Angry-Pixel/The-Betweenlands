package thebetweenlands.api.capability;

public interface IInfectionCapability {
	public float getInfectionPercent();
	
	public void setInfectionPercent(float percent);
	
	public float getInfectionIncrease();
	
	public void resetInfectionIncrease();
	
	public int getInfectionIncreaseTimer();
	
	public void incrementInfectionIncreaseTimer();
	
	public void resetInfectionIncreaseTimer();
	
	public boolean isInfectable();
	
	public float getInfectionHealingRate();
	
	public int getInfectionRenderSeed();
}
