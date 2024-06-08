package net.minecraft.world.level.levelgen;

import java.util.Random;
import java.util.function.LongFunction;

public class WorldgenRandom extends Random implements RandomSource {
   private final RandomSource randomSource;
   private int count;

   public WorldgenRandom(RandomSource p_190056_) {
      super(0L);
      this.randomSource = p_190056_;
   }

   public int getCount() {
      return this.count;
   }

   public RandomSource fork() {
      return this.randomSource.fork();
   }

   public PositionalRandomFactory forkPositional() {
      return this.randomSource.forkPositional();
   }

   public int next(int p_64708_) {
      ++this.count;
      RandomSource randomsource = this.randomSource;
      if (randomsource instanceof LegacyRandomSource) {
         LegacyRandomSource legacyrandomsource = (LegacyRandomSource)randomsource;
         return legacyrandomsource.next(p_64708_);
      } else {
         return (int)(this.randomSource.nextLong() >>> 64 - p_64708_);
      }
   }

   public synchronized void setSeed(long p_190073_) {
      if (this.randomSource != null) {
         this.randomSource.setSeed(p_190073_);
      }
   }

   public long setDecorationSeed(long p_64691_, int p_64692_, int p_64693_) {
      this.setSeed(p_64691_);
      long i = this.nextLong() | 1L;
      long j = this.nextLong() | 1L;
      long k = (long)p_64692_ * i + (long)p_64693_ * j ^ p_64691_;
      this.setSeed(k);
      return k;
   }

   public void setFeatureSeed(long p_190065_, int p_190066_, int p_190067_) {
      long i = p_190065_ + (long)p_190066_ + (long)(10000 * p_190067_);
      this.setSeed(i);
   }

   public void setLargeFeatureSeed(long p_190069_, int p_190070_, int p_190071_) {
      this.setSeed(p_190069_);
      long i = this.nextLong();
      long j = this.nextLong();
      long k = (long)p_190070_ * i ^ (long)p_190071_ * j ^ p_190069_;
      this.setSeed(k);
   }

   public void setLargeFeatureWithSalt(long p_190059_, int p_190060_, int p_190061_, int p_190062_) {
      long i = (long)p_190060_ * 341873128712L + (long)p_190061_ * 132897987541L + p_190059_ + (long)p_190062_;
      this.setSeed(i);
   }

   public static Random seedSlimeChunk(int p_64686_, int p_64687_, long p_64688_, long p_64689_) {
      return new Random(p_64688_ + (long)(p_64686_ * p_64686_ * 4987142) + (long)(p_64686_ * 5947611) + (long)(p_64687_ * p_64687_) * 4392871L + (long)(p_64687_ * 389711) ^ p_64689_);
   }

   public static enum Algorithm {
      LEGACY(LegacyRandomSource::new),
      XOROSHIRO(XoroshiroRandomSource::new);

      private final LongFunction<RandomSource> constructor;

      private Algorithm(LongFunction<RandomSource> p_190082_) {
         this.constructor = p_190082_;
      }

      public RandomSource newInstance(long p_190085_) {
         return this.constructor.apply(p_190085_);
      }
   }
}