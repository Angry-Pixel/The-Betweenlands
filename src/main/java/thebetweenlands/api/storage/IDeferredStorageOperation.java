package thebetweenlands.api.storage;

import net.minecraft.nbt.CompoundTag;

public interface IDeferredStorageOperation {
	/**
	 * Called when the chunk is loaded and this operation is to be run
	 * @param chunkStorage
	 */
	public void apply(IChunkStorage chunkStorage);

	/**
	 * Reads the deferred storage operation data from NBT.
	 * @param tag
	 */
	void readFromNBT(CompoundTag tag);

	/**
	 * Writes the deferred storage operation data to NBT.
	 * @param tag
	 * @return
	 */
	CompoundTag writeToNBT(CompoundTag tag);
}
