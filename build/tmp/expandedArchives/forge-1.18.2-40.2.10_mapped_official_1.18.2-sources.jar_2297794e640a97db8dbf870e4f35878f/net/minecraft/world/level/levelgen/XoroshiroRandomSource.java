package net.minecraft.world.level.levelgen;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Charsets;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import com.google.common.primitives.Longs;
import net.minecraft.util.Mth;

public class XoroshiroRandomSource implements RandomSource {
   private static final float FLOAT_UNIT = 5.9604645E-8F;
   private static final double DOUBLE_UNIT = (double)1.110223E-16F;
   private Xoroshiro128PlusPlus randomNumberGenerator;
   private final MarsagliaPolarGaussian gaussianSource = new MarsagliaPolarGaussian(this);

   public XoroshiroRandomSource(long p_190102_) {
      this.randomNumberGenerator = new Xoroshiro128PlusPlus(RandomSupport.upgradeSeedTo128bit(p_190102_));
   }

   public XoroshiroRandomSource(long p_190104_, long p_190105_) {
      this.randomNumberGenerator = new Xoroshiro128PlusPlus(p_190104_, p_190105_);
   }

   public RandomSource fork() {
      return new XoroshiroRandomSource(this.randomNumberGenerator.nextLong(), this.randomNumberGenerator.nextLong());
   }

   public PositionalRandomFactory forkPositional() {
      return new XoroshiroRandomSource.XoroshiroPositionalRandomFactory(this.randomNumberGenerator.nextLong(), this.randomNumberGenerator.nextLong());
   }

   public void setSeed(long p_190121_) {
      this.randomNumberGenerator = new Xoroshiro128PlusPlus(RandomSupport.upgradeSeedTo128bit(p_190121_));
      this.gaussianSource.reset();
   }

   public int nextInt() {
      return (int)this.randomNumberGenerator.nextLong();
   }

   public int nextInt(int p_190118_) {
      if (p_190118_ <= 0) {
         throw new IllegalArgumentException("Bound must be positive");
      } else {
         long i = Integer.toUnsignedLong(this.nextInt());
         long j = i * (long)p_190118_;
         long k = j & 4294967295L;
         if (k < (long)p_190118_) {
            for(int l = Integer.remainderUnsigned(~p_190118_ + 1, p_190118_); k < (long)l; k = j & 4294967295L) {
               i = Integer.toUnsignedLong(this.nextInt());
               j = i * (long)p_190118_;
            }
         }

         long i1 = j >> 32;
         return (int)i1;
      }
   }

   public long nextLong() {
      return this.randomNumberGenerator.nextLong();
   }

   public boolean nextBoolean() {
      return (this.randomNumberGenerator.nextLong() & 1L) != 0L;
   }

   public float nextFloat() {
      return (float)this.nextBits(24) * 5.9604645E-8F;
   }

   public double nextDouble() {
      return (double)this.nextBits(53) * (double)1.110223E-16F;
   }

   public double nextGaussian() {
      return this.gaussianSource.nextGaussian();
   }

   public void consumeCount(int p_190111_) {
      for(int i = 0; i < p_190111_; ++i) {
         this.randomNumberGenerator.nextLong();
      }

   }

   private long nextBits(int p_190108_) {
      return this.randomNumberGenerator.nextLong() >>> 64 - p_190108_;
   }

   public static class XoroshiroPositionalRandomFactory implements PositionalRandomFactory {
      private static final HashFunction MD5_128 = Hashing.md5();
      private final long seedLo;
      private final long seedHi;

      public XoroshiroPositionalRandomFactory(long p_190127_, long p_190128_) {
         this.seedLo = p_190127_;
         this.seedHi = p_190128_;
      }

      public RandomSource at(int p_190130_, int p_190131_, int p_190132_) {
         long i = Mth.getSeed(p_190130_, p_190131_, p_190132_);
         long j = i ^ this.seedLo;
         return new XoroshiroRandomSource(j, this.seedHi);
      }

      public RandomSource fromHashOf(String p_190134_) {
         byte[] abyte = MD5_128.hashString(p_190134_, Charsets.UTF_8).asBytes();
         long i = Longs.fromBytes(abyte[0], abyte[1], abyte[2], abyte[3], abyte[4], abyte[5], abyte[6], abyte[7]);
         long j = Longs.fromBytes(abyte[8], abyte[9], abyte[10], abyte[11], abyte[12], abyte[13], abyte[14], abyte[15]);
         return new XoroshiroRandomSource(i ^ this.seedLo, j ^ this.seedHi);
      }

      @VisibleForTesting
      public void parityConfigString(StringBuilder p_190136_) {
         p_190136_.append("seedLo: ").append(this.seedLo).append(", seedHi: ").append(this.seedHi);
      }
   }
}