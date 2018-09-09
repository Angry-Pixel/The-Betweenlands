package thebetweenlands.api.environment;

import javax.annotation.Nullable;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import thebetweenlands.api.network.IGenericDataManagerAccess;

public interface IEnvironmentEvent {
	/**
	 * Returns the world
	 * @return
	 */
	public World getWorld();

	/**
	 * Returns whether this event is currently active
	 * @return
	 */
	public boolean isActive();

	/**
	 * Returns whether the event affects the specified position.
	 * Can be used by events that should only affect certain locations, e.g.
	 * biomes, height etc.
	 * <br>
	 * Returns {@link #isActive()} by default
	 * @param x
	 * @param y
	 * @param z
	 * @return
	 */
	public boolean isActiveAt(double x, double y, double z);

	/**
	 * Resets the active state. Can be used by seasonal events to
	 * reset to the correct and expected state
	 */
	public void resetActiveState();

	/**
	 * Activates or deactivates the event.
	 * @param active Whether the event should be activated or deactivated
	 */
	public void setActive(boolean active);

	/**
	 * Sets the event data to be loaded
	 */
	public void setLoaded();

	/**
	 * Called every world tick.
	 * @param rnd
	 */
	public void update(World world);

	/**
	 * Returns the NBT data of this event.
	 * @return
	 */
	public NBTTagCompound getData();

	/**
	 * Saves the event data.
	 * @param compound
	 */
	public void writeToNBT(NBTTagCompound compound);

	/**
	 * Loads the event data.
	 * @param compound
	 */
	public void readFromNBT(NBTTagCompound compound);

	/**
	 * Sets the default values when the event is first loaded from the save file.
	 */
	public void setDefaults();

	/**
	 * Saves additional event data.
	 */
	public void saveEventData();

	/**
	 * Loads additional event data.
	 */
	public void loadEventData();

	/**
	 * Returns whether the data of this event has already been loaded.
	 * @return
	 */
	public boolean isLoaded();

	/**
	 * Returns the name of this event.
	 * @return
	 */
	public abstract ResourceLocation getEventName();

	/**
	 * Returns the localization name of this event.
	 * @return
	 */
	public String getLocalizationEventName();

	/**
	 * Returns the event registry of this event.
	 * @return
	 */
	public IEnvironmentEventRegistry getRegistry();
	
	/**
	 * Returns the data manager used to sync data
	 * @return
	 */
	@Nullable
	public IGenericDataManagerAccess getDataManager();
}
