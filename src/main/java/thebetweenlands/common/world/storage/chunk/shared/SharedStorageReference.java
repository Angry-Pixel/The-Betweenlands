package thebetweenlands.common.world.storage.chunk.shared;

import java.util.UUID;

import net.minecraft.nbt.NBTTagCompound;

public class SharedStorageReference {
	private UUID storageUUID;
	private String storageUUIDString;

	private SharedStorageReference() { }

	public SharedStorageReference(UUID uuid) {
		this.setUUID(uuid);
	}

	/**
	 * Reads the reference from the specified NBT
	 * @param nbt
	 * @return
	 */
	public static SharedStorageReference readFromNBT(NBTTagCompound nbt) {
		SharedStorageReference reference = new SharedStorageReference();
		reference.setUUID(nbt.getUniqueId("uuid"));
		return reference;
	}

	/**
	 * Writes this reference to the specified NBT
	 * @param nbt
	 * @return
	 */
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		nbt.setUniqueId("uuid", this.storageUUID);
		return nbt;
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
}
