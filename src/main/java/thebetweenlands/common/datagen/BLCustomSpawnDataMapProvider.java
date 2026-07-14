package thebetweenlands.common.datagen;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.biome.Biome;
import net.neoforged.neoforge.common.data.DataMapProvider;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.datagen.tags.BLBiomeTagProvider;
import thebetweenlands.common.registries.BiomeRegistry;
import thebetweenlands.common.registries.DataMapRegistry;
import thebetweenlands.common.registries.EntityRegistry;
import thebetweenlands.common.world.spawning.BaseSpawnProperties;
import thebetweenlands.common.world.spawning.SurfaceSpawnEntry;

public class BLCustomSpawnDataMapProvider extends DataMapProvider {
	private final CompletableFuture<HolderLookup.Provider> lookupProviderRef;

	public BLCustomSpawnDataMapProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
		super(output, lookupProvider);
		this.lookupProviderRef = lookupProvider;
	}

	@Override
	protected void gather() {
		HolderLookup.Provider registries = this.lookupProviderRef.join();  
		var biomeLookup = registries.lookupOrThrow(Registries.BIOME);

		// Single biome for ref (redundant if we use the tags instead)
		HolderSet<Biome> singleBiome = HolderSet.direct(biomeLookup.getOrThrow(BiomeRegistry.PATCHY_ISLANDS));

		// List of specific biomes for ref
		HolderSet<Biome> biomeList = HolderSet.direct(
			biomeLookup.getOrThrow(BiomeRegistry.PATCHY_ISLANDS),
			biomeLookup.getOrThrow(BiomeRegistry.SWAMPLANDS)
		);

		// Single tags for ref
		HolderSet<Biome> patchy_islands = biomeLookup.getOrThrow(BLBiomeTagProvider.PATCHY_ISLANDS);
		HolderSet<Biome> swamplands = biomeLookup.getOrThrow(BLBiomeTagProvider.SWAMPLANDS);
		HolderSet<Biome> deep_waters = biomeLookup.getOrThrow(BLBiomeTagProvider.DEEP_WATERS);
		HolderSet<Biome> coarse_islands = biomeLookup.getOrThrow(BLBiomeTagProvider.COARSE_ISLANDS);
		HolderSet<Biome> raised_isles = biomeLookup.getOrThrow(BLBiomeTagProvider.RAISED_ISLES);
		HolderSet<Biome> sludge_plains = biomeLookup.getOrThrow(BLBiomeTagProvider.SLUDGE_PLAINS);
		HolderSet<Biome> eroded_marsh = biomeLookup.getOrThrow(BLBiomeTagProvider.ERODED_MARSH);
		HolderSet<Biome> marsh = biomeLookup.getOrThrow(BLBiomeTagProvider.MARSH);
		HolderSet<Biome> swamplands_clearing = biomeLookup.getOrThrow(BLBiomeTagProvider.SWAMPLANDS_CLEARING);
		HolderSet<Biome> sludge_plains_clearing = biomeLookup.getOrThrow(BLBiomeTagProvider.SLUDGE_PLAINS_CLEARING);

		var baseNumbers = new BaseSpawnProperties(biomeList, (short) 20, (short) 100, true, 2, 4, -64, 0, 16.0, 8.0, 4.0, 20);

		// Dragonfly setup
		var dragonfly = new BaseSpawnProperties(patchy_islands, (short) 100, (short) 30, false, 1, 3, TheBetweenlands.CAVE_START, TheBetweenlands.LAYER_HEIGHT, 64, 6, 6, 400);

		builder(DataMapRegistry.CUSTOM_SPAWNS).add(EntityRegistry.DRAGONFLY, List.of(new SurfaceSpawnEntry(dragonfly, true, false)), false);

	}
}
