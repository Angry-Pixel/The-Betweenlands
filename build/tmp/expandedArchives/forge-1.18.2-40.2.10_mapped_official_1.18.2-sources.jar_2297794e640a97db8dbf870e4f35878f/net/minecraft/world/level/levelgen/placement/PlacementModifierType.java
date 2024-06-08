package net.minecraft.world.level.levelgen.placement;

import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;

public interface PlacementModifierType<P extends PlacementModifier> {
   PlacementModifierType<BlockPredicateFilter> BLOCK_PREDICATE_FILTER = register("block_predicate_filter", BlockPredicateFilter.CODEC);
   PlacementModifierType<RarityFilter> RARITY_FILTER = register("rarity_filter", RarityFilter.CODEC);
   PlacementModifierType<SurfaceRelativeThresholdFilter> SURFACE_RELATIVE_THRESHOLD_FILTER = register("surface_relative_threshold_filter", SurfaceRelativeThresholdFilter.CODEC);
   PlacementModifierType<SurfaceWaterDepthFilter> SURFACE_WATER_DEPTH_FILTER = register("surface_water_depth_filter", SurfaceWaterDepthFilter.CODEC);
   PlacementModifierType<BiomeFilter> BIOME_FILTER = register("biome", BiomeFilter.CODEC);
   PlacementModifierType<CountPlacement> COUNT = register("count", CountPlacement.CODEC);
   PlacementModifierType<NoiseBasedCountPlacement> NOISE_BASED_COUNT = register("noise_based_count", NoiseBasedCountPlacement.CODEC);
   PlacementModifierType<NoiseThresholdCountPlacement> NOISE_THRESHOLD_COUNT = register("noise_threshold_count", NoiseThresholdCountPlacement.CODEC);
   PlacementModifierType<CountOnEveryLayerPlacement> COUNT_ON_EVERY_LAYER = register("count_on_every_layer", CountOnEveryLayerPlacement.CODEC);
   PlacementModifierType<EnvironmentScanPlacement> ENVIRONMENT_SCAN = register("environment_scan", EnvironmentScanPlacement.CODEC);
   PlacementModifierType<HeightmapPlacement> HEIGHTMAP = register("heightmap", HeightmapPlacement.CODEC);
   PlacementModifierType<HeightRangePlacement> HEIGHT_RANGE = register("height_range", HeightRangePlacement.CODEC);
   PlacementModifierType<InSquarePlacement> IN_SQUARE = register("in_square", InSquarePlacement.CODEC);
   PlacementModifierType<RandomOffsetPlacement> RANDOM_OFFSET = register("random_offset", RandomOffsetPlacement.CODEC);
   PlacementModifierType<CarvingMaskPlacement> CARVING_MASK_PLACEMENT = register("carving_mask", CarvingMaskPlacement.CODEC);

   Codec<P> codec();

   private static <P extends PlacementModifier> PlacementModifierType<P> register(String p_191867_, Codec<P> p_191868_) {
      return Registry.register(Registry.PLACEMENT_MODIFIERS, p_191867_, () -> {
         return p_191868_;
      });
   }
}