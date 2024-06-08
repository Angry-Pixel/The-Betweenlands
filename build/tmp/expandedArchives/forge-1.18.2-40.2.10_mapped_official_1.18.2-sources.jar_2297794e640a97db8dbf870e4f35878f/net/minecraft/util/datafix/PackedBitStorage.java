package net.minecraft.util.datafix;

import net.minecraft.util.Mth;
import org.apache.commons.lang3.Validate;

public class PackedBitStorage {
   private static final int BIT_TO_LONG_SHIFT = 6;
   private final long[] data;
   private final int bits;
   private final long mask;
   private final int size;

   public PackedBitStorage(int p_14555_, int p_14556_) {
      this(p_14555_, p_14556_, new long[Mth.roundToward(p_14556_ * p_14555_, 64) / 64]);
   }

   public PackedBitStorage(int p_14558_, int p_14559_, long[] p_14560_) {
      Validate.inclusiveBetween(1L, 32L, (long)p_14558_);
      this.size = p_14559_;
      this.bits = p_14558_;
      this.data = p_14560_;
      this.mask = (1L << p_14558_) - 1L;
      int i = Mth.roundToward(p_14559_ * p_14558_, 64) / 64;
      if (p_14560_.length != i) {
         throw new IllegalArgumentException("Invalid length given for storage, got: " + p_14560_.length + " but expected: " + i);
      }
   }

   public void set(int p_14565_, int p_14566_) {
      Validate.inclusiveBetween(0L, (long)(this.size - 1), (long)p_14565_);
      Validate.inclusiveBetween(0L, this.mask, (long)p_14566_);
      int i = p_14565_ * this.bits;
      int j = i >> 6;
      int k = (p_14565_ + 1) * this.bits - 1 >> 6;
      int l = i ^ j << 6;
      this.data[j] = this.data[j] & ~(this.mask << l) | ((long)p_14566_ & this.mask) << l;
      if (j != k) {
         int i1 = 64 - l;
         int j1 = this.bits - i1;
         this.data[k] = this.data[k] >>> j1 << j1 | ((long)p_14566_ & this.mask) >> i1;
      }

   }

   public int get(int p_14563_) {
      Validate.inclusiveBetween(0L, (long)(this.size - 1), (long)p_14563_);
      int i = p_14563_ * this.bits;
      int j = i >> 6;
      int k = (p_14563_ + 1) * this.bits - 1 >> 6;
      int l = i ^ j << 6;
      if (j == k) {
         return (int)(this.data[j] >>> l & this.mask);
      } else {
         int i1 = 64 - l;
         return (int)((this.data[j] >>> l | this.data[k] << i1) & this.mask);
      }
   }

   public long[] getRaw() {
      return this.data;
   }

   public int getBits() {
      return this.bits;
   }
}