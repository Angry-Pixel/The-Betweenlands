package net.minecraft.world.level.levelgen;

import net.minecraft.util.Mth;

public class MarsagliaPolarGaussian {
   public final RandomSource randomSource;
   private double nextNextGaussian;
   private boolean haveNextNextGaussian;

   public MarsagliaPolarGaussian(RandomSource p_188601_) {
      this.randomSource = p_188601_;
   }

   public void reset() {
      this.haveNextNextGaussian = false;
   }

   public double nextGaussian() {
      if (this.haveNextNextGaussian) {
         this.haveNextNextGaussian = false;
         return this.nextNextGaussian;
      } else {
         while(true) {
            double d0 = 2.0D * this.randomSource.nextDouble() - 1.0D;
            double d1 = 2.0D * this.randomSource.nextDouble() - 1.0D;
            double d2 = Mth.square(d0) + Mth.square(d1);
            if (!(d2 >= 1.0D)) {
               if (d2 != 0.0D) {
                  double d3 = Math.sqrt(-2.0D * Math.log(d2) / d2);
                  this.nextNextGaussian = d1 * d3;
                  this.haveNextNextGaussian = true;
                  return d0 * d3;
               }
            }
         }
      }
   }
}