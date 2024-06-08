package net.minecraft.world.phys.shapes;

import it.unimi.dsi.fastutil.doubles.DoubleList;

public class IdenticalMerger implements IndexMerger {
   private final DoubleList coords;

   public IdenticalMerger(DoubleList p_82903_) {
      this.coords = p_82903_;
   }

   public boolean forMergedIndexes(IndexMerger.IndexConsumer p_82906_) {
      int i = this.coords.size() - 1;

      for(int j = 0; j < i; ++j) {
         if (!p_82906_.merge(j, j, j)) {
            return false;
         }
      }

      return true;
   }

   public int size() {
      return this.coords.size();
   }

   public DoubleList getList() {
      return this.coords;
   }
}