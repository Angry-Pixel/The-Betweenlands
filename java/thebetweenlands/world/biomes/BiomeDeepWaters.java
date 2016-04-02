package thebetweenlands.world.biomes;

import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.entities.mobs.EntityAngler;
import thebetweenlands.entities.mobs.EntityBlindCaveFish;
import thebetweenlands.entities.mobs.EntityChiromaw;
import thebetweenlands.entities.mobs.EntityFirefly;
import thebetweenlands.entities.mobs.EntityLurker;
import thebetweenlands.entities.mobs.EntitySporeling;
import thebetweenlands.entities.mobs.EntitySwampHag;
import thebetweenlands.entities.mobs.EntityWight;
import thebetweenlands.world.WorldProviderBetweenlands;
import thebetweenlands.world.biomes.base.BiomeGenBaseBetweenlands;
import thebetweenlands.world.biomes.decorators.BiomeDecoratorDeepWaters;
import thebetweenlands.world.biomes.decorators.base.BiomeDecoratorBaseBetweenlands;
import thebetweenlands.world.biomes.feature.AlgaeNoiseFeature;
import thebetweenlands.world.biomes.feature.CragSpiresNoiseFeature;
import thebetweenlands.world.biomes.feature.DeepWaterNoiseFeature;
import thebetweenlands.world.biomes.feature.SiltNoiseFeature;
import thebetweenlands.world.biomes.spawning.spawners.CaveSpawnEntry;
import thebetweenlands.world.biomes.spawning.spawners.SurfaceSpawnEntry;
import thebetweenlands.world.biomes.spawning.spawners.TreeSpawnEntry;

public class BiomeDeepWaters
extends BiomeGenBaseBetweenlands
{
	public BiomeDeepWaters(int biomeID) {
		this(biomeID, new BiomeDecoratorDeepWaters());
	}

	public BiomeDeepWaters(int biomeID, BiomeDecoratorBaseBetweenlands decorator) {
		super(biomeID, decorator);
		this.setFogColor((byte)10, (byte)30, (byte)12);
		setColors(0x314D31, 0x314D31);
		setWeight(15);
		this.setHeightAndVariation(WorldProviderBetweenlands.LAYER_HEIGHT - 20, 10);
		this.setBiomeName("deepWaters");
		this.setBlocks(BLBlockRegistry.betweenstone, BLBlockRegistry.swampDirt, BLBlockRegistry.swampGrass, BLBlockRegistry.mud, BLBlockRegistry.betweenlandsBedrock);
		this.setFillerBlockHeight((byte) 5);
		this.waterColorMultiplier = 0x1b3944;
		this.addFeature(new DeepWaterNoiseFeature())
		.addFeature(new SiltNoiseFeature())
		.addFeature(new AlgaeNoiseFeature())
		.addFeature(new CragSpiresNoiseFeature());

		this.blSpawnEntries.add(new SurfaceSpawnEntry(EntityFirefly.class, (short) 10).setSpawnCheckRadius(32.0D));
		this.blSpawnEntries.add(new TreeSpawnEntry(EntitySporeling.class, (short) 80).setGroupSize(2, 5).setSpawnCheckRadius(32.0D));
		this.blSpawnEntries.add(new CaveSpawnEntry(EntityBlindCaveFish.class, (short) 30).setGroupSize(3, 5).setSpawnCheckRadius(32.0D));

		this.blSpawnEntries.add(new SurfaceSpawnEntry(EntityLurker.class, (short) 35).setHostile(true).setSpawnCheckRadius(16.0D));
		this.blSpawnEntries.add(new SurfaceSpawnEntry(EntityAngler.class, (short) 45).setHostile(true).setGroupSize(1, 3));
		this.blSpawnEntries.add(new CaveSpawnEntry(EntitySwampHag.class, (short) 140).setHostile(true).setSpawnCheckRadius(6.0D));
		this.blSpawnEntries.add(new CaveSpawnEntry(EntityWight.class, (short) 18).setHostile(true).setSpawnCheckRadius(30.0D));
		this.blSpawnEntries.add(new CaveSpawnEntry(EntityChiromaw.class, (short) 30).setHostile(true).setSpawnCheckRadius(20.0D).setGroupSize(1, 3));
	}
}
