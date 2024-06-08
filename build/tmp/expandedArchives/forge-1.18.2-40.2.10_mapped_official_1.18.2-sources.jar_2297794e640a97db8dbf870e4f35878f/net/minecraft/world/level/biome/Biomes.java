package net.minecraft.world.level.biome;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public abstract class Biomes {
   public static final ResourceKey<Biome> THE_VOID = register("the_void");
   public static final ResourceKey<Biome> PLAINS = register("plains");
   public static final ResourceKey<Biome> SUNFLOWER_PLAINS = register("sunflower_plains");
   public static final ResourceKey<Biome> SNOWY_PLAINS = register("snowy_plains");
   public static final ResourceKey<Biome> ICE_SPIKES = register("ice_spikes");
   public static final ResourceKey<Biome> DESERT = register("desert");
   public static final ResourceKey<Biome> SWAMP = register("swamp");
   public static final ResourceKey<Biome> FOREST = register("forest");
   public static final ResourceKey<Biome> FLOWER_FOREST = register("flower_forest");
   public static final ResourceKey<Biome> BIRCH_FOREST = register("birch_forest");
   public static final ResourceKey<Biome> DARK_FOREST = register("dark_forest");
   public static final ResourceKey<Biome> OLD_GROWTH_BIRCH_FOREST = register("old_growth_birch_forest");
   public static final ResourceKey<Biome> OLD_GROWTH_PINE_TAIGA = register("old_growth_pine_taiga");
   public static final ResourceKey<Biome> OLD_GROWTH_SPRUCE_TAIGA = register("old_growth_spruce_taiga");
   public static final ResourceKey<Biome> TAIGA = register("taiga");
   public static final ResourceKey<Biome> SNOWY_TAIGA = register("snowy_taiga");
   public static final ResourceKey<Biome> SAVANNA = register("savanna");
   public static final ResourceKey<Biome> SAVANNA_PLATEAU = register("savanna_plateau");
   public static final ResourceKey<Biome> WINDSWEPT_HILLS = register("windswept_hills");
   public static final ResourceKey<Biome> WINDSWEPT_GRAVELLY_HILLS = register("windswept_gravelly_hills");
   public static final ResourceKey<Biome> WINDSWEPT_FOREST = register("windswept_forest");
   public static final ResourceKey<Biome> WINDSWEPT_SAVANNA = register("windswept_savanna");
   public static final ResourceKey<Biome> JUNGLE = register("jungle");
   public static final ResourceKey<Biome> SPARSE_JUNGLE = register("sparse_jungle");
   public static final ResourceKey<Biome> BAMBOO_JUNGLE = register("bamboo_jungle");
   public static final ResourceKey<Biome> BADLANDS = register("badlands");
   public static final ResourceKey<Biome> ERODED_BADLANDS = register("eroded_badlands");
   public static final ResourceKey<Biome> WOODED_BADLANDS = register("wooded_badlands");
   public static final ResourceKey<Biome> MEADOW = register("meadow");
   public static final ResourceKey<Biome> GROVE = register("grove");
   public static final ResourceKey<Biome> SNOWY_SLOPES = register("snowy_slopes");
   public static final ResourceKey<Biome> FROZEN_PEAKS = register("frozen_peaks");
   public static final ResourceKey<Biome> JAGGED_PEAKS = register("jagged_peaks");
   public static final ResourceKey<Biome> STONY_PEAKS = register("stony_peaks");
   public static final ResourceKey<Biome> RIVER = register("river");
   public static final ResourceKey<Biome> FROZEN_RIVER = register("frozen_river");
   public static final ResourceKey<Biome> BEACH = register("beach");
   public static final ResourceKey<Biome> SNOWY_BEACH = register("snowy_beach");
   public static final ResourceKey<Biome> STONY_SHORE = register("stony_shore");
   public static final ResourceKey<Biome> WARM_OCEAN = register("warm_ocean");
   public static final ResourceKey<Biome> LUKEWARM_OCEAN = register("lukewarm_ocean");
   public static final ResourceKey<Biome> DEEP_LUKEWARM_OCEAN = register("deep_lukewarm_ocean");
   public static final ResourceKey<Biome> OCEAN = register("ocean");
   public static final ResourceKey<Biome> DEEP_OCEAN = register("deep_ocean");
   public static final ResourceKey<Biome> COLD_OCEAN = register("cold_ocean");
   public static final ResourceKey<Biome> DEEP_COLD_OCEAN = register("deep_cold_ocean");
   public static final ResourceKey<Biome> FROZEN_OCEAN = register("frozen_ocean");
   public static final ResourceKey<Biome> DEEP_FROZEN_OCEAN = register("deep_frozen_ocean");
   public static final ResourceKey<Biome> MUSHROOM_FIELDS = register("mushroom_fields");
   public static final ResourceKey<Biome> DRIPSTONE_CAVES = register("dripstone_caves");
   public static final ResourceKey<Biome> LUSH_CAVES = register("lush_caves");
   public static final ResourceKey<Biome> NETHER_WASTES = register("nether_wastes");
   public static final ResourceKey<Biome> WARPED_FOREST = register("warped_forest");
   public static final ResourceKey<Biome> CRIMSON_FOREST = register("crimson_forest");
   public static final ResourceKey<Biome> SOUL_SAND_VALLEY = register("soul_sand_valley");
   public static final ResourceKey<Biome> BASALT_DELTAS = register("basalt_deltas");
   public static final ResourceKey<Biome> THE_END = register("the_end");
   public static final ResourceKey<Biome> END_HIGHLANDS = register("end_highlands");
   public static final ResourceKey<Biome> END_MIDLANDS = register("end_midlands");
   public static final ResourceKey<Biome> SMALL_END_ISLANDS = register("small_end_islands");
   public static final ResourceKey<Biome> END_BARRENS = register("end_barrens");

   private static ResourceKey<Biome> register(String p_48229_) {
      return ResourceKey.create(Registry.BIOME_REGISTRY, new ResourceLocation(p_48229_));
   }
}