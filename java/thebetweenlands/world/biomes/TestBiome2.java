package thebetweenlands.world.biomes;

import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.world.WorldProviderBetweenlands;
import thebetweenlands.world.biomes.decorators.BiomeDecoratorBaseBetweenlands;

public class TestBiome2
        extends BiomeGenBaseBetweenlands
{
	public TestBiome2(int biomeID, BiomeDecoratorBaseBetweenlands decorator) {
		super(biomeID, decorator);
		this.setFogColor((short)10, (short)30, (short)40);
		this.setHeightAndVariation(WorldProviderBetweenlands.LAYER_HEIGHT, 80);
		this.setBiomeName("TestBiome2");
		this.setBlocks(BLBlockRegistry.betweenstone, BLBlockRegistry.swampDirt, BLBlockRegistry.swampGrass, BLBlockRegistry.swampDirt, BLBlockRegistry.betweenlandsBedrock);
		this.setFillerBlockHeight((byte)5);
	}
}
