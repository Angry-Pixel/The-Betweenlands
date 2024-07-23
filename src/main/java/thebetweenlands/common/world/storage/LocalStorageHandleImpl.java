package thebetweenlands.common.world.storage;

import net.minecraft.world.level.Level;
import thebetweenlands.api.storage.ILocalStorage;
import thebetweenlands.api.storage.ILocalStorageHandle;
import thebetweenlands.api.storage.LocalStorageReference;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.registries.DimensionRegistries;

import javax.annotation.Nullable;

public class LocalStorageHandleImpl implements ILocalStorageHandle {
	private final ILocalStorage storage;
	@Nullable
	private final LocalStorageReference handleRef;

	public LocalStorageHandleImpl(ILocalStorage storage, LocalStorageReference reference) {
		this.storage = storage;

		Level level = TheBetweenlands.getLevelWorkaround(DimensionRegistries.DIMENSION_KEY);
		if(level != null && !level.isClientSide()) {
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
