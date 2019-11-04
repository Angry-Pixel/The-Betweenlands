package thebetweenlands.common.world.storage;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import com.google.common.base.Predicate;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ITickable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import thebetweenlands.api.network.IGenericDataManagerAccess;
import thebetweenlands.api.storage.IChunkStorage;
import thebetweenlands.api.storage.ILocalStorage;
import thebetweenlands.api.storage.ILocalStorageHandler;
import thebetweenlands.api.storage.IWorldStorage;
import thebetweenlands.api.storage.LocalRegion;
import thebetweenlands.api.storage.LocalStorageReference;
import thebetweenlands.api.storage.StorageID;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.network.clientbound.MessageSyncLocalStorageData;
import thebetweenlands.common.registries.StorageRegistry;

public class LocalStorageHandlerImpl implements ILocalStorageHandler {
	private final IWorldStorage worldStorage;
	private final World world;
	private final File localStorageDir;

	private final Map<StorageID, ILocalStorage> localStorage = new HashMap<StorageID, ILocalStorage>();
	private final List<ILocalStorage> tickableLocalStorage = new ArrayList<>();

	private final LocalRegionCache regionCache;

	private final LocalStorageSaveHandler saveHandler = new LocalStorageSaveHandler();

	public LocalStorageHandlerImpl(IWorldStorage worldStorage) {
		this.worldStorage = worldStorage;
		this.world = worldStorage.getWorld();
		String dimFolder = this.world.provider.getSaveFolder();
		this.localStorageDir = new File(this.world.getSaveHandler().getWorldDirectory(), (dimFolder != null && dimFolder.length() > 0 ? dimFolder + File.separator : "") + "data" + File.separator + "local_storage" + File.separator);
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
		if(!this.localStorage.containsKey(storage.getID())) {
			this.localStorage.put(storage.getID(), storage);

			if(storage instanceof ITickable) {
				this.tickableLocalStorage.add(storage);
			}

			//Add already loaded references
			for(ChunkPos referenceChunk : storage.getLinkedChunks()) {
				Chunk chunk = this.world.getChunkProvider().getLoadedChunk(referenceChunk.x, referenceChunk.z);
				if(chunk != null) {
					IChunkStorage chunkStorage = this.worldStorage.getChunkStorage(chunk);
					if(chunkStorage != null) {
						LocalStorageReference reference = chunkStorage.getReference(storage.getID());
						if(reference != null && !storage.getLoadedReferences().contains(reference)) {
							//Add reference
							storage.loadReference(reference);
						}
					}
				}
			}

			if(isInitialAdd) {
				storage.onAdded();
			}

			storage.onLoaded();

			return true;
		}
		return false;
	}

	@Override
	public boolean removeLocalStorage(ILocalStorage storage) {
		if(this.localStorage.containsKey(storage.getID())) {
			storage.onRemoving();
			
			if(!this.world.isRemote) {
				storage.unlinkAllChunks();
			}

			this.localStorage.remove(storage.getID());

			Iterator<ILocalStorage> it = this.tickableLocalStorage.iterator();
			ILocalStorage tickableStorage = null;
			while(it.hasNext()) {
				tickableStorage = it.next();
				if(storage.getID().equals(tickableStorage.getID())) {
					it.remove();
				}
			}

			if(!this.world.isRemote) {
				this.deleteLocalStorageFile(storage);
			}

			storage.onUnloaded();

			storage.onRemoved();

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
		int cx = MathHelper.floor(x) >> 4;
		int cz = MathHelper.floor(z) >> 4;
		Chunk chunk = this.world.getChunk(cx, cz);
		IChunkStorage chunkStorage = this.getWorldStorage().getChunkStorage(chunk);
		if(chunkStorage != null) {
			for(LocalStorageReference ref : chunkStorage.getLocalStorageReferences()) {
				ILocalStorage localStorage = this.getLocalStorage(ref.getID());
				if(localStorage != null && localStorage.getBoundingBox() != null && type.isAssignableFrom(localStorage.getClass())
						&& (filter == null || filter.apply((T) localStorage))) {
					if(!storages.contains(localStorage)) {
						storages.add((T) localStorage);
					}
				}
			}
		}
		return storages;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends ILocalStorage> List<T> getLocalStorages(Class<T> type, AxisAlignedBB aabb, @Nullable Predicate<T> filter) {
		List<T> storages = new ArrayList<>();
		int sx = MathHelper.floor(aabb.minX) >> 4;
		int sz = MathHelper.floor(aabb.minZ) >> 4;
		int ex = MathHelper.floor(aabb.maxX) >> 4;
		int ez = MathHelper.floor(aabb.maxZ) >> 4;
		for(int cx = sx; cx <= ex; cx++) {
			for(int cz = sz; cz <= ez; cz++) {
				Chunk chunk = this.world.getChunk(cx, cz);
				IChunkStorage chunkStorage = this.getWorldStorage().getChunkStorage(chunk);
				if(chunkStorage != null) {
					for(LocalStorageReference ref : chunkStorage.getLocalStorageReferences()) {
						ILocalStorage localStorage = this.getLocalStorage(ref.getID());
						if(localStorage != null && localStorage.getBoundingBox() != null && type.isAssignableFrom(localStorage.getClass()) && localStorage.getBoundingBox().intersects(aabb)
								&& (filter == null || filter.apply((T) localStorage))) {
							if(!storages.contains(localStorage)) {
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
		if(storage.getRegion() == null) {
			File file = new File(this.getLocalStorageDirectory(), storage.getID().getStringID() + ".dat");
			this.saveHandler.queueLocalStorage(file, null);
		} else {
			LocalRegionData regionData = this.regionCache.getOrCreateRegion(storage.getRegion());
			if(regionData != null) {
				regionData.deleteLocalStorage(this.regionCache.getDir(), storage.getID());
			}
		}
	}

	@Override
	public void saveLocalStorageFile(ILocalStorage storage) {
		NBTTagCompound nbt = this.saveLocalStorageToNBT(new NBTTagCompound(), storage);
		if(storage.getRegion() == null) {
			File file = new File(this.getLocalStorageDirectory(), storage.getID().getStringID() + ".dat");
			this.saveHandler.queueLocalStorage(file, nbt);
		} else {
			LocalRegionData region = this.regionCache.getOrCreateRegion(storage.getRegion());
			region.setLocalStorageNBT(storage.getID(), nbt);
		}
	}

	@Override
	public ILocalStorage loadLocalStorage(LocalStorageReference reference) {
		try {
			ILocalStorage storage = this.createLocalStorageFromFile(reference);
			if(storage != null) {
				this.addLocalStorageInternal(storage, false);

				if(storage.getRegion() != null) {
					LocalRegionData data = this.regionCache.getOrCreateRegion(reference.getRegion());
					data.incrRefCounter();
				}

				return storage;
			}
		} catch(Exception ex) {
			TheBetweenlands.logger.error(String.format("Failed loading local storage with ID %s at %s", reference.getID().getStringID(), "[x=" + reference.getChunk().x + ", z=" + reference.getChunk().z + "]"), ex);
		}
		return null;
	}

	/**
	 * Creates an instance of the local storage specified by the reference
	 * @param reference
	 * @return
	 */
	@Nullable
	private ILocalStorage createLocalStorageFromFile(LocalStorageReference reference) {
		if(!reference.hasRegion()) {
			File file = new File(this.getLocalStorageDirectory(), reference.getID().getStringID() + ".dat");
			try {
				NBTTagCompound nbt = this.saveHandler.loadFileNbt(file);;
				if(nbt != null) {
					return this.createLocalStorageFromNBT(nbt, null);
				}
			} catch(Exception ex) {
				TheBetweenlands.logger.error(String.format("Failed reading local storage %s from file: %s", reference.getID().getStringID(), file.getAbsolutePath()), ex);
			}
		} else {
			LocalRegionData region = this.regionCache.getOrCreateRegion(reference.getRegion());
			NBTTagCompound nbt = region.getLocalStorageNBT(reference.getID());
			if(nbt != null) {
				return this.createLocalStorageFromNBT(nbt, reference.getRegion());
			}
		}
		return null;
	}

	@Override
	public boolean unloadLocalStorage(ILocalStorage storage) {
		if(this.localStorage.containsKey(storage.getID())) {
			//Only save if dirty
			if(!this.world.isRemote && storage.isDirty()) {
				this.saveLocalStorageFile(storage);
				storage.setDirty(false);
			}

			this.localStorage.remove(storage.getID());

			Iterator<ILocalStorage> it = this.tickableLocalStorage.iterator();
			ILocalStorage tickableStorage = null;
			while(it.hasNext()) {
				tickableStorage = it.next();
				if(storage.getID().equals(tickableStorage.getID())) {
					it.remove();
				}
			}

			storage.onUnloaded();

			if(!this.world.isRemote && storage.getRegion() != null) {
				LocalRegionData data = this.regionCache.getOrCreateRegion(storage.getRegion());
				data.decrRefCounter();

				if(!data.hasReferences()) {
					if(data.isDirty()) {
						data.saveRegion(this.regionCache.getDir());
					}
					this.regionCache.removeRegion(storage.getRegion());
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
	public void update() {
		for(int i = 0; i < this.tickableLocalStorage.size(); i++) {
			ILocalStorage localStorage = this.tickableLocalStorage.get(i);
			((ITickable)localStorage).update();

			IGenericDataManagerAccess dataManager = localStorage.getDataManager();
			if(dataManager != null) {
				dataManager.update();
				if(dataManager.isDirty()) {
					MessageSyncLocalStorageData message = new MessageSyncLocalStorageData(localStorage, false);
					for (EntityPlayerMP watcher : localStorage.getWatchers()) {
						TheBetweenlands.networkWrapper.sendTo(message, watcher);
					}
				}
			}
		}
	}

	@Override
	public ILocalStorage createLocalStorageFromNBT(NBTTagCompound nbt, @Nullable LocalRegion region) {
		ResourceLocation type = new ResourceLocation(nbt.getString("type"));
		StorageID id = StorageID.readFromNBT(nbt);
		ILocalStorage storage = this.createLocalStorage(type, id, region);
		storage.readFromNBT(nbt.getCompoundTag("data"));
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
	public NBTTagCompound saveLocalStorageToNBT(NBTTagCompound nbt, ILocalStorage storage) {
		ResourceLocation type = StorageRegistry.getStorageId(storage.getClass());
		if (type == null) {
			throw new RuntimeException("Local storage type not mapped: " + storage);
		}
		nbt.setString("type", type.toString());
		storage.getID().writeToNBT(nbt);
		nbt.setTag("data", storage.writeToNBT(new NBTTagCompound()));
		return nbt;
	}

	public LocalStorageSaveHandler getSaveHandler() {
		return this.saveHandler;
	}

	@Override
	public void saveAll() {
		//Save loaded storages
		for(ILocalStorage localStorage : this.getLoadedStorages()) {
			//Only save if dirty
			if(localStorage.isDirty()) {
				this.saveLocalStorageFile(localStorage);
				localStorage.setDirty(false);
			}
		}

		//Save regional cache
		this.regionCache.saveAllRegions();
	}
}
