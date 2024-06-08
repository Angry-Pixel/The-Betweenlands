package net.minecraft.world.level.levelgen;

public class SingleThreadedRandomSource implements BitRandomSource {
   private static final int MODULUS_BITS = 48;
   private static final long MODULUS_MASK = 281474976710655L;
   private static final long MULTIPLIER = 25214903917L;
   private static final long INCREMENT = 11L;
   private long seed;
   private final MarsagliaPolarGaussian gaussianSource = new MarsagliaPolarGaussian(this);

   public SingleThreadedRandomSource(long p_189353_) {
      this.setSeed(p_189353_);
   }

   public RandomSource fork() {
      return new SingleThreadedRandomSource(this.nextLong());
   }

   public PositionalRandomFactory forkPositional() {
      return new LegacyRandomSource.LegacyPositionalRandomFactory(this.nextLong());
   }

   public void setSeed(long p_189360_) {
      this.seed = (p_189360_ ^ 25214903917L) & 281474976710655L;
      this.gaussianSource.reset();
   }

   public int next(int p_189356_) {
      long i = this.seed * 25214903917L + 11L & 281474976710655L;
      this.seed = i;
      return (int)(i >> 48 - p_189356_);
   }

   public double nextGaussian() {
      return this.gaussianSource.nextGaussian();
   }
}