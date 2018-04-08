package thebetweenlands.common.entity.ai.puppet;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.math.Vec3d;

public class EntityAIStay extends EntityAIBase {
	protected final EntityLiving taskOwner;
	protected boolean stay;
	protected Vec3d pos;
	protected int delayCounter = 0;
	protected int failedPathFindingPenalty = 0;

	public EntityAIStay(EntityLiving taskOwner) {
		this.taskOwner = taskOwner;
		this.setMutexBits(1);
	}

	public void setStay(boolean stay) {
		this.stay = stay;
		this.pos = this.taskOwner.getPositionVector();
	}

	public boolean getStay() {
		return this.stay;
	}
	
	@Override
	public boolean shouldExecute() {
		return this.stay;
	}

	@Override
	public boolean shouldContinueExecuting() {
		return this.stay;
	}

	@Override
	public void updateTask() {
		PathNavigate navigator = this.taskOwner.getNavigator();
		if(navigator != null && this.pos != null) {
			double dist = this.taskOwner.getDistanceSq(this.pos.x, this.pos.y, this.pos.z);
			
			if(dist > 1.0D) {
				--this.delayCounter;
	
				if (this.delayCounter <= 0) {
					this.delayCounter = 2 + this.taskOwner.getRNG().nextInt(5);
	
					this.delayCounter += this.failedPathFindingPenalty;
	
					if (this.taskOwner.getNavigator().getPath() != null) {
						PathPoint finalPathPoint = this.taskOwner.getNavigator().getPath().getFinalPathPoint();
						if (finalPathPoint != null && this.pos.squareDistanceTo(finalPathPoint.x, finalPathPoint.y, finalPathPoint.z) < 2) {
							this.failedPathFindingPenalty = 0;
						} else {
							this.failedPathFindingPenalty += 6;
						}
					} else {
						this.failedPathFindingPenalty += 6;
					}
	
					if (dist > 1024.0D) {
						this.delayCounter += 6;
					} else if (dist > 256.0D) {
						this.delayCounter += 3;
					}
	
					if (!this.taskOwner.getNavigator().tryMoveToXYZ(this.pos.x, this.pos.y, this.pos.z, 0.75D)) {
						this.delayCounter += 15;
					}
				}
			} else {
				navigator.clearPath();
			}
		}
	}
}
