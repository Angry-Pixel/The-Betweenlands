package thebetweenlands.world.biomes;

import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.entities.mobs.EntityBlindCaveFish;
import thebetweenlands.entities.mobs.EntityFirefly;
import thebetweenlands.entities.mobs.EntityLeech;
import thebetweenlands.entities.mobs.EntityPeatMummy;
import thebetweenlands.entities.mobs.EntitySludge;
import thebetweenlands.entities.mobs.EntityTarBeast;
import thebetweenlands.entities.mobs.EntityWight;
import thebetweenlands.world.WorldProviderBetweenlands;
import thebetweenlands.world.biomes.base.BiomeGenBaseBetweenlands;
import thebetweenlands.world.biomes.decorators.BiomeDecoratorSludgePlains;
import thebetweenlands.world.biomes.decorators.base.BiomeDecoratorBaseBetweenlands;
import thebetweenlands.world.biomes.feature.FlatLandNoiseFeature;
import thebetweenlands.world.biomes.feature.PatchNoiseFeature;

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
		this.setBlocks(BLBlockRegistry.betweenstone, BLBlockRegistry.swampDirt, BLBlockRegistry.mud, BLBlockRegistry.mud, BLBlockRegistry.betweenlandsBedrock);
		this.setFillerBlockHeight((byte) 1);
		this.addFeature(new FlatLandNoiseFeature())
		.addFeature(new PatchNoiseFeature(0.03125D * 5.75D, 0.03125D * 5.75D, BLBlockRegistry.sludgyDirt))
		.addFeature(new PatchNoiseFeature(0.74D, 0.74D, BLBlockRegistry.swampDirt))
		.addFeature(new PatchNoiseFeature(0.65D, 0.65D, BLBlockRegistry.mud, 1.0D / 1.35D, 1.72D));
		this.waterColorMultiplier = 0x3A2F0B;

		spawnableCreatureList.add(new SpawnListEntry(EntityFirefly.class, 25, 1, 3));

		spawnableWaterCreatureList.add(new SpawnListEntry(EntityBlindCaveFish.class, 500, 2, 5));

		spawnableCaveCreatureList.add(new SpawnListEntry(EntityTarBeast.class, 18, 1, 1));

		spawnableMonsterList.add(new SpawnListEntry(EntityPeatMummy.class, 12, -1, -1));
		spawnableMonsterList.add(new SpawnListEntry(EntityWight.class, 4, -1, -1));
		spawnableMonsterList.add(new SpawnListEntry(EntitySludge.class, 135, 1, 1));
		spawnableMonsterList.add(new SpawnListEntry(EntityLeech.class, 40, 1, 1));
	}
}
