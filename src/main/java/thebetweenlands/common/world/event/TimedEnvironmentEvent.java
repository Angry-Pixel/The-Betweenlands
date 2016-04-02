package thebetweenlands.common.world.event;

import java.util.Random;

import net.minecraft.world.World;

public abstract class TimedEnvironmentEvent extends EnvironmentEvent {
	public TimedEnvironmentEvent(EnvironmentEventRegistry registry) {
		super(registry);
	}

	private int time = 0;
	private Random rnd = null;

	@Override
	public void update(World world) {
		if(world.isRemote || this.getRegistry().isDisabled()) return;
		this.rnd = world.rand;
		this.time--;
		if(this.time <= 0) {
			if(this.isActive()) {
				this.time = this.getOffTime(this.rnd);
			} else {
				this.time = this.getOnTime(this.rnd);
			}
			this.setActive(!this.isActive(), true);
		}
	}

	@Override
	public void setActive(boolean active, boolean markDirty) {
		super.setActive(active, false);
		if(this.rnd != null) {
			if(!this.isActive()) {
				this.time = this.getOffTime(this.rnd);
			} else {
				this.time = this.getOnTime(this.rnd);
			}
		}
		if(markDirty) this.markDirty();
	}
	
	@Override
	public void saveEventData() {
		this.getData().setInteger("time", this.time);
	}

	@Override
	public void loadEventData() {
		this.time = this.getData().getInteger("time");
	}

	@Override
	public void setDefaults() {
		this.time = this.getOffTime(new Random());
	}
	
	/**
	 * Returns how many ticks the event is not active.
	 * @param rnd
	 * @return
	 */
	public abstract int getOffTime(Random rnd);

	/**
	 * Returns how many ticks the event is active.
	 * @param rnd
	 * @return
	 */
	public abstract int getOnTime(Random rnd);
}
