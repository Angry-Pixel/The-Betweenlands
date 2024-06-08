package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.level.pathfinder.Path;

public class AcquirePoi extends Behavior<PathfinderMob> {
   private static final int BATCH_SIZE = 5;
   private static final int RATE = 20;
   public static final int SCAN_RANGE = 48;
   private final PoiType poiType;
   private final MemoryModuleType<GlobalPos> memoryToAcquire;
   private final boolean onlyIfAdult;
   private final Optional<Byte> onPoiAcquisitionEvent;
   private long nextScheduledStart;
   private final Long2ObjectMap<AcquirePoi.JitteredLinearRetry> batchCache = new Long2ObjectOpenHashMap<>();

   public AcquirePoi(PoiType p_22323_, MemoryModuleType<GlobalPos> p_22324_, MemoryModuleType<GlobalPos> p_22325_, boolean p_22326_, Optional<Byte> p_22327_) {
      super(constructEntryConditionMap(p_22324_, p_22325_));
      this.poiType = p_22323_;
      this.memoryToAcquire = p_22325_;
      this.onlyIfAdult = p_22326_;
      this.onPoiAcquisitionEvent = p_22327_;
   }

   public AcquirePoi(PoiType p_22329_, MemoryModuleType<GlobalPos> p_22330_, boolean p_22331_, Optional<Byte> p_22332_) {
      this(p_22329_, p_22330_, p_22330_, p_22331_, p_22332_);
   }

   private static ImmutableMap<MemoryModuleType<?>, MemoryStatus> constructEntryConditionMap(MemoryModuleType<GlobalPos> p_22362_, MemoryModuleType<GlobalPos> p_22363_) {
      Builder<MemoryModuleType<?>, MemoryStatus> builder = ImmutableMap.builder();
      builder.put(p_22362_, MemoryStatus.VALUE_ABSENT);
      if (p_22363_ != p_22362_) {
         builder.put(p_22363_, MemoryStatus.VALUE_ABSENT);
      }

      return builder.build();
   }

   protected boolean checkExtraStartConditions(ServerLevel p_22347_, PathfinderMob p_22348_) {
      if (this.onlyIfAdult && p_22348_.isBaby()) {
         return false;
      } else if (this.nextScheduledStart == 0L) {
         this.nextScheduledStart = p_22348_.level.getGameTime() + (long)p_22347_.random.nextInt(20);
         return false;
      } else {
         return p_22347_.getGameTime() >= this.nextScheduledStart;
      }
   }

   protected void start(ServerLevel p_22350_, PathfinderMob p_22351_, long p_22352_) {
      this.nextScheduledStart = p_22352_ + 20L + (long)p_22350_.getRandom().nextInt(20);
      PoiManager poimanager = p_22350_.getPoiManager();
      this.batchCache.long2ObjectEntrySet().removeIf((p_22338_) -> {
         return !p_22338_.getValue().isStillValid(p_22352_);
      });
      Predicate<BlockPos> predicate = (p_22335_) -> {
         AcquirePoi.JitteredLinearRetry acquirepoi$jitteredlinearretry = this.batchCache.get(p_22335_.asLong());
         if (acquirepoi$jitteredlinearretry == null) {
            return true;
         } else if (!acquirepoi$jitteredlinearretry.shouldRetry(p_22352_)) {
            return false;
         } else {
            acquirepoi$jitteredlinearretry.markAttempt(p_22352_);
            return true;
         }
      };
      Set<BlockPos> set = poimanager.findAllClosestFirst(this.poiType.getPredicate(), predicate, p_22351_.blockPosition(), 48, PoiManager.Occupancy.HAS_SPACE).limit(5L).collect(Collectors.toSet());
      Path path = p_22351_.getNavigation().createPath(set, this.poiType.getValidRange());
      if (path != null && path.canReach()) {
         BlockPos blockpos1 = path.getTarget();
         poimanager.getType(blockpos1).ifPresent((p_22369_) -> {
            poimanager.take(this.poiType.getPredicate(), (p_147372_) -> {
               return p_147372_.equals(blockpos1);
            }, blockpos1, 1);
            p_22351_.getBrain().setMemory(this.memoryToAcquire, GlobalPos.of(p_22350_.dimension(), blockpos1));
            this.onPoiAcquisitionEvent.ifPresent((p_147369_) -> {
               p_22350_.broadcastEntityEvent(p_22351_, p_147369_);
            });
            this.batchCache.clear();
            DebugPackets.sendPoiTicketCountPacket(p_22350_, blockpos1);
         });
      } else {
         for(BlockPos blockpos : set) {
            this.batchCache.computeIfAbsent(blockpos.asLong(), (p_22360_) -> {
               return new AcquirePoi.JitteredLinearRetry(p_22351_.level.random, p_22352_);
            });
         }
      }

   }

   static class JitteredLinearRetry {
      private static final int MIN_INTERVAL_INCREASE = 40;
      private static final int MAX_INTERVAL_INCREASE = 80;
      private static final int MAX_RETRY_PATHFINDING_INTERVAL = 400;
      private final Random random;
      private long previousAttemptTimestamp;
      private long nextScheduledAttemptTimestamp;
      private int currentDelay;

      JitteredLinearRetry(Random p_22378_, long p_22379_) {
         this.random = p_22378_;
         this.markAttempt(p_22379_);
      }

      public void markAttempt(long p_22381_) {
         this.previousAttemptTimestamp = p_22381_;
         int i = this.currentDelay + this.random.nextInt(40) + 40;
         this.currentDelay = Math.min(i, 400);
         this.nextScheduledAttemptTimestamp = p_22381_ + (long)this.currentDelay;
      }

      public boolean isStillValid(long p_22383_) {
         return p_22383_ - this.previousAttemptTimestamp < 400L;
      }

      public boolean shouldRetry(long p_22385_) {
         return p_22385_ >= this.nextScheduledAttemptTimestamp;
      }

      public String toString() {
         return "RetryMarker{, previousAttemptAt=" + this.previousAttemptTimestamp + ", nextScheduledAttemptAt=" + this.nextScheduledAttemptTimestamp + ", currentDelay=" + this.currentDelay + "}";
      }
   }
}