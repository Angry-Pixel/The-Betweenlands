package thebetweenlands.api.storage;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;

public interface IDeferredStorageOperation {
	/**
	 * Called when the chunk is loaded and this operation is to be run
	 * @param chunkStorage
	 */
	void apply(LevelAccessor level, IChunkStorage chunkStorage);

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
