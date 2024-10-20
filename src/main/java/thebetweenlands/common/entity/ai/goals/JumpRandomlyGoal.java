package thebetweenlands.common.entity.ai.goals;

import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;
import java.util.function.Supplier;

public class JumpRandomlyGoal extends Goal {

	private final PathfinderMob mob;
	private final int chance;
	private final Supplier<Boolean> condition;

	public JumpRandomlyGoal(PathfinderMob mob, int chance, Supplier<Boolean> condition) {
		this.mob = mob;
		this.chance = chance;
		this.condition = condition;
		this.setFlags(EnumSet.of(Flag.JUMP));
	}

	@Override
	public boolean canUse() {
		return this.mob.isAlive() && this.mob.getRandom().nextInt(this.chance) == 0 && this.condition.get();
	}

	@Override
	public void start() {
		this.mob.getJumpControl().jump();
	}

	@Override
	public boolean canContinueToUse() {
		return false;
	}
}
