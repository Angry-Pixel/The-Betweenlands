package net.minecraft.world.level.levelgen.carver;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.valueproviders.FloatProvider;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.heightproviders.HeightProvider;

public class CanyonCarverConfiguration extends CarverConfiguration {
   public static final Codec<CanyonCarverConfiguration> CODEC = RecordCodecBuilder.create((p_158984_) -> {
      return p_158984_.group(CarverConfiguration.CODEC.forGetter((p_158990_) -> {
         return p_158990_;
      }), FloatProvider.CODEC.fieldOf("vertical_rotation").forGetter((p_158988_) -> {
         return p_158988_.verticalRotation;
      }), CanyonCarverConfiguration.CanyonShapeConfiguration.CODEC.fieldOf("shape").forGetter((p_158986_) -> {
         return p_158986_.shape;
      })).apply(p_158984_, CanyonCarverConfiguration::new);
   });
   public final FloatProvider verticalRotation;
   public final CanyonCarverConfiguration.CanyonShapeConfiguration shape;

   public CanyonCarverConfiguration(float p_190586_, HeightProvider p_190587_, FloatProvider p_190588_, VerticalAnchor p_190589_, CarverDebugSettings p_190590_, FloatProvider p_190591_, CanyonCarverConfiguration.CanyonShapeConfiguration p_190592_) {
      super(p_190586_, p_190587_, p_190588_, p_190589_, p_190590_);
      this.verticalRotation = p_190591_;
      this.shape = p_190592_;
   }

   public CanyonCarverConfiguration(CarverConfiguration p_158980_, FloatProvider p_158981_, CanyonCarverConfiguration.CanyonShapeConfiguration p_158982_) {
      this(p_158980_.probability, p_158980_.y, p_158980_.yScale, p_158980_.lavaLevel, p_158980_.debugSettings, p_158981_, p_158982_);
   }

   public static class CanyonShapeConfiguration {
      public static final Codec<CanyonCarverConfiguration.CanyonShapeConfiguration> CODEC = RecordCodecBuilder.create((p_159007_) -> {
         return p_159007_.group(FloatProvider.CODEC.fieldOf("distance_factor").forGetter((p_159019_) -> {
            return p_159019_.distanceFactor;
         }), FloatProvider.CODEC.fieldOf("thickness").forGetter((p_159017_) -> {
            return p_159017_.thickness;
         }), ExtraCodecs.NON_NEGATIVE_INT.fieldOf("width_smoothness").forGetter((p_159015_) -> {
            return p_159015_.widthSmoothness;
         }), FloatProvider.CODEC.fieldOf("horizontal_radius_factor").forGetter((p_159013_) -> {
            return p_159013_.horizontalRadiusFactor;
         }), Codec.FLOAT.fieldOf("vertical_radius_default_factor").forGetter((p_159011_) -> {
            return p_159011_.verticalRadiusDefaultFactor;
         }), Codec.FLOAT.fieldOf("vertical_radius_center_factor").forGetter((p_159009_) -> {
            return p_159009_.verticalRadiusCenterFactor;
         })).apply(p_159007_, CanyonCarverConfiguration.CanyonShapeConfiguration::new);
      });
      public final FloatProvider distanceFactor;
      public final FloatProvider thickness;
      public final int widthSmoothness;
      public final FloatProvider horizontalRadiusFactor;
      public final float verticalRadiusDefaultFactor;
      public final float verticalRadiusCenterFactor;

      public CanyonShapeConfiguration(FloatProvider p_159000_, FloatProvider p_159001_, int p_159002_, FloatProvider p_159003_, float p_159004_, float p_159005_) {
         this.widthSmoothness = p_159002_;
         this.horizontalRadiusFactor = p_159003_;
         this.verticalRadiusDefaultFactor = p_159004_;
         this.verticalRadiusCenterFactor = p_159005_;
         this.distanceFactor = p_159000_;
         this.thickness = p_159001_;
      }
   }
}