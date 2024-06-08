package net.minecraft.world.entity.ai.behavior;

import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;

public abstract class Behavior<E extends LivingEntity> {
   private static final int DEFAULT_DURATION = 60;
   protected final Map<MemoryModuleType<?>, MemoryStatus> entryCondition;
   private Behavior.Status status = Behavior.Status.STOPPED;
   private long endTimestamp;
   private final int minDuration;
   private final int maxDuration;

   public Behavior(Map<MemoryModuleType<?>, MemoryStatus> p_22528_) {
      this(p_22528_, 60);
   }

   public Behavior(Map<MemoryModuleType<?>, MemoryStatus> p_22530_, int p_22531_) {
      this(p_22530_, p_22531_, p_22531_);
   }

   public Behavior(Map<MemoryModuleType<?>, MemoryStatus> p_22533_, int p_22534_, int p_22535_) {
      this.minDuration = p_22534_;
      this.maxDuration = p_22535_;
      this.entryCondition = p_22533_;
   }

   public Behavior.Status getStatus() {
      return this.status;
   }

   public final boolean tryStart(ServerLevel p_22555_, E p_22556_, long p_22557_) {
      if (this.hasRequiredMemories(p_22556_) && this.checkExtraStartConditions(p_22555_, p_22556_)) {
         this.status = Behavior.Status.RUNNING;
         int i = this.minDuration + p_22555_.getRandom().nextInt(this.maxDuration + 1 - this.minDuration);
         this.endTimestamp = p_22557_ + (long)i;
         this.start(p_22555_, p_22556_, p_22557_);
         return true;
      } else {
         return false;
      }
   }

   protected void start(ServerLevel p_22540_, E p_22541_, long p_22542_) {
   }

   public final void tickOrStop(ServerLevel p_22559_, E p_22560_, long p_22561_) {
      if (!this.timedOut(p_22561_) && this.canStillUse(p_22559_, p_22560_, p_22561_)) {
         this.tick(p_22559_, p_22560_, p_22561_);
      } else {
         this.doStop(p_22559_, p_22560_, p_22561_);
      }

   }

   protected void tick(ServerLevel p_22551_, E p_22552_, long p_22553_) {
   }

   public final void doStop(ServerLevel p_22563_, E p_22564_, long p_22565_) {
      this.status = Behavior.Status.STOPPED;
      this.stop(p_22563_, p_22564_, p_22565_);
   }

   protected void stop(ServerLevel p_22548_, E p_22549_, long p_22550_) {
   }

   protected boolean canStillUse(ServerLevel p_22545_, E p_22546_, long p_22547_) {
      return false;
   }

   protected boolean timedOut(long p_22537_) {
      return p_22537_ > this.endTimestamp;
   }

   protected boolean checkExtraStartConditions(ServerLevel p_22538_, E p_22539_) {
      return true;
   }

   public String toString() {
      return this.getClass().getSimpleName();
   }

   private boolean hasRequiredMemories(E p_22544_) {
      for(Entry<MemoryModuleType<?>, MemoryStatus> entry : this.entryCondition.entrySet()) {
         MemoryModuleType<?> memorymoduletype = entry.getKey();
         MemoryStatus memorystatus = entry.getValue();
         if (!p_22544_.getBrain().checkMemory(memorymoduletype, memorystatus)) {
            return false;
         }
      }

      return true;
   }

   public static enum Status {
      STOPPED,
      RUNNING;
   }
}