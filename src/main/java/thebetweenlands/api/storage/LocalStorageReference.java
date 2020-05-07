package thebetweenlands.api.storage;

import javax.annotation.Nullable;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.ChunkPos;

public class LocalStorageReference {
	private final ChunkPos chunkPos;
	private final StorageID id;
	private final LocalRegion region;
	private final ILocalStorageHandle handle;

	/**
	 * Creates a new local storage reference.
	 * @param chunkPos
	 * @param id
	 */
	public LocalStorageReference(ChunkPos chunkPos, StorageID id, @Nullable LocalRegion region) {
		this.id = id;
		this.chunkPos = chunkPos;
		this.region = region;
		this.handle = null;
	}

	/**
	 * Creates a new local storage reference for a handle.
	 * @param handle
	 * @param id
	 * @param region
	 */
	public LocalStorageReference(ILocalStorageHandle handle, StorageID id, @Nullable LocalRegion region) {
		this.id = id;
		this.chunkPos = new ChunkPos(0, 0);
		this.region = region;
		this.handle = handle;
	}

	/**
	 * Reads the reference from the specified NBT
	 * @param nbt
	 * @return
	 */
	public static LocalStorageReference readFromNBT(NBTTagCompound nbt) {
		ChunkPos pos = new ChunkPos(nbt.getInteger("x"), nbt.getInteger("z"));
		LocalRegion region = null;
		if(nbt.hasKey("region")) {
			region = LocalRegion.readFromNBT(nbt.getCompoundTag("region"));
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
		nbt.setInteger("x", this.chunkPos.x);
		nbt.setInteger("z", this.chunkPos.z);
		if(this.region != null) {
			nbt.setTag("region", this.region.writeToNBT(new NBTTagCompound()));
		}
		return nbt;
	}

	/**
	 * Returns the {@link ILocalStorageHandle} this reference belongs to,
	 * if this reference is from a handle
	 * @return
	 */
	@Nullable
	public ILocalStorageHandle getHandle() {
		return this.handle;
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
		result = prime * result + ((chunkPos == null) ? 0 : chunkPos.hashCode());
		result = prime * result + ((handle == null) ? 0 : handle.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((region == null) ? 0 : region.hashCode());
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
		if (chunkPos == null) {
			if (other.chunkPos != null)
				return false;
		} else if (!chunkPos.equals(other.chunkPos))
			return false;
		if (handle == null) {
			if (other.handle != null)
				return false;
		} else if (!handle.equals(other.handle))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (region == null) {
			if (other.region != null)
				return false;
		} else if (!region.equals(other.region))
			return false;
		return true;
	}
}
