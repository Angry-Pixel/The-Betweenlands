package thebetweenlands.world.biomes;

import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.world.WorldProviderBetweenlands;
import thebetweenlands.world.biomes.decorators.BiomeDecoratorBaseBetweenlands;

public class BiomeSwampLands
        extends BiomeGenBaseBetweenlands
{
	public BiomeSwampLands(int biomeID, BiomeDecoratorBaseBetweenlands decorator) {
		super(biomeID, decorator);
		this.setFogColor((byte)10, (byte)30, (byte)12);
		this.setHeightAndVariation(WorldProviderBetweenlands.LAYER_HEIGHT, 10);
		this.setBiomeName("TestBiome");
		this.setBlocks(BLBlockRegistry.betweenstone, BLBlockRegistry.swampDirt, BLBlockRegistry.swampGrass, BLBlockRegistry.swampDirt, BLBlockRegistry.betweenlandsBedrock);
		this.setFillerBlockHeight((byte)5);
	}
}
