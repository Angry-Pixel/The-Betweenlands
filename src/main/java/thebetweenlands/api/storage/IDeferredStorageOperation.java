package thebetweenlands.api.storage;

import net.minecraft.nbt.NBTTagCompound;

public interface IDeferredStorageOperation {
	/**
	 * Called when the chunk is loaded and this operation is to be run
	 * @param chunkStorage
	 */
	public void apply(IChunkStorage chunkStorage);

	/**
	 * Reads the deferred storage operation data from NBT.
	 * @param nbt
	 */
	public void readFromNBT(NBTTagCompound nbt);

	/**
	 * Writes the deferred storage operation data to NBT.
	 * @param nbt
	 * @return
	 */
	public NBTTagCompound writeToNBT(NBTTagCompound nbt);
}
