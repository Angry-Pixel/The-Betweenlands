package thebetweenlands.common.world.storage.world.shared;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityDispatcher;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import thebetweenlands.common.event.AttachSharedStorageCapabilitiesEvent;
import thebetweenlands.common.world.storage.chunk.storage.shared.SharedStorageReference;

public abstract class SharedStorage implements ICapabilityProvider {
	private static final Map<ResourceLocation, Class<? extends SharedStorage>> STORAGE_MAP = new HashMap<ResourceLocation, Class<? extends SharedStorage>>();

	/**
	 * Returns the chunk storage class for the specified ID
	 * @param type
	 * @return
	 */
	public static Class<? extends SharedStorage> getStorageType(ResourceLocation id) {
		return STORAGE_MAP.get(id);
	}

	/**
	 * Returns the chunk storage ID for the specified class
	 * @param storageClass
	 * @return
	 */
	public static ResourceLocation getStorageTypeID(Class<? extends SharedStorage> storageClass) {
		for(Entry<ResourceLocation, Class<? extends SharedStorage>> entry : STORAGE_MAP.entrySet()) {
			if(storageClass.equals(entry.getValue()))
				return entry.getKey();
		}
		return null;
	}

	/**
	 * Registers a chunk storage type
	 * @param id
	 * @param storageClass
	 */
	public static void registerStorageType(ResourceLocation id, Class<? extends SharedStorage> storageClass) {
		if(STORAGE_MAP.containsKey(id))
			throw new RuntimeException("Duplicate shared storage ID");
		STORAGE_MAP.put(id, storageClass);
	}

	/**
	 * Loads a shared storage from NBT
	 * @param nbt
	 * @return
	 */
	public static SharedStorage load(NBTTagCompound nbt) {
		try {
			ResourceLocation type = new ResourceLocation(nbt.getString("type"));
			Class<? extends SharedStorage> storageClass = SharedStorage.getStorageType(type);
			if (storageClass == null)
				throw new Exception("Shared storage type not mapped!");
			Constructor<? extends SharedStorage> ctor = storageClass.getConstructor(UUID.class);
			UUID uuid = nbt.getUniqueId("uuid");
			SharedStorage storage = ctor.newInstance(uuid);
			storage.readFromNBT(nbt.getCompoundTag("storage"));
			return storage;
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	/**
	 * Saves a shared storage to NBT
	 * @param storage
	 * @param nbt
	 */
	public static void save(SharedStorage storage, NBTTagCompound nbt) {
		ResourceLocation type = SharedStorage.getStorageTypeID(storage.getClass());
		if (type == null)
			throw new RuntimeException("Shared storage type not mapped!");
		nbt.setString("type", type.toString());
		nbt.setUniqueId("uuid", storage.uuid);
		NBTTagCompound storageNBT = new NBTTagCompound();
		storage.writeToNBT(storageNBT);
		nbt.setTag("storage", storageNBT);
	}

	private final List<SharedStorageReference> references = new ArrayList<>();
	private CapabilityDispatcher capabilities;
	private final List<EntityPlayerMP> watchers = new ArrayList<EntityPlayerMP>();
	private boolean dirty = false;
	private final UUID uuid;
	private final String uuidStr;

	public SharedStorage(UUID uuid) {
		this.uuid = uuid;
		this.uuidStr = uuid.toString();

		//Gather capabilities
		AttachCapabilitiesEvent event = new AttachSharedStorageCapabilitiesEvent(this);
		MinecraftForge.EVENT_BUS.post(event);
		this.capabilities = event.getCapabilities().size() > 0 ? new CapabilityDispatcher(event.getCapabilities(), null) : null;
	}

	/**
	 * Returns the UUID
	 * @return
	 */
	public UUID getUniqueID() {
		return this.uuid;
	}

	/**
	 * Returns the UUID String
	 * @return
	 */
	public String getUniqueIDString() {
		return this.uuidStr;
	}

	/**
	 * Sets whether the data is dirty
	 */
	public void setDirty(boolean dirty) {
		this.dirty = dirty;
	}

	/**
	 * Returns whether the data needs to be saved
	 * @return
	 */
	public boolean isDirty() {
		return this.dirty;
	}

	/**
	 * Called when a reference is unloaded
	 */
	public void unloadReference(SharedStorageReference reference) {
		this.references.remove(reference);
	}

	/**
	 * Called when a reference is loaded
	 * @param reference
	 */
	public void loadReference(SharedStorageReference reference) {
		this.references.add(reference);
	}

	/**
	 * Called when the shared storage is unloaded
	 */
	public void onUnload() {

	}

	/**
	 * Called when the shared storage is watched by a player
	 * @param player
	 */
	public void onWatched(EntityPlayerMP player) {
		this.watchers.add(player);
	}

	/**
	 * Called when the storage is unwatched by a player
	 * @param player
	 */
	public void onUnwatched(EntityPlayerMP player) {
		this.watchers.remove(player);
	}

	/**
	 * Returns the list of all watchers
	 * @return
	 */
	public List<EntityPlayerMP> getWatchers() {
		return this.watchers;
	}

	/**
	 * Returns the list of all references
	 * @return
	 */
	public List<SharedStorageReference> getReferences() {
		return this.references;
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		return this.capabilities == null ? false : this.capabilities.hasCapability(capability, facing);
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		return this.capabilities == null ? null : this.capabilities.getCapability(capability, facing);
	}

	/**
	 * Reads the data from the NBT
	 * @param nbt
	 */
	public void readFromNBT(NBTTagCompound nbt) {
		if (this.capabilities != null && nbt.hasKey("ForgeCaps")) this.capabilities.deserializeNBT(nbt.getCompoundTag("ForgeCaps"));
	}

	/**
	 * Writes the data to the NBT
	 * @param nbt
	 */
	public void writeToNBT(NBTTagCompound nbt) {
		if (this.capabilities != null) nbt.setTag("ForgeCaps", this.capabilities.serializeNBT());
	}

	/**
	 * Reads the data from the packet NBT
	 * @param nbt
	 */
	public void readFromPacketNBT(NBTTagCompound nbt) {
		this.readFromNBT(nbt);
		//TODO: Read currently loaded references from nbt and add them if their chunks are loaded on the client side (this side)
	}

	/**
	 * Writes the data to the packet NBT
	 * @param nbt
	 */
	public void writeToPacketNBT(NBTTagCompound nbt) {
		this.writeToNBT(nbt);
		//TODO: Write currently loaded references to nbt
	}
}
