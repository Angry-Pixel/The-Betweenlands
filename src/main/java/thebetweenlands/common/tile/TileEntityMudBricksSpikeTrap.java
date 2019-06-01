package thebetweenlands.common.tile;

public class TileEntityMudBricksSpikeTrap extends TileEntitySpikeTrap {
	public int prevSpoopAnimationTicks;
	public int spoopAnimationTicks;
	public boolean activeSpoop;

	@Override
	public void update() {
		super.update();
		prevSpoopAnimationTicks = spoopAnimationTicks;
		if(!activeSpoop && getWorld().rand.nextInt(11) + getWorld().getTotalWorldTime()%10 == 0 && spoopAnimationTicks == 0)
			setActiveSpoop(true);
		if (activeSpoop) {
			if (spoopAnimationTicks <= 20)
				spoopAnimationTicks += 1;
			if (spoopAnimationTicks == 20)
				setActiveSpoop(false);
		}
		if (!activeSpoop)
			if (spoopAnimationTicks >= 1)
				spoopAnimationTicks--;
	}

	public void setActiveSpoop(boolean isActive) {
		activeSpoop = isActive;
		getWorld().notifyBlockUpdate(getPos(), getWorld().getBlockState(getPos()), getWorld().getBlockState(getPos()), 2);
	}
}