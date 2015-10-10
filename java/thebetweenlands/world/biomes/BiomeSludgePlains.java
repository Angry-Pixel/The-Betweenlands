package thebetweenlands.world.biomes;

import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.entities.mobs.*;
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
        this.setHeightAndVariation(WorldProviderBetweenlands.LAYER_HEIGHT + 8, 2);
        this.setBiomeName("Sludge Plains");
        this.setBlocks(BLBlockRegistry.betweenstone, BLBlockRegistry.mud, BLBlockRegistry.mud, BLBlockRegistry.swampDirt, BLBlockRegistry.betweenlandsBedrock);
        this.setFillerBlockHeight((byte) 1);
        this.addFeature(new SludgePlainsNoiseFeature());
        this.waterColorMultiplier = 0x002f06;

        spawnableMonsterList.add(new SpawnListEntry(EntityFirefly.class, 100, 10, 20));
        spawnableMonsterList.add(new SpawnListEntry(EntityDragonFly.class, 20, 2, 4));
        spawnableMonsterList.add(new SpawnListEntry(EntityWight.class, 5, 0, 0));
        spawnableWaterCreatureList.add(new SpawnListEntry(EntityAngler.class, 20, 1, 2));
        spawnableWaterCreatureList.add(new SpawnListEntry(EntityLurker.class, 5, 1, 1));
    }
}
