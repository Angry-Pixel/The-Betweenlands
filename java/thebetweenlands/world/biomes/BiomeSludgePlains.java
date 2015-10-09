package thebetweenlands.world.biomes;

import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.world.WorldProviderBetweenlands;
import thebetweenlands.world.biomes.base.BiomeGenBaseBetweenlands;
import thebetweenlands.world.biomes.decorators.BiomeDecoratorSludgePlains;
import thebetweenlands.world.biomes.decorators.base.BiomeDecoratorBaseBetweenlands;
import thebetweenlands.world.biomes.feature.SludgePlainsNoiseFeature;

/**
 * Created by Bart on 9-10-2015.
 */
public class BiomeSludgePlains extends BiomeGenBaseBetweenlands {

    public BiomeSludgePlains(int biomeID) {
        this(biomeID, new BiomeDecoratorSludgePlains());
    }

    public BiomeSludgePlains(int biomeID, BiomeDecoratorBaseBetweenlands decorator) {
        super(biomeID, decorator);
        this.setFogColor((byte) 10, (byte) 30, (byte) 12);
        setColors(0x314D31, 0x314D31);
        setWeight(20);
        this.setHeightAndVariation(WorldProviderBetweenlands.LAYER_HEIGHT, 4);
        this.setBiomeName("WIP Sludge Plains");
        this.setBlocks(BLBlockRegistry.betweenstone, BLBlockRegistry.mud, BLBlockRegistry.mud, BLBlockRegistry.swampDirt, BLBlockRegistry.betweenlandsBedrock);
        this.setFillerBlockHeight((byte) 2);
        this.addFeature(new SludgePlainsNoiseFeature());
        this.waterColorMultiplier = 0x002f06;

    }
}
