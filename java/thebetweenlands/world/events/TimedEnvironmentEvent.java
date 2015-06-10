package thebetweenlands.world.events;

import java.util.Random;

public abstract class TimedEnvironmentEvent extends EnvironmentEvent {
	private int time = 0;

	@Override
	public void update(Random rnd) {
		this.time--;
		if(this.time <= 0) {
			if(this.isActive()) {
				this.time = this.getOffTime(rnd);
			} else {
				this.time = this.getOnTime(rnd);
			}
			this.setActive(!this.isActive());
			this.markDirty();
		}
	}

	@Override
	public void saveEventData() {
		this.getData().setInteger("time", this.time);
	}

	@Override
	public void loadEventData() {
		this.time = this.getData().getInteger("time");
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
