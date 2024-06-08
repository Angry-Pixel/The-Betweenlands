package net.minecraft.world.level.levelgen.feature.configurations;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.valueproviders.FloatProvider;
import net.minecraft.util.valueproviders.IntProvider;

public class DripstoneClusterConfiguration implements FeatureConfiguration {
   public static final Codec<DripstoneClusterConfiguration> CODEC = RecordCodecBuilder.create((p_160784_) -> {
      return p_160784_.group(Codec.intRange(1, 512).fieldOf("floor_to_ceiling_search_range").forGetter((p_160806_) -> {
         return p_160806_.floorToCeilingSearchRange;
      }), IntProvider.codec(1, 128).fieldOf("height").forGetter((p_160804_) -> {
         return p_160804_.height;
      }), IntProvider.codec(1, 128).fieldOf("radius").forGetter((p_160802_) -> {
         return p_160802_.radius;
      }), Codec.intRange(0, 64).fieldOf("max_stalagmite_stalactite_height_diff").forGetter((p_160800_) -> {
         return p_160800_.maxStalagmiteStalactiteHeightDiff;
      }), Codec.intRange(1, 64).fieldOf("height_deviation").forGetter((p_160798_) -> {
         return p_160798_.heightDeviation;
      }), IntProvider.codec(0, 128).fieldOf("dripstone_block_layer_thickness").forGetter((p_160796_) -> {
         return p_160796_.dripstoneBlockLayerThickness;
      }), FloatProvider.codec(0.0F, 2.0F).fieldOf("density").forGetter((p_160794_) -> {
         return p_160794_.density;
      }), FloatProvider.codec(0.0F, 2.0F).fieldOf("wetness").forGetter((p_160792_) -> {
         return p_160792_.wetness;
      }), Codec.floatRange(0.0F, 1.0F).fieldOf("chance_of_dripstone_column_at_max_distance_from_center").forGetter((p_160790_) -> {
         return p_160790_.chanceOfDripstoneColumnAtMaxDistanceFromCenter;
      }), Codec.intRange(1, 64).fieldOf("max_distance_from_edge_affecting_chance_of_dripstone_column").forGetter((p_160788_) -> {
         return p_160788_.maxDistanceFromEdgeAffectingChanceOfDripstoneColumn;
      }), Codec.intRange(1, 64).fieldOf("max_distance_from_center_affecting_height_bias").forGetter((p_160786_) -> {
         return p_160786_.maxDistanceFromCenterAffectingHeightBias;
      })).apply(p_160784_, DripstoneClusterConfiguration::new);
   });
   public final int floorToCeilingSearchRange;
   public final IntProvider height;
   public final IntProvider radius;
   public final int maxStalagmiteStalactiteHeightDiff;
   public final int heightDeviation;
   public final IntProvider dripstoneBlockLayerThickness;
   public final FloatProvider density;
   public final FloatProvider wetness;
   public final float chanceOfDripstoneColumnAtMaxDistanceFromCenter;
   public final int maxDistanceFromEdgeAffectingChanceOfDripstoneColumn;
   public final int maxDistanceFromCenterAffectingHeightBias;

   public DripstoneClusterConfiguration(int p_160772_, IntProvider p_160773_, IntProvider p_160774_, int p_160775_, int p_160776_, IntProvider p_160777_, FloatProvider p_160778_, FloatProvider p_160779_, float p_160780_, int p_160781_, int p_160782_) {
      this.floorToCeilingSearchRange = p_160772_;
      this.height = p_160773_;
      this.radius = p_160774_;
      this.maxStalagmiteStalactiteHeightDiff = p_160775_;
      this.heightDeviation = p_160776_;
      this.dripstoneBlockLayerThickness = p_160777_;
      this.density = p_160778_;
      this.wetness = p_160779_;
      this.chanceOfDripstoneColumnAtMaxDistanceFromCenter = p_160780_;
      this.maxDistanceFromEdgeAffectingChanceOfDripstoneColumn = p_160781_;
      this.maxDistanceFromCenterAffectingHeightBias = p_160782_;
   }
}