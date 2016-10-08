package thebetweenlands.common.world.storage.world.shared;

import java.util.UUID;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.chunk.Chunk;
import thebetweenlands.common.world.storage.world.global.WorldDataBase;

public class LocationStorageNew extends BetweenlandsSharedStorage {
	private AxisAlignedBB boundingBox;

	public LocationStorageNew(WorldDataBase<?> worldStorage, UUID uuid) {
		super(worldStorage, uuid);
	}

	/**
	 * Creates a new location
	 * @param worldStorage
	 * @param uuid
	 * @param boundingBox
	 */
	public LocationStorageNew(WorldDataBase<?> worldStorage, UUID uuid, AxisAlignedBB boundingBox) {
		super(worldStorage, uuid);
		this.boundingBox = boundingBox;
	}

	/**
	 * Links all chunks of this location
	 * @return
	 */
	public LocationStorageNew linkChunks() {
		int sx = MathHelper.floor_double(this.boundingBox.minX / 16.0D);
		int sz = MathHelper.floor_double(this.boundingBox.minZ / 16.0D);
		int ex = MathHelper.floor_double(this.boundingBox.maxX / 16.0D);
		int ez = MathHelper.floor_double(this.boundingBox.maxZ / 16.0D);
		for(int cx = sx; cx <= ex; cx++) {
			for(int cz = sz; cz <= ez; cz++) {
				Chunk chunk = this.getWorldStorage().getWorld().getChunkFromChunkCoords(cx, cz);
				this.linkChunk(chunk);
			}
		}
		return this;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		double minX = nbt.getDouble("minX");
		double minY = nbt.getDouble("minY");
		double minZ = nbt.getDouble("minZ");
		double maxX = nbt.getDouble("maxX");
		double maxY = nbt.getDouble("maxY");
		double maxZ = nbt.getDouble("maxZ");
		this.boundingBox = new AxisAlignedBB(minX, minY, minZ, maxX, maxY, maxZ);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		nbt.setDouble("minX", this.boundingBox.minX);
		nbt.setDouble("minY", this.boundingBox.minY);
		nbt.setDouble("minZ", this.boundingBox.minZ);
		nbt.setDouble("maxX", this.boundingBox.maxX);
		nbt.setDouble("maxY", this.boundingBox.maxY);
		nbt.setDouble("maxZ", this.boundingBox.maxZ);
		return nbt;
	}

	public AxisAlignedBB getBoundingBox() {
		return this.boundingBox;
	}
}
