package thebetweenlands.common.world.event;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import thebetweenlands.api.environment.EnvironmentEvent;

public abstract class BLEnvironmentEvent extends EnvironmentEvent {
	protected final BLEnvironmentEventRegistry blRegistry;

	protected int remoteResetTicks = -1;

	public BLEnvironmentEvent(BLEnvironmentEventRegistry registry) {
		super(registry);
		this.blRegistry = registry;
	}

	@Override
	public BLEnvironmentEventRegistry getRegistry() {
		return this.blRegistry;
	}

	@Override
	public void update(World world) {
		super.update(world);

		if(!world.isRemote && this.remoteResetTicks > 0) {
			this.remoteResetTicks--;
			if(this.remoteResetTicks == 0) {
				this.resetActiveState();
				this.remoteResetTicks = -1;
			}
		}
	}

	@Override
	public void saveEventData() {
		super.saveEventData();
		NBTTagCompound nbt = this.getData();
		nbt.setInteger("remoteResetTicks", this.remoteResetTicks);
	}

	@Override
	public void loadEventData() {
		super.loadEventData();
		NBTTagCompound nbt = this.getData();
		this.remoteResetTicks = nbt.getInteger("remoteResetTicks");
	}

	/**
	 * Sets the event state if the event is no longer of the specified state and the remote reset ticks have run out.
	 * So this will not change the state of an event if it changed its own state or was changed by command
	 * @param value The state the event should be set to
	 * @param remoteResetTicks Remote reset cooldown. For <= 0 No automatic reset
	 */
	public void setActiveRemotely(boolean value, int remoteResetTicks) {
		if(this.isActive() != value && this.remoteResetTicks <= 0) {
			this.setActive(value, true);
		}
		this.remoteResetTicks = remoteResetTicks;
	}
}
