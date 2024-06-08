package net.minecraft.world.level.levelgen.feature.configurations;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.valueproviders.FloatProvider;
import net.minecraft.util.valueproviders.IntProvider;

public class LargeDripstoneConfiguration implements FeatureConfiguration {
   public static final Codec<LargeDripstoneConfiguration> CODEC = RecordCodecBuilder.create((p_160966_) -> {
      return p_160966_.group(Codec.intRange(1, 512).fieldOf("floor_to_ceiling_search_range").orElse(30).forGetter((p_160984_) -> {
         return p_160984_.floorToCeilingSearchRange;
      }), IntProvider.codec(1, 60).fieldOf("column_radius").forGetter((p_160982_) -> {
         return p_160982_.columnRadius;
      }), FloatProvider.codec(0.0F, 20.0F).fieldOf("height_scale").forGetter((p_160980_) -> {
         return p_160980_.heightScale;
      }), Codec.floatRange(0.1F, 1.0F).fieldOf("max_column_radius_to_cave_height_ratio").forGetter((p_160978_) -> {
         return p_160978_.maxColumnRadiusToCaveHeightRatio;
      }), FloatProvider.codec(0.1F, 10.0F).fieldOf("stalactite_bluntness").forGetter((p_160976_) -> {
         return p_160976_.stalactiteBluntness;
      }), FloatProvider.codec(0.1F, 10.0F).fieldOf("stalagmite_bluntness").forGetter((p_160974_) -> {
         return p_160974_.stalagmiteBluntness;
      }), FloatProvider.codec(0.0F, 2.0F).fieldOf("wind_speed").forGetter((p_160972_) -> {
         return p_160972_.windSpeed;
      }), Codec.intRange(0, 100).fieldOf("min_radius_for_wind").forGetter((p_160970_) -> {
         return p_160970_.minRadiusForWind;
      }), Codec.floatRange(0.0F, 5.0F).fieldOf("min_bluntness_for_wind").forGetter((p_160968_) -> {
         return p_160968_.minBluntnessForWind;
      })).apply(p_160966_, LargeDripstoneConfiguration::new);
   });
   public final int floorToCeilingSearchRange;
   public final IntProvider columnRadius;
   public final FloatProvider heightScale;
   public final float maxColumnRadiusToCaveHeightRatio;
   public final FloatProvider stalactiteBluntness;
   public final FloatProvider stalagmiteBluntness;
   public final FloatProvider windSpeed;
   public final int minRadiusForWind;
   public final float minBluntnessForWind;

   public LargeDripstoneConfiguration(int p_160956_, IntProvider p_160957_, FloatProvider p_160958_, float p_160959_, FloatProvider p_160960_, FloatProvider p_160961_, FloatProvider p_160962_, int p_160963_, float p_160964_) {
      this.floorToCeilingSearchRange = p_160956_;
      this.columnRadius = p_160957_;
      this.heightScale = p_160958_;
      this.maxColumnRadiusToCaveHeightRatio = p_160959_;
      this.stalactiteBluntness = p_160960_;
      this.stalagmiteBluntness = p_160961_;
      this.windSpeed = p_160962_;
      this.minRadiusForWind = p_160963_;
      this.minBluntnessForWind = p_160964_;
   }
}