package thebetweenlands.common.entity.ai.goals;

import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.goal.Goal;
import thebetweenlands.common.entity.monster.Sludge;
import thebetweenlands.common.entity.movement.SludgeMoveControl;

import java.util.EnumSet;

//copy of Slime.SlimeRandomDirectionGoal, modified to use sludges and their move control
public class SludgeRandomDirectionGoal extends Goal {
	private final Sludge sludge;
	private float chosenDegrees;
	private int nextRandomizeTime;

	public SludgeRandomDirectionGoal(Sludge sludge) {
		this.sludge = sludge;
		this.setFlags(EnumSet.of(Goal.Flag.LOOK));
	}

	@Override
	public boolean canUse() {
		return this.sludge.getTarget() == null
			&& (this.sludge.onGround() || this.sludge.isInWater() || this.sludge.isInLava() || this.sludge.hasEffect(MobEffects.LEVITATION))
			&& this.sludge.getMoveControl() instanceof SludgeMoveControl;
	}

	@Override
	public void tick() {
		if (--this.nextRandomizeTime <= 0) {
			this.nextRandomizeTime = this.adjustedTickDelay(40 + this.sludge.getRandom().nextInt(60));
			this.chosenDegrees = (float)this.sludge.getRandom().nextInt(360);
		}

		if (this.sludge.getMoveControl() instanceof SludgeMoveControl control) {
			control.setDirection(this.chosenDegrees, false);
		}
	}
}
