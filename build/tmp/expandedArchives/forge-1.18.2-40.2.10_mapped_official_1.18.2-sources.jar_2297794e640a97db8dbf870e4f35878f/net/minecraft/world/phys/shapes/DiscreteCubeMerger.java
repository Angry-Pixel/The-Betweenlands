package net.minecraft.world.phys.shapes;

import com.google.common.math.IntMath;
import it.unimi.dsi.fastutil.doubles.DoubleList;

public final class DiscreteCubeMerger implements IndexMerger {
   private final CubePointRange result;
   private final int firstDiv;
   private final int secondDiv;

   DiscreteCubeMerger(int p_82776_, int p_82777_) {
      this.result = new CubePointRange((int)Shapes.lcm(p_82776_, p_82777_));
      int i = IntMath.gcd(p_82776_, p_82777_);
      this.firstDiv = p_82776_ / i;
      this.secondDiv = p_82777_ / i;
   }

   public boolean forMergedIndexes(IndexMerger.IndexConsumer p_82780_) {
      int i = this.result.size() - 1;

      for(int j = 0; j < i; ++j) {
         if (!p_82780_.merge(j / this.secondDiv, j / this.firstDiv, j)) {
            return false;
         }
      }

      return true;
   }

   public int size() {
      return this.result.size();
   }

   public DoubleList getList() {
      return this.result;
   }
}