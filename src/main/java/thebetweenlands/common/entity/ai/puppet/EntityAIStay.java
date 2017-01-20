package thebetweenlands.common.entity.ai.puppet;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.pathfinding.PathNavigate;

public class EntityAIStay extends EntityAIBase {
	protected final EntityLiving taskOwner;
	protected boolean stay;

	public EntityAIStay(EntityLiving taskOwner) {
		this.taskOwner = taskOwner;
		this.setMutexBits(1);
	}

	public void setStay(boolean stay) {
		this.stay = stay;
	}

	public boolean getStay() {
		return this.stay;
	}
	
	@Override
	public boolean shouldExecute() {
		return this.stay;
	}

	@Override
	public boolean continueExecuting() {
		return this.stay;
	}

	@Override
	public void updateTask() {
		PathNavigate navigator = this.taskOwner.getNavigator();
		if(navigator != null && !navigator.noPath()) {
			navigator.clearPathEntity();
		}
	}
}
