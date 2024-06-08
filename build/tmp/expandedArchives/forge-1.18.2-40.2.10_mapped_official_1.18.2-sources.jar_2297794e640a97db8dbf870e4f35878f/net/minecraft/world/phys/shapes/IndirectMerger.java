package net.minecraft.world.phys.shapes;

import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import it.unimi.dsi.fastutil.doubles.DoubleLists;

public class IndirectMerger implements IndexMerger {
   private static final DoubleList EMPTY = DoubleLists.unmodifiable(DoubleArrayList.wrap(new double[]{0.0D}));
   private final double[] result;
   private final int[] firstIndices;
   private final int[] secondIndices;
   private final int resultLength;

   public IndirectMerger(DoubleList p_83001_, DoubleList p_83002_, boolean p_83003_, boolean p_83004_) {
      double d0 = Double.NaN;
      int i = p_83001_.size();
      int j = p_83002_.size();
      int k = i + j;
      this.result = new double[k];
      this.firstIndices = new int[k];
      this.secondIndices = new int[k];
      boolean flag = !p_83003_;
      boolean flag1 = !p_83004_;
      int l = 0;
      int i1 = 0;
      int j1 = 0;

      while(true) {
         boolean flag4;
         while(true) {
            boolean flag2 = i1 >= i;
            boolean flag3 = j1 >= j;
            if (flag2 && flag3) {
               this.resultLength = Math.max(1, l);
               return;
            }

            flag4 = !flag2 && (flag3 || p_83001_.getDouble(i1) < p_83002_.getDouble(j1) + 1.0E-7D);
            if (flag4) {
               ++i1;
               if (!flag || j1 != 0 && !flag3) {
                  break;
               }
            } else {
               ++j1;
               if (!flag1 || i1 != 0 && !flag2) {
                  break;
               }
            }
         }

         int k1 = i1 - 1;
         int l1 = j1 - 1;
         double d1 = flag4 ? p_83001_.getDouble(k1) : p_83002_.getDouble(l1);
         if (!(d0 >= d1 - 1.0E-7D)) {
            this.firstIndices[l] = k1;
            this.secondIndices[l] = l1;
            this.result[l] = d1;
            ++l;
            d0 = d1;
         } else {
            this.firstIndices[l - 1] = k1;
            this.secondIndices[l - 1] = l1;
         }
      }
   }

   public boolean forMergedIndexes(IndexMerger.IndexConsumer p_83007_) {
      int i = this.resultLength - 1;

      for(int j = 0; j < i; ++j) {
         if (!p_83007_.merge(this.firstIndices[j], this.secondIndices[j], j)) {
            return false;
         }
      }

      return true;
   }

   public int size() {
      return this.resultLength;
   }

   public DoubleList getList() {
      return (DoubleList)(this.resultLength <= 1 ? EMPTY : DoubleArrayList.wrap(this.result, this.resultLength));
   }
}