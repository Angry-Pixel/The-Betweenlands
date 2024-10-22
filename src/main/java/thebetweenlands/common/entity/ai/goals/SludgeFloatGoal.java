package thebetweenlands.common.entity.ai.goals;

import net.minecraft.world.entity.ai.goal.Goal;
import thebetweenlands.common.entity.monster.Sludge;
import thebetweenlands.common.entity.movement.SludgeMoveControl;

import java.util.EnumSet;

//copy of Slime.SlimeFloatGoal, modified to use sludges and their move control
public class SludgeFloatGoal extends Goal {
	private final Sludge sludge;

	public SludgeFloatGoal(Sludge sludge) {
		this.sludge = sludge;
		this.setFlags(EnumSet.of(Goal.Flag.JUMP, Goal.Flag.MOVE));
		sludge.getNavigation().setCanFloat(true);
	}

	@Override
	public boolean canUse() {
		return (this.sludge.isInWater() || this.sludge.isInLava()) && this.sludge.getMoveControl() instanceof SludgeMoveControl;
	}

	@Override
	public boolean requiresUpdateEveryTick() {
		return true;
	}

	@Override
	public void tick() {
		if (this.sludge.getRandom().nextFloat() < 0.8F) {
			this.sludge.getJumpControl().jump();
			this.sludge.setDeltaMovement(this.sludge.getDeltaMovement().add(0.0D, 0.01D, 0.0D));
		}

		if (this.sludge.getMoveControl() instanceof SludgeMoveControl control) {
			control.setWantedMovement(1.2D);
		}
	}
}
