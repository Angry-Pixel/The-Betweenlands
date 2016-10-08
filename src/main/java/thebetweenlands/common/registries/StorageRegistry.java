package thebetweenlands.common.registries;

import net.minecraft.util.ResourceLocation;
import thebetweenlands.common.lib.ModInfo;
import thebetweenlands.common.world.storage.world.shared.SharedStorage;
import thebetweenlands.common.world.storage.world.shared.location.LocationStorage;

public class StorageRegistry {
	public static void preInit() {
		SharedStorage.registerStorageType(new ResourceLocation(ModInfo.ID, "location_storage"), LocationStorage.class);
	}
}
