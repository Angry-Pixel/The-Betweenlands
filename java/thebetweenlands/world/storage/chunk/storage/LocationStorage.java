package thebetweenlands.world.storage.chunk.storage;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.StatCollector;
import net.minecraft.util.Vec3;
import net.minecraft.world.chunk.Chunk;

public class LocationStorage extends ChunkStorage {
	public static enum EnumLocationType {
		NONE("none"), RUINS("ruins"), HUT("hut"), SHACK("shack"), WIGHT_TOWER("wightTower");

		public static EnumLocationType[] TYPES = EnumLocationType.values();

		public final String name;
		private EnumLocationType(String name) {
			this.name = name;
		}
		public static EnumLocationType fromName(String name) {
			for(EnumLocationType type : TYPES){
				if(type.name.equals(name))
					return type;
			}
			return NONE;
		}
	}

	private String name;
	private AxisAlignedBB area;
	private EnumLocationType type;

	public LocationStorage(Chunk chunk) {
		super(chunk);
	}

	public LocationStorage(Chunk chunk, String name, AxisAlignedBB area, EnumLocationType type) {
		super(chunk);
		this.name = name;
		this.area = area;
		if(type == null)
			type = EnumLocationType.NONE;
		this.type = type;
	}

	public String getLocalizedName() {
		String locationName = this.name;
		boolean translate = locationName.startsWith("translate:");
		if(translate)
			locationName = StatCollector.translateToLocal("location." + locationName.replaceFirst("translate:", "") + ".name");
		return locationName;
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
		this.type = EnumLocationType.fromName(nbt.getString("type"));
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
		nbt.setString("type", this.type.name);
	}

	public boolean isAreaEqual(AxisAlignedBB other) {
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

	public EnumLocationType getType() {
		return this.type;
	}
}
