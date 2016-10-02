package thebetweenlands.common.world.storage.chunk.storage.shared;

import java.util.Random;
import java.util.UUID;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.chunk.Chunk;
import thebetweenlands.common.world.storage.chunk.ChunkDataBase;
import thebetweenlands.common.world.storage.chunk.storage.ChunkStorage;
import thebetweenlands.common.world.storage.world.global.WorldDataBase;
import thebetweenlands.common.world.storage.world.shared.SharedStorage;

public abstract class SharedStorageReference extends ChunkStorage {
	private UUID locationUUID;
	private String locationUUIDStr;
	private SharedStorage sharedStorage;
	private final WorldDataBase worldStorage;

	public SharedStorageReference(Chunk chunk, ChunkDataBase chunkData) {
		super(chunk, chunkData);
		this.locationUUID = MathHelper.getRandomUuid(new Random());
		this.locationUUIDStr = this.locationUUID.toString();
		this.worldStorage = this.getWorldStorage();
	}

	public SharedStorageReference(Chunk chunk, ChunkDataBase chunkData, UUID locationUUID) {
		super(chunk, chunkData);
		this.locationUUID = locationUUID;
		this.locationUUIDStr = this.locationUUID.toString();
		this.worldStorage = this.getWorldStorage();
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		this.locationUUID = nbt.getUniqueId("UUID");
		this.locationUUIDStr = this.locationUUID.toString();
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		nbt.setUniqueId("UUID", this.locationUUID);
	}

	@Override
	public void onLoaded() {
		if(!this.getChunk().getWorld().isRemote && this.locationUUID != null) {
			this.worldStorage.loadSharedStorageReference(this, null); //TODO: Load NBT from file on server side with CompressedStreamTools
		}
	}

	@Override
	public void onUnloaded() {
		this.worldStorage.unloadSharedStorageReference(this, null); //TODO: Save NBT to file on server side with CompressedStreamTools
	}

	/**
	 * Returns the UUID of the shared storage
	 * @return
	 */
	public UUID getUniqueID() {
		return this.locationUUID;
	}

	/**
	 * Returns the UUID String of the shared storage
	 * @return
	 */
	public String getUniqueIDString() {
		return this.locationUUIDStr;
	}

	/**
	 * Returns the shared storage
	 * @return
	 */
	public SharedStorage getSharedStorage() {
		return this.sharedStorage;
	}

	@Override
	public void onWatched(EntityPlayerMP player) {
		if(this.sharedStorage != null && !this.sharedStorage.getWatchers().contains(player)) {
			this.sharedStorage.onWatched(player);
		}
	}

	@Override
	public void onUnwatched(EntityPlayerMP player) {
		if(this.sharedStorage != null && this.sharedStorage.getWatchers().contains(player)) {
			this.sharedStorage.onUnwatched(player);
		}
	}

	/**
	 * Returns the world storage
	 * @return
	 */
	public abstract WorldDataBase getWorldStorage();
}
