package thebetweenlands.world.biomes.base;

import thebetweenlands.utils.confighandler.ConfigHandler;
import thebetweenlands.world.biomes.BiomeCoarseIslands;
import thebetweenlands.world.biomes.BiomeDeepWater;
import thebetweenlands.world.biomes.BiomePatchyIslands;
import thebetweenlands.world.biomes.BiomeSwampLands;
import thebetweenlands.world.biomes.decorators.TestDecorator;
import thebetweenlands.world.genlayer.GenLayerBetweenlandsBiome;

public class BLBiomeRegistry
{
	public static void init() {
		addBiome(new BiomeSwampLands(ConfigHandler.BIOME_ID_SWAMPLANDS, new TestDecorator()));
		//addBiome(new BiomeCoarseIslands(ConfigHandler.BIOME_ID_SWAMPLANDS+1, new TestDecorator()));
		//addBiome(new BiomeDeepWater(ConfigHandler.BIOME_ID_SWAMPLANDS+2, new TestDecorator()));
		//addBiome(new BiomePatchyIslands(ConfigHandler.BIOME_ID_SWAMPLANDS+3, new TestDecorator()));
	}

	private static void addBiome(BiomeGenBaseBetweenlands biome) {
		biome.createMutation();
		GenLayerBetweenlandsBiome.biomesToGenerate.add(biome);
	}
}
