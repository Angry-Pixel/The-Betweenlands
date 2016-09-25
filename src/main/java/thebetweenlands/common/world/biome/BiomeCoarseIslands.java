package thebetweenlands.common.world.biome;

import thebetweenlands.common.entity.mobs.*;
import thebetweenlands.common.world.biome.spawning.spawners.CaveSpawnEntry;
import thebetweenlands.common.world.biome.spawning.spawners.SurfaceSpawnEntry;
import thebetweenlands.common.world.biome.spawning.spawners.TreeSpawnEntry;
import thebetweenlands.common.world.gen.biome.decorator.BiomeDecoratorCoarseIslands;
import thebetweenlands.common.world.gen.biome.feature.AlgaeFeature;
import thebetweenlands.common.world.gen.biome.feature.CoarseIslandsFeature;

public class BiomeCoarseIslands extends BiomeBetweenlands {

	public BiomeCoarseIslands() {
		super(new BiomeProperties("coarse_islands").setBaseHeight(115.0F).setHeightVariation(4.0F).setWaterColor(0x1b3944).setTemperature(0.8F).setRainfall(0.9F));
		//this.setWeight(15);
		this.setFogColor(10, 30, 12);
		this.getBiomeGenerator().setDecorator(new BiomeDecoratorCoarseIslands())
		.addFeature(new CoarseIslandsFeature())
		.addFeature(new AlgaeFeature());

		this.blSpawnEntries.add(new SurfaceSpawnEntry(EntityDragonFly.class, (short) 36).setGroupSize(1, 2).setSpawnCheckRadius(32.0D).setSpawningInterval(400));
		this.blSpawnEntries.add(new SurfaceSpawnEntry(EntityFirefly.class, (short) 25).setSpawnCheckRadius(32.0D));
		this.blSpawnEntries.add(new SurfaceSpawnEntry(EntityGecko.class, (short) 52).setGroupSize(1, 3).setSpawnCheckRadius(32.0D).setSpawningInterval(600));
		this.blSpawnEntries.add(new SurfaceSpawnEntry(EntityFrog.class, (short) 30).setCanSpawnOnWater(true).setGroupSize(1, 3).setSpawnCheckRadius(32.0D).setSpawningInterval(100));
		this.blSpawnEntries.add(new CaveSpawnEntry(EntityBlindCaveFish.class, (short) 30).setGroupSize(3, 5).setSpawnCheckRadius(32.0D));
		this.blSpawnEntries.add(new TreeSpawnEntry(EntitySporeling.class, (short) 80).setGroupSize(2, 5).setSpawnCheckRadius(32.0D));

		this.blSpawnEntries.add(new SurfaceSpawnEntry(EntityLurker.class, (short) 32).setHostile(true).setSpawnCheckRadius(16.0D));
		this.blSpawnEntries.add(new SurfaceSpawnEntry(EntityAngler.class, (short) 42).setHostile(true).setGroupSize(1, 3));
		this.blSpawnEntries.add(new CaveSpawnEntry(EntityAngler.class, (short) 35).setHostile(true).setGroupSize(1, 3));
		this.blSpawnEntries.add(new SurfaceSpawnEntry(EntitySwampHag.class, (short) 50).setHostile(true));
		this.blSpawnEntries.add(new CaveSpawnEntry(EntitySwampHag.class, (short) 140).setHostile(true).setSpawnCheckRadius(6.0D).setGroupSize(1, 3));
		this.blSpawnEntries.add(new SurfaceSpawnEntry(EntityWight.class, (short) 12).setHostile(true).setSpawnCheckRadius(64.0D));
		this.blSpawnEntries.add(new CaveSpawnEntry(EntityWight.class, (short) 18).setHostile(true).setSpawnCheckRadius(64.0D));
		this.blSpawnEntries.add(new SurfaceSpawnEntry(EntityChiromaw.class, (short) 12).setHostile(true).setSpawnCheckRadius(30.0D));
		this.blSpawnEntries.add(new CaveSpawnEntry(EntityChiromaw.class, (short) 40).setHostile(true).setSpawnCheckRadius(20.0D).setGroupSize(1, 3));
	}

}
