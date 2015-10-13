package thebetweenlands.world.biomes;

import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.entities.mobs.EntityAngler;
import thebetweenlands.entities.mobs.EntityBloodSnail;
import thebetweenlands.entities.mobs.EntityDragonFly;
import thebetweenlands.entities.mobs.EntityFirefly;
import thebetweenlands.entities.mobs.EntityLeech;
import thebetweenlands.entities.mobs.EntityLurker;
import thebetweenlands.entities.mobs.EntityMireSnail;
import thebetweenlands.entities.mobs.EntitySiltCrab;
import thebetweenlands.entities.mobs.EntitySporeling;
import thebetweenlands.entities.mobs.EntitySwampHag;
import thebetweenlands.entities.mobs.EntityTarBeast;
import thebetweenlands.entities.mobs.EntityWight;
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
		setWeight(20);
		this.setHeightAndVariation(WorldProviderBetweenlands.LAYER_HEIGHT, 3);
		this.setBiomeName("Patchy Islands");
		this.setBlocks(BLBlockRegistry.betweenstone, BLBlockRegistry.swampDirt, BLBlockRegistry.swampGrass, BLBlockRegistry.mud, BLBlockRegistry.betweenlandsBedrock);
		this.setFillerBlockHeight((byte)1);
		this.addFeature(new PatchyIslandNoiseFeature())
		.addFeature(new SiltNoiseFeature())
		.addFeature(new AlgaeNoiseFeature());
		this.waterColorMultiplier = 0x184220;

		spawnableMonsterList.add(new SpawnListEntry(EntitySwampHag.class, 20, 1, 1));
		spawnableMonsterList.add(new SpawnListEntry(EntityWight.class, 5, -1, -1));
		spawnableWaterCreatureList.add(new SpawnListEntry(EntityAngler.class, 20, 1, 2));
		spawnableCreatureList.add(new SpawnListEntry(EntityDragonFly.class, 20, 1, 4));
    	spawnableCreatureList.add(new SpawnListEntry(EntityFirefly.class, 25, 1, 3));
    	spawnableMonsterList.add(new SpawnListEntry(EntityLeech.class, 15, 1, 1));
    	spawnableCreatureList.add(new SpawnListEntry(EntityMireSnail.class, 30, 1, 2));
    	spawnableMonsterList.add(new SpawnListEntry(EntityBloodSnail.class, 15, 1, 1));
    	spawnableWaterCreatureList.add(new SpawnListEntry(EntityLurker.class, 20, 1, 1));
    	spawnableMonsterList.add(new SpawnListEntry(EntitySiltCrab.class, 50, 1, 4));
    	spawnableCreatureList.add(new SpawnListEntry(EntitySporeling.class, 100, 5, 8));
	}
}
