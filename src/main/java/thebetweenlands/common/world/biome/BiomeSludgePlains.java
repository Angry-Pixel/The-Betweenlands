package thebetweenlands.common.world.biome;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;
import thebetweenlands.common.entity.mobs.EntityAngler;
import thebetweenlands.common.entity.mobs.EntityBlindCaveFish;
import thebetweenlands.common.entity.mobs.EntityChiromaw;
import thebetweenlands.common.entity.mobs.EntityFirefly;
import thebetweenlands.common.entity.mobs.EntityLeech;
import thebetweenlands.common.entity.mobs.EntityPeatMummy;
import thebetweenlands.common.entity.mobs.EntitySludge;
import thebetweenlands.common.entity.mobs.EntityTarBeast;
import thebetweenlands.common.entity.mobs.EntityWight;
import thebetweenlands.common.lib.ModInfo;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.world.WorldProviderBetweenlands;
import thebetweenlands.common.world.biome.spawning.spawners.CaveSpawnEntry;
import thebetweenlands.common.world.biome.spawning.spawners.SurfaceSpawnEntry;
import thebetweenlands.common.world.biome.spawning.spawners.SwampHagCaveSpawnEntry;
import thebetweenlands.common.world.biome.spawning.spawners.TarSpawnEntry;
import thebetweenlands.common.world.gen.biome.decorator.BiomeDecoratorSludgePlains;
import thebetweenlands.common.world.gen.biome.feature.FlatLandFeature;
import thebetweenlands.common.world.gen.biome.feature.MiddleGemFeature;
import thebetweenlands.common.world.gen.biome.feature.PatchFeature;

public class BiomeSludgePlains extends BiomeBetweenlands {

	public BiomeSludgePlains() {
		super(new ResourceLocation(ModInfo.ID, "sludge_plains"), 
				new BiomeProperties("Sludge Plains")
				.setBaseHeight(WorldProviderBetweenlands.LAYER_HEIGHT - 5)
				.setHeightVariation(3F)
				.setWaterColor(0x3A2F0B)
				.setTemperature(0.8F)
				.setRainfall(0.9F));
		
		this.setWeight(5);
		this.getBiomeGenerator().setTopBlockState(BlockRegistry.MUD.getDefaultState()).setFillerBlockHeight(1).setDecorator(new BiomeDecoratorSludgePlains(this))
		.addFeature(new FlatLandFeature(WorldProviderBetweenlands.LAYER_HEIGHT, 5))
		.addFeature(new PatchFeature(0.03125D * 5.75D, 0.03125D * 5.75D, BlockRegistry.SLUDGY_DIRT.getDefaultState()))
		.addFeature(new PatchFeature(0.74D, 0.74D, BlockRegistry.SWAMP_DIRT.getDefaultState()))
		.addFeature(new PatchFeature(0.65D, 0.65D, BlockRegistry.MUD.getDefaultState(), 1.0D / 1.35D, 1.72D))
		.addFeature(new MiddleGemFeature());
		this.setFoliageColors(0x5B3522, 0x5B3522);
	}

	@Override
	public void addTypes() {
		BiomeDictionary.addTypes(this, Type.SWAMP, Type.WET, Type.WASTELAND, Type.DEAD);

	}

	@Override
	protected void addSpawnEntries() {
		super.addSpawnEntries();

		this.blSpawnEntries.add(new SurfaceSpawnEntry(0, EntityFirefly.class, (short) 20).setGroupSize(1, 3));
		this.blSpawnEntries.add(new CaveSpawnEntry(1, EntityBlindCaveFish.class, (short) 30).setCanSpawnInWater(true).setGroupSize(3, 5));

		this.blSpawnEntries.add(new TarSpawnEntry(2, EntityTarBeast.class, (short) 120).setHostile(true).setGroupSize(1, 2).setSpawnCheckRadius(14.0D));
		this.blSpawnEntries.add(new SurfaceSpawnEntry(3, EntityLeech.class, (short) 50).setHostile(true).setSpawnCheckRadius(18.0D).setSpawningInterval(800));
		this.blSpawnEntries.add(new SurfaceSpawnEntry(4, EntitySludge.class, (short) 64).setHostile(true).setGroupSize(1, 3).setGroupSpawnRadius(4.0D));
		this.blSpawnEntries.add(new SurfaceSpawnEntry(5, EntityPeatMummy.class, (short) 26).setHostile(true).setSpawnCheckRadius(64.0D));
		this.blSpawnEntries.add(new SwampHagCaveSpawnEntry(6, (short) 140).setHostile(true).setSpawnCheckRadius(24.0D).setGroupSize(1, 3));
		this.blSpawnEntries.add(new SurfaceSpawnEntry(7, EntityWight.class, (short) 15).setHostile(true).setSpawnCheckRadius(64.0D));
		this.blSpawnEntries.add(new CaveSpawnEntry(8, EntityWight.class, (short) 18).setHostile(true).setSpawnCheckRadius(64.0D));
		this.blSpawnEntries.add(new SurfaceSpawnEntry(9, EntityChiromaw.class, (short) 30).setHostile(true).setSpawnCheckRadius(30.0D));
		this.blSpawnEntries.add(new CaveSpawnEntry(10, EntityChiromaw.class, (short) 60).setHostile(true).setSpawnCheckRadius(20.0D).setGroupSize(1, 3));
		this.blSpawnEntries.add(new CaveSpawnEntry(11, EntityAngler.class, (short) 45).setCanSpawnInWater(true).setHostile(true).setGroupSize(1, 3));
	}
}
