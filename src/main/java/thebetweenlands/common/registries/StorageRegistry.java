package thebetweenlands.common.registries;

import net.minecraft.util.ResourceLocation;
import thebetweenlands.common.lib.ModInfo;
import thebetweenlands.common.world.gen.feature.tree.WorldGenGiantTree;
import thebetweenlands.common.world.storage.world.shared.SharedStorage;
import thebetweenlands.common.world.storage.world.shared.location.LocationCragrockTower;
import thebetweenlands.common.world.storage.world.shared.location.LocationGuarded;
import thebetweenlands.common.world.storage.world.shared.location.LocationStorage;

public class StorageRegistry {
	public static void preInit() {
		SharedStorage.registerStorageType(new ResourceLocation(ModInfo.ID, "location_storage"), LocationStorage.class);
		SharedStorage.registerStorageType(new ResourceLocation(ModInfo.ID, "giant_tree_chunk_marker"), WorldGenGiantTree.ChunkMaker.class);
		SharedStorage.registerStorageType(new ResourceLocation(ModInfo.ID, "cragrock_tower"), LocationCragrockTower.class);
		SharedStorage.registerStorageType(new ResourceLocation(ModInfo.ID, "location_guarded"), LocationGuarded.class);
	}
}
