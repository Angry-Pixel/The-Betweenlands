package net.minecraft.world.level.levelgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class GeodeLayerSettings {
   private static final Codec<Double> LAYER_RANGE = Codec.doubleRange(0.01D, 50.0D);
   public static final Codec<GeodeLayerSettings> CODEC = RecordCodecBuilder.create((p_158354_) -> {
      return p_158354_.group(LAYER_RANGE.fieldOf("filling").orElse(1.7D).forGetter((p_158362_) -> {
         return p_158362_.filling;
      }), LAYER_RANGE.fieldOf("inner_layer").orElse(2.2D).forGetter((p_158360_) -> {
         return p_158360_.innerLayer;
      }), LAYER_RANGE.fieldOf("middle_layer").orElse(3.2D).forGetter((p_158358_) -> {
         return p_158358_.middleLayer;
      }), LAYER_RANGE.fieldOf("outer_layer").orElse(4.2D).forGetter((p_158356_) -> {
         return p_158356_.outerLayer;
      })).apply(p_158354_, GeodeLayerSettings::new);
   });
   public final double filling;
   public final double innerLayer;
   public final double middleLayer;
   public final double outerLayer;

   public GeodeLayerSettings(double p_158349_, double p_158350_, double p_158351_, double p_158352_) {
      this.filling = p_158349_;
      this.innerLayer = p_158350_;
      this.middleLayer = p_158351_;
      this.outerLayer = p_158352_;
   }
}