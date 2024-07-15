package thebetweenlands.api.storage;

import java.io.File;
import java.util.Collection;
import java.util.List;

import javax.annotation.Nullable;

import com.google.common.base.Predicate;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.phys.AABB;

public interface ILocalStorageHandler {
	/**
	 * Returns the world storage
	 * @return
	 */
	IWorldStorage getWorldStorage();

	/**
	 * Adds a local storage to the world.
	 * The storage may have no linked chunks when initially added, but if it is not linked
	 * until after local storages are ticked again it may be unloaded and discarded.
	 * @param storage
	 * @return
	 */
	boolean addLocalStorage(ILocalStorage storage);

	/**
	 * Removes a local storage from the world
	 * @param storage
	 * @return
	 */
	boolean removeLocalStorage(ILocalStorage storage);

	/**
	 * Returns the local storage with the specified ID, if currently loaded
	 * @param id
	 * @return
	 */
	@Nullable
	ILocalStorage getLocalStorage(StorageID id);

	/**
	 * Returns a list of all local storages of the specified type at the specified position
	 * @param type
	 * @param x
	 * @param z
	 * @return
	 */
	<T extends ILocalStorage> List<T> getLocalStorages(Class<T> type, double x, double z, @Nullable Predicate<T> filter);

	/**
	 * Returns a list of all local storages of the specified type that intersect with the specified AABB
	 * @param aabb
	 * @param type
	 * @return
	 */
	<T extends ILocalStorage> List<T> getLocalStorages(Class<T> type, AABB aabb, @Nullable Predicate<T> filter);

	/**
	 * Deletes the file (or entry if in a region) of
	 * the specified local storage
	 * @param storage
	 */
	void deleteLocalStorageFile(ILocalStorage storage);

	/**
	 * Saves the local storage to a file (or entry if in a region)
	 * @param storage
	 */
	void saveLocalStorageFile(ILocalStorage storage);

	/**
	 * Deprecated. Use {@link #getOrLoadLocalStorage(LocalStorageReference)}!
	 * Using this may cause changes made to the location to be lost.
	 * @param reference
	 * @return
	 */
	@Deprecated
	@Nullable
	ILocalStorage loadLocalStorage(LocalStorageReference reference);

	/**
	 * Returns a {@link ILocalStorageHandle} of the local storage of the specified reference. If the local storage is already loaded that
	 * instance will be returned, otherwise it will be loaded from a file or the region cache if the local storage
	 * uses a region.
	 * Handle must be closed when no longer needed.
	 * @param reference
	 * @return
	 */
	@Nullable
	ILocalStorageHandle getOrLoadLocalStorage(LocalStorageReference reference);

	/**
	 * Unloads a local storage and saves to a file if necessary
	 * @param storage
	 * @return True if the storage was successfully unloaded
	 */
	boolean unloadLocalStorage(ILocalStorage storage);

	/**
	 * Returns an unmodifiable list of all currently loaded local storages
	 * @return
	 */
	Collection<ILocalStorage> getLoadedStorages();

	/**
	 * Updates all local storages that implement {@link TickableStorage}
	 */
	void tick();

	/**
	 * Returns the directory where the data of all local storages are saved
	 * @return
	 */
	File getLocalStorageDirectory();

	/**
	 * Creates a local storage instance from the specified NBT, saved by {@link #saveLocalStorageToNBT(CompoundTag, ILocalStorage)}
	 * @param tag
	 * @param region
	 * @return
	 */
	ILocalStorage createLocalStorageFromNBT(CompoundTag tag, LocalRegion region);

	/**
	 * Creates a new local storage
	 * @param type
	 * @param id
	 * @param region
	 * @return
	 */
	ILocalStorage createLocalStorage(ResourceLocation type, StorageID id, @Nullable LocalRegion region);

	/**
	 * Queues the specified deferred storage operation to be run when the specified chunk is loaded. If the chunk is already loaded
	 * the operation is executed immediately.
	 * @param chunk
	 * @param operation
	 */
	void queueDeferredOperation(ChunkPos chunk, IDeferredStorageOperation operation);

	/**
	 * Loads and runs the deferred storage operations of the specified chunk.
	 * @param storage
	 */
	void loadDeferredOperations(IChunkStorage storage);

	/**
	 * Saves a local storage instance to NBT
	 * @param tag
	 * @param storage
	 * @return
	 */
	CompoundTag saveLocalStorageToNBT(CompoundTag tag, ILocalStorage storage);

	/**
	 * Saves all local storages and regions
	 */
	void saveAll();
}
