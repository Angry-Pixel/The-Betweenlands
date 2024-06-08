package net.minecraft.world.phys.shapes;

import it.unimi.dsi.fastutil.doubles.AbstractDoubleList;
import it.unimi.dsi.fastutil.doubles.DoubleList;

public class OffsetDoubleList extends AbstractDoubleList {
   private final DoubleList delegate;
   private final double offset;

   public OffsetDoubleList(DoubleList p_83031_, double p_83032_) {
      this.delegate = p_83031_;
      this.offset = p_83032_;
   }

   public double getDouble(int p_83034_) {
      return this.delegate.getDouble(p_83034_) + this.offset;
   }

   public int size() {
      return this.delegate.size();
   }
}