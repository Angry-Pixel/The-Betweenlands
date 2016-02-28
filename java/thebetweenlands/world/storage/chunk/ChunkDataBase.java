package thebetweenlands.world.storage.chunk;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.event.world.ChunkDataEvent;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.event.world.ChunkWatchEvent;
import thebetweenlands.TheBetweenlands;
import thebetweenlands.network.message.base.AbstractMessage;

public abstract class ChunkDataBase {
	public final String name;

	private NBTTagCompound data = new NBTTagCompound();
	private Chunk chunk;

	public ChunkDataBase(String name) {
		this.name = name;
	}

	public final void readFromNBT(NBTTagCompound compound) {
		this.data = compound.getCompoundTag(this.name);
	}

	public final void writeToNBT(NBTTagCompound compound) {
		this.save();
		compound.setTag(this.name, this.data);
	}

	public Chunk getChunk() {
		return this.chunk;
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

	public NBTTagCompound getData() {
		return this.data;
	}

	protected static final Map<ChunkDataTypePair, ChunkDataBase> CACHE = new HashMap<ChunkDataTypePair, ChunkDataBase>();
	protected static final Map<ChunkCoordIntPair, NBTTagCompound> CHUNK_NBT_CACHE = new HashMap<ChunkCoordIntPair, NBTTagCompound>();
	private static final List<Chunk> CHUNK_UNLOAD_QUEUE = new ArrayList<Chunk>();

	protected static class ChunkDataTypePair {
		protected ChunkCoordIntPair pos;
		protected Class<? extends ChunkDataBase> data;
		private ChunkDataTypePair(ChunkCoordIntPair pos, Class<? extends ChunkDataBase> data) {
			this.pos = pos;
			this.data = data;
		}
	}

	private static ChunkDataBase getMatchingData(ChunkCoordIntPair pos, Class<? extends ChunkDataBase> clazz) {
		synchronized(CHUNK_DATA_HANDLER) {
			for(Entry<ChunkDataTypePair, ChunkDataBase> cacheEntry : CACHE.entrySet()) {
				ChunkDataTypePair pair = cacheEntry.getKey();
				if(pair.pos.equals(pos) && pair.data.equals(clazz)) {
					return cacheEntry.getValue();
				}
			}
			return null;
		}
	}

	public static <T extends ChunkDataBase> T forChunk(Chunk chunk, Class<T> clazz) {
		synchronized(CHUNK_DATA_HANDLER) {
			ChunkCoordIntPair chunkPos = new ChunkCoordIntPair(chunk.xPosition, chunk.zPosition);

			ChunkDataBase cached = getMatchingData(chunkPos, clazz);
			if(cached != null) {
				//Already cached
				cached.chunk = chunk;
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

			if(!nbt.hasKey(newInstance.name)) {
				//System.out.println("CREATE NEW");
				//Creating for the first time
				newInstance.init();
				newInstance.setDefaults();
				chunk.setChunkModified();
				newInstance.postInit();
			} else {
				//System.out.println("LOAD FROM NBT");
				//Loading from chunk NBT
				newInstance.init();
				newInstance.readFromNBT(nbt);
				newInstance.load();
				newInstance.postInit();
			}

			CACHE.put(new ChunkDataTypePair(chunkPos, clazz), newInstance);

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
							CACHE.get(pair).writeToNBT(chunkData);
							chunkNBT.setTag("extendedChunkData", chunkData);
							//System.out.println("SAVE FOR: " + CACHE.get(pair) + " " + pair.pos.chunkXPos + " " + pair.pos.chunkZPos);
						}
					}

					//Remove unloaded chunks from the cache

					Iterator<Chunk> unloadIT = CHUNK_UNLOAD_QUEUE.iterator();
					while(unloadIT.hasNext()) {
						Chunk unloadedChunk = unloadIT.next();
						if(unloadedChunk == event.getChunk()) {
							for(ChunkDataTypePair cpair : this.getDataCacheKeys(chunkPos)) {
								if(cpair != null) {
									CACHE.remove(cpair);
								}
							}
							unloadIT.remove();
						}
					}

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
				for(ChunkDataTypePair pair : this.getDataCacheKeys(chunkPos)) {
					//System.out.println("NBT Cache: " + CHUNK_NBT_CACHE.size());
					//System.out.println("DATA CACHE: " + CACHE.size());

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

		private List<ChunkDataTypePair> getDataCacheKeys(ChunkCoordIntPair chunkPos) {
			synchronized(CHUNK_DATA_HANDLER) {
				List<ChunkDataTypePair> caches = new ArrayList<ChunkDataTypePair>();
				Iterator<Entry<ChunkDataTypePair, ChunkDataBase>> pairIT = CACHE.entrySet().iterator();
				while(pairIT.hasNext()) {
					Entry<ChunkDataTypePair, ChunkDataBase> entry = pairIT.next();
					if(entry.getKey().pos.equals(chunkPos)) {
						caches.add(entry.getKey());
					}
				}
				return caches;
			}
		}
	}
}
