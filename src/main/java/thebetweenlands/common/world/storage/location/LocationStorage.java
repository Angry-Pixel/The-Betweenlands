package thebetweenlands.common.world.storage.location;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.annotation.Nullable;

import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TObjectIntHashMap;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import thebetweenlands.api.network.IGenericDataManagerAccess;
import thebetweenlands.api.storage.IWorldStorage;
import thebetweenlands.api.storage.LocalRegion;
import thebetweenlands.api.storage.StorageID;
import thebetweenlands.common.network.datamanager.GenericDataManager;
import thebetweenlands.common.world.storage.BetweenlandsWorldStorage;
import thebetweenlands.common.world.storage.LocalStorageImpl;
import thebetweenlands.common.world.storage.location.guard.ILocationGuard;

public class LocationStorage extends LocalStorageImpl {
	private List<AxisAlignedBB> boundingBoxes = new ArrayList<>();
	private AxisAlignedBB enclosingBoundingBox;
	private EnumLocationType type;
	private int layer;
	private LocationAmbience ambience = null;
	private boolean inheritAmbience = true;
	private long locationSeed = 0L;

	protected GenericDataManager dataManager;

	private TObjectIntMap<Entity> titleDisplayCooldowns = new TObjectIntHashMap<Entity>();

	protected static final DataParameter<String> NAME = GenericDataManager.createKey(LocationStorage.class, DataSerializers.STRING);
	protected static final DataParameter<Boolean> VISIBLE = GenericDataManager.createKey(LocationStorage.class, DataSerializers.BOOLEAN);

	public LocationStorage(IWorldStorage worldStorage, StorageID id, @Nullable LocalRegion region) {
		this(worldStorage, id, region, "", EnumLocationType.NONE);
	}

	/**
	 * Creates a new location
	 * @param worldStorage
	 * @param id
	 */
	public LocationStorage(IWorldStorage worldStorage, StorageID id, @Nullable LocalRegion region, String name, EnumLocationType type) {
		super(worldStorage, id, region);

		this.dataManager = new GenericDataManager(this);

		this.dataManager.register(NAME, name);
		this.dataManager.register(VISIBLE, false);

		if(type == null) {
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
	 * @param boundingBoxes
	 * @return
	 */
	public LocationStorage addBounds(AxisAlignedBB... boundingBoxes) {
		for(AxisAlignedBB boundingBox : boundingBoxes) {
			this.boundingBoxes.add(boundingBox);
			this.markDirty();
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
			this.markDirty();
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

	@Override
	public void onAdded() {
		if(!this.getWorldStorage().getWorld().isRemote) {
			this.linkChunks();
		}
	}

	/**
	 * Links all chunks of this location.
	 * Called when the location is initially added to the world.
	 * If the location bounds are changed this needs to be called again manually.
	 * @return
	 */
	public LocationStorage linkChunks() {
		for(AxisAlignedBB boundingBox : this.boundingBoxes) {
			int sx = MathHelper.floor(boundingBox.minX) >> 4;
			int sz = MathHelper.floor(boundingBox.minZ) >> 4;
			int ex = MathHelper.floor(boundingBox.maxX) >> 4;
			int ez = MathHelper.floor(boundingBox.maxZ) >> 4;
			for(int cx = sx; cx <= ex; cx++) {
				for(int cz = sz; cz <= ez; cz++) {
					this.linkChunkSafely(new ChunkPos(cx, cz));
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
		this.markDirty();
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
		this.markDirty();
		return this;
	}

	/**
	 * Sets whether this location is visible
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
	public LocationStorage setAmbience(@Nullable LocationAmbience ambience) {
		if(ambience != null) {
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
		return I18n.translateToLocal("location." + this.dataManager.get(NAME) + ".name");
	}

	public boolean hasLocalizedName() {
		return I18n.canTranslate("location." + this.dataManager.get(NAME) + ".name");
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);

		this.readSharedNbt(nbt);
	}

	protected void readSharedNbt(NBTTagCompound nbt) {
		this.dataManager.set(NAME, nbt.getString("name"));
		if(this.dataManager.get(NAME).startsWith("translate:")) {
			this.dataManager.set(NAME, this.dataManager.get(NAME).replaceFirst("translate:", ""));
			this.setDirty(true);
		}
		this.boundingBoxes.clear();
		NBTTagList boundingBoxes = nbt.getTagList("bounds", Constants.NBT.TAG_COMPOUND);
		for(int i = 0; i < boundingBoxes.tagCount(); i++) {
			NBTTagCompound boxNbt = boundingBoxes.getCompoundTagAt(i);
			this.boundingBoxes.add(this.readAabb(boxNbt));
		}
		this.updateEnclosingBounds();
		this.type = EnumLocationType.fromName(nbt.getString("type"));
		this.layer = nbt.getInteger("layer");
		if(nbt.hasKey("ambience")) {
			NBTTagCompound ambienceTag = nbt.getCompoundTag("ambience");
			this.ambience = LocationAmbience.readFromNBT(this, ambienceTag);
		}
		this.dataManager.set(VISIBLE, nbt.getBoolean("visible"));
		this.locationSeed = nbt.getLong("seed");
	}

	protected NBTTagCompound writeAabb(AxisAlignedBB aabb) {
		NBTTagCompound boxNbt = new NBTTagCompound();
		boxNbt.setDouble("minX", aabb.minX);
		boxNbt.setDouble("minY", aabb.minY);
		boxNbt.setDouble("minZ", aabb.minZ);
		boxNbt.setDouble("maxX", aabb.maxX);
		boxNbt.setDouble("maxY", aabb.maxY);
		boxNbt.setDouble("maxZ", aabb.maxZ);
		return boxNbt;
	}

	protected AxisAlignedBB readAabb(NBTTagCompound nbt) {
		double minX = nbt.getDouble("minX");
		double minY = nbt.getDouble("minY");
		double minZ = nbt.getDouble("minZ");
		double maxX = nbt.getDouble("maxX");
		double maxY = nbt.getDouble("maxY");
		double maxZ = nbt.getDouble("maxZ");
		return new AxisAlignedBB(minX, minY, minZ, maxX, maxY, maxZ);
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

		this.writeSharedNbt(nbt);

		return nbt;
	}

	protected void writeSharedNbt(NBTTagCompound nbt) {
		nbt.setString("name", this.dataManager.get(NAME));
		NBTTagList boundingBoxes = new NBTTagList();
		for(AxisAlignedBB boundingBox : this.boundingBoxes) {
			boundingBoxes.appendTag(this.writeAabb(boundingBox));
		}
		nbt.setTag("bounds", boundingBoxes);
		nbt.setString("type", this.type.name);
		nbt.setInteger("layer", this.layer);
		if(this.hasAmbience()) {
			NBTTagCompound ambienceTag = new NBTTagCompound();
			this.ambience.writeToNBT(ambienceTag);
			nbt.setTag("ambience", ambienceTag);
		}
		nbt.setBoolean("visible", this.dataManager.get(VISIBLE));
		nbt.setLong("seed", this.locationSeed);
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

	@Override
	public void readInitialPacket(NBTTagCompound nbt) {
		super.readInitialPacket(nbt);
		this.readSharedNbt(nbt);
	}

	@Override
	public NBTTagCompound writeInitialPacket(NBTTagCompound nbt) {
		super.writeInitialPacket(nbt);
		this.writeSharedNbt(nbt);
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
		return this.dataManager.get(NAME);
	}

	/**
	 * Sets the location's name
	 * @param name
	 */
	public void setName(String name) {
		this.dataManager.set(NAME, name);
		this.markDirty();
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
		return this.dataManager.get(VISIBLE);
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
		BetweenlandsWorldStorage worldStorage = BetweenlandsWorldStorage.forWorld(world);
		return worldStorage.getLocalStorageHandler().getLocalStorages(LocationStorage.class, position.x, position.z,
				(location) -> {
					return location.isInside(position);
				});
	}

	/**
	 * Returns a list of all locations that intersect the specified AABB
	 * @param world
	 * @param aabb
	 * @return
	 */
	public static List<LocationStorage> getLocations(World world, AxisAlignedBB aabb) {
		BetweenlandsWorldStorage worldStorage = BetweenlandsWorldStorage.forWorld(world);
		return worldStorage.getLocalStorageHandler().getLocalStorages(LocationStorage.class, aabb, (location) -> {
			return location.intersects(aabb);
		});
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

	@Override
	public AxisAlignedBB getBoundingBox() {
		return this.enclosingBoundingBox;
	}

	/**
	 * Called when a block is broken inside the location
	 * @param event
	 * @return
	 */
	public void onBreakBlock(BreakEvent event) {

	}
}
