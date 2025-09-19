package thebetweenlands.common.entity.ai.goals;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.phys.Vec3;
import thebetweenlands.common.entity.monster.chiromaw.ChiromawGreeblingRider;

import javax.annotation.Nullable;
import java.util.EnumSet;

public class ChiromawRiderMoveTowardsTargetGoal extends Goal {

	private final ChiromawGreeblingRider chiromawRider;
	@Nullable
	private LivingEntity target;
	private double movePosX;
	private double movePosY;
	private double movePosZ;
	private final double speed;
	private final float minTargetDistance;
	private final float maxTargetDistance;

	public ChiromawRiderMoveTowardsTargetGoal(ChiromawGreeblingRider chiromawRider, double speedIn, float targetMinDistance, float targetMaxDistance) {
		this.chiromawRider = chiromawRider;
		this.speed = speedIn;
		this.minTargetDistance = targetMinDistance;
		this.maxTargetDistance = targetMaxDistance;
		this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
	}

	@Override
	public boolean canUse() {
		this.target = this.chiromawRider.getTarget();

		if (this.target == null) {
			return false;
		} else if (this.chiromawRider.distanceToSqr(this.target) > (double) (this.maxTargetDistance * this.maxTargetDistance)) {
			return false;
		} else if (this.chiromawRider.distanceToSqr(this.target) > (double) (this.minTargetDistance * this.minTargetDistance) && this.chiromawRider.distanceToSqr(this.target) <= (double) (this.maxTargetDistance * this.maxTargetDistance)) {
			Vec3 vec3 = this.findNextPointTowards(8, 3, this.target.position());
			if (vec3 == null) {
				return false;
			} else {
				this.flyToLocation(vec3);
				return true;
			}
		} else if (this.chiromawRider.distanceToSqr(this.target) <= (double) (this.minTargetDistance * this.minTargetDistance)) {
			Vec3 vec3 = this.findNextPointAway(16, 2, this.target.position());
			if (vec3 == null) {
				return false;
			} else {
				this.flyToLocation(vec3);
				return true;
			}
		} else
			return false;
	}

	@Nullable
	private Vec3 findNextPointTowards(int xz, int y, Vec3 target) {
		Vec3 newTarget = DefaultRandomPos.getPosTowards(this.chiromawRider, xz, y, target, Mth.HALF_PI);
		if (newTarget == null) {
			return null;
		}
		if (target.y < this.chiromawRider.getY()) {
			newTarget = new Vec3(newTarget.x, this.chiromawRider.getY() - Math.abs(newTarget.y - this.chiromawRider.getY()), newTarget.z);
		} else {
			newTarget = new Vec3(newTarget.x, this.chiromawRider.getY() + Math.abs(newTarget.y - this.chiromawRider.getY()), newTarget.z);
		}
		return newTarget;
	}

	@Nullable
	private Vec3 findNextPointAway(int xz, int y, Vec3 target) {
		Vec3 newTarget = DefaultRandomPos.getPosAway(this.chiromawRider, xz, y, target);
		if (newTarget == null) {
			return null;
		}
		if (target.y > this.chiromawRider.getY()) {
			newTarget = new Vec3(newTarget.x, this.chiromawRider.getY() - Math.abs(newTarget.y - this.chiromawRider.getY()), newTarget.z);
		} else {
			newTarget = new Vec3(newTarget.x, this.chiromawRider.getY() + Math.abs(newTarget.y - this.chiromawRider.getY()), newTarget.z);
		}
		return newTarget;
	}

	public void flyToLocation(Vec3 vec3d) {
		this.movePosX = vec3d.x;
		this.movePosY = vec3d.y;
		this.movePosZ = vec3d.z;
	}

	@Override
	public boolean canContinueToUse() {
		return !this.chiromawRider.getNavigation().isDone() && this.target.isAlive() && this.target.distanceToSqr(this.chiromawRider) <= (double) (this.maxTargetDistance * this.maxTargetDistance);
	}

	@Override
	public void stop() {
		this.target = null;
	}

	@Override
	public void start() {
		this.chiromawRider.getNavigation().moveTo(this.movePosX, this.movePosY, this.movePosZ, this.speed);
	}
}
