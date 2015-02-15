package thebetweenlands.world.biomes;

import thebetweenlands.world.biomes.decorators.TestDecorator;
import thebetweenlands.world.genlayer.GenLayerBetweenlandsBiome;

public class BLBiomeRegistry {
	public static TestBiome testBiome;
	
	public static void init() {
		testBiome = new TestBiome(50, new TestDecorator());
		testBiome.createMutation();
		GenLayerBetweenlandsBiome.biomesToGenerate.add(testBiome);
	}
}
