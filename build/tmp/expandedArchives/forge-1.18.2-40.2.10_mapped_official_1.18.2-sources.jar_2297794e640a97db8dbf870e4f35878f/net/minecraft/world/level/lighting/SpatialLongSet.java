package net.minecraft.world.level.lighting;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.longs.Long2LongLinkedOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongLinkedOpenHashSet;
import java.util.NoSuchElementException;
import net.minecraft.util.Mth;

public class SpatialLongSet extends LongLinkedOpenHashSet {
   private final SpatialLongSet.InternalMap map;

   public SpatialLongSet(int p_164462_, float p_164463_) {
      super(p_164462_, p_164463_);
      this.map = new SpatialLongSet.InternalMap(p_164462_ / 64, p_164463_);
   }

   public boolean add(long p_164465_) {
      return this.map.addBit(p_164465_);
   }

   public boolean rem(long p_164468_) {
      return this.map.removeBit(p_164468_);
   }

   public long removeFirstLong() {
      return this.map.removeFirstBit();
   }

   public int size() {
      throw new UnsupportedOperationException();
   }

   public boolean isEmpty() {
      return this.map.isEmpty();
   }

   protected static class InternalMap extends Long2LongLinkedOpenHashMap {
      private static final int X_BITS = Mth.log2(60000000);
      private static final int Z_BITS = Mth.log2(60000000);
      private static final int Y_BITS = 64 - X_BITS - Z_BITS;
      private static final int Y_OFFSET = 0;
      private static final int Z_OFFSET = Y_BITS;
      private static final int X_OFFSET = Y_BITS + Z_BITS;
      private static final long OUTER_MASK = 3L << X_OFFSET | 3L | 3L << Z_OFFSET;
      private int lastPos = -1;
      private long lastOuterKey;
      private final int minSize;

      public InternalMap(int p_164483_, float p_164484_) {
         super(p_164483_, p_164484_);
         this.minSize = p_164483_;
      }

      static long getOuterKey(long p_164490_) {
         return p_164490_ & ~OUTER_MASK;
      }

      static int getInnerKey(long p_164498_) {
         int i = (int)(p_164498_ >>> X_OFFSET & 3L);
         int j = (int)(p_164498_ >>> 0 & 3L);
         int k = (int)(p_164498_ >>> Z_OFFSET & 3L);
         return i << 4 | k << 2 | j;
      }

      static long getFullKey(long p_164492_, int p_164493_) {
         p_164492_ |= (long)(p_164493_ >>> 4 & 3) << X_OFFSET;
         p_164492_ |= (long)(p_164493_ >>> 2 & 3) << Z_OFFSET;
         return p_164492_ | (long)(p_164493_ >>> 0 & 3) << 0;
      }

      public boolean addBit(long p_164500_) {
         long i = getOuterKey(p_164500_);
         int j = getInnerKey(p_164500_);
         long k = 1L << j;
         int l;
         if (i == 0L) {
            if (this.containsNullKey) {
               return this.replaceBit(this.n, k);
            }

            this.containsNullKey = true;
            l = this.n;
         } else {
            if (this.lastPos != -1 && i == this.lastOuterKey) {
               return this.replaceBit(this.lastPos, k);
            }

            long[] along = this.key;
            l = (int)HashCommon.mix(i) & this.mask;

            for(long i1 = along[l]; i1 != 0L; i1 = along[l]) {
               if (i1 == i) {
                  this.lastPos = l;
                  this.lastOuterKey = i;
                  return this.replaceBit(l, k);
               }

               l = l + 1 & this.mask;
            }
         }

         this.key[l] = i;
         this.value[l] = k;
         if (this.size == 0) {
            this.first = this.last = l;
            this.link[l] = -1L;
         } else {
            this.link[this.last] ^= (this.link[this.last] ^ (long)l & 4294967295L) & 4294967295L;
            this.link[l] = ((long)this.last & 4294967295L) << 32 | 4294967295L;
            this.last = l;
         }

         if (this.size++ >= this.maxFill) {
            this.rehash(HashCommon.arraySize(this.size + 1, this.f));
         }

         return false;
      }

      private boolean replaceBit(int p_164487_, long p_164488_) {
         boolean flag = (this.value[p_164487_] & p_164488_) != 0L;
         this.value[p_164487_] |= p_164488_;
         return flag;
      }

      public boolean removeBit(long p_164502_) {
         long i = getOuterKey(p_164502_);
         int j = getInnerKey(p_164502_);
         long k = 1L << j;
         if (i == 0L) {
            return this.containsNullKey ? this.removeFromNullEntry(k) : false;
         } else if (this.lastPos != -1 && i == this.lastOuterKey) {
            return this.removeFromEntry(this.lastPos, k);
         } else {
            long[] along = this.key;
            int l = (int)HashCommon.mix(i) & this.mask;

            for(long i1 = along[l]; i1 != 0L; i1 = along[l]) {
               if (i == i1) {
                  this.lastPos = l;
                  this.lastOuterKey = i;
                  return this.removeFromEntry(l, k);
               }

               l = l + 1 & this.mask;
            }

            return false;
         }
      }

      private boolean removeFromNullEntry(long p_164504_) {
         if ((this.value[this.n] & p_164504_) == 0L) {
            return false;
         } else {
            this.value[this.n] &= ~p_164504_;
            if (this.value[this.n] != 0L) {
               return true;
            } else {
               this.containsNullKey = false;
               --this.size;
               this.fixPointers(this.n);
               if (this.size < this.maxFill / 4 && this.n > 16) {
                  this.rehash(this.n / 2);
               }

               return true;
            }
         }
      }

      private boolean removeFromEntry(int p_164495_, long p_164496_) {
         if ((this.value[p_164495_] & p_164496_) == 0L) {
            return false;
         } else {
            this.value[p_164495_] &= ~p_164496_;
            if (this.value[p_164495_] != 0L) {
               return true;
            } else {
               this.lastPos = -1;
               --this.size;
               this.fixPointers(p_164495_);
               this.shiftKeys(p_164495_);
               if (this.size < this.maxFill / 4 && this.n > 16) {
                  this.rehash(this.n / 2);
               }

               return true;
            }
         }
      }

      public long removeFirstBit() {
         if (this.size == 0) {
            throw new NoSuchElementException();
         } else {
            int i = this.first;
            long j = this.key[i];
            int k = Long.numberOfTrailingZeros(this.value[i]);
            this.value[i] &= ~(1L << k);
            if (this.value[i] == 0L) {
               this.removeFirstLong();
               this.lastPos = -1;
            }

            return getFullKey(j, k);
         }
      }

      protected void rehash(int p_164506_) {
         if (p_164506_ > this.minSize) {
            super.rehash(p_164506_);
         }

      }
   }
}