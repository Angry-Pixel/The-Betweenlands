package thebetweenlands.common.world.storage.chunk.storage;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import thebetweenlands.common.world.storage.chunk.ChunkDataBase;

public abstract class ChunkStorage {
	private static final Map<ResourceLocation, Class<? extends ChunkStorage>> STORAGE_MAP = new HashMap<ResourceLocation, Class<? extends ChunkStorage>>();

	/**
	 * Returns the chunk storage class for the specified ID
	 * @param type
	 * @return
	 */
	public static Class<? extends ChunkStorage> getStorageType(ResourceLocation id) {
		return STORAGE_MAP.get(id);
	}

	/**
	 * Returns the chunk storage ID for the specified class
	 * @param storageClass
	 * @return
	 */
	public static ResourceLocation getStorageTypeID(Class<? extends ChunkStorage> storageClass) {
		for(Entry<ResourceLocation, Class<? extends ChunkStorage>> entry : STORAGE_MAP.entrySet()) {
			if(storageClass.equals(entry.getValue()))
				return entry.getKey();
		}
		return null;
	}

	/**
	 * Registers a chunk storage type
	 * @param id
	 * @param storageClass
	 */
	public static void registerStorageType(ResourceLocation id, Class<? extends ChunkStorage> storageClass) {
		if(STORAGE_MAP.containsKey(id))
			throw new RuntimeException("Duplicate chunk storage ID");
		STORAGE_MAP.put(id, storageClass);
	}

	/**
	 * Loads a chunk storage from NBT
	 * @param nbt
	 * @return
	 */
	public static ChunkStorage load(World world, ChunkDataBase chunkData, NBTTagCompound nbt, boolean packet) {
		try {
			ResourceLocation type = new ResourceLocation(nbt.getString("type"));
			Class<? extends ChunkStorage> storageClass = ChunkStorage.getStorageType(type);
			if (storageClass == null)
				throw new Exception("Chunk storage type not mapped");
			Constructor<? extends ChunkStorage> ctor = storageClass.getConstructor(World.class, ChunkDataBase.class);
			ChunkStorage storage = ctor.newInstance(world, chunkData);
			if(packet) {
				storage.readFromPacketNBT(nbt.getCompoundTag("storage"));
			} else {
				storage.readFromNBT(nbt.getCompoundTag("storage"));
			}
			return storage;
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	/**
	 * Saves a chunk storage to NBT
	 * @param storage
	 * @param nbt
	 */
	public static void save(ChunkStorage storage, NBTTagCompound nbt, boolean packet) {
		ResourceLocation type = ChunkStorage.getStorageTypeID(storage.getClass());
		if (type == null)
			throw new RuntimeException("Chunk storage type not mapped");
		nbt.setString("type", type.toString());
		NBTTagCompound storageNBT = new NBTTagCompound();
		if(packet) {
			storage.writeToPacketNBT(storageNBT);
		} else {
			storage.writeToNBT(storageNBT);
		}
		nbt.setTag("storage", storageNBT);
	}

	private final Chunk chunk;
	private final ChunkDataBase chunkData;

	public ChunkStorage(World world, ChunkDataBase chunkData) {
		this.chunk = chunkData.getChunk();
		this.chunkData = chunkData;
	}

	/**
	 * Returns the chunk
	 * @return
	 */
	public Chunk getChunk() {
		return this.chunk;
	}

	/**
	 * Marks the data as dirty
	 */
	public void markDirty() {
		this.chunkData.markDirty();
	}

	/**
	 * Called after the chunk data has loaded
	 */
	public void onLoaded() {

	}

	/**
	 * Called when the chunk is unloaded
	 */
	public void onUnloaded() {

	}

	/**
	 * Called when a player starts watching the chunk
	 * @param player
	 */
	public void onWatched(EntityPlayerMP player) {

	}

	/**
	 * Called when a player stops watching the chunk
	 * @param player
	 */
	public void onUnwatched(EntityPlayerMP player) {

	}

	/**
	 * Reads the data from the NBT
	 * @param nbt
	 */
	public void readFromNBT(NBTTagCompound nbt) {
	}

	/**
	 * Writes the data to the NBT
	 * @param nbt
	 */
	public void writeToNBT(NBTTagCompound nbt) {
	}

	/**
	 * Reads the data from the packet NBT
	 * @param nbt
	 */
	public void readFromPacketNBT(NBTTagCompound nbt) {
		this.readFromNBT(nbt);
	}

	/**
	 * Writes the data to the packet NBT
	 * @param nbt
	 */
	public void writeToPacketNBT(NBTTagCompound nbt) {
		this.writeToNBT(nbt);
	}
}
