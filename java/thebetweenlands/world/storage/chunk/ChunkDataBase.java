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
import net.minecraftforge.event.world.WorldEvent;

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
		this.save();
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

	private static final Map<ChunkDataTypePair, ChunkDataBase> CACHE = new HashMap<ChunkDataTypePair, ChunkDataBase>();
	private static final Map<ChunkPosWorldPair, NBTTagCompound> CHUNK_NBT_CACHE = new HashMap<ChunkPosWorldPair, NBTTagCompound>();
	private static final List<Chunk> CHUNK_UNLOAD_QUEUE = new ArrayList<Chunk>();

	private static class ChunkDataTypePair {
		protected final ChunkCoordIntPair pos;
		protected final World world;
		protected final Class<? extends ChunkDataBase> type;
		private ChunkDataTypePair(ChunkCoordIntPair pos, World world, Class<? extends ChunkDataBase> data) {
			this.pos = pos;
			this.world = world;
			this.type = data;
		}
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((pos == null) ? 0 : pos.hashCode());
			result = prime * result + ((type == null) ? 0 : type.hashCode());
			result = prime * result + ((world == null) ? 0 : world.hashCode());
			return result;
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			ChunkDataTypePair other = (ChunkDataTypePair) obj;
			if (pos == null) {
				if (other.pos != null)
					return false;
			} else if (!pos.equals(other.pos))
				return false;
			if (type == null) {
				if (other.type != null)
					return false;
			} else if (!type.equals(other.type))
				return false;
			if (world == null) {
				if (other.world != null)
					return false;
			} else if (!world.equals(other.world))
				return false;
			return true;
		}
	}

	private static class ChunkPosWorldPair {
		protected final ChunkCoordIntPair pos;
		protected final World world;
		private ChunkPosWorldPair(ChunkCoordIntPair pos, World world) {
			this.pos = pos;
			this.world = world;
		}
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((pos == null) ? 0 : pos.hashCode());
			result = prime * result + ((world == null) ? 0 : world.hashCode());
			return result;
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			ChunkPosWorldPair other = (ChunkPosWorldPair) obj;
			if (pos == null) {
				if (other.pos != null)
					return false;
			} else if (!pos.equals(other.pos))
				return false;
			if (world == null) {
				if (other.world != null)
					return false;
			} else if (!world.equals(other.world))
				return false;
			return true;
		}
	}

	protected static void addDataCache(ChunkCoordIntPair pos, World world, Class<? extends ChunkDataBase> clazz, ChunkDataBase data) {
		synchronized(CHUNK_DATA_HANDLER) {
			CACHE.put(new ChunkDataTypePair(pos, world, clazz), data);
		}
	}

	protected static ChunkDataBase getDataCache(ChunkCoordIntPair pos, World world, Class<? extends ChunkDataBase> clazz) {
		synchronized(CHUNK_DATA_HANDLER) {
			for(Entry<ChunkDataTypePair, ChunkDataBase> cacheEntry : CACHE.entrySet()) {
				ChunkDataTypePair pair = cacheEntry.getKey();
				if(pair.pos.equals(pos) && pair.type.equals(clazz) && pair.world.equals(world)) {
					return cacheEntry.getValue();
				}
			}
			return null;
		}
	}

	protected static void removeDataCache(ChunkCoordIntPair pos, World world, Class<? extends ChunkDataBase> clazz) {
		synchronized(CHUNK_DATA_HANDLER) {
			Iterator<Entry<ChunkDataTypePair, ChunkDataBase>> dataCacheIT = CACHE.entrySet().iterator();
			while(dataCacheIT.hasNext()) {
				ChunkDataTypePair pair = dataCacheIT.next().getKey();
				if(pair.pos.equals(pos) && pair.world.equals(world) && pair.type.equals(clazz))
					dataCacheIT.remove();
			}
		}
	}

	protected static void addNBTCache(ChunkCoordIntPair pos, World world, NBTTagCompound nbt) {
		synchronized(CHUNK_DATA_HANDLER) {
			CHUNK_NBT_CACHE.put(new ChunkPosWorldPair(pos, world), nbt);
		}
	}

	protected static NBTTagCompound getNBTCache(ChunkCoordIntPair pos, World world) {
		synchronized(CHUNK_DATA_HANDLER) {
			for(Entry<ChunkPosWorldPair, NBTTagCompound> cacheEntry : CHUNK_NBT_CACHE.entrySet()) {
				ChunkPosWorldPair pair = cacheEntry.getKey();
				if(pair.pos.equals(pos) && pair.world.equals(world)) {
					return cacheEntry.getValue();
				}
			}
			return null;
		}
	}

	protected static void removeNBTCache(ChunkCoordIntPair pos, World world) {
		synchronized(CHUNK_DATA_HANDLER) {
			Iterator<Entry<ChunkPosWorldPair, NBTTagCompound>> nbtCacheIT = CHUNK_NBT_CACHE.entrySet().iterator();
			while(nbtCacheIT.hasNext()) {
				ChunkPosWorldPair chunkPosWorldPair = nbtCacheIT.next().getKey();
				if(chunkPosWorldPair.pos.equals(pos) && chunkPosWorldPair.world.equals(world))
					nbtCacheIT.remove();
			}
		}
	}

	public static <T extends ChunkDataBase> T forChunk(World world, Chunk chunk, Class<T> clazz) {
		synchronized(CHUNK_DATA_HANDLER) {
			ChunkCoordIntPair chunkPos = new ChunkCoordIntPair(chunk.xPosition, chunk.zPosition);

			ChunkDataBase cached = getDataCache(chunkPos, world, clazz);
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

			NBTTagCompound nbt = getNBTCache(chunkPos, world);

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
					NBTTagCompound prevNBT = getNBTCache(chunkPos, event.world);
					if(prevNBT != null) {
						chunkNBT.setTag("extendedChunkData", prevNBT);
					}

					//Write new extended chunk data
					for(ChunkDataTypePair pair : CACHE.keySet()) {
						if(pair.pos.equals(chunkPos)) {
							NBTTagCompound chunkData = new NBTTagCompound();
							ChunkDataBase data = CACHE.get(pair);
							data.writeToNBTInternal(chunkData);
							chunkNBT.setTag("extendedChunkData", chunkData);
						}
					}

					boolean removeNBTCache = false;

					//Remove unloaded chunks from the cache
					Iterator<Chunk> unloadIT = CHUNK_UNLOAD_QUEUE.iterator();
					while(unloadIT.hasNext()) {
						Chunk unloadedChunk = unloadIT.next();
						if(unloadedChunk == event.getChunk()) {
							for(ChunkDataTypePair cpair : this.getDataCacheKeys(chunkPos, event.world)) {
								if(cpair != null) {
									ChunkDataBase data = CACHE.get(cpair);
									if(data != null) {
										data.onUnload();
										CACHE.remove(cpair);
										//Data has been previously written from data cache, NBT cache not required anymore
										removeNBTCache = true;
									}
								}
							}
							unloadIT.remove();
						}
					}
					if(!event.getChunk().isChunkLoaded || event.world.isRemote) {
						for(ChunkDataTypePair cpair : this.getDataCacheKeys(chunkPos, event.world)) {
							ChunkDataBase data = CACHE.get(cpair);
							if(data != null) {
								data.onUnload();
								CACHE.remove(cpair);
								//Data has been previously written from data cache, NBT cache not required anymore
								removeNBTCache = true;
							}
						}
					}
					if(!event.getChunk().isChunkLoaded || removeNBTCache || event.world.isRemote) {
						removeNBTCache(chunkPos, event.world);
					}
				} else if(event instanceof ChunkDataEvent.Load) {
					if(chunkNBT.hasKey("extendedChunkData") && chunkNBT.getTag("extendedChunkData") instanceof NBTTagCompound) {
						//Cache extended chunk data
						CHUNK_NBT_CACHE.put(new ChunkPosWorldPair(chunkPos, event.world), chunkNBT.getCompoundTag("extendedChunkData"));
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
						if(CACHE.containsKey(pair) || getNBTCache(chunkPos, event.world) != null) {
							if(event.world.isRemote) {
								//Directly remove cache on client side
								for(ChunkDataTypePair cpair : this.getDataCacheKeys(chunkPos, event.world)) {
									CACHE.remove(cpair);
								}
							} else {
								//Add chunk to unloading queue
								CHUNK_UNLOAD_QUEUE.add(event.getChunk());
							}

							//System.out.println("Cache: " + CACHE.size());
							//System.out.println("NBT Cache: " + CHUNK_NBT_CACHE.size());
							//System.out.println("Unload queue: " + CHUNK_UNLOAD_QUEUE.size());
						}
					}
				}
				if(event.world.isRemote) {
					//Directly remove cache on client side
					removeNBTCache(chunkPos, event.world);
				}
			}
		}

		@SubscribeEvent
		public void onWorldUnload(WorldEvent.Unload event) {
			synchronized(CHUNK_DATA_HANDLER) {
				if(event.world.isRemote) {
					CHUNK_UNLOAD_QUEUE.clear();
					CHUNK_NBT_CACHE.clear();
					CACHE.clear();
				}
			}
		}

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
