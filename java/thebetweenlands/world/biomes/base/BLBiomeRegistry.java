package thebetweenlands.world.biomes.base;

import thebetweenlands.utils.confighandler.ConfigHandler;
import thebetweenlands.world.biomes.BiomeCoarseIslands;
import thebetweenlands.world.biomes.BiomeDeepWater;
import thebetweenlands.world.biomes.BiomePatchyIslands;
import thebetweenlands.world.biomes.BiomeSwampLands;
import thebetweenlands.world.genlayer.GenLayerBetweenlandsBiome;

public class BLBiomeRegistry
{
	public static BiomeGenBaseBetweenlands swampLands;
	public static BiomeGenBaseBetweenlands coarseIslands;
	public static BiomeGenBaseBetweenlands deepWater;
	public static BiomeGenBaseBetweenlands patchyIslands;
	
	public static void init() {
		swampLands = new BiomeSwampLands(ConfigHandler.BIOME_ID_SWAMPLANDS);
		coarseIslands = new BiomeCoarseIslands(ConfigHandler.BIOME_ID_COARSE_ISLANDS);
		deepWater = new BiomeDeepWater(ConfigHandler.BIOME_ID_DEEP_WATER);
		patchyIslands = new BiomePatchyIslands(ConfigHandler.BIOME_ID_PATCHY_ISLANDS);
		
		addBiome(swampLands);
		addBiome(coarseIslands);
		addBiome(deepWater);
		addBiome(patchyIslands);
	}

	private static void addBiome(BiomeGenBaseBetweenlands biome) {
		biome.createMutation();
		GenLayerBetweenlandsBiome.biomesToGenerate.add(biome);
	}
}