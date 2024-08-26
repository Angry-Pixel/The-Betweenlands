package thebetweenlands.api.storage;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;

/**
 * One region spans over 32x32 chunks. Any {@link ILocalStorage} assigned to a region will be saved in that region file.
 * Regions stay loaded until all references are unloaded or the cache is cleared.
 */
public record LocalRegion(int x, int z) {

	/**
	 * Returns a region from the specified BlockPos
	 * @see LocalRegion
	 * @param pos
	 * @return
	 */
	public static LocalRegion getFromBlockPos(BlockPos pos) {
		return getFromBlockPos(pos.getX(), pos.getZ());
	}

	/**
	 * Returns a region from the specified X and Z coordinate
	 * @see LocalRegion
	 * @param x
	 * @param z
	 * @return
	 */
	public static LocalRegion getFromBlockPos(int x, int z) {
		return new LocalRegion(x >> 9, z >> 9);
	}

	/**
	 * Returns the region file name
	 * @return
	 */
	public String getFileName() {
		return "r." + this.x() + "." + this.z();
	}

	/**
	 * Writes the region to the specified NBT
	 * @param tag
	 * @return
	 */
	public CompoundTag writeToNBT(CompoundTag tag) {
		tag.putInt("x", this.x());
		tag.putInt("z", this.z());
		return tag;
	}

	/**
	 * Reads a region from the specified NBT
	 * @param tag
	 * @return
	 */
	public static LocalRegion readFromNBT(CompoundTag tag) {
		return new LocalRegion(tag.getInt("x"), tag.getInt("z"));
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + this.x();
		result = prime * result + this.z();
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (this.getClass() != obj.getClass())
			return false;
		LocalRegion other = (LocalRegion) obj;
		if (this.x() != other.x())
			return false;
		return this.z() == other.z();
	}
}
