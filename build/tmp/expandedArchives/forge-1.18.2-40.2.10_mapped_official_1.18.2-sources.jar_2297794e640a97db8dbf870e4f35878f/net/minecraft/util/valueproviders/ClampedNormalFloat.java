package net.minecraft.util.valueproviders;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.util.Mth;

public class ClampedNormalFloat extends FloatProvider {
   public static final Codec<ClampedNormalFloat> CODEC = RecordCodecBuilder.<ClampedNormalFloat>create((p_146431_) -> {
      return p_146431_.group(Codec.FLOAT.fieldOf("mean").forGetter((p_146449_) -> {
         return p_146449_.mean;
      }), Codec.FLOAT.fieldOf("deviation").forGetter((p_146447_) -> {
         return p_146447_.deviation;
      }), Codec.FLOAT.fieldOf("min").forGetter((p_146445_) -> {
         return p_146445_.min;
      }), Codec.FLOAT.fieldOf("max").forGetter((p_146442_) -> {
         return p_146442_.max;
      })).apply(p_146431_, ClampedNormalFloat::new);
   }).comapFlatMap((p_146429_) -> {
      return p_146429_.max < p_146429_.min ? DataResult.error("Max must be larger than min: [" + p_146429_.min + ", " + p_146429_.max + "]") : DataResult.success(p_146429_);
   }, Function.identity());
   private float mean;
   private float deviation;
   private float min;
   private float max;

   public static ClampedNormalFloat of(float p_146424_, float p_146425_, float p_146426_, float p_146427_) {
      return new ClampedNormalFloat(p_146424_, p_146425_, p_146426_, p_146427_);
   }

   private ClampedNormalFloat(float p_146418_, float p_146419_, float p_146420_, float p_146421_) {
      this.mean = p_146418_;
      this.deviation = p_146419_;
      this.min = p_146420_;
      this.max = p_146421_;
   }

   public float sample(Random p_146433_) {
      return sample(p_146433_, this.mean, this.deviation, this.min, this.max);
   }

   public static float sample(Random p_146435_, float p_146436_, float p_146437_, float p_146438_, float p_146439_) {
      return Mth.clamp(Mth.normal(p_146435_, p_146436_, p_146437_), p_146438_, p_146439_);
   }

   public float getMinValue() {
      return this.min;
   }

   public float getMaxValue() {
      return this.max;
   }

   public FloatProviderType<?> getType() {
      return FloatProviderType.CLAMPED_NORMAL;
   }

   public String toString() {
      return "normal(" + this.mean + ", " + this.deviation + ") in [" + this.min + "-" + this.max + "]";
   }
}