package thebetweenlands.world.storage.chunk.storage;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.chunk.Chunk;
import thebetweenlands.world.storage.chunk.BetweenlandsChunkData;
import thebetweenlands.world.storage.chunk.storage.location.LocationStorage;

public abstract class ChunkStorage {
	private static final Map<String, Class<? extends ChunkStorage>> STORAGE_MAP = new HashMap<String, Class<? extends ChunkStorage>>();

	public static Class<? extends ChunkStorage> getStorageClass(String type) {
		return STORAGE_MAP.get(type);
	}

	public static String getStorageType(Class<? extends ChunkStorage> storageClass) {
		for(Entry<String, Class<? extends ChunkStorage>> entry : STORAGE_MAP.entrySet()) {
			if(storageClass.equals(entry.getValue()))
				return entry.getKey();
		}
		return null;
	}

	static {
		STORAGE_MAP.put("chunkArea", LocationStorage.class);
	}

	private final Chunk chunk;
	private final BetweenlandsChunkData chunkData;

	public ChunkStorage(Chunk chunk, BetweenlandsChunkData chunkData) {
		this.chunk = chunk;
		this.chunkData = chunkData;
	}

	public Chunk getChunk() {
		return this.chunk;
	}

	public BetweenlandsChunkData getChunkData() {
		return this.chunkData;
	}

	public abstract void readFromNBT(NBTTagCompound nbt);
	public abstract void writeToNBT(NBTTagCompound nbt);
}
