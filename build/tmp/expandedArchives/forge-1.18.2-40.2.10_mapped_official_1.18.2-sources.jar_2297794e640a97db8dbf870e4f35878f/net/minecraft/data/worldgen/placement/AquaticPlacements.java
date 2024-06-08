package net.minecraft.data.worldgen.placement;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.data.worldgen.features.AquaticFeatures;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.placement.BiomeFilter;
import net.minecraft.world.level.levelgen.placement.BlockPredicateFilter;
import net.minecraft.world.level.levelgen.placement.CarvingMaskPlacement;
import net.minecraft.world.level.levelgen.placement.CountPlacement;
import net.minecraft.world.level.levelgen.placement.InSquarePlacement;
import net.minecraft.world.level.levelgen.placement.NoiseBasedCountPlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.RarityFilter;

public class AquaticPlacements {
   public static final Holder<PlacedFeature> SEAGRASS_WARM = PlacementUtils.register("seagrass_warm", AquaticFeatures.SEAGRASS_SHORT, seagrassPlacement(80));
   public static final Holder<PlacedFeature> SEAGRASS_NORMAL = PlacementUtils.register("seagrass_normal", AquaticFeatures.SEAGRASS_SHORT, seagrassPlacement(48));
   public static final Holder<PlacedFeature> SEAGRASS_COLD = PlacementUtils.register("seagrass_cold", AquaticFeatures.SEAGRASS_SHORT, seagrassPlacement(32));
   public static final Holder<PlacedFeature> SEAGRASS_RIVER = PlacementUtils.register("seagrass_river", AquaticFeatures.SEAGRASS_SLIGHTLY_LESS_SHORT, seagrassPlacement(48));
   public static final Holder<PlacedFeature> SEAGRASS_SWAMP = PlacementUtils.register("seagrass_swamp", AquaticFeatures.SEAGRASS_MID, seagrassPlacement(64));
   public static final Holder<PlacedFeature> SEAGRASS_DEEP_WARM = PlacementUtils.register("seagrass_deep_warm", AquaticFeatures.SEAGRASS_TALL, seagrassPlacement(80));
   public static final Holder<PlacedFeature> SEAGRASS_DEEP = PlacementUtils.register("seagrass_deep", AquaticFeatures.SEAGRASS_TALL, seagrassPlacement(48));
   public static final Holder<PlacedFeature> SEAGRASS_DEEP_COLD = PlacementUtils.register("seagrass_deep_cold", AquaticFeatures.SEAGRASS_TALL, seagrassPlacement(40));
   public static final Holder<PlacedFeature> SEAGRASS_SIMPLE = PlacementUtils.register("seagrass_simple", AquaticFeatures.SEAGRASS_SIMPLE, CarvingMaskPlacement.forStep(GenerationStep.Carving.LIQUID), RarityFilter.onAverageOnceEvery(10), BlockPredicateFilter.forPredicate(BlockPredicate.allOf(BlockPredicate.matchesBlock(Blocks.STONE, new BlockPos(0, -1, 0)), BlockPredicate.matchesBlock(Blocks.WATER, BlockPos.ZERO), BlockPredicate.matchesBlock(Blocks.WATER, new BlockPos(0, 1, 0)))), BiomeFilter.biome());
   public static final Holder<PlacedFeature> SEA_PICKLE = PlacementUtils.register("sea_pickle", AquaticFeatures.SEA_PICKLE, RarityFilter.onAverageOnceEvery(16), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP_TOP_SOLID, BiomeFilter.biome());
   public static final Holder<PlacedFeature> KELP_COLD = PlacementUtils.register("kelp_cold", AquaticFeatures.KELP, NoiseBasedCountPlacement.of(120, 80.0D, 0.0D), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP_TOP_SOLID, BiomeFilter.biome());
   public static final Holder<PlacedFeature> KELP_WARM = PlacementUtils.register("kelp_warm", AquaticFeatures.KELP, NoiseBasedCountPlacement.of(80, 80.0D, 0.0D), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP_TOP_SOLID, BiomeFilter.biome());
   public static final Holder<PlacedFeature> WARM_OCEAN_VEGETATION = PlacementUtils.register("warm_ocean_vegetation", AquaticFeatures.WARM_OCEAN_VEGETATION, NoiseBasedCountPlacement.of(20, 400.0D, 0.0D), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP_TOP_SOLID, BiomeFilter.biome());

   public static List<PlacementModifier> seagrassPlacement(int p_195234_) {
      return List.of(InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP_TOP_SOLID, CountPlacement.of(p_195234_), BiomeFilter.biome());
   }
}