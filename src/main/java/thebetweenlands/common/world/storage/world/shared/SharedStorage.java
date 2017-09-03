package thebetweenlands.common.world.storage.world.shared;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityDispatcher;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.Constants;
import thebetweenlands.api.event.AttachSharedStorageCapabilitiesEvent;
import thebetweenlands.common.world.storage.chunk.ChunkDataBase;
import thebetweenlands.common.world.storage.chunk.shared.SharedStorageReference;
import thebetweenlands.common.world.storage.world.global.WorldDataBase;

/**
 * Shared storage can be linked to an arbitrary number of chunks. Each linked chunk
 * has access to the same shared storage it was linked to. A chunk can be linked to multiple shared
 * storages.
 * <p>Capabilities can be attached using the {@link AttachSharedStorageCapabilitiesEvent} event.
 * <p>Implement {@link net.minecraft.util.ITickable} if the shared storage has to be updated every tick.
 */
public abstract class SharedStorage implements ICapabilityProvider {
	private static final Map<ResourceLocation, Class<? extends SharedStorage>> STORAGE_MAP = new HashMap<ResourceLocation, Class<? extends SharedStorage>>();

	/**
	 * Returns the shared storage class for the specified ID
	 * @return
	 */
	public static Class<? extends SharedStorage> getStorageType(ResourceLocation id) {
		return STORAGE_MAP.get(id);
	}

	/**
	 * Returns the shared storage ID for the specified class
	 * @param storageClass
	 * @return
	 */
	public static ResourceLocation getStorageTypeID(Class<? extends SharedStorage> storageClass) {
		for(Entry<ResourceLocation, Class<? extends SharedStorage>> entry : STORAGE_MAP.entrySet()) {
			if(storageClass.equals(entry.getValue()))
				return entry.getKey();
		}
		return null;
	}

	/**
	 * Registers a shared storage type
	 * @param id
	 * @param storageClass
	 */
	public static void registerStorageType(ResourceLocation id, Class<? extends SharedStorage> storageClass) {
		if(STORAGE_MAP.containsKey(id))
			throw new RuntimeException("Duplicate shared storage ID");
		STORAGE_MAP.put(id, storageClass);
	}

	/**
	 * Loads a shared storage from NBT
	 * @param nbt
	 * @return
	 */
	public static SharedStorage load(WorldDataBase<?> worldStorage, NBTTagCompound nbt, @Nullable SharedRegion region, boolean packet) {
		try {
			ResourceLocation type = new ResourceLocation(nbt.getString("type"));
			Class<? extends SharedStorage> storageClass = SharedStorage.getStorageType(type);
			if (storageClass == null)
				throw new Exception("Shared storage type not mapped");
			Constructor<? extends SharedStorage> ctor = storageClass.getConstructor(WorldDataBase.class, String.class, SharedRegion.class);
			SharedStorage storage = ctor.newInstance(worldStorage, nbt.getString("id"), region);
			if(packet) {
				storage.readFromPacketNBT(nbt.getCompoundTag("data"));
			} else {
				storage.readFromNBT(nbt.getCompoundTag("data"));
			}
			return storage;
		} catch(Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	/**
	 * Saves a shared storage to NBT
	 * @param nbt
	 */
	public static NBTTagCompound save(SharedStorage sharedStorage, NBTTagCompound nbt, boolean packet) {
		ResourceLocation type = SharedStorage.getStorageTypeID(sharedStorage.getClass());
		if (type == null)
			throw new RuntimeException("Shared storage type not mapped");
		nbt.setString("type", type.toString());
		nbt.setString("id", sharedStorage.getID());
		if(packet) {
			nbt.setTag("data", sharedStorage.writeToPacketNBT(new NBTTagCompound()));
		} else {
			nbt.setTag("data", sharedStorage.writeToNBT(new NBTTagCompound()));
		}
		return nbt;
	}

	private final List<EntityPlayerMP> watchers = new ArrayList<>();
	private final WorldDataBase<?> worldStorage;
	private final List<ChunkPos> linkedChunks = new ArrayList<>();
	private final List<SharedStorageReference> loadedReferences = new ArrayList<>();
	private final String id;
	private final SharedRegion region;
	private CapabilityDispatcher capabilities;
	private boolean dirty = false;
	private int version = 0;

	/**
	 * Creates a new shared storage
	 * @param worldStorage World storage
	 * @param id Storage ID. <b>Must be a unique ID!</b>
	 * @param region Optional shared region. <b>Shared regions stay loaded until all references are unloaded, make sure the linked chunks are within a reasonable distance if a region is used!</b>
	 */
	public SharedStorage(WorldDataBase<?> worldStorage, String id, @Nullable SharedRegion region) {
		this.worldStorage = worldStorage;
		this.id = id;
		this.region = region;

		//Gather capabilities
		AttachSharedStorageCapabilitiesEvent event = new AttachSharedStorageCapabilitiesEvent(this);
		MinecraftForge.EVENT_BUS.post(event);
		this.capabilities = event.getCapabilities().size() > 0 ? new CapabilityDispatcher(event.getCapabilities(), null) : null;
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		return this.capabilities == null ? false : this.capabilities.hasCapability(capability, facing);
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		return this.capabilities == null ? null : this.capabilities.getCapability(capability, facing);
	}

	/**
	 * Returns the world storage
	 * @return
	 */
	public final WorldDataBase<?> getWorldStorage() {
		return this.worldStorage;
	}

	/**
	 * Links the specified chunk to this shared storage
	 * @param chunk
	 * @return True if the chunk was linked successfully
	 */
	public final boolean linkChunk(Chunk chunk) {
		ChunkPos chunkPos = new ChunkPos(chunk.x, chunk.z);
		if(!this.linkedChunks.contains(chunkPos)) {
			ChunkDataBase chunkData = ChunkDataBase.forChunk(this.worldStorage, chunk);
			if(chunkData != null && chunkData.linkSharedStorage(this)) {
				if(this.linkedChunks.add(chunkPos)) {
					this.setDirty(true);
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Unlinks the specified chunk from this shared storage
	 * @param chunk
	 * @return True if the chunk was unlinked successfully
	 */
	public final boolean unlinkChunk(Chunk chunk) {
		ChunkPos chunkPos = new ChunkPos(chunk.x, chunk.z);
		if(this.linkedChunks.contains(chunkPos)) {
			ChunkDataBase chunkData = ChunkDataBase.forChunk(this.worldStorage, chunk);
			if(chunkData != null) {
				chunkData.unlinkSharedStorage(this);
				if(this.linkedChunks.remove(chunkPos)) {
					this.setDirty(true);
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Unlinks all chunks from this shared storage
	 * @return True if all chunks were successfully unlinked
	 */
	public final boolean unlinkAllChunks() {
		boolean changed = false;
		boolean allUnlinked = true;
		List<ChunkPos> chunks = new ArrayList<>(this.linkedChunks.size());
		chunks.addAll(this.linkedChunks);
		Iterator<ChunkPos> it = chunks.iterator();
		ChunkPos pos = null;
		while(it.hasNext()) {
			pos = it.next();
			Chunk chunk = this.worldStorage.getWorld().getChunkFromChunkCoords(pos.x, pos.z);
			ChunkDataBase chunkData = ChunkDataBase.forChunk(this.worldStorage, chunk);
			if(chunkData == null || !chunkData.unlinkSharedStorage(this)) {
				allUnlinked = false;
			} else if(chunkData != null) {
				changed = true;
			}
		}
		if(changed) {
			this.setDirty(true);
		}
		this.linkedChunks.clear();
		return allUnlinked;
	}

	/**
	 * Loads a reference
	 * @param reference
	 * @return
	 */
	public final boolean loadReference(SharedStorageReference reference) {
		if(!this.loadedReferences.contains(reference)) {
			return this.loadedReferences.add(reference);
		}
		return false;
	}

	/**
	 * Unloads a reference
	 * @param reference
	 * @return
	 */
	public final boolean unloadReference(SharedStorageReference reference) {
		return this.loadedReferences.remove(reference);
	}

	/**
	 * Returns a list of all currently loaded references
	 * @return
	 */
	public final List<SharedStorageReference> getReferences() {
		return Collections.unmodifiableList(this.loadedReferences);
	}

	/**
	 * Returns a list of all linked chunks
	 * @return
	 */
	public final List<ChunkPos> getLinkedChunks() {
		return Collections.unmodifiableList(this.linkedChunks);
	}

	/**
	 * Returns the ID
	 * @return
	 */
	public final String getID() {
		return this.id;
	}
	
	/**
	 * Returns the region
	 * @return
	 */
	@Nullable
	public final SharedRegion getRegion() {
		return this.region;
	}

	/**
	 * Returns whether this storage is assigned to a region
	 * @return
	 */
	public final boolean hasRegion() {
		return this.region != null;
	}

	/**
	 * Writes the shared storage to the NBT
	 * @param nbt
	 * @return
	 */
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		NBTTagList referenceChunkList = new NBTTagList();
		for(ChunkPos referenceChunk : this.linkedChunks) {
			NBTTagCompound referenceChunkNbt = new NBTTagCompound();
			referenceChunkNbt.setInteger("x", referenceChunk.x);
			referenceChunkNbt.setInteger("z", referenceChunk.z);
			referenceChunkList.appendTag(referenceChunkNbt);
		}
		nbt.setTag("ReferenceChunks", referenceChunkList);
		nbt.setInteger("Version", this.version);
		if (this.capabilities != null) nbt.setTag("ForgeCaps", this.capabilities.serializeNBT());
		return nbt;
	}

	/**
	 * Reads the shared storage from the NBT
	 * @param nbt
	 */
	public void readFromNBT(NBTTagCompound nbt) {
		this.linkedChunks.clear();
		NBTTagList referenceChunkList = nbt.getTagList("ReferenceChunks", Constants.NBT.TAG_COMPOUND);
		for(int i = 0; i < referenceChunkList.tagCount(); i++) {
			NBTTagCompound referenceChunkNbt = referenceChunkList.getCompoundTagAt(i);
			this.linkedChunks.add(new ChunkPos(referenceChunkNbt.getInteger("x"), referenceChunkNbt.getInteger("z")));
		}
		if(nbt.hasKey("Version", Constants.NBT.TAG_INT)) {
			this.version = nbt.getInteger("Version");
		}
		if (this.capabilities != null && nbt.hasKey("ForgeCaps")) this.capabilities.deserializeNBT(nbt.getCompoundTag("ForgeCaps"));
	}

	/**
	 * Writes the shared storage to the packet NBT
	 * @param nbt
	 * @return
	 */
	public NBTTagCompound writeToPacketNBT(NBTTagCompound nbt) {
		return this.writeToNBT(nbt);
	}

	/**
	 * Reads the shared storage from the packet NBT
	 * @param nbt
	 */
	public void readFromPacketNBT(NBTTagCompound nbt) {
		this.readFromNBT(nbt);
	}

	/**
	 * Called when a player starts watching this shared storage
	 * @param chunkStorage
	 * @param player
	 */
	public void onWatched(ChunkDataBase chunkStorage, EntityPlayerMP player) {
		this.watchers.add(player);
	}

	/**
	 * Called when a player stops watching this shared storage
	 * @param chunkStorage
	 * @param player
	 */
	public void onUnwatched(ChunkDataBase chunkStorage, EntityPlayerMP player) {
		this.watchers.remove(player);
	}

	/**
	 * Returns the list of all watchers
	 * @return
	 */
	public List<EntityPlayerMP> getWatchers() {
		return Collections.unmodifiableList(this.watchers);
	}

	/**
	 * Sets whether the data is dirty and needs to be saved to the file
	 * @param dirty
	 * @return
	 */
	public void setDirty(boolean dirty) {
		this.dirty = dirty;
	}

	/**
	 * Returns whether the data is dirty
	 * @return
	 */
	public boolean isDirty() {
		return this.dirty;
	}

	/**
	 * Called after the shared storage has loaded
	 */
	public void onLoaded() {

	}

	/**
	 * Called when the shared storage is unloaded
	 */
	public void onUnloaded() {

	}

	/**
	 * Called when the shared storage is permanently removed from the world
	 */
	public void onRemoved() {

	}
}
