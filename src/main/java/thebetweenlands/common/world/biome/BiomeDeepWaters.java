package thebetweenlands.common.world.biome;

import java.util.List;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;
import thebetweenlands.api.entity.spawning.ICustomSpawnEntry;
import thebetweenlands.common.entity.mobs.EntityAnadia;
import thebetweenlands.common.entity.mobs.EntityAngler;
import thebetweenlands.common.entity.mobs.EntityBoulderSprite;
import thebetweenlands.common.entity.mobs.EntityCaveFish;
import thebetweenlands.common.entity.mobs.EntityChiromaw;
import thebetweenlands.common.entity.mobs.EntityChiromawGreeblingRider;
import thebetweenlands.common.entity.mobs.EntityFirefly;
import thebetweenlands.common.entity.mobs.EntityFreshwaterUrchin;
import thebetweenlands.common.entity.mobs.EntityGreeblingCoracle;
import thebetweenlands.common.entity.mobs.EntityJellyfish;
import thebetweenlands.common.entity.mobs.EntityJellyfishCave;
import thebetweenlands.common.entity.mobs.EntityLurker;
import thebetweenlands.common.entity.mobs.EntityOlm;
import thebetweenlands.common.entity.mobs.EntityRockSnot;
import thebetweenlands.common.entity.mobs.EntitySporeling;
import thebetweenlands.common.entity.mobs.EntityStalker;
import thebetweenlands.common.entity.mobs.EntitySwarm;
import thebetweenlands.common.entity.mobs.EntityWight;
import thebetweenlands.common.lib.ModInfo;
import thebetweenlands.common.world.WorldProviderBetweenlands;
import thebetweenlands.common.world.biome.spawning.spawners.BetweenstoneCaveSpawnEntry;
import thebetweenlands.common.world.biome.spawning.spawners.CaveSpawnEntry;
import thebetweenlands.common.world.biome.spawning.spawners.PitstoneCaveSpawnEntry;
import thebetweenlands.common.world.biome.spawning.spawners.SkySpawnEntry;
import thebetweenlands.common.world.biome.spawning.spawners.SporelingSpawnEntry;
import thebetweenlands.common.world.biome.spawning.spawners.SurfaceSpawnEntry;
import thebetweenlands.common.world.biome.spawning.spawners.SwampHagCaveSpawnEntry;
import thebetweenlands.common.world.gen.biome.decorator.BiomeDecoratorDeepWaters;
import thebetweenlands.common.world.gen.biome.decorator.SurfaceType;
import thebetweenlands.common.world.gen.biome.feature.AlgaeFeature;
import thebetweenlands.common.world.gen.biome.feature.CragSpiresFeature;
import thebetweenlands.common.world.gen.biome.feature.DeepWatersFeature;

public class BiomeDeepWaters extends BiomeBetweenlands {

	public BiomeDeepWaters() {
		super(new ResourceLocation(ModInfo.ID, "deep_waters"),
				new BiomeProperties("Deep Waters")
				.setBaseHeight(WorldProviderBetweenlands.LAYER_HEIGHT - 12)
				.setHeightVariation(5.0F)
				.setWaterColor(0x1b3944)
				.setTemperature(0.8F)
				.setRainfall(0.9F));
		
		this.setWeight(12);
		this.getBiomeGenerator().setDecorator(new BiomeDecoratorDeepWaters(this))
		.addFeature(new DeepWatersFeature())
		.addFeature(new AlgaeFeature())
		.addFeature(new CragSpiresFeature());
		this.setFoliageColors(0xE5F745, 0xE5F745);
	}

	@Override
	public void addTypes() {
		BiomeDictionary.addTypes(this, Type.SWAMP, Type.WATER);
	}

	@Override
	protected void addSpawnEntries(List<ICustomSpawnEntry> entries) {
		super.addSpawnEntries(entries);

		entries.add(new SurfaceSpawnEntry(0, EntityFirefly.class, EntityFirefly::new, (short) 10).setSpawnCheckRadius(32.0D));
		entries.add(new SporelingSpawnEntry(1, EntitySporeling.class, EntitySporeling::new, (short) 80).setGroupSize(2, 5).setSpawnCheckRadius(32.0D));
		entries.add(new CaveSpawnEntry(2, EntityOlm.class, EntityOlm::new, (short) 30).setCanSpawnInWater(true).setGroupSize(2, 4).setSpawnCheckRadius(32.0D));

		entries.add(new SurfaceSpawnEntry(3, EntityLurker.class, EntityLurker::new, (short) 35).setCanSpawnInWater(true).setHostile(true).setSpawnCheckRadius(16.0D));
		entries.add(new SurfaceSpawnEntry(4, EntityAngler.class, EntityAngler::new, (short) 45).setCanSpawnInWater(true).setHostile(true).setGroupSize(1, 3));
		entries.add(new CaveSpawnEntry(5, EntityAngler.class, EntityAngler::new, (short) 35).setCanSpawnInWater(true).setHostile(true).setGroupSize(1, 3));
		entries.add(new SwampHagCaveSpawnEntry(6, (short) 120).setHostile(true).setSpawnCheckRadius(24.0D).setGroupSize(1, 3));
		entries.add(new CaveSpawnEntry(7, EntityWight.class, EntityWight::new, (short) 18).setHostile(true).setSpawnCheckRadius(64.0D));
		entries.add(new CaveSpawnEntry(8, EntityChiromaw.class, EntityChiromaw::new, (short) 60).setHostile(true).setSpawnCheckRadius(20.0D).setGroupSize(1, 3));
		entries.add(new BetweenstoneCaveSpawnEntry(9, EntityBoulderSprite.class, EntityBoulderSprite::new, (short) 60).setHostile(true).setSpawnCheckRadius(16.0D).setSpawnCheckRangeY(8));
		entries.add(new SkySpawnEntry(10, EntityChiromawGreeblingRider.class, EntityChiromawGreeblingRider::new, (short) 20).setSpawnCheckRadius(64.0D).setGroupSize(1, 3).setSpawningInterval(600).setHostile(true));
		entries.add(new PitstoneCaveSpawnEntry(11, EntityStalker.class, EntityStalker::new, (short) 12).setConstantWeight(true).setHostile(true).setSpawnCheckRadius(64.0D).setSpawnCheckRangeY(16).setSpawningInterval(6000));
		entries.add(new CaveSpawnEntry(12, EntitySwarm.class, EntitySwarm::new, (short) 60).setConstantWeight(true).setHostile(true).setSpawnCheckRadius(32.0D));
		entries.add(new SurfaceSpawnEntry(13, EntityAnadia.class, EntityAnadia::new, (short) 60).setCanSpawnInWater(true).setHostile(false).setGroupSize(1, 5));
		entries.add(new SurfaceSpawnEntry(14, EntityFreshwaterUrchin.class, EntityFreshwaterUrchin::new, (short) 60).setSurfacePredicate(SurfaceType.DIRT).setCanSpawnInWater(true).setHostile(false).setGroupSize(1, 3));
		entries.add(new SurfaceSpawnEntry(15, EntityJellyfish.class, EntityJellyfish::new, (short) 60).setCanSpawnInWater(true).setHostile(false).setGroupSize(1, 3));
		entries.add(new CaveSpawnEntry(16, EntityJellyfishCave.class, EntityJellyfishCave::new, (short) 25).setCanSpawnInWater(true).setGroupSize(1, 3).setSpawnCheckRadius(32.0D));
		entries.add(new SurfaceSpawnEntry(17, EntityRockSnot.class, EntityRockSnot::new, (short) 30).setSurfacePredicate(SurfaceType.MIXED_GROUND).setCanSpawnInWater(true).setHostile(true).setGroupSize(1, 1));
		entries.add(new CaveSpawnEntry(18, EntityCaveFish.class, EntityCaveFish::new, (short) 30).setCanSpawnInWater(true).setGroupSize(1, 3).setSpawnCheckRadius(32.0D));
		entries.add(new SurfaceSpawnEntry(19, EntityGreeblingCoracle.class, EntityGreeblingCoracle::new, (short) 5).setCanSpawnOnWater(true).setHostile(false).setGroupSize(1, 1).setSpawnCheckRadius(32.0D));
	}
}
