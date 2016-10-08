package thebetweenlands.common.world.storage.world.global;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Nullable;

import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.storage.MapStorage;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import thebetweenlands.common.world.storage.chunk.ChunkDataBase;
import thebetweenlands.common.world.storage.chunk.shared.SharedStorageReference;
import thebetweenlands.common.world.storage.world.shared.SharedStorage;

/**
 * World specific storage.
 * <p><b>{@link WorldDataBase#WORLD_EVENT_HANDLER} must be registered to {@link MinecraftForge.EVENT_BUS}.</b>
 * 
 * @param <T> Chunk storage type
 */
public abstract class WorldDataBase<T extends ChunkDataBase> extends WorldSavedData {
	public static final WorldEventHandler WORLD_EVENT_HANDLER = new WorldEventHandler();
	private static final Map<WorldDataTypePair, WorldDataBase<?>> CACHE = new HashMap<WorldDataTypePair, WorldDataBase<?>>();

	private World world;
	private NBTTagCompound data = new NBTTagCompound();
	private final Map<String, SharedStorage> sharedStorage = new HashMap<String, SharedStorage>();
	private File sharedStorageDir;

	public WorldDataBase(String name) {
		super(name);
	}

	private static WorldDataBase<?> getMatchingData(World world, Class<? extends WorldDataBase<?>> clazz) {
		for (Entry<WorldDataTypePair, WorldDataBase<?>> cacheEntry : CACHE.entrySet()) {
			WorldDataTypePair pair = cacheEntry.getKey();
			if (pair.world == world && pair.data.equals(clazz)) {
				return cacheEntry.getValue();
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public static <T extends WorldDataBase<?>> T forWorld(World world, Class<T> clazz) {
		WorldDataBase<?> cached = getMatchingData(world, clazz);
		if (cached != null) {
			cached.world = world;
			return (T) cached;
		}
		MapStorage storage = world.getPerWorldStorage();
		T newInstance = null;
		try {
			newInstance = clazz.getConstructor().newInstance();
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
		WorldDataBase<?> result = (WorldDataBase<?>) storage.getOrLoadData(clazz, newInstance.mapName);
		if (result == null) {
			result = newInstance;
			result.world = world;
			result.init();
			result.setDefaults();
			result.save();
			result.markDirty();
			storage.setData(result.mapName, result);
		} else {
			result.world = world;
			result.init();
			result.load();
		}
		result.sharedStorageDir = new File(world.getSaveHandler().getWorldDirectory(), "data" + File.separator + "shared_storage" + File.separator);
		CACHE.put(new WorldDataTypePair(world, clazz), result);
		return (T) result;
	}

	/**
	 * Returns the world object
	 * @return
	 */
	public World getWorld() {
		return this.world;
	}

	@Override
	public final void readFromNBT(NBTTagCompound compound) {
		this.data = compound.getCompoundTag("worldData");
	}

	@Override
	public final NBTTagCompound writeToNBT(NBTTagCompound compound) {
		this.save();
		compound.setTag("worldData", this.data);
		return compound;
	}

	/**
	 * Load data here
	 */
	protected abstract void load();

	/**
	 * Save data here
	 */
	protected abstract void save();

	/**
	 * Called before loading data and setting defaults
	 */
	protected abstract void init();

	/**
	 * Called when this data is created for the first time for this world
	 */
	protected abstract void setDefaults();

	/**
	 * Returns the chunk storage
	 * @return
	 */
	public abstract Class<T> getChunkStorage();

	/**
	 * Returns the world data
	 * @return
	 */
	public NBTTagCompound getData() {
		return this.data;
	}

	/**
	 * Returns the shared storage file directory
	 * @return
	 */
	public File getSharedStorageDirectory() {
		return this.sharedStorageDir;
	}

	/**
	 * Adds a shared storage
	 * @param storage
	 */
	public void addSharedStorage(SharedStorage storage) {
		this.sharedStorage.put(storage.getUUIDString(), storage);

		//Add already loaded references
		for(ChunkPos referenceChunk : storage.getLinkedChunks()) {
			Chunk chunk = this.world.getChunkProvider().getLoadedChunk(referenceChunk.chunkXPos, referenceChunk.chunkZPos);
			if(chunk != null) {
				ChunkDataBase chunkData = ChunkDataBase.forChunk(this, chunk);
				SharedStorageReference reference = chunkData.getReference(storage.getUUID());
				if(reference != null && !storage.getReferences().contains(reference)) {
					storage.loadReference(reference);
				}
			}
		}

		storage.onLoaded();
	}

	/**
	 * Permanently removes a shared storage
	 * @param storage
	 */
	public void removeSharedStorage(SharedStorage storage) {
		if(this.sharedStorage.containsKey(storage.getUUIDString())) {
			if(!this.world.isRemote) {
				storage.unlinkAllChunks();
			}
			this.sharedStorage.remove(storage.getUUIDString());
			if(!this.world.isRemote) {
				File file = new File(this.getSharedStorageDirectory(), storage.getUUIDString() + ".dat");
				if(file.exists()) {
					file.delete();
				}
			}
			storage.onUnloaded();
		}
	}

	/**
	 * Returns the shared storage with the specified UUID
	 * @param uuid
	 * @return
	 */
	public SharedStorage getSharedStorage(String uuid) {
		return this.sharedStorage.get(uuid);
	}

	/**
	 * Returns a list of all currently loaded shared storages
	 * @return
	 */
	public Collection<SharedStorage> getSharedStorage() {
		return Collections.unmodifiableCollection(this.sharedStorage.values());
	}

	/**
	 * Loads a shared storage from a file
	 * @param reference
	 */
	@Nullable
	public SharedStorage loadSharedStorage(SharedStorageReference reference) {
		SharedStorage storage = null;
		NBTTagCompound nbt = null;
		File file = new File(this.getSharedStorageDirectory(), reference.getUUIDString() + ".dat");
		if(file.exists()) {
			try {
				nbt = CompressedStreamTools.read(file);
			} catch(Exception ex) {
				throw new RuntimeException(ex);
			}
			storage = SharedStorage.load(this, nbt, false);
		} else {
			return null;
		}
		this.addSharedStorage(storage);
		storage.onLoaded();
		return storage;
	}

	/**
	 * Unloads a shared storage and saves to a file if necessary
	 * @param storage
	 */
	public void unloadSharedStorage(SharedStorage storage) {
		if(this.sharedStorage.containsKey(storage.getUUIDString())) {
			//Only save if dirty
			if(storage.isDirty()) {
				this.saveSharedStorage(storage);
			}
			this.sharedStorage.remove(storage.getUUIDString());
			storage.onUnloaded();
		}
	}

	/**
	 * Saves the specified shared storage to a file
	 * @param storage
	 */
	public void saveSharedStorage(SharedStorage storage) {
		NBTTagCompound nbt = SharedStorage.save(storage, new NBTTagCompound(), false);
		try {
			File savePath = this.getSharedStorageDirectory();
			savePath.mkdirs();
			File file = new File(savePath, storage.getUUIDString() + ".dat");
			CompressedStreamTools.safeWrite(nbt, file);
		} catch(Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	private static class WorldDataTypePair {
		private World world;
		private Class<? extends WorldDataBase<?>> data;
		private WorldDataTypePair(World world, Class<? extends WorldDataBase<?>> data) {
			this.world = world;
			this.data = data;
		}
	}

	public static class WorldEventHandler {
		private WorldEventHandler(){}

		@SubscribeEvent
		public void onWorldUnload(WorldEvent.Unload event) {
			Iterator<Entry<WorldDataTypePair, WorldDataBase<?>>> cacheIT = CACHE.entrySet().iterator();
			while(cacheIT.hasNext()) {
				Entry<WorldDataTypePair, WorldDataBase<?>> entry = cacheIT.next();
				World world = entry.getKey().world;
				if(world.equals(event.getWorld())) {
					//Unload and save shared storages
					WorldDataBase<?> worldStorage = entry.getValue();
					List<SharedStorage> sharedStorages = new ArrayList<>();
					sharedStorages.addAll(worldStorage.getSharedStorage());
					for(SharedStorage sharedStorage : sharedStorages) {
						worldStorage.unloadSharedStorage(sharedStorage);
					}

					//Remove entry
					cacheIT.remove();
				}
			}
		}

		@SubscribeEvent
		public void onWorldSave(WorldEvent.Save event) {
			for(WorldDataBase<?> worldStorage : CACHE.values()) {
				//Save shared storages
				List<SharedStorage> sharedStorages = new ArrayList<>();
				sharedStorages.addAll(worldStorage.getSharedStorage());
				for(SharedStorage sharedStorage : sharedStorages) {
					worldStorage.saveSharedStorage(sharedStorage);
				}
			}
		}
	}
}
