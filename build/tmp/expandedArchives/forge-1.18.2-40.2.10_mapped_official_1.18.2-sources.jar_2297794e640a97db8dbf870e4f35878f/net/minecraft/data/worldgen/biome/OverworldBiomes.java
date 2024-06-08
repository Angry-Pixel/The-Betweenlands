package net.minecraft.data.worldgen.biome;

import javax.annotation.Nullable;
import net.minecraft.data.worldgen.BiomeDefaultFeatures;
import net.minecraft.data.worldgen.placement.AquaticPlacements;
import net.minecraft.data.worldgen.placement.MiscOverworldPlacements;
import net.minecraft.data.worldgen.placement.VegetationPlacements;
import net.minecraft.sounds.Music;
import net.minecraft.sounds.Musics;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.AmbientMoodSettings;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.biome.BiomeSpecialEffects;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.GenerationStep;

public class OverworldBiomes {
   // TODO: getAdditionalOverworldBiomes, likely in this class. -C

   protected static final int NORMAL_WATER_COLOR = 4159204;
   protected static final int NORMAL_WATER_FOG_COLOR = 329011;
   private static final int OVERWORLD_FOG_COLOR = 12638463;
   @Nullable
   private static final Music NORMAL_MUSIC = null;

   protected static int calculateSkyColor(float p_194844_) {
      float $$1 = p_194844_ / 3.0F;
      $$1 = Mth.clamp($$1, -1.0F, 1.0F);
      return Mth.hsvToRgb(0.62222224F - $$1 * 0.05F, 0.5F + $$1 * 0.1F, 1.0F);
   }

   private static Biome biome(Biome.Precipitation p_194862_, Biome.BiomeCategory p_194863_, float p_194864_, float p_194865_, MobSpawnSettings.Builder p_194866_, BiomeGenerationSettings.Builder p_194867_, @Nullable Music p_194868_) {
      return biome(p_194862_, p_194863_, p_194864_, p_194865_, 4159204, 329011, p_194866_, p_194867_, p_194868_);
   }

   private static Biome biome(Biome.Precipitation p_194852_, Biome.BiomeCategory p_194853_, float p_194854_, float p_194855_, int p_194856_, int p_194857_, MobSpawnSettings.Builder p_194858_, BiomeGenerationSettings.Builder p_194859_, @Nullable Music p_194860_) {
      return (new Biome.BiomeBuilder()).precipitation(p_194852_).biomeCategory(p_194853_).temperature(p_194854_).downfall(p_194855_).specialEffects((new BiomeSpecialEffects.Builder()).waterColor(p_194856_).waterFogColor(p_194857_).fogColor(12638463).skyColor(calculateSkyColor(p_194854_)).ambientMoodSound(AmbientMoodSettings.LEGACY_CAVE_SETTINGS).backgroundMusic(p_194860_).build()).mobSpawnSettings(p_194858_.build()).generationSettings(p_194859_.build()).build();
   }

   private static void globalOverworldGeneration(BiomeGenerationSettings.Builder p_194870_) {
      BiomeDefaultFeatures.addDefaultCarversAndLakes(p_194870_);
      BiomeDefaultFeatures.addDefaultCrystalFormations(p_194870_);
      BiomeDefaultFeatures.addDefaultMonsterRoom(p_194870_);
      BiomeDefaultFeatures.addDefaultUndergroundVariety(p_194870_);
      BiomeDefaultFeatures.addDefaultSprings(p_194870_);
      BiomeDefaultFeatures.addSurfaceFreezing(p_194870_);
   }

   public static Biome oldGrowthTaiga(boolean p_194877_) {
      MobSpawnSettings.Builder mobspawnsettings$builder = new MobSpawnSettings.Builder();
      BiomeDefaultFeatures.farmAnimals(mobspawnsettings$builder);
      mobspawnsettings$builder.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.WOLF, 8, 4, 4));
      mobspawnsettings$builder.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.RABBIT, 4, 2, 3));
      mobspawnsettings$builder.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.FOX, 8, 2, 4));
      if (p_194877_) {
         BiomeDefaultFeatures.commonSpawns(mobspawnsettings$builder);
      } else {
         BiomeDefaultFeatures.caveSpawns(mobspawnsettings$builder);
         BiomeDefaultFeatures.monsters(mobspawnsettings$builder, 100, 25, 100, false);
      }

      BiomeGenerationSettings.Builder biomegenerationsettings$builder = new BiomeGenerationSettings.Builder();
      globalOverworldGeneration(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addMossyStoneBlock(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addFerns(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addDefaultOres(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addDefaultSoftDisks(biomegenerationsettings$builder);
      biomegenerationsettings$builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, p_194877_ ? VegetationPlacements.TREES_OLD_GROWTH_SPRUCE_TAIGA : VegetationPlacements.TREES_OLD_GROWTH_PINE_TAIGA);
      BiomeDefaultFeatures.addDefaultFlowers(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addGiantTaigaVegetation(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addDefaultMushrooms(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addDefaultExtraVegetation(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addCommonBerryBushes(biomegenerationsettings$builder);
      return biome(Biome.Precipitation.RAIN, Biome.BiomeCategory.TAIGA, p_194877_ ? 0.25F : 0.3F, 0.8F, mobspawnsettings$builder, biomegenerationsettings$builder, NORMAL_MUSIC);
   }

   public static Biome sparseJungle() {
      MobSpawnSettings.Builder mobspawnsettings$builder = new MobSpawnSettings.Builder();
      BiomeDefaultFeatures.baseJungleSpawns(mobspawnsettings$builder);
      return baseJungle(0.8F, false, true, false, mobspawnsettings$builder);
   }

   public static Biome jungle() {
      MobSpawnSettings.Builder mobspawnsettings$builder = new MobSpawnSettings.Builder();
      BiomeDefaultFeatures.baseJungleSpawns(mobspawnsettings$builder);
      mobspawnsettings$builder.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.PARROT, 40, 1, 2)).addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(EntityType.OCELOT, 2, 1, 3)).addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.PANDA, 1, 1, 2));
      return baseJungle(0.9F, false, false, true, mobspawnsettings$builder);
   }

   public static Biome bambooJungle() {
      MobSpawnSettings.Builder mobspawnsettings$builder = new MobSpawnSettings.Builder();
      BiomeDefaultFeatures.baseJungleSpawns(mobspawnsettings$builder);
      mobspawnsettings$builder.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.PARROT, 40, 1, 2)).addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.PANDA, 80, 1, 2)).addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(EntityType.OCELOT, 2, 1, 1));
      return baseJungle(0.9F, true, false, true, mobspawnsettings$builder);
   }

   private static Biome baseJungle(float p_194846_, boolean p_194847_, boolean p_194848_, boolean p_194849_, MobSpawnSettings.Builder p_194850_) {
      BiomeGenerationSettings.Builder biomegenerationsettings$builder = new BiomeGenerationSettings.Builder();
      globalOverworldGeneration(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addDefaultOres(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addDefaultSoftDisks(biomegenerationsettings$builder);
      if (p_194847_) {
         BiomeDefaultFeatures.addBambooVegetation(biomegenerationsettings$builder);
      } else {
         if (p_194849_) {
            BiomeDefaultFeatures.addLightBambooVegetation(biomegenerationsettings$builder);
         }

         if (p_194848_) {
            BiomeDefaultFeatures.addSparseJungleTrees(biomegenerationsettings$builder);
         } else {
            BiomeDefaultFeatures.addJungleTrees(biomegenerationsettings$builder);
         }
      }

      BiomeDefaultFeatures.addWarmFlowers(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addJungleGrass(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addDefaultMushrooms(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addDefaultExtraVegetation(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addJungleVines(biomegenerationsettings$builder);
      if (p_194848_) {
         BiomeDefaultFeatures.addSparseJungleMelons(biomegenerationsettings$builder);
      } else {
         BiomeDefaultFeatures.addJungleMelons(biomegenerationsettings$builder);
      }

      return biome(Biome.Precipitation.RAIN, Biome.BiomeCategory.JUNGLE, 0.95F, p_194846_, p_194850_, biomegenerationsettings$builder, NORMAL_MUSIC);
   }

   public static Biome windsweptHills(boolean p_194887_) {
      MobSpawnSettings.Builder mobspawnsettings$builder = new MobSpawnSettings.Builder();
      BiomeDefaultFeatures.farmAnimals(mobspawnsettings$builder);
      mobspawnsettings$builder.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.LLAMA, 5, 4, 6));
      BiomeDefaultFeatures.commonSpawns(mobspawnsettings$builder);
      BiomeGenerationSettings.Builder biomegenerationsettings$builder = new BiomeGenerationSettings.Builder();
      globalOverworldGeneration(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addDefaultOres(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addDefaultSoftDisks(biomegenerationsettings$builder);
      if (p_194887_) {
         BiomeDefaultFeatures.addMountainForestTrees(biomegenerationsettings$builder);
      } else {
         BiomeDefaultFeatures.addMountainTrees(biomegenerationsettings$builder);
      }

      BiomeDefaultFeatures.addDefaultFlowers(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addDefaultGrass(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addDefaultMushrooms(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addDefaultExtraVegetation(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addExtraEmeralds(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addInfestedStone(biomegenerationsettings$builder);
      return biome(Biome.Precipitation.RAIN, Biome.BiomeCategory.EXTREME_HILLS, 0.2F, 0.3F, mobspawnsettings$builder, biomegenerationsettings$builder, NORMAL_MUSIC);
   }

   public static Biome desert() {
      MobSpawnSettings.Builder mobspawnsettings$builder = new MobSpawnSettings.Builder();
      BiomeDefaultFeatures.desertSpawns(mobspawnsettings$builder);
      BiomeGenerationSettings.Builder biomegenerationsettings$builder = new BiomeGenerationSettings.Builder();
      BiomeDefaultFeatures.addFossilDecoration(biomegenerationsettings$builder);
      globalOverworldGeneration(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addDefaultOres(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addDefaultSoftDisks(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addDefaultFlowers(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addDefaultGrass(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addDesertVegetation(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addDefaultMushrooms(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addDesertExtraVegetation(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addDesertExtraDecoration(biomegenerationsettings$builder);
      return biome(Biome.Precipitation.NONE, Biome.BiomeCategory.DESERT, 2.0F, 0.0F, mobspawnsettings$builder, biomegenerationsettings$builder, NORMAL_MUSIC);
   }

   public static Biome plains(boolean p_194882_, boolean p_194883_, boolean p_194884_) {
      MobSpawnSettings.Builder mobspawnsettings$builder = new MobSpawnSettings.Builder();
      BiomeGenerationSettings.Builder biomegenerationsettings$builder = new BiomeGenerationSettings.Builder();
      globalOverworldGeneration(biomegenerationsettings$builder);
      if (p_194883_) {
         mobspawnsettings$builder.creatureGenerationProbability(0.07F);
         BiomeDefaultFeatures.snowySpawns(mobspawnsettings$builder);
         if (p_194884_) {
            biomegenerationsettings$builder.addFeature(GenerationStep.Decoration.SURFACE_STRUCTURES, MiscOverworldPlacements.ICE_SPIKE);
            biomegenerationsettings$builder.addFeature(GenerationStep.Decoration.SURFACE_STRUCTURES, MiscOverworldPlacements.ICE_PATCH);
         }
      } else {
         BiomeDefaultFeatures.plainsSpawns(mobspawnsettings$builder);
         BiomeDefaultFeatures.addPlainGrass(biomegenerationsettings$builder);
         if (p_194882_) {
            biomegenerationsettings$builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.PATCH_SUNFLOWER);
         }
      }

      BiomeDefaultFeatures.addDefaultOres(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addDefaultSoftDisks(biomegenerationsettings$builder);
      if (p_194883_) {
         BiomeDefaultFeatures.addSnowyTrees(biomegenerationsettings$builder);
         BiomeDefaultFeatures.addDefaultFlowers(biomegenerationsettings$builder);
         BiomeDefaultFeatures.addDefaultGrass(biomegenerationsettings$builder);
      } else {
         BiomeDefaultFeatures.addPlainVegetation(biomegenerationsettings$builder);
      }

      BiomeDefaultFeatures.addDefaultMushrooms(biomegenerationsettings$builder);
      if (p_194882_) {
         biomegenerationsettings$builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.PATCH_SUGAR_CANE);
         biomegenerationsettings$builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.PATCH_PUMPKIN);
      } else {
         BiomeDefaultFeatures.addDefaultExtraVegetation(biomegenerationsettings$builder);
      }

      float f = p_194883_ ? 0.0F : 0.8F;
      return biome(p_194883_ ? Biome.Precipitation.SNOW : Biome.Precipitation.RAIN, p_194883_ ? Biome.BiomeCategory.ICY : Biome.BiomeCategory.PLAINS, f, p_194883_ ? 0.5F : 0.4F, mobspawnsettings$builder, biomegenerationsettings$builder, NORMAL_MUSIC);
   }

   public static Biome mushroomFields() {
      MobSpawnSettings.Builder mobspawnsettings$builder = new MobSpawnSettings.Builder();
      BiomeDefaultFeatures.mooshroomSpawns(mobspawnsettings$builder);
      BiomeGenerationSettings.Builder biomegenerationsettings$builder = new BiomeGenerationSettings.Builder();
      globalOverworldGeneration(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addDefaultOres(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addDefaultSoftDisks(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addMushroomFieldVegetation(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addDefaultExtraVegetation(biomegenerationsettings$builder);
      return biome(Biome.Precipitation.RAIN, Biome.BiomeCategory.MUSHROOM, 0.9F, 1.0F, mobspawnsettings$builder, biomegenerationsettings$builder, NORMAL_MUSIC);
   }

   public static Biome savanna(boolean p_194879_, boolean p_194880_) {
      BiomeGenerationSettings.Builder biomegenerationsettings$builder = new BiomeGenerationSettings.Builder();
      globalOverworldGeneration(biomegenerationsettings$builder);
      if (!p_194879_) {
         BiomeDefaultFeatures.addSavannaGrass(biomegenerationsettings$builder);
      }

      BiomeDefaultFeatures.addDefaultOres(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addDefaultSoftDisks(biomegenerationsettings$builder);
      if (p_194879_) {
         BiomeDefaultFeatures.addShatteredSavannaTrees(biomegenerationsettings$builder);
         BiomeDefaultFeatures.addDefaultFlowers(biomegenerationsettings$builder);
         BiomeDefaultFeatures.addShatteredSavannaGrass(biomegenerationsettings$builder);
      } else {
         BiomeDefaultFeatures.addSavannaTrees(biomegenerationsettings$builder);
         BiomeDefaultFeatures.addWarmFlowers(biomegenerationsettings$builder);
         BiomeDefaultFeatures.addSavannaExtraGrass(biomegenerationsettings$builder);
      }

      BiomeDefaultFeatures.addDefaultMushrooms(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addDefaultExtraVegetation(biomegenerationsettings$builder);
      MobSpawnSettings.Builder mobspawnsettings$builder = new MobSpawnSettings.Builder();
      BiomeDefaultFeatures.farmAnimals(mobspawnsettings$builder);
      mobspawnsettings$builder.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.HORSE, 1, 2, 6)).addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.DONKEY, 1, 1, 1));
      BiomeDefaultFeatures.commonSpawns(mobspawnsettings$builder);
      if (p_194880_) {
         mobspawnsettings$builder.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.LLAMA, 8, 4, 4));
      }

      return biome(Biome.Precipitation.NONE, Biome.BiomeCategory.SAVANNA, 2.0F, 0.0F, mobspawnsettings$builder, biomegenerationsettings$builder, NORMAL_MUSIC);
   }

   public static Biome badlands(boolean p_194897_) {
      MobSpawnSettings.Builder mobspawnsettings$builder = new MobSpawnSettings.Builder();
      BiomeDefaultFeatures.commonSpawns(mobspawnsettings$builder);
      BiomeGenerationSettings.Builder biomegenerationsettings$builder = new BiomeGenerationSettings.Builder();
      globalOverworldGeneration(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addDefaultOres(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addExtraGold(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addDefaultSoftDisks(biomegenerationsettings$builder);
      if (p_194897_) {
         BiomeDefaultFeatures.addBadlandsTrees(biomegenerationsettings$builder);
      }

      BiomeDefaultFeatures.addBadlandGrass(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addDefaultMushrooms(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addBadlandExtraVegetation(biomegenerationsettings$builder);
      return (new Biome.BiomeBuilder()).precipitation(Biome.Precipitation.NONE).biomeCategory(Biome.BiomeCategory.MESA).temperature(2.0F).downfall(0.0F).specialEffects((new BiomeSpecialEffects.Builder()).waterColor(4159204).waterFogColor(329011).fogColor(12638463).skyColor(calculateSkyColor(2.0F)).foliageColorOverride(10387789).grassColorOverride(9470285).ambientMoodSound(AmbientMoodSettings.LEGACY_CAVE_SETTINGS).build()).mobSpawnSettings(mobspawnsettings$builder.build()).generationSettings(biomegenerationsettings$builder.build()).build();
   }

   private static Biome baseOcean(MobSpawnSettings.Builder p_194872_, int p_194873_, int p_194874_, BiomeGenerationSettings.Builder p_194875_) {
      return biome(Biome.Precipitation.RAIN, Biome.BiomeCategory.OCEAN, 0.5F, 0.5F, p_194873_, p_194874_, p_194872_, p_194875_, NORMAL_MUSIC);
   }

   private static BiomeGenerationSettings.Builder baseOceanGeneration() {
      BiomeGenerationSettings.Builder biomegenerationsettings$builder = new BiomeGenerationSettings.Builder();
      globalOverworldGeneration(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addDefaultOres(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addDefaultSoftDisks(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addWaterTrees(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addDefaultFlowers(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addDefaultGrass(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addDefaultMushrooms(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addDefaultExtraVegetation(biomegenerationsettings$builder);
      return biomegenerationsettings$builder;
   }

   public static Biome coldOcean(boolean p_194900_) {
      MobSpawnSettings.Builder mobspawnsettings$builder = new MobSpawnSettings.Builder();
      BiomeDefaultFeatures.oceanSpawns(mobspawnsettings$builder, 3, 4, 15);
      mobspawnsettings$builder.addSpawn(MobCategory.WATER_AMBIENT, new MobSpawnSettings.SpawnerData(EntityType.SALMON, 15, 1, 5));
      BiomeGenerationSettings.Builder biomegenerationsettings$builder = baseOceanGeneration();
      biomegenerationsettings$builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, p_194900_ ? AquaticPlacements.SEAGRASS_DEEP_COLD : AquaticPlacements.SEAGRASS_COLD);
      BiomeDefaultFeatures.addDefaultSeagrass(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addColdOceanExtraVegetation(biomegenerationsettings$builder);
      return baseOcean(mobspawnsettings$builder, 4020182, 329011, biomegenerationsettings$builder);
   }

   public static Biome ocean(boolean p_194903_) {
      MobSpawnSettings.Builder mobspawnsettings$builder = new MobSpawnSettings.Builder();
      BiomeDefaultFeatures.oceanSpawns(mobspawnsettings$builder, 1, 4, 10);
      mobspawnsettings$builder.addSpawn(MobCategory.WATER_CREATURE, new MobSpawnSettings.SpawnerData(EntityType.DOLPHIN, 1, 1, 2));
      BiomeGenerationSettings.Builder biomegenerationsettings$builder = baseOceanGeneration();
      biomegenerationsettings$builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, p_194903_ ? AquaticPlacements.SEAGRASS_DEEP : AquaticPlacements.SEAGRASS_NORMAL);
      BiomeDefaultFeatures.addDefaultSeagrass(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addColdOceanExtraVegetation(biomegenerationsettings$builder);
      return baseOcean(mobspawnsettings$builder, 4159204, 329011, biomegenerationsettings$builder);
   }

   public static Biome lukeWarmOcean(boolean p_194906_) {
      MobSpawnSettings.Builder mobspawnsettings$builder = new MobSpawnSettings.Builder();
      if (p_194906_) {
         BiomeDefaultFeatures.oceanSpawns(mobspawnsettings$builder, 8, 4, 8);
      } else {
         BiomeDefaultFeatures.oceanSpawns(mobspawnsettings$builder, 10, 2, 15);
      }

      mobspawnsettings$builder.addSpawn(MobCategory.WATER_AMBIENT, new MobSpawnSettings.SpawnerData(EntityType.PUFFERFISH, 5, 1, 3)).addSpawn(MobCategory.WATER_AMBIENT, new MobSpawnSettings.SpawnerData(EntityType.TROPICAL_FISH, 25, 8, 8)).addSpawn(MobCategory.WATER_CREATURE, new MobSpawnSettings.SpawnerData(EntityType.DOLPHIN, 2, 1, 2));
      BiomeGenerationSettings.Builder biomegenerationsettings$builder = baseOceanGeneration();
      biomegenerationsettings$builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, p_194906_ ? AquaticPlacements.SEAGRASS_DEEP_WARM : AquaticPlacements.SEAGRASS_WARM);
      if (p_194906_) {
         BiomeDefaultFeatures.addDefaultSeagrass(biomegenerationsettings$builder);
      }

      BiomeDefaultFeatures.addLukeWarmKelp(biomegenerationsettings$builder);
      return baseOcean(mobspawnsettings$builder, 4566514, 267827, biomegenerationsettings$builder);
   }

   public static Biome warmOcean() {
      MobSpawnSettings.Builder mobspawnsettings$builder = (new MobSpawnSettings.Builder()).addSpawn(MobCategory.WATER_AMBIENT, new MobSpawnSettings.SpawnerData(EntityType.PUFFERFISH, 15, 1, 3));
      BiomeDefaultFeatures.warmOceanSpawns(mobspawnsettings$builder, 10, 4);
      BiomeGenerationSettings.Builder biomegenerationsettings$builder = baseOceanGeneration().addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, AquaticPlacements.WARM_OCEAN_VEGETATION).addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, AquaticPlacements.SEAGRASS_WARM).addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, AquaticPlacements.SEA_PICKLE);
      return baseOcean(mobspawnsettings$builder, 4445678, 270131, biomegenerationsettings$builder);
   }

   public static Biome frozenOcean(boolean p_194909_) {
      MobSpawnSettings.Builder mobspawnsettings$builder = (new MobSpawnSettings.Builder()).addSpawn(MobCategory.WATER_CREATURE, new MobSpawnSettings.SpawnerData(EntityType.SQUID, 1, 1, 4)).addSpawn(MobCategory.WATER_AMBIENT, new MobSpawnSettings.SpawnerData(EntityType.SALMON, 15, 1, 5)).addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.POLAR_BEAR, 1, 1, 2));
      BiomeDefaultFeatures.commonSpawns(mobspawnsettings$builder);
      mobspawnsettings$builder.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(EntityType.DROWNED, 5, 1, 1));
      float f = p_194909_ ? 0.5F : 0.0F;
      BiomeGenerationSettings.Builder biomegenerationsettings$builder = new BiomeGenerationSettings.Builder();
      BiomeDefaultFeatures.addIcebergs(biomegenerationsettings$builder);
      globalOverworldGeneration(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addBlueIce(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addDefaultOres(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addDefaultSoftDisks(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addWaterTrees(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addDefaultFlowers(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addDefaultGrass(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addDefaultMushrooms(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addDefaultExtraVegetation(biomegenerationsettings$builder);
      return (new Biome.BiomeBuilder()).precipitation(p_194909_ ? Biome.Precipitation.RAIN : Biome.Precipitation.SNOW).biomeCategory(Biome.BiomeCategory.OCEAN).temperature(f).temperatureAdjustment(Biome.TemperatureModifier.FROZEN).downfall(0.5F).specialEffects((new BiomeSpecialEffects.Builder()).waterColor(3750089).waterFogColor(329011).fogColor(12638463).skyColor(calculateSkyColor(f)).ambientMoodSound(AmbientMoodSettings.LEGACY_CAVE_SETTINGS).build()).mobSpawnSettings(mobspawnsettings$builder.build()).generationSettings(biomegenerationsettings$builder.build()).build();
   }

   public static Biome forest(boolean p_194892_, boolean p_194893_, boolean p_194894_) {
      BiomeGenerationSettings.Builder biomegenerationsettings$builder = new BiomeGenerationSettings.Builder();
      globalOverworldGeneration(biomegenerationsettings$builder);
      if (p_194894_) {
         biomegenerationsettings$builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.FLOWER_FOREST_FLOWERS);
      } else {
         BiomeDefaultFeatures.addForestFlowers(biomegenerationsettings$builder);
      }

      BiomeDefaultFeatures.addDefaultOres(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addDefaultSoftDisks(biomegenerationsettings$builder);
      if (p_194894_) {
         biomegenerationsettings$builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.TREES_FLOWER_FOREST);
         biomegenerationsettings$builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.FLOWER_FLOWER_FOREST);
         BiomeDefaultFeatures.addDefaultGrass(biomegenerationsettings$builder);
      } else {
         if (p_194892_) {
            if (p_194893_) {
               BiomeDefaultFeatures.addTallBirchTrees(biomegenerationsettings$builder);
            } else {
               BiomeDefaultFeatures.addBirchTrees(biomegenerationsettings$builder);
            }
         } else {
            BiomeDefaultFeatures.addOtherBirchTrees(biomegenerationsettings$builder);
         }

         BiomeDefaultFeatures.addDefaultFlowers(biomegenerationsettings$builder);
         BiomeDefaultFeatures.addForestGrass(biomegenerationsettings$builder);
      }

      BiomeDefaultFeatures.addDefaultMushrooms(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addDefaultExtraVegetation(biomegenerationsettings$builder);
      MobSpawnSettings.Builder mobspawnsettings$builder = new MobSpawnSettings.Builder();
      BiomeDefaultFeatures.farmAnimals(mobspawnsettings$builder);
      BiomeDefaultFeatures.commonSpawns(mobspawnsettings$builder);
      if (p_194894_) {
         mobspawnsettings$builder.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.RABBIT, 4, 2, 3));
      } else if (!p_194892_) {
         mobspawnsettings$builder.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.WOLF, 5, 4, 4));
      }

      float f = p_194892_ ? 0.6F : 0.7F;
      return biome(Biome.Precipitation.RAIN, Biome.BiomeCategory.FOREST, f, p_194892_ ? 0.6F : 0.8F, mobspawnsettings$builder, biomegenerationsettings$builder, NORMAL_MUSIC);
   }

   public static Biome taiga(boolean p_194912_) {
      MobSpawnSettings.Builder mobspawnsettings$builder = new MobSpawnSettings.Builder();
      BiomeDefaultFeatures.farmAnimals(mobspawnsettings$builder);
      mobspawnsettings$builder.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.WOLF, 8, 4, 4)).addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.RABBIT, 4, 2, 3)).addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.FOX, 8, 2, 4));
      BiomeDefaultFeatures.commonSpawns(mobspawnsettings$builder);
      float f = p_194912_ ? -0.5F : 0.25F;
      BiomeGenerationSettings.Builder biomegenerationsettings$builder = new BiomeGenerationSettings.Builder();
      globalOverworldGeneration(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addFerns(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addDefaultOres(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addDefaultSoftDisks(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addTaigaTrees(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addDefaultFlowers(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addTaigaGrass(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addDefaultExtraVegetation(biomegenerationsettings$builder);
      if (p_194912_) {
         BiomeDefaultFeatures.addRareBerryBushes(biomegenerationsettings$builder);
      } else {
         BiomeDefaultFeatures.addCommonBerryBushes(biomegenerationsettings$builder);
      }

      return biome(p_194912_ ? Biome.Precipitation.SNOW : Biome.Precipitation.RAIN, Biome.BiomeCategory.TAIGA, f, p_194912_ ? 0.4F : 0.8F, p_194912_ ? 4020182 : 4159204, 329011, mobspawnsettings$builder, biomegenerationsettings$builder, NORMAL_MUSIC);
   }

   public static Biome darkForest() {
      MobSpawnSettings.Builder mobspawnsettings$builder = new MobSpawnSettings.Builder();
      BiomeDefaultFeatures.farmAnimals(mobspawnsettings$builder);
      BiomeDefaultFeatures.commonSpawns(mobspawnsettings$builder);
      BiomeGenerationSettings.Builder biomegenerationsettings$builder = new BiomeGenerationSettings.Builder();
      globalOverworldGeneration(biomegenerationsettings$builder);
      biomegenerationsettings$builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.DARK_FOREST_VEGETATION);
      BiomeDefaultFeatures.addForestFlowers(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addDefaultOres(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addDefaultSoftDisks(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addDefaultFlowers(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addForestGrass(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addDefaultMushrooms(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addDefaultExtraVegetation(biomegenerationsettings$builder);
      return (new Biome.BiomeBuilder()).precipitation(Biome.Precipitation.RAIN).biomeCategory(Biome.BiomeCategory.FOREST).temperature(0.7F).downfall(0.8F).specialEffects((new BiomeSpecialEffects.Builder()).waterColor(4159204).waterFogColor(329011).fogColor(12638463).skyColor(calculateSkyColor(0.7F)).grassColorModifier(BiomeSpecialEffects.GrassColorModifier.DARK_FOREST).ambientMoodSound(AmbientMoodSettings.LEGACY_CAVE_SETTINGS).build()).mobSpawnSettings(mobspawnsettings$builder.build()).generationSettings(biomegenerationsettings$builder.build()).build();
   }

   public static Biome swamp() {
      MobSpawnSettings.Builder mobspawnsettings$builder = new MobSpawnSettings.Builder();
      BiomeDefaultFeatures.farmAnimals(mobspawnsettings$builder);
      BiomeDefaultFeatures.commonSpawns(mobspawnsettings$builder);
      mobspawnsettings$builder.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(EntityType.SLIME, 1, 1, 1));
      BiomeGenerationSettings.Builder biomegenerationsettings$builder = new BiomeGenerationSettings.Builder();
      BiomeDefaultFeatures.addFossilDecoration(biomegenerationsettings$builder);
      globalOverworldGeneration(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addDefaultOres(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addSwampClayDisk(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addSwampVegetation(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addDefaultMushrooms(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addSwampExtraVegetation(biomegenerationsettings$builder);
      biomegenerationsettings$builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, AquaticPlacements.SEAGRASS_SWAMP);
      return (new Biome.BiomeBuilder()).precipitation(Biome.Precipitation.RAIN).biomeCategory(Biome.BiomeCategory.SWAMP).temperature(0.8F).downfall(0.9F).specialEffects((new BiomeSpecialEffects.Builder()).waterColor(6388580).waterFogColor(2302743).fogColor(12638463).skyColor(calculateSkyColor(0.8F)).foliageColorOverride(6975545).grassColorModifier(BiomeSpecialEffects.GrassColorModifier.SWAMP).ambientMoodSound(AmbientMoodSettings.LEGACY_CAVE_SETTINGS).build()).mobSpawnSettings(mobspawnsettings$builder.build()).generationSettings(biomegenerationsettings$builder.build()).build();
   }

   public static Biome river(boolean p_194915_) {
      MobSpawnSettings.Builder mobspawnsettings$builder = (new MobSpawnSettings.Builder()).addSpawn(MobCategory.WATER_CREATURE, new MobSpawnSettings.SpawnerData(EntityType.SQUID, 2, 1, 4)).addSpawn(MobCategory.WATER_AMBIENT, new MobSpawnSettings.SpawnerData(EntityType.SALMON, 5, 1, 5));
      BiomeDefaultFeatures.commonSpawns(mobspawnsettings$builder);
      mobspawnsettings$builder.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(EntityType.DROWNED, p_194915_ ? 1 : 100, 1, 1));
      BiomeGenerationSettings.Builder biomegenerationsettings$builder = new BiomeGenerationSettings.Builder();
      globalOverworldGeneration(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addDefaultOres(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addDefaultSoftDisks(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addWaterTrees(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addDefaultFlowers(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addDefaultGrass(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addDefaultMushrooms(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addDefaultExtraVegetation(biomegenerationsettings$builder);
      if (!p_194915_) {
         biomegenerationsettings$builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, AquaticPlacements.SEAGRASS_RIVER);
      }

      float f = p_194915_ ? 0.0F : 0.5F;
      return biome(p_194915_ ? Biome.Precipitation.SNOW : Biome.Precipitation.RAIN, Biome.BiomeCategory.RIVER, f, 0.5F, p_194915_ ? 3750089 : 4159204, 329011, mobspawnsettings$builder, biomegenerationsettings$builder, NORMAL_MUSIC);
   }

   public static Biome beach(boolean p_194889_, boolean p_194890_) {
      MobSpawnSettings.Builder mobspawnsettings$builder = new MobSpawnSettings.Builder();
      boolean flag = !p_194890_ && !p_194889_;
      if (flag) {
         mobspawnsettings$builder.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.TURTLE, 5, 2, 5));
      }

      BiomeDefaultFeatures.commonSpawns(mobspawnsettings$builder);
      BiomeGenerationSettings.Builder biomegenerationsettings$builder = new BiomeGenerationSettings.Builder();
      globalOverworldGeneration(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addDefaultOres(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addDefaultSoftDisks(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addDefaultFlowers(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addDefaultGrass(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addDefaultMushrooms(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addDefaultExtraVegetation(biomegenerationsettings$builder);
      float f;
      if (p_194889_) {
         f = 0.05F;
      } else if (p_194890_) {
         f = 0.2F;
      } else {
         f = 0.8F;
      }

      return biome(p_194889_ ? Biome.Precipitation.SNOW : Biome.Precipitation.RAIN, Biome.BiomeCategory.BEACH, f, flag ? 0.4F : 0.3F, p_194889_ ? 4020182 : 4159204, 329011, mobspawnsettings$builder, biomegenerationsettings$builder, NORMAL_MUSIC);
   }

   public static Biome theVoid() {
      BiomeGenerationSettings.Builder biomegenerationsettings$builder = new BiomeGenerationSettings.Builder();
      biomegenerationsettings$builder.addFeature(GenerationStep.Decoration.TOP_LAYER_MODIFICATION, MiscOverworldPlacements.VOID_START_PLATFORM);
      return biome(Biome.Precipitation.NONE, Biome.BiomeCategory.NONE, 0.5F, 0.5F, new MobSpawnSettings.Builder(), biomegenerationsettings$builder, NORMAL_MUSIC);
   }

   public static Biome meadow() {
      BiomeGenerationSettings.Builder biomegenerationsettings$builder = new BiomeGenerationSettings.Builder();
      MobSpawnSettings.Builder mobspawnsettings$builder = new MobSpawnSettings.Builder();
      mobspawnsettings$builder.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.DONKEY, 1, 1, 2)).addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.RABBIT, 2, 2, 6)).addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.SHEEP, 2, 2, 4));
      BiomeDefaultFeatures.commonSpawns(mobspawnsettings$builder);
      globalOverworldGeneration(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addPlainGrass(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addDefaultOres(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addDefaultSoftDisks(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addMeadowVegetation(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addExtraEmeralds(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addInfestedStone(biomegenerationsettings$builder);
      Music music = Musics.createGameMusic(SoundEvents.MUSIC_BIOME_MEADOW);
      return biome(Biome.Precipitation.RAIN, Biome.BiomeCategory.MOUNTAIN, 0.5F, 0.8F, 937679, 329011, mobspawnsettings$builder, biomegenerationsettings$builder, music);
   }

   public static Biome frozenPeaks() {
      BiomeGenerationSettings.Builder biomegenerationsettings$builder = new BiomeGenerationSettings.Builder();
      MobSpawnSettings.Builder mobspawnsettings$builder = new MobSpawnSettings.Builder();
      mobspawnsettings$builder.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.GOAT, 5, 1, 3));
      BiomeDefaultFeatures.commonSpawns(mobspawnsettings$builder);
      globalOverworldGeneration(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addFrozenSprings(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addDefaultOres(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addDefaultSoftDisks(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addExtraEmeralds(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addInfestedStone(biomegenerationsettings$builder);
      Music music = Musics.createGameMusic(SoundEvents.MUSIC_BIOME_FROZEN_PEAKS);
      return biome(Biome.Precipitation.SNOW, Biome.BiomeCategory.MOUNTAIN, -0.7F, 0.9F, mobspawnsettings$builder, biomegenerationsettings$builder, music);
   }

   public static Biome jaggedPeaks() {
      BiomeGenerationSettings.Builder biomegenerationsettings$builder = new BiomeGenerationSettings.Builder();
      MobSpawnSettings.Builder mobspawnsettings$builder = new MobSpawnSettings.Builder();
      mobspawnsettings$builder.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.GOAT, 5, 1, 3));
      BiomeDefaultFeatures.commonSpawns(mobspawnsettings$builder);
      globalOverworldGeneration(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addFrozenSprings(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addDefaultOres(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addDefaultSoftDisks(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addExtraEmeralds(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addInfestedStone(biomegenerationsettings$builder);
      Music music = Musics.createGameMusic(SoundEvents.MUSIC_BIOME_JAGGED_PEAKS);
      return biome(Biome.Precipitation.SNOW, Biome.BiomeCategory.MOUNTAIN, -0.7F, 0.9F, mobspawnsettings$builder, biomegenerationsettings$builder, music);
   }

   public static Biome stonyPeaks() {
      BiomeGenerationSettings.Builder biomegenerationsettings$builder = new BiomeGenerationSettings.Builder();
      MobSpawnSettings.Builder mobspawnsettings$builder = new MobSpawnSettings.Builder();
      BiomeDefaultFeatures.commonSpawns(mobspawnsettings$builder);
      globalOverworldGeneration(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addDefaultOres(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addDefaultSoftDisks(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addExtraEmeralds(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addInfestedStone(biomegenerationsettings$builder);
      Music music = Musics.createGameMusic(SoundEvents.MUSIC_BIOME_STONY_PEAKS);
      return biome(Biome.Precipitation.RAIN, Biome.BiomeCategory.MOUNTAIN, 1.0F, 0.3F, mobspawnsettings$builder, biomegenerationsettings$builder, music);
   }

   public static Biome snowySlopes() {
      BiomeGenerationSettings.Builder biomegenerationsettings$builder = new BiomeGenerationSettings.Builder();
      MobSpawnSettings.Builder mobspawnsettings$builder = new MobSpawnSettings.Builder();
      mobspawnsettings$builder.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.RABBIT, 4, 2, 3)).addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.GOAT, 5, 1, 3));
      BiomeDefaultFeatures.commonSpawns(mobspawnsettings$builder);
      globalOverworldGeneration(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addFrozenSprings(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addDefaultOres(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addDefaultSoftDisks(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addDefaultExtraVegetation(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addExtraEmeralds(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addInfestedStone(biomegenerationsettings$builder);
      Music music = Musics.createGameMusic(SoundEvents.MUSIC_BIOME_SNOWY_SLOPES);
      return biome(Biome.Precipitation.SNOW, Biome.BiomeCategory.MOUNTAIN, -0.3F, 0.9F, mobspawnsettings$builder, biomegenerationsettings$builder, music);
   }

   public static Biome grove() {
      BiomeGenerationSettings.Builder biomegenerationsettings$builder = new BiomeGenerationSettings.Builder();
      MobSpawnSettings.Builder mobspawnsettings$builder = new MobSpawnSettings.Builder();
      BiomeDefaultFeatures.farmAnimals(mobspawnsettings$builder);
      mobspawnsettings$builder.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.WOLF, 8, 4, 4)).addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.RABBIT, 4, 2, 3)).addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.FOX, 8, 2, 4));
      BiomeDefaultFeatures.commonSpawns(mobspawnsettings$builder);
      globalOverworldGeneration(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addFrozenSprings(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addDefaultOres(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addDefaultSoftDisks(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addGroveTrees(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addDefaultExtraVegetation(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addExtraEmeralds(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addInfestedStone(biomegenerationsettings$builder);
      Music music = Musics.createGameMusic(SoundEvents.MUSIC_BIOME_GROVE);
      return biome(Biome.Precipitation.SNOW, Biome.BiomeCategory.FOREST, -0.2F, 0.8F, mobspawnsettings$builder, biomegenerationsettings$builder, music);
   }

   public static Biome lushCaves() {
      MobSpawnSettings.Builder mobspawnsettings$builder = new MobSpawnSettings.Builder();
      mobspawnsettings$builder.addSpawn(MobCategory.AXOLOTLS, new MobSpawnSettings.SpawnerData(EntityType.AXOLOTL, 10, 4, 6));
      mobspawnsettings$builder.addSpawn(MobCategory.WATER_AMBIENT, new MobSpawnSettings.SpawnerData(EntityType.TROPICAL_FISH, 25, 8, 8));
      BiomeDefaultFeatures.commonSpawns(mobspawnsettings$builder);
      BiomeGenerationSettings.Builder biomegenerationsettings$builder = new BiomeGenerationSettings.Builder();
      globalOverworldGeneration(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addPlainGrass(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addDefaultOres(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addLushCavesSpecialOres(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addDefaultSoftDisks(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addLushCavesVegetationFeatures(biomegenerationsettings$builder);
      Music music = Musics.createGameMusic(SoundEvents.MUSIC_BIOME_LUSH_CAVES);
      return biome(Biome.Precipitation.RAIN, Biome.BiomeCategory.UNDERGROUND, 0.5F, 0.5F, mobspawnsettings$builder, biomegenerationsettings$builder, music);
   }

   public static Biome dripstoneCaves() {
      MobSpawnSettings.Builder mobspawnsettings$builder = new MobSpawnSettings.Builder();
      BiomeDefaultFeatures.dripstoneCavesSpawns(mobspawnsettings$builder);
      BiomeGenerationSettings.Builder biomegenerationsettings$builder = new BiomeGenerationSettings.Builder();
      globalOverworldGeneration(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addPlainGrass(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addDefaultOres(biomegenerationsettings$builder, true);
      BiomeDefaultFeatures.addDefaultSoftDisks(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addPlainVegetation(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addDefaultMushrooms(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addDefaultExtraVegetation(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addDripstone(biomegenerationsettings$builder);
      Music music = Musics.createGameMusic(SoundEvents.MUSIC_BIOME_DRIPSTONE_CAVES);
      return biome(Biome.Precipitation.RAIN, Biome.BiomeCategory.UNDERGROUND, 0.8F, 0.4F, mobspawnsettings$builder, biomegenerationsettings$builder, music);
   }
}
