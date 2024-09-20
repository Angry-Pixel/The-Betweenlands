package thebetweenlands.common.entity.fishing.anadia;

import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.RandomSwimmingGoal;

public class AnadiaWanderGoal extends RandomSwimmingGoal {

	public AnadiaWanderGoal(PathfinderMob mob, double speedModifier, int interval) {
		super(mob, speedModifier, interval);
	}

	@Override
	public boolean canUse() {
		return !this.mob.isVehicle() && super.canUse();
	}

	@Override
	public boolean canContinueToUse() {
		return !this.mob.getNavigation().isDone() && !this.mob.isVehicle();
	}
}
