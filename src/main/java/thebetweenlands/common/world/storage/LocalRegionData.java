package thebetweenlands.common.world.storage;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import org.apache.commons.io.FileUtils;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.ChunkPos;
import net.minecraftforge.common.util.Constants;
import thebetweenlands.api.storage.IChunkStorage;
import thebetweenlands.api.storage.ILocalStorage;
import thebetweenlands.api.storage.LocalRegion;
import thebetweenlands.api.storage.LocalStorageMetadata;
import thebetweenlands.api.storage.LocalStorageReference;
import thebetweenlands.api.storage.StorageID;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.registries.StorageRegistry;

public class LocalRegionData {
	private static class CacheKey {
		private final StorageID storageId;

		@Nullable
		private final LocalRegion region;

		private final ResourceLocation registryId;

		public CacheKey(LocalStorageMetadata metadata) {
			this.storageId = metadata.getStorageId();
			this.region = metadata.getRegion();
			this.registryId = metadata.getRegistryId();
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((region == null) ? 0 : region.hashCode());
			result = prime * result + ((registryId == null) ? 0 : registryId.hashCode());
			result = prime * result + ((storageId == null) ? 0 : storageId.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if(this == obj)
				return true;
			if(obj == null)
				return false;
			if(getClass() != obj.getClass())
				return false;
			CacheKey other = (CacheKey) obj;
			if(region == null) {
				if(other.region != null)
					return false;
			} else if(!region.equals(other.region))
				return false;
			if(registryId == null) {
				if(other.registryId != null)
					return false;
			} else if(!registryId.equals(other.registryId))
				return false;
			if(storageId == null) {
				if(other.storageId != null)
					return false;
			} else if(!storageId.equals(other.storageId))
				return false;
			return true;
		}
	}

	private static class CacheEntry {
		public final CacheKey key;

		public final LocalStorageMetadata metadata;

		public int refCounter;

		public CacheEntry(LocalStorageMetadata metadata) {
			this.key = new CacheKey(metadata);
			this.metadata = metadata;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((key == null) ? 0 : key.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if(this == obj)
				return true;
			if(obj == null)
				return false;
			if(getClass() != obj.getClass())
				return false;
			CacheEntry other = (CacheEntry) obj;
			if(key == null) {
				if(other.key != null)
					return false;
			} else if(!key.equals(other.key))
				return false;
			return true;
		}
	}

	private final LocalRegion region;
	private final File dir;

	@Nullable
	private NBTTagCompound storageNbt;

	@Nullable
	private NBTTagCompound deferredOperationsNbt;

	private int refCounter;
	private boolean dirty;

	private boolean persistent;

	private boolean isDataFromDisk;

	private boolean dataLoaded;
	private boolean metadataLoaded;

	private Map<CacheKey, CacheEntry> valueCache = new HashMap<>();
	private Multimap<ChunkPos, CacheEntry> chunkCache = MultimapBuilder.hashKeys().hashSetValues().build();

	private final LocalRegionCache cache;

	private int minCacheTicks = 20;

	private LocalRegionData(LocalRegionCache cache, LocalRegion region, File dir, boolean persistent) {
		this.region = region;
		this.dir = dir;
		this.persistent = persistent;

		this.refCounter = 0;
		this.dirty = false;

		this.cache = cache;
	}

	public boolean isDataFromDisk() {
		return this.isDataFromDisk;
	}

	public int getMinCacheTicks() {
		return this.minCacheTicks;
	}

	public void setMinCacheTicks(int ticks) {
		this.minCacheTicks = ticks;
	}

	private void checkPersistent() {
		if(!this.persistent) {
			throw new IllegalStateException("Region is non-persistent");
		}
	}

	@Nullable
	private NBTTagCompound tryLoadFile(File file) {
		try {
			return this.cache.getLocalStorageHandler().getSaveHandler().loadFileNbt(file);
		} catch(Exception ex) {
			TheBetweenlands.logger.error(String.format("Failed loading local region cache %s", file.getAbsolutePath()), ex);
			File backup = new File(file.getAbsolutePath() + ".backup");
			try {
				FileUtils.copyFile(file, backup);
				TheBetweenlands.logger.info(String.format("Created a backup of local region cache at %s", backup.getAbsolutePath()));
			} catch (IOException e) {
				TheBetweenlands.logger.error(String.format("Failed creating backup of local region cache %s", file.getAbsolutePath()), e);
			}
			try {
				file.delete();
			} catch(Exception e) {}
		}
		return null;
	}

	private boolean tryLoadStorageData() {
		this.dataLoaded = true;

		if(this.storageNbt == null) {
			NBTTagCompound nbt = this.tryLoadFile(new File(this.dir, this.getID() + ".dat"));
			if(nbt != null) {
				this.storageNbt = nbt;
				return true;
			} else {
				this.storageNbt = new NBTTagCompound();
			}
		}

		return false;
	}

	private boolean tryLoadMetadata() {
		this.metadataLoaded = true;

		NBTTagCompound nbt = this.tryLoadFile(new File(this.dir, this.getID() + ".meta.dat"));
		if(nbt != null) {
			this.readMetadataFromNBT(nbt);
			return true;
		}

		return false;
	}

	/**
	 * Tries to read the region from a file and if it doesn't exist and {@code create} is true a new region is created.
	 * If {@code createNonPersistentIfEmpty} is true, then a new region is also created, but it is non-persistent and
	 * can thus only be read from and not be modified. If {@code metaOnly} is true, then only metadata is loaded and
	 * the rest of the data is lazily loaded.
	 * @param cache
	 * @param dir
	 * @param region
	 * @param create Whether to create a persistent region if none exists already.
	 * @param createNonPersistentIfEmpty Whether to create a non-persistent region if none exists already.
	 * @param metaOnly Whether only metadata should be loaded.
	 * @return
	 */
	@Nullable
	public static LocalRegionData getOrCreateRegion(LocalRegionCache cache, File dir, LocalRegion region, boolean create, boolean createNonPersistentIfEmpty, boolean metaOnly) {
		LocalRegionData data = new LocalRegionData(cache, region, dir, true);

		if(!metaOnly) {
			data.isDataFromDisk |= data.tryLoadStorageData();
		}

		data.isDataFromDisk |= data.tryLoadMetadata();

		if(data.isDataFromDisk || create) {
			return data;
		} else if(createNonPersistentIfEmpty) {
			data.persistent = false;
			return data;
		}

		return null;
	}

	/**
	 * Returns the region ID
	 * @return
	 */
	public String getID() {
		return this.region.getFileName();
	}

	/**
	 * Returns the region this data belongs to
	 * @return
	 */
	public LocalRegion getRegion() {
		return this.region;
	}

	/**
	 * Increases the reference counter
	 */
	public void incrRefCounter() {
		this.refCounter++;
	}

	/**
	 * Decreases the reference counter
	 */
	public void decrRefCounter() {
		this.refCounter--;
	}

	/**
	 * Returns whether there are any references left
	 * @return
	 */
	public boolean hasReferences() {
		return this.refCounter > 0;
	}

	/**
	 * Returns the NBT of a local storage in this region.
	 * @param id
	 * @return
	 */
	@Nullable
	public NBTTagCompound getLocalStorageNBT(StorageID id) {
		this.tryLoadStorageData();

		if(this.storageNbt.hasKey(id.getStringID(), Constants.NBT.TAG_COMPOUND)) {
			return this.storageNbt.getCompoundTag(id.getStringID());
		}

		return null;
	}

	/**
	 * Sets the NBT of a local storage in this region
	 * @param id
	 * @param nbt
	 */
	public void setLocalStorageNBT(StorageID id, NBTTagCompound nbt) {
		this.checkPersistent();

		this.tryLoadStorageData();

		this.storageNbt.setTag(id.getStringID(), nbt);
		this.dirty = true;
	}

	/**
	 * Removes a shared storage from this region
	 * @param id
	 * @return true if something was removed
	 */
	public boolean deleteLocalStorage(StorageID id) {
		this.checkPersistent();

		this.tryLoadStorageData();

		if(this.storageNbt.hasKey(id.getStringID(), Constants.NBT.TAG_COMPOUND)) {
			this.dirty = true;
			this.storageNbt.removeTag(id.getStringID());
			this.deleteFilesIfEmpty();
			return true;
		}

		return false;
	}

	/**
	 * Returns the legacy deferred operations (<= v3.9.6) NBT of the
	 * specified chunk.
	 * @param chunk
	 * @return
	 */
	@Nullable
	public NBTTagCompound getLegacyDeferredOperationsNBT(ChunkPos chunk) {
		this.tryLoadStorageData();

		if(this.storageNbt.hasKey("ChunkData." + chunk.x + "." + chunk.z, Constants.NBT.TAG_COMPOUND)) {
			return this.storageNbt.getCompoundTag("ChunkData." + chunk.x + "." + chunk.z);
		}

		return null;
	}

	/**
	 * Removes the legacy deferred operations (<= v3.9.6) NBT of the
	 * specified chunk.
	 * @param chunk
	 */
	public void removeLegacyDeferredOperations(ChunkPos chunk) {
		this.checkPersistent();

		this.tryLoadStorageData();

		if(this.storageNbt.hasKey("ChunkData." + chunk.x + "." + chunk.z, Constants.NBT.TAG_COMPOUND)) {
			this.storageNbt.removeTag("ChunkData." + chunk.x + "." + chunk.z);
			this.dirty = true;
		}
	}

	/**
	 * Sets the NBT of all deferred operations of the specified
	 * chunk.
	 * @param chunk
	 * @param nbt
	 */
	public void setDeferredOperationsNBT(ChunkPos chunk, @Nullable NBTTagList nbt) {
		this.checkPersistent();

		if(nbt != null) {
			if(this.deferredOperationsNbt == null) {
				this.deferredOperationsNbt = new NBTTagCompound();
			}

			this.deferredOperationsNbt.setTag("Chunk." + chunk.x + "." + chunk.z, nbt);
		} else if(this.deferredOperationsNbt != null) {
			this.deferredOperationsNbt.removeTag("Chunk." + chunk.x + "." + chunk.z);

			if(this.deferredOperationsNbt.getSize() == 0) {
				this.deferredOperationsNbt = null;
			}
		}

		this.dirty = true;
	}

	/**
	 * Returns the NBT of all deferred operations of the specified
	 * chunk.
	 * @param chunk
	 * @return
	 */
	@Nullable
	public NBTTagList getDeferredOperationsNBT(ChunkPos chunk) {
		if(this.deferredOperationsNbt != null && this.deferredOperationsNbt.hasKey("Chunk." + chunk.x + "." + chunk.z, Constants.NBT.TAG_LIST)) {
			return this.deferredOperationsNbt.getTagList("Chunk." + chunk.x + "." + chunk.z, Constants.NBT.TAG_COMPOUND);
		}
		return null;
	}

	/**
	 * Adds the given metadata to the specified chunk. If there is already
	 * metadata cached for the same entry then it is refreshed. Note that
	 * this will also change the metadata of all other chunks in this region
	 * that reference this piece of metadata (i.e. same region and storage id).
	 * @param chunk
	 * @param metadata
	 */
	public void addMetadata(ChunkPos chunk, LocalStorageMetadata metadata) {
		this.checkPersistent();

		this.addMetadata(chunk, new CacheEntry(metadata));

		this.dirty = true;
	}

	private boolean addMetadata(ChunkPos chunk, CacheEntry newEntry) {
		CacheEntry oldEntry = this.valueCache.get(newEntry.key);

		if(oldEntry != null) {
			newEntry.refCounter = oldEntry.refCounter + 1;
		} else {
			newEntry.refCounter = 1;
		}

		this.valueCache.put(newEntry.key, newEntry);
		if((oldEntry != null && this.chunkCache.remove(chunk, oldEntry)) | this.chunkCache.put(chunk, newEntry)) {
			return true;
		}

		return false;
	}

	/**
	 * Deletes the given metadata from the specified chunk
	 * @param chunk
	 * @param metadata
	 */
	public void deleteMetadata(ChunkPos chunk, LocalStorageMetadata metadata) {
		this.checkPersistent();

		if(this.deleteMetadata(chunk, new CacheEntry(metadata))) {
			this.dirty = true;
			this.deleteFilesIfEmpty();
		}
	}

	private boolean deleteMetadata(ChunkPos chunk, CacheEntry entry) {
		if(this.chunkCache.remove(chunk, entry)) {
			this.deleteMetadataValue(entry);
			return true;
		}
		return false;
	}

	private void deleteMetadataValue(CacheEntry entry) {
		CacheEntry cachedEntry = this.valueCache.get(entry.key);
		if(cachedEntry != null && --cachedEntry.refCounter <= 0) {
			this.valueCache.remove(cachedEntry.key);
		}
	}

	/**
	 * Syncs the cache with the current metadata of the specified chunk
	 * @param chunkStorage
	 */
	public void syncMetadata(IChunkStorage chunkStorage) {
		this.checkPersistent();

		ChunkPos pos = chunkStorage.getChunk().getPos();

		for(CacheEntry entry : this.chunkCache.removeAll(pos)) {
			this.deleteMetadataValue(entry);
		}

		for(LocalStorageReference ref : chunkStorage.getLocalStorageReferences()) {
			ILocalStorage storage = chunkStorage.getWorldStorage().getLocalStorageHandler().getLocalStorage(ref.getID());

			if(storage != null) {
				if(storage.shouldCacheMetadata()) {
					ResourceLocation registryId = StorageRegistry.getStorageId(storage.getClass());

					if(registryId != null) {
						NBTTagCompound data = storage.getCacheMetadata();

						// Using this overload to not set dirty flag
						this.addMetadata(pos, new CacheEntry(new LocalStorageMetadata(ref, registryId, data)));
					}
				}
			} else {
				TheBetweenlands.logger.warn("Local storage with id {} referenced by loaded chunk {} is not loaded", ref.getID().getStringID(), pos);
			}
		}

		// TODO Should only set to dirty if stuff actually changed
		this.dirty = true;

		this.deleteFilesIfEmpty();
	}

	/**
	 * Returns the metadata of the specified chunk
	 * @param chunk
	 * @return
	 */
	public Iterable<LocalStorageMetadata> getMetadata(ChunkPos chunk) {
		return () -> this.chunkCache.get(chunk).stream().map(value -> value.metadata).iterator();
	}

	private NBTTagCompound writeMetadataToNBT(NBTTagCompound nbt) {
		if(this.deferredOperationsNbt != null && this.deferredOperationsNbt.getSize() > 0) {
			nbt.setTag("DeferredOperations", this.deferredOperationsNbt);
		}

		if(!this.chunkCache.isEmpty()) {
			Map<CacheKey, Integer> idMap = new HashMap<>();

			NBTTagList metadataNbt = new NBTTagList();

			for(CacheEntry entry : this.valueCache.values()) {
				int id = idMap.size();
				idMap.put(entry.key, id);
				metadataNbt.appendTag(entry.metadata.writeToNBT(new NBTTagCompound()));
			}

			nbt.setTag("Metadata", metadataNbt);

			NBTTagList chunksNbt = new NBTTagList();

			for(ChunkPos chunk : this.chunkCache.keySet()) {
				NBTTagList indicesNbt = new NBTTagList();

				for(CacheEntry entry : this.chunkCache.get(chunk)) {
					Integer idx = idMap.get(entry.key);
					if(idx != null) {
						indicesNbt.appendTag(new NBTTagInt(idx));
					}
				}

				if(indicesNbt.tagCount() > 0) {
					NBTTagCompound chunkNbt = new NBTTagCompound();

					chunkNbt.setInteger("x", chunk.x);
					chunkNbt.setInteger("z", chunk.z);
					chunkNbt.setTag("i", indicesNbt);

					chunksNbt.appendTag(chunkNbt);
				}
			}

			nbt.setTag("Chunks", chunksNbt);
		}

		return nbt;
	}

	private void readMetadataFromNBT(NBTTagCompound nbt) {
		if(nbt.hasKey("DeferredOperations", Constants.NBT.TAG_COMPOUND)) {
			this.deferredOperationsNbt = nbt.getCompoundTag("DeferredOperations");
		}

		this.valueCache.clear();
		this.chunkCache.clear();

		Map<Integer, CacheEntry> idMap = new HashMap<>();

		NBTTagList metadataNbt = nbt.getTagList("Metadata", Constants.NBT.TAG_COMPOUND);

		for(int i = 0; i < metadataNbt.tagCount(); ++i) {
			LocalStorageMetadata metadata = LocalStorageMetadata.readFromNBT(metadataNbt.getCompoundTagAt(i));
			CacheEntry entry = new CacheEntry(metadata);
			this.valueCache.put(entry.key, entry);
			idMap.put(idMap.size(), entry);
		}

		NBTTagList chunksNbt = nbt.getTagList("Chunks", Constants.NBT.TAG_COMPOUND);

		for(int i = 0; i < chunksNbt.tagCount(); ++i) {
			NBTTagCompound chunkNbt = chunksNbt.getCompoundTagAt(i);

			ChunkPos chunk = new ChunkPos(chunkNbt.getInteger("x"), chunkNbt.getInteger("z"));

			NBTTagList indicesNbt = chunkNbt.getTagList("i", Constants.NBT.TAG_INT);
			for(int j = 0; j < indicesNbt.tagCount(); ++j) {
				CacheEntry entry = idMap.get(indicesNbt.getIntAt(j));
				if(entry != null) {
					this.addMetadata(chunk, entry);
				}
			}
		}
	}

	private boolean hasData() {
		return this.storageNbt != null && this.storageNbt.getSize() > 0;
	}

	private boolean hasMetadata() {
		return !this.chunkCache.isEmpty() || (this.deferredOperationsNbt != null && this.deferredOperationsNbt.getSize() > 0);
	}

	/**
	 * Returns whether the data is dirty
	 * @return
	 */
	public boolean isDirty() {
		return this.persistent && this.dirty;
	}

	/**
	 * Returns whether this region is persistent. Modifying a non-persistent
	 * region is not allowed and will throw an {@link IllegalStateException}.
	 * @return
	 */
	public boolean isPersistent() {
		return this.persistent;
	}

	/**
	 * Saves the region to disk
	 * @param dir
	 */
	public void saveRegion() {
		this.checkPersistent();

		if(this.dataLoaded) {
			if(this.hasData()) {
				this.cache.getLocalStorageHandler().getSaveHandler().queueRegion(new File(this.dir, this.getID() + ".dat"), this.storageNbt.copy());
			} else {
				this.deleteDataFile();
			}
		}

		if(this.metadataLoaded) {
			if(this.hasMetadata()) {
				this.cache.getLocalStorageHandler().getSaveHandler().queueRegion(new File(this.dir, this.getID() + ".meta.dat"), this.writeMetadataToNBT(new NBTTagCompound()));
			} else {
				this.deleteMetadataFile();
			}
		}

		this.dirty = false;
	}

	/**
	 * Deletes the region files
	 * @param dir
	 */
	public void deleteRegion() {
		this.checkPersistent();

		this.deleteDataFile();
		this.deleteMetadataFile();

		this.dirty = false;
	}

	private void deleteFilesIfEmpty() {
		if(this.persistent) {
			if(this.dataLoaded && !this.hasData()) {
				this.deleteDataFile();
			}

			if(this.metadataLoaded && !this.hasMetadata()) {
				this.deleteMetadataFile();
			}
		}
	}

	private void deleteDataFile() {
		this.cache.getLocalStorageHandler().getSaveHandler().queueRegion(new File(this.dir, this.getID() + ".dat"), null);
	}

	private void deleteMetadataFile() {
		this.cache.getLocalStorageHandler().getSaveHandler().queueRegion(new File(this.dir, this.getID() + ".meta.dat"), null);
	}
}
