package thebetweenlands.common.event;

import net.minecraftforge.event.AttachCapabilitiesEvent;
import thebetweenlands.common.world.storage.world.shared.SharedStorage;

public class AttachSharedStorageCapabilitiesEvent extends AttachCapabilitiesEvent {
	private final SharedStorage storage;

	public AttachSharedStorageCapabilitiesEvent(SharedStorage storage) {
		super(storage);
		this.storage = storage;
	}

	public SharedStorage getStorage() {
		return this.storage;
	}
}
