package net.minecraft.world.phys.shapes;

import it.unimi.dsi.fastutil.doubles.AbstractDoubleList;
import it.unimi.dsi.fastutil.doubles.DoubleList;

public class NonOverlappingMerger extends AbstractDoubleList implements IndexMerger {
   private final DoubleList lower;
   private final DoubleList upper;
   private final boolean swap;

   protected NonOverlappingMerger(DoubleList p_83012_, DoubleList p_83013_, boolean p_83014_) {
      this.lower = p_83012_;
      this.upper = p_83013_;
      this.swap = p_83014_;
   }

   public int size() {
      return this.lower.size() + this.upper.size();
   }

   public boolean forMergedIndexes(IndexMerger.IndexConsumer p_83017_) {
      return this.swap ? this.forNonSwappedIndexes((p_83020_, p_83021_, p_83022_) -> {
         return p_83017_.merge(p_83021_, p_83020_, p_83022_);
      }) : this.forNonSwappedIndexes(p_83017_);
   }

   private boolean forNonSwappedIndexes(IndexMerger.IndexConsumer p_83024_) {
      int i = this.lower.size();

      for(int j = 0; j < i; ++j) {
         if (!p_83024_.merge(j, -1, j)) {
            return false;
         }
      }

      int l = this.upper.size() - 1;

      for(int k = 0; k < l; ++k) {
         if (!p_83024_.merge(i - 1, k, i + k)) {
            return false;
         }
      }

      return true;
   }

   public double getDouble(int p_83026_) {
      return p_83026_ < this.lower.size() ? this.lower.getDouble(p_83026_) : this.upper.getDouble(p_83026_ - this.lower.size());
   }

   public DoubleList getList() {
      return this;
   }
}