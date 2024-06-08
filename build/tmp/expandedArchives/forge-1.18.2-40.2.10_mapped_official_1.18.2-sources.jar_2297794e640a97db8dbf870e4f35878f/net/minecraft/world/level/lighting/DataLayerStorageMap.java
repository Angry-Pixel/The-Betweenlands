package net.minecraft.world.level.lighting;

import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import javax.annotation.Nullable;
import net.minecraft.world.level.chunk.DataLayer;

public abstract class DataLayerStorageMap<M extends DataLayerStorageMap<M>> {
   private static final int CACHE_SIZE = 2;
   private final long[] lastSectionKeys = new long[2];
   private final DataLayer[] lastSections = new DataLayer[2];
   private boolean cacheEnabled;
   protected final Long2ObjectOpenHashMap<DataLayer> map;

   protected DataLayerStorageMap(Long2ObjectOpenHashMap<DataLayer> p_75523_) {
      this.map = p_75523_;
      this.clearCache();
      this.cacheEnabled = true;
   }

   public abstract M copy();

   public void copyDataLayer(long p_75525_) {
      this.map.put(p_75525_, this.map.get(p_75525_).copy());
      this.clearCache();
   }

   public boolean hasLayer(long p_75530_) {
      return this.map.containsKey(p_75530_);
   }

   @Nullable
   public DataLayer getLayer(long p_75533_) {
      if (this.cacheEnabled) {
         for(int i = 0; i < 2; ++i) {
            if (p_75533_ == this.lastSectionKeys[i]) {
               return this.lastSections[i];
            }
         }
      }

      DataLayer datalayer = this.map.get(p_75533_);
      if (datalayer == null) {
         return null;
      } else {
         if (this.cacheEnabled) {
            for(int j = 1; j > 0; --j) {
               this.lastSectionKeys[j] = this.lastSectionKeys[j - 1];
               this.lastSections[j] = this.lastSections[j - 1];
            }

            this.lastSectionKeys[0] = p_75533_;
            this.lastSections[0] = datalayer;
         }

         return datalayer;
      }
   }

   @Nullable
   public DataLayer removeLayer(long p_75536_) {
      return this.map.remove(p_75536_);
   }

   public void setLayer(long p_75527_, DataLayer p_75528_) {
      this.map.put(p_75527_, p_75528_);
   }

   public void clearCache() {
      for(int i = 0; i < 2; ++i) {
         this.lastSectionKeys[i] = Long.MAX_VALUE;
         this.lastSections[i] = null;
      }

   }

   public void disableCache() {
      this.cacheEnabled = false;
   }
}