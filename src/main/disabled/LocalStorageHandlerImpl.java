package thebetweenlands.common.world.storage;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import com.google.common.base.Predicate;

import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.network.PacketDistributor;
import thebetweenlands.api.ITickable;
import thebetweenlands.api.network.IGenericDataAccessorAccess;
import thebetweenlands.api.storage.IChunkStorage;
import thebetweenlands.api.storage.IDeferredStorageOperation;
import thebetweenlands.api.storage.ILocalStorage;
import thebetweenlands.api.storage.ILocalStorageHandle;
import thebetweenlands.api.storage.ILocalStorageHandler;
import thebetweenlands.api.storage.IWorldStorage;
import thebetweenlands.api.storage.LocalRegion;
import thebetweenlands.api.storage.LocalStorageReference;
import thebetweenlands.api.storage.StorageID;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.network.SyncLocalStorageDataPacket;
import thebetweenlands.common.registries.StorageRegistry;

public class LocalStorageHandlerImpl implements ILocalStorageHandler {
	private final IWorldStorage worldStorage;
	private final Level level;
	private final File localStorageDir;

	private final Map<StorageID, ILocalStorage> localStorage = new HashMap<>();
	private final List<ILocalStorage> tickableLocalStorage = new ArrayList<>();
	private final List<ILocalStorage> pendingUnreferencedStorages = new ArrayList<>();

	private final Object2LongMap<LocalRegionData> pendingUnreferencedRegions = new Object2LongOpenHashMap<>();

	private final LocalRegionCache regionCache;

	private final LocalStorageSaveHandler saveHandler = new LocalStorageSaveHandler();

	public LocalStorageHandlerImpl(IWorldStorage worldStorage) {
		this.worldStorage = worldStorage;
		this.level = worldStorage.getLevel();
		Path dimFolder = DimensionType.getStorageFolder(this.level.dimension(), this.level.getServer().getWorldPath(LevelResource.ROOT));
		this.localStorageDir = new File(Minecraft.getInstance().getLevelSource().validateAndCreateAccess(this.level.noSave()), (!dimFolder.toString().isEmpty() ? dimFolder + File.separator : "") + "data" + File.separator + "local_storage" + File.separator);
		this.regionCache = new LocalRegionCache(this, new File(this.localStorageDir, "region"));
	}

	@Override
	public IWorldStorage getWorldStorage() {
		return this.worldStorage;
	}

	@Override
	public boolean addLocalStorage(ILocalStorage storage) {
		return this.addLocalStorageInternal(storage, true);
	}

	protected boolean addLocalStorageInternal(ILocalStorage storage, boolean isInitialAdd) {
		if (!this.localStorage.containsKey(storage.getID())) {
			this.localStorage.put(storage.getID(), storage);

			if (storage instanceof ITickable) {
				this.tickableLocalStorage.add(storage);
			}

			if (isInitialAdd) {
				storage.onAdded();
			}

			storage.onLoaded();

			boolean isReferenced = false;

			//Add already loaded references
			for (ChunkPos referenceChunk : storage.getLinkedChunks()) {
				ChunkAccess chunk = this.level.getChunkSource().getChunkNow(referenceChunk.x, referenceChunk.z);
				if (chunk != null) {
					IChunkStorage chunkStorage = this.worldStorage.getChunkStorage(chunk);
					if (chunkStorage != null) {
						LocalStorageReference reference = chunkStorage.getReference(storage.getID());
						if (reference != null) {
							isReferenced = true;

							if (!storage.getLoadedReferences().contains(reference)) {
								//Add reference
								storage.loadReference(reference);
							}
						}
					}
				}
			}

			if (!isReferenced && isInitialAdd && !this.level.isClientSide()) {
				//Queue storage to be checked in the next tick.
				//If it is still unreferenced it will be unloaded.
				this.pendingUnreferencedStorages.add(storage);
			}

			return true;
		}
		return false;
	}

	@Override
	public boolean removeLocalStorage(ILocalStorage storage) {
		if (this.localStorage.containsKey(storage.getID())) {
			storage.onRemoving();

			if (!this.level.isClientSide()) {
				storage.unlinkAllChunks();
			}

			this.localStorage.remove(storage.getID());

			Iterator<ILocalStorage> tickableIt = this.tickableLocalStorage.iterator();
			while (tickableIt.hasNext()) {
				ILocalStorage tickableStorage = tickableIt.next();
				if (storage.getID().equals(tickableStorage.getID())) {
					tickableIt.remove();
				}
			}

			Iterator<ILocalStorage> pendingIt = this.pendingUnreferencedStorages.iterator();
			while (pendingIt.hasNext()) {
				ILocalStorage pendingStorage = pendingIt.next();
				if (storage.getID().equals(pendingStorage.getID())) {
					pendingIt.remove();
				}
			}

			boolean wasSavedToRegion = false;

			if (!this.level.isClientSide()) {
				wasSavedToRegion = this.deleteLocalStorageFileInternal(storage);
			}

			storage.onUnloaded();

			storage.onRemoved();

			//A region only has a reference from a storage if that storage was new and saved to that
			//region or if it was loaded from that region. Only in those cases the region reference
			//counter needs to be decremented and the region unloaded if no longer referenced.
			if (wasSavedToRegion && !this.level.isClientSide()) {
				LocalRegion region = storage.getRegion();

				if (region != null) {
					this.decrRegionRef(this.regionCache.getCachedRegion(region), null, true);
				}
			}

			return true;
		}
		return false;
	}

	@Override
	public ILocalStorage getLocalStorage(StorageID id) {
		return this.localStorage.get(id);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends ILocalStorage> List<T> getLocalStorages(Class<T> type, double x, double z, @Nullable Predicate<T> filter) {
		List<T> storages = new ArrayList<>();
		int cx = Mth.floor(x) >> 4;
		int cz = Mth.floor(z) >> 4;
		ChunkAccess chunk = this.level.getChunk(cx, cz);
		IChunkStorage chunkStorage = this.getWorldStorage().getChunkStorage(chunk);
		if (chunkStorage != null) {
			for (LocalStorageReference ref : chunkStorage.getLocalStorageReferences()) {
				ILocalStorage localStorage = this.getLocalStorage(ref.getID());
				if (localStorage != null && localStorage.getBoundingBox() != null && type.isAssignableFrom(localStorage.getClass())
					&& (filter == null || filter.apply((T) localStorage))) {
					if (!storages.contains(localStorage)) {
						storages.add((T) localStorage);
					}
				}
			}
		}
		return storages;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends ILocalStorage> List<T> getLocalStorages(Class<T> type, AABB aabb, @Nullable Predicate<T> filter) {
		List<T> storages = new ArrayList<>();
		int sx = Mth.floor(aabb.minX) >> 4;
		int sz = Mth.floor(aabb.minZ) >> 4;
		int ex = Mth.floor(aabb.maxX) >> 4;
		int ez = Mth.floor(aabb.maxZ) >> 4;
		for (int cx = sx; cx <= ex; cx++) {
			for (int cz = sz; cz <= ez; cz++) {
				ChunkAccess chunk = this.level.getChunk(cx, cz);
				IChunkStorage chunkStorage = this.getWorldStorage().getChunkStorage(chunk);
				if (chunkStorage != null) {
					for (LocalStorageReference ref : chunkStorage.getLocalStorageReferences()) {
						ILocalStorage localStorage = this.getLocalStorage(ref.getID());
						if (localStorage != null && localStorage.getBoundingBox() != null && type.isAssignableFrom(localStorage.getClass()) && localStorage.getBoundingBox().intersects(aabb)
							&& (filter == null || filter.apply((T) localStorage))) {
							if (!storages.contains(localStorage)) {
								storages.add((T) localStorage);
							}
						}
					}
				}
			}
		}
		return storages;
	}

	@Override
	public void deleteLocalStorageFile(ILocalStorage storage) {
		this.deleteLocalStorageFileInternal(storage);
	}

	private boolean deleteLocalStorageFileInternal(ILocalStorage storage) {
		if (storage.getRegion() == null) {
			File file = new File(this.getLocalStorageDirectory(), storage.getID().getStringID() + ".dat");
			this.saveHandler.queueLocalStorage(file, null);
		} else {
			LocalRegionData data = this.regionCache.getOrCreateRegion(storage.getRegion(), false);

			if (data != null) {
				this.incrRegionRef(data);

				try {
					return data.deleteLocalStorage(this.regionCache.getDir(), storage.getID());
				} finally {
					this.decrRegionRef(data, null, true);
				}
			}
		}

		return false;
	}

	@Override
	public void saveLocalStorageFile(ILocalStorage storage) {
		this.saveLocalStorageFile(storage, false);
	}

	private void saveLocalStorageFile(ILocalStorage storage, boolean skipRegionUnloading) {
		CompoundTag nbt = this.saveLocalStorageToNBT(new CompoundTag(), storage);
		if (storage.getRegion() == null) {
			File file = new File(this.getLocalStorageDirectory(), storage.getID().getStringID() + ".dat");
			this.saveHandler.queueLocalStorage(file, nbt);
		} else {
			LocalRegion region = storage.getRegion();
			LocalRegionData data = this.regionCache.getOrCreateRegion(region);

			boolean isLoaded = !storage.getLoadedReferences().isEmpty() && this.getLocalStorage(storage.getID()) != null;
			boolean isFromRegion = data.getLocalStorageNBT(storage.getID()) != null;
			boolean isNewStorage = isLoaded && !isFromRegion;

			this.incrRegionRef(data);

			try {
				data.setLocalStorageNBT(storage.getID(), nbt);
			} finally {
				if (skipRegionUnloading) {
					this.decrRegionRef(data, null, false);
				} else {
					//If the region is new, i.e. was properly added but not loaded from a region, then
					//the region need not be unloaded or the region reference counter need not be decreased,
					//because we can be sure that this will happen later on.
					//In all other cases this should normally not cause the region to be unloaded unless the local storage
					//is a dangling instance or the region was not loaded properly, because when loaded properly the region
					//should also be loaded and be referenced accordingly. The only safe way to proceed if the region was
					//not loaded or not referenced properly is to immediately unload the region again to prevent a dangling region.
					if (!isNewStorage && this.decrRegionRef(data, null, true)) {
						TheBetweenlands.LOGGER.warn(String.format("Saving local storage with ID %s, but its region %s was not loaded. This should not happen...", storage.getID().getStringID(), data.getID()));
					}
				}
			}
		}
	}

	@Nullable
	private ILocalStorage loadLocalStorageUnsafe(LocalStorageReference reference) {
		if (!this.level.isClientSide()) {
			try {
				ILocalStorage storage = this.createLocalStorageFromFile(reference);
				if (storage != null) {
					this.addLocalStorageInternal(storage, false);

					LocalRegion region = storage.getRegion();
					if (region != null) {
						LocalRegionData data = this.regionCache.getOrCreateRegion(region);
						this.incrRegionRef(data);
					}
				}
				return storage;
			} catch (Exception ex) {
				TheBetweenlands.LOGGER.error(String.format("Failed loading local storage with ID %s at %s", reference.getID().getStringID(), "[x=" + reference.pos().x + ", z=" + reference.pos().z + "]"), ex);
			}
		}
		return null;
	}

	@Deprecated
	@Override
	public ILocalStorage loadLocalStorage(LocalStorageReference reference) {
		return this.loadLocalStorageUnsafe(reference);
	}

	@Override
	public ILocalStorageHandle getOrLoadLocalStorage(LocalStorageReference reference) {
		ILocalStorage storage = this.getLocalStorage(reference.getID());

		//If not already loaded try to load from file
		if (storage == null && !this.level.isClientSide()) {
			storage = this.loadLocalStorageUnsafe(reference);
		}

		if (storage != null) {
			return new LocalStorageHandleImpl(storage, reference);
		}

		return null;
	}

	/**
	 * Creates an instance of the local storage specified by the reference
	 *
	 * @param reference
	 * @return
	 */
	@Nullable
	private ILocalStorage createLocalStorageFromFile(LocalStorageReference reference) {
		if (!reference.hasRegion()) {
			File file = new File(this.getLocalStorageDirectory(), reference.getID().getStringID() + ".dat");
			try {
				CompoundTag nbt = this.saveHandler.loadFileNbt(file);
				;
				if (nbt != null) {
					return this.createLocalStorageFromNBT(nbt, null);
				}
			} catch (Exception ex) {
				TheBetweenlands.LOGGER.error(String.format("Failed reading local storage %s from file: %s", reference.getID().getStringID(), file.getAbsolutePath()), ex);
			}
		} else {
			LocalRegionData region = this.regionCache.getOrCreateRegion(reference.getRegion());
			CompoundTag nbt = region.getLocalStorageNBT(reference.getID());
			if (nbt != null) {
				return this.createLocalStorageFromNBT(nbt, reference.getRegion());
			}
		}
		return null;
	}

	private void incrRegionRef(@Nullable LocalRegionData data) {
		if (data != null) {
			data.incrRefCounter();

			if (data.hasReferences()) {
				this.pendingUnreferencedRegions.remove(data);
			}
		}
	}

	private boolean decrRegionRef(@Nullable LocalRegionData data, @Nullable StorageID checkID, boolean saveAndUnload) {
		if (data != null && (checkID == null || data.getLocalStorageNBT(checkID) != null)) {
			data.decrRefCounter();

			if (!data.hasReferences()) {
				if (saveAndUnload) {
					this.pendingUnreferencedRegions.put(data, this.level.getGameTime());
				}

				return true;
			}
		}

		return false;
	}

	@Override
	public boolean unloadLocalStorage(ILocalStorage storage) {
		if (this.localStorage.containsKey(storage.getID())) {
			//Only save if dirty
			if (!this.level.isClientSide() && storage.isDirty()) {
				//Skip region unloading because that'll be handled after
				//unloading the local storage
				this.saveLocalStorageFile(storage, true);
				storage.setDirty(false);
			}

			this.localStorage.remove(storage.getID());

			this.tickableLocalStorage.removeIf(tickableStorage -> storage.getID().equals(tickableStorage.getID()));

			this.pendingUnreferencedStorages.removeIf(pendingStorage -> storage.getID().equals(pendingStorage.getID()));

			storage.onUnloaded();

			if (!this.level.isClientSide()) {
				LocalRegion region = storage.getRegion();

				if (region != null) {
					//A region only has a reference from a storage if that storage was new and saved to that
					//region or if it was loaded from that region. Only in those cases the region reference
					//counter needs to be decremented and the region unloaded if no longer referenced, hence
					//decrRegionRef needs to check if the region data contains that storage.
					this.decrRegionRef(this.regionCache.getCachedRegion(region), storage.getID(), true);
				}
			}

			return true;
		}
		return false;
	}

	@Override
	public Collection<ILocalStorage> getLoadedStorages() {
		return Collections.unmodifiableCollection(this.localStorage.values());
	}

	@Override
	public File getLocalStorageDirectory() {
		return this.localStorageDir;
	}

	@Override
	public void tick() {
		for (ILocalStorage localStorage : this.tickableLocalStorage) {
			if (localStorage instanceof ITickable ticking) {
				ticking.tick();
			}

			IGenericDataAccessorAccess dataManager = localStorage.getDataManager();
			if (dataManager != null) {
				dataManager.tick();
				if (dataManager.isDirty()) {
					SyncLocalStorageDataPacket message = new SyncLocalStorageDataPacket(localStorage, false);
					for (ServerPlayer watcher : localStorage.getWatchers()) {
						PacketDistributor.sendToPlayer(watcher, message);
					}
				}
			}
		}

		if (!this.level.isClientSide()) {
			for (ILocalStorage localStorage : this.pendingUnreferencedStorages) {
				if (localStorage.getLoadedReferences().isEmpty() && this.getLocalStorage(localStorage.getID()) != null) {
					//Storage is not referenced by any chunk and being added for the first time.
					//Linking probably deferred. Save and unload storage until needed.
					this.unloadLocalStorage(localStorage);
				}
			}
			//FIXME
//			TObjectLongIterator<LocalRegionData> pendingUnreferencedRegionsIT = this.pendingUnreferencedRegions.iterator();
//			while (pendingUnreferencedRegionsIT.hasNext()) {
//				pendingUnreferencedRegionsIT.advance();
//
//				if (this.level.getGameTime() - pendingUnreferencedRegionsIT.value() > 20L) {
//					LocalRegionData data = pendingUnreferencedRegionsIT.key();
//
//					pendingUnreferencedRegionsIT.remove();
//
//					if (data.isDirty()) {
//						data.saveRegion(this.regionCache.getDir());
//					}
//
//					this.regionCache.removeRegion(data.getRegion());
//				}
//			}
		}
		this.pendingUnreferencedStorages.clear();
	}

	@Override
	public ILocalStorage createLocalStorageFromNBT(CompoundTag nbt, @Nullable LocalRegion region) {
		ResourceLocation type = ResourceLocation.parse(nbt.getString("type"));
		StorageID id = StorageID.readFromNBT(nbt);
		ILocalStorage storage = this.createLocalStorage(type, id, region);
		storage.readFromNBT(nbt.getCompound("data"));
		return storage;
	}

	@Override
	public ILocalStorage createLocalStorage(ResourceLocation type, StorageID id, @Nullable LocalRegion region) {
		StorageRegistry.Factory<? extends ILocalStorage> factory = StorageRegistry.getStorageFactory(type);
		if (factory == null) {
			throw new RuntimeException("Local storage type not mapped: " + type);
		}
		return factory.create(this.worldStorage, id, region);
	}

	@Override
	public CompoundTag saveLocalStorageToNBT(CompoundTag nbt, ILocalStorage storage) {
		ResourceLocation type = StorageRegistry.getStorageId(storage.getClass());
		if (type == null) {
			throw new RuntimeException("Local storage type not mapped: " + storage);
		}
		nbt.putString("type", type.toString());
		storage.getID().writeToNBT(nbt);
		nbt.put("data", storage.writeToNBT(new CompoundTag()));
		return nbt;
	}

	@Override
	public void queueDeferredOperation(ChunkPos chunk, IDeferredStorageOperation operation) {
		//Run immediately if chunk is already loaded
		ChunkAccess loadedChunk = this.getWorldStorage().getLevel().getChunkSource().getChunkNow(chunk.x, chunk.z);
		if (loadedChunk != null) {
			IChunkStorage chunkStorage = this.getWorldStorage().getChunkStorage(loadedChunk);
			operation.apply(chunkStorage);
			return;
		}

		LocalRegion region = LocalRegion.getFromBlockPos(chunk.x * 16, chunk.z * 16);
		LocalRegionData data = this.regionCache.getOrCreateRegion(region);

		this.incrRegionRef(data);

		try {
			CompoundTag chunkNbt = data.getChunkNBT(chunk);
			if (chunkNbt == null) {
				chunkNbt = new CompoundTag();
			}

			ListTag operationsNbt = chunkNbt.getList("DeferredOperations", Tag.TAG_COMPOUND);

			ResourceLocation type = StorageRegistry.getDeferredOperationId(operation.getClass());
			if (type == null) {
				throw new RuntimeException("Deferred storage operation type not mapped: " + operation);
			}

			CompoundTag nbt = new CompoundTag();
			nbt.putString("type", type.toString());
			nbt.put("data", operation.writeToNBT(new CompoundTag()));

			operationsNbt.add(nbt);
			chunkNbt.put("DeferredOperations", operationsNbt);

			data.setChunkNBT(chunk, chunkNbt);
		} finally {
			this.decrRegionRef(data, null, true);
		}
	}

	@Override
	public void loadDeferredOperations(IChunkStorage storage) {
		ChunkPos chunk = storage.getChunk().getPos();

		LocalRegion region = LocalRegion.getFromBlockPos(chunk.x * 16, chunk.z * 16);
		LocalRegionData data = this.regionCache.getOrCreateRegion(region, false);

		if (data != null) {
			this.incrRegionRef(data);

			try {
				CompoundTag chunkNbt = data.getChunkNBT(chunk);

				if (chunkNbt != null && chunkNbt.contains("DeferredOperations", Tag.TAG_LIST)) {
					ListTag operationsNbt = chunkNbt.getList("DeferredOperations", Tag.TAG_COMPOUND);

					for (int i = 0; i < operationsNbt.size(); i++) {
						CompoundTag nbt = operationsNbt.getCompound(i);

						ResourceLocation type = ResourceLocation.parse(nbt.getString("type"));

						Supplier<? extends IDeferredStorageOperation> factory = StorageRegistry.getDeferredOperationFactory(type);
						if (factory == null) {
							TheBetweenlands.LOGGER.error("Deferred storage operation type not mapped: {}. Skipping...", type);
							continue;
						}

						IDeferredStorageOperation operation = factory.get();

						operation.readFromNBT(nbt.getCompound("data"));

						operation.apply(storage);
					}

					//Clear deferred operations
					chunkNbt.remove("DeferredOperations");

					data.setChunkNBT(chunk, chunkNbt);
				}
			} finally {
				this.decrRegionRef(data, null, true);
			}
		}
	}

	public LocalStorageSaveHandler getSaveHandler() {
		return this.saveHandler;
	}

	@Override
	public void saveAll() {
		//Save loaded storages
		for (ILocalStorage localStorage : this.getLoadedStorages()) {
			//Only save if dirty
			if (localStorage.isDirty()) {
				this.saveLocalStorageFile(localStorage);
				localStorage.setDirty(false);
			}
		}

		//Save and unload all unreferenced regions
		for (LocalRegionData data : this.pendingUnreferencedRegions.keySet()) {
			if (data.isDirty()) {
				data.saveRegion(this.regionCache.getDir());
			}

			this.regionCache.removeRegion(data.getRegion());
		}
		this.pendingUnreferencedRegions.clear();

		//Save rest of regional cache
		this.regionCache.saveAllRegions();
	}
}
