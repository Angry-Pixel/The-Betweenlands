package net.minecraft.util;

import it.unimi.dsi.fastutil.objects.ObjectArrays;
import java.util.AbstractSet;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;
import javax.annotation.Nullable;

public class SortedArraySet<T> extends AbstractSet<T> {
   private static final int DEFAULT_INITIAL_CAPACITY = 10;
   private final Comparator<T> comparator;
   T[] contents;
   int size;

   private SortedArraySet(int p_14244_, Comparator<T> p_14245_) {
      this.comparator = p_14245_;
      if (p_14244_ < 0) {
         throw new IllegalArgumentException("Initial capacity (" + p_14244_ + ") is negative");
      } else {
         this.contents = (T[])castRawArray(new Object[p_14244_]);
      }
   }

   public static <T extends Comparable<T>> SortedArraySet<T> create() {
      return create(10);
   }

   public static <T extends Comparable<T>> SortedArraySet<T> create(int p_14247_) {
      return new SortedArraySet<>(p_14247_, Comparator.<T>naturalOrder());
   }

   public static <T> SortedArraySet<T> create(Comparator<T> p_144977_) {
      return create(p_144977_, 10);
   }

   public static <T> SortedArraySet<T> create(Comparator<T> p_144979_, int p_144980_) {
      return new SortedArraySet<>(p_144980_, p_144979_);
   }

   private static <T> T[] castRawArray(Object[] p_14259_) {
      return (T[])p_14259_;
   }

   private int findIndex(T p_14270_) {
      return Arrays.binarySearch(this.contents, 0, this.size, p_14270_, this.comparator);
   }

   private static int getInsertionPosition(int p_14264_) {
      return -p_14264_ - 1;
   }

   public boolean add(T p_14261_) {
      int i = this.findIndex(p_14261_);
      if (i >= 0) {
         return false;
      } else {
         int j = getInsertionPosition(i);
         this.addInternal(p_14261_, j);
         return true;
      }
   }

   private void grow(int p_14268_) {
      if (p_14268_ > this.contents.length) {
         if (this.contents != ObjectArrays.DEFAULT_EMPTY_ARRAY) {
            p_14268_ = (int)Math.max(Math.min((long)this.contents.length + (long)(this.contents.length >> 1), 2147483639L), (long)p_14268_);
         } else if (p_14268_ < 10) {
            p_14268_ = 10;
         }

         Object[] aobject = new Object[p_14268_];
         System.arraycopy(this.contents, 0, aobject, 0, this.size);
         this.contents = (T[])castRawArray(aobject);
      }
   }

   private void addInternal(T p_14256_, int p_14257_) {
      this.grow(this.size + 1);
      if (p_14257_ != this.size) {
         System.arraycopy(this.contents, p_14257_, this.contents, p_14257_ + 1, this.size - p_14257_);
      }

      this.contents[p_14257_] = p_14256_;
      ++this.size;
   }

   void removeInternal(int p_14275_) {
      --this.size;
      if (p_14275_ != this.size) {
         System.arraycopy(this.contents, p_14275_ + 1, this.contents, p_14275_, this.size - p_14275_);
      }

      this.contents[this.size] = null;
   }

   private T getInternal(int p_14277_) {
      return this.contents[p_14277_];
   }

   public T addOrGet(T p_14254_) {
      int i = this.findIndex(p_14254_);
      if (i >= 0) {
         return this.getInternal(i);
      } else {
         this.addInternal(p_14254_, getInsertionPosition(i));
         return p_14254_;
      }
   }

   public boolean remove(Object p_14282_) {
      int i = this.findIndex((T)p_14282_);
      if (i >= 0) {
         this.removeInternal(i);
         return true;
      } else {
         return false;
      }
   }

   @Nullable
   public T get(T p_144982_) {
      int i = this.findIndex(p_144982_);
      return (T)(i >= 0 ? this.getInternal(i) : null);
   }

   public T first() {
      return this.getInternal(0);
   }

   public T last() {
      return this.getInternal(this.size - 1);
   }

   public boolean contains(Object p_14273_) {
      int i = this.findIndex((T)p_14273_);
      return i >= 0;
   }

   public Iterator<T> iterator() {
      return new SortedArraySet.ArrayIterator();
   }

   public int size() {
      return this.size;
   }

   public Object[] toArray() {
      return this.contents.clone();
   }

   public <U> U[] toArray(U[] p_14286_) {
      if (p_14286_.length < this.size) {
         return (U[])Arrays.copyOf(this.contents, this.size, p_14286_.getClass());
      } else {
         System.arraycopy(this.contents, 0, p_14286_, 0, this.size);
         if (p_14286_.length > this.size) {
            p_14286_[this.size] = null;
         }

         return p_14286_;
      }
   }

   public void clear() {
      Arrays.fill(this.contents, 0, this.size, (Object)null);
      this.size = 0;
   }

   public boolean equals(Object p_14279_) {
      if (this == p_14279_) {
         return true;
      } else {
         if (p_14279_ instanceof SortedArraySet) {
            SortedArraySet<?> sortedarrayset = (SortedArraySet)p_14279_;
            if (this.comparator.equals(sortedarrayset.comparator)) {
               return this.size == sortedarrayset.size && Arrays.equals(this.contents, sortedarrayset.contents);
            }
         }

         return super.equals(p_14279_);
      }
   }

   class ArrayIterator implements Iterator<T> {
      private int index;
      private int last = -1;

      public boolean hasNext() {
         return this.index < SortedArraySet.this.size;
      }

      public T next() {
         if (this.index >= SortedArraySet.this.size) {
            throw new NoSuchElementException();
         } else {
            this.last = this.index++;
            return SortedArraySet.this.contents[this.last];
         }
      }

      public void remove() {
         if (this.last == -1) {
            throw new IllegalStateException();
         } else {
            SortedArraySet.this.removeInternal(this.last);
            --this.index;
            this.last = -1;
         }
      }
   }
}