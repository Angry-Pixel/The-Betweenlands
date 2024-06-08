package net.minecraft.world.level.levelgen.synth;

import com.google.common.annotations.VisibleForTesting;
import com.mojang.serialization.Codec;
import java.util.stream.IntStream;
import net.minecraft.util.Mth;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.NoiseSamplingSettings;
import net.minecraft.world.level.levelgen.RandomSource;
import net.minecraft.world.level.levelgen.XoroshiroRandomSource;

public class BlendedNoise implements DensityFunction.SimpleFunction {
   public static final BlendedNoise UNSEEDED = new BlendedNoise(new XoroshiroRandomSource(0L), new NoiseSamplingSettings(1.0D, 1.0D, 80.0D, 160.0D), 4, 8);
   public static final Codec<BlendedNoise> CODEC = Codec.unit(UNSEEDED);
   private final PerlinNoise minLimitNoise;
   private final PerlinNoise maxLimitNoise;
   private final PerlinNoise mainNoise;
   private final double xzScale;
   private final double yScale;
   private final double xzMainScale;
   private final double yMainScale;
   private final int cellWidth;
   private final int cellHeight;
   private final double maxValue;

   private BlendedNoise(PerlinNoise p_192811_, PerlinNoise p_192812_, PerlinNoise p_192813_, NoiseSamplingSettings p_192814_, int p_192815_, int p_192816_) {
      this.minLimitNoise = p_192811_;
      this.maxLimitNoise = p_192812_;
      this.mainNoise = p_192813_;
      this.xzScale = 684.412D * p_192814_.xzScale();
      this.yScale = 684.412D * p_192814_.yScale();
      this.xzMainScale = this.xzScale / p_192814_.xzFactor();
      this.yMainScale = this.yScale / p_192814_.yFactor();
      this.cellWidth = p_192815_;
      this.cellHeight = p_192816_;
      this.maxValue = p_192811_.maxBrokenValue(this.yScale);
   }

   public BlendedNoise(RandomSource p_192806_, NoiseSamplingSettings p_192807_, int p_192808_, int p_192809_) {
      this(PerlinNoise.createLegacyForBlendedNoise(p_192806_, IntStream.rangeClosed(-15, 0)), PerlinNoise.createLegacyForBlendedNoise(p_192806_, IntStream.rangeClosed(-15, 0)), PerlinNoise.createLegacyForBlendedNoise(p_192806_, IntStream.rangeClosed(-7, 0)), p_192807_, p_192808_, p_192809_);
   }

   public double compute(DensityFunction.FunctionContext p_210621_) {
      int i = Math.floorDiv(p_210621_.blockX(), this.cellWidth);
      int j = Math.floorDiv(p_210621_.blockY(), this.cellHeight);
      int k = Math.floorDiv(p_210621_.blockZ(), this.cellWidth);
      double d0 = 0.0D;
      double d1 = 0.0D;
      double d2 = 0.0D;
      boolean flag = true;
      double d3 = 1.0D;

      for(int l = 0; l < 8; ++l) {
         ImprovedNoise improvednoise = this.mainNoise.getOctaveNoise(l);
         if (improvednoise != null) {
            d2 += improvednoise.noise(PerlinNoise.wrap((double)i * this.xzMainScale * d3), PerlinNoise.wrap((double)j * this.yMainScale * d3), PerlinNoise.wrap((double)k * this.xzMainScale * d3), this.yMainScale * d3, (double)j * this.yMainScale * d3) / d3;
         }

         d3 /= 2.0D;
      }

      double d8 = (d2 / 10.0D + 1.0D) / 2.0D;
      boolean flag1 = d8 >= 1.0D;
      boolean flag2 = d8 <= 0.0D;
      d3 = 1.0D;

      for(int i1 = 0; i1 < 16; ++i1) {
         double d4 = PerlinNoise.wrap((double)i * this.xzScale * d3);
         double d5 = PerlinNoise.wrap((double)j * this.yScale * d3);
         double d6 = PerlinNoise.wrap((double)k * this.xzScale * d3);
         double d7 = this.yScale * d3;
         if (!flag1) {
            ImprovedNoise improvednoise1 = this.minLimitNoise.getOctaveNoise(i1);
            if (improvednoise1 != null) {
               d0 += improvednoise1.noise(d4, d5, d6, d7, (double)j * d7) / d3;
            }
         }

         if (!flag2) {
            ImprovedNoise improvednoise2 = this.maxLimitNoise.getOctaveNoise(i1);
            if (improvednoise2 != null) {
               d1 += improvednoise2.noise(d4, d5, d6, d7, (double)j * d7) / d3;
            }
         }

         d3 /= 2.0D;
      }

      return Mth.clampedLerp(d0 / 512.0D, d1 / 512.0D, d8) / 128.0D;
   }

   public double minValue() {
      return -this.maxValue();
   }

   public double maxValue() {
      return this.maxValue;
   }

   @VisibleForTesting
   public void parityConfigString(StringBuilder p_192818_) {
      p_192818_.append("BlendedNoise{minLimitNoise=");
      this.minLimitNoise.parityConfigString(p_192818_);
      p_192818_.append(", maxLimitNoise=");
      this.maxLimitNoise.parityConfigString(p_192818_);
      p_192818_.append(", mainNoise=");
      this.mainNoise.parityConfigString(p_192818_);
      p_192818_.append(String.format(", xzScale=%.3f, yScale=%.3f, xzMainScale=%.3f, yMainScale=%.3f, cellWidth=%d, cellHeight=%d", this.xzScale, this.yScale, this.xzMainScale, this.yMainScale, this.cellWidth, this.cellHeight)).append('}');
   }

   public Codec<? extends DensityFunction> codec() {
      return CODEC;
   }
}