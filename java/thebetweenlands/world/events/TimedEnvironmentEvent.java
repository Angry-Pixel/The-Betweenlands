package thebetweenlands.world.events;

import java.util.Random;

import thebetweenlands.world.events.impl.EventHeavyRain;

import net.minecraft.world.World;

public abstract class TimedEnvironmentEvent extends EnvironmentEvent {
	public TimedEnvironmentEvent(EnvironmentEventRegistry registry) {
		super(registry);
	}

	private int time = 0;
	private Random rnd = null;

	@Override
	public void update(World world) {
		if(world.isRemote) return;
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
		this.markDirty();
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
