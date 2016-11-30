package thebetweenlands.common.world.biome;

import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;
import thebetweenlands.common.entity.mobs.EntityAngler;
import thebetweenlands.common.entity.mobs.EntityBlindCaveFish;
import thebetweenlands.common.entity.mobs.EntityChiromaw;
import thebetweenlands.common.entity.mobs.EntityFirefly;
import thebetweenlands.common.entity.mobs.EntityLeech;
import thebetweenlands.common.entity.mobs.EntityPeatMummy;
import thebetweenlands.common.entity.mobs.EntitySludge;
import thebetweenlands.common.entity.mobs.EntitySwampHag;
import thebetweenlands.common.entity.mobs.EntityTarBeast;
import thebetweenlands.common.entity.mobs.EntityWight;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.world.WorldProviderBetweenlands;
import thebetweenlands.common.world.biome.spawning.spawners.CaveSpawnEntry;
import thebetweenlands.common.world.biome.spawning.spawners.SurfaceSpawnEntry;
import thebetweenlands.common.world.biome.spawning.spawners.TarSpawnEntry;
import thebetweenlands.common.world.gen.biome.decorator.BiomeDecoratorSludgePlains;
import thebetweenlands.common.world.gen.biome.feature.FlatLandFeature;
import thebetweenlands.common.world.gen.biome.feature.MiddleGemFeature;
import thebetweenlands.common.world.gen.biome.feature.PatchFeature;

public class BiomeSludgePlains extends BiomeBetweenlands {

	public BiomeSludgePlains() {
		super(new BiomeProperties("sludge_plains").setBaseHeight(WorldProviderBetweenlands.LAYER_HEIGHT - 10).setHeightVariation(3F).setWaterColor(0x3A2F0B).setTemperature(0.8F).setRainfall(0.9F));
		this.setWeight(5);
		this.setFogColor(10, 30, 12);
		this.getBiomeGenerator().setTopBlockState(BlockRegistry.MUD.getDefaultState()).setFillerBlockHeight(1).setDecorator(new BiomeDecoratorSludgePlains())
		.addFeature(new FlatLandFeature(WorldProviderBetweenlands.LAYER_HEIGHT))
		.addFeature(new PatchFeature(0.03125D * 5.75D, 0.03125D * 5.75D, BlockRegistry.SLUDGY_DIRT.getDefaultState()))
		.addFeature(new PatchFeature(0.74D, 0.74D, BlockRegistry.SWAMP_DIRT.getDefaultState()))
		.addFeature(new PatchFeature(0.65D, 0.65D, BlockRegistry.MUD.getDefaultState(), 1.0D / 1.35D, 1.72D))
		.addFeature(new MiddleGemFeature());
		this.setFoliageColors(0x5B3522, 0x5B3522);

		this.blSpawnEntries.add(new SurfaceSpawnEntry(EntityFirefly.class, (short) 20).setGroupSize(1, 3));
		this.blSpawnEntries.add(new CaveSpawnEntry(EntityBlindCaveFish.class, (short) 30).setCanSpawnInWater(true).setGroupSize(3, 5));

		this.blSpawnEntries.add(new TarSpawnEntry(EntityTarBeast.class, (short) 84).setHostile(true).setGroupSize(1, 2).setSpawnCheckRadius(14.0D));
		this.blSpawnEntries.add(new SurfaceSpawnEntry(EntityLeech.class, (short) 50).setHostile(true));
		this.blSpawnEntries.add(new SurfaceSpawnEntry(EntitySludge.class, (short) 64).setHostile(true).setGroupSize(1, 3).setGroupSpawnRadius(4.0D));
		this.blSpawnEntries.add(new SurfaceSpawnEntry(EntityPeatMummy.class, (short) 10).setHostile(true).setSpawnCheckRadius(64.0D));
		this.blSpawnEntries.add(new CaveSpawnEntry(EntitySwampHag.class, (short) 140).setHostile(true).setSpawnCheckRadius(6.0D).setGroupSize(1, 3));
		this.blSpawnEntries.add(new SurfaceSpawnEntry(EntityWight.class, (short) 12).setHostile(true).setSpawnCheckRadius(64.0D));
		this.blSpawnEntries.add(new CaveSpawnEntry(EntityWight.class, (short) 18).setHostile(true).setSpawnCheckRadius(64.0D));
		this.blSpawnEntries.add(new SurfaceSpawnEntry(EntityChiromaw.class, (short) 12).setHostile(true).setSpawnCheckRadius(30.0D));
		this.blSpawnEntries.add(new CaveSpawnEntry(EntityChiromaw.class, (short) 40).setHostile(true).setSpawnCheckRadius(20.0D).setGroupSize(1, 3));
		this.blSpawnEntries.add(new CaveSpawnEntry(EntityAngler.class, (short) 35).setCanSpawnInWater(true).setHostile(true).setGroupSize(1, 3));

		BiomeDictionary.registerBiomeType(this, Type.SWAMP, Type.WET, Type.WASTELAND, Type.DEAD);
	}

}
