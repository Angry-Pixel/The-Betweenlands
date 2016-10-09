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
import java.util.function.Predicate;

import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.storage.MapStorage;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityDispatcher;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.WorldTickEvent;
import thebetweenlands.common.event.AttachWorldCapabilitiesEvent;
import thebetweenlands.common.world.storage.chunk.ChunkDataBase;
import thebetweenlands.common.world.storage.chunk.shared.SharedStorageReference;
import thebetweenlands.common.world.storage.world.shared.SharedStorage;

/**
 * World specific storage.
 * <p>Capabilities can be attached using the {@link AttachWorldCapabilitiesEvent} event.
 * <p><b>{@link WorldDataBase#WORLD_EVENT_HANDLER} must be registered to {@link MinecraftForge.EVENT_BUS}.</b>
 * 
 * @param <T> Chunk storage type
 */
public abstract class WorldDataBase<T extends ChunkDataBase> extends WorldSavedData implements ICapabilityProvider {
	public static final WorldEventHandler WORLD_EVENT_HANDLER = new WorldEventHandler();
	private static final Map<WorldDataTypePair, WorldDataBase<?>> CACHE = new HashMap<WorldDataTypePair, WorldDataBase<?>>();

	private World world;
	private NBTTagCompound data = new NBTTagCompound();
	private final Map<String, SharedStorage> sharedStorage = new HashMap<String, SharedStorage>();
	private final List<SharedStorage> tickableSharedStorage = new ArrayList<>();
	private File sharedStorageDir;
	private CapabilityDispatcher capabilities;

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

		//Gather capabilities
		AttachCapabilitiesEvent event = new AttachWorldCapabilitiesEvent(result);
		MinecraftForge.EVENT_BUS.post(event);
		result.capabilities = event.getCapabilities().size() > 0 ? new CapabilityDispatcher(event.getCapabilities(), null) : null;

		CACHE.put(new WorldDataTypePair(world, clazz), result);

		return (T) result;
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		return this.capabilities == null ? false : this.capabilities.hasCapability(capability, facing);
	}

	@Override
	public <F> F getCapability(Capability<F> capability, EnumFacing facing) {
		return this.capabilities == null ? null : this.capabilities.getCapability(capability, facing);
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
		if(!this.sharedStorage.containsKey(storage.getUUIDString())) {
			this.sharedStorage.put(storage.getUUIDString(), storage);

			if(storage instanceof ITickable) {
				this.tickableSharedStorage.add(storage);
			}

			//Add already loaded references and watchers
			for(ChunkPos referenceChunk : storage.getLinkedChunks()) {
				Chunk chunk = this.world.getChunkProvider().getLoadedChunk(referenceChunk.chunkXPos, referenceChunk.chunkZPos);
				if(chunk != null) {
					ChunkDataBase chunkData = ChunkDataBase.forChunk(this, chunk);
					if(chunkData != null) {
						SharedStorageReference reference = chunkData.getReference(storage.getUUID());
						if(reference != null && !storage.getReferences().contains(reference)) {
							//Add reference
							storage.loadReference(reference);

							//Add watchers
							for(EntityPlayerMP watcher : chunkData.getWatchers()) {
								if(!storage.getWatchers().contains(watcher)) {
									storage.onWatched(chunkData, watcher);
								}
							}
						}
					}
				}
			}

			storage.onLoaded();
		}
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

			Iterator<SharedStorage> it = this.tickableSharedStorage.iterator();
			SharedStorage tickableStorage = null;
			while(it.hasNext()) {
				tickableStorage = it.next();
				if(storage.getUUIDString().equals(tickableStorage.getUUIDString())) {
					it.remove();
				}
			}

			if(!this.world.isRemote) {
				this.deleteSharedStorageFile(storage);
			}

			storage.onUnloaded();

			storage.onRemoved();
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
			if(storage.isDirty() && !this.world.isRemote) {
				this.saveSharedStorageFile(storage);
				storage.setDirty(false);
			}

			this.sharedStorage.remove(storage.getUUIDString());

			Iterator<SharedStorage> it = this.tickableSharedStorage.iterator();
			SharedStorage tickableStorage = null;
			while(it.hasNext()) {
				tickableStorage = it.next();
				if(storage.getUUIDString().equals(tickableStorage.getUUIDString())) {
					it.remove();
				}
			}

			storage.onUnloaded();
		}
	}

	/**
	 * Saves the specified shared storage to a file
	 * @param storage
	 */
	public void saveSharedStorageFile(SharedStorage storage) {
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

	/**
	 * Deletes the specified shared storage file
	 * @param storage
	 */
	public void deleteSharedStorageFile(SharedStorage storage) {
		File file = new File(this.getSharedStorageDirectory(), storage.getUUIDString() + ".dat");
		if(file.exists()) {
			file.delete();
		}
	}

	/**
	 * Returns a list of shared storages at the specified position
	 * @param storageClass
	 * @param selector
	 * @param x
	 * @param y
	 * @param z
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <F extends SharedStorage> List<F> getSharedStorageAt(Class<F> storageClass, @Nullable Predicate<F> selector, double x, double z) {
		List<F> storages = new ArrayList<>();
		int cx = MathHelper.floor_double(x) / 16;
		int cz = MathHelper.floor_double(z) / 16;
		Chunk chunk = this.world.getChunkFromChunkCoords(cx, cz);
		ChunkDataBase chunkStorage = ChunkDataBase.forChunk(this, chunk);
		if(chunkStorage != null) {
			for(SharedStorageReference ref : chunkStorage.getSharedStorageReferences()) {
				SharedStorage storage = this.getSharedStorage(ref.getUUIDString());
				if(storage != null && storageClass.isAssignableFrom(storage.getClass()) && (selector == null || selector.test((F) storage))) {
					storages.add((F) storage);
				}
			}
		}
		return storages;
	}

	/**
	 * Returns a list of shared storages at the specified position
	 * @param storageClass
	 * @param x
	 * @param z
	 * @return
	 */
	public <F extends SharedStorage> List<F> getSharedStorageAt(Class<F> storageClass, double x, double z) {
		return this.getSharedStorageAt(storageClass, null, x, z);
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
					//Only save if dirty
					if(sharedStorage.isDirty()) {
						worldStorage.saveSharedStorageFile(sharedStorage);
						sharedStorage.setDirty(false);
					}
				}
			}
		}

		@SubscribeEvent
		public void onWorldTick(WorldTickEvent event) {
			for(WorldDataBase<?> worldStorage : CACHE.values()) {
				for(SharedStorage sharedStorage : worldStorage.tickableSharedStorage) {
					((ITickable)sharedStorage).update();
				}
			}
		}
	}
}
