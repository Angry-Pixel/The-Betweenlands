package thebetweenlands.api.storage;

import java.util.Collection;

import javax.annotation.Nullable;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.chunk.ChunkAccess;

public interface IChunkStorage {
	/**
	 * Returns the world storage
	 * @return
	 */
	public IWorldStorage getWorldStorage();

	/**
	 * Returns the chunk of this chunk storage
	 * @return
	 */
	ChunkAccess getChunk();

	/**
	 * Called when the chunk storage is initialized before any data is loaded
	 */
	void init();

	/**
	 * Called when the chunk storage is unloaded, after the data has been saved
	 */
	void onUnload();

	/**
	 * Sets the default value if the chunk is new
	 */
	void setDefaults();

	/**
	 * Reads the chunk storage data from NBT
	 * @param tag NBT
	 * @param packet Whether the NBT is being read from a packet
	 */
	void readFromNBT(CompoundTag tag, boolean packet);

	/**
	 * Reads the local storage references from NBT
	 * @param tag
	 * @return
	 */
	CompoundTag readLocalStorageReferences(CompoundTag tag);

	/**
	 * Writes the chunk storage data to NBT
	 * @param tag NBT
	 * @param packet Whether the NBT is being written to a packet
	 * @return
	 */
	CompoundTag writeToNBT(CompoundTag tag, boolean packet);

	/**
	 * Writes the local storage references to NBT
	 * @param tag
	 * @return
	 */
	CompoundTag writeLocalStorageReferences(CompoundTag tag);

	/**
	 * Adds a watcher
	 * @param player
	 * @return True if the player wasn't watching yet
	 */
	boolean addWatcher(ServerPlayer player);

	/**
	 * Removes a watcher
	 * @param player
	 * @return True if the player was watching
	 */
	boolean removeWatcher(ServerPlayer player);

	/**
	 * Returns an unmodifiable list of all current watching players
	 * @return
	 */
	Collection<ServerPlayer> getWatchers();

	/**
	 * Marks the chunk storage and the chunk as dirty
	 */
	void markDirty();

	/**
	 * Sets whether the chunk storage is dirty.
	 * If dirty is true the chunk is also marked dirty
	 * @param dirty
	 */
	void setDirty(boolean dirty);

	/**
	 * Returns whether the chunk storage data is dirty
	 * @return
	 */
	boolean isDirty();

	/**
	 * Returns the reference with the specified ID
	 * @param id
	 * @return
	 */
	@Nullable
	LocalStorageReference getReference(StorageID id);

	/**
	 * Unlinks this chunk from the specified local storage
	 * @param storage
	 * @return True if it was successfully unlinked
	 */
	boolean unlinkLocalStorage(ILocalStorage storage);

	/**
	 * Links this chunk with the specified local storage
	 * @param storage
	 * @return
	 */
	boolean linkLocalStorage(ILocalStorage storage);

	/**
	 * Returns a list of all local storage references
	 * @return
	 */
	Collection<LocalStorageReference> getLocalStorageReferences();
}
