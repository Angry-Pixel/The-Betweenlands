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
import thebetweenlands.common.world.gen.biome.feature.CragSpiresFeature;
import thebetweenlands.common.world.gen.biome.generator.BiomeGenerator;

import java.util.List;

public class BiomeDeepWaters extends BiomeBetweenlands {
	
	public BiomeDeepWaters() {
		super();
		this.setWeight(12);
		this.biomeName = "betweenlands_deep_waters";
	}

	public Biome biomeBuilder() {

		// Biome carvers and features
		this.biomeGenSettings.addCarver(GenerationStep.Carving.AIR, CarverRegistry.ConfigueredCarvers.CAVES_BETWEENLANDS.getHolder().get())
				.addCarver(GenerationStep.Carving.AIR, CarverRegistry.ConfigueredCarvers.BETWEENLANDS_RAVINES.getHolder().get());
				//.addFeature(GenerationStep.Decoration.RAW_GENERATION, FeatureRegistries.PlacedFeatures.DEEP_WATERS_CRAG_SPIRES);

		// Mob Spawn settings
		this.mobSpawnSettings.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(EntityType.CAVE_SPIDER, 1, 1, 10));

		// Biome colors and other effects
		this.biomeSpecialFX.foliageColorOverride(0xE5F745)
				.grassColorOverride(15071045)
				.fogColor(0x0a1e16)
				.waterColor(0x1b3944)
				.waterFogColor(0x1b3944)
				.skyColor(0xff78A7FF) // TODO: fix, I think this is causing blue clouds
				.backgroundMusic(MusicHandler.BETWEENLANDS_MUSIC); // TODO: set this in a way that stops creative mode from adding its music

		// Build Biome
		this.biomeBuilder.generationSettings(this.biomeGenSettings.build())
				.mobSpawnSettings(this.mobSpawnSettings.build())
				.specialEffects(this.biomeSpecialFX.build())
				.downfall(0.9F)
				.temperature(0.8F)
				.biomeCategory(BiomeCategory.SWAMP)
				.precipitation(Precipitation.RAIN)
				.build();

		// Make BiomeGenerator
		this.biomeGenerator = new BiomeGenerator(new BiomeDecorator(List.of(new CragSpiresFeature()), this.id));


		return this.biomeBuilder.build();
	}

	// Used for vanilla features
	public void setFeatures() {
		super.setFeatures();
		WorldEventsHook.regiserFeature(this.biome.getId(), GenerationStep.Decoration.RAW_GENERATION, FeatureRegistries.PlacedFeatures.DEEP_WATERS_CRAG_SPIRES);
		//WorldEvents.regiserFeature(this.biome.getId(), GenerationStep.Decoration.RAW_GENERATION, FeatureRegistries.PlacedFeatures.DEEP_WATERS_CRAG_SPIRES);
	}

	public float getBaseHeight() {
		return 120-12;
	}
	public float getHeightVariation() {
		return 5;
	}
}
