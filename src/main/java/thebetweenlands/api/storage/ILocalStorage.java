package thebetweenlands.api.storage;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.phys.AABB;
import thebetweenlands.api.network.GenericDataAccessorAccess;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;

public interface ILocalStorage {
	/**
	 * Returns the world storage
	 *
	 * @return
	 */
	IWorldStorage getWorldStorage();

	/**
	 * Returns the bounds of the local storage. May be null
	 *
	 * @return
	 */
	@Nullable
	AABB getBoundingBox();

	/**
	 * Returns whether the local storage is loaded
	 *
	 * @return
	 */
	boolean isLoaded();

	/**
	 * Returns the storage ID
	 *
	 * @return
	 */
	StorageID getID();

	/**
	 * Returns the storage region
	 *
	 * @return
	 */
	@Nullable
	LocalRegion getRegion();

	/**
	 * Reads the local storage data from NBT.
	 * {@link #getID()} and {@link #getRegion()} are already read automatically
	 *
	 * @param tag
	 */
	void readFromNBT(CompoundTag tag);

	/**
	 * Writes the local storage data to NBT.
	 * {@link #getID()} and {@link #getRegion()} are already written automatically
	 *
	 * @param tag
	 * @return
	 */
	CompoundTag writeToNBT(CompoundTag tag);

	/**
	 * Reads the initial data that is sent the first time
	 *
	 * @param tag
	 */
	void readInitialPacket(CompoundTag tag);

	/**
	 * Writes the initial data that is sent the first time
	 *
	 * @param tag
	 * @return
	 */
	CompoundTag writeInitialPacket(CompoundTag tag);

	/**
	 * Marks the local storage as dirty
	 */
	void markDirty();

	/**
	 * Sets whether the local storage is dirty
	 *
	 * @param dirty
	 */
	void setDirty(boolean dirty);

	/**
	 * Returns whether the local storage data is dirty
	 *
	 * @return
	 */
	boolean isDirty();

	/**
	 * Returns an unmodifiable list of all linked chunks
	 *
	 * @return
	 */
	List<ChunkPos> getLinkedChunks();

	/**
	 * Sets the linked chunks. Only for use on client side for syncing
	 *
	 * @param linkedChunks New linked chunks
	 */
	void setLinkedChunks(List<ChunkPos> linkedChunks);

	/**
	 * Called once when the local storage is initially added to the world
	 */
	default void onAdded(Level level) {

	}

	/**
	 * Called when the local storage is loaded
	 */
	void onLoaded();

	/**
	 * Called when the local storage is unloaded
	 */
	void onUnloaded();

	/**
	 * Called when the local storage has been removed
	 */
	void onRemoved();

	/**
	 * Called before the local storage is being removed
	 */
	default void onRemoving(Level level) {

	}

	/**
	 * Returns a list of all currently loaded references
	 *
	 * @return
	 */
	Collection<LocalStorageReference> getLoadedReferences();

	/**
	 * Loads a reference
	 *
	 * @param reference
	 * @return True if the reference wasn't loaded yet
	 */
	boolean loadReference(LocalStorageReference reference);

	/**
	 * Unloads a reference
	 *
	 * @param reference
	 * @return True if the reference was loaded
	 */
	boolean unloadReference(LocalStorageReference reference);

	/**
	 * Adds a watcher of the specified chunk storage.
	 * May be called multiple times with the same player but from
	 * a different chunk storage
	 *
	 * @param chunkStorage
	 * @param player
	 * @return True if the player wasn't watching yet
	 */
	boolean addWatcher(IChunkStorage chunkStorage, ServerPlayer player);

	/**
	 * Removes a watcher of the specified chunk storage.
	 * May be called multiple times with the same player but from
	 * a different chunk storage
	 *
	 * @param chunkStorage
	 * @param player
	 * @return True if the player was watching
	 */
	boolean removeWatcher(IChunkStorage chunkStorage, ServerPlayer player);

	/**
	 * Returns an unmodifiable list of all current watching players
	 *
	 * @return
	 */
	Collection<ServerPlayer> getWatchers();

	/**
	 * Unlinks all chunks from this local storage.
	 * Do not use this to remove local storage since the
	 * file won't be deleted. To remove a local storage
	 * use {@link ILocalStorageHandler#removeLocalStorage(ILocalStorage)} instead
	 *
	 * @return True if all chunks were successfully unlinked
	 */
	boolean unlinkAllChunks(Level level);

	/**
	 * Links the specified chunk to this local storage
	 *
	 * @param chunk
	 * @return True if the chunk was linked successfully
	 */
	boolean linkChunk(ChunkAccess chunk);

	/**
	 * Links the specified chunk to this local storage using a deferred
	 * storage operation. The link will be completed once the chunk is loaded
	 *
	 * @param chunk
	 */
	default void linkChunkDeferred(ChunkPos chunk) {

	}

	/**
	 * Links the specified chunk to this local storage in a safe manner,
	 * i.e. calls {@link #linkChunk(ChunkAccess)} if the chunk already exists and is loaded,
	 * and {@link #linkChunkDeferred(ChunkPos)} if the chunk does not yet exist
	 * or is not loaded.
	 *
	 * @param chunk
	 */
	default void linkChunkSafely(Level level, ChunkPos chunk) {
		ChunkAccess instance = level.getChunkSource().getChunkNow(chunk.x, chunk.z);
		if (instance != null) {
			this.linkChunk(instance);
		} else {
			this.linkChunkDeferred(chunk);
		}
	}

	/**
	 * Unlinks the specified chunk from this local storage
	 * Do not use this to remove local storage since the
	 * file won't be deleted. To remove a local storage
	 * use {@link ILocalStorageHandler#removeLocalStorage(ILocalStorage)} instead
	 *
	 * @param chunk
	 * @return True if the chunk was unlinked successfully
	 */
	boolean unlinkChunk(ChunkAccess chunk);

	/**
	 * Returns the data manager used to sync data. <p><b>Only storages that implement {@link TickableStorage}
	 * will automatically update the data manager!</b>
	 *
	 * @return
	 */
	@Nullable
	GenericDataAccessorAccess getDataManager();
}
