package net.minecraft.data.worldgen.biome;

import net.minecraft.core.Holder;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;

public abstract class Biomes {
   private static void register(ResourceKey<Biome> p_206464_, Biome p_206465_) {
      BuiltinRegistries.register(BuiltinRegistries.BIOME, p_206464_, p_206465_);
   }

   public static Holder<Biome> bootstrap() {
      return BuiltinRegistries.BIOME.getHolderOrThrow(net.minecraft.world.level.biome.Biomes.PLAINS);
   }

   static {
      register(net.minecraft.world.level.biome.Biomes.THE_VOID, OverworldBiomes.theVoid());
      register(net.minecraft.world.level.biome.Biomes.PLAINS, OverworldBiomes.plains(false, false, false));
      register(net.minecraft.world.level.biome.Biomes.SUNFLOWER_PLAINS, OverworldBiomes.plains(true, false, false));
      register(net.minecraft.world.level.biome.Biomes.SNOWY_PLAINS, OverworldBiomes.plains(false, true, false));
      register(net.minecraft.world.level.biome.Biomes.ICE_SPIKES, OverworldBiomes.plains(false, true, true));
      register(net.minecraft.world.level.biome.Biomes.DESERT, OverworldBiomes.desert());
      register(net.minecraft.world.level.biome.Biomes.SWAMP, OverworldBiomes.swamp());
      register(net.minecraft.world.level.biome.Biomes.FOREST, OverworldBiomes.forest(false, false, false));
      register(net.minecraft.world.level.biome.Biomes.FLOWER_FOREST, OverworldBiomes.forest(false, false, true));
      register(net.minecraft.world.level.biome.Biomes.BIRCH_FOREST, OverworldBiomes.forest(true, false, false));
      register(net.minecraft.world.level.biome.Biomes.DARK_FOREST, OverworldBiomes.darkForest());
      register(net.minecraft.world.level.biome.Biomes.OLD_GROWTH_BIRCH_FOREST, OverworldBiomes.forest(true, true, false));
      register(net.minecraft.world.level.biome.Biomes.OLD_GROWTH_PINE_TAIGA, OverworldBiomes.oldGrowthTaiga(false));
      register(net.minecraft.world.level.biome.Biomes.OLD_GROWTH_SPRUCE_TAIGA, OverworldBiomes.oldGrowthTaiga(true));
      register(net.minecraft.world.level.biome.Biomes.TAIGA, OverworldBiomes.taiga(false));
      register(net.minecraft.world.level.biome.Biomes.SNOWY_TAIGA, OverworldBiomes.taiga(true));
      register(net.minecraft.world.level.biome.Biomes.SAVANNA, OverworldBiomes.savanna(false, false));
      register(net.minecraft.world.level.biome.Biomes.SAVANNA_PLATEAU, OverworldBiomes.savanna(false, true));
      register(net.minecraft.world.level.biome.Biomes.WINDSWEPT_HILLS, OverworldBiomes.windsweptHills(false));
      register(net.minecraft.world.level.biome.Biomes.WINDSWEPT_GRAVELLY_HILLS, OverworldBiomes.windsweptHills(false));
      register(net.minecraft.world.level.biome.Biomes.WINDSWEPT_FOREST, OverworldBiomes.windsweptHills(true));
      register(net.minecraft.world.level.biome.Biomes.WINDSWEPT_SAVANNA, OverworldBiomes.savanna(true, false));
      register(net.minecraft.world.level.biome.Biomes.JUNGLE, OverworldBiomes.jungle());
      register(net.minecraft.world.level.biome.Biomes.SPARSE_JUNGLE, OverworldBiomes.sparseJungle());
      register(net.minecraft.world.level.biome.Biomes.BAMBOO_JUNGLE, OverworldBiomes.bambooJungle());
      register(net.minecraft.world.level.biome.Biomes.BADLANDS, OverworldBiomes.badlands(false));
      register(net.minecraft.world.level.biome.Biomes.ERODED_BADLANDS, OverworldBiomes.badlands(false));
      register(net.minecraft.world.level.biome.Biomes.WOODED_BADLANDS, OverworldBiomes.badlands(true));
      register(net.minecraft.world.level.biome.Biomes.MEADOW, OverworldBiomes.meadow());
      register(net.minecraft.world.level.biome.Biomes.GROVE, OverworldBiomes.grove());
      register(net.minecraft.world.level.biome.Biomes.SNOWY_SLOPES, OverworldBiomes.snowySlopes());
      register(net.minecraft.world.level.biome.Biomes.FROZEN_PEAKS, OverworldBiomes.frozenPeaks());
      register(net.minecraft.world.level.biome.Biomes.JAGGED_PEAKS, OverworldBiomes.jaggedPeaks());
      register(net.minecraft.world.level.biome.Biomes.STONY_PEAKS, OverworldBiomes.stonyPeaks());
      register(net.minecraft.world.level.biome.Biomes.RIVER, OverworldBiomes.river(false));
      register(net.minecraft.world.level.biome.Biomes.FROZEN_RIVER, OverworldBiomes.river(true));
      register(net.minecraft.world.level.biome.Biomes.BEACH, OverworldBiomes.beach(false, false));
      register(net.minecraft.world.level.biome.Biomes.SNOWY_BEACH, OverworldBiomes.beach(true, false));
      register(net.minecraft.world.level.biome.Biomes.STONY_SHORE, OverworldBiomes.beach(false, true));
      register(net.minecraft.world.level.biome.Biomes.WARM_OCEAN, OverworldBiomes.warmOcean());
      register(net.minecraft.world.level.biome.Biomes.LUKEWARM_OCEAN, OverworldBiomes.lukeWarmOcean(false));
      register(net.minecraft.world.level.biome.Biomes.DEEP_LUKEWARM_OCEAN, OverworldBiomes.lukeWarmOcean(true));
      register(net.minecraft.world.level.biome.Biomes.OCEAN, OverworldBiomes.ocean(false));
      register(net.minecraft.world.level.biome.Biomes.DEEP_OCEAN, OverworldBiomes.ocean(true));
      register(net.minecraft.world.level.biome.Biomes.COLD_OCEAN, OverworldBiomes.coldOcean(false));
      register(net.minecraft.world.level.biome.Biomes.DEEP_COLD_OCEAN, OverworldBiomes.coldOcean(true));
      register(net.minecraft.world.level.biome.Biomes.FROZEN_OCEAN, OverworldBiomes.frozenOcean(false));
      register(net.minecraft.world.level.biome.Biomes.DEEP_FROZEN_OCEAN, OverworldBiomes.frozenOcean(true));
      register(net.minecraft.world.level.biome.Biomes.MUSHROOM_FIELDS, OverworldBiomes.mushroomFields());
      register(net.minecraft.world.level.biome.Biomes.DRIPSTONE_CAVES, OverworldBiomes.dripstoneCaves());
      register(net.minecraft.world.level.biome.Biomes.LUSH_CAVES, OverworldBiomes.lushCaves());
      register(net.minecraft.world.level.biome.Biomes.NETHER_WASTES, NetherBiomes.netherWastes());
      register(net.minecraft.world.level.biome.Biomes.WARPED_FOREST, NetherBiomes.warpedForest());
      register(net.minecraft.world.level.biome.Biomes.CRIMSON_FOREST, NetherBiomes.crimsonForest());
      register(net.minecraft.world.level.biome.Biomes.SOUL_SAND_VALLEY, NetherBiomes.soulSandValley());
      register(net.minecraft.world.level.biome.Biomes.BASALT_DELTAS, NetherBiomes.basaltDeltas());
      register(net.minecraft.world.level.biome.Biomes.THE_END, EndBiomes.theEnd());
      register(net.minecraft.world.level.biome.Biomes.END_HIGHLANDS, EndBiomes.endHighlands());
      register(net.minecraft.world.level.biome.Biomes.END_MIDLANDS, EndBiomes.endMidlands());
      register(net.minecraft.world.level.biome.Biomes.SMALL_END_ISLANDS, EndBiomes.smallEndIslands());
      register(net.minecraft.world.level.biome.Biomes.END_BARRENS, EndBiomes.endBarrens());
   }
}