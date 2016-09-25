package thebetweenlands.common.world.biome;

import thebetweenlands.common.entity.mobs.*;
import thebetweenlands.common.world.biome.spawning.spawners.CaveSpawnEntry;
import thebetweenlands.common.world.biome.spawning.spawners.SurfaceSpawnEntry;
import thebetweenlands.common.world.biome.spawning.spawners.TreeSpawnEntry;
import thebetweenlands.common.world.gen.biome.decorator.BiomeDecoratorDeepWaters;
import thebetweenlands.common.world.gen.biome.feature.AlgaeFeature;
import thebetweenlands.common.world.gen.biome.feature.CragSpiresFeature;
import thebetweenlands.common.world.gen.biome.feature.DeepWatersFeature;

public class BiomeDeepWaters extends BiomeBetweenlands {

	public BiomeDeepWaters() {
		super(new BiomeProperties("deep_waters").setBaseHeight(108.0F).setHeightVariation(5.0F).setWaterColor(0x1b3944).setTemperature(0.8F).setRainfall(0.9F));
		//this.setWeight(15);
		this.setFogColor(10, 30, 12);
		this.getBiomeGenerator().setDecorator(new BiomeDecoratorDeepWaters())
		.addFeature(new DeepWatersFeature())
		.addFeature(new AlgaeFeature())
		.addFeature(new CragSpiresFeature());

		this.blSpawnEntries.add(new SurfaceSpawnEntry(EntityFirefly.class, (short) 10).setSpawnCheckRadius(32.0D));
		this.blSpawnEntries.add(new TreeSpawnEntry(EntitySporeling.class, (short) 80).setGroupSize(2, 5).setSpawnCheckRadius(32.0D));
		this.blSpawnEntries.add(new CaveSpawnEntry(EntityBlindCaveFish.class, (short) 30).setGroupSize(3, 5).setSpawnCheckRadius(32.0D));

		this.blSpawnEntries.add(new SurfaceSpawnEntry(EntityLurker.class, (short) 35).setHostile(true).setSpawnCheckRadius(16.0D));
		this.blSpawnEntries.add(new SurfaceSpawnEntry(EntityAngler.class, (short) 45).setHostile(true).setGroupSize(1, 3));
		this.blSpawnEntries.add(new CaveSpawnEntry(EntityAngler.class, (short) 35).setHostile(true).setGroupSize(1, 3));
		this.blSpawnEntries.add(new CaveSpawnEntry(EntitySwampHag.class, (short) 140).setHostile(true).setSpawnCheckRadius(6.0D).setGroupSize(1, 3));
		this.blSpawnEntries.add(new CaveSpawnEntry(EntityWight.class, (short) 18).setHostile(true).setSpawnCheckRadius(64.0D));
		this.blSpawnEntries.add(new CaveSpawnEntry(EntityChiromaw.class, (short) 30).setHostile(true).setSpawnCheckRadius(20.0D).setGroupSize(1, 3));
	}

}
