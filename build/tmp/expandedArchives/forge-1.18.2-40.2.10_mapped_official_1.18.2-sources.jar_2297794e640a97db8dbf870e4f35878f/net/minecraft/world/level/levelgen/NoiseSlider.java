package net.minecraft.world.level.levelgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.Mth;

public record NoiseSlider(double target, int size, int offset) {
   public static final Codec<NoiseSlider> CODEC = RecordCodecBuilder.create((p_189236_) -> {
      return p_189236_.group(Codec.DOUBLE.fieldOf("target").forGetter((p_189242_) -> {
         return p_189242_.target;
      }), ExtraCodecs.NON_NEGATIVE_INT.fieldOf("size").forGetter((p_189240_) -> {
         return p_189240_.size;
      }), Codec.INT.fieldOf("offset").forGetter((p_189238_) -> {
         return p_189238_.offset;
      })).apply(p_189236_, NoiseSlider::new);
   });

   public double applySlide(double p_209639_, double p_209640_) {
      if (this.size <= 0) {
         return p_209639_;
      } else {
         double d0 = (p_209640_ - (double)this.offset) / (double)this.size;
         return Mth.clampedLerp(this.target, p_209639_, d0);
      }
   }
}