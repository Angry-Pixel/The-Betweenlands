package thebetweenlands.common.entity.ai.goals;

import net.minecraft.world.entity.ai.goal.Goal;
import thebetweenlands.common.entity.monster.Sludge;
import thebetweenlands.common.entity.movement.SludgeMoveControl;

import java.util.EnumSet;

//copy of Slime.SlimeKeepOnJumpingGoal, modified to use sludges and their move control
public class SludgeJumpGoal extends Goal {
	private final Sludge sludge;

	public SludgeJumpGoal(Sludge sludge) {
		this.sludge = sludge;
		this.setFlags(EnumSet.of(Goal.Flag.JUMP, Goal.Flag.MOVE));
	}

	@Override
	public boolean canUse() {
		return !this.sludge.isPassenger();
	}

	@Override
	public void tick() {
		if (this.sludge.getMoveControl() instanceof SludgeMoveControl control) {
			control.setWantedMovement(1.0D);
		}
	}
}
