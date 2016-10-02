package thebetweenlands.common.event;

import net.minecraftforge.event.AttachCapabilitiesEvent;
import thebetweenlands.common.world.storage.chunk.storage.ChunkStorage;

public class AttachChunkStorageCapabilitiesEvent extends AttachCapabilitiesEvent {
	private final ChunkStorage storage;

	public AttachChunkStorageCapabilitiesEvent(ChunkStorage storage) {
		super(storage);
		this.storage = storage;
	}

	public ChunkStorage getStorage() {
		return this.storage;
	}
}
