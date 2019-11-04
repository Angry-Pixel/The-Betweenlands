package thebetweenlands.common.world.biome;

import java.util.List;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;
import thebetweenlands.api.entity.spawning.ICustomSpawnEntry;
import thebetweenlands.common.entity.mobs.EntityAngler;
import thebetweenlands.common.entity.mobs.EntityBlindCaveFish;
import thebetweenlands.common.entity.mobs.EntityBloodSnail;
import thebetweenlands.common.entity.mobs.EntityBoulderSprite;
import thebetweenlands.common.entity.mobs.EntityChiromaw;
import thebetweenlands.common.entity.mobs.EntityDragonFly;
import thebetweenlands.common.entity.mobs.EntityEmberling;
import thebetweenlands.common.entity.mobs.EntityFirefly;
import thebetweenlands.common.entity.mobs.EntityFrog;
import thebetweenlands.common.entity.mobs.EntityGecko;
import thebetweenlands.common.entity.mobs.EntityLeech;
import thebetweenlands.common.entity.mobs.EntityLurker;
import thebetweenlands.common.entity.mobs.EntityMireSnail;
import thebetweenlands.common.entity.mobs.EntitySiltCrab;
import thebetweenlands.common.entity.mobs.EntitySporeling;
import thebetweenlands.common.entity.mobs.EntitySwampHag;
import thebetweenlands.common.entity.mobs.EntityWight;
import thebetweenlands.common.lib.ModInfo;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.world.WorldProviderBetweenlands;
import thebetweenlands.common.world.biome.spawning.spawners.BetweenstoneCaveSpawnEntry;
import thebetweenlands.common.world.biome.spawning.spawners.CaveSpawnEntry;
import thebetweenlands.common.world.biome.spawning.spawners.GreeblingSpawnEntry;
import thebetweenlands.common.world.biome.spawning.spawners.SporelingSpawnEntry;
import thebetweenlands.common.world.biome.spawning.spawners.SurfaceSpawnEntry;
import thebetweenlands.common.world.biome.spawning.spawners.SwampHagCaveSpawnEntry;
import thebetweenlands.common.world.gen.biome.decorator.BiomeDecoratorPatchyIslands;
import thebetweenlands.common.world.gen.biome.feature.AlgaeFeature;
import thebetweenlands.common.world.gen.biome.feature.SiltBeachFeature;

public class BiomePatchyIslands extends BiomeBetweenlands {

	public BiomePatchyIslands() {
		super(new ResourceLocation(ModInfo.ID, "patchy_islands"),
				new BiomeProperties("Patchy Islands")
				.setBaseHeight(WorldProviderBetweenlands.LAYER_HEIGHT - 1.25F)
				.setHeightVariation(4.75F)
				.setWaterColor(0x184220)
				.setTemperature(0.8F)
				.setRainfall(0.9F));
		
		this.setWeight(20);
		this.getBiomeGenerator().setDecorator(new BiomeDecoratorPatchyIslands(this))
		.addFeature(new SiltBeachFeature())
		.addFeature(new AlgaeFeature());
		this.setFoliageColors(0x1FC66D, 0x00AD7C);
		this.setSecondaryFoliageColors(0x1fc63d, -1);
	}

	@Override
	public void addTypes() {
		BiomeDictionary.addTypes(this, Type.SWAMP, Type.WET, Type.WATER);
	}

	@Override
	protected void addSpawnEntries(List<ICustomSpawnEntry> entries) {
		super.addSpawnEntries(entries);

		entries.add(new SurfaceSpawnEntry(0, EntityDragonFly.class, EntityDragonFly::new, (short) 35).setCanSpawnOnWater(true).setGroupSize(1, 3).setSpawnCheckRadius(64.0D).setSpawningInterval(400));
		entries.add(new SurfaceSpawnEntry(1, EntityFirefly.class, EntityFirefly::new, (short) 60).setCanSpawnOnWater(true).setSpawnCheckRadius(32.0D));
		entries.add(new SurfaceSpawnEntry(2, EntityMireSnail.class, EntityMireSnail::new, (short) 60).setGroupSize(1, 5).setSpawnCheckRadius(32.0D).setSpawningInterval(1000));
		entries.add(new SurfaceSpawnEntry(3, EntityFrog.class, EntityFrog::new, (short) 32).setCanSpawnOnWater(true).setGroupSize(1, 3).setSpawnCheckRadius(32.0D).setSpawningInterval(1000));
		entries.add(new CaveSpawnEntry(4, EntityBlindCaveFish.class, EntityBlindCaveFish::new, (short) 30).setCanSpawnInWater(true).setGroupSize(3, 5).setSpawnCheckRadius(32.0D));
		entries.add(new SurfaceSpawnEntry(5, EntityGecko.class, EntityGecko::new, (short) 40).setGroupSize(1, 3).setSpawnCheckRadius(32.0D).setSpawningInterval(600));
		entries.add(new SporelingSpawnEntry(6, EntitySporeling.class, EntitySporeling::new, (short) 80).setGroupSize(2, 5).setSpawnCheckRadius(32.0D));
		entries.add(new GreeblingSpawnEntry(20, (short) 20).setGroupSize(1, 3).setSpawnCheckRadius(64.0D).setGroupSpawnRadius(4).setSpawningInterval(24000));
		
		entries.add(new SurfaceSpawnEntry(21, EntityEmberling.class, EntityEmberling::new, (short) 20).setSurfacePredicate(state -> state.getBlock() == BlockRegistry.LOG_HEARTHGROVE).setHostile(true).setGroupSize(1, 1).setSpawnCheckRadius(32));
		entries.add(new SurfaceSpawnEntry(7, EntityLurker.class, EntityLurker::new, (short) 35).setCanSpawnInWater(true).setHostile(true).setSpawnCheckRadius(16.0D));
		entries.add(new SurfaceSpawnEntry(8, EntityAngler.class, EntityAngler::new, (short) 45).setCanSpawnInWater(true).setHostile(true).setGroupSize(1, 3));
		entries.add(new CaveSpawnEntry(9, EntityAngler.class, EntityAngler::new, (short) 35).setCanSpawnInWater(true).setHostile(true).setGroupSize(1, 3));
		entries.add(new SurfaceSpawnEntry(10, EntitySwampHag.class, EntitySwampHag::new, (short) 90).setHostile(true));
		entries.add(new SwampHagCaveSpawnEntry(11, (short) 80).setHostile(true).setSpawnCheckRadius(24.0D).setGroupSize(1, 3));
		entries.add(new SurfaceSpawnEntry(12, EntityWight.class, EntityWight::new, (short) 16).setHostile(true).setSpawnCheckRadius(64.0D).setSpawnCheckRangeY(16.0D).setSpawningInterval(6000));
		entries.add(new CaveSpawnEntry(13, EntityWight.class, EntityWight::new, (short) 18).setHostile(true).setSpawnCheckRadius(64.0D));
		entries.add(new SurfaceSpawnEntry(14, EntitySiltCrab.class, EntitySiltCrab::new, (short) 50).setSurfacePredicate((state) -> state.getBlock() == BlockRegistry.SILT).setHostile(true).setGroupSize(2, 8).setSpawnCheckRadius(32.0D).setSpawnCheckRangeY(16.0D).setSpawningInterval(1000));
		entries.add(new SurfaceSpawnEntry(15, EntityBloodSnail.class, EntityBloodSnail::new, (short) 30).setHostile(true).setSpawnCheckRadius(32.0D).setSpawningInterval(1000));
		entries.add(new SurfaceSpawnEntry(16, EntityLeech.class, EntityLeech::new, (short) 35).setHostile(true).setSpawnCheckRadius(24.0D).setSpawningInterval(1000));
		entries.add(new SurfaceSpawnEntry(17, EntityChiromaw.class, EntityChiromaw::new, (short) 40).setHostile(true).setSpawnCheckRadius(30.0D));
		entries.add(new CaveSpawnEntry(18, EntityChiromaw.class, EntityChiromaw::new, (short) 60).setHostile(true).setSpawnCheckRadius(20.0D).setGroupSize(1, 3));
		entries.add(new BetweenstoneCaveSpawnEntry(19, EntityBoulderSprite.class, EntityBoulderSprite::new, (short) 60).setHostile(true).setSpawnCheckRadius(16.0D).setSpawnCheckRangeY(8));
	}
}
