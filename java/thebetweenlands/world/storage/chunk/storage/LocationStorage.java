package thebetweenlands.world.storage.chunk.storage;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3;
import net.minecraft.world.chunk.Chunk;

public class LocationStorage extends ChunkStorage {
	private String name;
	private AxisAlignedBB area;

	public LocationStorage(Chunk chunk) {
		super(chunk);
	}

	public LocationStorage(Chunk chunk, String name, AxisAlignedBB area) {
		super(chunk);
		this.name = name;
		this.area = area;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		this.name = nbt.getString("name");
		double minX = nbt.getDouble("minX");
		double minY = nbt.getDouble("minY");
		double minZ = nbt.getDouble("minZ");
		double maxX = nbt.getDouble("maxX");
		double maxY = nbt.getDouble("maxY");
		double maxZ = nbt.getDouble("maxZ");
		this.area = AxisAlignedBB.getBoundingBox(minX, minY, minZ, maxX, maxY, maxZ);
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		nbt.setString("name", this.name);
		nbt.setDouble("minX", this.area.minX);
		nbt.setDouble("minY", this.area.minY);
		nbt.setDouble("minZ", this.area.minZ);
		nbt.setDouble("maxX", this.area.maxX);
		nbt.setDouble("maxY", this.area.maxY);
		nbt.setDouble("maxZ", this.area.maxZ);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		long temp;
		temp = Double.doubleToLongBits(area.minX);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(area.minY);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(area.minZ);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(area.maxX);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(area.maxY);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(area.maxZ);
		result = prime * result + (int) (temp ^ (temp >>> 32));
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
		LocationStorage other = (LocationStorage) obj;
		if (this.area == null) {
			if (other.area != null)
				return false;
		} else if (!this.isAreaEqual(other.area))
			return false;
		if (this.name == null) {
			if (other.name != null)
				return false;
		} else if (!this.name.equals(other.name))
			return false;
		return true;
	}

	private boolean isAreaEqual(AxisAlignedBB other) {
		return this.area.minX == other.minX && this.area.minY == other.minY && this.area.minZ == other.minZ
				&& this.area.maxX == other.maxX && this.area.maxY == other.maxY && this.area.maxZ == other.maxZ;
	}

	public boolean isInside(Entity entity) {
		return this.area.isVecInside(Vec3.createVectorHelper(entity.posX, entity.posY, entity.posZ));
	}

	public AxisAlignedBB getArea() {
		return this.area;
	}

	public String getName() {
		return this.name;
	}
}
