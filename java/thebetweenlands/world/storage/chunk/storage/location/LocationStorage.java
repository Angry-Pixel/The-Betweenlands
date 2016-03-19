package thebetweenlands.world.storage.chunk.storage.location;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;
import net.minecraft.util.Vec3;
import net.minecraft.world.chunk.Chunk;
import thebetweenlands.world.storage.chunk.BetweenlandsChunkData;
import thebetweenlands.world.storage.chunk.storage.ChunkStorage;

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
	private int layer;
	private LocationAmbience ambience = null;
	private boolean visible = true;
	private boolean inheritAmbience = true;

	public LocationStorage(Chunk chunk, BetweenlandsChunkData data) {
		super(chunk, data);
	}

	public LocationStorage(Chunk chunk, BetweenlandsChunkData data, String name, AxisAlignedBB area, EnumLocationType type) {
		super(chunk, data);
		this.name = name;
		this.area = area;
		if(type == null)
			type = EnumLocationType.NONE;
		this.type = type;
	}

	public LocationStorage setInheritAmbience(boolean inherit) {
		this.inheritAmbience = inherit;
		this.ambience = null;
		return this;
	}

	public LocationStorage setVisible(boolean visible) {
		this.visible = visible;
		return this;
	}

	public LocationStorage setLayer(int layer) {
		this.layer = layer;
		return this;
	}

	public int getLayer() {
		return this.layer;
	}

	public LocationStorage setAmbience(LocationAmbience ambience) {
		this.inheritAmbience = false;
		this.ambience = ambience;
		ambience.setLocation(this);
		return this;
	}

	public boolean hasAmbience() {
		return this.ambience != null;
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
		this.layer = nbt.getInteger("layer");
		if(nbt.hasKey("ambience")) {
			NBTTagCompound ambienceTag = nbt.getCompoundTag("ambience");
			this.ambience = LocationAmbience.readFromNBT(this, ambienceTag);
		}
		this.visible = nbt.getBoolean("visible");
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
		nbt.setInteger("layer", this.layer);
		if(this.hasAmbience()) {
			NBTTagCompound ambienceTag = new NBTTagCompound();
			this.ambience.writeToNBT(ambienceTag);
			nbt.setTag("ambience", ambienceTag);
		}
		nbt.setBoolean("visible", this.visible);
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

	public boolean isVisible(Entity entity) {
		return this.visible;
	}

	public static List<LocationStorage> getLocations(Entity entity) {
		List<LocationStorage> locations = new ArrayList<LocationStorage>();
		Chunk chunk = entity.worldObj.getChunkFromBlockCoords(MathHelper.floor_double(entity.posX), MathHelper.floor_double(entity.posZ));
		if(chunk != null) {
			BetweenlandsChunkData chunkData = BetweenlandsChunkData.forChunk(entity.worldObj, chunk);
			for(ChunkStorage storage : chunkData.getStorage()) {
				if(storage instanceof LocationStorage && ((LocationStorage)storage).isInside(entity))
					locations.add((LocationStorage)storage);
			}
		}
		return locations;
	}

	private static final Comparator<LocationStorage> LAYER_SORTER = new Comparator<LocationStorage>() {
		@Override
		public int compare(LocationStorage s1, LocationStorage s2) {
			return Integer.compare(s1.getLayer(), s2.getLayer());
		}
	};

	public static LocationAmbience getAmbience(Entity entity) {
		List<LocationStorage> locations = LocationStorage.getLocations(entity);
		Collections.sort(locations, LAYER_SORTER);
		LocationStorage highestLocation = null;
		for(int i = 0; i < locations.size(); i++) {
			LocationStorage storage = locations.get(i);
			if(storage.hasAmbience() || storage.inheritAmbience)
				highestLocation = storage;
		}
		if(highestLocation != null) {
			if(highestLocation.ambience == null && highestLocation.inheritAmbience) {
				for(int i = 0; i < locations.size(); i++) {
					LocationStorage storage = locations.get(i);
					if(storage.hasAmbience())
						return storage.ambience;
				}
			}
			return highestLocation.ambience;
		}
		return null;
	}

	public static boolean isInLocationType(Entity entity, EnumLocationType type) {
		List<LocationStorage> locations = getLocations(entity);
		for(LocationStorage location : locations) {
			if(location.getType() == type) 
				return true;
		}
		return false;
	}
}
