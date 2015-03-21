package thebetweenlands.world.biomes;

import net.minecraft.world.biome.BiomeGenBase.SpawnListEntry;
import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.entities.mobs.EntityAngler;
import thebetweenlands.entities.mobs.EntityFirefly;
import thebetweenlands.entities.mobs.EntitySwampHag;
import thebetweenlands.entities.mobs.EntityTarBeast;
import thebetweenlands.entities.mobs.EntityWight;
import thebetweenlands.world.WorldProviderBetweenlands;
import thebetweenlands.world.biomes.base.BiomeGenBaseBetweenlands;
import thebetweenlands.world.biomes.decorators.BiomeDecoratorSwampLands;
import thebetweenlands.world.biomes.decorators.base.BiomeDecoratorBaseBetweenlands;
import thebetweenlands.world.biomes.feature.AlgaeNoiseFeature;
import thebetweenlands.world.biomes.feature.SiltNoiseFeature;
import thebetweenlands.world.biomes.feature.SmallIslandNoiseFeature;

public class BiomeSwampLands
extends BiomeGenBaseBetweenlands
{
	public BiomeSwampLands(int biomeID) {
		this(biomeID, new BiomeDecoratorSwampLands());
	}
	
	public BiomeSwampLands(int biomeID, BiomeDecoratorBaseBetweenlands decorator) {
		super(biomeID, decorator);
		this.setFogColor((byte)10, (byte)30, (byte)12);
		setColors(0x314D31, 0x314D31);
		this.setHeightAndVariation(WorldProviderBetweenlands.LAYER_HEIGHT - 10, 0);
		this.setBiomeName("Swamplands");
		this.setBlocks(BLBlockRegistry.betweenstone, BLBlockRegistry.swampDirt, BLBlockRegistry.swampGrass, BLBlockRegistry.mud, BLBlockRegistry.betweenlandsBedrock);
		this.setFillerBlockHeight((byte)1);
		this.addFeature(new SmallIslandNoiseFeature())
		.addFeature(new SiltNoiseFeature())
		.addFeature(new AlgaeNoiseFeature());
		this.waterColorMultiplier = 0x184220;

		spawnableMonsterList.add(new SpawnListEntry(EntitySwampHag.class, 30, 1, 1));
		spawnableMonsterList.add(new SpawnListEntry(EntityTarBeast.class, 15, 1, 1));
		spawnableMonsterList.add(new SpawnListEntry(EntityWight.class, 2, 1, 1));
		spawnableWaterCreatureList.add(new SpawnListEntry(EntityAngler.class, 20, 1, 2));
		spawnableCreatureList.add(new SpawnListEntry(EntityFirefly.class, 5, 1, 2));
	}

	/*@Override
	public int getRootHeight(int x, int z) {
		return WorldProviderBetweenlands.LAYER_HEIGHT - 10;
	}

	@Override
	public int getHeightVariation(int x, int z) {
		return 0;
	}*/
}