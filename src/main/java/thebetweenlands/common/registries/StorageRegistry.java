package thebetweenlands.common.registries;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import net.minecraft.util.ResourceLocation;
import thebetweenlands.api.storage.ILocalStorage;
import thebetweenlands.api.storage.IWorldStorage;
import thebetweenlands.api.storage.LocalRegion;
import thebetweenlands.api.storage.StorageID;
import thebetweenlands.common.lib.ModInfo;
import thebetweenlands.common.world.storage.SharedLootPoolStorage;
import thebetweenlands.common.world.storage.location.LocationCragrockTower;
import thebetweenlands.common.world.storage.location.LocationGuarded;
import thebetweenlands.common.world.storage.location.LocationPortal;
import thebetweenlands.common.world.storage.location.LocationSludgeWormDungeon;
import thebetweenlands.common.world.storage.location.LocationSpiritTree;
import thebetweenlands.common.world.storage.location.LocationStorage;

public class StorageRegistry {
	private static final BiMap<ResourceLocation, Class<? extends ILocalStorage>> STORAGE_MAP = HashBiMap.<ResourceLocation, Class<? extends ILocalStorage>>create();
	private static final Map<ResourceLocation, Factory<?>> FACTORIES = new HashMap<>();

	public static interface Factory<T extends ILocalStorage> {
		public T create(IWorldStorage worldStorage, StorageID id, @Nullable LocalRegion region);
	}

	public static void preInit() {
		register(new ResourceLocation(ModInfo.ID, "shared_loot_pool_storage"), SharedLootPoolStorage.class, SharedLootPoolStorage::new);
		register(new ResourceLocation(ModInfo.ID, "location_storage"), LocationStorage.class, LocationStorage::new);
		register(new ResourceLocation(ModInfo.ID, "cragrock_tower"), LocationCragrockTower.class, LocationCragrockTower::new);
		register(new ResourceLocation(ModInfo.ID, "location_guarded"), LocationGuarded.class, LocationGuarded::new);
		register(new ResourceLocation(ModInfo.ID, "portal"), LocationPortal.class, LocationPortal::new);
		register(new ResourceLocation(ModInfo.ID, "spirit_tree"), LocationSpiritTree.class, LocationSpiritTree::new);
		register(new ResourceLocation(ModInfo.ID, "sludge_worm_dungeon"), LocationSludgeWormDungeon.class, LocationSludgeWormDungeon::new);
	}

	/**
	 * Registers a local storage
	 * @param cls
	 * @param id
	 * @param factory
	 */
	public static <T extends ILocalStorage> void register(ResourceLocation id, Class<T> cls, Factory<T> factory) {
		STORAGE_MAP.put(id, cls);
		FACTORIES.put(id, factory);
	}

	/**
	 * Returns the local storage class for the specified ID
	 * @param type
	 * @return
	 */
	public static Class<? extends ILocalStorage> getStorageType(ResourceLocation id) {
		return STORAGE_MAP.get(id);
	}

	/**
	 * Returns the factory for the specified id
	 * @param id
	 * @return
	 */
	public static Factory<? extends ILocalStorage> getStorageFactory(ResourceLocation id) {
		return FACTORIES.get(id);
	}

	/**
	 * Returns the ID of a local storage class
	 * @param storage
	 * @return
	 */
	public static ResourceLocation getStorageId(Class<? extends ILocalStorage> storage) {
		return STORAGE_MAP.inverse().get(storage);
	}
}
