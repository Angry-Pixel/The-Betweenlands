package thebetweenlands.common.world.biome;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;
import thebetweenlands.common.entity.mobs.EntityAngler;
import thebetweenlands.common.entity.mobs.EntityBlindCaveFish;
import thebetweenlands.common.entity.mobs.EntityChiromaw;
import thebetweenlands.common.entity.mobs.EntityDragonFly;
import thebetweenlands.common.entity.mobs.EntityFirefly;
import thebetweenlands.common.entity.mobs.EntityFrog;
import thebetweenlands.common.entity.mobs.EntityGecko;
import thebetweenlands.common.entity.mobs.EntityLurker;
import thebetweenlands.common.entity.mobs.EntitySporeling;
import thebetweenlands.common.entity.mobs.EntitySwampHag;
import thebetweenlands.common.entity.mobs.EntityWight;
import thebetweenlands.common.lib.ModInfo;
import thebetweenlands.common.world.WorldProviderBetweenlands;
import thebetweenlands.common.world.biome.spawning.spawners.CaveSpawnEntry;
import thebetweenlands.common.world.biome.spawning.spawners.SporelingSpawnEntry;
import thebetweenlands.common.world.biome.spawning.spawners.SurfaceSpawnEntry;
import thebetweenlands.common.world.biome.spawning.spawners.SwampHagCaveSpawnEntry;
import thebetweenlands.common.world.biome.spawning.spawners.TreeSpawnEntry;
import thebetweenlands.common.world.gen.biome.decorator.BiomeDecoratorCoarseIslands;
import thebetweenlands.common.world.gen.biome.feature.AlgaeFeature;
import thebetweenlands.common.world.gen.biome.feature.CoarseIslandsFeature;

public class BiomeCoarseIslands extends BiomeBetweenlands {

	public BiomeCoarseIslands() {
		super(new ResourceLocation(ModInfo.ID, "coarse_islands"), 
				new BiomeProperties("Coarse Islands")
				.setBaseHeight(WorldProviderBetweenlands.LAYER_HEIGHT - 5)
				.setHeightVariation(4.0F)
				.setWaterColor(0x1b3944)
				.setTemperature(0.8F)
				.setRainfall(0.9F));
		this.setWeight(16);
		this.getBiomeGenerator().setDecorator(new BiomeDecoratorCoarseIslands(this))
		.addFeature(new CoarseIslandsFeature())
		.addFeature(new AlgaeFeature());
	}

	@Override
	public void addTypes() {
		BiomeDictionary.addTypes(this, Type.SWAMP, Type.WET, Type.WATER);
	}

	@Override
	protected void addSpawnEntries() {
		super.addSpawnEntries();

		this.blSpawnEntries.add(new SurfaceSpawnEntry(0, EntityDragonFly.class, (short) 36).setCanSpawnOnWater(true).setGroupSize(1, 2).setSpawnCheckRadius(32.0D).setSpawningInterval(400));
		this.blSpawnEntries.add(new SurfaceSpawnEntry(1, EntityFirefly.class, (short) 25).setSpawnCheckRadius(32.0D));
		this.blSpawnEntries.add(new SurfaceSpawnEntry(2, EntityGecko.class, (short) 52).setGroupSize(1, 3).setSpawnCheckRadius(32.0D).setSpawningInterval(600));
		this.blSpawnEntries.add(new SurfaceSpawnEntry(3, EntityFrog.class, (short) 30).setCanSpawnOnWater(true).setGroupSize(1, 3).setSpawnCheckRadius(32.0D).setSpawningInterval(100));
		this.blSpawnEntries.add(new CaveSpawnEntry(4, EntityBlindCaveFish.class, (short) 30).setCanSpawnInWater(true).setGroupSize(3, 5).setSpawnCheckRadius(32.0D));
		this.blSpawnEntries.add(new SporelingSpawnEntry(5, EntitySporeling.class, (short) 80).setGroupSize(2, 5).setSpawnCheckRadius(32.0D));

		this.blSpawnEntries.add(new SurfaceSpawnEntry(6, EntityLurker.class, (short) 32).setCanSpawnInWater(true).setHostile(true).setSpawnCheckRadius(16.0D));
		this.blSpawnEntries.add(new SurfaceSpawnEntry(7, EntityAngler.class, (short) 42).setCanSpawnInWater(true).setHostile(true).setGroupSize(1, 3));
		this.blSpawnEntries.add(new CaveSpawnEntry(8, EntityAngler.class, (short) 45).setCanSpawnInWater(true).setHostile(true).setGroupSize(1, 3));
		this.blSpawnEntries.add(new SurfaceSpawnEntry(9, EntitySwampHag.class, (short) 100).setHostile(true));
		this.blSpawnEntries.add(new SwampHagCaveSpawnEntry(10, (short) 120).setHostile(true).setSpawnCheckRadius(24.0D).setGroupSize(1, 3));
		this.blSpawnEntries.add(new SurfaceSpawnEntry(11, EntityWight.class, (short) 8).setHostile(true).setSpawnCheckRadius(64.0D).setSpawnCheckRangeY(16.0D).setSpawningInterval(6000));
		this.blSpawnEntries.add(new CaveSpawnEntry(12, EntityWight.class, (short) 20).setHostile(true).setSpawnCheckRadius(64.0D));
		this.blSpawnEntries.add(new SurfaceSpawnEntry(13, EntityChiromaw.class, (short) 40).setHostile(true).setSpawnCheckRadius(30.0D));
		this.blSpawnEntries.add(new CaveSpawnEntry(14, EntityChiromaw.class, (short) 60).setHostile(true).setSpawnCheckRadius(20.0D).setGroupSize(1, 3));
	}
}
