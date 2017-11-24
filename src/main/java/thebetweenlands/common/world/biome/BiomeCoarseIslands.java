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

		this.blSpawnEntries.add(new SurfaceSpawnEntry(EntityDragonFly.class, (short) 36).setCanSpawnOnWater(true).setGroupSize(1, 2).setSpawnCheckRadius(32.0D).setSpawningInterval(400));
		this.blSpawnEntries.add(new SurfaceSpawnEntry(EntityFirefly.class, (short) 25).setSpawnCheckRadius(32.0D));
		this.blSpawnEntries.add(new SurfaceSpawnEntry(EntityGecko.class, (short) 52).setGroupSize(1, 3).setSpawnCheckRadius(32.0D).setSpawningInterval(600));
		this.blSpawnEntries.add(new SurfaceSpawnEntry(EntityFrog.class, (short) 30).setCanSpawnOnWater(true).setGroupSize(1, 3).setSpawnCheckRadius(32.0D).setSpawningInterval(100));
		this.blSpawnEntries.add(new CaveSpawnEntry(EntityBlindCaveFish.class, (short) 30).setCanSpawnInWater(true).setGroupSize(3, 5).setSpawnCheckRadius(32.0D));
		this.blSpawnEntries.add(new TreeSpawnEntry(EntitySporeling.class, (short) 80).setGroupSize(2, 5).setSpawnCheckRadius(32.0D));

		this.blSpawnEntries.add(new SurfaceSpawnEntry(EntityLurker.class, (short) 32).setCanSpawnInWater(true).setHostile(true).setSpawnCheckRadius(16.0D));
		this.blSpawnEntries.add(new SurfaceSpawnEntry(EntityAngler.class, (short) 42).setCanSpawnInWater(true).setHostile(true).setGroupSize(1, 3));
		this.blSpawnEntries.add(new CaveSpawnEntry(EntityAngler.class, (short) 35).setCanSpawnInWater(true).setHostile(true).setGroupSize(1, 3));
		this.blSpawnEntries.add(new SurfaceSpawnEntry(EntitySwampHag.class, (short) 90).setHostile(true));
		this.blSpawnEntries.add(new SwampHagCaveSpawnEntry((short) 80).setHostile(true).setSpawnCheckRadius(24.0D).setGroupSize(1, 3));
		this.blSpawnEntries.add(new SurfaceSpawnEntry(EntityWight.class, (short) 12).setHostile(true).setSpawnCheckRadius(64.0D));
		this.blSpawnEntries.add(new CaveSpawnEntry(EntityWight.class, (short) 18).setHostile(true).setSpawnCheckRadius(64.0D));
		this.blSpawnEntries.add(new SurfaceSpawnEntry(EntityChiromaw.class, (short) 40).setHostile(true).setSpawnCheckRadius(30.0D));
		this.blSpawnEntries.add(new CaveSpawnEntry(EntityChiromaw.class, (short) 40).setHostile(true).setSpawnCheckRadius(20.0D).setGroupSize(1, 3));
	}
}
