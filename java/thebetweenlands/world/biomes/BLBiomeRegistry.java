package thebetweenlands.world.biomes;

import thebetweenlands.utils.confighandler.ConfigHandler;
import thebetweenlands.world.biomes.decorators.TestDecorator;
import thebetweenlands.world.genlayer.GenLayerBetweenlandsBiome;

public class BLBiomeRegistry
{
	public static BiomeSwampLands swampLands;
	//public static TestBiome2 testBiome2;

	public static void init() {
		/*testBiome = new TestBiome(50, new TestDecorator());
		testBiome.createMutation();
		GenLayerBetweenlandsBiome.biomesToGenerate.add(testBiome);

		testBiome2 = new TestBiome2(51, new TestDecorator());
		testBiome2.createMutation();
		GenLayerBetweenlandsBiome.biomesToGenerate.add(testBiome2);*/

		//GenLayerBetweenlandsBiome.biomesToGenerate.add(BiomeGenBase.jungle);

		addBiome(new BiomeSwampLands(ConfigHandler.BIOME_ID_SWAMPLANDS, new TestDecorator()));
	}

	private static void addBiome(BiomeGenBaseBetweenlands biome) {
		biome.createMutation();
		GenLayerBetweenlandsBiome.biomesToGenerate.add(biome);
	}
}
