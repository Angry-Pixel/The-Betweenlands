package thebetweenlands.common.entity.ai.goals;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import thebetweenlands.common.entity.monster.Sludge;
import thebetweenlands.common.entity.movement.SludgeMoveControl;

import java.util.EnumSet;

//copy of Slime.SlimeAttackGoal, modified to use sludges and their move control
public class SludgeAttackGoal extends Goal {
	private final Sludge sludge;
	private int growTiredTimer;

	public SludgeAttackGoal(Sludge sludge) {
		this.sludge = sludge;
		this.setFlags(EnumSet.of(Goal.Flag.LOOK));
	}

	@Override
	public boolean canUse() {
		LivingEntity livingentity = this.sludge.getTarget();
		if (livingentity == null) {
			return false;
		} else {
			return this.sludge.canAttack(livingentity) && this.sludge.getMoveControl() instanceof SludgeMoveControl;
		}
	}

	@Override
	public void start() {
		this.growTiredTimer = reducedTickDelay(300);
		super.start();
	}

	@Override
	public boolean canContinueToUse() {
		LivingEntity livingentity = this.sludge.getTarget();
		if (livingentity == null) {
			return false;
		} else {
			return this.sludge.canAttack(livingentity) && --this.growTiredTimer > 0;
		}
	}

	@Override
	public boolean requiresUpdateEveryTick() {
		return true;
	}

	@Override
	public void tick() {
		LivingEntity livingentity = this.sludge.getTarget();
		if (livingentity != null) {
			this.sludge.lookAt(livingentity, 10.0F, 10.0F);
		}

		if (this.sludge.getMoveControl() instanceof SludgeMoveControl control) {
			control.setDirection(this.sludge.getYRot(), true);
		}
	}
}
