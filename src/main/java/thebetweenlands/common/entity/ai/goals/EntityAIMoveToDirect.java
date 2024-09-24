package thebetweenlands.common.entity.ai.goals;

import javax.annotation.Nullable;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import org.joml.Vector3d;

public abstract class EntityAIMoveToDirect<T extends Mob> extends Goal {
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
	public boolean canUse() {
		return this.getTarget() != null;
	}

	@Override
	public boolean canContinueToUse() {
		return false;
	}

	@Override
	public void tick() {
		Vector3d target = this.getTarget();
		if (target != null) {
			this.entity.getMoveControl().setWantedPosition(target.x, target.y, target.z, this.speed);
		}
	}

	/**
	 * Returns the target. Returns null if there is no target
	 *
	 * @return
	 */
	@Nullable
	protected abstract Vector3d getTarget();
}
