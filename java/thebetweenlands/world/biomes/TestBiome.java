package thebetweenlands.world.biomes;

import thebetweenlands.world.biomes.decorators.BiomeDecoratorBaseBetweenlands;

public class TestBiome extends BiomeGenBaseBetweenlands {
	public TestBiome(int biomeID, BiomeDecoratorBaseBetweenlands decorator) {
		super(biomeID, decorator);
		this.setFogColor((short)20, (short)180, (short)50);
	}
}
