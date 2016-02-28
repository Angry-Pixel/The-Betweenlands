package thebetweenlands.world.storage.chunk;

import net.minecraft.world.chunk.Chunk;
import thebetweenlands.lib.ModInfo;

public class BetweenlandsChunkData extends ChunkDataBase {
	public BetweenlandsChunkData() {
		super(ModInfo.ID + ":chunkData");
	}

	@Override
	protected void load() {
		System.out.println("LOAD CHUNK DATA");
	}

	@Override
	protected void save() {
		System.out.println("SAVE CHUNK DATA");
	}

	@Override
	protected void init() {
		System.out.println("INIT");
	}

	@Override
	protected void setDefaults() {
		System.out.println("DEFAULTS");
	}

	public static BetweenlandsChunkData forChunk(Chunk chunk) {
		return ChunkDataBase.forChunk(chunk, BetweenlandsChunkData.class);
	}
}
