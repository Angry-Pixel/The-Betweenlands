package thebetweenlands.common.entity.ai.goals;

import javax.annotation.Nullable;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public abstract class EntityAIMoveToDirect<T extends Mob> extends Goal {
	protected final T entity;
	protected double speed;

	public EntityAIMoveToDirect(T entity, double speed) {
		this.entity = entity;
		this.speed = speed;
		this.setFlags(EnumSet.of(Flag.MOVE, Flag.TARGET));
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
		Vec3 target = this.getTarget();
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
	protected abstract Vec3 getTarget();
}
