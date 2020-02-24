package thebetweenlands.common.world.event;

import java.util.Random;

import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.world.World;
import thebetweenlands.common.network.datamanager.GenericDataManager;
import thebetweenlands.common.registries.GameruleRegistry;

public abstract class TimedEnvironmentEvent extends BLEnvironmentEvent {
	public TimedEnvironmentEvent(BLEnvironmentEventRegistry registry) {
		super(registry);
	}

	protected static final DataParameter<Integer> TICKS = GenericDataManager.createKey(TimedEnvironmentEvent.class, DataSerializers.VARINT);
	protected static final DataParameter<Integer> START_TICKS = GenericDataManager.createKey(TimedEnvironmentEvent.class, DataSerializers.VARINT);

	@Override
	protected void initDataParameters() {
		super.initDataParameters();
		this.dataManager.register(TICKS, 20, 0);
		this.dataManager.register(START_TICKS, 0);
	}

	@Override
	public void update(World world) {
		super.update(world);

		if(!this.getRegistry().isDisabled() && !this.isCurrentStateFromRemote() && GameruleRegistry.getGameRuleBooleanValue(GameruleRegistry.BL_TIMED_EVENTS)) {
			if(this.isActive() || this.canActivate()) {
				this.dataManager.set(TICKS, this.getTicks() - 1);
			}

			if(!world.isRemote && this.getTicks() <= 0) {
				if(this.isActive()) {
					int offTime = this.getOffTime(world.rand);
					this.dataManager.set(TICKS, offTime).syncImmediately();
					this.dataManager.set(START_TICKS, offTime);
				} else {
					int onTime = this.getOnTime(world.rand);
					this.dataManager.set(TICKS, onTime).syncImmediately();
					this.dataManager.set(START_TICKS, onTime);
				}
				if(this.isActive() || this.canActivate()) {
					this.setActive(!this.isActive());
				}
			}
		}
	}

	/**
	 * Returns whether the event can activate right now
	 * @return
	 */
	protected boolean canActivate() {
		return true;
	}

	/**
	 * Sets how many ticks this event stays on/off
	 * @param ticks
	 * @return
	 */
	public void setTicks(int ticks) {
		int currTicks = this.getTicks();
		int diff = ticks - currTicks;
		this.dataManager.set(TICKS, currTicks + diff);
		this.dataManager.set(START_TICKS, Math.max(this.getStartTicks() + diff, 0));
	}

	/**
	 * Returns the time in ticks this event stays on/off
	 * @return
	 */
	public int getTicks() {
		return this.dataManager.get(TICKS);
	}

	/**
	 * Returns the maximum ticks
	 * @return
	 */
	public int getStartTicks() {
		return this.dataManager.get(START_TICKS);
	}

	/**
	 * Returns how many ticks have elapsed since changing state
	 * @return
	 */
	public int getTicksElapsed() {
		return this.getStartTicks() - this.getTicks();
	}

	@Override
	public void setActive(boolean active) {
		if(!active || this.canActivate()) {
			super.setActive(active);
			if(!this.getWorld().isRemote) {
				if(!this.isActive()) {
					int offTime = this.getOffTime(this.getWorld().rand);
					this.dataManager.set(TICKS, offTime).syncImmediately();
					this.dataManager.set(START_TICKS, offTime);
				} else {
					int onTime = this.getOnTime(this.getWorld().rand);
					this.dataManager.set(TICKS, onTime).syncImmediately();
					this.dataManager.set(START_TICKS, onTime);
				}
			}
		}
	}

	@Override
	public void saveEventData() {
		super.saveEventData();
		this.getData().setInteger("ticks", this.getTicks());
		this.getData().setInteger("startTicks", this.getStartTicks());
	}

	@Override
	public void loadEventData() {
		super.loadEventData();
		this.dataManager.set(TICKS, this.getData().getInteger("ticks")).syncImmediately();
		this.dataManager.set(START_TICKS, this.getData().getInteger("startTicks"));
	}

	@Override
	public void setDefaults() {
		this.dataManager.set(TICKS, this.getOffTime(new Random()));
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
