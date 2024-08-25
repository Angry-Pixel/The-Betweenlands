package thebetweenlands.common.entities.fishing.anadia;

import net.minecraft.world.entity.ai.goal.PanicGoal;

public class PanicWhenUnhookedGoal extends PanicGoal {

	private final Anadia anadia;
	private int timeSinceUnhook = 0;

	public PanicWhenUnhookedGoal(Anadia anadia) {
		super(anadia, 2.0D);
		this.anadia = anadia;
	}

	@Override
	public boolean canUse() {
		if(this.anadia.isVehicle() && this.anadia.getStaminaTicks() > 0) {
			this.timeSinceUnhook = 0;
		} else if(this.anadia.isVehicle() && this.anadia.getStaminaTicks() <= 0) {
			this.timeSinceUnhook = 1;
		} else if(this.timeSinceUnhook == 1 && this.anadia.getNettableTimer() <= 0) {
			this.timeSinceUnhook = 2;
		} else if(this.timeSinceUnhook >= 2) {
			this.timeSinceUnhook += 2;

			if(anadia.isVehicle() || this.timeSinceUnhook >= 60) {
				this.timeSinceUnhook = 0;
				return false;
			}

			return this.findRandomPosition();
		}

		return false;
	}

	@Override
	public boolean canContinueToUse() {
		this.timeSinceUnhook++;

		if(this.anadia.isVehicle() || this.timeSinceUnhook >= 60) {
			this.timeSinceUnhook = 0;
			return false;
		}

		return !this.anadia.getNavigation().isDone() && !this.anadia.isVehicle();
	}
}
