package thebetweenlands.common.world.storage.chunk.shared;

import javax.annotation.Nullable;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.ChunkPos;
import thebetweenlands.common.world.storage.world.shared.SharedRegion;

public class SharedStorageReference {
	private final ChunkPos chunkPos;
	private final String id;
	private final SharedRegion region;

	/**
	 * Creates a new shared storage reference.
	 * @param chunkPos
	 * @param id
	 */
	public SharedStorageReference(ChunkPos chunkPos, String id, @Nullable SharedRegion region) {
		this.id = id;
		this.chunkPos = chunkPos;
		this.region = region;
	}

	/**
	 * Reads the reference from the specified NBT
	 * @param nbt
	 * @return
	 */
	public static SharedStorageReference readFromNBT(NBTTagCompound nbt) {
		String id = nbt.getString("id");
		ChunkPos pos = new ChunkPos(nbt.getInteger("x"), nbt.getInteger("z"));
		SharedRegion region = null;
		if(nbt.hasKey("region")) {
			region = SharedRegion.readFromNBT(nbt.getCompoundTag("region"));
		}
		return new SharedStorageReference(pos, id, region);
	}

	/**
	 * Writes this reference to the specified NBT
	 * @param nbt
	 * @return
	 */
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		nbt.setString("id", this.id);
		nbt.setInteger("x", this.chunkPos.x);
		nbt.setInteger("z", this.chunkPos.z);
		if(this.region != null) {
			nbt.setTag("region", this.region.writeToNBT(new NBTTagCompound()));
		}
		return nbt;
	}

	/**
	 * Returns the reference ID string
	 * @return
	 */
	public String getID() {
		return this.id;
	}

	/**
	 * Returns the region
	 * @return
	 */
	@Nullable
	public SharedRegion getRegion() {
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
		SharedStorageReference other = (SharedStorageReference) obj;
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
