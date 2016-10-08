package thebetweenlands.common.registries;

import net.minecraft.util.ResourceLocation;
import thebetweenlands.common.lib.ModInfo;
import thebetweenlands.common.world.storage.chunk.storage.ChunkStorage;
import thebetweenlands.common.world.storage.chunk.storage.locationold.GuardedLocationStorage;
import thebetweenlands.common.world.storage.chunk.storage.locationold.LocationStorage;
import thebetweenlands.common.world.storage.world.shared.LocationStorageNew;
import thebetweenlands.common.world.storage.world.shared.SharedStorage;

public class StorageRegistry {
	public static void preInit() {
		ChunkStorage.registerStorageType(new ResourceLocation(ModInfo.ID, "chunk_area"), LocationStorage.class);
		ChunkStorage.registerStorageType(new ResourceLocation(ModInfo.ID, "guarded_chunk_area"), GuardedLocationStorage.class);

		SharedStorage.registerStorageType(new ResourceLocation(ModInfo.ID, "location_storage"), LocationStorageNew.class);
	}
}
