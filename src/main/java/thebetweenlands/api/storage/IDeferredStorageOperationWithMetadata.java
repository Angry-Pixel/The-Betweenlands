package thebetweenlands.api.storage;

/**
 * Allows {@link IDeferredStorageOperation} to provide metadata. This metadata can be
 * queried by {@link ILocalStorageHandler#getMetadata(Class, net.minecraft.util.math.ChunkPos, boolean)}.
 */
public interface IDeferredStorageOperationWithMetadata extends IDeferredStorageOperation {
	public LocalStorageMetadata getMetadata();
}
