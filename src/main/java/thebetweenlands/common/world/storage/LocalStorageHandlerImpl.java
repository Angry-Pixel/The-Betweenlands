package thebetweenlands.common.world.storage;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import com.google.common.base.Predicate;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.phys.AABB;
import thebetweenlands.api.storage.IChunkStorage;
import thebetweenlands.api.storage.IDeferredStorageOperation;
import thebetweenlands.api.storage.ILocalStorage;
import thebetweenlands.api.storage.ILocalStorageHandle;
import thebetweenlands.api.storage.ILocalStorageHandler;
import thebetweenlands.api.storage.IWorldStorage;
import thebetweenlands.api.storage.LocalRegion;
import thebetweenlands.api.storage.LocalStorageReference;
import thebetweenlands.api.storage.StorageID;
import thebetweenlands.common.registries.StorageRegistry;

public class LocalStorageHandlerImpl implements ILocalStorageHandler {
	private final IWorldStorage worldStorage;
	private final Level level;

	private final Map<StorageID, ILocalStorage> localStorage = new HashMap<>();
	private final List<ILocalStorage> tickableLocalStorage = new ArrayList<>();
	private final List<ILocalStorage> pendingUnreferencedStorages = new ArrayList<>();

//	private final TObjectLongMap<LocalRegionData> pendingUnreferencedRegions = new TObjectLongHashMap<>();
//
//	private final LocalRegionCache regionCache;
//
//	private final LocalStorageSaveHandler saveHandler = new LocalStorageSaveHandler();


	public LocalStorageHandlerImpl(IWorldStorage master) {
		this.worldStorage = master;
		this.level = master.getLevel();
	}

	@Override
	public IWorldStorage getWorldStorage() {
		return this.worldStorage;
	}

	@Override
	public boolean addLocalStorage(ILocalStorage storage) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean removeLocalStorage(ILocalStorage storage) {
		// TODO Auto-generated method stub
		return false;
	}

	@Nullable
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
		LevelChunk chunk = this.level.getChunk(cx, cz);
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
	public <T extends ILocalStorage> List<T> getLocalStorages(Class<T> type, AABB aabb, @Nullable Predicate<T> filter) {
		List<T> storages = new ArrayList<>();
		int sx = Mth.floor(aabb.minX) >> 4;
		int sz = Mth.floor(aabb.minZ) >> 4;
		int ex = Mth.floor(aabb.maxX) >> 4;
		int ez = Mth.floor(aabb.maxZ) >> 4;
		for(int cx = sx; cx <= ex; cx++) {
			for(int cz = sz; cz <= ez; cz++) {
				LevelChunk chunk = this.level.getChunk(cx, cz);
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
		// TODO Auto-generated method stub

	}

	@Override
	public void saveLocalStorageFile(ILocalStorage storage) {
		// TODO Auto-generated method stub

	}

	@Override
	public ILocalStorage loadLocalStorage(LocalStorageReference reference) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ILocalStorageHandle getOrLoadLocalStorage(LocalStorageReference reference) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean unloadLocalStorage(ILocalStorage storage) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Collection<ILocalStorage> getLoadedStorages() {
		return Collections.unmodifiableCollection(this.localStorage.values());
	}

	@Override
	public void tick() {
		// TODO Auto-generated method stub

	}

	@Override
	public File getLocalStorageDirectory() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ILocalStorage createLocalStorageFromNBT(CompoundTag tag, LocalRegion region) {
		ResourceLocation type = ResourceLocation.tryParse(tag.getString("type"));
		StorageID id = StorageID.readFromNBT(tag);
		ILocalStorage storage = this.createLocalStorage(type, id, region);
		storage.readFromNBT(tag.getCompound("data"));
		return storage;
	}

	@Override
	public ILocalStorage createLocalStorage(ResourceLocation type, StorageID id, LocalRegion region) {
		StorageRegistry.Factory<? extends ILocalStorage> factory = StorageRegistry.getStorageFactory(type);
		if (factory == null) {
			throw new RuntimeException("Local storage type not mapped: " + type);
		}
		return factory.create(this.worldStorage, id, region);
	}

	@Override
	public CompoundTag saveLocalStorageToNBT(CompoundTag tag, ILocalStorage storage) {
		ResourceLocation type = StorageRegistry.getStorageId(storage.getClass());
		if (type == null) {
			throw new RuntimeException("Local storage type not mapped: " + storage);
		}
		tag.putString("type", type.toString());
		storage.getID().writeToNBT(tag);
		tag.put("data", storage.writeToNBT(new CompoundTag()));
		return tag;
	}

	@Override
	public void queueDeferredOperation(ChunkPos chunk, IDeferredStorageOperation operation) {
		// TODO Auto-generated method stub

	}

	@Override
	public void loadDeferredOperations(IChunkStorage storage) {
		// TODO Auto-generated method stub

	}

	@Override
	public void saveAll() {
		// TODO Auto-generated method stub

	}

}
