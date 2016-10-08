package thebetweenlands.common.world.storage.world.shared.location;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.chunk.Chunk;
import thebetweenlands.common.world.storage.world.global.BetweenlandsWorldData;
import thebetweenlands.common.world.storage.world.global.WorldDataBase;
import thebetweenlands.common.world.storage.world.shared.BetweenlandsSharedStorage;

public class LocationStorage extends BetweenlandsSharedStorage {
	private AxisAlignedBB boundingBox;
	private String name;
	private EnumLocationType type;
	private int layer;
	private LocationAmbience ambience = null;
	private boolean visible = true;
	private boolean inheritAmbience = true;
	private long locationSeed = 0L;
	private boolean guarded = false;

	public LocationStorage(WorldDataBase<?> worldStorage, UUID uuid) {
		super(worldStorage, uuid);
	}

	/**
	 * Creates a new location
	 * @param worldStorage
	 * @param uuid
	 * @param boundingBox
	 */
	public LocationStorage(WorldDataBase<?> worldStorage, UUID uuid, String name, AxisAlignedBB boundingBox, EnumLocationType type) {
		super(worldStorage, uuid);
		this.name = name;
		this.boundingBox = boundingBox;
		if(type == null)
			type = EnumLocationType.NONE;
		this.type = type;
	}

	/**
	 * Links all chunks of this location
	 * @return
	 */
	public LocationStorage linkChunks() {
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

	/**
	 * Returns whether this location is guarded
	 * @return
	 */
	public boolean isGuarded() {
		return this.guarded;
	}

	/**
	 * Sets whether this location is guarded
	 * @param guarded
	 * @return
	 */
	public LocationStorage setGuarded(boolean guarded) {
		this.guarded = guarded;
		return this;
	}

	/**
	 * Sets the location seed
	 * @param seed
	 * @return
	 */
	public LocationStorage setSeed(long seed) {
		this.locationSeed = seed;
		return this;
	}

	/**
	 * Returns the location seed
	 * @return
	 */
	public long getSeed() {
		return this.locationSeed;
	}

	/**
	 * Sets whether this location should inherit ambiences from lower layers
	 * @param inherit
	 * @return
	 */
	public LocationStorage setInheritAmbience(boolean inherit) {
		this.inheritAmbience = inherit;
		this.ambience = null;
		return this;
	}

	/**
	 * Sets whether this location is visible
	 * @param visible
	 * @return
	 */
	public LocationStorage setVisible(boolean visible) {
		this.visible = visible;
		return this;
	}

	/**
	 * Sets the location layer
	 * @param layer
	 * @return
	 */
	public LocationStorage setLayer(int layer) {
		this.layer = layer;
		return this;
	}

	/**
	 * Returns the location layer
	 * @return
	 */
	public int getLayer() {
		return this.layer;
	}

	/**
	 * Sets the location ambience
	 * @param ambience
	 * @return
	 */
	public LocationStorage setAmbience(LocationAmbience ambience) {
		this.inheritAmbience = false;
		this.ambience = ambience;
		ambience.setLocation(this);
		return this;
	}

	/**
	 * Returns whether the location has an ambience
	 * @return
	 */
	public boolean hasAmbience() {
		return this.ambience != null;
	}

	/**
	 * Returns the localized name of this location
	 * @return
	 */
	public String getLocalizedName() {
		String locationName = this.name;
		boolean translate = locationName.startsWith("translate:");
		if(translate)
			locationName = I18n.format("location." + locationName.replaceFirst("translate:", "") + ".name");
		return locationName;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		this.name = nbt.getString("name");
		double minX = nbt.getDouble("minX");
		double minY = nbt.getDouble("minY");
		double minZ = nbt.getDouble("minZ");
		double maxX = nbt.getDouble("maxX");
		double maxY = nbt.getDouble("maxY");
		double maxZ = nbt.getDouble("maxZ");
		this.boundingBox = new AxisAlignedBB(minX, minY, minZ, maxX, maxY, maxZ);
		this.type = EnumLocationType.fromName(nbt.getString("type"));
		this.layer = nbt.getInteger("layer");
		if(nbt.hasKey("ambience")) {
			NBTTagCompound ambienceTag = nbt.getCompoundTag("ambience");
			this.ambience = LocationAmbience.readFromNBT(this, ambienceTag);
		}
		this.visible = nbt.getBoolean("visible");
		this.guarded = nbt.getBoolean("guarded");
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		nbt.setString("name", this.name);
		nbt.setDouble("minX", this.boundingBox.minX);
		nbt.setDouble("minY", this.boundingBox.minY);
		nbt.setDouble("minZ", this.boundingBox.minZ);
		nbt.setDouble("maxX", this.boundingBox.maxX);
		nbt.setDouble("maxY", this.boundingBox.maxY);
		nbt.setDouble("maxZ", this.boundingBox.maxZ);
		nbt.setString("type", this.type.name);
		nbt.setInteger("layer", this.layer);
		if(this.hasAmbience()) {
			NBTTagCompound ambienceTag = new NBTTagCompound();
			this.ambience.writeToNBT(ambienceTag);
			nbt.setTag("ambience", ambienceTag);
		}
		nbt.setBoolean("visible", this.visible);
		nbt.setBoolean("guarded", this.guarded);
		return nbt;
	}

	/**
	 * Returns the bounding box of this location
	 * @return
	 */
	public AxisAlignedBB getBoundingBox() {
		return this.boundingBox;
	}

	/**
	 * Returns whether the specified bounding box is equal to the bounding box of this location
	 * @param other
	 * @return
	 */
	public boolean isBoundingBoxEqual(AxisAlignedBB other) {
		return this.boundingBox.minX == other.minX && this.boundingBox.minY == other.minY && this.boundingBox.minZ == other.minZ
				&& this.boundingBox.maxX == other.maxX && this.boundingBox.maxY == other.maxY && this.boundingBox.maxZ == other.maxZ;
	}

	/**
	 * Returns whether the specified entity is inside this location
	 * @param entity
	 * @return
	 */
	public boolean isInside(Entity entity) {
		return this.boundingBox.isVecInside(entity.getPositionVector());
	}

	/**
	 * Returns whether the specified position is inside this location
	 * @param pos
	 * @return
	 */
	public boolean isInside(BlockPos pos) {
		return this.boundingBox.isVecInside(new Vec3d(pos.getX(), pos.getY(), pos.getZ()));
	}

	/**
	 * Returns the location bounding box
	 * @return
	 */
	public AxisAlignedBB getArea() {
		return this.boundingBox;
	}

	/**
	 * Returns the location name
	 * @return
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Returns the location type
	 * @return
	 */
	public EnumLocationType getType() {
		return this.type;
	}

	/**
	 * Returns whether the location is visible and has a title
	 * @param entity
	 * @return
	 */
	public boolean isVisible(Entity entity) {
		return this.visible;
	}

	private static final Comparator<LocationStorage> LAYER_SORTER = new Comparator<LocationStorage>() {
		@Override
		public int compare(LocationStorage s1, LocationStorage s2) {
			return Integer.compare(s1.getLayer(), s2.getLayer());
		}
	};

	/**
	 * Returns a list of all locations at the specified entity
	 * @param entity
	 * @return
	 */
	public static List<LocationStorage> getLocations(Entity entity) {
		BetweenlandsWorldData worldStorage = BetweenlandsWorldData.forWorld(entity.worldObj);
		return worldStorage.getSharedStorageAt(LocationStorage.class, (location) -> {
			return location.isInside(entity);
		}, entity.posX, entity.posZ);
	}

	/**
	 * Returns the highest priority ambience at the specified entity
	 * @param entity
	 * @return
	 */
	public static LocationAmbience getAmbience(Entity entity) {
		List<LocationStorage> locations = LocationStorage.getLocations(entity);
		if(locations.isEmpty())
			return null;
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

	/**
	 * Returns whether the entity is in the specified location type
	 * @param entity
	 * @param type
	 * @return
	 */
	public static boolean isInLocationType(Entity entity, EnumLocationType type) {
		List<LocationStorage> locations = getLocations(entity);
		for(LocationStorage location : locations) {
			if(location.getType() == type) 
				return true;
		}
		return false;
	}

	/**
	 * Returns whether the location at the entity position is guarded
	 * @param entity
	 * @return
	 */
	public static boolean isLocationGuarded(Entity entity) {
		List<LocationStorage> locations = getLocations(entity);
		for(LocationStorage location : locations) {
			if(location.isGuarded())
				return true;
		}
		return false;
	}
}
