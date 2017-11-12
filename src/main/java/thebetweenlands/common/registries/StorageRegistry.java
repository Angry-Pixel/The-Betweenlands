package thebetweenlands.common.registries;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import net.minecraft.util.ResourceLocation;
import thebetweenlands.api.storage.ILocalStorage;
import thebetweenlands.common.lib.ModInfo;
import thebetweenlands.common.world.storage.BetweenlandsLocalStorage;
import thebetweenlands.common.world.storage.location.LocationCragrockTower;
import thebetweenlands.common.world.storage.location.LocationGuarded;
import thebetweenlands.common.world.storage.location.LocationPortal;
import thebetweenlands.common.world.storage.location.LocationStorage;

public class StorageRegistry {
	private static final BiMap<ResourceLocation, Class<? extends BetweenlandsLocalStorage>> STORAGE_MAP = HashBiMap.<ResourceLocation, Class<? extends BetweenlandsLocalStorage>>create();

	public static void preInit() {
		STORAGE_MAP.put(new ResourceLocation(ModInfo.ID, "location_storage"), LocationStorage.class);
		STORAGE_MAP.put(new ResourceLocation(ModInfo.ID, "cragrock_tower"), LocationCragrockTower.class);
		STORAGE_MAP.put(new ResourceLocation(ModInfo.ID, "location_guarded"), LocationGuarded.class);
		STORAGE_MAP.put(new ResourceLocation(ModInfo.ID, "portal"), LocationPortal.class);
	}

	/**
	 * Returns the local storage class for the specified ID
	 * @param type
	 * @return
	 */
	public static Class<? extends BetweenlandsLocalStorage> getStorageType(ResourceLocation id) {
		return STORAGE_MAP.get(id);
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
