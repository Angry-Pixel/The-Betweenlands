package net.minecraft.client.renderer;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RunningTrimmedMean {
   private final long[] values;
   private int count;
   private int cursor;

   public RunningTrimmedMean(int p_110711_) {
      this.values = new long[p_110711_];
   }

   public long registerValueAndGetMean(long p_110713_) {
      if (this.count < this.values.length) {
         ++this.count;
      }

      this.values[this.cursor] = p_110713_;
      this.cursor = (this.cursor + 1) % this.values.length;
      long i = Long.MAX_VALUE;
      long j = Long.MIN_VALUE;
      long k = 0L;

      for(int l = 0; l < this.count; ++l) {
         long i1 = this.values[l];
         k += i1;
         i = Math.min(i, i1);
         j = Math.max(j, i1);
      }

      if (this.count > 2) {
         k -= i + j;
         return k / (long)(this.count - 2);
      } else {
         return k > 0L ? (long)this.count / k : 0L;
      }
   }
}