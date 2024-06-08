package net.minecraft.world.level.levelgen.synth;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import it.unimi.dsi.fastutil.ints.IntBidirectionalIterator;
import it.unimi.dsi.fastutil.ints.IntRBTreeSet;
import it.unimi.dsi.fastutil.ints.IntSortedSet;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;
import javax.annotation.Nullable;
import net.minecraft.util.Mth;
import net.minecraft.world.level.levelgen.PositionalRandomFactory;
import net.minecraft.world.level.levelgen.RandomSource;

public class PerlinNoise {
   private static final int ROUND_OFF = 33554432;
   private final ImprovedNoise[] noiseLevels;
   private final int firstOctave;
   private final DoubleList amplitudes;
   private final double lowestFreqValueFactor;
   private final double lowestFreqInputFactor;
   private final double maxValue;

   /** @deprecated */
   @Deprecated
   public static PerlinNoise createLegacyForBlendedNoise(RandomSource p_192886_, IntStream p_192887_) {
      return new PerlinNoise(p_192886_, makeAmplitudes(new IntRBTreeSet(p_192887_.boxed().collect(ImmutableList.toImmutableList()))), false);
   }

   /** @deprecated */
   @Deprecated
   public static PerlinNoise createLegacyForLegacyNetherBiome(RandomSource p_210646_, int p_210647_, DoubleList p_210648_) {
      return new PerlinNoise(p_210646_, Pair.of(p_210647_, p_210648_), false);
   }

   public static PerlinNoise create(RandomSource p_192894_, IntStream p_192895_) {
      return create(p_192894_, p_192895_.boxed().collect(ImmutableList.toImmutableList()));
   }

   public static PerlinNoise create(RandomSource p_192883_, List<Integer> p_192884_) {
      return new PerlinNoise(p_192883_, makeAmplitudes(new IntRBTreeSet(p_192884_)), true);
   }

   public static PerlinNoise create(RandomSource p_192874_, int p_192875_, double p_192876_, double... p_192877_) {
      DoubleArrayList doublearraylist = new DoubleArrayList(p_192877_);
      doublearraylist.add(0, p_192876_);
      return new PerlinNoise(p_192874_, Pair.of(p_192875_, doublearraylist), true);
   }

   public static PerlinNoise create(RandomSource p_164382_, int p_164383_, DoubleList p_164384_) {
      return new PerlinNoise(p_164382_, Pair.of(p_164383_, p_164384_), true);
   }

   private static Pair<Integer, DoubleList> makeAmplitudes(IntSortedSet p_75431_) {
      if (p_75431_.isEmpty()) {
         throw new IllegalArgumentException("Need some octaves!");
      } else {
         int i = -p_75431_.firstInt();
         int j = p_75431_.lastInt();
         int k = i + j + 1;
         if (k < 1) {
            throw new IllegalArgumentException("Total number of octaves needs to be >= 1");
         } else {
            DoubleList doublelist = new DoubleArrayList(new double[k]);
            IntBidirectionalIterator intbidirectionaliterator = p_75431_.iterator();

            while(intbidirectionaliterator.hasNext()) {
               int l = intbidirectionaliterator.nextInt();
               doublelist.set(l + i, 1.0D);
            }

            return Pair.of(-i, doublelist);
         }
      }
   }

   protected PerlinNoise(RandomSource p_192869_, Pair<Integer, DoubleList> p_192870_, boolean p_192871_) {
      this.firstOctave = p_192870_.getFirst();
      this.amplitudes = p_192870_.getSecond();
      int i = this.amplitudes.size();
      int j = -this.firstOctave;
      this.noiseLevels = new ImprovedNoise[i];
      if (p_192871_) {
         PositionalRandomFactory positionalrandomfactory = p_192869_.forkPositional();

         for(int k = 0; k < i; ++k) {
            if (this.amplitudes.getDouble(k) != 0.0D) {
               int l = this.firstOctave + k;
               this.noiseLevels[k] = new ImprovedNoise(positionalrandomfactory.fromHashOf("octave_" + l));
            }
         }
      } else {
         ImprovedNoise improvednoise = new ImprovedNoise(p_192869_);
         if (j >= 0 && j < i) {
            double d0 = this.amplitudes.getDouble(j);
            if (d0 != 0.0D) {
               this.noiseLevels[j] = improvednoise;
            }
         }

         for(int i1 = j - 1; i1 >= 0; --i1) {
            if (i1 < i) {
               double d1 = this.amplitudes.getDouble(i1);
               if (d1 != 0.0D) {
                  this.noiseLevels[i1] = new ImprovedNoise(p_192869_);
               } else {
                  skipOctave(p_192869_);
               }
            } else {
               skipOctave(p_192869_);
            }
         }

         if (Arrays.stream(this.noiseLevels).filter(Objects::nonNull).count() != this.amplitudes.stream().filter((p_192897_) -> {
            return p_192897_ != 0.0D;
         }).count()) {
            throw new IllegalStateException("Failed to create correct number of noise levels for given non-zero amplitudes");
         }

         if (j < i - 1) {
            throw new IllegalArgumentException("Positive octaves are temporarily disabled");
         }
      }

      this.lowestFreqInputFactor = Math.pow(2.0D, (double)(-j));
      this.lowestFreqValueFactor = Math.pow(2.0D, (double)(i - 1)) / (Math.pow(2.0D, (double)i) - 1.0D);
      this.maxValue = this.edgeValue(2.0D);
   }

   protected double maxValue() {
      return this.maxValue;
   }

   private static void skipOctave(RandomSource p_164380_) {
      p_164380_.consumeCount(262);
   }

   public double getValue(double p_75409_, double p_75410_, double p_75411_) {
      return this.getValue(p_75409_, p_75410_, p_75411_, 0.0D, 0.0D, false);
   }

   /** @deprecated */
   @Deprecated
   public double getValue(double p_75418_, double p_75419_, double p_75420_, double p_75421_, double p_75422_, boolean p_75423_) {
      double d0 = 0.0D;
      double d1 = this.lowestFreqInputFactor;
      double d2 = this.lowestFreqValueFactor;

      for(int i = 0; i < this.noiseLevels.length; ++i) {
         ImprovedNoise improvednoise = this.noiseLevels[i];
         if (improvednoise != null) {
            double d3 = improvednoise.noise(wrap(p_75418_ * d1), p_75423_ ? -improvednoise.yo : wrap(p_75419_ * d1), wrap(p_75420_ * d1), p_75421_ * d1, p_75422_ * d1);
            d0 += this.amplitudes.getDouble(i) * d3 * d2;
         }

         d1 *= 2.0D;
         d2 /= 2.0D;
      }

      return d0;
   }

   public double maxBrokenValue(double p_210644_) {
      return this.edgeValue(p_210644_ + 2.0D);
   }

   private double edgeValue(double p_210650_) {
      double d0 = 0.0D;
      double d1 = this.lowestFreqValueFactor;

      for(int i = 0; i < this.noiseLevels.length; ++i) {
         ImprovedNoise improvednoise = this.noiseLevels[i];
         if (improvednoise != null) {
            d0 += this.amplitudes.getDouble(i) * p_210650_ * d1;
         }

         d1 /= 2.0D;
      }

      return d0;
   }

   @Nullable
   public ImprovedNoise getOctaveNoise(int p_75425_) {
      return this.noiseLevels[this.noiseLevels.length - 1 - p_75425_];
   }

   public static double wrap(double p_75407_) {
      return p_75407_ - (double)Mth.lfloor(p_75407_ / 3.3554432E7D + 0.5D) * 3.3554432E7D;
   }

   protected int firstOctave() {
      return this.firstOctave;
   }

   protected DoubleList amplitudes() {
      return this.amplitudes;
   }

   @VisibleForTesting
   public void parityConfigString(StringBuilder p_192891_) {
      p_192891_.append("PerlinNoise{");
      List<String> list = this.amplitudes.stream().map((p_192889_) -> {
         return String.format("%.2f", p_192889_);
      }).toList();
      p_192891_.append("first octave: ").append(this.firstOctave).append(", amplitudes: ").append((Object)list).append(", noise levels: [");

      for(int i = 0; i < this.noiseLevels.length; ++i) {
         p_192891_.append(i).append(": ");
         ImprovedNoise improvednoise = this.noiseLevels[i];
         if (improvednoise == null) {
            p_192891_.append("null");
         } else {
            improvednoise.parityConfigString(p_192891_);
         }

         p_192891_.append(", ");
      }

      p_192891_.append("]");
      p_192891_.append("}");
   }
}