package thebetweenlands.common.entity.ai.goals;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;
import thebetweenlands.common.entity.monster.Stalker;

import java.util.EnumSet;

public class StalkerDropAttackGoal extends Goal {
	private final Stalker entity;

	private float dropRadius = 5.0f;

	private int dropTimer = 0;

	private int waitingTimer = 20;

	private boolean attacked = false;

	public StalkerDropAttackGoal(Stalker entity) {
		this.entity = entity;
		this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
	}

	@Override
	public boolean canUse() {
		LivingEntity target = this.entity.getTarget();

		boolean inRange = false;

		if (target != null) {
			if (this.entity.hasLineOfSight(target)) {
				Vec3 up = new Vec3(0, 1, 0);
				Vec3 diff = target.position().subtract(this.entity.position());
				double diffY = up.dot(diff);

				if (diffY <= -3.0D && diff.subtract(up.scale(diffY)).length() <= this.dropRadius) {
					inRange = true;

					if (this.dropTimer++ >= 40) {
						return true;
					}
				}
			}
		}

		if (!inRange) {
			this.dropTimer = Math.max(0, this.dropTimer - 1);
		}

		return false;
	}

	@Override
	public void tick() {
		this.entity.getNavigation().stop();
		this.entity.getMoveControl().setWantedPosition(this.entity.getX(), this.entity.getY(), this.entity.getZ(), 1);
		this.entity.setDropping(true);
		this.entity.isStalking = false;

		LivingEntity target = this.entity.getTarget();
		if (target != null) {
			this.entity.getLookControl().setLookAt(target, 30, 30);

			if (this.waitingTimer > 30 && this.entity.onGround() && !this.attacked) {
				this.attacked = true;
				this.entity.useParalysisAttack(target);
			}
		}

		this.waitingTimer++;
	}

	@Override
	public boolean canContinueToUse() {
		return this.entity.getTarget() != null && this.waitingTimer < 80;
	}

	@Override
	public void stop() {
		this.dropTimer = 0;
		this.waitingTimer = 0;
		this.attacked = false;
	}
}
