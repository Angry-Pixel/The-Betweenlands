package thebetweenlands.api.storage;

import javax.annotation.Nullable;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.ChunkPos;
import net.minecraftforge.common.util.Constants;

public class LocalStorageReference {
	private final ChunkPos chunkPos;
	private final StorageID id;
	private final LocalRegion region;

	/**
	 * Creates a new local storage reference.
	 * @param chunkPos
	 * @param id
	 */
	public LocalStorageReference(ChunkPos chunkPos, StorageID id, @Nullable LocalRegion region) {
		this.id = id;
		this.chunkPos = chunkPos;
		this.region = region;
	}

	/**
	 * Reads the reference from the specified NBT
	 * @param nbt
	 * @return
	 */
	public static LocalStorageReference readFromNBT(NBTTagCompound nbt) {
		ChunkPos pos = new ChunkPos(nbt.getInt("x"), nbt.getInt("z"));
		LocalRegion region = null;
		if(nbt.contains("region", Constants.NBT.TAG_COMPOUND)) {
			region = LocalRegion.readFromNBT(nbt.getCompound("region"));
		}
		return new LocalStorageReference(pos, StorageID.readFromNBT(nbt), region);
	}

	/**
	 * Writes this reference to the specified NBT
	 * @param nbt
	 * @return
	 */
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		this.id.writeToNBT(nbt);
		nbt.setInt("x", this.chunkPos.x);
		nbt.setInt("z", this.chunkPos.z);
		if(this.region != null) {
			nbt.setTag("region", this.region.writeToNBT(new NBTTagCompound()));
		}
		return nbt;
	}

	/**
	 * Returns the reference ID string
	 * @return
	 */
	public StorageID getID() {
		return this.id;
	}

	/**
	 * Returns the region
	 * @return
	 */
	@Nullable
	public LocalRegion getRegion() {
		return this.region;
	}

	/**
	 * Returns whether this reference is assigned to a region
	 * @return
	 */
	public boolean hasRegion() {
		return this.region != null;
	}

	/**
	 * Returns the chunk position
	 * @return
	 */
	public ChunkPos getChunk() {
		return this.chunkPos;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.chunkPos == null) ? 0 : this.chunkPos.hashCode());
		result = prime * result + ((this.id == null) ? 0 : this.id.hashCode());
		result = prime * result + ((this.region == null) ? 0 : this.region.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		LocalStorageReference other = (LocalStorageReference) obj;
		if (this.chunkPos == null) {
			if (other.chunkPos != null)
				return false;
		} else if (!this.chunkPos.equals(other.chunkPos))
			return false;
		if (this.id == null) {
			if (other.id != null)
				return false;
		} else if (!this.id.equals(other.id))
			return false;
		if (this.region == null) {
			if (other.region != null)
				return false;
		} else if (!this.region.equals(other.region))
			return false;
		return true;
	}
}
