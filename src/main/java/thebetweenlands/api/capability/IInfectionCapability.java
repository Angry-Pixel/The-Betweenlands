package thebetweenlands.api.capability;

import javax.annotation.Nullable;

import thebetweenlands.api.entity.IInfectionBehavior;

public interface IInfectionCapability {
	public boolean triggerInfectionBehavior(IInfectionBehavior behavior);
	
	public boolean stopInfectionBehavior();
	
	public void removeInfectionBehavior();
	
	@Nullable
	public IInfectionBehavior getCurrentInfectionBehavior();
	
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
