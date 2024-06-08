package net.minecraft.data.worldgen;

import net.minecraft.data.worldgen.placement.AquaticPlacements;
import net.minecraft.data.worldgen.placement.CavePlacements;
import net.minecraft.data.worldgen.placement.MiscOverworldPlacements;
import net.minecraft.data.worldgen.placement.OrePlacements;
import net.minecraft.data.worldgen.placement.VegetationPlacements;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.GenerationStep;

public class BiomeDefaultFeatures {
   public static void addDefaultCarversAndLakes(BiomeGenerationSettings.Builder p_194721_) {
      p_194721_.addCarver(GenerationStep.Carving.AIR, Carvers.CAVE);
      p_194721_.addCarver(GenerationStep.Carving.AIR, Carvers.CAVE_EXTRA_UNDERGROUND);
      p_194721_.addCarver(GenerationStep.Carving.AIR, Carvers.CANYON);
      p_194721_.addFeature(GenerationStep.Decoration.LAKES, MiscOverworldPlacements.LAKE_LAVA_UNDERGROUND);
      p_194721_.addFeature(GenerationStep.Decoration.LAKES, MiscOverworldPlacements.LAKE_LAVA_SURFACE);
   }

   public static void addDefaultMonsterRoom(BiomeGenerationSettings.Builder p_126807_) {
      p_126807_.addFeature(GenerationStep.Decoration.UNDERGROUND_STRUCTURES, CavePlacements.MONSTER_ROOM);
      p_126807_.addFeature(GenerationStep.Decoration.UNDERGROUND_STRUCTURES, CavePlacements.MONSTER_ROOM_DEEP);
   }

   public static void addDefaultUndergroundVariety(BiomeGenerationSettings.Builder p_126811_) {
      p_126811_.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, OrePlacements.ORE_DIRT);
      p_126811_.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, OrePlacements.ORE_GRAVEL);
      p_126811_.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, OrePlacements.ORE_GRANITE_UPPER);
      p_126811_.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, OrePlacements.ORE_GRANITE_LOWER);
      p_126811_.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, OrePlacements.ORE_DIORITE_UPPER);
      p_126811_.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, OrePlacements.ORE_DIORITE_LOWER);
      p_126811_.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, OrePlacements.ORE_ANDESITE_UPPER);
      p_126811_.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, OrePlacements.ORE_ANDESITE_LOWER);
      p_126811_.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, OrePlacements.ORE_TUFF);
      p_126811_.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, CavePlacements.GLOW_LICHEN);
   }

   public static void addDripstone(BiomeGenerationSettings.Builder p_176864_) {
      p_176864_.addFeature(GenerationStep.Decoration.LOCAL_MODIFICATIONS, CavePlacements.LARGE_DRIPSTONE);
      p_176864_.addFeature(GenerationStep.Decoration.UNDERGROUND_DECORATION, CavePlacements.DRIPSTONE_CLUSTER);
      p_176864_.addFeature(GenerationStep.Decoration.UNDERGROUND_DECORATION, CavePlacements.POINTED_DRIPSTONE);
   }

   public static void addDefaultOres(BiomeGenerationSettings.Builder p_126815_) {
      addDefaultOres(p_126815_, false);
   }

   public static void addDefaultOres(BiomeGenerationSettings.Builder p_194723_, boolean p_194724_) {
      p_194723_.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, OrePlacements.ORE_COAL_UPPER);
      p_194723_.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, OrePlacements.ORE_COAL_LOWER);
      p_194723_.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, OrePlacements.ORE_IRON_UPPER);
      p_194723_.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, OrePlacements.ORE_IRON_MIDDLE);
      p_194723_.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, OrePlacements.ORE_IRON_SMALL);
      p_194723_.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, OrePlacements.ORE_GOLD);
      p_194723_.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, OrePlacements.ORE_GOLD_LOWER);
      p_194723_.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, OrePlacements.ORE_REDSTONE);
      p_194723_.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, OrePlacements.ORE_REDSTONE_LOWER);
      p_194723_.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, OrePlacements.ORE_DIAMOND);
      p_194723_.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, OrePlacements.ORE_DIAMOND_LARGE);
      p_194723_.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, OrePlacements.ORE_DIAMOND_BURIED);
      p_194723_.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, OrePlacements.ORE_LAPIS);
      p_194723_.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, OrePlacements.ORE_LAPIS_BURIED);
      p_194723_.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, p_194724_ ? OrePlacements.ORE_COPPER_LARGE : OrePlacements.ORE_COPPER);
      p_194723_.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, CavePlacements.UNDERWATER_MAGMA);
   }

   public static void addExtraGold(BiomeGenerationSettings.Builder p_126817_) {
      p_126817_.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, OrePlacements.ORE_GOLD_EXTRA);
   }

   public static void addExtraEmeralds(BiomeGenerationSettings.Builder p_126819_) {
      p_126819_.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, OrePlacements.ORE_EMERALD);
   }

   public static void addInfestedStone(BiomeGenerationSettings.Builder p_126821_) {
      p_126821_.addFeature(GenerationStep.Decoration.UNDERGROUND_DECORATION, OrePlacements.ORE_INFESTED);
   }

   public static void addDefaultSoftDisks(BiomeGenerationSettings.Builder p_126823_) {
      p_126823_.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, MiscOverworldPlacements.DISK_SAND);
      p_126823_.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, MiscOverworldPlacements.DISK_CLAY);
      p_126823_.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, MiscOverworldPlacements.DISK_GRAVEL);
   }

   public static void addSwampClayDisk(BiomeGenerationSettings.Builder p_126825_) {
      p_126825_.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, MiscOverworldPlacements.DISK_CLAY);
   }

   public static void addMossyStoneBlock(BiomeGenerationSettings.Builder p_126827_) {
      p_126827_.addFeature(GenerationStep.Decoration.LOCAL_MODIFICATIONS, MiscOverworldPlacements.FOREST_ROCK);
   }

   public static void addFerns(BiomeGenerationSettings.Builder p_126829_) {
      p_126829_.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.PATCH_LARGE_FERN);
   }

   public static void addRareBerryBushes(BiomeGenerationSettings.Builder p_194736_) {
      p_194736_.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.PATCH_BERRY_RARE);
   }

   public static void addCommonBerryBushes(BiomeGenerationSettings.Builder p_194738_) {
      p_194738_.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.PATCH_BERRY_COMMON);
   }

   public static void addLightBambooVegetation(BiomeGenerationSettings.Builder p_126835_) {
      p_126835_.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.BAMBOO_LIGHT);
   }

   public static void addBambooVegetation(BiomeGenerationSettings.Builder p_126837_) {
      p_126837_.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.BAMBOO);
      p_126837_.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.BAMBOO_VEGETATION);
   }

   public static void addTaigaTrees(BiomeGenerationSettings.Builder p_126839_) {
      p_126839_.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.TREES_TAIGA);
   }

   public static void addGroveTrees(BiomeGenerationSettings.Builder p_194740_) {
      p_194740_.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.TREES_GROVE);
   }

   public static void addWaterTrees(BiomeGenerationSettings.Builder p_126841_) {
      p_126841_.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.TREES_WATER);
   }

   public static void addBirchTrees(BiomeGenerationSettings.Builder p_126843_) {
      p_126843_.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.TREES_BIRCH);
   }

   public static void addOtherBirchTrees(BiomeGenerationSettings.Builder p_126845_) {
      p_126845_.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.TREES_BIRCH_AND_OAK);
   }

   public static void addTallBirchTrees(BiomeGenerationSettings.Builder p_126847_) {
      p_126847_.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.BIRCH_TALL);
   }

   public static void addSavannaTrees(BiomeGenerationSettings.Builder p_126681_) {
      p_126681_.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.TREES_SAVANNA);
   }

   public static void addShatteredSavannaTrees(BiomeGenerationSettings.Builder p_126683_) {
      p_126683_.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.TREES_WINDSWEPT_SAVANNA);
   }

   public static void addLushCavesVegetationFeatures(BiomeGenerationSettings.Builder p_176851_) {
      p_176851_.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, CavePlacements.LUSH_CAVES_CEILING_VEGETATION);
      p_176851_.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, CavePlacements.CAVE_VINES);
      p_176851_.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, CavePlacements.LUSH_CAVES_CLAY);
      p_176851_.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, CavePlacements.LUSH_CAVES_VEGETATION);
      p_176851_.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, CavePlacements.ROOTED_AZALEA_TREE);
      p_176851_.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, CavePlacements.SPORE_BLOSSOM);
      p_176851_.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, CavePlacements.CLASSIC_VINES);
   }

   public static void addLushCavesSpecialOres(BiomeGenerationSettings.Builder p_176853_) {
      p_176853_.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, OrePlacements.ORE_CLAY);
   }

   public static void addMountainTrees(BiomeGenerationSettings.Builder p_126685_) {
      p_126685_.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.TREES_WINDSWEPT_HILLS);
   }

   public static void addMountainForestTrees(BiomeGenerationSettings.Builder p_194717_) {
      p_194717_.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.TREES_WINDSWEPT_FOREST);
   }

   public static void addJungleTrees(BiomeGenerationSettings.Builder p_126689_) {
      p_126689_.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.TREES_JUNGLE);
   }

   public static void addSparseJungleTrees(BiomeGenerationSettings.Builder p_198928_) {
      p_198928_.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.TREES_SPARSE_JUNGLE);
   }

   public static void addBadlandsTrees(BiomeGenerationSettings.Builder p_126693_) {
      p_126693_.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.TREES_BADLANDS);
   }

   public static void addSnowyTrees(BiomeGenerationSettings.Builder p_126695_) {
      p_126695_.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.TREES_SNOWY);
   }

   public static void addJungleGrass(BiomeGenerationSettings.Builder p_126697_) {
      p_126697_.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.PATCH_GRASS_JUNGLE);
   }

   public static void addSavannaGrass(BiomeGenerationSettings.Builder p_126699_) {
      p_126699_.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.PATCH_TALL_GRASS);
   }

   public static void addShatteredSavannaGrass(BiomeGenerationSettings.Builder p_126701_) {
      p_126701_.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.PATCH_GRASS_NORMAL);
   }

   public static void addSavannaExtraGrass(BiomeGenerationSettings.Builder p_126703_) {
      p_126703_.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.PATCH_GRASS_SAVANNA);
   }

   public static void addBadlandGrass(BiomeGenerationSettings.Builder p_126705_) {
      p_126705_.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.PATCH_GRASS_BADLANDS);
      p_126705_.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.PATCH_DEAD_BUSH_BADLANDS);
   }

   public static void addForestFlowers(BiomeGenerationSettings.Builder p_126707_) {
      p_126707_.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.FOREST_FLOWERS);
   }

   public static void addForestGrass(BiomeGenerationSettings.Builder p_126709_) {
      p_126709_.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.PATCH_GRASS_FOREST);
   }

   public static void addSwampVegetation(BiomeGenerationSettings.Builder p_126711_) {
      p_126711_.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.TREES_SWAMP);
      p_126711_.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.FLOWER_SWAMP);
      p_126711_.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.PATCH_GRASS_NORMAL);
      p_126711_.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.PATCH_DEAD_BUSH);
      p_126711_.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.PATCH_WATERLILY);
      p_126711_.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.BROWN_MUSHROOM_SWAMP);
      p_126711_.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.RED_MUSHROOM_SWAMP);
   }

   public static void addMushroomFieldVegetation(BiomeGenerationSettings.Builder p_126713_) {
      p_126713_.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.MUSHROOM_ISLAND_VEGETATION);
      p_126713_.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.BROWN_MUSHROOM_TAIGA);
      p_126713_.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.RED_MUSHROOM_TAIGA);
   }

   public static void addPlainVegetation(BiomeGenerationSettings.Builder p_126715_) {
      p_126715_.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.TREES_PLAINS);
      p_126715_.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.FLOWER_PLAINS);
      p_126715_.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.PATCH_GRASS_PLAIN);
   }

   public static void addDesertVegetation(BiomeGenerationSettings.Builder p_126717_) {
      p_126717_.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.PATCH_DEAD_BUSH_2);
   }

   public static void addGiantTaigaVegetation(BiomeGenerationSettings.Builder p_126719_) {
      p_126719_.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.PATCH_GRASS_TAIGA);
      p_126719_.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.PATCH_DEAD_BUSH);
      p_126719_.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.BROWN_MUSHROOM_OLD_GROWTH);
      p_126719_.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.RED_MUSHROOM_OLD_GROWTH);
   }

   public static void addDefaultFlowers(BiomeGenerationSettings.Builder p_126721_) {
      p_126721_.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.FLOWER_DEFAULT);
   }

   public static void addMeadowVegetation(BiomeGenerationSettings.Builder p_194719_) {
      p_194719_.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.PATCH_GRASS_PLAIN);
      p_194719_.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.FLOWER_MEADOW);
      p_194719_.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.TREES_MEADOW);
   }

   public static void addWarmFlowers(BiomeGenerationSettings.Builder p_126723_) {
      p_126723_.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.FLOWER_WARM);
   }

   public static void addDefaultGrass(BiomeGenerationSettings.Builder p_126725_) {
      p_126725_.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.PATCH_GRASS_BADLANDS);
   }

   public static void addTaigaGrass(BiomeGenerationSettings.Builder p_126727_) {
      p_126727_.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.PATCH_GRASS_TAIGA_2);
      p_126727_.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.BROWN_MUSHROOM_TAIGA);
      p_126727_.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.RED_MUSHROOM_TAIGA);
   }

   public static void addPlainGrass(BiomeGenerationSettings.Builder p_126729_) {
      p_126729_.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.PATCH_TALL_GRASS_2);
   }

   public static void addDefaultMushrooms(BiomeGenerationSettings.Builder p_126731_) {
      p_126731_.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.BROWN_MUSHROOM_NORMAL);
      p_126731_.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.RED_MUSHROOM_NORMAL);
   }

   public static void addDefaultExtraVegetation(BiomeGenerationSettings.Builder p_126746_) {
      p_126746_.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.PATCH_SUGAR_CANE);
      p_126746_.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.PATCH_PUMPKIN);
   }

   public static void addBadlandExtraVegetation(BiomeGenerationSettings.Builder p_126748_) {
      p_126748_.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.PATCH_SUGAR_CANE_BADLANDS);
      p_126748_.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.PATCH_PUMPKIN);
      p_126748_.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.PATCH_CACTUS_DECORATED);
   }

   public static void addJungleMelons(BiomeGenerationSettings.Builder p_198930_) {
      p_198930_.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.PATCH_MELON);
   }

   public static void addSparseJungleMelons(BiomeGenerationSettings.Builder p_198932_) {
      p_198932_.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.PATCH_MELON_SPARSE);
   }

   public static void addJungleVines(BiomeGenerationSettings.Builder p_198934_) {
      p_198934_.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.VINES);
   }

   public static void addDesertExtraVegetation(BiomeGenerationSettings.Builder p_126752_) {
      p_126752_.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.PATCH_SUGAR_CANE_DESERT);
      p_126752_.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.PATCH_PUMPKIN);
      p_126752_.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.PATCH_CACTUS_DESERT);
   }

   public static void addSwampExtraVegetation(BiomeGenerationSettings.Builder p_126754_) {
      p_126754_.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.PATCH_SUGAR_CANE_SWAMP);
      p_126754_.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.PATCH_PUMPKIN);
   }

   public static void addDesertExtraDecoration(BiomeGenerationSettings.Builder p_126756_) {
      p_126756_.addFeature(GenerationStep.Decoration.SURFACE_STRUCTURES, MiscOverworldPlacements.DESERT_WELL);
   }

   public static void addFossilDecoration(BiomeGenerationSettings.Builder p_126758_) {
      p_126758_.addFeature(GenerationStep.Decoration.UNDERGROUND_STRUCTURES, CavePlacements.FOSSIL_UPPER);
      p_126758_.addFeature(GenerationStep.Decoration.UNDERGROUND_STRUCTURES, CavePlacements.FOSSIL_LOWER);
   }

   public static void addColdOceanExtraVegetation(BiomeGenerationSettings.Builder p_126760_) {
      p_126760_.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, AquaticPlacements.KELP_COLD);
   }

   public static void addDefaultSeagrass(BiomeGenerationSettings.Builder p_126762_) {
      p_126762_.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, AquaticPlacements.SEAGRASS_SIMPLE);
   }

   public static void addLukeWarmKelp(BiomeGenerationSettings.Builder p_126764_) {
      p_126764_.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, AquaticPlacements.KELP_WARM);
   }

   public static void addDefaultSprings(BiomeGenerationSettings.Builder p_126766_) {
      p_126766_.addFeature(GenerationStep.Decoration.FLUID_SPRINGS, MiscOverworldPlacements.SPRING_WATER);
      p_126766_.addFeature(GenerationStep.Decoration.FLUID_SPRINGS, MiscOverworldPlacements.SPRING_LAVA);
   }

   public static void addFrozenSprings(BiomeGenerationSettings.Builder p_194732_) {
      p_194732_.addFeature(GenerationStep.Decoration.FLUID_SPRINGS, MiscOverworldPlacements.SPRING_LAVA_FROZEN);
   }

   public static void addIcebergs(BiomeGenerationSettings.Builder p_126768_) {
      p_126768_.addFeature(GenerationStep.Decoration.LOCAL_MODIFICATIONS, MiscOverworldPlacements.ICEBERG_PACKED);
      p_126768_.addFeature(GenerationStep.Decoration.LOCAL_MODIFICATIONS, MiscOverworldPlacements.ICEBERG_BLUE);
   }

   public static void addBlueIce(BiomeGenerationSettings.Builder p_126770_) {
      p_126770_.addFeature(GenerationStep.Decoration.SURFACE_STRUCTURES, MiscOverworldPlacements.BLUE_ICE);
   }

   public static void addSurfaceFreezing(BiomeGenerationSettings.Builder p_126772_) {
      p_126772_.addFeature(GenerationStep.Decoration.TOP_LAYER_MODIFICATION, MiscOverworldPlacements.FREEZE_TOP_LAYER);
   }

   public static void addNetherDefaultOres(BiomeGenerationSettings.Builder p_126774_) {
      p_126774_.addFeature(GenerationStep.Decoration.UNDERGROUND_DECORATION, OrePlacements.ORE_GRAVEL_NETHER);
      p_126774_.addFeature(GenerationStep.Decoration.UNDERGROUND_DECORATION, OrePlacements.ORE_BLACKSTONE);
      p_126774_.addFeature(GenerationStep.Decoration.UNDERGROUND_DECORATION, OrePlacements.ORE_GOLD_NETHER);
      p_126774_.addFeature(GenerationStep.Decoration.UNDERGROUND_DECORATION, OrePlacements.ORE_QUARTZ_NETHER);
      addAncientDebris(p_126774_);
   }

   public static void addAncientDebris(BiomeGenerationSettings.Builder p_126776_) {
      p_126776_.addFeature(GenerationStep.Decoration.UNDERGROUND_DECORATION, OrePlacements.ORE_ANCIENT_DEBRIS_LARGE);
      p_126776_.addFeature(GenerationStep.Decoration.UNDERGROUND_DECORATION, OrePlacements.ORE_ANCIENT_DEBRIS_SMALL);
   }

   public static void addDefaultCrystalFormations(BiomeGenerationSettings.Builder p_176858_) {
      p_176858_.addFeature(GenerationStep.Decoration.LOCAL_MODIFICATIONS, CavePlacements.AMETHYST_GEODE);
   }

   public static void farmAnimals(MobSpawnSettings.Builder p_126735_) {
      p_126735_.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.SHEEP, 12, 4, 4));
      p_126735_.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.PIG, 10, 4, 4));
      p_126735_.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.CHICKEN, 10, 4, 4));
      p_126735_.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.COW, 8, 4, 4));
   }

   public static void caveSpawns(MobSpawnSettings.Builder p_176860_) {
      p_176860_.addSpawn(MobCategory.AMBIENT, new MobSpawnSettings.SpawnerData(EntityType.BAT, 10, 8, 8));
      p_176860_.addSpawn(MobCategory.UNDERGROUND_WATER_CREATURE, new MobSpawnSettings.SpawnerData(EntityType.GLOW_SQUID, 10, 4, 6));
   }

   public static void commonSpawns(MobSpawnSettings.Builder p_126789_) {
      caveSpawns(p_126789_);
      monsters(p_126789_, 95, 5, 100, false);
   }

   public static void oceanSpawns(MobSpawnSettings.Builder p_126741_, int p_126742_, int p_126743_, int p_126744_) {
      p_126741_.addSpawn(MobCategory.WATER_CREATURE, new MobSpawnSettings.SpawnerData(EntityType.SQUID, p_126742_, 1, p_126743_));
      p_126741_.addSpawn(MobCategory.WATER_AMBIENT, new MobSpawnSettings.SpawnerData(EntityType.COD, p_126744_, 3, 6));
      commonSpawns(p_126741_);
      p_126741_.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(EntityType.DROWNED, 5, 1, 1));
   }

   public static void warmOceanSpawns(MobSpawnSettings.Builder p_126737_, int p_126738_, int p_126739_) {
      p_126737_.addSpawn(MobCategory.WATER_CREATURE, new MobSpawnSettings.SpawnerData(EntityType.SQUID, p_126738_, p_126739_, 4));
      p_126737_.addSpawn(MobCategory.WATER_AMBIENT, new MobSpawnSettings.SpawnerData(EntityType.TROPICAL_FISH, 25, 8, 8));
      p_126737_.addSpawn(MobCategory.WATER_CREATURE, new MobSpawnSettings.SpawnerData(EntityType.DOLPHIN, 2, 1, 2));
      p_126737_.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(EntityType.DROWNED, 5, 1, 1));
      commonSpawns(p_126737_);
   }

   public static void plainsSpawns(MobSpawnSettings.Builder p_126793_) {
      farmAnimals(p_126793_);
      p_126793_.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.HORSE, 5, 2, 6));
      p_126793_.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.DONKEY, 1, 1, 3));
      commonSpawns(p_126793_);
   }

   public static void snowySpawns(MobSpawnSettings.Builder p_126797_) {
      p_126797_.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.RABBIT, 10, 2, 3));
      p_126797_.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.POLAR_BEAR, 1, 1, 2));
      caveSpawns(p_126797_);
      monsters(p_126797_, 95, 5, 20, false);
      p_126797_.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(EntityType.STRAY, 80, 4, 4));
   }

   public static void desertSpawns(MobSpawnSettings.Builder p_126801_) {
      p_126801_.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.RABBIT, 4, 2, 3));
      caveSpawns(p_126801_);
      monsters(p_126801_, 19, 1, 100, false);
      p_126801_.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(EntityType.HUSK, 80, 4, 4));
   }

   public static void dripstoneCavesSpawns(MobSpawnSettings.Builder p_194734_) {
      caveSpawns(p_194734_);
      int i = 95;
      monsters(p_194734_, 95, 5, 100, false);
      p_194734_.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(EntityType.DROWNED, 95, 4, 4));
   }

   public static void monsters(MobSpawnSettings.Builder p_194726_, int p_194727_, int p_194728_, int p_194729_, boolean p_194730_) {
      p_194726_.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(EntityType.SPIDER, 100, 4, 4));
      p_194726_.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(p_194730_ ? EntityType.DROWNED : EntityType.ZOMBIE, p_194727_, 4, 4));
      p_194726_.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(EntityType.ZOMBIE_VILLAGER, p_194728_, 1, 1));
      p_194726_.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(EntityType.SKELETON, p_194729_, 4, 4));
      p_194726_.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(EntityType.CREEPER, 100, 4, 4));
      p_194726_.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(EntityType.SLIME, 100, 4, 4));
      p_194726_.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(EntityType.ENDERMAN, 10, 1, 4));
      p_194726_.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(EntityType.WITCH, 5, 1, 1));
   }

   public static void mooshroomSpawns(MobSpawnSettings.Builder p_126805_) {
      p_126805_.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.MOOSHROOM, 8, 4, 8));
      caveSpawns(p_126805_);
   }

   public static void baseJungleSpawns(MobSpawnSettings.Builder p_126809_) {
      farmAnimals(p_126809_);
      p_126809_.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.CHICKEN, 10, 4, 4));
      commonSpawns(p_126809_);
   }

   public static void endSpawns(MobSpawnSettings.Builder p_126813_) {
      p_126813_.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(EntityType.ENDERMAN, 10, 4, 4));
   }
}