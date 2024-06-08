package net.minecraft.data.worldgen.placement;

import java.util.List;
import net.minecraft.core.Holder;
import net.minecraft.data.worldgen.features.NetherFeatures;
import net.minecraft.util.valueproviders.BiasedToBottomInt;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.levelgen.placement.BiomeFilter;
import net.minecraft.world.level.levelgen.placement.CountOnEveryLayerPlacement;
import net.minecraft.world.level.levelgen.placement.CountPlacement;
import net.minecraft.world.level.levelgen.placement.InSquarePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;

public class NetherPlacements {
   public static final Holder<PlacedFeature> DELTA = PlacementUtils.register("delta", NetherFeatures.DELTA, CountOnEveryLayerPlacement.of(40), BiomeFilter.biome());
   public static final Holder<PlacedFeature> SMALL_BASALT_COLUMNS = PlacementUtils.register("small_basalt_columns", NetherFeatures.SMALL_BASALT_COLUMNS, CountOnEveryLayerPlacement.of(4), BiomeFilter.biome());
   public static final Holder<PlacedFeature> LARGE_BASALT_COLUMNS = PlacementUtils.register("large_basalt_columns", NetherFeatures.LARGE_BASALT_COLUMNS, CountOnEveryLayerPlacement.of(2), BiomeFilter.biome());
   public static final Holder<PlacedFeature> BASALT_BLOBS = PlacementUtils.register("basalt_blobs", NetherFeatures.BASALT_BLOBS, CountPlacement.of(75), InSquarePlacement.spread(), PlacementUtils.FULL_RANGE, BiomeFilter.biome());
   public static final Holder<PlacedFeature> BLACKSTONE_BLOBS = PlacementUtils.register("blackstone_blobs", NetherFeatures.BLACKSTONE_BLOBS, CountPlacement.of(25), InSquarePlacement.spread(), PlacementUtils.FULL_RANGE, BiomeFilter.biome());
   public static final Holder<PlacedFeature> GLOWSTONE_EXTRA = PlacementUtils.register("glowstone_extra", NetherFeatures.GLOWSTONE_EXTRA, CountPlacement.of(BiasedToBottomInt.of(0, 9)), InSquarePlacement.spread(), PlacementUtils.RANGE_4_4, BiomeFilter.biome());
   public static final Holder<PlacedFeature> GLOWSTONE = PlacementUtils.register("glowstone", NetherFeatures.GLOWSTONE_EXTRA, CountPlacement.of(10), InSquarePlacement.spread(), PlacementUtils.FULL_RANGE, BiomeFilter.biome());
   public static final Holder<PlacedFeature> CRIMSON_FOREST_VEGETATION = PlacementUtils.register("crimson_forest_vegetation", NetherFeatures.CRIMSON_FOREST_VEGETATION, CountOnEveryLayerPlacement.of(6), BiomeFilter.biome());
   public static final Holder<PlacedFeature> WARPED_FOREST_VEGETATION = PlacementUtils.register("warped_forest_vegetation", NetherFeatures.WARPED_FOREST_VEGETION, CountOnEveryLayerPlacement.of(5), BiomeFilter.biome());
   public static final Holder<PlacedFeature> NETHER_SPROUTS = PlacementUtils.register("nether_sprouts", NetherFeatures.NETHER_SPROUTS, CountOnEveryLayerPlacement.of(4), BiomeFilter.biome());
   public static final Holder<PlacedFeature> TWISTING_VINES = PlacementUtils.register("twisting_vines", NetherFeatures.TWISTING_VINES, CountPlacement.of(10), InSquarePlacement.spread(), PlacementUtils.FULL_RANGE, BiomeFilter.biome());
   public static final Holder<PlacedFeature> WEEPING_VINES = PlacementUtils.register("weeping_vines", NetherFeatures.WEEPING_VINES, CountPlacement.of(10), InSquarePlacement.spread(), PlacementUtils.FULL_RANGE, BiomeFilter.biome());
   public static final Holder<PlacedFeature> PATCH_CRIMSON_ROOTS = PlacementUtils.register("patch_crimson_roots", NetherFeatures.PATCH_CRIMSON_ROOTS, PlacementUtils.FULL_RANGE, BiomeFilter.biome());
   public static final Holder<PlacedFeature> BASALT_PILLAR = PlacementUtils.register("basalt_pillar", NetherFeatures.BASALT_PILLAR, CountPlacement.of(10), InSquarePlacement.spread(), PlacementUtils.FULL_RANGE, BiomeFilter.biome());
   public static final Holder<PlacedFeature> SPRING_DELTA = PlacementUtils.register("spring_delta", NetherFeatures.SPRING_LAVA_NETHER, CountPlacement.of(16), InSquarePlacement.spread(), PlacementUtils.RANGE_4_4, BiomeFilter.biome());
   public static final Holder<PlacedFeature> SPRING_CLOSED = PlacementUtils.register("spring_closed", NetherFeatures.SPRING_NETHER_CLOSED, CountPlacement.of(16), InSquarePlacement.spread(), PlacementUtils.RANGE_10_10, BiomeFilter.biome());
   public static final Holder<PlacedFeature> SPRING_CLOSED_DOUBLE = PlacementUtils.register("spring_closed_double", NetherFeatures.SPRING_NETHER_CLOSED, CountPlacement.of(32), InSquarePlacement.spread(), PlacementUtils.RANGE_10_10, BiomeFilter.biome());
   public static final Holder<PlacedFeature> SPRING_OPEN = PlacementUtils.register("spring_open", NetherFeatures.SPRING_NETHER_OPEN, CountPlacement.of(8), InSquarePlacement.spread(), PlacementUtils.RANGE_4_4, BiomeFilter.biome());
   public static final List<PlacementModifier> FIRE_PLACEMENT = List.of(CountPlacement.of(UniformInt.of(0, 5)), InSquarePlacement.spread(), PlacementUtils.RANGE_4_4, BiomeFilter.biome());
   public static final Holder<PlacedFeature> PATCH_SOUL_FIRE = PlacementUtils.register("patch_soul_fire", NetherFeatures.PATCH_SOUL_FIRE, FIRE_PLACEMENT);
   public static final Holder<PlacedFeature> PATCH_FIRE = PlacementUtils.register("patch_fire", NetherFeatures.PATCH_FIRE, FIRE_PLACEMENT);
}