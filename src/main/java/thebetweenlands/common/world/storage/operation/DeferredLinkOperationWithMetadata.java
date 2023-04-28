package thebetweenlands.common.world.storage.operation;

import net.minecraft.nbt.NBTTagCompound;
import thebetweenlands.api.storage.IDeferredStorageOperationWithMetadata;
import thebetweenlands.api.storage.LocalStorageMetadata;
import thebetweenlands.api.storage.LocalStorageReference;

public class DeferredLinkOperationWithMetadata extends DeferredLinkOperation implements IDeferredStorageOperationWithMetadata {
	private LocalStorageMetadata metadata;

	public DeferredLinkOperationWithMetadata() {
		super();
	}

	public DeferredLinkOperationWithMetadata(LocalStorageReference ref, LocalStorageMetadata metadata) {
		super(ref);
		this.metadata = metadata;
	}

	@Override
	public LocalStorageMetadata getMetadata() {
		return this.metadata;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		this.metadata = LocalStorageMetadata.readFromNBT(nbt.getCompoundTag("meta"));
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		nbt = super.writeToNBT(nbt);
		nbt.setTag("meta", this.metadata.writeToNBT(new NBTTagCompound()));
		return nbt;
	}
}
