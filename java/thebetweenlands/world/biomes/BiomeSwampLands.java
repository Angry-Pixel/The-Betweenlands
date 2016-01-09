package thebetweenlands.world.biomes;

import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.entities.mobs.EntityAngler;
import thebetweenlands.entities.mobs.EntityBlindCaveFish;
import thebetweenlands.entities.mobs.EntityBloodSnail;
import thebetweenlands.entities.mobs.EntityFirefly;
import thebetweenlands.entities.mobs.EntityGecko;
import thebetweenlands.entities.mobs.EntityLeech;
import thebetweenlands.entities.mobs.EntityMireSnail;
import thebetweenlands.entities.mobs.EntitySwampHag;
import thebetweenlands.entities.mobs.EntityTarBeast;
import thebetweenlands.entities.mobs.EntityWight;
import thebetweenlands.world.WorldProviderBetweenlands;
import thebetweenlands.world.biomes.base.BiomeGenBaseBetweenlands;
import thebetweenlands.world.biomes.decorators.BiomeDecoratorSwampLands;
import thebetweenlands.world.biomes.decorators.base.BiomeDecoratorBaseBetweenlands;
import thebetweenlands.world.biomes.feature.AlgaeNoiseFeature;
import thebetweenlands.world.biomes.feature.FlatLandNoiseFeature;
import thebetweenlands.world.biomes.feature.PatchNoiseFeature;
import thebetweenlands.world.biomes.spawning.spawners.CaveSpawnEntry;
import thebetweenlands.world.biomes.spawning.spawners.SurfaceSpawnEntry;

public class BiomeSwampLands
extends BiomeGenBaseBetweenlands {
	public BiomeSwampLands(int biomeID) {
		this(biomeID, new BiomeDecoratorSwampLands());
	}

	public BiomeSwampLands(int biomeID, BiomeDecoratorBaseBetweenlands decorator) {
		super(biomeID, decorator);
		this.setFogColor((byte) 10, (byte) 30, (byte) 12);
		setColors(0x314D31, 0x314D31);
		setWeight(20);
		this.setHeightAndVariation(WorldProviderBetweenlands.CAVE_START, 0);
		this.setBiomeName("Swamplands");
		this.setBlocks(BLBlockRegistry.betweenstone, BLBlockRegistry.swampDirt, BLBlockRegistry.deadGrass, BLBlockRegistry.mud, BLBlockRegistry.betweenlandsBedrock);
		this.setFillerBlockHeight((byte) 1);
		this.addFeature(new FlatLandNoiseFeature())
		.addFeature(new PatchNoiseFeature(0.03125D * 5.75D, 0.03125D * 5.75D, BLBlockRegistry.swampGrass))
		.addFeature(new PatchNoiseFeature(0.74D, 0.74D, BLBlockRegistry.swampDirt))
		.addFeature(new PatchNoiseFeature(0.65D, 0.65D, BLBlockRegistry.mud, 1.0D / 1.35D, 1.72D))
		.addFeature(new AlgaeNoiseFeature());
		this.waterColorMultiplier = 0x184220;

		/*spawnableCreatureList.add(new SpawnListEntry(EntityFirefly.class, 15, 2, 6));
		spawnableCreatureList.add(new SpawnListEntry(EntityGecko.class, 30, 1, 3));
		spawnableCreatureList.add(new SpawnListEntry(EntityMireSnail.class, 30, 1, 2));

		spawnableWaterCreatureList.add(new SpawnListEntry(EntityTarBeast.class, 200, 1, 1));
		spawnableWaterCreatureList.add(new SpawnListEntry(EntityBlindCaveFish.class, 100, 2, 5));
		spawnableWaterCreatureList.add(new SpawnListEntry(EntityAngler.class, 20, 1, 2));

		spawnableCaveCreatureList.add(new SpawnListEntry(EntitySporeling.class, 200, 5, 8));

		spawnableMonsterList.add(new SpawnListEntry(EntitySwampHag.class, 20, 1, 1));
		spawnableMonsterList.add(new SpawnListEntry(EntityLeech.class, 15, 1, 1));
		spawnableMonsterList.add(new SpawnListEntry(EntityTarBeast.class, 15, 1, 1));
		spawnableMonsterList.add(new SpawnListEntry(EntityWight.class, 5, -1, -1));
		spawnableMonsterList.add(new SpawnListEntry(EntityBloodSnail.class, 15, 1, 1));*/

		//TODO: Tweak weights
		
		this.blSpawnEntries.add(new SurfaceSpawnEntry(EntityFirefly.class, (short) 20));
		this.blSpawnEntries.add(new SurfaceSpawnEntry(EntityGecko.class, (short) 80).setGroupSize(1, 5));
		this.blSpawnEntries.add(new SurfaceSpawnEntry(EntityMireSnail.class, (short) 60).setGroupSize(1, 5));
		this.blSpawnEntries.add(new CaveSpawnEntry(EntityBlindCaveFish.class, (short) 60).setGroupSize(3, 5));

		this.blSpawnEntries.add(new SurfaceSpawnEntry(EntityTarBeast.class, (short) 20).setHostile(true));
		this.blSpawnEntries.add(new SurfaceSpawnEntry(EntityLeech.class, (short) 35).setHostile(true));

		this.blSpawnEntries.add(new CaveSpawnEntry(EntityAngler.class, (short) 40).setHostile(true));
		this.blSpawnEntries.add(new CaveSpawnEntry(EntitySwampHag.class, (short) 30).setHostile(true));
		this.blSpawnEntries.add(new CaveSpawnEntry(EntityWight.class, (short) 3).setHostile(true));
		this.blSpawnEntries.add(new CaveSpawnEntry(EntityBloodSnail.class, (short) 25).setHostile(true));
	}

	/*@Override
    public int getRootHeight(int x, int z) {
		return WorldProviderBetweenlands.CAVE_START;
	}

	@Override
	public int getHeightVariation(int x, int z) {
		return 0;
	}*/
}
