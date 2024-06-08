package net.minecraft.world.level.levelgen;

public interface RandomSource {
   RandomSource fork();

   PositionalRandomFactory forkPositional();

   void setSeed(long p_158879_);

   int nextInt();

   int nextInt(int p_158878_);

   default int nextIntBetweenInclusive(int p_189321_, int p_189322_) {
      return this.nextInt(p_189322_ - p_189321_ + 1) + p_189321_;
   }

   long nextLong();

   boolean nextBoolean();

   float nextFloat();

   double nextDouble();

   double nextGaussian();

   default void consumeCount(int p_158877_) {
      for(int i = 0; i < p_158877_; ++i) {
         this.nextInt();
      }

   }
}