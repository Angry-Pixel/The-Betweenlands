package net.minecraft.world.level.chunk.storage;

import com.google.common.annotations.VisibleForTesting;
import it.unimi.dsi.fastutil.ints.IntArraySet;
import it.unimi.dsi.fastutil.ints.IntCollection;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.util.BitSet;

public class RegionBitmap {
   private final BitSet used = new BitSet();

   public void force(int p_63613_, int p_63614_) {
      this.used.set(p_63613_, p_63613_ + p_63614_);
   }

   public void free(int p_63616_, int p_63617_) {
      this.used.clear(p_63616_, p_63616_ + p_63617_);
   }

   public int allocate(int p_63611_) {
      int i = 0;

      while(true) {
         int j = this.used.nextClearBit(i);
         int k = this.used.nextSetBit(j);
         if (k == -1 || k - j >= p_63611_) {
            this.force(j, p_63611_);
            return j;
         }

         i = k;
      }
   }

   @VisibleForTesting
   public IntSet getUsed() {
      return this.used.stream().collect(IntArraySet::new, IntCollection::add, IntCollection::addAll);
   }
}