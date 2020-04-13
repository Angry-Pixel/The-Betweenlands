package thebetweenlands.common.world.storage.operation;

import net.minecraft.nbt.NBTTagCompound;
import thebetweenlands.api.storage.IChunkStorage;
import thebetweenlands.api.storage.IDeferredStorageOperation;
import thebetweenlands.api.storage.ILocalStorage;
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

		ILocalStorage localStorage = handler.getLocalStorage(this.ref.getID());
		if(localStorage == null) {
			localStorage = handler.loadLocalStorage(ref);
		}

		if(localStorage != null) {
			chunkStorage.linkLocalStorage(localStorage);
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
