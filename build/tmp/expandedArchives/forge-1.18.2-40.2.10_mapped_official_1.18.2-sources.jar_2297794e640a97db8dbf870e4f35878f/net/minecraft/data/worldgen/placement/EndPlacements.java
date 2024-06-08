package net.minecraft.data.worldgen.placement;

import net.minecraft.core.Holder;
import net.minecraft.data.worldgen.features.EndFeatures;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.placement.BiomeFilter;
import net.minecraft.world.level.levelgen.placement.CountPlacement;
import net.minecraft.world.level.levelgen.placement.HeightRangePlacement;
import net.minecraft.world.level.levelgen.placement.InSquarePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.RandomOffsetPlacement;
import net.minecraft.world.level.levelgen.placement.RarityFilter;

public class EndPlacements {
   public static final Holder<PlacedFeature> END_SPIKE = PlacementUtils.register("end_spike", EndFeatures.END_SPIKE, BiomeFilter.biome());
   public static final Holder<PlacedFeature> END_GATEWAY_RETURN = PlacementUtils.register("end_gateway_return", EndFeatures.END_GATEWAY_RETURN, RarityFilter.onAverageOnceEvery(700), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP, RandomOffsetPlacement.vertical(UniformInt.of(3, 9)), BiomeFilter.biome());
   public static final Holder<PlacedFeature> CHORUS_PLANT = PlacementUtils.register("chorus_plant", EndFeatures.CHORUS_PLANT, CountPlacement.of(UniformInt.of(0, 4)), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP, BiomeFilter.biome());
   public static final Holder<PlacedFeature> END_ISLAND_DECORATED = PlacementUtils.register("end_island_decorated", EndFeatures.END_ISLAND, RarityFilter.onAverageOnceEvery(14), PlacementUtils.countExtra(1, 0.25F, 1), InSquarePlacement.spread(), HeightRangePlacement.uniform(VerticalAnchor.absolute(55), VerticalAnchor.absolute(70)), BiomeFilter.biome());
}