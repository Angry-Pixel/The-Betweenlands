package net.minecraft.core;

public class PositionImpl implements Position {
   protected final double x;
   protected final double y;
   protected final double z;

   public PositionImpl(double p_122802_, double p_122803_, double p_122804_) {
      this.x = p_122802_;
      this.y = p_122803_;
      this.z = p_122804_;
   }

   public double x() {
      return this.x;
   }

   public double y() {
      return this.y;
   }

   public double z() {
      return this.z;
   }
}