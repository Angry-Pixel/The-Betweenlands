package thebetweenlands.common.world.storage.location;

import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.level.BlockEvent;
import org.jetbrains.annotations.Nullable;
import thebetweenlands.common.world.storage.LocalStorageImpl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class LocationStorage extends LocalStorageImpl {
	private List<AABB> boundingBoxes = new ArrayList<>();
	private AABB enclosingBoundingBox;
	private EnumLocationType type;
	private int layer;
	private LocationAmbience ambience = null;
	private boolean inheritAmbience = true;
	private long locationSeed = 0L;

	protected GenericDataManager dataManager;

	private Object2IntMap<Entity> titleDisplayCooldowns = new Object2IntArrayMap<>();

	protected static final DataParameter<String> NAME = GenericDataManager.createKey(LocationStorage.class, DataSerializers.STRING);
	protected static final DataParameter<Boolean> VISIBLE = GenericDataManager.createKey(LocationStorage.class, DataSerializers.BOOLEAN);

	public LocationStorage(IWorldStorage worldStorage, StorageID id, @Nullable LocalRegion region) {
		this(worldStorage, id, region, "", EnumLocationType.NONE);
	}

	/**
	 * Creates a new location
	 *
	 * @param worldStorage
	 * @param id
	 */
	public LocationStorage(IWorldStorage worldStorage, StorageID id, @Nullable LocalRegion region, String name, EnumLocationType type) {
		super(worldStorage, id, region);

		this.dataManager = new GenericDataManager(this);

		this.dataManager.register(NAME, name);
		this.dataManager.register(VISIBLE, false);

		if (type == null) {
			type = EnumLocationType.NONE;
		}

		this.type = type;
	}

	@Override
	public IGenericDataManagerAccess getDataManager() {
		return this.dataManager;
	}

	/**
	 * Adds the specified bounding boxes
	 *
	 * @param boundingBoxes
	 * @return
	 */
	public LocationStorage addBounds(AABB... boundingBoxes) {
		for (AABB boundingBox : boundingBoxes) {
			this.boundingBoxes.add(boundingBox);
			this.markDirty();
		}
		this.updateEnclosingBounds();
		return this;
	}

	/**
	 * Returns the bounding boxes of this location
	 *
	 * @return
	 */
	public List<AABB> getBounds() {
		return Collections.unmodifiableList(this.boundingBoxes);
	}

	/**
	 * Removes the specified bounding boxes
	 *
	 * @param boundingBoxes
	 */
	public void removeBounds(AABB... boundingBoxes) {
		for (AABB boundingBox : boundingBoxes) {
			this.boundingBoxes.remove(boundingBox);
			this.markDirty();
		}
		this.updateEnclosingBounds();
	}

	/**
	 * Updates the enclosing bounding box
	 */
	protected void updateEnclosingBounds() {
		if (this.boundingBoxes.isEmpty()) {
			this.enclosingBoundingBox = null;
		} else if (this.boundingBoxes.size() == 1) {
			this.enclosingBoundingBox = this.boundingBoxes.get(0);
		} else {
			AABB union = this.boundingBoxes.get(0);
			for (AABB box : this.boundingBoxes) {
				union = union.union(box);
			}
			this.enclosingBoundingBox = union;
		}
	}

	/**
	 * Returns a bounding box that encloses all bounds
	 *
	 * @return
	 */
	public AABB getEnclosingBounds() {
		return this.enclosingBoundingBox;
	}

	@Override
	public void onAdded() {
		if (!this.getWorldStorage().getWorld().isRemote) {
			this.linkChunks();
		}
	}

	/**
	 * Links all chunks of this location.
	 * Called when the location is initially added to the world.
	 * If the location bounds are changed this needs to be called again manually.
	 *
	 * @return
	 */
	public LocationStorage linkChunks() {
		for (AABB boundingBox : this.boundingBoxes) {
			int sx = Mth.floor(boundingBox.minX) >> 4;
			int sz = Mth.floor(boundingBox.minZ) >> 4;
			int ex = Mth.floor(boundingBox.maxX) >> 4;
			int ez = Mth.floor(boundingBox.maxZ) >> 4;
			for (int cx = sx; cx <= ex; cx++) {
				for (int cz = sz; cz <= ez; cz++) {
					this.linkChunkSafely(new ChunkPos(cx, cz));
				}
			}
		}
		return this;
	}

	/**
	 * Returns the location guard. Must be created before
	 * {@link #readFromNBT(CompoundTag)} is called
	 *
	 * @return
	 */
	@Nullable
	public ILocationGuard getGuard() {
		return null;
	}

	/**
	 * Sets the location seed
	 *
	 * @param seed
	 * @return
	 */
	public LocationStorage setSeed(long seed) {
		this.locationSeed = seed;
		this.markDirty();
		return this;
	}

	/**
	 * Returns the location seed
	 *
	 * @return
	 */
	public long getSeed() {
		return this.locationSeed;
	}

	/**
	 * Sets whether this location should inherit ambiences from lower layers
	 *
	 * @param inherit
	 * @return
	 */
	public LocationStorage setInheritAmbience(boolean inherit) {
		this.inheritAmbience = inherit;
		this.ambience = null;
		this.markDirty();
		return this;
	}

	/**
	 * Sets whether this location is visible
	 *
	 * @param visible
	 * @return
	 */
	public LocationStorage setVisible(boolean visible) {
		this.dataManager.set(VISIBLE, visible);
		this.markDirty();
		return this;
	}

	/**
	 * Sets the location layer
	 *
	 * @param layer
	 * @return
	 */
	public LocationStorage setLayer(int layer) {
		this.layer = layer;
		this.markDirty();
		return this;
	}

	/**
	 * Returns the location layer
	 *
	 * @return
	 */
	public int getLayer() {
		return this.layer;
	}

	/**
	 * Sets the location ambience
	 *
	 * @param ambience
	 * @return
	 */
	public LocationStorage setAmbience(@Nullable LocationAmbience ambience) {
		if (ambience != null) {
			this.inheritAmbience = false;
			this.ambience = ambience;
			ambience.setLocation(this);
		} else {
			this.ambience = null;
		}
		this.markDirty();
		return this;
	}

	/**
	 * Returns whether the location has an ambience
	 *
	 * @return
	 */
	public boolean hasAmbience() {
		return this.ambience != null;
	}

	/**
	 * Returns the localized name of this location
	 *
	 * @return
	 */
	public String getLocalizedName() {
		return I18n.get("location." + this.dataManager.get(NAME) + ".name");
	}

	public boolean hasLocalizedName() {
		return I18n.exists("location." + this.dataManager.get(NAME) + ".name");
	}

	@Override
	public void readFromNBT(CompoundTag nbt) {
		super.readFromNBT(nbt);

		this.readSharedNbt(nbt);
	}

	protected void readSharedNbt(CompoundTag nbt) {
		this.dataManager.set(NAME, nbt.getString("name"));
		if (this.dataManager.get(NAME).startsWith("translate:")) {
			this.dataManager.set(NAME, this.dataManager.get(NAME).replaceFirst("translate:", ""));
			this.setDirty(true);
		}
		this.boundingBoxes.clear();
		ListTag boundingBoxes = nbt.getList("bounds", Tag.TAG_COMPOUND);
		for (int i = 0; i < boundingBoxes.size(); i++) {
			CompoundTag boxNbt = boundingBoxes.getCompound(i);
			this.boundingBoxes.add(this.readAabb(boxNbt));
		}
		this.updateEnclosingBounds();
		this.type = EnumLocationType.fromName(nbt.getString("type"));
		this.layer = nbt.getInt("layer");
		if (nbt.contains("ambience")) {
			CompoundTag ambienceTag = nbt.getCompound("ambience");
			this.ambience = LocationAmbience.readFromNBT(this, ambienceTag);
		}
		this.dataManager.set(VISIBLE, nbt.getBoolean("visible"));
		this.locationSeed = nbt.getLong("seed");
	}

	protected CompoundTag writeAabb(AABB aabb) {
		CompoundTag boxNbt = new CompoundTag();
		boxNbt.putDouble("minX", aabb.minX);
		boxNbt.putDouble("minY", aabb.minY);
		boxNbt.putDouble("minZ", aabb.minZ);
		boxNbt.putDouble("maxX", aabb.maxX);
		boxNbt.putDouble("maxY", aabb.maxY);
		boxNbt.putDouble("maxZ", aabb.maxZ);
		return boxNbt;
	}

	protected AABB readAabb(CompoundTag nbt) {
		double minX = nbt.getDouble("minX");
		double minY = nbt.getDouble("minY");
		double minZ = nbt.getDouble("minZ");
		double maxX = nbt.getDouble("maxX");
		double maxY = nbt.getDouble("maxY");
		double maxZ = nbt.getDouble("maxZ");
		return new AABB(minX, minY, minZ, maxX, maxY, maxZ);
	}

	/**
	 * Reads the guard data from NBT
	 *
	 * @param nbt
	 */
	public void readGuardNBT(CompoundTag nbt) {
		if (this.getGuard() != null) {
			this.getGuard().readFromNBT(nbt.getCompound("guard"));
		}
	}

	@Override
	public CompoundTag writeToNBT(CompoundTag nbt) {
		super.writeToNBT(nbt);

		this.writeSharedNbt(nbt);

		return nbt;
	}

	protected void writeSharedNbt(CompoundTag nbt) {
		nbt.putString("name", this.dataManager.get(NAME));
		ListTag boundingBoxes = new ListTag();
		for (AABB boundingBox : this.boundingBoxes) {
			boundingBoxes.appendTag(this.writeAabb(boundingBox));
		}
		nbt.put("bounds", boundingBoxes);
		nbt.putString("type", this.type.name);
		nbt.putInt("layer", this.layer);
		if (this.hasAmbience()) {
			CompoundTag ambienceTag = new CompoundTag();
			this.ambience.writeToNBT(ambienceTag);
			nbt.put("ambience", ambienceTag);
		}
		nbt.putBoolean("visible", this.dataManager.get(VISIBLE));
		nbt.putLong("seed", this.locationSeed);
	}

	/**
	 * Writes the guard data to NBT
	 *
	 * @param nbt
	 * @return
	 */
	public CompoundTag writeGuardNBT(CompoundTag nbt) {
		if (this.getGuard() != null) {
			nbt.put("guard", this.getGuard().writeToNBT(new CompoundTag()));
		}
		return nbt;
	}

	@Override
	public void readInitialPacket(CompoundTag nbt) {
		super.readInitialPacket(nbt);
		this.readSharedNbt(nbt);
	}

	@Override
	public CompoundTag writeInitialPacket(CompoundTag nbt) {
		super.writeInitialPacket(nbt);
		this.writeSharedNbt(nbt);
		return nbt;
	}

	/**
	 * Returns whether the specified entity is inside this location
	 *
	 * @param entity
	 * @return
	 */
	public boolean isInside(Entity entity) {
		return this.intersects(entity.getBoundingBox());
	}

	/**
	 * Returns whether the specified position is inside this location
	 *
	 * @param pos
	 * @return
	 */
	public boolean isInside(Vec3i pos) {
		for (AABB boundingBox : this.boundingBoxes) {
			if (this.isVecInsideOrEdge(boundingBox, new Vec3(pos.getX(), pos.getY(), pos.getZ()))) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns whether the specified position is inside this location
	 *
	 * @param pos
	 * @return
	 */
	public boolean isInside(Vec3 pos) {
		for (AABB boundingBox : this.boundingBoxes) {
			if (this.isVecInsideOrEdge(boundingBox, pos)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns whether this location intersects with the specified AABB
	 *
	 * @param aabb
	 * @return
	 */
	public boolean intersects(AABB aabb) {
		for (AABB boundingBox : this.boundingBoxes) {
			if (boundingBox.intersects(aabb)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns whether the vector touches the AABB or is fully inside
	 *
	 * @param aabb
	 * @param vec
	 * @return
	 */
	protected final boolean isVecInsideOrEdge(AABB aabb, Vec3 vec) {
		return vec.x >= aabb.minX && vec.x <= aabb.maxX && (vec.y >= aabb.minY && vec.y <= aabb.maxY && vec.z >= aabb.minZ && vec.z <= aabb.maxZ);
	}

	/**
	 * Returns the location name
	 *
	 * @return
	 */
	public String getName() {
		return this.dataManager.get(NAME);
	}

	/**
	 * Sets the location's name
	 *
	 * @param name
	 */
	public void setName(String name) {
		this.dataManager.set(NAME, name);
		this.markDirty();
	}

	/**
	 * Returns the location type
	 *
	 * @return
	 */
	public EnumLocationType getType() {
		return this.type;
	}

	/**
	 * Returns whether the location is visible and has a title
	 *
	 * @param entity
	 * @return
	 */
	public boolean isVisible(Entity entity) {
		return this.dataManager.get(VISIBLE);
	}

	private static final Comparator<LocationStorage> LAYER_SORTER = Comparator.comparingInt(LocationStorage::getLayer);

	/**
	 * Returns a list of all locations at the specified entity
	 *
	 * @param entity
	 * @return
	 */
	public static List<LocationStorage> getLocations(Entity entity) {
		return getLocations(entity.level(), entity.getBoundingBox());
	}

	/**
	 * Returns a list of all locations at the specified position
	 *
	 * @param world
	 * @return
	 */
	public static List<LocationStorage> getLocations(Level level, Vec3 position) {
		BetweenlandsWorldStorage worldStorage = BetweenlandsWorldStorage.forWorld(level);
		return worldStorage.getLocalStorageHandler().getLocalStorages(LocationStorage.class, position.x, position.z, (location) -> location.isInside(position));
	}

	/**
	 * Returns a list of all locations that intersect the specified AABB
	 *
	 * @param world
	 * @param aabb
	 * @return
	 */
	public static List<LocationStorage> getLocations(Level level, AABB aabb) {
		BetweenlandsWorldStorage worldStorage = BetweenlandsWorldStorage.forWorld(level);
		return worldStorage.getLocalStorageHandler().getLocalStorages(LocationStorage.class, aabb, (location) -> location.intersects(aabb));
	}

	/**
	 * Returns the highest priority ambience at the specified position
	 *
	 * @param world
	 * @return
	 */
	public static LocationAmbience getAmbience(Level level, Vec3 position) {
		List<LocationStorage> locations = LocationStorage.getLocations(level, position);
		if (locations.isEmpty())
			return null;
		Collections.sort(locations, LAYER_SORTER);
		LocationStorage highestLocation = null;
		for (int i = 0; i < locations.size(); i++) {
			LocationStorage storage = locations.get(i);
			if (storage.hasAmbience() || storage.inheritAmbience)
				highestLocation = storage;
		}
		if (highestLocation != null) {
			if (highestLocation.ambience == null && highestLocation.inheritAmbience) {
				for (int i = 0; i < locations.size(); i++) {
					LocationStorage storage = locations.get(i);
					if (storage.hasAmbience())
						return storage.ambience;
				}
			}
			return highestLocation.ambience;
		}
		return null;
	}

	/**
	 * Returns the highest priority ambience at the specified entity
	 *
	 * @param entity
	 * @return
	 */
	public static LocationAmbience getAmbience(Entity entity) {
		List<LocationStorage> locations = getLocations(entity);
		if (locations.isEmpty())
			return null;
		Collections.sort(locations, LAYER_SORTER);
		LocationStorage highestLocation = null;
		for (int i = 0; i < locations.size(); i++) {
			LocationStorage storage = locations.get(i);
			if (storage.hasAmbience() || storage.inheritAmbience)
				highestLocation = storage;
		}
		if (highestLocation != null) {
			if (highestLocation.ambience == null && highestLocation.inheritAmbience) {
				for (int i = 0; i < locations.size(); i++) {
					LocationStorage storage = locations.get(i);
					if (storage.hasAmbience())
						return storage.ambience;
				}
			}
			return highestLocation.ambience;
		}
		return null;
	}

	/**
	 * Returns whether the entity is in the specified location type
	 *
	 * @param entity
	 * @param type
	 * @return
	 */
	public static boolean isInLocationType(Entity entity, EnumLocationType type) {
		List<LocationStorage> locations = getLocations(entity);
		for (LocationStorage location : locations) {
			if (location.getType() == type)
				return true;
		}
		return false;
	}

	/**
	 * Returns whether the location at the entity position is guarded
	 *
	 * @param entity
	 * @return
	 */
	public static boolean isLocationGuarded(Level level, @Nullable Entity entity, BlockPos pos) {
		List<LocationStorage> locations = getLocations(level, pos.getCenter());
		for (LocationStorage location : locations) {
			if (location.getGuard() != null && location.getGuard().isGuarded(level, entity, pos))
				return true;
		}
		return false;
	}

	@Override
	public AABB getBoundingBox() {
		return this.enclosingBoundingBox;
	}

	/**
	 * Called when a block is broken inside the location
	 *
	 * @param event
	 * @return
	 */
	public void onBreakBlock(BlockEvent.BreakEvent event) {

	}
}