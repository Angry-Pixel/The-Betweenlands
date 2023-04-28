package thebetweenlands.api.storage;

import java.util.Collection;

import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

public interface IChunkStorage extends ICapabilityProvider {
	/**
	 * Returns the world storage
	 * @return
	 */
	public IWorldStorage getWorldStorage();

	/**
	 * Returns the chunk of this chunk storage
	 * @return
	 */
	public Chunk getChunk();

	/**
	 * Called when the chunk storage is initialized before any data is loaded
	 */
	public void init();

	/**
	 * Called when the chunk storage is unloaded, after the data has been saved
	 */
	public void onUnload();

	/**
	 * Sets the default value if the chunk is new
	 */
	public void setDefaults();

	/**
	 * Reads the chunk storage data from NBT
	 * @param nbt NBT
	 * @param packet Whether the NBT is being read from a packet
	 */
	public void readFromNBT(NBTTagCompound nbt, boolean packet);
	
	/**
	 * Reads the local storage references from NBT
	 * @param nbt
	 * @return
	 */
	public NBTTagCompound readLocalStorageReferences(NBTTagCompound nbt);

	/**
	 * Writes the chunk storage data to NBT
	 * @param nbt NBT
	 * @param packet Whether the NBT is being written to a packet
	 * @return
	 */
	public NBTTagCompound writeToNBT(NBTTagCompound nbt, boolean packet);
	
	/**
	 * Writes the local storage references to NBT
	 * @param nbt
	 * @return
	 */
	public NBTTagCompound writeLocalStorageReferences(NBTTagCompound nbt);

	/**
	 * Adds a watcher
	 * @param player
	 * @return True if the player wasn't watching yet
	 */
	public boolean addWatcher(EntityPlayerMP player);

	/**
	 * Removes a watcher
	 * @param player
	 * @return True if the player was watching
	 */
	public boolean removeWatcher(EntityPlayerMP player);

	/**
	 * Returns an unmodifiable list of all current watching players
	 * @return
	 */
	public Collection<EntityPlayerMP> getWatchers();

	/**
	 * Marks the chunk storage and the chunk as dirty
	 */
	public void markDirty();

	/**
	 * Sets whether the chunk storage is dirty.
	 * If dirty is true the chunk is also marked dirty
	 * @param dirty
	 */
	public void setDirty(boolean dirty);

	/**
	 * Returns whether the chunk storage data is dirty
	 * @return
	 */
	public boolean isDirty();

	/**
	 * Returns the reference with the specified ID
	 * @param id
	 * @return
	 */
	@Nullable
	public LocalStorageReference getReference(StorageID id);

	/**
	 * Unlinks this chunk from the specified local storage
	 * @deprecated since API 1.15.0. Use {@link ILocalStorageHandler#unlinkChunk(ILocalStorage, IChunkStorage)} instead to support metadata.
	 * @param storage
	 * @return True if it was successfully unlinked
	 */
	@Deprecated
	public boolean unlinkLocalStorage(ILocalStorage storage);

	/**
	 * Links this chunk with the specified local storage
	 * @deprecated since API 1.15.0. Use {@link ILocalStorageHandler#linkChunk(ILocalStorage, IChunkStorage)} instead to support metadata.
	 * @param storage
	 * @return
	 */
	@Deprecated
	public boolean linkLocalStorage(ILocalStorage storage);

	/**
	 * Returns a list of all local storage references
	 * @return
	 */
	public Collection<LocalStorageReference> getLocalStorageReferences();
}
