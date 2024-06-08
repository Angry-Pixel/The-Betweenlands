package net.minecraft.util.valueproviders;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.util.Mth;

public class ClampedNormalInt extends IntProvider {
   public static final Codec<ClampedNormalInt> CODEC = RecordCodecBuilder.<ClampedNormalInt>create((p_185887_) -> {
      return p_185887_.group(Codec.FLOAT.fieldOf("mean").forGetter((p_185905_) -> {
         return p_185905_.mean;
      }), Codec.FLOAT.fieldOf("deviation").forGetter((p_185903_) -> {
         return p_185903_.deviation;
      }), Codec.INT.fieldOf("min_inclusive").forGetter((p_185901_) -> {
         return p_185901_.min_inclusive;
      }), Codec.INT.fieldOf("max_inclusive").forGetter((p_185898_) -> {
         return p_185898_.max_inclusive;
      })).apply(p_185887_, ClampedNormalInt::new);
   }).comapFlatMap((p_185885_) -> {
      return p_185885_.max_inclusive < p_185885_.min_inclusive ? DataResult.error("Max must be larger than min: [" + p_185885_.min_inclusive + ", " + p_185885_.max_inclusive + "]") : DataResult.success(p_185885_);
   }, Function.identity());
   private float mean;
   private float deviation;
   private int min_inclusive;
   private int max_inclusive;

   public static ClampedNormalInt of(float p_185880_, float p_185881_, int p_185882_, int p_185883_) {
      return new ClampedNormalInt(p_185880_, p_185881_, p_185882_, p_185883_);
   }

   private ClampedNormalInt(float p_185874_, float p_185875_, int p_185876_, int p_185877_) {
      this.mean = p_185874_;
      this.deviation = p_185875_;
      this.min_inclusive = p_185876_;
      this.max_inclusive = p_185877_;
   }

   public int sample(Random p_185889_) {
      return sample(p_185889_, this.mean, this.deviation, (float)this.min_inclusive, (float)this.max_inclusive);
   }

   public static int sample(Random p_185891_, float p_185892_, float p_185893_, float p_185894_, float p_185895_) {
      return (int)Mth.clamp(Mth.normal(p_185891_, p_185892_, p_185893_), p_185894_, p_185895_);
   }

   public int getMinValue() {
      return this.min_inclusive;
   }

   public int getMaxValue() {
      return this.max_inclusive;
   }

   public IntProviderType<?> getType() {
      return IntProviderType.CLAMPED_NORMAL;
   }

   public String toString() {
      return "normal(" + this.mean + ", " + this.deviation + ") in [" + this.min_inclusive + "-" + this.max_inclusive + "]";
   }
}