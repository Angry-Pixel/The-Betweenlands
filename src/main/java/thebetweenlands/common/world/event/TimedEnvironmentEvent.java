package thebetweenlands.common.world.event;

import java.util.Random;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import thebetweenlands.api.environment.IEnvironmentEvent;

public abstract class TimedEnvironmentEvent extends BLEnvironmentEvent {
	public TimedEnvironmentEvent(BLEnvironmentEventRegistry registry) {
		super(registry);
	}

	protected int ticks = 0;
	protected int startTicks = 0;

	@Override
	public void update(World world) {
		super.update(world);
		
		if(!this.getRegistry().isDisabled() && !this.isCurrentStateFromRemote()) {
			if(this.isActive() || this.canActivate()) {
				this.ticks--;
			}

			if(!world.isRemote && this.ticks % 20 == 0) {
				this.setDirty(true);
			}

			if(!world.isRemote && this.ticks <= 0) {
				if(this.isActive()) {
					this.startTicks = this.ticks = this.getOffTime(world.rand);
				} else {
					this.startTicks = this.ticks = this.getOnTime(world.rand);
				}
				if(this.isActive() || this.canActivate()) {
					this.setActive(!this.isActive(), true);
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
	 * Returns the time in ticks this event stays on/off
	 * @return
	 */
	public int getTicks() {
		return this.ticks;
	}

	/**
	 * Returns the maximum ticks
	 * @return
	 */
	public int getStartTicks() {
		return this.startTicks;
	}

	/**
	 * Returns how many ticks have elapsed since changing state
	 * @return
	 */
	public int getTicksElapsed() {
		return this.startTicks - this.ticks;
	}

	@Override
	public void setActive(boolean active, boolean markDirty) {
		if(!active || this.canActivate()) {
			super.setActive(active, false);
			if(!this.getWorld().isRemote) {
				if(!this.isActive()) {
					this.startTicks = this.ticks = this.getOffTime(this.getWorld().rand);
				} else {
					this.startTicks = this.ticks = this.getOnTime(this.getWorld().rand);
				}
				if(markDirty) {
					this.markDirty();
				}
			}
		}
	}

	@Override
	public void saveEventData() {
		super.saveEventData();
		this.getData().setInteger("ticks", this.ticks);
		this.getData().setInteger("startTicks", this.startTicks);
	}

	@Override
	public void loadEventData() {
		super.loadEventData();
		this.ticks = this.getData().getInteger("ticks");
		this.startTicks = this.getData().getInteger("startTicks");
	}

	@Override
	public void loadEventPacket(NBTTagCompound nbt) {
		super.loadEventPacket(nbt);
		this.ticks = nbt.getInteger("ticks");
		this.startTicks = nbt.getInteger("startTicks");
	}

	@Override
	public void sendEventPacket(NBTTagCompound nbt) {
		super.sendEventPacket(nbt);
		nbt.setInteger("ticks", this.ticks);
		nbt.setInteger("startTicks", this.startTicks);
	}

	@Override
	public void setDefaults() {
		this.ticks = this.getOffTime(new Random());
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
