package thebetweenlands.common.entity.ai;

import javax.annotation.Nullable;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.math.Vec3d;

public abstract class EntityAIMoveToDirect<T extends EntityLiving> extends EntityAIBase {
	protected final T entity;
	protected double speed;

	public EntityAIMoveToDirect(T entity, double speed) {
		this.entity = entity;
		this.speed = speed;
	}

	public void setSpeed(double speed) {
		this.speed = speed;
	}

	@Override
	public boolean shouldExecute() {
		return this.getTarget() != null;
	}

	@Override
	public boolean shouldContinueExecuting() {
		return this.getTarget() != null;
	}

	@Override
	public void updateTask() {
		Vec3d target = this.getTarget();
		if(target != null) {
			this.entity.getMoveHelper().setMoveTo(target.xCoord, target.yCoord, target.zCoord, this.speed);
		}
	}

	/**
	 * Returns the target. Returns null if there is no target
	 * @return
	 */
	@Nullable
	protected abstract Vec3d getTarget();
}
