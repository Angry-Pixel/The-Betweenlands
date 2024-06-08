package net.minecraft.world.level.lighting;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMaps;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap.Entry;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.SectionTracker;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.chunk.DataLayer;
import net.minecraft.world.level.chunk.LightChunkGetter;

public abstract class LayerLightSectionStorage<M extends DataLayerStorageMap<M>> extends SectionTracker {
   protected static final int LIGHT_AND_DATA = 0;
   protected static final int LIGHT_ONLY = 1;
   protected static final int EMPTY = 2;
   protected static final DataLayer EMPTY_DATA = new DataLayer();
   private static final Direction[] DIRECTIONS = Direction.values();
   private final LightLayer layer;
   private final LightChunkGetter chunkSource;
   protected final LongSet dataSectionSet = new LongOpenHashSet();
   protected final LongSet toMarkNoData = new LongOpenHashSet();
   protected final LongSet toMarkData = new LongOpenHashSet();
   protected volatile M visibleSectionData;
   protected final M updatingSectionData;
   protected final LongSet changedSections = new LongOpenHashSet();
   protected final LongSet sectionsAffectedByLightUpdates = new LongOpenHashSet();
   protected final Long2ObjectMap<DataLayer> queuedSections = Long2ObjectMaps.synchronize(new Long2ObjectOpenHashMap<>());
   private final LongSet untrustedSections = new LongOpenHashSet();
   private final LongSet columnsToRetainQueuedDataFor = new LongOpenHashSet();
   private final LongSet toRemove = new LongOpenHashSet();
   protected volatile boolean hasToRemove;

   protected LayerLightSectionStorage(LightLayer p_75745_, LightChunkGetter p_75746_, M p_75747_) {
      super(3, 16, 256);
      this.layer = p_75745_;
      this.chunkSource = p_75746_;
      this.updatingSectionData = p_75747_;
      this.visibleSectionData = p_75747_.copy();
      this.visibleSectionData.disableCache();
   }

   public boolean storingLightForSection(long p_75792_) {
      return this.getDataLayer(p_75792_, true) != null;
   }

   @Nullable
   public DataLayer getDataLayer(long p_75759_, boolean p_75760_) {
      return this.getDataLayer((M)(p_75760_ ? this.updatingSectionData : this.visibleSectionData), p_75759_);
   }

   @Nullable
   protected DataLayer getDataLayer(M p_75762_, long p_75763_) {
      return p_75762_.getLayer(p_75763_);
   }

   @Nullable
   public DataLayer getDataLayerData(long p_75794_) {
      DataLayer datalayer = this.queuedSections.get(p_75794_);
      return datalayer != null ? datalayer : this.getDataLayer(p_75794_, false);
   }

   protected abstract int getLightValue(long p_75786_);

   protected int getStoredLevel(long p_75796_) {
      long i = SectionPos.blockToSection(p_75796_);
      DataLayer datalayer = this.getDataLayer(i, true);
      return datalayer.get(SectionPos.sectionRelative(BlockPos.getX(p_75796_)), SectionPos.sectionRelative(BlockPos.getY(p_75796_)), SectionPos.sectionRelative(BlockPos.getZ(p_75796_)));
   }

   protected void setStoredLevel(long p_75773_, int p_75774_) {
      long i = SectionPos.blockToSection(p_75773_);
      if (this.changedSections.add(i)) {
         this.updatingSectionData.copyDataLayer(i);
      }

      DataLayer datalayer = this.getDataLayer(i, true);
      datalayer.set(SectionPos.sectionRelative(BlockPos.getX(p_75773_)), SectionPos.sectionRelative(BlockPos.getY(p_75773_)), SectionPos.sectionRelative(BlockPos.getZ(p_75773_)), p_75774_);
      SectionPos.aroundAndAtBlockPos(p_75773_, this.sectionsAffectedByLightUpdates::add);
   }

   protected int getLevel(long p_75781_) {
      if (p_75781_ == Long.MAX_VALUE) {
         return 2;
      } else if (this.dataSectionSet.contains(p_75781_)) {
         return 0;
      } else {
         return !this.toRemove.contains(p_75781_) && this.updatingSectionData.hasLayer(p_75781_) ? 1 : 2;
      }
   }

   protected int getLevelFromSource(long p_75771_) {
      if (this.toMarkNoData.contains(p_75771_)) {
         return 2;
      } else {
         return !this.dataSectionSet.contains(p_75771_) && !this.toMarkData.contains(p_75771_) ? 2 : 0;
      }
   }

   protected void setLevel(long p_75749_, int p_75750_) {
      int i = this.getLevel(p_75749_);
      if (i != 0 && p_75750_ == 0) {
         this.dataSectionSet.add(p_75749_);
         this.toMarkData.remove(p_75749_);
      }

      if (i == 0 && p_75750_ != 0) {
         this.dataSectionSet.remove(p_75749_);
         this.toMarkNoData.remove(p_75749_);
      }

      if (i >= 2 && p_75750_ != 2) {
         if (this.toRemove.contains(p_75749_)) {
            this.toRemove.remove(p_75749_);
         } else {
            this.updatingSectionData.setLayer(p_75749_, this.createDataLayer(p_75749_));
            this.changedSections.add(p_75749_);
            this.onNodeAdded(p_75749_);
            int j = SectionPos.x(p_75749_);
            int k = SectionPos.y(p_75749_);
            int l = SectionPos.z(p_75749_);

            for(int i1 = -1; i1 <= 1; ++i1) {
               for(int j1 = -1; j1 <= 1; ++j1) {
                  for(int k1 = -1; k1 <= 1; ++k1) {
                     this.sectionsAffectedByLightUpdates.add(SectionPos.asLong(j + j1, k + k1, l + i1));
                  }
               }
            }
         }
      }

      if (i != 2 && p_75750_ >= 2) {
         this.toRemove.add(p_75749_);
      }

      this.hasToRemove = !this.toRemove.isEmpty();
   }

   protected DataLayer createDataLayer(long p_75797_) {
      DataLayer datalayer = this.queuedSections.get(p_75797_);
      return datalayer != null ? datalayer : new DataLayer();
   }

   protected void clearQueuedSectionBlocks(LayerLightEngine<?, ?> p_75765_, long p_75766_) {
      if (p_75765_.getQueueSize() != 0) {
         if (p_75765_.getQueueSize() < 8192) {
            p_75765_.removeIf((p_75753_) -> {
               return SectionPos.blockToSection(p_75753_) == p_75766_;
            });
         } else {
            int i = SectionPos.sectionToBlockCoord(SectionPos.x(p_75766_));
            int j = SectionPos.sectionToBlockCoord(SectionPos.y(p_75766_));
            int k = SectionPos.sectionToBlockCoord(SectionPos.z(p_75766_));

            for(int l = 0; l < 16; ++l) {
               for(int i1 = 0; i1 < 16; ++i1) {
                  for(int j1 = 0; j1 < 16; ++j1) {
                     long k1 = BlockPos.asLong(i + l, j + i1, k + j1);
                     p_75765_.removeFromQueue(k1);
                  }
               }
            }

         }
      }
   }

   protected boolean hasInconsistencies() {
      return this.hasToRemove;
   }

   protected void markNewInconsistencies(LayerLightEngine<M, ?> p_75767_, boolean p_75768_, boolean p_75769_) {
      if (this.hasInconsistencies() || !this.queuedSections.isEmpty()) {
         for(long i : this.toRemove) {
            this.clearQueuedSectionBlocks(p_75767_, i);
            DataLayer datalayer = this.queuedSections.remove(i);
            DataLayer datalayer1 = this.updatingSectionData.removeLayer(i);
            if (this.columnsToRetainQueuedDataFor.contains(SectionPos.getZeroNode(i))) {
               if (datalayer != null) {
                  this.queuedSections.put(i, datalayer);
               } else if (datalayer1 != null) {
                  this.queuedSections.put(i, datalayer1);
               }
            }
         }

         this.updatingSectionData.clearCache();

         for(long k : this.toRemove) {
            this.onNodeRemoved(k);
         }

         this.toRemove.clear();
         this.hasToRemove = false;

         for(Entry<DataLayer> entry : this.queuedSections.long2ObjectEntrySet()) {
            long j = entry.getLongKey();
            if (this.storingLightForSection(j)) {
               DataLayer datalayer2 = entry.getValue();
               if (this.updatingSectionData.getLayer(j) != datalayer2) {
                  this.clearQueuedSectionBlocks(p_75767_, j);
                  this.updatingSectionData.setLayer(j, datalayer2);
                  this.changedSections.add(j);
               }
            }
         }

         this.updatingSectionData.clearCache();
         if (!p_75769_) {
            for(long l : this.queuedSections.keySet()) {
               this.checkEdgesForSection(p_75767_, l);
            }
         } else {
            for(long i1 : this.untrustedSections) {
               this.checkEdgesForSection(p_75767_, i1);
            }
         }

         this.untrustedSections.clear();
         ObjectIterator<Entry<DataLayer>> objectiterator = this.queuedSections.long2ObjectEntrySet().iterator();

         while(objectiterator.hasNext()) {
            Entry<DataLayer> entry1 = objectiterator.next();
            long j1 = entry1.getLongKey();
            if (this.storingLightForSection(j1)) {
               objectiterator.remove();
            }
         }

      }
   }

   private void checkEdgesForSection(LayerLightEngine<M, ?> p_75778_, long p_75779_) {
      if (this.storingLightForSection(p_75779_)) {
         int i = SectionPos.sectionToBlockCoord(SectionPos.x(p_75779_));
         int j = SectionPos.sectionToBlockCoord(SectionPos.y(p_75779_));
         int k = SectionPos.sectionToBlockCoord(SectionPos.z(p_75779_));

         for(Direction direction : DIRECTIONS) {
            long l = SectionPos.offset(p_75779_, direction);
            if (!this.queuedSections.containsKey(l) && this.storingLightForSection(l)) {
               for(int i1 = 0; i1 < 16; ++i1) {
                  for(int j1 = 0; j1 < 16; ++j1) {
                     long k1;
                     long l1;
                     switch(direction) {
                     case DOWN:
                        k1 = BlockPos.asLong(i + j1, j, k + i1);
                        l1 = BlockPos.asLong(i + j1, j - 1, k + i1);
                        break;
                     case UP:
                        k1 = BlockPos.asLong(i + j1, j + 16 - 1, k + i1);
                        l1 = BlockPos.asLong(i + j1, j + 16, k + i1);
                        break;
                     case NORTH:
                        k1 = BlockPos.asLong(i + i1, j + j1, k);
                        l1 = BlockPos.asLong(i + i1, j + j1, k - 1);
                        break;
                     case SOUTH:
                        k1 = BlockPos.asLong(i + i1, j + j1, k + 16 - 1);
                        l1 = BlockPos.asLong(i + i1, j + j1, k + 16);
                        break;
                     case WEST:
                        k1 = BlockPos.asLong(i, j + i1, k + j1);
                        l1 = BlockPos.asLong(i - 1, j + i1, k + j1);
                        break;
                     default:
                        k1 = BlockPos.asLong(i + 16 - 1, j + i1, k + j1);
                        l1 = BlockPos.asLong(i + 16, j + i1, k + j1);
                     }

                     p_75778_.checkEdge(k1, l1, p_75778_.computeLevelFromNeighbor(k1, l1, p_75778_.getLevel(k1)), false);
                     p_75778_.checkEdge(l1, k1, p_75778_.computeLevelFromNeighbor(l1, k1, p_75778_.getLevel(l1)), false);
                  }
               }
            }
         }

      }
   }

   protected void onNodeAdded(long p_75798_) {
   }

   protected void onNodeRemoved(long p_75799_) {
   }

   protected void enableLightSources(long p_75775_, boolean p_75776_) {
   }

   public void retainData(long p_75783_, boolean p_75784_) {
      if (p_75784_) {
         this.columnsToRetainQueuedDataFor.add(p_75783_);
      } else {
         this.columnsToRetainQueuedDataFor.remove(p_75783_);
      }

   }

   protected void queueSectionData(long p_75755_, @Nullable DataLayer p_75756_, boolean p_75757_) {
      if (p_75756_ != null) {
         this.queuedSections.put(p_75755_, p_75756_);
         if (!p_75757_) {
            this.untrustedSections.add(p_75755_);
         }
      } else {
         this.queuedSections.remove(p_75755_);
      }

   }

   protected void updateSectionStatus(long p_75788_, boolean p_75789_) {
      boolean flag = this.dataSectionSet.contains(p_75788_);
      if (!flag && !p_75789_) {
         this.toMarkData.add(p_75788_);
         this.checkEdge(Long.MAX_VALUE, p_75788_, 0, true);
      }

      if (flag && p_75789_) {
         this.toMarkNoData.add(p_75788_);
         this.checkEdge(Long.MAX_VALUE, p_75788_, 2, false);
      }

   }

   public void runAllUpdates() {
      if (this.hasWork()) {
         this.runUpdates(Integer.MAX_VALUE);
      }

   }

   protected void swapSectionMap() {
      if (!this.changedSections.isEmpty()) {
         M m = this.updatingSectionData.copy();
         m.disableCache();
         this.visibleSectionData = m;
         this.changedSections.clear();
      }

      if (!this.sectionsAffectedByLightUpdates.isEmpty()) {
         LongIterator longiterator = this.sectionsAffectedByLightUpdates.iterator();

         while(longiterator.hasNext()) {
            long i = longiterator.nextLong();
            this.chunkSource.onLightUpdate(this.layer, SectionPos.of(i));
         }

         this.sectionsAffectedByLightUpdates.clear();
      }

   }
}