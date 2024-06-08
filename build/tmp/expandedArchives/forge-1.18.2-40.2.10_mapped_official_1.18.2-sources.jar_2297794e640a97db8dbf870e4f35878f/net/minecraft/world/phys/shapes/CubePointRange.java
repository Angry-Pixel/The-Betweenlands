package net.minecraft.world.phys.shapes;

import it.unimi.dsi.fastutil.doubles.AbstractDoubleList;

public class CubePointRange extends AbstractDoubleList {
   private final int parts;

   CubePointRange(int p_82760_) {
      if (p_82760_ <= 0) {
         throw new IllegalArgumentException("Need at least 1 part");
      } else {
         this.parts = p_82760_;
      }
   }

   public double getDouble(int p_82762_) {
      return (double)p_82762_ / (double)this.parts;
   }

   public int size() {
      return this.parts + 1;
   }
}