package thebetweenlands.common.world.storage.operation;

import net.minecraft.nbt.NBTTagCompound;
import thebetweenlands.api.storage.IChunkStorage;
import thebetweenlands.api.storage.IDeferredStorageOperation;
import thebetweenlands.api.storage.ILocalStorageHandle;
import thebetweenlands.api.storage.ILocalStorageHandler;
import thebetweenlands.api.storage.LocalStorageReference;

public class DeferredLinkOperation implements IDeferredStorageOperation {
	protected LocalStorageReference ref;

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
				handler.linkChunk(handle.get(), chunkStorage.getChunk());
			}
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		this.ref = LocalStorageReference.readFromNBT(nbt);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		this.ref.writeToNBT(nbt);
		return nbt;
	}
}
