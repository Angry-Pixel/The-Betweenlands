package thebetweenlands.common.registries;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import thebetweenlands.api.storage.*;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.world.storage.location.*;
import thebetweenlands.common.world.storage.operation.DeferredLinkOperation;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

//TODO make into proper registry
public class StorageRegistry {
	private static final BiMap<ResourceLocation, Class<? extends ILocalStorage>> STORAGE_MAP = HashBiMap.create();
	private static final Map<ResourceLocation, Factory<?>> FACTORIES = new HashMap<>();

	private static final BiMap<ResourceLocation, Class<? extends IDeferredStorageOperation>> DEFERRED_MAP = HashBiMap.create();
	private static final Map<ResourceLocation, Supplier<? extends IDeferredStorageOperation>> DEFERRED_FACTORIES = new HashMap<>();

	public interface Factory<T extends ILocalStorage> {
		T create(IWorldStorage worldStorage, StorageID id, @Nullable LocalRegion region);
	}

	public static void preInit() {
		//register(TheBetweenlands.prefix("shared_loot_pool_storage"), SharedLootPoolStorage.class, SharedLootPoolStorage::new);
		register(TheBetweenlands.prefix("location_storage"), LocationStorage.class, LocationStorage::new);
		register(TheBetweenlands.prefix("cragrock_tower"), LocationCragrockTower.class, LocationCragrockTower::new);
		register(TheBetweenlands.prefix("location_guarded"), LocationGuarded.class, LocationGuarded::new);
		register(TheBetweenlands.prefix("portal"), LocationPortal.class, LocationPortal::new);
		register(TheBetweenlands.prefix("spirit_tree"), LocationSpiritTree.class, LocationSpiritTree::new);
		register(TheBetweenlands.prefix("sludge_worm_dungeon"), LocationSludgeWormDungeon.class, LocationSludgeWormDungeon::new);
		register(TheBetweenlands.prefix("chiromaw_matriarch_nest"), LocationChiromawMatriarchNest.class, LocationChiromawMatriarchNest::new);
		register(TheBetweenlands.prefix("token_bucket"), LocationTokenBucket.class, LocationTokenBucket::new);

		register(TheBetweenlands.prefix("deferred_link"), DeferredLinkOperation.class, DeferredLinkOperation::new);
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
	 * @param id
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
	 * @param id
	 * @return
	 */
	public static Class<? extends IDeferredStorageOperation> getDeferredOperationType(ResourceLocation id) {
		return DEFERRED_MAP.get(id);
	}

	/**
	 * Returns the factory for the specified id
	 * @param id
	 * @return
	 */
	public static Supplier<? extends IDeferredStorageOperation> getDeferredOperationFactory(ResourceLocation id) {
		return DEFERRED_FACTORIES.get(id);
	}

	/**
	 * Returns the ID of a deferred storage operation class
	 * @param operation
	 * @return
	 */
	public static ResourceLocation getDeferredOperationId(Class<? extends IDeferredStorageOperation> operation) {
		return DEFERRED_MAP.inverse().get(operation);
	}
}
