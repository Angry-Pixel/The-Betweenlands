package thebetweenlands.api.event;

import net.minecraftforge.event.AttachCapabilitiesEvent;
import thebetweenlands.api.storage.ILocalStorage;

public class AttachLocalStorageCapabilitiesEvent extends AttachCapabilitiesEvent<ILocalStorage> {
	private final ILocalStorage storage;

	public AttachLocalStorageCapabilitiesEvent(ILocalStorage storage) {
		super(ILocalStorage.class, storage);
		this.storage = storage;
	}

	public ILocalStorage getStorage() {
		return this.storage;
	}
}
