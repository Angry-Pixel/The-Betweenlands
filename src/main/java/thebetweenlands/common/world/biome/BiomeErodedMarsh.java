package thebetweenlands.common.world.biome;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.biome.Biome.BiomeCategory;
import net.minecraft.world.level.biome.Biome.Precipitation;
import net.minecraft.world.level.levelgen.GenerationStep;
import thebetweenlands.common.registries.*;
import thebetweenlands.common.world.WorldEventsHook;
import thebetweenlands.common.world.gen.biome.decorator.BiomeDecorator;
import thebetweenlands.common.world.gen.biome.feature.ErodedMarshFeature;
import thebetweenlands.common.world.gen.biome.feature.PatchFeature;
import thebetweenlands.common.world.gen.biome.generator.BiomeGenerator;

import java.util.List;

public class BiomeErodedMarsh extends BiomeBetweenlands {
	
	
	public BiomeErodedMarsh() {
		super();
		this.setWeight(4);
		this.biomeName = "betweenlands_eroded_marsh";
	}

	public Biome biomeBuilder() {
		// Biome carvers and features
		this.biomeGenSettings.addCarver(GenerationStep.Carving.AIR, CarverRegistry.ConfigueredCarvers.CAVES_BETWEENLANDS.getHolder().get())
				.addCarver(GenerationStep.Carving.AIR, CarverRegistry.ConfigueredCarvers.BETWEENLANDS_RAVINES.getHolder().get());
		//
		// .addFeature(new PatchFeature(0.03125D * 3.5D, 0.03125D * 3.5D, BlockRegistry.PEAT.getDefaultState()))
		//		.addFeature(new PatchFeature(0.03125D * 12.5D, 0.03125D * 12.5D, BlockRegistry.PEAT.getDefaultState()))
		//		.addFeature(new PatchFeature(0.03125D * 5.5D, 0.03125D * 5.5D, BlockRegistry.MUD.getDefaultState()))
		//		.addFeature(new PatchFeature(0.03125D * 8.5D, 0.03125D * 8.5D, BlockRegistry.MUD.getDefaultState()))
		//.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, FeatureRegistries.PlacedFeatures.SULFUR)
		//.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, FeatureRegistries.PlacedFeatures.BETWEENLANDS_CAVE_VEGETATION.getHolder().get())
		//.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, FeatureRegistries.PlacedFeatures.WEEDWOOD_TREE.getHolder().get());
		//.addFeature(new FlatLandFeature(WorldProviderBetweenlands.LAYER_HEIGHT, 8))
		//.addFeature(new PatchFeature(0.18D, 0.18D, BlockRegistry.SWAMP_GRASS.getDefaultState()))
		//.addFeature(new PatchFeature(0.05D, 0.05D, BlockRegistry.SWAMP_GRASS.getDefaultState()))
		//.addFeature(new PatchFeature(0.74D, 0.74D, BlockRegistry.COARSE_SWAMP_DIRT.getDefaultState()))
		//.addFeature(new PatchFeature(0.65D, 0.65D, BlockRegistry.MUD.getDefaultState(), 1.0D / 1.35D, 1.72D))
		//.addFeature(new AlgaeFeature())
		//.addFeature(new SiltBeachFeature(0.98F))
		//.setDecorator(new BiomeDecoratorSwamplands(this));

		// Mob Spawn settings
		this.mobSpawnSettings.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(EntityType.CAVE_SPIDER, 1, 1, 10));

		// Biome colors and other effects
		this.biomeSpecialFX.foliageColorOverride(0x63B581)
				.fogColor(0x0a1e16)
				.waterColor(0x485E18)
				.waterFogColor(0x485E18)
				.skyColor(0xff78A7FF)
				.grassColorOverride(0x627017)
				.backgroundMusic(MusicHandler.BETWEENLANDS_MUSIC);
		//this.setFoliageColors(0x2AFF00, 0x52AF5A);
		//this.setSecondaryFoliageColors(0x50a040, 0x85af51);

		// Build Biome
		this.biomeBuilder.generationSettings(this.biomeGenSettings.build())
				.mobSpawnSettings(this.mobSpawnSettings.build())
				.specialEffects(this.biomeSpecialFX.build())
				.downfall(0.9f)
				.temperature(0.8f)
				.biomeCategory(BiomeCategory.SWAMP)
				.precipitation(Precipitation.RAIN)
				.build();

		// Make BiomeGenerator
		this.biomeGenerator = new BiomeGenerator(new BiomeDecorator(List.of(new ErodedMarshFeature(),
				new PatchFeature(0.03125D * 3.5D, 0.03125D * 3.5D, BlockRegistry.PEAT.get().defaultBlockState()),
				new PatchFeature(0.03125D * 12.5D, 0.03125D * 12.5D, BlockRegistry.PEAT.get().defaultBlockState()),
				new PatchFeature(0.03125D * 5.5D, 0.03125D * 5.5D, BlockRegistry.MUD.get().defaultBlockState()),
				new PatchFeature(0.03125D * 8.5D, 0.03125D * 8.5D, BlockRegistry.MUD.get().defaultBlockState())
		), this.id));

        return this.biomeBuilder.build();
    }

	public void setFeatures() {
		super.setFeatures();
		WorldEventsHook.regiserFeature(this.biome.getId(), GenerationStep.Decoration.VEGETAL_DECORATION, FeatureRegistries.PlacedFeatures.RARE_WEEDWOOD_TREE);
		WorldEventsHook.regiserFeature(this.biome.getId(), GenerationStep.Decoration.VEGETAL_DECORATION, FeatureRegistries.PlacedFeatures.SWAMP_TALLGRASS_PATCH);
	}

	public float getBaseHeight() {
		return 120-1;
	}
	public float getHeightVariation() {
		return 1.1f;
	}
}
