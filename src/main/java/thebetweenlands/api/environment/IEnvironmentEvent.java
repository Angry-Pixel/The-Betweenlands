package thebetweenlands.api.environment;

import javax.annotation.Nullable;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import thebetweenlands.api.network.IGenericDataAccessorAccess;

public interface IEnvironmentEvent {
	/**
	 * Returns the world
	 * @return
	 */
	Level getLevel();

	/**
	 * Returns whether this event is currently active
	 * @return
	 */
	boolean isActive();

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
	boolean isActiveAt(double x, double y, double z);

	/**
	 * Resets the active state. Can be used by seasonal events to
	 * reset to the correct and expected state
	 */
	void resetActiveState();

	/**
	 * Activates or deactivates the event.
	 * @param active Whether the event should be activated or deactivated
	 */
	void setActive(boolean active);

	/**
	 * Sets the event data to be loaded
	 */
	void setLoaded();

	/**
	 * Called every world tick.
	 * @param level
	 */
	void tick(Level level);

	/**
	 * Returns the NBT data of this event.
	 * @return
	 */
	CompoundTag getData();

	/**
	 * Saves the event data.
	 * @param compound
	 */
	void writeToNBT(CompoundTag compound);

	/**
	 * Loads the event data.
	 * @param compound
	 */
	void readFromNBT(CompoundTag compound);

	/**
	 * Sets the default values when the event is first loaded from the save file.
	 */
	void setDefaults();

	/**
	 * Saves additional event data.
	 */
	void saveEventData();

	/**
	 * Loads additional event data.
	 */
	void loadEventData();

	/**
	 * Returns whether the data of this event has already been loaded.
	 * @return
	 */
	boolean isLoaded();

	/**
	 * Returns the name of this event.
	 * @return
	 */
	ResourceLocation getEventName();

	/**
	 * Returns the localization name of this event.
	 * @return
	 */
	String getLocalizationEventName();

	/**
	 * Returns the event registry of this event.
	 * @return
	 */
	IEnvironmentEventRegistry getRegistry();

	/**
	 * Returns the data manager used to sync data
	 * @return
	 */
	@Nullable
	IGenericDataAccessorAccess getDataManager();
}
