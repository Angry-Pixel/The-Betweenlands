package thebetweenlands.common.world.biome;

import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biome.BiomeCategory;
import net.minecraft.world.level.biome.Biome.Precipitation;
import net.minecraft.world.level.biome.BiomeSpecialEffects;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.GenerationStep;
import thebetweenlands.common.colors.BetweenlandsColorModifiers;
import thebetweenlands.common.registries.*;
import thebetweenlands.common.world.WorldEventsHook;

public class BiomeSwamplandsClearing extends BiomeBetweenlands {


	public BiomeSwamplandsClearing() {
		super();
		this.setWeight(0);
		this.biomeName = "betweenlands_swamplands_clearing";
	}

	public Biome biomeBuilder() {
		// Biome carvers and features
		this.biomeGenSettings.addCarver(GenerationStep.Carving.AIR, CarverRegistry.ConfigueredCarvers.CAVES_BETWEENLANDS.getHolder().get())
				.addCarver(GenerationStep.Carving.AIR, CarverRegistry.ConfigueredCarvers.BETWEENLANDS_RAVINES.getHolder().get());
		//.addFeature(GenerationStep.Decoration.TOP_LAYER_MODIFICATION, new RandomPatchFeature(0.18D, 0.18D, BlockRegistry.SWAMP_GRASS.get().defaultBlockState()))
		//.addFeature(GenerationStep.Decoration.TOP_LAYER_MODIFICATION, new RandomPatchFeature(0.05D, 0.05D, BlockRegistry.SWAMP_GRASS.get().defaultBlockState()))
		//.addFeature(GenerationStep.Decoration.TOP_LAYER_MODIFICATION, new RandomPatchFeature(0.74D, 0.74D, BlockRegistry.SWAMP_DIRT.get().defaultBlockState()))
		//.addFeature(GenerationStep.Decoration.TOP_LAYER_MODIFICATION, new RandomPatchFeature(0.65D, 0.65D, BlockRegistry.MUD.get().defaultBlockState(), 1.0D / 1.35D, 1.72D));

		//.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, FeatureRegistries.PlacedFeatures.SULFUR)
		//.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, FeatureRegistries.PlacedFeatures.BETWEENLANDS_CAVE_VEGETATION.getHolder().get())
		//.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, FeatureRegistries.PlacedFeatures.WEEDWOOD_TREE.getHolder().get())
		//.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, FeatureRegistries.PlacedFeatures.RUBBER_TREE.getHolder().get())
		//.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, FeatureRegistries.PlacedFeatures.SAP_TREE.getHolder().get());
		//.addFeature(new FlatLandFeature(WorldProviderBetweenlands.LAYER_HEIGHT, 8))
		//.addFeature(new RandomPatchFeature(0.18D, 0.18D, BlockRegistries.SWAMP_GRASS.get().defaultBlockState()));
		//.addFeature(new PatchFeature(0.05D, 0.05D, BlockRegistry.SWAMP_GRASS.getDefaultState()))
		//.addFeature(new PatchFeature(0.74D, 0.74D, BlockRegistry.COARSE_SWAMP_DIRT.getDefaultState()))
		//.addFeature(new PatchFeature(0.65D, 0.65D, BlockRegistry.MUD.getDefaultState(), 1.0D / 1.35D, 1.72D))
		//.addFeature(new AlgaeFeature())
		//.addFeature(new SiltBeachFeature(0.98F))
		//.setDecorator(new BiomeDecoratorSwamplands(this));

		// Mob Spawn settings

		//entries.add(new SurfaceSpawnEntry(13, EntitySwampHag.class, EntitySwampHag::new, (short) 90).setHostile(true));
		//this.mobSpawnSettings.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(EntityRegistries.SWAMP_HAG.get(), 90, 1, 3));

		// Biome colors and other effects
		this.biomeSpecialFX.foliageColorOverride(5418842)
				.fogColor(0x0a1e16)
				.waterColor(0x184220)
				.waterFogColor(0x184220)
				.skyColor(0xff78A7FF)
				.backgroundMusic(MusicHandler.BETWEENLANDS_MUSIC)
				.grassColorModifier(BiomeSpecialEffects.GrassColorModifier.create("betweenlands_swamplands", "betweenlands_swamplands", (x, z, color) -> ((BetweenlandsColorModifiers.Betweenlands2ColorMod((int)x, (int)z, 2817792, 5283904, 0.07d)))));
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
        return this.biomeBuilder.build();
    }

	public void setFeatures() {
		super.setFeatures();
		WorldEventsHook.regiserFeature(this.biome.getId(), GenerationStep.Decoration.VEGETAL_DECORATION, FeatureRegistries.PlacedFeatures.SWAMP_TALLGRASS_PATCH);
	}

	public void setSpawns() {
		super.setSpawns();
		WorldEventsHook.regiserSpawn(BiomeRegistry.SWAMPLANDS_CLEARING.biome.getId(), MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(EntityRegistry.SWAMP_HAG.get(), 90, 1, 3));
	}

	// helps clean up some boilerplate stuff
	public void spawnHelper(MobCategory catagory, MobSpawnSettings.SpawnerData spawnSettings) {
		WorldEventsHook.regiserSpawn(BiomeRegistry.SWAMPLANDS_CLEARING.biome.getId(), catagory, spawnSettings);
	}

	public float getBaseHeight() {
		return 120+2;
	}
	public float getHeightVariation() {
		return 1;
	}
}
