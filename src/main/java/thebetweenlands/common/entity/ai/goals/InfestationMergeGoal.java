package thebetweenlands.common.entity.ai.goals;

import net.minecraft.world.entity.ai.goal.Goal;
import thebetweenlands.common.entity.monster.infestation.Infestation;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.List;

public class InfestationMergeGoal extends Goal {
	private final Infestation entity;
	private int delay;
	private final double speedTowardsTarget;

	@Nullable
	private Infestation leader;
	private int delayCounter;

	public InfestationMergeGoal(Infestation entity, int delay, double speed) {
		this.entity = entity;
		this.delay = delay;
		this.speedTowardsTarget = speed;
		this.setFlags(EnumSet.of(Flag.MOVE));
	}

	@Override
	public boolean canUse() {
		if (this.entity.getSwarmSize() < 0.9f && this.delay-- <= 0) {
			Infestation leader = this.findLeader();

			if (leader != null && leader != this.entity) {
				this.leader = leader;
				return true;
			}
		}
		return false;
	}

	@Nullable
	private Infestation findLeader() {
		List<Infestation> swarms = this.entity.level().getEntitiesOfClass(Infestation.class, this.entity.getBoundingBox().inflate(8));

		int minId = Integer.MAX_VALUE;
		Infestation leader = null;

		for (Infestation swarm : swarms) {
			if (swarm.getId() < minId && swarm.getSwarmSize() + this.entity.getSwarmSize() <= 1) {
				minId = swarm.getId();
				leader = swarm;
			}
		}

		return leader;
	}

	@Override
	public boolean canContinueToUse() {
		return this.leader != null && this.leader.isAlive() && this.leader.getSwarmSize() + this.entity.getSwarmSize() <= 1;
	}

	@Override
	public void stop() {
		this.leader = null;
		this.delayCounter = 0;
	}

	@Override
	public void tick() {
		if (this.leader != null) {
			if (this.leader.distanceTo(this.entity) < 1.25F) {
				this.entity.mergeInto(this.leader);
			} else if (--this.delayCounter <= 0) {
				this.delayCounter = 4 + this.entity.getRandom().nextInt(7);

				double dstSq = this.entity.distanceToSqr(this.leader.getX(), this.leader.getBoundingBox().minY, this.leader.getZ());

				if (dstSq > 1024.0D) {
					this.delayCounter += 10;
				} else if (dstSq > 256.0D) {
					this.delayCounter += 5;
				}

				if (!this.entity.getNavigation().moveTo(this.leader, this.speedTowardsTarget)) {
					this.delayCounter += 15;
				}
			}
		}
	}
}
