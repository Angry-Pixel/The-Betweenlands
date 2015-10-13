package thebetweenlands.world.biomes;

import net.minecraft.world.biome.BiomeGenBase.SpawnListEntry;
import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.entities.mobs.EntityAngler;
import thebetweenlands.entities.mobs.EntityBloodSnail;
import thebetweenlands.entities.mobs.EntityFirefly;
import thebetweenlands.entities.mobs.EntityLeech;
import thebetweenlands.entities.mobs.EntityMireSnail;
import thebetweenlands.entities.mobs.EntitySporeling;
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

        spawnableMonsterList.add(new SpawnListEntry(EntitySwampHag.class, 20, 1, 1));
        spawnableMonsterList.add(new SpawnListEntry(EntityLeech.class, 15, 1, 1));
        spawnableMonsterList.add(new SpawnListEntry(EntityTarBeast.class, 15, 1, 1));
        spawnableMonsterList.add(new SpawnListEntry(EntityWight.class, 5, -1, -1));
        spawnableWaterCreatureList.add(new SpawnListEntry(EntityAngler.class, 20, 1, 2));
        spawnableCreatureList.add(new SpawnListEntry(EntityFirefly.class, 15, 2, 6));
        spawnableCreatureList.add(new SpawnListEntry(EntityMireSnail.class, 30, 1, 2));
        spawnableMonsterList.add(new SpawnListEntry(EntityBloodSnail.class, 15, 1, 1));
        spawnableCreatureList.add(new SpawnListEntry(EntitySporeling.class, 100, 5, 8));
        spawnableWaterCreatureList.add(new SpawnListEntry(EntityTarBeast.class, 200, 1, 1));
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
