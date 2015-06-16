package thebetweenlands.world.events;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import thebetweenlands.lib.ModInfo;

public abstract class EnvironmentEvent {
	private final EnvironmentEventRegistry registry;
	private NBTTagCompound nbtt = new NBTTagCompound();
	private boolean active = false;
	private boolean dirty = false;
	private boolean loaded = false;

	public EnvironmentEvent(EnvironmentEventRegistry registry) {
		this.registry = registry;
	}
	
	/**
	 * Returns whether this event is currently active.
	 * @return
	 */
	public boolean isActive() {
		return this.active;
	}

	/**
	 * Marks this event as dirty, indicating that something has changed. Forces the server to send a packet to the client
	 * and save the data when the world is saved.
	 */
	public void markDirty() {
		this.dirty = true;
	}

	/**
	 * Marks the event as dirty or clean.
	 * @param dirty
	 */
	public void setDirty(boolean dirty) {
		this.dirty = dirty;
	}
	
	/**
	 * Returns whether this event is marked as dirty.
	 * @return
	 */
	public boolean isDirty() {
		return this.dirty;
	}

	/**
	 * Activates or deactivates the event. Marks the event as dirty.
	 * @param active
	 */
	public void setActive(boolean active, boolean markDirty) {
		this.active = active;
		if(markDirty) this.markDirty();
	}

	/**
	 * Called every world tick.
	 * @param rnd
	 */
	public void update(World world) { }

	/**
	 * Returns the NBT data of this event.
	 * @return
	 */
	public NBTTagCompound getData() {
		return this.nbtt;
	}

	/**
	 * Saves the event data.
	 * @param compound
	 */
	public final void writeToNBT(NBTTagCompound compound) {
		this.nbtt.setBoolean("active", this.active);
		this.saveEventData();
		compound.setTag(ModInfo.ID + ":environmentEvent:" + this.getEventName(), this.nbtt);
	}

	/**
	 * Loads the event data.
	 * @param compound
	 */
	public final void readFromNBT(NBTTagCompound compound) {
		this.nbtt = compound.getCompoundTag(ModInfo.ID + ":environmentEvent:" + this.getEventName());
		this.active = this.nbtt.getBoolean("active");
		this.loadEventData();
		this.loaded = true;
	}

	/**
	 * Saves additional event data.
	 */
	public void saveEventData() { }

	/**
	 * Loads additional event data.
	 */
	public void loadEventData() { }

	/**
	 * Loads event data from the sync packet.
	 * @param buffer
	 */
	public void loadEventPacket(ByteBuf buffer) { }
	
	/**
	 * Saves event data to the sync packet.
	 * @param buffer
	 */
	public void sendEventPacket(ByteBuf buffer) { }
	
	/**
	 * Returns whether the data of this event has already been loaded.
	 * @return
	 */
	public boolean isLoaded() {
		return this.loaded;
	}

	/**
	 * Returns the name of this event.
	 * @return
	 */
	public abstract String getEventName();
	
	/**
	 * Returns the event registry of this event.
	 * @return
	 */
	public EnvironmentEventRegistry getRegistry() {
		return this.registry;
	}
}
