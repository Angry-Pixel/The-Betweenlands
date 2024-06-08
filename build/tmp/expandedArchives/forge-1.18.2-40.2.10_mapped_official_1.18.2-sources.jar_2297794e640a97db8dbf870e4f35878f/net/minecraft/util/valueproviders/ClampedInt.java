package net.minecraft.util.valueproviders;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.util.Mth;

public class ClampedInt extends IntProvider {
   public static final Codec<ClampedInt> CODEC = RecordCodecBuilder.<ClampedInt>create((p_146400_) -> {
      return p_146400_.group(IntProvider.CODEC.fieldOf("source").forGetter((p_146410_) -> {
         return p_146410_.source;
      }), Codec.INT.fieldOf("min_inclusive").forGetter((p_146408_) -> {
         return p_146408_.minInclusive;
      }), Codec.INT.fieldOf("max_inclusive").forGetter((p_146405_) -> {
         return p_146405_.maxInclusive;
      })).apply(p_146400_, ClampedInt::new);
   }).comapFlatMap((p_146394_) -> {
      return p_146394_.maxInclusive < p_146394_.minInclusive ? DataResult.error("Max must be at least min, min_inclusive: " + p_146394_.minInclusive + ", max_inclusive: " + p_146394_.maxInclusive) : DataResult.success(p_146394_);
   }, Function.identity());
   private final IntProvider source;
   private int minInclusive;
   private int maxInclusive;

   public static ClampedInt of(IntProvider p_146396_, int p_146397_, int p_146398_) {
      return new ClampedInt(p_146396_, p_146397_, p_146398_);
   }

   public ClampedInt(IntProvider p_146389_, int p_146390_, int p_146391_) {
      this.source = p_146389_;
      this.minInclusive = p_146390_;
      this.maxInclusive = p_146391_;
   }

   public int sample(Random p_146402_) {
      return Mth.clamp(this.source.sample(p_146402_), this.minInclusive, this.maxInclusive);
   }

   public int getMinValue() {
      return Math.max(this.minInclusive, this.source.getMinValue());
   }

   public int getMaxValue() {
      return Math.min(this.maxInclusive, this.source.getMaxValue());
   }

   public IntProviderType<?> getType() {
      return IntProviderType.CLAMPED;
   }
}