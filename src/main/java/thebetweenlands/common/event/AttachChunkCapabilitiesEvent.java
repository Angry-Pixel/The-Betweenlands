package thebetweenlands.common.event;

import net.minecraftforge.event.AttachCapabilitiesEvent;
import thebetweenlands.common.world.storage.chunk.ChunkDataBase;

public class AttachChunkCapabilitiesEvent extends AttachCapabilitiesEvent {
	private final ChunkDataBase storage;

	public AttachChunkCapabilitiesEvent(ChunkDataBase storage) {
		super(storage);
		this.storage = storage;
	}

	public ChunkDataBase getStorage() {
		return this.storage;
	}
}
