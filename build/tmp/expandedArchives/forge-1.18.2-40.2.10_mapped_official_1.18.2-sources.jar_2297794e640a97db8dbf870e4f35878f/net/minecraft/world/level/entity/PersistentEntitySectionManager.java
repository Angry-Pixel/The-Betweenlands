package net.minecraft.world.level.entity;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMaps;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap.Entry;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.util.CsvOutput;
import net.minecraft.util.VisibleForDebug;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import org.slf4j.Logger;

public class PersistentEntitySectionManager<T extends EntityAccess> implements AutoCloseable {
   static final Logger LOGGER = LogUtils.getLogger();
   final Set<UUID> knownUuids = Sets.newHashSet();
   final LevelCallback<T> callbacks;
   private final EntityPersistentStorage<T> permanentStorage;
   private final EntityLookup<T> visibleEntityStorage;
   final EntitySectionStorage<T> sectionStorage;
   private final LevelEntityGetter<T> entityGetter;
   private final Long2ObjectMap<Visibility> chunkVisibility = new Long2ObjectOpenHashMap<>();
   private final Long2ObjectMap<PersistentEntitySectionManager.ChunkLoadStatus> chunkLoadStatuses = new Long2ObjectOpenHashMap<>();
   private final LongSet chunksToUnload = new LongOpenHashSet();
   private final Queue<ChunkEntities<T>> loadingInbox = Queues.newConcurrentLinkedQueue();

   public PersistentEntitySectionManager(Class<T> p_157503_, LevelCallback<T> p_157504_, EntityPersistentStorage<T> p_157505_) {
      this.visibleEntityStorage = new EntityLookup<>();
      this.sectionStorage = new EntitySectionStorage<>(p_157503_, this.chunkVisibility);
      this.chunkVisibility.defaultReturnValue(Visibility.HIDDEN);
      this.chunkLoadStatuses.defaultReturnValue(PersistentEntitySectionManager.ChunkLoadStatus.FRESH);
      this.callbacks = p_157504_;
      this.permanentStorage = p_157505_;
      this.entityGetter = new LevelEntityGetterAdapter<>(this.visibleEntityStorage, this.sectionStorage);
   }

   void removeSectionIfEmpty(long p_157510_, EntitySection<T> p_157511_) {
      if (p_157511_.isEmpty()) {
         this.sectionStorage.remove(p_157510_);
      }

   }

   private boolean addEntityUuid(T p_157558_) {
      if (!this.knownUuids.add(p_157558_.getUUID())) {
         LOGGER.warn("UUID of added entity already exists: {}", (Object)p_157558_);
         return false;
      } else {
         return true;
      }
   }

   public boolean addNewEntity(T p_157534_) {
      return this.addEntity(p_157534_, false);
   }

   public boolean addNewEntityWithoutEvent(T entity) {
      return this.addEntityWithoutEvent(entity, false);
   }

   private boolean addEntity(T p_157539_, boolean p_157540_) {
      if (p_157539_ instanceof Entity entity && net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.entity.EntityJoinWorldEvent(entity, entity.level, p_157540_))) return false;
      return addEntityWithoutEvent(p_157539_, p_157540_);
   }

   private boolean addEntityWithoutEvent(T p_157539_, boolean p_157540_) {
      if (!this.addEntityUuid(p_157539_)) {
         return false;
      } else {
         long i = SectionPos.asLong(p_157539_.blockPosition());
         EntitySection<T> entitysection = this.sectionStorage.getOrCreateSection(i);
         entitysection.add(p_157539_);
         p_157539_.setLevelCallback(new PersistentEntitySectionManager.Callback(p_157539_, i, entitysection));
         if (!p_157540_) {
            this.callbacks.onCreated(p_157539_);
         }

         Visibility visibility = getEffectiveStatus(p_157539_, entitysection.getStatus());
         if (visibility.isAccessible()) {
            this.startTracking(p_157539_);
         }

         if (visibility.isTicking()) {
            this.startTicking(p_157539_);
         }

         return true;
      }
   }

   static <T extends EntityAccess> Visibility getEffectiveStatus(T p_157536_, Visibility p_157537_) {
      return p_157536_.isAlwaysTicking() ? Visibility.TICKING : p_157537_;
   }

   public void addLegacyChunkEntities(Stream<T> p_157553_) {
      p_157553_.forEach((p_157607_) -> {
         this.addEntity(p_157607_, true);
         if (p_157607_ instanceof Entity entity) entity.onAddedToWorld();
      });
   }

   public void addWorldGenChunkEntities(Stream<T> p_157560_) {
      p_157560_.forEach((p_157605_) -> {
         this.addEntity(p_157605_, false);
         if (p_157605_ instanceof Entity entity) entity.onAddedToWorld();
      });
   }

   void startTicking(T p_157565_) {
      this.callbacks.onTickingStart(p_157565_);
   }

   void stopTicking(T p_157571_) {
      this.callbacks.onTickingEnd(p_157571_);
   }

   void startTracking(T p_157576_) {
      this.visibleEntityStorage.add(p_157576_);
      this.callbacks.onTrackingStart(p_157576_);
   }

   void stopTracking(T p_157581_) {
      this.callbacks.onTrackingEnd(p_157581_);
      this.visibleEntityStorage.remove(p_157581_);
   }

   public void updateChunkStatus(ChunkPos p_157525_, ChunkHolder.FullChunkStatus p_157526_) {
      Visibility visibility = Visibility.fromFullChunkStatus(p_157526_);
      this.updateChunkStatus(p_157525_, visibility);
   }

   public void updateChunkStatus(ChunkPos p_157528_, Visibility p_157529_) {
      long i = p_157528_.toLong();
      if (p_157529_ == Visibility.HIDDEN) {
         this.chunkVisibility.remove(i);
         this.chunksToUnload.add(i);
      } else {
         this.chunkVisibility.put(i, p_157529_);
         this.chunksToUnload.remove(i);
         this.ensureChunkQueuedForLoad(i);
      }

      this.sectionStorage.getExistingSectionsInChunk(i).forEach((p_157545_) -> {
         Visibility visibility = p_157545_.updateChunkStatus(p_157529_);
         boolean flag = visibility.isAccessible();
         boolean flag1 = p_157529_.isAccessible();
         boolean flag2 = visibility.isTicking();
         boolean flag3 = p_157529_.isTicking();
         if (flag2 && !flag3) {
            p_157545_.getEntities().filter((p_157603_) -> {
               return !p_157603_.isAlwaysTicking();
            }).forEach(this::stopTicking);
         }

         if (flag && !flag1) {
            p_157545_.getEntities().filter((p_157601_) -> {
               return !p_157601_.isAlwaysTicking();
            }).forEach(this::stopTracking);
         } else if (!flag && flag1) {
            p_157545_.getEntities().filter((p_157599_) -> {
               return !p_157599_.isAlwaysTicking();
            }).forEach(this::startTracking);
         }

         if (!flag2 && flag3) {
            p_157545_.getEntities().filter((p_157597_) -> {
               return !p_157597_.isAlwaysTicking();
            }).forEach(this::startTicking);
         }

      });
   }

   private void ensureChunkQueuedForLoad(long p_157556_) {
      PersistentEntitySectionManager.ChunkLoadStatus persistententitysectionmanager$chunkloadstatus = this.chunkLoadStatuses.get(p_157556_);
      if (persistententitysectionmanager$chunkloadstatus == PersistentEntitySectionManager.ChunkLoadStatus.FRESH) {
         this.requestChunkLoad(p_157556_);
      }

   }

   private boolean storeChunkSections(long p_157513_, Consumer<T> p_157514_) {
      PersistentEntitySectionManager.ChunkLoadStatus persistententitysectionmanager$chunkloadstatus = this.chunkLoadStatuses.get(p_157513_);
      if (persistententitysectionmanager$chunkloadstatus == PersistentEntitySectionManager.ChunkLoadStatus.PENDING) {
         return false;
      } else {
         List<T> list = this.sectionStorage.getExistingSectionsInChunk(p_157513_).flatMap((p_157542_) -> {
            return p_157542_.getEntities().filter(EntityAccess::shouldBeSaved);
         }).collect(Collectors.toList());
         if (list.isEmpty()) {
            if (persistententitysectionmanager$chunkloadstatus == PersistentEntitySectionManager.ChunkLoadStatus.LOADED) {
               this.permanentStorage.storeEntities(new ChunkEntities<>(new ChunkPos(p_157513_), ImmutableList.of()));
            }

            return true;
         } else if (persistententitysectionmanager$chunkloadstatus == PersistentEntitySectionManager.ChunkLoadStatus.FRESH) {
            this.requestChunkLoad(p_157513_);
            return false;
         } else {
            this.permanentStorage.storeEntities(new ChunkEntities<>(new ChunkPos(p_157513_), list));
            list.forEach(p_157514_);
            return true;
         }
      }
   }

   private void requestChunkLoad(long p_157563_) {
      this.chunkLoadStatuses.put(p_157563_, PersistentEntitySectionManager.ChunkLoadStatus.PENDING);
      ChunkPos chunkpos = new ChunkPos(p_157563_);
      this.permanentStorage.loadEntities(chunkpos).thenAccept(this.loadingInbox::add).exceptionally((p_157532_) -> {
         LOGGER.error("Failed to read chunk {}", chunkpos, p_157532_);
         return null;
      });
   }

   private boolean processChunkUnload(long p_157569_) {
      boolean flag = this.storeChunkSections(p_157569_, (p_157595_) -> {
         p_157595_.getPassengersAndSelf().forEach(this::unloadEntity);
      });
      if (!flag) {
         return false;
      } else {
         this.chunkLoadStatuses.remove(p_157569_);
         return true;
      }
   }

   private void unloadEntity(EntityAccess p_157586_) {
      p_157586_.setRemoved(Entity.RemovalReason.UNLOADED_TO_CHUNK);
      p_157586_.setLevelCallback(EntityInLevelCallback.NULL);
   }

   private void processUnloads() {
      this.chunksToUnload.removeIf((long p_157584_) -> {
         return this.chunkVisibility.get(p_157584_) != Visibility.HIDDEN ? true : this.processChunkUnload(p_157584_);
      });
   }

   private void processPendingLoads() {
      ChunkEntities<T> chunkentities;
      while((chunkentities = this.loadingInbox.poll()) != null) {
         chunkentities.getEntities().forEach((p_157593_) -> {
            this.addEntity(p_157593_, true);
            if (p_157593_ instanceof Entity entity) entity.onAddedToWorld();
         });
         this.chunkLoadStatuses.put(chunkentities.getPos().toLong(), PersistentEntitySectionManager.ChunkLoadStatus.LOADED);
      }

   }

   public void tick() {
      this.processPendingLoads();
      this.processUnloads();
   }

   private LongSet getAllChunksToSave() {
      LongSet longset = this.sectionStorage.getAllChunksWithExistingSections();

      for(Entry<PersistentEntitySectionManager.ChunkLoadStatus> entry : Long2ObjectMaps.fastIterable(this.chunkLoadStatuses)) {
         if (entry.getValue() == PersistentEntitySectionManager.ChunkLoadStatus.LOADED) {
            longset.add(entry.getLongKey());
         }
      }

      return longset;
   }

   public void autoSave() {
      this.getAllChunksToSave().forEach((long p_157579_) -> {
         boolean flag = this.chunkVisibility.get(p_157579_) == Visibility.HIDDEN;
         if (flag) {
            this.processChunkUnload(p_157579_);
         } else {
            this.storeChunkSections(p_157579_, (p_157591_) -> {
            });
         }

      });
   }

   public void saveAll() {
      LongSet longset = this.getAllChunksToSave();

      while(!longset.isEmpty()) {
         this.permanentStorage.flush(false);
         this.processPendingLoads();
         longset.removeIf((long p_157574_) -> {
            boolean flag = this.chunkVisibility.get(p_157574_) == Visibility.HIDDEN;
            return flag ? this.processChunkUnload(p_157574_) : this.storeChunkSections(p_157574_, (p_157589_) -> {
            });
         });
      }

      this.permanentStorage.flush(true);
   }

   public void close() throws IOException {
      this.saveAll();
      this.permanentStorage.close();
   }

   public boolean isLoaded(UUID p_157551_) {
      return this.knownUuids.contains(p_157551_);
   }

   public LevelEntityGetter<T> getEntityGetter() {
      return this.entityGetter;
   }

   public boolean canPositionTick(BlockPos p_202168_) {
      return this.chunkVisibility.get(ChunkPos.asLong(p_202168_)).isTicking();
   }

   public boolean canPositionTick(ChunkPos p_202166_) {
      return this.chunkVisibility.get(p_202166_.toLong()).isTicking();
   }

   public boolean areEntitiesLoaded(long p_157508_) {
      return this.chunkLoadStatuses.get(p_157508_) == PersistentEntitySectionManager.ChunkLoadStatus.LOADED;
   }

   public void dumpSections(Writer p_157549_) throws IOException {
      CsvOutput csvoutput = CsvOutput.builder().addColumn("x").addColumn("y").addColumn("z").addColumn("visibility").addColumn("load_status").addColumn("entity_count").build(p_157549_);
      this.sectionStorage.getAllChunksWithExistingSections().forEach((long p_157517_) -> {
         PersistentEntitySectionManager.ChunkLoadStatus persistententitysectionmanager$chunkloadstatus = this.chunkLoadStatuses.get(p_157517_);
         this.sectionStorage.getExistingSectionPositionsInChunk(p_157517_).forEach((p_157521_) -> {
            EntitySection<T> entitysection = this.sectionStorage.getSection(p_157521_);
            if (entitysection != null) {
               try {
                  csvoutput.writeRow(SectionPos.x(p_157521_), SectionPos.y(p_157521_), SectionPos.z(p_157521_), entitysection.getStatus(), persistententitysectionmanager$chunkloadstatus, entitysection.size());
               } catch (IOException ioexception) {
                  throw new UncheckedIOException(ioexception);
               }
            }

         });
      });
   }

   @VisibleForDebug
   public String gatherStats() {
      return this.knownUuids.size() + "," + this.visibleEntityStorage.count() + "," + this.sectionStorage.count() + "," + this.chunkLoadStatuses.size() + "," + this.chunkVisibility.size() + "," + this.loadingInbox.size() + "," + this.chunksToUnload.size();
   }

   class Callback implements EntityInLevelCallback {
      private final T entity;
      private final Entity realEntity;
      private long currentSectionKey;
      private EntitySection<T> currentSection;

      Callback(T p_157614_, long p_157615_, EntitySection<T> p_157616_) {
         this.entity = p_157614_;
         this.realEntity = p_157614_ instanceof Entity ? (Entity) p_157614_ : null;
         this.currentSectionKey = p_157615_;
         this.currentSection = p_157616_;
      }

      public void onMove() {
         BlockPos blockpos = this.entity.blockPosition();
         long i = SectionPos.asLong(blockpos);
         if (i != this.currentSectionKey) {
            Visibility visibility = this.currentSection.getStatus();
            if (!this.currentSection.remove(this.entity)) {
               PersistentEntitySectionManager.LOGGER.warn("Entity {} wasn't found in section {} (moving to {})", this.entity, SectionPos.of(this.currentSectionKey), i);
            }

            PersistentEntitySectionManager.this.removeSectionIfEmpty(this.currentSectionKey, this.currentSection);
            EntitySection<T> entitysection = PersistentEntitySectionManager.this.sectionStorage.getOrCreateSection(i);
            entitysection.add(this.entity);
            long oldSectionKey = currentSectionKey;
            this.currentSection = entitysection;
            this.currentSectionKey = i;
            this.updateStatus(visibility, entitysection.getStatus());
            if (this.realEntity != null) net.minecraftforge.common.ForgeHooks.onEntityEnterSection(this.realEntity, oldSectionKey, i);
         }

      }

      private void updateStatus(Visibility p_157621_, Visibility p_157622_) {
         Visibility visibility = PersistentEntitySectionManager.getEffectiveStatus(this.entity, p_157621_);
         Visibility visibility1 = PersistentEntitySectionManager.getEffectiveStatus(this.entity, p_157622_);
         if (visibility != visibility1) {
            boolean flag = visibility.isAccessible();
            boolean flag1 = visibility1.isAccessible();
            if (flag && !flag1) {
               PersistentEntitySectionManager.this.stopTracking(this.entity);
            } else if (!flag && flag1) {
               PersistentEntitySectionManager.this.startTracking(this.entity);
            }

            boolean flag2 = visibility.isTicking();
            boolean flag3 = visibility1.isTicking();
            if (flag2 && !flag3) {
               PersistentEntitySectionManager.this.stopTicking(this.entity);
            } else if (!flag2 && flag3) {
               PersistentEntitySectionManager.this.startTicking(this.entity);
            }

         }
      }

      public void onRemove(Entity.RemovalReason p_157619_) {
         if (!this.currentSection.remove(this.entity)) {
            PersistentEntitySectionManager.LOGGER.warn("Entity {} wasn't found in section {} (destroying due to {})", this.entity, SectionPos.of(this.currentSectionKey), p_157619_);
         }

         Visibility visibility = PersistentEntitySectionManager.getEffectiveStatus(this.entity, this.currentSection.getStatus());
         if (visibility.isTicking()) {
            PersistentEntitySectionManager.this.stopTicking(this.entity);
         }

         if (visibility.isAccessible()) {
            PersistentEntitySectionManager.this.stopTracking(this.entity);
         }

         if (p_157619_.shouldDestroy()) {
            PersistentEntitySectionManager.this.callbacks.onDestroyed(this.entity);
         }

         PersistentEntitySectionManager.this.knownUuids.remove(this.entity.getUUID());
         this.entity.setLevelCallback(NULL);
         PersistentEntitySectionManager.this.removeSectionIfEmpty(this.currentSectionKey, this.currentSection);
      }
   }

   static enum ChunkLoadStatus {
      FRESH,
      PENDING,
      LOADED;
   }
}
