package thebetweenlands.api.entity;

import net.minecraft.util.ITickable;

public interface IInfectionBehavior extends ITickable {
	public boolean isDone();
	
	public void start();
	
	public boolean stop();
	
	public void onRemove();
}
