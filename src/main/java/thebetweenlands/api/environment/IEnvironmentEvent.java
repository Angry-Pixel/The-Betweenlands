package thebetweenlands.api.environment;

import javax.annotation.Nullable;

import net.minecraft.Util;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import thebetweenlands.api.BLRegistries;
import thebetweenlands.api.network.IGenericDataAccessorAccess;

public interface IEnvironmentEvent {

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
	void resetActiveState(Level level);

	/**
	 * Activates or deactivates the event.
	 * @param active Whether the event should be activated or deactivated
	 */
	void setActive(Level level, boolean active);

	/**
	 * Sets the event data to be loaded
	 */
	void setLoaded(Level level);

	/**
	 * Called every world tick.
	 * @param level
	 */
	void tick(Level level);

	/**
	 * Saves the event data.
	 * @param tag
	 */
	void writeToNBT(CompoundTag tag, HolderLookup.Provider registries);

	/**
	 * Loads the event data.
	 * @param tag
	 */
	void readFromNBT(CompoundTag tag, HolderLookup.Provider registries);

	/**
	 * Sets the default values when the event is first loaded from the save file.
	 */
	void setDefaults(Level level);

	/**
	 * Saves additional event data.
	 */
	void saveEventData(CompoundTag tag, HolderLookup.Provider registries);

	/**
	 * Loads additional event data.
	 */
	void loadEventData(CompoundTag tag, HolderLookup.Provider registries);

	/**
	 * Returns whether the data of this event has already been loaded.
	 * @return
	 */
	boolean isLoaded();

	default String getDescriptionId() {
		return Util.makeDescriptionId("environment_event", BLRegistries.ENVIRONMENT_EVENTS.getKey(this));
	}

	/**
	 * Returns the data manager used to sync data
	 * @return
	 */
	@Nullable
	IGenericDataAccessorAccess getDataManager();
}
