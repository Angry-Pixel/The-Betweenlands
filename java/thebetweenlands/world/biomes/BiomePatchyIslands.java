package thebetweenlands.world.biomes;

import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.entities.mobs.*;
import thebetweenlands.world.WorldProviderBetweenlands;
import thebetweenlands.world.biomes.base.BiomeGenBaseBetweenlands;
import thebetweenlands.world.biomes.decorators.BiomeDecoratorPatchyIslands;
import thebetweenlands.world.biomes.decorators.base.BiomeDecoratorBaseBetweenlands;
import thebetweenlands.world.biomes.feature.AlgaeNoiseFeature;
import thebetweenlands.world.biomes.feature.PatchyIslandNoiseFeature;
import thebetweenlands.world.biomes.feature.SiltNoiseFeature;

public class BiomePatchyIslands
extends BiomeGenBaseBetweenlands
{
	public BiomePatchyIslands(int biomeID) {
		this(biomeID, new BiomeDecoratorPatchyIslands());
	}
	
	public BiomePatchyIslands(int biomeID, BiomeDecoratorBaseBetweenlands decorator) {
		super(biomeID, decorator);
		this.setFogColor((byte)10, (byte)30, (byte)12);
		setColors(0x314D31, 0x314D31);
		this.setHeightAndVariation(WorldProviderBetweenlands.LAYER_HEIGHT, 3);
		this.setBiomeName("Patchy Islands");
		this.setBlocks(BLBlockRegistry.betweenstone, BLBlockRegistry.swampDirt, BLBlockRegistry.swampGrass, BLBlockRegistry.mud, BLBlockRegistry.betweenlandsBedrock);
		this.setFillerBlockHeight((byte)1);
		this.addFeature(new PatchyIslandNoiseFeature())
		.addFeature(new SiltNoiseFeature())
		.addFeature(new AlgaeNoiseFeature());
		this.waterColorMultiplier = 0x184220;

		spawnableMonsterList.add(new SpawnListEntry(EntitySwampHag.class, 20, 1, 1));
		// spawnableMonsterList.add(new SpawnListEntry(EntityTarBeast.class, 15, 1, 1)); TODO
		spawnableMonsterList.add(new SpawnListEntry(EntityWight.class, 5, 0, 0));
		spawnableWaterCreatureList.add(new SpawnListEntry(EntityAngler.class, 20, 1, 2));
        	spawnableMonsterList.add(new SpawnListEntry(EntityDragonFly.class, 20, 2, 4));
        	spawnableMonsterList.add(new SpawnListEntry(EntityFirefly.class, 100, 10, 20));
        	spawnableMonsterList.add(new SpawnListEntry(EntityLeech.class, 15, 1, 1));
        	spawnableCreatureList.add(new SpawnListEntry(EntityMireSnail.class, 30, 1, 2));
        	spawnableMonsterList.add(new SpawnListEntry(EntityBloodSnail.class, 15, 1, 1));
	}
}
