package thebetweenlands.api.storage;

import java.util.Collection;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.api.network.IGenericDataManagerAccess;

public interface ILocalStorage extends ICapabilityProvider {
	/**
	 * Returns the world storage
	 * @return
	 */
	public IWorldStorage getWorldStorage();

	/**
	 * Returns the bounds of the local storage. May be null
	 * @return
	 */
	@Nullable
	public AxisAlignedBB getBoundingBox();

	/**
	 * Returns whether the local storage is loaded
	 * @return
	 */
	public boolean isLoaded();

	/**
	 * Returns the storage ID
	 * @return
	 */
	public StorageID getID();

	/**
	 * Returns the storage region
	 * @return
	 */
	@Nullable
	public LocalRegion getRegion();

	/**
	 * Reads the local storage data from NBT.
	 * {@link #getID()} and {@link #getRegion()} are already read automatically
	 * @param nbt
	 */
	public void readFromNBT(NBTTagCompound nbt);

	/**
	 * Writes the local storage data to NBT.
	 * {@link #getID()} and {@link #getRegion()} are already written automatically
	 * @param nbt
	 * @return
	 */
	public NBTTagCompound writeToNBT(NBTTagCompound nbt);
	
	/**
	 * Reads the initial data that is sent the first time
	 * @param nbt
	 */
	public void readInitialPacket(NBTTagCompound nbt);
	
	/**
	 * Writes the initial data that is sent the first time
	 * @param nbt
	 * @return
	 */
	public NBTTagCompound writeInitialPacket(NBTTagCompound nbt);

	/**
	 * Marks the local storage as dirty
	 */
	public void markDirty();

	/**
	 * Sets whether the local storage is dirty
	 * @param dirty
	 */
	public void setDirty(boolean dirty);

	/**
	 * Returns whether the local storage data is dirty
	 * @return
	 */
	public boolean isDirty();

	/**
	 * Returns an unmodifiable list of all linked chunks
	 * @return
	 */
	public List<ChunkPos> getLinkedChunks();

	/**
	 * Sets the linked chunks. Only for use on client side for syncing
	 * @param linkedChunks New linked chunks
	 */
	@SideOnly(Side.CLIENT)
	public void setLinkedChunks(List<ChunkPos> linkedChunks);

	/**
	 * Called once when the local storage is initially added to the world
	 */
	public default void onAdded() {
		
	}
	
	/**
	 * Called when the local storage is loaded
	 */
	public void onLoaded();

	/**
	 * Called when the local storage is unloaded
	 */
	public void onUnloaded();

	/**
	 * Called when the local storage has been removed
	 */
	public void onRemoved();
	
	/**
	 * Called before the local storage is being removed
	 */
	public default void onRemoving() {
		
	}

	/**
	 * Returns a list of all currently loaded references
	 * @return
	 */
	public Collection<LocalStorageReference> getLoadedReferences();

	/**
	 * Loads a reference
	 * @param reference
	 * @return True if the reference wasn't loaded yet
	 */
	public boolean loadReference(LocalStorageReference reference);

	/**
	 * Unloads a reference
	 * @param reference
	 * @return True if the reference was loaded
	 */
	public boolean unloadReference(LocalStorageReference reference);

	/**
	 * Adds a watcher of the specified chunk storage.
	 * May be called multiple times with the same player but from
	 * a different chunk storage
	 * @param chunkStorage
	 * @param player
	 * @return True if the player wasn't watching yet
	 */
	public boolean addWatcher(IChunkStorage chunkStorage, EntityPlayerMP player);

	/**
	 * Removes a watcher of the specified chunk storage.
	 * May be called multiple times with the same player but from
	 * a different chunk storage
	 * @param chunkStorage
	 * @param player
	 * @return True if the player was watching
	 */
	public boolean removeWatcher(IChunkStorage chunkStorage, EntityPlayerMP player);

	/**
	 * Returns an unmodifiable list of all current watching players
	 * @return
	 */
	public Collection<EntityPlayerMP> getWatchers();

	/**
	 * Unlinks all chunks from this local storage.
	 * Do not use this to remove local storage since the
	 * file won't be deleted. To remove a local storage
	 * use {@link ILocalStorageHandler#removeLocalStorage(ILocalStorage)} instead
	 * @return True if all chunks were successfully unlinked
	 */
	public boolean unlinkAllChunks();

	/**
	 * Links the specified chunk to this local storage
	 * @param chunk
	 * @return True if the chunk was linked successfully
	 */
	public boolean linkChunk(Chunk chunk);
	
	/**
	 * Links the specified chunk to this local storage using a deferred
	 * storage operation. The link will be completed once the chunk is loaded
	 * @param chunk
	 */
	public default void linkChunkDeferred(ChunkPos chunk) {
		
	}
	
	/**
	 * Links the specified chunk to this local storage in a safe manner,
	 * i.e. calls {@link #linkChunk(Chunk)} if the chunk already exists and is loaded,
	 * and {@link #linkChunkDeferred(ChunkPos)} if the chunk does not yet exist
	 * or is not loaded.
	 * @param chunk
	 */
	public default void linkChunkSafely(ChunkPos chunk) {
		Chunk instance = this.getWorldStorage().getWorld().getChunkProvider().getLoadedChunk(chunk.x, chunk.z);
		if(instance != null) {
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
	 * @param chunk
	 * @return True if the chunk was unlinked successfully
	 */
	public boolean unlinkChunk(Chunk chunk);

	/**
	 * Returns the data manager used to sync data. <p><b>Only storages that implement {@link ITickable}
	 * will automatically update the data manager!</b>
	 * @return
	 */
	@Nullable
	public IGenericDataManagerAccess getDataManager();
}
