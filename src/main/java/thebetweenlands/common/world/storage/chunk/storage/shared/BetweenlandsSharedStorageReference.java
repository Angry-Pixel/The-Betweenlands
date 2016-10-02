package thebetweenlands.common.world.storage.chunk.storage.shared;

import java.util.UUID;

import net.minecraft.world.chunk.Chunk;
import thebetweenlands.common.world.storage.chunk.ChunkDataBase;
import thebetweenlands.common.world.storage.world.global.BetweenlandsWorldData;
import thebetweenlands.common.world.storage.world.global.WorldDataBase;

public class BetweenlandsSharedStorageReference extends SharedStorageReference {
	public BetweenlandsSharedStorageReference(Chunk chunk, ChunkDataBase chunkData) {
		super(chunk, chunkData);
	}

	public BetweenlandsSharedStorageReference(Chunk chunk, ChunkDataBase chunkData, UUID uuid) {
		super(chunk, chunkData, uuid);
	}

	@Override
	public WorldDataBase getWorldStorage() {
		return BetweenlandsWorldData.forWorld(this.getChunk().getWorld());
	}
}
