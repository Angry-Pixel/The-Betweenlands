package thebetweenlands.common.world.storage;

import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import thebetweenlands.api.storage.IWorldStorage;


public class BetweenlandsChunkStorage extends ChunkStorageImpl {
	public BetweenlandsChunkStorage(IWorldStorage worldStorage, Chunk chunk) {
		super(worldStorage, chunk);
		//Constructor must be accessible
	}

	public static BetweenlandsChunkStorage forChunk(World world, Chunk chunk) {
		BetweenlandsWorldStorage worldStorage = BetweenlandsWorldStorage.forWorld(world);
		if(worldStorage != null) {
			ChunkStorageImpl chunkStorage = worldStorage.getChunkStorage(chunk);
			if(chunkStorage instanceof BetweenlandsChunkStorage) {
				return (BetweenlandsChunkStorage) chunkStorage;
			}
		}
		return null;
	}

	@Override
	public void init() {
		//System.out.println("INIT");
	}

	@Override
	public void setDefaults() {
		//System.out.println("DEFAULTS");
	}
}
