package thebetweenlands.common.world.storage.chunk.shared;

import java.util.UUID;

import javax.annotation.Nullable;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.ChunkPos;

public class SharedStorageReference {
	private ChunkPos chunkPos;
	private UUID storageUUID;
	private String storageUUIDString;
	private String region;

	private SharedStorageReference() { }

	public SharedStorageReference(ChunkPos chunkPos, UUID uuid, String region) {
		this.setUUID(uuid);
		this.chunkPos = chunkPos;
		this.region = region;
	}

	/**
	 * Reads the reference from the specified NBT
	 * @param nbt
	 * @return
	 */
	public static SharedStorageReference readFromNBT(NBTTagCompound nbt) {
		SharedStorageReference reference = new SharedStorageReference();
		reference.setUUID(nbt.getUniqueId("uuid"));
		reference.chunkPos = new ChunkPos(nbt.getInteger("x"), nbt.getInteger("z"));
		if(nbt.hasKey("region")) {
			reference.setRegion(nbt.getString("region"));
		} else {
			reference.setRegion(null);
		}
		return reference;
	}

	/**
	 * Writes this reference to the specified NBT
	 * @param nbt
	 * @return
	 */
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		nbt.setUniqueId("uuid", this.storageUUID);
		nbt.setInteger("x", this.chunkPos.chunkXPos);
		nbt.setInteger("z", this.chunkPos.chunkZPos);
		String region = this.getRegion();
		if(region != null) {
			nbt.setString("region", region);
		}
		return nbt;
	}

	/**
	 * Sets the storage region. Storages with the same region identifier are
	 * saved in the same file
	 * @param region
	 */
	public void setRegion(@Nullable String region) {
		this.region = region;
	}

	/**
	 * Returns whether this reference is attached to a region
	 * @return
	 */
	public boolean hasRegion() {
		return this.region != null;
	}

	/**
	 * Returns the region
	 * @return
	 */
	@Nullable 
	public String getRegion() {
		return this.region;
	}

	/**
	 * Sets the reference UUID
	 * @param uuid
	 */
	public void setUUID(UUID uuid) {
		this.storageUUID = uuid;
		this.storageUUIDString = uuid.toString();
	}

	/**
	 * Returns the reference UUID
	 * @return
	 */
	public UUID getUUID() {
		return this.storageUUID;
	}

	/**
	 * Returns the reference UUID string
	 * @return
	 */
	public String getUUIDString() {
		return this.storageUUIDString;
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
		result = prime * result + ((this.storageUUID == null) ? 0 : this.storageUUID.hashCode());
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
		if (this.storageUUID == null) {
			if (other.storageUUID != null)
				return false;
		} else if (!this.storageUUID.equals(other.storageUUID))
			return false;
		return true;
	}
}
