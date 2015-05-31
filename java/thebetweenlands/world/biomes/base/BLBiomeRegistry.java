package thebetweenlands.world.biomes.base;

import thebetweenlands.utils.confighandler.ConfigHandler;
import thebetweenlands.world.biomes.*;
import thebetweenlands.world.biomes.feature.Marsh1NoiseFeature;
import thebetweenlands.world.biomes.feature.Marsh2NoiseFeature;
import thebetweenlands.world.genlayer.GenLayerBetweenlandsBiome;

public class BLBiomeRegistry
{
	public static BiomeGenBaseBetweenlands swampLands;
	public static BiomeGenBaseBetweenlands coarseIslands;
	public static BiomeGenBaseBetweenlands deepWater;
	public static BiomeGenBaseBetweenlands patchyIslands;
	public static BiomeGenBaseBetweenlands marsh1;
	public static BiomeGenBaseBetweenlands marsh2;
	
	public static void init() {
		swampLands = new BiomeSwampLands(ConfigHandler.BIOME_ID_SWAMPLANDS);
		coarseIslands = new BiomeCoarseIslands(ConfigHandler.BIOME_ID_COARSE_ISLANDS);
		deepWater = new BiomeDeepWaters(ConfigHandler.BIOME_ID_DEEP_WATER);
		patchyIslands = new BiomePatchyIslands(ConfigHandler.BIOME_ID_PATCHY_ISLANDS);
		marsh1 = new BiomeMarsh(ConfigHandler.BIOME_ID_MARSH1, "Marsh 1", new Marsh1NoiseFeature());
		marsh2 = new BiomeMarsh(ConfigHandler.BIOME_ID_MARSH2, "Marsh 2", new Marsh2NoiseFeature());
		
		addBiome(swampLands);
		addBiome(coarseIslands);
		addBiome(deepWater);
		addBiome(patchyIslands);
		addBiome(marsh1);
		addBiome(marsh2);
	}

	private static void addBiome(BiomeGenBaseBetweenlands biome) {
		biome.createMutation();
		GenLayerBetweenlandsBiome.biomesToGenerate.add(biome);
	}
}