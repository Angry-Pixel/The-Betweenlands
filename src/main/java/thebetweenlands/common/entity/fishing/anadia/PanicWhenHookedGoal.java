package thebetweenlands.common.entity.fishing.anadia;

import net.minecraft.world.entity.ai.goal.PanicGoal;

public class PanicWhenHookedGoal extends PanicGoal {

	private final Anadia anadia;

	public PanicWhenHookedGoal(Anadia anadia) {
		super(anadia, 2.0D);
		this.anadia = anadia;
	}

	@Override
	public boolean canUse() {
		return this.anadia.isVehicle() && this.anadia.getStaminaTicks() >= 1 && this.findRandomPosition();
	}

	@Override
	public boolean canContinueToUse() {
		return this.anadia.getNavigation().isDone() && this.anadia.isVehicle() && this.anadia.getStaminaTicks() >= 1;
	}
}
