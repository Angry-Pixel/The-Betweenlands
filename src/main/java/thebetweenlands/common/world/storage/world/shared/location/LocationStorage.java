package thebetweenlands.common.world.storage.world.shared.location;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.util.Constants;
import thebetweenlands.common.world.storage.world.global.BetweenlandsWorldData;
import thebetweenlands.common.world.storage.world.global.WorldDataBase;
import thebetweenlands.common.world.storage.world.shared.BetweenlandsSharedStorage;
import thebetweenlands.common.world.storage.world.shared.SharedRegion;
import thebetweenlands.common.world.storage.world.shared.location.guard.ILocationGuard;

public class LocationStorage extends BetweenlandsSharedStorage {
	private List<AxisAlignedBB> boundingBoxes = new ArrayList<>();
	private AxisAlignedBB enclosingBoundingBox;
	private String name;
	private EnumLocationType type;
	private int layer;
	private LocationAmbience ambience = null;
	private boolean visible = true;
	private boolean inheritAmbience = true;
	private long locationSeed = 0L;

	public LocationStorage(WorldDataBase<?> worldStorage, String id, @Nullable SharedRegion region) {
		super(worldStorage, id, region);
		this.name = "";
		this.type = EnumLocationType.NONE;
	}

	/**
	 * Creates a new location
	 * @param worldStorage
	 * @param id
	 * @param boundingBox
	 */
	public LocationStorage(WorldDataBase<?> worldStorage, String id, @Nullable SharedRegion region, String name, EnumLocationType type) {
		super(worldStorage, id, region);
		this.name = name;
		if(type == null)
			type = EnumLocationType.NONE;
		this.type = type;
	}

	/**
	 * Adds the specified bounding boxes
	 * @param boundingBoxes
	 * @return
	 */
	public LocationStorage addBounds(AxisAlignedBB... boundingBoxes) {
		for(AxisAlignedBB boundingBox : boundingBoxes) {
			this.boundingBoxes.add(boundingBox);
		}
		this.updateEnclosingBounds();
		return this;
	}

	/**
	 * Returns the bounding boxes of this location
	 * @return
	 */
	public List<AxisAlignedBB> getBounds() {
		return Collections.unmodifiableList(this.boundingBoxes);
	}

	/**
	 * Removes the specified bounding boxes
	 * @param boundingBoxes
	 */
	public void removeBounds(AxisAlignedBB... boundingBoxes) {
		for(AxisAlignedBB boundingBox : boundingBoxes) {
			this.boundingBoxes.remove(boundingBox);
		}
		this.updateEnclosingBounds();
	}

	/**
	 * Updates the enclosing bounding box
	 */
	protected void updateEnclosingBounds() {
		if(this.boundingBoxes.isEmpty()) {
			this.enclosingBoundingBox = null;
		} else if(this.boundingBoxes.size() == 1) {
			this.enclosingBoundingBox = this.boundingBoxes.get(0);
		} else {
			AxisAlignedBB union = this.boundingBoxes.get(0);
			for(AxisAlignedBB box : this.boundingBoxes) {
				union = union.union(box);
			}
			this.enclosingBoundingBox = union;
		}
	}

	/**
	 * Returns a bounding box that encloses all bounds
	 * @return
	 */
	public AxisAlignedBB getEnclosingBounds() {
		return this.enclosingBoundingBox;
	}

	/**
	 * Links all chunks of this location
	 * @return
	 */
	public LocationStorage linkChunks() {
		for(AxisAlignedBB boundingBox : this.boundingBoxes) {
			int sx = MathHelper.floor(boundingBox.minX) / 16;
			int sz = MathHelper.floor(boundingBox.minZ) / 16;
			int ex = MathHelper.floor(boundingBox.maxX) / 16;
			int ez = MathHelper.floor(boundingBox.maxZ) / 16;
			for(int cx = sx; cx <= ex; cx++) {
				for(int cz = sz; cz <= ez; cz++) {
					Chunk chunk = this.getWorldStorage().getWorld().getChunkFromChunkCoords(cx, cz);
					this.linkChunk(chunk);
				}
			}
		}
		return this;
	}

	/**
	 * Returns the location guard. Must be created before
	 * {@link #readFromNBT(NBTTagCompound)} is called
	 * @return
	 */
	@Nullable
	public ILocationGuard getGuard() {
		return null;
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
		this.boundingBoxes.clear();
		NBTTagList boundingBoxes = nbt.getTagList("bounds", Constants.NBT.TAG_COMPOUND);
		for(int i = 0; i < boundingBoxes.tagCount(); i++) {
			NBTTagCompound boxNbt = boundingBoxes.getCompoundTagAt(i);
			double minX = boxNbt.getDouble("minX");
			double minY = boxNbt.getDouble("minY");
			double minZ = boxNbt.getDouble("minZ");
			double maxX = boxNbt.getDouble("maxX");
			double maxY = boxNbt.getDouble("maxY");
			double maxZ = boxNbt.getDouble("maxZ");
			this.boundingBoxes.add(new AxisAlignedBB(minX, minY, minZ, maxX, maxY, maxZ));
		}
		this.updateEnclosingBounds();
		this.type = EnumLocationType.fromName(nbt.getString("type"));
		this.layer = nbt.getInteger("layer");
		if(nbt.hasKey("ambience")) {
			NBTTagCompound ambienceTag = nbt.getCompoundTag("ambience");
			this.ambience = LocationAmbience.readFromNBT(this, ambienceTag);
		}
		this.visible = nbt.getBoolean("visible");
		this.locationSeed = nbt.getLong("seed");
	}

	/**
	 * Reads the guard data from NBT
	 * @param nbt
	 */
	public void readGuardNBT(NBTTagCompound nbt) {
		if(this.getGuard() != null) {
			this.getGuard().readFromNBT(nbt.getCompoundTag("guard"));
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		nbt.setString("name", this.name);
		NBTTagList boundingBoxes = new NBTTagList();
		for(AxisAlignedBB boundingBox : this.boundingBoxes) {
			NBTTagCompound boxNbt = new NBTTagCompound();
			boxNbt.setDouble("minX", boundingBox.minX);
			boxNbt.setDouble("minY", boundingBox.minY);
			boxNbt.setDouble("minZ", boundingBox.minZ);
			boxNbt.setDouble("maxX", boundingBox.maxX);
			boxNbt.setDouble("maxY", boundingBox.maxY);
			boxNbt.setDouble("maxZ", boundingBox.maxZ);
			boundingBoxes.appendTag(boxNbt);
		}
		nbt.setTag("bounds", boundingBoxes);
		nbt.setString("type", this.type.name);
		nbt.setInteger("layer", this.layer);
		if(this.hasAmbience()) {
			NBTTagCompound ambienceTag = new NBTTagCompound();
			this.ambience.writeToNBT(ambienceTag);
			nbt.setTag("ambience", ambienceTag);
		}
		nbt.setBoolean("visible", this.visible);
		nbt.setLong("seed", this.locationSeed);
		return nbt;
	}

	/**
	 * Writes the guard data to NBT
	 * @param nbt
	 * @return
	 */
	public NBTTagCompound writeGuardNBT(NBTTagCompound nbt) {
		if(this.getGuard() != null) {
			nbt.setTag("guard", this.getGuard().writeToNBT(new NBTTagCompound()));
		}
		return nbt;
	}

	/**
	 * Returns whether the specified entity is inside this location
	 * @param entity
	 * @return
	 */
	public boolean isInside(Entity entity) {
		return this.intersects(entity.getEntityBoundingBox());
	}

	/**
	 * Returns whether the specified position is inside this location
	 * @param pos
	 * @return
	 */
	public boolean isInside(Vec3i pos) {
		for(AxisAlignedBB boundingBox : this.boundingBoxes) {
			if(this.isVecInsideOrEdge(boundingBox, new Vec3d(pos.getX(), pos.getY(), pos.getZ()))) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns whether the specified position is inside this location
	 * @param pos
	 * @return
	 */
	public boolean isInside(Vec3d pos) {
		for(AxisAlignedBB boundingBox : this.boundingBoxes) {
			if(this.isVecInsideOrEdge(boundingBox, pos)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns whether this location intersects with the specified AABB
	 * @param aabb
	 * @return
	 */
	public boolean intersects(AxisAlignedBB aabb) {
		for(AxisAlignedBB boundingBox : this.boundingBoxes) {
			if(boundingBox.intersects(aabb)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns whether the vector touches the AABB or is fully inside
	 * @param aabb
	 * @param vec
	 * @return
	 */
	protected final boolean isVecInsideOrEdge(AxisAlignedBB aabb, Vec3d vec) {
		return vec.x >= aabb.minX && vec.x <= aabb.maxX ? (vec.y >= aabb.minY && vec.y <= aabb.maxY ? vec.z >= aabb.minZ && vec.z <= aabb.maxZ : false) : false;
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
		return getLocations(entity.world, entity.getEntityBoundingBox());
	}

	/**
	 * Returns a list of all locations at the specified position
	 * @param world
	 * @return
	 */
	public static List<LocationStorage> getLocations(World world, Vec3d position) {
		BetweenlandsWorldData worldStorage = BetweenlandsWorldData.forWorld(world);
		return worldStorage.getSharedStorageAt(LocationStorage.class, (location) -> {
			return location.isInside(position);
		}, position.x, position.z);
	}

	/**
	 * Returns a list of all locations that intersect the specified AABB
	 * @param world
	 * @param aabb
	 * @return
	 */
	public static List<LocationStorage> getLocations(World world, AxisAlignedBB aabb) {
		BetweenlandsWorldData worldStorage = BetweenlandsWorldData.forWorld(world);
		return worldStorage.getSharedStorageAt(LocationStorage.class, (location) -> {
			return location.intersects(aabb);
		}, aabb);
	}

	/**
	 * Returns the highest priority ambience at the specified position
	 * @param world
	 * @return
	 */
	public static LocationAmbience getAmbience(World world, Vec3d position) {
		List<LocationStorage> locations = LocationStorage.getLocations(world, position);
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
	 * Returns the highest priority ambience at the specified entity
	 * @param entity
	 * @return
	 */
	public static LocationAmbience getAmbience(Entity entity) {
		List<LocationStorage> locations = getLocations(entity);
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
	public static boolean isLocationGuarded(World world, @Nullable Entity entity, BlockPos pos) {
		List<LocationStorage> locations = getLocations(world, new Vec3d(pos));
		for(LocationStorage location : locations) {
			if(location.getGuard() != null && location.getGuard().isGuarded(world, entity, pos))
				return true;
		}
		return false;
	}
}
