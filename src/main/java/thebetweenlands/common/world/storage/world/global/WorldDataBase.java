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

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
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
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.WorldTickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.event.AttachWorldCapabilitiesEvent;
import thebetweenlands.common.world.storage.chunk.ChunkDataBase;
import thebetweenlands.common.world.storage.chunk.shared.SharedStorageReference;
import thebetweenlands.common.world.storage.world.shared.SharedRegionCache;
import thebetweenlands.common.world.storage.world.shared.SharedRegionData;
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
	private SharedRegionCache regionCache;

	public WorldDataBase(String name) {
		super(name);
	}

	private static WorldDataBase<?> getMatchingData(World world, Class<? extends WorldDataBase<?>> clazz) {
		synchronized(CACHE) {
			for (Entry<WorldDataTypePair, WorldDataBase<?>> cacheEntry : CACHE.entrySet()) {
				WorldDataTypePair pair = cacheEntry.getKey();
				if (pair.world == world && pair.data.equals(clazz)) {
					return cacheEntry.getValue();
				}
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

		String dimFolder = world.provider.getSaveFolder();
		File sharedStorageDir = new File(world.getSaveHandler().getWorldDirectory(), (dimFolder != null && dimFolder.length() > 0 ? dimFolder + File.separator : "") + "data" + File.separator + "shared_storage" + File.separator);
		SharedRegionCache regionCache = new SharedRegionCache(new File(sharedStorageDir, "region"));

		if (result == null) {
			result = newInstance;
			result.world = world;
			result.sharedStorageDir = sharedStorageDir;
			result.regionCache = regionCache;
			result.init();
			result.setDefaults();
			result.save();
			result.markDirty();
			storage.setData(result.mapName, result);
		} else {
			result.world = world;
			result.sharedStorageDir = sharedStorageDir;
			result.regionCache = regionCache;
			result.init();
			result.load();
		}

		//Gather capabilities
		AttachCapabilitiesEvent<WorldDataBase<?>> event = new AttachWorldCapabilitiesEvent(result);
		MinecraftForge.EVENT_BUS.post(event);
		result.capabilities = event.getCapabilities().size() > 0 ? new CapabilityDispatcher(event.getCapabilities(), null) : null;

		synchronized(CACHE) {
			CACHE.put(new WorldDataTypePair(world, clazz), result);
		}

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
	 * Adds a shared storage. All necessary chunks must be linked before adding the specified shared storage.
	 * @param storage
	 * @return True if the storage was successfully added
	 */
	public boolean addSharedStorage(SharedStorage storage) {
		if(!this.sharedStorage.containsKey(storage.getID()) && !storage.getLinkedChunks().isEmpty()) {
			this.sharedStorage.put(storage.getID(), storage);

			if(storage instanceof ITickable) {
				this.tickableSharedStorage.add(storage);
			}

			//Add already loaded references and watchers
			for(ChunkPos referenceChunk : storage.getLinkedChunks()) {
				Chunk chunk = this.world.getChunkProvider().getLoadedChunk(referenceChunk.chunkXPos, referenceChunk.chunkZPos);
				if(chunk != null) {
					ChunkDataBase chunkData = ChunkDataBase.forChunk(this, chunk);
					if(chunkData != null) {
						SharedStorageReference reference = chunkData.getReference(storage.getID());
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

			return true;
		}
		return false;
	}

	/**
	 * Permanently removes a shared storage. All linked chunks will be unlinked.
	 * @param storage
	 * @return True if the storage was successfully removed
	 */
	public boolean removeSharedStorage(SharedStorage storage) {
		if(this.sharedStorage.containsKey(storage.getID())) {
			if(!this.world.isRemote) {
				storage.unlinkAllChunks();
			}

			this.sharedStorage.remove(storage.getID());

			Iterator<SharedStorage> it = this.tickableSharedStorage.iterator();
			SharedStorage tickableStorage = null;
			while(it.hasNext()) {
				tickableStorage = it.next();
				if(storage.getID().equals(tickableStorage.getID())) {
					it.remove();
				}
			}

			if(!this.world.isRemote) {
				this.deleteSharedStorageFile(storage);
			}

			storage.onUnloaded();

			storage.onRemoved();

			return true;
		}
		return false;
	}

	/**
	 * Returns the shared storage with the specified ID
	 * @param id
	 * @return
	 */
	public SharedStorage getSharedStorage(String id) {
		return this.sharedStorage.get(id);
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
		SharedStorage storage = this.loadSharedStorageFile(reference);
		if(storage != null) {
			this.addSharedStorage(storage);
			storage.onLoaded();

			if(storage.hasRegion()) {
				SharedRegionData data = this.regionCache.getOrCreateRegion(reference.getRegion());
				data.incrRefCounter();
			}

			return storage;
		}
		return null;
	}

	/**
	 * Unloads a shared storage and saves to a file if necessary
	 * @param storage
	 * @return True if the storage was successfully unloaded
	 */
	public boolean unloadSharedStorage(SharedStorage storage) {
		if(this.sharedStorage.containsKey(storage.getID())) {
			//Only save if dirty
			if(!this.world.isRemote && storage.isDirty()) {
				this.saveSharedStorageFile(storage);
				storage.setDirty(false);
			}

			this.sharedStorage.remove(storage.getID());

			Iterator<SharedStorage> it = this.tickableSharedStorage.iterator();
			SharedStorage tickableStorage = null;
			while(it.hasNext()) {
				tickableStorage = it.next();
				if(storage.getID().equals(tickableStorage.getID())) {
					it.remove();
				}
			}

			storage.onUnloaded();

			if(!this.world.isRemote && storage.hasRegion()) {
				SharedRegionData data = this.regionCache.getOrCreateRegion(storage.getRegion());
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

	/**
	 * Loads the the specified shared storage from a file
	 * @param reference
	 * @return
	 */
	@Nullable
	public SharedStorage loadSharedStorageFile(SharedStorageReference reference) {
		if(!reference.hasRegion()) {
			File file = new File(this.getSharedStorageDirectory(), reference.getID() + ".dat");
			if(file.exists()) {
				try {
					NBTTagCompound nbt = CompressedStreamTools.read(file);
					return SharedStorage.load(this, nbt, null, false);
				} catch(Exception ex) {
					throw new RuntimeException(ex);
				}
			}
		} else {
			SharedRegionData region = this.regionCache.getOrCreateRegion(reference.getRegion());
			NBTTagCompound nbt = region.getSharedStorageNBT(reference.getID());
			if(nbt != null) {
				return SharedStorage.load(this, nbt, reference.getRegion(), false);
			}
		}
		return null;
	}

	/**
	 * Saves the specified shared storage to a file
	 * @param storage
	 */
	public void saveSharedStorageFile(SharedStorage storage) {
		NBTTagCompound nbt = SharedStorage.save(storage, new NBTTagCompound(), false);
		if(!storage.hasRegion()) {
			try {
				File savePath = this.getSharedStorageDirectory();
				savePath.mkdirs();
				File file = new File(savePath, storage.getID() + ".dat");
				CompressedStreamTools.safeWrite(nbt, file);
			} catch(Exception ex) {
				throw new RuntimeException(ex);
			}
		} else {
			SharedRegionData region = this.regionCache.getOrCreateRegion(storage.getRegion());
			region.setSharedStorageNBT(storage.getID(), nbt);
		}
	}

	/**
	 * Deletes the specified shared storage file
	 * @param storage
	 */
	public void deleteSharedStorageFile(SharedStorage storage) {
		if(!storage.hasRegion()) {
			File file = new File(this.getSharedStorageDirectory(), storage.getID() + ".dat");
			if(file.exists()) {
				file.delete();
			}
		} else {
			SharedRegionData regionData = this.regionCache.getOrCreateRegion(storage.getRegion());
			if(regionData != null) {
				regionData.deleteSharedStorage(this.regionCache.getDir(), storage.getID());
			}
		}
	}

	/**
	 * Returns a list of shared storages at the specified position
	 * @param storageClass
	 * @param selector
	 * @param x
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
				SharedStorage storage = this.getSharedStorage(ref.getID());
				if(storage != null && storageClass.isAssignableFrom(storage.getClass()) && (selector == null || selector.test((F) storage))) {
					storages.add((F) storage);
				}
			}
		}
		return storages;
	}

	/**
	 * Returns a list of shared storages at the specified AABB
	 * @param storageClass
	 * @param selector
	 * @param aabb
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <F extends SharedStorage> List<F> getSharedStorageAt(Class<F> storageClass, @Nullable Predicate<F> selector, AxisAlignedBB aabb) {
		List<F> storages = new ArrayList<>();
		int sx = MathHelper.floor_double(aabb.minX) / 16;
		int sz = MathHelper.floor_double(aabb.minZ) / 16;
		int ex = MathHelper.floor_double(aabb.maxX) / 16;
		int ez = MathHelper.floor_double(aabb.maxZ) / 16;
		for(int cx = sx; cx <= ex; cx++) {
			for(int cz = sz; cz <= ez; cz++) {
				Chunk chunk = this.world.getChunkFromChunkCoords(cx, cz);
				ChunkDataBase chunkStorage = ChunkDataBase.forChunk(this, chunk);
				if(chunkStorage != null) {
					for(SharedStorageReference ref : chunkStorage.getSharedStorageReferences()) {
						SharedStorage storage = this.getSharedStorage(ref.getID());
						if(storage != null && storageClass.isAssignableFrom(storage.getClass()) && (selector == null || selector.test((F) storage))) {
							storages.add((F) storage);
						}
					}
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

	/**
	 * Returns a list of shared storages at the specified AABB
	 * @param storageClass
	 * @param aabb
	 * @return
	 */
	public <F extends SharedStorage> List<F> getSharedStorageAt(Class<F> storageClass, AxisAlignedBB aabb) {
		return this.getSharedStorageAt(storageClass, null, aabb);
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
			synchronized(CACHE) {
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
		}

		@SubscribeEvent
		public void onWorldSave(WorldEvent.Save event) {
			synchronized(CACHE) {
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

					worldStorage.regionCache.saveAllRegions();
				}
			}
		}

		@SubscribeEvent
		public void onWorldTick(WorldTickEvent event) {
			if(event.phase == Phase.END && !event.world.isRemote) {
				this.tickWorld(event.world);
			}
		}

		@SideOnly(Side.CLIENT)
		@SubscribeEvent
		public void onClientTick(ClientTickEvent event) {
			if(event.phase == Phase.END) {
				World world = Minecraft.getMinecraft().theWorld;
				if(world != null && !Minecraft.getMinecraft().isGamePaused()) {
					this.tickWorld(world);
				}
			}
		}

		private void tickWorld(World world) {
			synchronized(CACHE) {
				for(WorldDataBase<?> worldStorage : CACHE.values()) {
					if(worldStorage.getWorld() == world) {
						for(int i = 0; i < worldStorage.tickableSharedStorage.size(); i++) {
							SharedStorage sharedStorage = worldStorage.tickableSharedStorage.get(i);
							((ITickable)sharedStorage).update();
						}
					}
				}
			}
		}
	}
}
