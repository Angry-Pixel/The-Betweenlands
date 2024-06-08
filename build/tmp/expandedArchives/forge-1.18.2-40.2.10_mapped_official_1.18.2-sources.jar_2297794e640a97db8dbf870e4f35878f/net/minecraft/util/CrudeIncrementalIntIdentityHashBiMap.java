package net.minecraft.util;

import com.google.common.base.Predicates;
import com.google.common.collect.Iterators;
import java.util.Arrays;
import java.util.Iterator;
import javax.annotation.Nullable;
import net.minecraft.core.IdMap;

public class CrudeIncrementalIntIdentityHashBiMap<K> implements IdMap<K> {
   private static final int NOT_FOUND = -1;
   private static final Object EMPTY_SLOT = null;
   private static final float LOADFACTOR = 0.8F;
   private K[] keys;
   private int[] values;
   private K[] byId;
   private int nextId;
   private int size;

   private CrudeIncrementalIntIdentityHashBiMap(int p_13553_) {
      this.keys = (K[])(new Object[p_13553_]);
      this.values = new int[p_13553_];
      this.byId = (K[])(new Object[p_13553_]);
   }

   private CrudeIncrementalIntIdentityHashBiMap(K[] p_199841_, int[] p_199842_, K[] p_199843_, int p_199844_, int p_199845_) {
      this.keys = p_199841_;
      this.values = p_199842_;
      this.byId = p_199843_;
      this.nextId = p_199844_;
      this.size = p_199845_;
   }

   public static <A> CrudeIncrementalIntIdentityHashBiMap<A> create(int p_184238_) {
      return new CrudeIncrementalIntIdentityHashBiMap<>((int)((float)p_184238_ / 0.8F));
   }

   public int getId(@Nullable K p_13558_) {
      return this.getValue(this.indexOf(p_13558_, this.hash(p_13558_)));
   }

   @Nullable
   public K byId(int p_13556_) {
      return (K)(p_13556_ >= 0 && p_13556_ < this.byId.length ? this.byId[p_13556_] : null);
   }

   private int getValue(int p_13568_) {
      return p_13568_ == -1 ? -1 : this.values[p_13568_];
   }

   public boolean contains(K p_144610_) {
      return this.getId(p_144610_) != -1;
   }

   public boolean contains(int p_144608_) {
      return this.byId(p_144608_) != null;
   }

   public int add(K p_13570_) {
      int i = this.nextId();
      this.addMapping(p_13570_, i);
      return i;
   }

   private int nextId() {
      while(this.nextId < this.byId.length && this.byId[this.nextId] != null) {
         ++this.nextId;
      }

      return this.nextId;
   }

   private void grow(int p_13572_) {
      K[] ak = this.keys;
      int[] aint = this.values;
      CrudeIncrementalIntIdentityHashBiMap<K> crudeincrementalintidentityhashbimap = new CrudeIncrementalIntIdentityHashBiMap<>(p_13572_);

      for(int i = 0; i < ak.length; ++i) {
         if (ak[i] != null) {
            crudeincrementalintidentityhashbimap.addMapping(ak[i], aint[i]);
         }
      }

      this.keys = crudeincrementalintidentityhashbimap.keys;
      this.values = crudeincrementalintidentityhashbimap.values;
      this.byId = crudeincrementalintidentityhashbimap.byId;
      this.nextId = crudeincrementalintidentityhashbimap.nextId;
      this.size = crudeincrementalintidentityhashbimap.size;
   }

   public void addMapping(K p_13560_, int p_13561_) {
      int i = Math.max(p_13561_, this.size + 1);
      if ((float)i >= (float)this.keys.length * 0.8F) {
         int j;
         for(j = this.keys.length << 1; j < p_13561_; j <<= 1) {
         }

         this.grow(j);
      }

      int k = this.findEmpty(this.hash(p_13560_));
      this.keys[k] = p_13560_;
      this.values[k] = p_13561_;
      this.byId[p_13561_] = p_13560_;
      ++this.size;
      if (p_13561_ == this.nextId) {
         ++this.nextId;
      }

   }

   private int hash(@Nullable K p_13574_) {
      return (Mth.murmurHash3Mixer(System.identityHashCode(p_13574_)) & Integer.MAX_VALUE) % this.keys.length;
   }

   private int indexOf(@Nullable K p_13564_, int p_13565_) {
      for(int i = p_13565_; i < this.keys.length; ++i) {
         if (this.keys[i] == p_13564_) {
            return i;
         }

         if (this.keys[i] == EMPTY_SLOT) {
            return -1;
         }
      }

      for(int j = 0; j < p_13565_; ++j) {
         if (this.keys[j] == p_13564_) {
            return j;
         }

         if (this.keys[j] == EMPTY_SLOT) {
            return -1;
         }
      }

      return -1;
   }

   private int findEmpty(int p_13576_) {
      for(int i = p_13576_; i < this.keys.length; ++i) {
         if (this.keys[i] == EMPTY_SLOT) {
            return i;
         }
      }

      for(int j = 0; j < p_13576_; ++j) {
         if (this.keys[j] == EMPTY_SLOT) {
            return j;
         }
      }

      throw new RuntimeException("Overflowed :(");
   }

   public Iterator<K> iterator() {
      return Iterators.filter(Iterators.forArray(this.byId), Predicates.notNull());
   }

   public void clear() {
      Arrays.fill(this.keys, (Object)null);
      Arrays.fill(this.byId, (Object)null);
      this.nextId = 0;
      this.size = 0;
   }

   public int size() {
      return this.size;
   }

   public CrudeIncrementalIntIdentityHashBiMap<K> copy() {
      return new CrudeIncrementalIntIdentityHashBiMap<>((K[])((Object[])this.keys.clone()), (int[])this.values.clone(), (K[])((Object[])this.byId.clone()), this.nextId, this.size);
   }
}