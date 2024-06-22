package thebetweenlands.common.world.biome;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biome.BiomeCategory;
import net.minecraft.world.level.biome.Biome.Precipitation;
import net.minecraft.world.level.biome.BiomeSpecialEffects;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.GenerationStep;
import thebetweenlands.common.colors.BetweenlandsColorModifiers;
import thebetweenlands.common.registries.CarverRegistry;
import thebetweenlands.common.registries.FeatureRegistries;
import thebetweenlands.common.world.WorldEventsHook;
import thebetweenlands.common.world.gen.biome.decorator.BiomeDecorator;
import thebetweenlands.common.world.gen.biome.feature.SiltBeachFeature;
import thebetweenlands.common.world.gen.biome.generator.BiomeGenerator;

import java.util.List;

public class BiomePatchyIslands extends BiomeBetweenlands {


	public BiomePatchyIslands() {
		super();
		this.setWeight(20);
		this.biomeName = "betweenlands_patchy_islands";
	}

	public Biome biomeBuilder() {
		// Biome carvers and features
		this.biomeGenSettings.addCarver(GenerationStep.Carving.AIR, CarverRegistry.ConfigueredCarvers.CAVES_BETWEENLANDS.getHolder().get())
			.addCarver(GenerationStep.Carving.AIR, CarverRegistry.ConfigueredCarvers.BETWEENLANDS_RAVINES.getHolder().get());
		//.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, FeatureRegistries.PlacedFeatures.SULFUR)
		//.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, FeatureRegistries.PlacedFeatures.BETWEENLANDS_CAVE_VEGETATION.getHolder().get())
		//.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, FeatureRegistries.PlacedFeatures.WEEDWOOD_TREE.getHolder().get())
		//.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, FeatureRegistries.PlacedFeatures.SAP_TREE.getHolder().get());

		// Mob Spawn settings
		this.mobSpawnSettings.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(EntityType.CAVE_SPIDER, 1, 1, 10));

		// Biome colors and other effects
		this.biomeSpecialFX.foliageColorOverride(0x00AD7C)
			.fogColor(0x0a1e16)
			.skyColor(0x78A7FF)
			.waterColor(0x184220)
			.waterFogColor(0x184220)
			.backgroundMusic(MusicHandler.BETWEENLANDS_MUSIC)
			.grassColorModifier(BiomeSpecialEffects.GrassColorModifier.create("betweenlands_patchy_islands", "betweenlands_patchy_islands", (x, z, color) -> (BetweenlandsColorModifiers.Betweenlands2ColorMod((int) x, (int) z, 0x1FC66D, 0x1fc63d, 0.07d))));
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

		this.biomeGenerator = new BiomeGenerator(new BiomeDecorator(List.of(new SiltBeachFeature()
		), this.id));

		return this.biomeBuilder.build();
	}

	public void setFeatures() {
		super.setFeatures();
		WorldEventsHook.regiserFeature(this.biome.getId(), GenerationStep.Decoration.VEGETAL_DECORATION, FeatureRegistries.PlacedFeatures.SPARSE_WEEDWOOD_TREE);
		WorldEventsHook.regiserFeature(this.biome.getId(), GenerationStep.Decoration.VEGETAL_DECORATION, FeatureRegistries.PlacedFeatures.HEATHGROVE_TREE);
		WorldEventsHook.regiserFeature(this.biome.getId(), GenerationStep.Decoration.VEGETAL_DECORATION, FeatureRegistries.PlacedFeatures.SAP_TREE);
		WorldEventsHook.regiserFeature(this.biome.getId(), GenerationStep.Decoration.VEGETAL_DECORATION, FeatureRegistries.PlacedFeatures.SWAMP_TALLGRASS_PATCH);
		WorldEventsHook.regiserFeature(this.biome.getId(), GenerationStep.Decoration.SURFACE_STRUCTURES, FeatureRegistries.PlacedFeatures.IDOL_HEAD);
	}

	public float getBaseHeight() {
		return 120 - 1.25F;
	}

	public float getHeightVariation() {
		return 4.75F;
	}
}
