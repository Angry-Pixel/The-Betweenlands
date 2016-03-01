package thebetweenlands.world.storage.chunk;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.event.world.ChunkDataEvent;
import net.minecraftforge.event.world.ChunkEvent;

public abstract class ChunkDataBase {
	public final String name;

	private NBTTagCompound data = new NBTTagCompound();
	private Chunk chunk;
	private World world;

	public ChunkDataBase(String name) {
		this.name = name;
	}

	/**
	 * Thread safe read from the NBT.
	 * Read-only
	 * @param compound
	 */
	public final NBTTagCompound readData() {
		synchronized(this) {
			return (NBTTagCompound) this.data.copy();
		}
	}

	/**
	 * Thread safe write to the NBT.
	 * @param compound
	 */
	public final void writeData(NBTTagCompound compound) {
		synchronized(this) {
			this.data = (NBTTagCompound) compound.copy();
		}
	}

	private final void writeToNBTInternal(NBTTagCompound compound) {
		synchronized(this) {
			compound.setTag(this.name, this.data);
		}
	}

	private final void readFromNBTInternal(NBTTagCompound compound) {
		synchronized(this) {
			this.data = compound.getCompoundTag(this.name);
		}
	}

	public Chunk getChunk() {
		return this.chunk;
	}

	public World getWorld() {
		return this.world;
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
	 * Marks the chunk as dirty.
	 * Syncs the data with the client.
	 * Don't forget this if you want the data to be saved!
	 */
	public void markDirty() {
		this.chunk.setChunkModified();
	}

	/**
	 * Called after the data has been loaded or initilized
	 */
	protected void postInit() { }

	/**
	 * Called when this data is created for the first time for this world
	 */
	protected abstract void setDefaults();

	/**
	 * Called when the data is unloaded
	 */
	protected void onUnload() { }

	protected static final Map<ChunkDataTypePair, ChunkDataBase> CACHE = new HashMap<ChunkDataTypePair, ChunkDataBase>();
	protected static final Map<ChunkCoordIntPair, NBTTagCompound> CHUNK_NBT_CACHE = new HashMap<ChunkCoordIntPair, NBTTagCompound>();
	private static final List<Chunk> CHUNK_UNLOAD_QUEUE = new ArrayList<Chunk>();

	protected static class ChunkDataTypePair {
		protected ChunkCoordIntPair pos;
		protected World world;
		protected Class<? extends ChunkDataBase> data;
		private ChunkDataTypePair(ChunkCoordIntPair pos, World world, Class<? extends ChunkDataBase> data) {
			this.pos = pos;
			this.world = world;
			this.data = data;
		}
	}

	private static ChunkDataBase getMatchingData(ChunkCoordIntPair pos, World world, Class<? extends ChunkDataBase> clazz) {
		synchronized(CHUNK_DATA_HANDLER) {
			for(Entry<ChunkDataTypePair, ChunkDataBase> cacheEntry : CACHE.entrySet()) {
				ChunkDataTypePair pair = cacheEntry.getKey();
				if(pair.pos.equals(pos) && pair.data.equals(clazz) && pair.world.equals(world)) {
					return cacheEntry.getValue();
				}
			}
			return null;
		}
	}

	public static <T extends ChunkDataBase> T forChunk(World world, Chunk chunk, Class<T> clazz) {
		synchronized(CHUNK_DATA_HANDLER) {
			ChunkCoordIntPair chunkPos = new ChunkCoordIntPair(chunk.xPosition, chunk.zPosition);

			ChunkDataBase cached = getMatchingData(chunkPos, world, clazz);
			if(cached != null) {
				//Already cached
				cached.chunk = chunk;
				cached.world = world;
				return (T) cached;
			}

			T newInstance = null;
			try {
				newInstance = clazz.getConstructor().newInstance();
			} catch(Exception ex) {
				throw new RuntimeException(ex);
			}

			NBTTagCompound nbt = CHUNK_NBT_CACHE.get(chunkPos);

			if(nbt == null)
				nbt = new NBTTagCompound();

			((ChunkDataBase)newInstance).chunk = chunk;
			((ChunkDataBase)newInstance).world = world;

			if(!nbt.hasKey(newInstance.name)) {
				//Creating for the first time
				newInstance.init();
				newInstance.setDefaults();
				chunk.setChunkModified();
				newInstance.postInit();
			} else {
				//Loading from chunk NBT
				newInstance.init();
				((ChunkDataBase)newInstance).readFromNBTInternal(nbt);
				newInstance.load();
				newInstance.postInit();
			}

			CACHE.put(new ChunkDataTypePair(chunkPos, world, clazz), newInstance);

			return (T) newInstance;
		}
	}

	public static final ChunkDataHandler CHUNK_DATA_HANDLER = new ChunkDataHandler();

	public static class ChunkDataHandler {
		private ChunkDataHandler(){}

		@SubscribeEvent
		public void onChunkDataEvent(ChunkDataEvent event) {
			synchronized(CHUNK_DATA_HANDLER) {
				ChunkCoordIntPair chunkPos = new ChunkCoordIntPair(event.getChunk().xPosition, event.getChunk().zPosition);
				NBTTagCompound chunkNBT = event.getData();
				if(event instanceof ChunkDataEvent.Save) {
					//Write previous extended chunk data
					if(CHUNK_NBT_CACHE.containsKey(chunkPos)) {
						NBTTagCompound prevNBT = CHUNK_NBT_CACHE.get(chunkPos);
						chunkNBT.setTag("extendedChunkData", prevNBT);
					}

					//Write new extended chunk data
					for(ChunkDataTypePair pair : CACHE.keySet()) {
						if(pair.pos.equals(chunkPos)) {
							NBTTagCompound chunkData = new NBTTagCompound();
							ChunkDataBase data = CACHE.get(pair);
							synchronized(data) {
								data.writeToNBTInternal(chunkData);
							}
							chunkNBT.setTag("extendedChunkData", chunkData);
						}
					}

					//Remove unloaded chunks from the cache
					Iterator<Chunk> unloadIT = CHUNK_UNLOAD_QUEUE.iterator();
					while(unloadIT.hasNext()) {
						Chunk unloadedChunk = unloadIT.next();
						if(unloadedChunk == event.getChunk()) {
							for(ChunkDataTypePair cpair : this.getDataCacheKeys(chunkPos, event.world)) {
								if(cpair != null) {
									ChunkDataBase data = CACHE.get(cpair);
									if(data != null)
										data.onUnload();
									CACHE.remove(cpair);
								}
							}
							unloadIT.remove();
						}
					}
					/*if(!event.getChunk().isChunkLoaded) {
						for(ChunkDataTypePair cpair : this.getDataCacheKeys(chunkPos, event.world)) {
							ChunkDataBase data = CACHE.get(cpair);
							if(data != null)
								data.onUnload();
							CACHE.remove(cpair);
						}
					}*/

					CHUNK_NBT_CACHE.remove(chunkPos);
				} else if(event instanceof ChunkDataEvent.Load) {
					synchronized(CHUNK_DATA_HANDLER) {
						if(chunkNBT.hasKey("extendedChunkData") && chunkNBT.getTag("extendedChunkData") instanceof NBTTagCompound) {
							//Cache extended chunk data
							CHUNK_NBT_CACHE.put(chunkPos, chunkNBT.getCompoundTag("extendedChunkData"));
						}
					}
				}
			}
		}

		@SubscribeEvent
		public void onChunkUnload(ChunkEvent.Unload event) {
			synchronized(CHUNK_DATA_HANDLER) {
				ChunkCoordIntPair chunkPos = new ChunkCoordIntPair(event.getChunk().xPosition, event.getChunk().zPosition);
				for(ChunkDataTypePair pair : this.getDataCacheKeys(chunkPos, event.world)) {
					//Add unloaded chunks to the unload queue
					if(pair != null) {
						if(CACHE.containsKey(pair) || CHUNK_NBT_CACHE.containsKey(chunkPos)) {
							CHUNK_UNLOAD_QUEUE.add(event.getChunk());
							//Force chunks with extended data to save
							event.getChunk().setChunkModified();
						}
					}
				}
			}
		}

		/*@SubscribeEvent
		public void onWorldUnload(WorldEvent.Unload event) {
			synchronized(CHUNK_DATA_HANDLER) {
				//TODO: Test
				CHUNK_UNLOAD_QUEUE.clear();
				CHUNK_NBT_CACHE.clear();
				CACHE.clear();
			}
		}*/

		private List<ChunkDataTypePair> getDataCacheKeys(ChunkCoordIntPair chunkPos, World world) {
			synchronized(CHUNK_DATA_HANDLER) {
				List<ChunkDataTypePair> caches = new ArrayList<ChunkDataTypePair>();
				Iterator<Entry<ChunkDataTypePair, ChunkDataBase>> pairIT = CACHE.entrySet().iterator();
				while(pairIT.hasNext()) {
					Entry<ChunkDataTypePair, ChunkDataBase> entry = pairIT.next();
					if(entry.getKey().pos.equals(chunkPos) && entry.getKey().world.equals(world)) {
						caches.add(entry.getKey());
					}
				}
				return caches;
			}
		}
	}
}
