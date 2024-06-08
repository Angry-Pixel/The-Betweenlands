package net.minecraft.world.level.levelgen;

public class Xoroshiro128PlusPlus {
   private long seedLo;
   private long seedHi;

   public Xoroshiro128PlusPlus(RandomSupport.Seed128bit p_190095_) {
      this(p_190095_.seedLo(), p_190095_.seedHi());
   }

   public Xoroshiro128PlusPlus(long p_190092_, long p_190093_) {
      this.seedLo = p_190092_;
      this.seedHi = p_190093_;
      if ((this.seedLo | this.seedHi) == 0L) {
         this.seedLo = -7046029254386353131L;
         this.seedHi = 7640891576956012809L;
      }

   }

   public long nextLong() {
      long i = this.seedLo;
      long j = this.seedHi;
      long k = Long.rotateLeft(i + j, 17) + i;
      j ^= i;
      this.seedLo = Long.rotateLeft(i, 49) ^ j ^ j << 21;
      this.seedHi = Long.rotateLeft(j, 28);
      return k;
   }
}