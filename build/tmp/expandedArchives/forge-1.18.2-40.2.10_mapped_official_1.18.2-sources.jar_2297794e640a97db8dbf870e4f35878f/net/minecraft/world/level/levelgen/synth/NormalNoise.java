package net.minecraft.world.level.levelgen.synth;

import com.google.common.annotations.VisibleForTesting;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import it.unimi.dsi.fastutil.doubles.DoubleListIterator;
import java.util.List;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.world.level.levelgen.RandomSource;

public class NormalNoise {
   private static final double INPUT_FACTOR = 1.0181268882175227D;
   private static final double TARGET_DEVIATION = 0.3333333333333333D;
   private final double valueFactor;
   private final PerlinNoise first;
   private final PerlinNoise second;
   private final double maxValue;
   private final NormalNoise.NoiseParameters parameters;

   /** @deprecated */
   @Deprecated
   public static NormalNoise createLegacyNetherBiome(RandomSource p_192844_, NormalNoise.NoiseParameters p_192845_) {
      return new NormalNoise(p_192844_, p_192845_, false);
   }

   public static NormalNoise create(RandomSource p_164355_, int p_164356_, double... p_164357_) {
      return create(p_164355_, new NormalNoise.NoiseParameters(p_164356_, new DoubleArrayList(p_164357_)));
   }

   public static NormalNoise create(RandomSource p_192849_, NormalNoise.NoiseParameters p_192850_) {
      return new NormalNoise(p_192849_, p_192850_, true);
   }

   private NormalNoise(RandomSource p_210627_, NormalNoise.NoiseParameters p_210628_, boolean p_210629_) {
      int i = p_210628_.firstOctave;
      DoubleList doublelist = p_210628_.amplitudes;
      this.parameters = p_210628_;
      if (p_210629_) {
         this.first = PerlinNoise.create(p_210627_, i, doublelist);
         this.second = PerlinNoise.create(p_210627_, i, doublelist);
      } else {
         this.first = PerlinNoise.createLegacyForLegacyNetherBiome(p_210627_, i, doublelist);
         this.second = PerlinNoise.createLegacyForLegacyNetherBiome(p_210627_, i, doublelist);
      }

      int j = Integer.MAX_VALUE;
      int k = Integer.MIN_VALUE;
      DoubleListIterator doublelistiterator = doublelist.iterator();

      while(doublelistiterator.hasNext()) {
         int l = doublelistiterator.nextIndex();
         double d0 = doublelistiterator.nextDouble();
         if (d0 != 0.0D) {
            j = Math.min(j, l);
            k = Math.max(k, l);
         }
      }

      this.valueFactor = 0.16666666666666666D / expectedDeviation(k - j);
      this.maxValue = (this.first.maxValue() + this.second.maxValue()) * this.valueFactor;
   }

   public double maxValue() {
      return this.maxValue;
   }

   private static double expectedDeviation(int p_75385_) {
      return 0.1D * (1.0D + 1.0D / (double)(p_75385_ + 1));
   }

   public double getValue(double p_75381_, double p_75382_, double p_75383_) {
      double d0 = p_75381_ * 1.0181268882175227D;
      double d1 = p_75382_ * 1.0181268882175227D;
      double d2 = p_75383_ * 1.0181268882175227D;
      return (this.first.getValue(p_75381_, p_75382_, p_75383_) + this.second.getValue(d0, d1, d2)) * this.valueFactor;
   }

   public NormalNoise.NoiseParameters parameters() {
      return this.parameters;
   }

   @VisibleForTesting
   public void parityConfigString(StringBuilder p_192847_) {
      p_192847_.append("NormalNoise {");
      p_192847_.append("first: ");
      this.first.parityConfigString(p_192847_);
      p_192847_.append(", second: ");
      this.second.parityConfigString(p_192847_);
      p_192847_.append("}");
   }

   public static record NoiseParameters(int firstOctave, DoubleList amplitudes) {
      public static final Codec<NormalNoise.NoiseParameters> DIRECT_CODEC = RecordCodecBuilder.create((p_192865_) -> {
         return p_192865_.group(Codec.INT.fieldOf("firstOctave").forGetter(NormalNoise.NoiseParameters::firstOctave), Codec.DOUBLE.listOf().fieldOf("amplitudes").forGetter(NormalNoise.NoiseParameters::amplitudes)).apply(p_192865_, NormalNoise.NoiseParameters::new);
      });
      public static final Codec<Holder<NormalNoise.NoiseParameters>> CODEC = RegistryFileCodec.create(Registry.NOISE_REGISTRY, DIRECT_CODEC);

      public NoiseParameters(int p_192861_, List<Double> p_192862_) {
         this(p_192861_, new DoubleArrayList(p_192862_));
      }

      public NoiseParameters(int p_192857_, double p_192858_, double... p_192859_) {
         this(p_192857_, Util.make(new DoubleArrayList(p_192859_), (p_210636_) -> {
            p_210636_.add(0, p_192858_);
         }));
      }
   }
}