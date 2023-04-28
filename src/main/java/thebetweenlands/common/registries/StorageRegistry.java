package thebetweenlands.common.registries;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import net.minecraft.util.ResourceLocation;
import thebetweenlands.api.storage.IDeferredStorageOperation;
import thebetweenlands.api.storage.ILocalStorage;
import thebetweenlands.api.storage.IWorldStorage;
import thebetweenlands.api.storage.LocalRegion;
import thebetweenlands.api.storage.StorageID;
import thebetweenlands.common.lib.ModInfo;
import thebetweenlands.common.world.storage.SharedLootPoolStorage;
import thebetweenlands.common.world.storage.location.LocationChiromawMatriarchNest;
import thebetweenlands.common.world.storage.location.LocationCragrockTower;
import thebetweenlands.common.world.storage.location.LocationGuarded;
import thebetweenlands.common.world.storage.location.LocationMobArenaProtection;
import thebetweenlands.common.world.storage.location.LocationPortal;
import thebetweenlands.common.world.storage.location.LocationSludgeWormDungeon;
import thebetweenlands.common.world.storage.location.LocationSpiritTree;
import thebetweenlands.common.world.storage.location.LocationSporeHive;
import thebetweenlands.common.world.storage.location.LocationStorage;
import thebetweenlands.common.world.storage.location.LocationTokenBucket;
import thebetweenlands.common.world.storage.operation.DeferredLinkOperation;
import thebetweenlands.common.world.storage.operation.DeferredLinkOperationWithMetadata;

public class StorageRegistry {
	private static final BiMap<ResourceLocation, Class<? extends ILocalStorage>> STORAGE_MAP = HashBiMap.create();
	private static final Map<ResourceLocation, Factory<?>> FACTORIES = new HashMap<>();

	private static final BiMap<ResourceLocation, Class<? extends IDeferredStorageOperation>> DEFERRED_MAP = HashBiMap.create();
	private static final Map<ResourceLocation, Supplier<? extends IDeferredStorageOperation>> DEFERRED_FACTORIES = new HashMap<>();
	
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
		register(new ResourceLocation(ModInfo.ID, "chiromaw_matriarch_nest"), LocationChiromawMatriarchNest.class, LocationChiromawMatriarchNest::new);
		register(new ResourceLocation(ModInfo.ID, "token_bucket"), LocationTokenBucket.class, LocationTokenBucket::new);
		register(new ResourceLocation(ModInfo.ID, "mob_arena_protection"), LocationMobArenaProtection.class, LocationMobArenaProtection::new);
		register(new ResourceLocation(ModInfo.ID, "spore_hive"), LocationSporeHive.class, LocationSporeHive::new);
		
		register(new ResourceLocation(ModInfo.ID, "deferred_link"), DeferredLinkOperation.class, DeferredLinkOperation::new);
		register(new ResourceLocation(ModInfo.ID, "deferred_link_with_meta"), DeferredLinkOperationWithMetadata.class, DeferredLinkOperationWithMetadata::new);
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
	@Nullable
	public static Class<? extends ILocalStorage> getStorageType(ResourceLocation id) {
		return STORAGE_MAP.get(id);
	}

	/**
	 * Returns the factory for the specified id
	 * @param id
	 * @return
	 */
	@Nullable
	public static Factory<? extends ILocalStorage> getStorageFactory(ResourceLocation id) {
		return FACTORIES.get(id);
	}

	/**
	 * Returns the ID of a local storage class
	 * @param storage
	 * @return
	 */
	@Nullable
	public static ResourceLocation getStorageId(Class<? extends ILocalStorage> storage) {
		return STORAGE_MAP.inverse().get(storage);
	}
	
	/**
	 * Registers a deferred storage operation
	 * @param cls
	 * @param id
	 * @param factory
	 */
	public static <T extends IDeferredStorageOperation> void register(ResourceLocation id, Class<T> cls, Supplier<T> factory) {
		DEFERRED_MAP.put(id, cls);
		DEFERRED_FACTORIES.put(id, factory);
	}
	
	/**
	 * Returns the local storage class for the specified ID
	 * @param type
	 * @return
	 */
	@Nullable
	public static Class<? extends IDeferredStorageOperation> getDeferredOperationType(ResourceLocation id) {
		return DEFERRED_MAP.get(id);
	}

	/**
	 * Returns the factory for the specified id
	 * @param id
	 * @return
	 */
	@Nullable
	public static Supplier<? extends IDeferredStorageOperation> getDeferredOperationFactory(ResourceLocation id) {
		return DEFERRED_FACTORIES.get(id);
	}

	/**
	 * Returns the ID of a deferred storage operation class
	 * @param operation
	 * @return
	 */
	@Nullable
	public static ResourceLocation getDeferredOperationId(Class<? extends IDeferredStorageOperation> operation) {
		return DEFERRED_MAP.inverse().get(operation);
	}
}
