package net.minecraft.world.level.levelgen;

public interface BitRandomSource extends RandomSource {
   float FLOAT_MULTIPLIER = 5.9604645E-8F;
   double DOUBLE_MULTIPLIER = (double)1.110223E-16F;

   int next(int p_188498_);

   default int nextInt() {
      return this.next(32);
   }

   default int nextInt(int p_188504_) {
      if (p_188504_ <= 0) {
         throw new IllegalArgumentException("Bound must be positive");
      } else if ((p_188504_ & p_188504_ - 1) == 0) {
         return (int)((long)p_188504_ * (long)this.next(31) >> 31);
      } else {
         int i;
         int j;
         do {
            i = this.next(31);
            j = i % p_188504_;
         } while(i - j + (p_188504_ - 1) < 0);

         return j;
      }
   }

   default long nextLong() {
      int i = this.next(32);
      int j = this.next(32);
      long k = (long)i << 32;
      return k + (long)j;
   }

   default boolean nextBoolean() {
      return this.next(1) != 0;
   }

   default float nextFloat() {
      return (float)this.next(24) * 5.9604645E-8F;
   }

   default double nextDouble() {
      int i = this.next(26);
      int j = this.next(27);
      long k = ((long)i << 27) + (long)j;
      return (double)k * (double)1.110223E-16F;
   }
}