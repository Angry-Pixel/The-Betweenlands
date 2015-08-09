package thebetweenlands.world.biomes.base;

import thebetweenlands.utils.WeightedList;
import thebetweenlands.utils.confighandler.ConfigHandler;
import thebetweenlands.world.biomes.BiomeCoarseIslands;
import thebetweenlands.world.biomes.BiomeDeepWaters;
import thebetweenlands.world.biomes.BiomeMarsh;
import thebetweenlands.world.biomes.BiomePatchyIslands;
import thebetweenlands.world.biomes.BiomeSwampLands;
import thebetweenlands.world.biomes.feature.Marsh1NoiseFeature;
import thebetweenlands.world.biomes.feature.Marsh2NoiseFeature;

public class BLBiomeRegistry {
	public static WeightedList<BiomeGenBaseBetweenlands> biomeList = new WeightedList<BiomeGenBaseBetweenlands>();

	public static BiomeGenBaseBetweenlands swampLands;
	public static BiomeGenBaseBetweenlands coarseIslands;
	public static BiomeGenBaseBetweenlands deepWater;
	public static BiomeGenBaseBetweenlands patchyIslands;
	public static BiomeGenBaseBetweenlands marsh1;
	public static BiomeGenBaseBetweenlands marsh2;

	public static void init() {
		for (int id : new int[] { ConfigHandler.BIOME_ID_SWAMPLANDS, ConfigHandler.BIOME_ID_COARSE_ISLANDS, ConfigHandler.BIOME_ID_DEEP_WATER, ConfigHandler.BIOME_ID_PATCHY_ISLANDS, ConfigHandler.BIOME_ID_MARSH1, ConfigHandler.BIOME_ID_MARSH2 })
			if (id >= 128)
				throw new RuntimeException("Betweenlands biome IDs cannot be higher than 127!");

		// CREATE BIOMES
		swampLands = new BiomeSwampLands(ConfigHandler.BIOME_ID_SWAMPLANDS);
		coarseIslands = new BiomeCoarseIslands(ConfigHandler.BIOME_ID_COARSE_ISLANDS);
		deepWater = new BiomeDeepWaters(ConfigHandler.BIOME_ID_DEEP_WATER);
		patchyIslands = new BiomePatchyIslands(ConfigHandler.BIOME_ID_PATCHY_ISLANDS);
		marsh1 = new BiomeMarsh(ConfigHandler.BIOME_ID_MARSH1, "Marsh 1", new Marsh1NoiseFeature());
		marsh2 = new BiomeMarsh(ConfigHandler.BIOME_ID_MARSH2, "Marsh 2", new Marsh2NoiseFeature());

		swampLands.createMutation();
		coarseIslands.createMutation();
		deepWater.createMutation();
		patchyIslands.createMutation();
		marsh1.createMutation();
		marsh2.createMutation();
	}

}