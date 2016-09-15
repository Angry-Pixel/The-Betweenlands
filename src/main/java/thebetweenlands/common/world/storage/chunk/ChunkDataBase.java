package thebetweenlands.common.world.storage.chunk;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.event.world.ChunkDataEvent;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.event.world.ChunkWatchEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.ServerTickEvent;
import thebetweenlands.common.lib.ModInfo;

public abstract class ChunkDataBase {
	private static final class ChunkIdentifier {
		private final World world;
		private final ChunkPos chunk;
		private final int hash;

		private ChunkIdentifier(World world, ChunkPos chunk) {
			this.world = world;
			this.chunk = chunk;
			int worldHash = world.hashCode();
			int chunkHash = chunk.hashCode();
			final int prime = 31;
			int hash = 1;
			hash = prime * hash + chunkHash;
			hash = prime * hash + worldHash;
			this.hash = hash;
		}

		@Override
		public int hashCode() {
			return this.hash;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			ChunkIdentifier other = (ChunkIdentifier) obj;
			if (this.chunk == null) {
				if (other.chunk != null)
					return false;
			} else if (!this.chunk.equals(other.chunk))
				return false;
			if (this.hash != other.hash)
				return false;
			if (this.world == null) {
				if (other.world != null)
					return false;
			} else if (!this.world.equals(other.world))
				return false;
			return true;
		}
	}

	private static final Map<ChunkIdentifier, ChunkDataContainer> CHUNK_CONTAINER_CACHE = new HashMap<>();
	private static final Map<ChunkIdentifier, List<EntityPlayerMP>> CHUNK_WATCHERS = new HashMap<>();
	private static final Deque<ChunkIdentifier> UNLOAD_QUEUE = new ArrayDeque<>();

	private Chunk chunk;
	private World world;

	@Nullable
	public static <T extends ChunkDataBase> T forChunk(World world, Chunk chunk, Class<T> clazz) {
		ChunkDataContainer container = CHUNK_CONTAINER_CACHE.get(new ChunkIdentifier(world, chunk.getChunkCoordIntPair()));

		if(container != null) {
			//Get cached handler instance
			T cached = container.getCachedHandler(clazz);

			if(cached != null)
				return cached;

			NBTTagCompound nbt = container.getNBT();

			//No cached instance, create new handler
			T newInstance = getHandlerInstance(world, chunk, clazz, (handlerName) -> {
				if(!nbt.hasKey(handlerName)) {
					return null;
				} else {
					NBTTagCompound handlerNBT = nbt.getCompoundTag(handlerName);
					//Data is either null or not matching
					if(handlerNBT instanceof NBTTagCompound == false)
						handlerNBT = new NBTTagCompound();
					return handlerNBT;
				}
			});

			container.addHandler(newInstance);

			return newInstance;
		}

		return null;
	}

	/**
	 * Returns a new handler instance of the specified handler type
	 * @param world
	 * @param chunk
	 * @param handlerClass
	 * @return
	 */
	private static <T extends ChunkDataBase> T getHandlerInstance(World world, Chunk chunk, Class<T> handlerClass, Function<String, NBTTagCompound> nbtProvider) {
		T newInstance = null;
		try {
			newInstance = handlerClass.getConstructor().newInstance();
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
		((ChunkDataBase)newInstance).chunk = chunk;
		((ChunkDataBase)newInstance).world = world;

		newInstance.init();

		NBTTagCompound nbt = nbtProvider.apply(newInstance.getName());

		//Load data from NBT
		if(nbt != null) {
			//Load from nbt
			newInstance.readFromNBT(nbt);
		} else {
			//Set defaults
			newInstance.setDefaults();
		}

		//Update watchers
		List<EntityPlayerMP> watchers = CHUNK_WATCHERS.get(new ChunkIdentifier(world, chunk.getChunkCoordIntPair()));
		if(watchers != null)
			for(EntityPlayerMP watcher : watchers)
				newInstance.onWatched(watcher);

		return newInstance;
	}

	/**
	 * Adds or overrides a chunk container
	 * @param chunk
	 * @param nbt
	 */
	public static void updateContainerData(World world, Chunk chunk, NBTTagCompound nbt) {
		if(chunk.isLoaded()) {
			CHUNK_CONTAINER_CACHE.put(new ChunkIdentifier(world, chunk.getChunkCoordIntPair()), new ChunkDataContainer(nbt));
		}
	}

	/**
	 * Adds or overrides the data of a chunk data handler
	 * @param chunk
	 * @param handlerClass
	 * @param nbt
	 */
	public static <T extends ChunkDataBase> void updateHandlerData(World world, Chunk chunk, Class<T> handlerClass, NBTTagCompound nbt) {
		if(chunk.isLoaded()) {
			ChunkDataContainer container = CHUNK_CONTAINER_CACHE.get(new ChunkIdentifier(world, chunk.getChunkCoordIntPair()));
			if(container != null) {
				ChunkDataBase handler = container.getCachedHandler(handlerClass);
				if(handler == null) {
					T newInstance = getHandlerInstance(world, chunk, handlerClass, (handlerName) -> nbt);
					container.addHandler(newInstance);
				} else {
					handler.readFromNBT(nbt);
				}
			}
		}
	}

	/**
	 * Returns the chunk
	 * @return
	 */
	public final Chunk getChunk() {
		return this.chunk;
	}

	/**
	 * Returns the world
	 * @return
	 */
	public final World getWorld() {
		return this.world;
	}

	/**
	 * Returns the name of this handler
	 * @return
	 */
	public abstract String getName();

	/**
	 * Called when the handler is initialized
	 */
	protected void init() {

	}

	/**
	 * Reads the data from the NBT
	 */
	protected void readFromNBT(NBTTagCompound nbt) {

	}

	/**
	 * Writes the data to the NBT
	 */
	protected NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		return nbt;
	}

	/**
	 * Called when the handler is added to a chunk for the first time
	 */
	protected void setDefaults() {

	}

	/**
	 * Called when the chunk is unloaded
	 */
	protected void onUnloaded() {

	}

	/**
	 * Called when a player starts watching a chunk
	 * @param player
	 */
	protected void onWatched(EntityPlayerMP player) {

	}

	/**
	 * Called when a player stops watching a chunk
	 * @param player
	 */
	protected void onUnwatched(EntityPlayerMP player) {

	}

	/**
	 * Marks the chunk as dirty
	 */
	public void markDirty() {
		this.chunk.setChunkModified();
	}

	public static final class ChunkEventHandler {
		private ChunkEventHandler() { }

		@SubscribeEvent
		public static void onChunkDataEvent(ChunkDataEvent event) {
			ChunkIdentifier id = new ChunkIdentifier(event.getWorld(), event.getChunk().getChunkCoordIntPair());
			NBTTagCompound chunkNBT = event.getData();

			if(event instanceof ChunkDataEvent.Save) {
				ChunkDataContainer container = CHUNK_CONTAINER_CACHE.get(id);

				//Save container to chunk NBT
				if(container != null) {
					container.saveHandlers();
					chunkNBT.setTag(ModInfo.ID + ".ExtendedChunkData", container.getNBT());
				}
			} else if(event instanceof ChunkDataEvent.Load) {
				NBTBase extendedChunkData = chunkNBT.getCompoundTag(ModInfo.ID + ".ExtendedChunkData");

				//Data is either null or not matching
				if(extendedChunkData instanceof NBTTagCompound == false)
					extendedChunkData = new NBTTagCompound();

				CHUNK_CONTAINER_CACHE.put(id, new ChunkDataContainer((NBTTagCompound)extendedChunkData));
			}
		}

		@SubscribeEvent(priority = EventPriority.HIGHEST)
		public static void onChunkLoad(ChunkEvent.Load event) {
			ChunkIdentifier id = new ChunkIdentifier(event.getWorld(), event.getChunk().getChunkCoordIntPair());
			if(!CHUNK_CONTAINER_CACHE.containsKey(id))
				CHUNK_CONTAINER_CACHE.put(id, new ChunkDataContainer(new NBTTagCompound()));
		}

		@SubscribeEvent
		public static void onChunkUnload(ChunkEvent.Unload event) {
			ChunkIdentifier id = new ChunkIdentifier(event.getWorld(), event.getChunk().getChunkCoordIntPair());

			CHUNK_WATCHERS.remove(id);

			//Chunks aren't saved client side, remove immediately
			if(event.getWorld().isRemote && !event.getChunk().isLoaded()) {
				CHUNK_CONTAINER_CACHE.remove(id);
				return;
			}

			//Queue chunk for unloading the next tick
			ChunkDataContainer container = CHUNK_CONTAINER_CACHE.get(id);
			if(container != null) {
				UNLOAD_QUEUE.push(id);
			}
		}

		@SubscribeEvent
		public static void onServerTick(ServerTickEvent event) {
			if(event.phase == Phase.END) {
				//Remove queued chunks
				ChunkIdentifier queuedChunk;
				while((queuedChunk = UNLOAD_QUEUE.poll()) != null) {
					ChunkDataContainer container = CHUNK_CONTAINER_CACHE.remove(queuedChunk);
					if(container != null) {
						List<ChunkDataBase> handlers = container.getHandlers();
						for(ChunkDataBase handler : handlers)
							handler.onUnloaded();
					}
				}
			}
		}

		@SubscribeEvent
		public static void onChunkWatchEvent(ChunkWatchEvent event) {
			ChunkIdentifier id = new ChunkIdentifier(event.getPlayer().getEntityWorld(), event.getChunk());
			ChunkDataContainer container = CHUNK_CONTAINER_CACHE.get(id);

			if(container != null) {
				List<EntityPlayerMP> watchers = CHUNK_WATCHERS.get(id);
				if(event instanceof ChunkWatchEvent.Watch) {
					if(watchers == null) {
						CHUNK_WATCHERS.put(id, watchers = new ArrayList<>());
					}
					watchers.add(event.getPlayer());
					List<ChunkDataBase> handlers = container.getHandlers();
					for(ChunkDataBase handler : handlers)
						handler.onWatched(event.getPlayer());
				} else if(event instanceof ChunkWatchEvent.UnWatch) {
					if(watchers != null)
						watchers.remove(event.getPlayer());
					List<ChunkDataBase> handlers = container.getHandlers();
					for(ChunkDataBase handler : handlers)
						handler.onUnwatched(event.getPlayer());
				}
			}
		}
	}
}
