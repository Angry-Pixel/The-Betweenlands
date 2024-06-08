package net.minecraft.world.level.levelgen.feature.configurations;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class UnderwaterMagmaConfiguration implements FeatureConfiguration {
   public static final Codec<UnderwaterMagmaConfiguration> CODEC = RecordCodecBuilder.create((p_161273_) -> {
      return p_161273_.group(Codec.intRange(0, 512).fieldOf("floor_search_range").forGetter((p_161279_) -> {
         return p_161279_.floorSearchRange;
      }), Codec.intRange(0, 64).fieldOf("placement_radius_around_floor").forGetter((p_161277_) -> {
         return p_161277_.placementRadiusAroundFloor;
      }), Codec.floatRange(0.0F, 1.0F).fieldOf("placement_probability_per_valid_position").forGetter((p_161275_) -> {
         return p_161275_.placementProbabilityPerValidPosition;
      })).apply(p_161273_, UnderwaterMagmaConfiguration::new);
   });
   public final int floorSearchRange;
   public final int placementRadiusAroundFloor;
   public final float placementProbabilityPerValidPosition;

   public UnderwaterMagmaConfiguration(int p_161269_, int p_161270_, float p_161271_) {
      this.floorSearchRange = p_161269_;
      this.placementRadiusAroundFloor = p_161270_;
      this.placementProbabilityPerValidPosition = p_161271_;
   }
}