package thebetweenlands.common.world.storage;

import thebetweenlands.api.storage.ILocalStorage;
import thebetweenlands.api.storage.ILocalStorageHandle;
import thebetweenlands.api.storage.LocalStorageReference;

public class LocalStorageHandleImpl implements ILocalStorageHandle {
	private final ILocalStorage storage;
	private final LocalStorageReference handleRef;

	public LocalStorageHandleImpl(ILocalStorage storage, LocalStorageReference reference) {
		this.storage = storage;

		if(!this.storage.getWorldStorage().getWorld().isRemote) {
			this.handleRef = new LocalStorageReference(this, reference.getID(), reference.getRegion());
			storage.loadReference(this.handleRef);
		} else {
			this.handleRef = null;
		}
	}

	@Override
	public ILocalStorage get() {
		return this.storage;
	}

	@Override
	public void close() {
		if(this.handleRef != null) {
			this.storage.unloadReference(this.handleRef);

			if(this.storage.getLoadedReferences().isEmpty()) {
				this.storage.getWorldStorage().getLocalStorageHandler().unloadLocalStorage(this.storage);
			}
		}
	}
}
