package net.minecraft.util.valueproviders;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Random;
import java.util.function.Function;

public class TrapezoidFloat extends FloatProvider {
   public static final Codec<TrapezoidFloat> CODEC = RecordCodecBuilder.<TrapezoidFloat>create((p_146578_) -> {
      return p_146578_.group(Codec.FLOAT.fieldOf("min").forGetter((p_146588_) -> {
         return p_146588_.min;
      }), Codec.FLOAT.fieldOf("max").forGetter((p_146586_) -> {
         return p_146586_.max;
      }), Codec.FLOAT.fieldOf("plateau").forGetter((p_146583_) -> {
         return p_146583_.plateau;
      })).apply(p_146578_, TrapezoidFloat::new);
   }).comapFlatMap((p_146576_) -> {
      if (p_146576_.max < p_146576_.min) {
         return DataResult.error("Max must be larger than min: [" + p_146576_.min + ", " + p_146576_.max + "]");
      } else {
         return p_146576_.plateau > p_146576_.max - p_146576_.min ? DataResult.error("Plateau can at most be the full span: [" + p_146576_.min + ", " + p_146576_.max + "]") : DataResult.success(p_146576_);
      }
   }, Function.identity());
   private final float min;
   private final float max;
   private final float plateau;

   public static TrapezoidFloat of(float p_146572_, float p_146573_, float p_146574_) {
      return new TrapezoidFloat(p_146572_, p_146573_, p_146574_);
   }

   private TrapezoidFloat(float p_146567_, float p_146568_, float p_146569_) {
      this.min = p_146567_;
      this.max = p_146568_;
      this.plateau = p_146569_;
   }

   public float sample(Random p_146580_) {
      float f = this.max - this.min;
      float f1 = (f - this.plateau) / 2.0F;
      float f2 = f - f1;
      return this.min + p_146580_.nextFloat() * f2 + p_146580_.nextFloat() * f1;
   }

   public float getMinValue() {
      return this.min;
   }

   public float getMaxValue() {
      return this.max;
   }

   public FloatProviderType<?> getType() {
      return FloatProviderType.TRAPEZOID;
   }

   public String toString() {
      return "trapezoid(" + this.plateau + ") in [" + this.min + "-" + this.max + "]";
   }
}