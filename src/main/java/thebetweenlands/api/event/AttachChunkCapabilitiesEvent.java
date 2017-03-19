package thebetweenlands.api.event;

import net.minecraftforge.event.AttachCapabilitiesEvent;
import thebetweenlands.common.world.storage.chunk.ChunkDataBase;

public class AttachChunkCapabilitiesEvent extends AttachCapabilitiesEvent<ChunkDataBase> {
	private final ChunkDataBase storage;

	public AttachChunkCapabilitiesEvent(ChunkDataBase storage) {
		super(ChunkDataBase.class, storage);
		this.storage = storage;
	}

	public ChunkDataBase getStorage() {
		return this.storage;
	}
}
