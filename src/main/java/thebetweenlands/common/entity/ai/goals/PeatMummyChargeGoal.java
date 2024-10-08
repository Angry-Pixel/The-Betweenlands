package thebetweenlands.common.entity.ai.goals;

import net.minecraft.world.entity.ai.goal.Goal;
import thebetweenlands.common.entity.monster.PeatMummy;

public class PeatMummyChargeGoal extends Goal {
	private final PeatMummy mummy;

	private int chargingCooldown;
	private int chargingTime;

	public PeatMummyChargeGoal(PeatMummy mummy) {
		this.mummy = mummy;
		this.chargingCooldown = mummy.getMaxChargingCooldown();
	}

	@Override
	public boolean canUse() {
		return this.mummy.getTarget() != null && this.mummy.getSpawningProgress() == 1;
	}

	@Override
	public boolean canContinueToUse() {
		return this.mummy.getTarget() != null;
	}

	@Override
	public void tick() {
		if (!this.mummy.isCharging()) {
			if (this.chargingCooldown == 0) {
				//Peat mummy done charging
				this.stop();
			}

			if (this.chargingCooldown > 0 && this.mummy.getTarget() != null) {
				this.chargingCooldown--;
			}
			if (this.chargingCooldown <= 0) {
				this.mummy.startCharging();
			}
		} else if (!this.mummy.isPreparing()) {
			this.chargingTime++;
			if (this.chargingTime >= 320.0D) {
				this.mummy.stopCharging();
			}
		}
	}

	@Override
	public void stop() {
		this.chargingTime = 0;
		this.chargingCooldown = this.mummy.getMaxChargingCooldown() + this.mummy.level().getRandom().nextInt(this.mummy.getMaxChargingCooldown() / 2 + 1);
	}
}
