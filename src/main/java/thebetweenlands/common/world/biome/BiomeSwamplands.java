package thebetweenlands.common.world.biome;

import java.util.List;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;
import thebetweenlands.api.entity.spawning.ICustomSpawnEntry;
import thebetweenlands.common.entity.mobs.EntityAngler;
import thebetweenlands.common.entity.mobs.EntityBloodSnail;
import thebetweenlands.common.entity.mobs.EntityBoulderSprite;
import thebetweenlands.common.entity.mobs.EntityBubblerCrab;
import thebetweenlands.common.entity.mobs.EntityCaveFish;
import thebetweenlands.common.entity.mobs.EntityChiromaw;
import thebetweenlands.common.entity.mobs.EntityChiromawGreeblingRider;
import thebetweenlands.common.entity.mobs.EntityFirefly;
import thebetweenlands.common.entity.mobs.EntityFrog;
import thebetweenlands.common.entity.mobs.EntityGecko;
import thebetweenlands.common.entity.mobs.EntityGiantToad;
import thebetweenlands.common.entity.mobs.EntityLeech;
import thebetweenlands.common.entity.mobs.EntityMireSnail;
import thebetweenlands.common.entity.mobs.EntityOlm;
import thebetweenlands.common.entity.mobs.EntityShambler;
import thebetweenlands.common.entity.mobs.EntitySporeling;
import thebetweenlands.common.entity.mobs.EntityStalker;
import thebetweenlands.common.entity.mobs.EntitySwampHag;
import thebetweenlands.common.entity.mobs.EntitySwarm;
import thebetweenlands.common.entity.mobs.EntityTarBeast;
import thebetweenlands.common.entity.mobs.EntityWight;
import thebetweenlands.common.lib.ModInfo;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.world.WorldProviderBetweenlands;
import thebetweenlands.common.world.biome.spawning.spawners.BetweenstoneCaveSpawnEntry;
import thebetweenlands.common.world.biome.spawning.spawners.CaveSpawnEntry;
import thebetweenlands.common.world.biome.spawning.spawners.GreeblingSpawnEntry;
import thebetweenlands.common.world.biome.spawning.spawners.PitstoneCaveSpawnEntry;
import thebetweenlands.common.world.biome.spawning.spawners.SkySpawnEntry;
import thebetweenlands.common.world.biome.spawning.spawners.SporelingSpawnEntry;
import thebetweenlands.common.world.biome.spawning.spawners.SurfaceSpawnEntry;
import thebetweenlands.common.world.biome.spawning.spawners.SwampHagCaveSpawnEntry;
import thebetweenlands.common.world.biome.spawning.spawners.TarSpawnEntry;
import thebetweenlands.common.world.gen.biome.decorator.BiomeDecoratorSwamplands;
import thebetweenlands.common.world.gen.biome.feature.AlgaeFeature;
import thebetweenlands.common.world.gen.biome.feature.FlatLandFeature;
import thebetweenlands.common.world.gen.biome.feature.PatchFeature;
import thebetweenlands.common.world.gen.biome.feature.SiltBeachFeature;

public class BiomeSwamplands extends BiomeBetweenlands {

	public BiomeSwamplands() {
		super(new ResourceLocation(ModInfo.ID, "swamplands"), 
				new BiomeProperties("Swamplands")
				.setBaseHeight(WorldProviderBetweenlands.LAYER_HEIGHT - 2)
				.setHeightVariation(1F)
				.setWaterColor(0x184220)
				.setTemperature(0.8F)
				.setRainfall(0.9F));
		
		this.setWeight(25);
		this.getBiomeGenerator().setFillerBlockState(BlockRegistry.COARSE_SWAMP_DIRT.getDefaultState())
		.setTopBlockState(BlockRegistry.DEAD_GRASS.getDefaultState())
		.addFeature(new FlatLandFeature(WorldProviderBetweenlands.LAYER_HEIGHT, 8))
		.addFeature(new PatchFeature(0.18D, 0.18D, BlockRegistry.SWAMP_GRASS.getDefaultState()))
		.addFeature(new PatchFeature(0.05D, 0.05D, BlockRegistry.SWAMP_GRASS.getDefaultState()))
		.addFeature(new PatchFeature(0.74D, 0.74D, BlockRegistry.COARSE_SWAMP_DIRT.getDefaultState()))
		.addFeature(new PatchFeature(0.65D, 0.65D, BlockRegistry.MUD.getDefaultState(), 1.0D / 1.35D, 1.72D))
		.addFeature(new AlgaeFeature())
		.addFeature(new SiltBeachFeature(0.98F))
		.setDecorator(new BiomeDecoratorSwamplands(this));
		this.setFoliageColors(0x2AFF00, 0x52AF5A);
		this.setSecondaryFoliageColors(0x50a040, 0x85af51);
	}

	@Override
	public void addTypes() {
		BiomeDictionary.addTypes(this, Type.SWAMP, Type.DENSE, Type.FOREST, Type.WET, Type.WATER);
	}

	@Override
	protected void addSpawnEntries(List<ICustomSpawnEntry> entries) {
		super.addSpawnEntries(entries);

		entries.add(new SurfaceSpawnEntry(0, EntityFirefly.class, EntityFirefly::new, (short) 95).setCanSpawnOnWater(true).setSpawnCheckRadius(32.0D));
		entries.add(new SurfaceSpawnEntry(1, EntityGecko.class, EntityGecko::new, (short) 40).setGroupSize(1, 3).setSpawnCheckRadius(32.0D).setSpawningInterval(600));
		entries.add(new SurfaceSpawnEntry(2, EntityMireSnail.class, EntityMireSnail::new, (short) 60).setGroupSize(1, 5).setSpawnCheckRadius(32.0D).setSpawningInterval(1000));
		entries.add(new SurfaceSpawnEntry(3, EntityFrog.class, EntityFrog::new, (short) 26).setCanSpawnOnWater(true).setGroupSize(1, 3).setSpawnCheckRadius(32.0D).setSpawningInterval(1000));
		entries.add(new CaveSpawnEntry(4, EntityOlm.class, EntityOlm::new, (short) 30).setCanSpawnInWater(true).setGroupSize(3, 5).setSpawnCheckRadius(32.0D));
		entries.add(new SporelingSpawnEntry(5, EntitySporeling.class, EntitySporeling::new, (short) 120).setGroupSize(2, 5).setSpawnCheckRadius(32.0D));
		entries.add(new SurfaceSpawnEntry(6, EntityGiantToad.class, EntityGiantToad::new, (short) 12).setSpawnCheckRadius(64.0D).setSpawningInterval(800));
		entries.add(new CaveSpawnEntry(7, EntityGiantToad.class, EntityGiantToad::new, (short) 8).setSpawnCheckRadius(64.0D).setSpawningInterval(800));
		entries.add(new GreeblingSpawnEntry(8, (short) 20).setGroupSize(1, 3).setSpawnCheckRadius(64.0D).setGroupSpawnRadius(4).setSpawningInterval(24000));
		
		entries.add(new TarSpawnEntry(9, EntityTarBeast.class, EntityTarBeast::new, (short) 80).setHostile(true));
		entries.add(new SurfaceSpawnEntry(10, EntityLeech.class, EntityLeech::new, (short) 35).setHostile(true).setSpawnCheckRadius(24.0D).setSpawningInterval(1000));
		entries.add(new SurfaceSpawnEntry(11, EntityAngler.class, EntityAngler::new, (short) 40).setCanSpawnInWater(true).setHostile(true));
		entries.add(new CaveSpawnEntry(12, EntityAngler.class, EntityAngler::new, (short) 35).setCanSpawnInWater(true).setHostile(true).setGroupSize(1, 3));
		entries.add(new SurfaceSpawnEntry(13, EntitySwampHag.class, EntitySwampHag::new, (short) 90).setHostile(true));
		entries.add(new SwampHagCaveSpawnEntry(14, (short) 140).setHostile(true).setSpawnCheckRadius(24.0D).setGroupSize(1, 3));
		entries.add(new SurfaceSpawnEntry(15, EntityWight.class, EntityWight::new, (short) 24).setHostile(true).setSpawnCheckRadius(64.0D).setSpawnCheckRangeY(16.0D).setSpawningInterval(6000));
		entries.add(new CaveSpawnEntry(16, EntityWight.class, EntityWight::new, (short) 28).setHostile(true).setSpawnCheckRadius(64.0D));
		entries.add(new SurfaceSpawnEntry(17, EntityBloodSnail.class, EntityBloodSnail::new, (short) 25).setHostile(true).setSpawnCheckRadius(32.0D).setSpawningInterval(1000));
		entries.add(new SurfaceSpawnEntry(18, EntityChiromaw.class, EntityChiromaw::new, (short) 40).setHostile(true).setSpawnCheckRadius(30.0D));
		entries.add(new CaveSpawnEntry(19, EntityChiromaw.class, EntityChiromaw::new, (short) 60).setHostile(true).setSpawnCheckRadius(20.0D).setGroupSize(1, 3));
		entries.add(new BetweenstoneCaveSpawnEntry(20, EntityBoulderSprite.class, EntityBoulderSprite::new, (short) 60).setHostile(true).setSpawnCheckRadius(16.0D).setSpawnCheckRangeY(8));
		entries.add(new SkySpawnEntry(21, EntityChiromawGreeblingRider.class, EntityChiromawGreeblingRider::new, (short) 20).setSpawnCheckRadius(64.0D).setGroupSize(1, 3).setSpawningInterval(600).setHostile(true));
		entries.add(new PitstoneCaveSpawnEntry(22, EntityStalker.class, EntityStalker::new, (short) 20).setConstantWeight(true).setHostile(true).setSpawnCheckRadius(64.0D).setSpawnCheckRangeY(16).setSpawningInterval(6000));
		entries.add(new CaveSpawnEntry(23, EntitySwarm.class, EntitySwarm::new, (short) 65).setConstantWeight(true).setHostile(true).setSpawnCheckRadius(32.0D));
		entries.add(new SurfaceSpawnEntry(24, EntityShambler.class, EntityShambler::new, (short) 30).setHostile(true));
		entries.add(new CaveSpawnEntry(25, EntityCaveFish.class, EntityCaveFish::new, (short) 30).setCanSpawnInWater(true).setGroupSize(1, 3).setSpawnCheckRadius(32.0D));
		entries.add(new SurfaceSpawnEntry(26, EntityBubblerCrab.class, EntityBubblerCrab::new, (short) 50).setSurfacePredicate((state) -> state.getBlock() == BlockRegistry.SILT).setHostile(true).setGroupSize(2, 8).setSpawnCheckRadius(32.0D).setSpawnCheckRangeY(16.0D).setSpawningInterval(1000));
	}
}
