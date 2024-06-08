package net.minecraft.util.valueproviders;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Random;
import java.util.function.Function;

public class BiasedToBottomInt extends IntProvider {
   public static final Codec<BiasedToBottomInt> CODEC = RecordCodecBuilder.<BiasedToBottomInt>create((p_146373_) -> {
      return p_146373_.group(Codec.INT.fieldOf("min_inclusive").forGetter((p_146381_) -> {
         return p_146381_.minInclusive;
      }), Codec.INT.fieldOf("max_inclusive").forGetter((p_146378_) -> {
         return p_146378_.maxInclusive;
      })).apply(p_146373_, BiasedToBottomInt::new);
   }).comapFlatMap((p_146371_) -> {
      return p_146371_.maxInclusive < p_146371_.minInclusive ? DataResult.error("Max must be at least min, min_inclusive: " + p_146371_.minInclusive + ", max_inclusive: " + p_146371_.maxInclusive) : DataResult.success(p_146371_);
   }, Function.identity());
   private final int minInclusive;
   private final int maxInclusive;

   private BiasedToBottomInt(int p_146364_, int p_146365_) {
      this.minInclusive = p_146364_;
      this.maxInclusive = p_146365_;
   }

   public static BiasedToBottomInt of(int p_146368_, int p_146369_) {
      return new BiasedToBottomInt(p_146368_, p_146369_);
   }

   public int sample(Random p_146375_) {
      return this.minInclusive + p_146375_.nextInt(p_146375_.nextInt(this.maxInclusive - this.minInclusive + 1) + 1);
   }

   public int getMinValue() {
      return this.minInclusive;
   }

   public int getMaxValue() {
      return this.maxInclusive;
   }

   public IntProviderType<?> getType() {
      return IntProviderType.BIASED_TO_BOTTOM;
   }

   public String toString() {
      return "[" + this.minInclusive + "-" + this.maxInclusive + "]";
   }
}