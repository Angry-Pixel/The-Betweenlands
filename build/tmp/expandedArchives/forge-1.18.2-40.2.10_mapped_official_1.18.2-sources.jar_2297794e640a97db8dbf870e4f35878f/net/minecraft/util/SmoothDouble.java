package net.minecraft.util;

public class SmoothDouble {
   private double targetValue;
   private double remainingValue;
   private double lastAmount;

   public double getNewDeltaValue(double p_14238_, double p_14239_) {
      this.targetValue += p_14238_;
      double d0 = this.targetValue - this.remainingValue;
      double d1 = Mth.lerp(0.5D, this.lastAmount, d0);
      double d2 = Math.signum(d0);
      if (d2 * d0 > d2 * this.lastAmount) {
         d0 = d1;
      }

      this.lastAmount = d1;
      this.remainingValue += d0 * p_14239_;
      return d0 * p_14239_;
   }

   public void reset() {
      this.targetValue = 0.0D;
      this.remainingValue = 0.0D;
      this.lastAmount = 0.0D;
   }
}