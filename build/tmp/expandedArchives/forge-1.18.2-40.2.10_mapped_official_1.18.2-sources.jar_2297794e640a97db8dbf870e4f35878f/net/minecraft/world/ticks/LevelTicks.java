package net.minecraft.world.ticks;

import it.unimi.dsi.fastutil.longs.Long2LongMap;
import it.unimi.dsi.fastutil.longs.Long2LongMaps;
import it.unimi.dsi.fastutil.longs.Long2LongOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2LongMap.Entry;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectOpenCustomHashSet;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.LongSummaryStatistics;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.LongPredicate;
import java.util.function.Predicate;
import java.util.function.Supplier;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.core.Vec3i;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

public class LevelTicks<T> implements LevelTickAccess<T> {
   private static final Comparator<LevelChunkTicks<?>> CONTAINER_DRAIN_ORDER = (p_193246_, p_193247_) -> {
      return ScheduledTick.INTRA_TICK_DRAIN_ORDER.compare(p_193246_.peek(), p_193247_.peek());
   };
   private final LongPredicate tickCheck;
   private final Supplier<ProfilerFiller> profiler;
   private final Long2ObjectMap<LevelChunkTicks<T>> allContainers = new Long2ObjectOpenHashMap<>();
   private final Long2LongMap nextTickForContainer = Util.make(new Long2LongOpenHashMap(), (p_193262_) -> {
      p_193262_.defaultReturnValue(Long.MAX_VALUE);
   });
   private final Queue<LevelChunkTicks<T>> containersToTick = new PriorityQueue<>(CONTAINER_DRAIN_ORDER);
   private final Queue<ScheduledTick<T>> toRunThisTick = new ArrayDeque<>();
   private final List<ScheduledTick<T>> alreadyRunThisTick = new ArrayList<>();
   private final Set<ScheduledTick<?>> toRunThisTickSet = new ObjectOpenCustomHashSet<>(ScheduledTick.UNIQUE_TICK_HASH);
   private final BiConsumer<LevelChunkTicks<T>, ScheduledTick<T>> chunkScheduleUpdater = (p_193249_, p_193250_) -> {
      if (p_193250_.equals(p_193249_.peek())) {
         this.updateContainerScheduling(p_193250_);
      }

   };

   public LevelTicks(LongPredicate p_193211_, Supplier<ProfilerFiller> p_193212_) {
      this.tickCheck = p_193211_;
      this.profiler = p_193212_;
   }

   public void addContainer(ChunkPos p_193232_, LevelChunkTicks<T> p_193233_) {
      long i = p_193232_.toLong();
      this.allContainers.put(i, p_193233_);
      ScheduledTick<T> scheduledtick = p_193233_.peek();
      if (scheduledtick != null) {
         this.nextTickForContainer.put(i, scheduledtick.triggerTick());
      }

      p_193233_.setOnTickAdded(this.chunkScheduleUpdater);
   }

   public void removeContainer(ChunkPos p_193230_) {
      long i = p_193230_.toLong();
      LevelChunkTicks<T> levelchunkticks = this.allContainers.remove(i);
      this.nextTickForContainer.remove(i);
      if (levelchunkticks != null) {
         levelchunkticks.setOnTickAdded((BiConsumer<LevelChunkTicks<T>, ScheduledTick<T>>)null);
      }

   }

   public void schedule(ScheduledTick<T> p_193252_) {
      long i = ChunkPos.asLong(p_193252_.pos());
      LevelChunkTicks<T> levelchunkticks = this.allContainers.get(i);
      if (levelchunkticks == null) {
         Util.pauseInIde(new IllegalStateException("Trying to schedule tick in not loaded position " + p_193252_.pos()));
      } else {
         levelchunkticks.schedule(p_193252_);
      }
   }

   public void tick(long p_193226_, int p_193227_, BiConsumer<BlockPos, T> p_193228_) {
      ProfilerFiller profilerfiller = this.profiler.get();
      profilerfiller.push("collect");
      this.collectTicks(p_193226_, p_193227_, profilerfiller);
      profilerfiller.popPush("run");
      profilerfiller.incrementCounter("ticksToRun", this.toRunThisTick.size());
      this.runCollectedTicks(p_193228_);
      profilerfiller.popPush("cleanup");
      this.cleanupAfterTick();
      profilerfiller.pop();
   }

   private void collectTicks(long p_193222_, int p_193223_, ProfilerFiller p_193224_) {
      this.sortContainersToTick(p_193222_);
      p_193224_.incrementCounter("containersToTick", this.containersToTick.size());
      this.drainContainers(p_193222_, p_193223_);
      this.rescheduleLeftoverContainers();
   }

   private void sortContainersToTick(long p_193217_) {
      ObjectIterator<Entry> objectiterator = Long2LongMaps.fastIterator(this.nextTickForContainer);

      while(objectiterator.hasNext()) {
         Entry entry = objectiterator.next();
         long i = entry.getLongKey();
         long j = entry.getLongValue();
         if (j <= p_193217_) {
            LevelChunkTicks<T> levelchunkticks = this.allContainers.get(i);
            if (levelchunkticks == null) {
               objectiterator.remove();
            } else {
               ScheduledTick<T> scheduledtick = levelchunkticks.peek();
               if (scheduledtick == null) {
                  objectiterator.remove();
               } else if (scheduledtick.triggerTick() > p_193217_) {
                  entry.setValue(scheduledtick.triggerTick());
               } else if (this.tickCheck.test(i)) {
                  objectiterator.remove();
                  this.containersToTick.add(levelchunkticks);
               }
            }
         }
      }

   }

   private void drainContainers(long p_193219_, int p_193220_) {
      LevelChunkTicks<T> levelchunkticks;
      while(this.canScheduleMoreTicks(p_193220_) && (levelchunkticks = this.containersToTick.poll()) != null) {
         ScheduledTick<T> scheduledtick = levelchunkticks.poll();
         this.scheduleForThisTick(scheduledtick);
         this.drainFromCurrentContainer(this.containersToTick, levelchunkticks, p_193219_, p_193220_);
         ScheduledTick<T> scheduledtick1 = levelchunkticks.peek();
         if (scheduledtick1 != null) {
            if (scheduledtick1.triggerTick() <= p_193219_ && this.canScheduleMoreTicks(p_193220_)) {
               this.containersToTick.add(levelchunkticks);
            } else {
               this.updateContainerScheduling(scheduledtick1);
            }
         }
      }

   }

   private void rescheduleLeftoverContainers() {
      for(LevelChunkTicks<T> levelchunkticks : this.containersToTick) {
         this.updateContainerScheduling(levelchunkticks.peek());
      }

   }

   private void updateContainerScheduling(ScheduledTick<T> p_193280_) {
      this.nextTickForContainer.put(ChunkPos.asLong(p_193280_.pos()), p_193280_.triggerTick());
   }

   private void drainFromCurrentContainer(Queue<LevelChunkTicks<T>> p_193268_, LevelChunkTicks<T> p_193269_, long p_193270_, int p_193271_) {
      if (this.canScheduleMoreTicks(p_193271_)) {
         LevelChunkTicks<T> levelchunkticks = p_193268_.peek();
         ScheduledTick<T> scheduledtick = levelchunkticks != null ? levelchunkticks.peek() : null;

         while(this.canScheduleMoreTicks(p_193271_)) {
            ScheduledTick<T> scheduledtick1 = p_193269_.peek();
            if (scheduledtick1 == null || scheduledtick1.triggerTick() > p_193270_ || scheduledtick != null && ScheduledTick.INTRA_TICK_DRAIN_ORDER.compare(scheduledtick1, scheduledtick) > 0) {
               break;
            }

            p_193269_.poll();
            this.scheduleForThisTick(scheduledtick1);
         }

      }
   }

   private void scheduleForThisTick(ScheduledTick<T> p_193286_) {
      this.toRunThisTick.add(p_193286_);
   }

   private boolean canScheduleMoreTicks(int p_193215_) {
      return this.toRunThisTick.size() < p_193215_;
   }

   private void runCollectedTicks(BiConsumer<BlockPos, T> p_193273_) {
      while(!this.toRunThisTick.isEmpty()) {
         ScheduledTick<T> scheduledtick = this.toRunThisTick.poll();
         if (!this.toRunThisTickSet.isEmpty()) {
            this.toRunThisTickSet.remove(scheduledtick);
         }

         this.alreadyRunThisTick.add(scheduledtick);
         p_193273_.accept(scheduledtick.pos(), scheduledtick.type());
      }

   }

   private void cleanupAfterTick() {
      this.toRunThisTick.clear();
      this.containersToTick.clear();
      this.alreadyRunThisTick.clear();
      this.toRunThisTickSet.clear();
   }

   public boolean hasScheduledTick(BlockPos p_193254_, T p_193255_) {
      LevelChunkTicks<T> levelchunkticks = this.allContainers.get(ChunkPos.asLong(p_193254_));
      return levelchunkticks != null && levelchunkticks.hasScheduledTick(p_193254_, p_193255_);
   }

   public boolean willTickThisTick(BlockPos p_193282_, T p_193283_) {
      this.calculateTickSetIfNeeded();
      return this.toRunThisTickSet.contains(ScheduledTick.probe(p_193283_, p_193282_));
   }

   private void calculateTickSetIfNeeded() {
      if (this.toRunThisTickSet.isEmpty() && !this.toRunThisTick.isEmpty()) {
         this.toRunThisTickSet.addAll(this.toRunThisTick);
      }

   }

   private void forContainersInArea(BoundingBox p_193237_, LevelTicks.PosAndContainerConsumer<T> p_193238_) {
      int i = SectionPos.posToSectionCoord((double)p_193237_.minX());
      int j = SectionPos.posToSectionCoord((double)p_193237_.minZ());
      int k = SectionPos.posToSectionCoord((double)p_193237_.maxX());
      int l = SectionPos.posToSectionCoord((double)p_193237_.maxZ());

      for(int i1 = i; i1 <= k; ++i1) {
         for(int j1 = j; j1 <= l; ++j1) {
            long k1 = ChunkPos.asLong(i1, j1);
            LevelChunkTicks<T> levelchunkticks = this.allContainers.get(k1);
            if (levelchunkticks != null) {
               p_193238_.accept(k1, levelchunkticks);
            }
         }
      }

   }

   public void clearArea(BoundingBox p_193235_) {
      Predicate<ScheduledTick<T>> predicate = (p_193241_) -> {
         return p_193235_.isInside(p_193241_.pos());
      };
      this.forContainersInArea(p_193235_, (p_193276_, p_193277_) -> {
         ScheduledTick<T> scheduledtick = p_193277_.peek();
         p_193277_.removeIf(predicate);
         ScheduledTick<T> scheduledtick1 = p_193277_.peek();
         if (scheduledtick1 != scheduledtick) {
            if (scheduledtick1 != null) {
               this.updateContainerScheduling(scheduledtick1);
            } else {
               this.nextTickForContainer.remove(p_193276_);
            }
         }

      });
      this.alreadyRunThisTick.removeIf(predicate);
      this.toRunThisTick.removeIf(predicate);
   }

   public void copyArea(BoundingBox p_193243_, Vec3i p_193244_) {
      List<ScheduledTick<T>> list = new ArrayList<>();
      Predicate<ScheduledTick<T>> predicate = (p_200922_) -> {
         return p_193243_.isInside(p_200922_.pos());
      };
      this.alreadyRunThisTick.stream().filter(predicate).forEach(list::add);
      this.toRunThisTick.stream().filter(predicate).forEach(list::add);
      this.forContainersInArea(p_193243_, (p_200931_, p_200932_) -> {
         p_200932_.getAll().filter(predicate).forEach(list::add);
      });
      LongSummaryStatistics longsummarystatistics = list.stream().mapToLong(ScheduledTick::subTickOrder).summaryStatistics();
      long i = longsummarystatistics.getMin();
      long j = longsummarystatistics.getMax();
      list.forEach((p_193260_) -> {
         this.schedule(new ScheduledTick<T>(p_193260_.type(), p_193260_.pos().offset(p_193244_), p_193260_.triggerTick(), p_193260_.priority(), p_193260_.subTickOrder() - i + j + 1L));
      });
   }

   public int count() {
      return this.allContainers.values().stream().mapToInt(TickAccess::count).sum();
   }

   @FunctionalInterface
   interface PosAndContainerConsumer<T> {
      void accept(long p_193289_, LevelChunkTicks<T> p_193290_);
   }
}