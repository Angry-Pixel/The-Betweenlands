package thebetweenlands.common.world.storage.chunk;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityDispatcher;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.event.world.ChunkDataEvent;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.event.world.ChunkWatchEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.ServerTickEvent;
import thebetweenlands.common.event.AttachChunkCapabilitiesEvent;
import thebetweenlands.common.lib.ModInfo;
import thebetweenlands.common.world.storage.chunk.shared.SharedStorageReference;
import thebetweenlands.common.world.storage.world.global.WorldDataBase;
import thebetweenlands.common.world.storage.world.shared.SharedStorage;

/**
 * Chunk specific storage.
 * <p>Capabilities can be attached using the {@link AttachChunkCapabilitiesEvent} event.
 * <p><b>{@link ChunkDataBase#CHUNK_EVENT_HANDLER} must be registered to {@link MinecraftForge.EVENT_BUS}.</b>
 */
public abstract class ChunkDataBase implements ICapabilityProvider {
	public static final ChunkEventHandler CHUNK_EVENT_HANDLER = new ChunkEventHandler();

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
	private WorldDataBase<?> worldStorage;
	private final List<EntityPlayerMP> watchers = new ArrayList<EntityPlayerMP>();
	private CapabilityDispatcher capabilities;
	private final List<SharedStorageReference> sharedStorageReferences = new ArrayList<>();

	public ChunkDataBase() {
		//Gather capabilities
		AttachChunkCapabilitiesEvent event = new AttachChunkCapabilitiesEvent(this);
		MinecraftForge.EVENT_BUS.post(event);
		this.capabilities = event.getCapabilities().size() > 0 ? new CapabilityDispatcher(event.getCapabilities(), null) : null;
	}

	@Nullable
	public static <T extends ChunkDataBase> T forChunk(WorldDataBase<T> worldStorage, Chunk chunk) {
		ChunkDataContainer container = CHUNK_CONTAINER_CACHE.get(new ChunkIdentifier(worldStorage.getWorld(), chunk.getChunkCoordIntPair()));

		if(container != null) {
			//Get cached handler instance
			T cached = container.getCachedHandler(worldStorage.getChunkStorage());

			if(cached != null)
				return cached;

			NBTTagCompound nbt = container.getNBT();

			//No cached instance, create new handler
			T newInstance = getHandlerInstance(worldStorage, chunk, (handlerName) -> {
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
	 * @param worldStorage
	 * @param chunk
	 * @param handlerClass
	 * @return
	 */
	private static <T extends ChunkDataBase> T getHandlerInstance(WorldDataBase<T> worldStorage, Chunk chunk, Function<String, NBTTagCompound> nbtProvider) {
		Class<T> handlerClass = (Class<T>) worldStorage.getChunkStorage();
		T newInstance = null;
		try {
			newInstance = handlerClass.getConstructor().newInstance();
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
		((ChunkDataBase)newInstance).chunk = chunk;
		((ChunkDataBase)newInstance).worldStorage = worldStorage;

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
		List<EntityPlayerMP> watchers = CHUNK_WATCHERS.get(new ChunkIdentifier(worldStorage.getWorld(), chunk.getChunkCoordIntPair()));
		if(watchers != null)
			for(EntityPlayerMP watcher : watchers)
				newInstance.onWatched(watcher);

		newInstance.onLoaded();

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
	public static <T extends ChunkDataBase> void updateHandlerData(WorldDataBase<T> world, Chunk chunk, NBTTagCompound nbt) {
		if(chunk.isLoaded()) {
			ChunkDataContainer container = CHUNK_CONTAINER_CACHE.get(new ChunkIdentifier(world.getWorld(), chunk.getChunkCoordIntPair()));
			if(container != null) {
				Class<T> handlerClass = world.getChunkStorage();
				ChunkDataBase handler = container.getCachedHandler(handlerClass);
				if(handler == null) {
					T newInstance = getHandlerInstance(world, chunk, (handlerName) -> nbt);
					container.addHandler(newInstance);
				} else {
					handler.readFromNBT(nbt);
				}
			}
		}
	}

	/**
	 * Links this chunk with the specified shared storage
	 * @param storage
	 * @return
	 */
	public final boolean linkSharedStorage(SharedStorage storage) {
		String id = storage.getID();
		for(SharedStorageReference ref : this.sharedStorageReferences) {
			if(id.equals(ref.getID()))
				return false;
		}
		SharedStorageReference ref = new SharedStorageReference(this.chunk.getChunkCoordIntPair(), id, storage.getRegion());
		if(this.sharedStorageReferences.add(ref)) {
			this.markDirty();
			return true;
		}
		return false;
	}

	/**
	 * Unlinks this chunk from the specified shared storage
	 * @param storage
	 * @return
	 */
	public final boolean unlinkSharedStorage(SharedStorage storage) {
		String id = storage.getID();
		List<SharedStorageReference> unlinkedReferences = new ArrayList<>();
		Iterator<SharedStorageReference> referenceIT = this.sharedStorageReferences.iterator();
		while(referenceIT.hasNext()) {
			SharedStorageReference ref = referenceIT.next();
			if(id.equals(ref.getID())) {
				unlinkedReferences.add(ref);
				referenceIT.remove();
			}
		}
		if(!unlinkedReferences.isEmpty()) {
			//Make sure the shared storage is also unlinked from this chunk
			for(SharedStorageReference ref : unlinkedReferences) {
				if(storage.getReferences().contains(ref)) {
					storage.unlinkChunk(this.chunk);
				}
			}
			this.markDirty();
			return true;
		}
		return false;
	}

	/**
	 * Returns all shared storage references
	 * @return
	 */
	public final List<SharedStorageReference> getSharedStorageReferences() {
		return Collections.unmodifiableList(this.sharedStorageReferences);
	}

	/**
	 * Returns the reference with the specified ID
	 * @param id
	 * @return
	 */
	@Nullable
	public final SharedStorageReference getReference(String id) {
		for(SharedStorageReference ref : this.sharedStorageReferences) {
			if(id.equals(ref.getID()))
				return ref;
		}
		return null;
	}

	/**
	 * Returns the chunk
	 * @return
	 */
	public final Chunk getChunk() {
		return this.chunk;
	}

	/**
	 * Returns the world storage
	 * @return
	 */
	public final WorldDataBase<?> getWorldStorage() {
		return this.worldStorage;
	}

	/**
	 * Returns the name of this handler
	 * @return
	 */
	public abstract String getName();

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		return this.capabilities == null ? false : this.capabilities.hasCapability(capability, facing);
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		return this.capabilities == null ? null : this.capabilities.getCapability(capability, facing);
	}

	/**
	 * Called when the handler is initialized
	 */
	protected void init() {

	}

	/**
	 * Reads the data from the NBT
	 */
	protected void readFromNBT(NBTTagCompound nbt) {
		if (this.capabilities != null && nbt.hasKey("ForgeCaps")) this.capabilities.deserializeNBT(nbt.getCompoundTag("ForgeCaps"));

		this.sharedStorageReferences.clear();
		NBTTagList sharedReferenceList = nbt.getTagList("SharedStorageReferences", Constants.NBT.TAG_COMPOUND);
		for(int i = 0; i < sharedReferenceList.tagCount(); i++) {
			this.sharedStorageReferences.add(SharedStorageReference.readFromNBT((NBTTagCompound)sharedReferenceList.get(i)));
		}

		Iterator<SharedStorageReference> refIT = this.sharedStorageReferences.iterator();
		while(refIT.hasNext()) {
			SharedStorageReference ref = refIT.next();
			SharedStorage sharedStorage = this.getWorldStorage().getSharedStorage(ref.getID());

			//Not cached, load from file
			if(!this.worldStorage.getWorld().isRemote && sharedStorage == null) {
				sharedStorage = this.getWorldStorage().loadSharedStorage(ref);
			}

			//Load reference if properly linked
			if(sharedStorage != null && sharedStorage.getLinkedChunks().contains(this.chunk.getChunkCoordIntPair())) {
				sharedStorage.loadReference(ref);
			} else if(!this.worldStorage.getWorld().isRemote) {
				//Shared storage doesn't exist or chunk shouldn't be linked to shared storage, remove link
				refIT.remove();
			}
		}
	}

	/**
	 * Writes the data to the NBT
	 */
	protected NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		if (this.capabilities != null) {
			NBTTagCompound caps = this.capabilities.serializeNBT();
			if(caps.getSize() > 0) {
				nbt.setTag("ForgeCaps", caps);
			}
		}
		if(this.sharedStorageReferences.size() > 0) {
			NBTTagList sharedReferenceList = new NBTTagList();
			for(SharedStorageReference ref : this.sharedStorageReferences) {
				sharedReferenceList.appendTag(ref.writeToNBT(new NBTTagCompound()));
			}
			nbt.setTag("SharedStorageReferences", sharedReferenceList);
		}
		return nbt;
	}

	/**
	 * Called when the handler is added to a chunk for the first time
	 */
	protected void setDefaults() {

	}

	/**
	 * Called after the chunk data has loaded
	 */
	protected void onLoaded() {

	}

	/**
	 * Called when the chunk is unloaded
	 */
	protected void onUnloaded() {
		for(SharedStorageReference ref : this.sharedStorageReferences) {
			SharedStorage sharedStorage = this.getWorldStorage().getSharedStorage(ref.getID());
			if(sharedStorage != null) {
				sharedStorage.unloadReference(ref);
				if(sharedStorage.getReferences().isEmpty()) {
					this.getWorldStorage().unloadSharedStorage(sharedStorage);
				}
			}
		}
	}

	/**
	 * Called when a player starts watching a chunk
	 * @param player
	 */
	protected void onWatched(EntityPlayerMP player) {
		this.watchers.add(player);
		for(SharedStorageReference ref : this.sharedStorageReferences) {
			SharedStorage sharedStorage = this.getWorldStorage().getSharedStorage(ref.getID());
			if(sharedStorage != null && !sharedStorage.getWatchers().contains(player)) {
				sharedStorage.onWatched(this, player);
			}
		}
	}

	/**
	 * Called when a player stops watching a chunk
	 * @param player
	 */
	protected void onUnwatched(EntityPlayerMP player) {
		this.watchers.remove(player);
		for(SharedStorageReference ref : this.sharedStorageReferences) {
			SharedStorage sharedStorage = this.getWorldStorage().getSharedStorage(ref.getID());
			if(sharedStorage != null && sharedStorage.getWatchers().contains(player)) {
				sharedStorage.onUnwatched(this, player);
			}
		}
	}

	/**
	 * Returns the list of all watchers
	 * @return
	 */
	public List<EntityPlayerMP> getWatchers() {
		return this.watchers;
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
		public void onChunkDataEvent(ChunkDataEvent event) {
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
		public void onChunkLoad(ChunkEvent.Load event) {
			ChunkIdentifier id = new ChunkIdentifier(event.getWorld(), event.getChunk().getChunkCoordIntPair());
			if(!CHUNK_CONTAINER_CACHE.containsKey(id))
				CHUNK_CONTAINER_CACHE.put(id, new ChunkDataContainer(new NBTTagCompound()));
		}

		@SubscribeEvent
		public void onChunkUnload(ChunkEvent.Unload event) {
			ChunkIdentifier id = new ChunkIdentifier(event.getWorld(), event.getChunk().getChunkCoordIntPair());

			CHUNK_WATCHERS.remove(id);

			//Chunks aren't saved client side, remove immediately
			if(event.getWorld().isRemote && !event.getChunk().isLoaded()) {
				ChunkDataContainer container = CHUNK_CONTAINER_CACHE.remove(id);
				if(container != null) {
					for(ChunkDataBase chunkStorage : container.getHandlers()) {
						chunkStorage.onUnloaded(); //Unload immediately on client side
					}
				}
				return;
			}

			//Queue chunk for unloading the next tick
			ChunkDataContainer container = CHUNK_CONTAINER_CACHE.get(id);
			if(container != null) {
				UNLOAD_QUEUE.push(id);
			}
		}

		@SubscribeEvent
		public void onServerTick(ServerTickEvent event) {
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
		public void onChunkWatchEvent(ChunkWatchEvent event) {
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
