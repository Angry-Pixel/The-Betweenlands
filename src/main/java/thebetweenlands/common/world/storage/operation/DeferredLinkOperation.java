package thebetweenlands.common.world.storage.operation;

import net.minecraft.nbt.CompoundTag;
import thebetweenlands.api.storage.IChunkStorage;
import thebetweenlands.api.storage.IDeferredStorageOperation;
import thebetweenlands.api.storage.ILocalStorageHandle;
import thebetweenlands.api.storage.ILocalStorageHandler;
import thebetweenlands.api.storage.LocalStorageReference;

public class DeferredLinkOperation implements IDeferredStorageOperation {
	private LocalStorageReference ref;

	public DeferredLinkOperation() {

	}

	public DeferredLinkOperation(LocalStorageReference ref) {
		this.ref = ref;
	}

	@Override
	public void apply(IChunkStorage chunkStorage) {
		ILocalStorageHandler handler = chunkStorage.getWorldStorage().getLocalStorageHandler();

		try(ILocalStorageHandle handle = handler.getOrLoadLocalStorage(this.ref)) {
			if(handle != null) {
				chunkStorage.linkLocalStorage(handle.get());
			}
		}
	}

	@Override
	public void readFromNBT(CompoundTag tag) {
		this.ref = LocalStorageReference.readFromNBT(tag);
	}

	@Override
	public CompoundTag writeToNBT(CompoundTag tag) {
		this.ref.writeToNBT(tag);
		return tag;
	}
}
