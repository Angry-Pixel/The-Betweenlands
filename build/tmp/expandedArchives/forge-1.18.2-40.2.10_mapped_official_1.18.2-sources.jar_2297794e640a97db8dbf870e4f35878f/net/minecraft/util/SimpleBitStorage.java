package net.minecraft.util;

import java.util.function.IntConsumer;
import javax.annotation.Nullable;
import org.apache.commons.lang3.Validate;

public class SimpleBitStorage implements BitStorage {
   private static final int[] MAGIC = new int[]{-1, -1, 0, Integer.MIN_VALUE, 0, 0, 1431655765, 1431655765, 0, Integer.MIN_VALUE, 0, 1, 858993459, 858993459, 0, 715827882, 715827882, 0, 613566756, 613566756, 0, Integer.MIN_VALUE, 0, 2, 477218588, 477218588, 0, 429496729, 429496729, 0, 390451572, 390451572, 0, 357913941, 357913941, 0, 330382099, 330382099, 0, 306783378, 306783378, 0, 286331153, 286331153, 0, Integer.MIN_VALUE, 0, 3, 252645135, 252645135, 0, 238609294, 238609294, 0, 226050910, 226050910, 0, 214748364, 214748364, 0, 204522252, 204522252, 0, 195225786, 195225786, 0, 186737708, 186737708, 0, 178956970, 178956970, 0, 171798691, 171798691, 0, 165191049, 165191049, 0, 159072862, 159072862, 0, 153391689, 153391689, 0, 148102320, 148102320, 0, 143165576, 143165576, 0, 138547332, 138547332, 0, Integer.MIN_VALUE, 0, 4, 130150524, 130150524, 0, 126322567, 126322567, 0, 122713351, 122713351, 0, 119304647, 119304647, 0, 116080197, 116080197, 0, 113025455, 113025455, 0, 110127366, 110127366, 0, 107374182, 107374182, 0, 104755299, 104755299, 0, 102261126, 102261126, 0, 99882960, 99882960, 0, 97612893, 97612893, 0, 95443717, 95443717, 0, 93368854, 93368854, 0, 91382282, 91382282, 0, 89478485, 89478485, 0, 87652393, 87652393, 0, 85899345, 85899345, 0, 84215045, 84215045, 0, 82595524, 82595524, 0, 81037118, 81037118, 0, 79536431, 79536431, 0, 78090314, 78090314, 0, 76695844, 76695844, 0, 75350303, 75350303, 0, 74051160, 74051160, 0, 72796055, 72796055, 0, 71582788, 71582788, 0, 70409299, 70409299, 0, 69273666, 69273666, 0, 68174084, 68174084, 0, Integer.MIN_VALUE, 0, 5};
   private final long[] data;
   private final int bits;
   private final long mask;
   private final int size;
   private final int valuesPerLong;
   private final int divideMul;
   private final int divideAdd;
   private final int divideShift;

   public SimpleBitStorage(int p_198164_, int p_198165_, int[] p_198166_) {
      this(p_198164_, p_198165_);
      int j = 0;

      int i;
      for(i = 0; i <= p_198165_ - this.valuesPerLong; i += this.valuesPerLong) {
         long k = 0L;

         for(int i1 = this.valuesPerLong - 1; i1 >= 0; --i1) {
            k <<= p_198164_;
            k |= (long)p_198166_[i + i1] & this.mask;
         }

         this.data[j++] = k;
      }

      int k1 = p_198165_ - i;
      if (k1 > 0) {
         long l = 0L;

         for(int j1 = k1 - 1; j1 >= 0; --j1) {
            l <<= p_198164_;
            l |= (long)p_198166_[i + j1] & this.mask;
         }

         this.data[j] = l;
      }

   }

   public SimpleBitStorage(int p_184717_, int p_184718_) {
      this(p_184717_, p_184718_, (long[])null);
   }

   public SimpleBitStorage(int p_184724_, int p_184725_, @Nullable long[] p_184726_) {
      Validate.inclusiveBetween(1L, 32L, (long)p_184724_);
      this.size = p_184725_;
      this.bits = p_184724_;
      this.mask = (1L << p_184724_) - 1L;
      this.valuesPerLong = (char)(64 / p_184724_);
      int i = 3 * (this.valuesPerLong - 1);
      this.divideMul = MAGIC[i + 0];
      this.divideAdd = MAGIC[i + 1];
      this.divideShift = MAGIC[i + 2];
      int j = (p_184725_ + this.valuesPerLong - 1) / this.valuesPerLong;
      if (p_184726_ != null) {
         if (p_184726_.length != j) {
            throw new SimpleBitStorage.InitializationException("Invalid length given for storage, got: " + p_184726_.length + " but expected: " + j);
         }

         this.data = p_184726_;
      } else {
         this.data = new long[j];
      }

   }

   private int cellIndex(int p_184740_) {
      long i = Integer.toUnsignedLong(this.divideMul);
      long j = Integer.toUnsignedLong(this.divideAdd);
      return (int)((long)p_184740_ * i + j >> 32 >> this.divideShift);
   }

   public int getAndSet(int p_184731_, int p_184732_) {
      Validate.inclusiveBetween(0L, (long)(this.size - 1), (long)p_184731_);
      Validate.inclusiveBetween(0L, this.mask, (long)p_184732_);
      int i = this.cellIndex(p_184731_);
      long j = this.data[i];
      int k = (p_184731_ - i * this.valuesPerLong) * this.bits;
      int l = (int)(j >> k & this.mask);
      this.data[i] = j & ~(this.mask << k) | ((long)p_184732_ & this.mask) << k;
      return l;
   }

   public void set(int p_184742_, int p_184743_) {
      Validate.inclusiveBetween(0L, (long)(this.size - 1), (long)p_184742_);
      Validate.inclusiveBetween(0L, this.mask, (long)p_184743_);
      int i = this.cellIndex(p_184742_);
      long j = this.data[i];
      int k = (p_184742_ - i * this.valuesPerLong) * this.bits;
      this.data[i] = j & ~(this.mask << k) | ((long)p_184743_ & this.mask) << k;
   }

   public int get(int p_184729_) {
      Validate.inclusiveBetween(0L, (long)(this.size - 1), (long)p_184729_);
      int i = this.cellIndex(p_184729_);
      long j = this.data[i];
      int k = (p_184729_ - i * this.valuesPerLong) * this.bits;
      return (int)(j >> k & this.mask);
   }

   public long[] getRaw() {
      return this.data;
   }

   public int getSize() {
      return this.size;
   }

   public int getBits() {
      return this.bits;
   }

   public void getAll(IntConsumer p_184734_) {
      int i = 0;

      for(long j : this.data) {
         for(int k = 0; k < this.valuesPerLong; ++k) {
            p_184734_.accept((int)(j & this.mask));
            j >>= this.bits;
            ++i;
            if (i >= this.size) {
               return;
            }
         }
      }

   }

   public void unpack(int[] p_198168_) {
      int i = this.data.length;
      int j = 0;

      for(int k = 0; k < i - 1; ++k) {
         long l = this.data[k];

         for(int i1 = 0; i1 < this.valuesPerLong; ++i1) {
            p_198168_[j + i1] = (int)(l & this.mask);
            l >>= this.bits;
         }

         j += this.valuesPerLong;
      }

      int j1 = this.size - j;
      if (j1 > 0) {
         long k1 = this.data[i - 1];

         for(int l1 = 0; l1 < j1; ++l1) {
            p_198168_[j + l1] = (int)(k1 & this.mask);
            k1 >>= this.bits;
         }
      }

   }

   public BitStorage copy() {
      return new SimpleBitStorage(this.bits, this.size, (long[])this.data.clone());
   }

   public static class InitializationException extends RuntimeException {
      InitializationException(String p_184746_) {
         super(p_184746_);
      }
   }
}