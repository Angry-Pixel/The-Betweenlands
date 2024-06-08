package net.minecraft.data.worldgen.biome;

import net.minecraft.data.worldgen.BiomeDefaultFeatures;
import net.minecraft.data.worldgen.placement.EndPlacements;
import net.minecraft.world.level.biome.AmbientMoodSettings;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.biome.BiomeSpecialEffects;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.GenerationStep;

public class EndBiomes {
   private static Biome baseEndBiome(BiomeGenerationSettings.Builder p_194825_) {
      MobSpawnSettings.Builder mobspawnsettings$builder = new MobSpawnSettings.Builder();
      BiomeDefaultFeatures.endSpawns(mobspawnsettings$builder);
      return (new Biome.BiomeBuilder()).precipitation(Biome.Precipitation.NONE).biomeCategory(Biome.BiomeCategory.THEEND).temperature(0.5F).downfall(0.5F).specialEffects((new BiomeSpecialEffects.Builder()).waterColor(4159204).waterFogColor(329011).fogColor(10518688).skyColor(0).ambientMoodSound(AmbientMoodSettings.LEGACY_CAVE_SETTINGS).build()).mobSpawnSettings(mobspawnsettings$builder.build()).generationSettings(p_194825_.build()).build();
   }

   public static Biome endBarrens() {
      BiomeGenerationSettings.Builder biomegenerationsettings$builder = new BiomeGenerationSettings.Builder();
      return baseEndBiome(biomegenerationsettings$builder);
   }

   public static Biome theEnd() {
      BiomeGenerationSettings.Builder biomegenerationsettings$builder = (new BiomeGenerationSettings.Builder()).addFeature(GenerationStep.Decoration.SURFACE_STRUCTURES, EndPlacements.END_SPIKE);
      return baseEndBiome(biomegenerationsettings$builder);
   }

   public static Biome endMidlands() {
      BiomeGenerationSettings.Builder biomegenerationsettings$builder = new BiomeGenerationSettings.Builder();
      return baseEndBiome(biomegenerationsettings$builder);
   }

   public static Biome endHighlands() {
      BiomeGenerationSettings.Builder biomegenerationsettings$builder = (new BiomeGenerationSettings.Builder()).addFeature(GenerationStep.Decoration.SURFACE_STRUCTURES, EndPlacements.END_GATEWAY_RETURN).addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, EndPlacements.CHORUS_PLANT);
      return baseEndBiome(biomegenerationsettings$builder);
   }

   public static Biome smallEndIslands() {
      BiomeGenerationSettings.Builder biomegenerationsettings$builder = (new BiomeGenerationSettings.Builder()).addFeature(GenerationStep.Decoration.RAW_GENERATION, EndPlacements.END_ISLAND_DECORATED);
      return baseEndBiome(biomegenerationsettings$builder);
   }
}