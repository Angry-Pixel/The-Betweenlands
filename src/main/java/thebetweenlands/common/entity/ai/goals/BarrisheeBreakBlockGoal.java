package thebetweenlands.common.entity.ai.goals;

import net.minecraft.world.entity.ai.goal.Goal;
import thebetweenlands.common.entity.boss.Barrishee;

import java.util.EnumSet;

public class BarrisheeBreakBlockGoal extends Goal {

	private final Barrishee barrishee;
	private int cooldown;
	private final int minCooldown;
	private final int maxCooldown;

	public BarrisheeBreakBlockGoal(Barrishee barrishee, int minCooldown, int maxCooldown) {
		this.barrishee = barrishee;
		this.minCooldown = minCooldown;
		this.maxCooldown = maxCooldown;
		this.cooldown = minCooldown + barrishee.getRandom().nextInt(maxCooldown - minCooldown);
		this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
	}

	@Override
	public boolean canUse() {
		if(this.barrishee.getTarget() != null && this.barrishee.isReadyForSpecialAttack()) {
			return this.cooldown-- < 0;
		}

		return false;
	}

	@Override
	public boolean canContinueToUse() {
		return false;
	}

	@Override
	public void start() {
		this.barrishee.setScreamTimer(0);
	}

	@Override
	public void stop() {
		this.cooldown = this.minCooldown + this.barrishee.getRandom().nextInt(this.maxCooldown - this.minCooldown);
	}
}
