package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.Map;
import java.util.function.Predicate;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;

public class RunIf<E extends LivingEntity> extends Behavior<E> {
   private final Predicate<E> predicate;
   private final Behavior<? super E> wrappedBehavior;
   private final boolean checkWhileRunningAlso;

   public RunIf(Map<MemoryModuleType<?>, MemoryStatus> p_23799_, Predicate<E> p_23800_, Behavior<? super E> p_23801_, boolean p_23802_) {
      super(mergeMaps(p_23799_, p_23801_.entryCondition));
      this.predicate = p_23800_;
      this.wrappedBehavior = p_23801_;
      this.checkWhileRunningAlso = p_23802_;
   }

   private static Map<MemoryModuleType<?>, MemoryStatus> mergeMaps(Map<MemoryModuleType<?>, MemoryStatus> p_23816_, Map<MemoryModuleType<?>, MemoryStatus> p_23817_) {
      Map<MemoryModuleType<?>, MemoryStatus> map = Maps.newHashMap();
      map.putAll(p_23816_);
      map.putAll(p_23817_);
      return map;
   }

   public RunIf(Predicate<E> p_147868_, Behavior<? super E> p_147869_, boolean p_147870_) {
      this(ImmutableMap.of(), p_147868_, p_147869_, p_147870_);
   }

   public RunIf(Predicate<E> p_23804_, Behavior<? super E> p_23805_) {
      this(ImmutableMap.of(), p_23804_, p_23805_, false);
   }

   public RunIf(Map<MemoryModuleType<?>, MemoryStatus> p_147865_, Behavior<? super E> p_147866_) {
      this(p_147865_, (p_147872_) -> {
         return true;
      }, p_147866_, false);
   }

   protected boolean checkExtraStartConditions(ServerLevel p_23809_, E p_23810_) {
      return this.predicate.test(p_23810_) && this.wrappedBehavior.checkExtraStartConditions(p_23809_, p_23810_);
   }

   protected boolean canStillUse(ServerLevel p_23819_, E p_23820_, long p_23821_) {
      return this.checkWhileRunningAlso && this.predicate.test(p_23820_) && this.wrappedBehavior.canStillUse(p_23819_, p_23820_, p_23821_);
   }

   protected boolean timedOut(long p_23807_) {
      return false;
   }

   protected void start(ServerLevel p_23812_, E p_23813_, long p_23814_) {
      this.wrappedBehavior.start(p_23812_, p_23813_, p_23814_);
   }

   protected void tick(ServerLevel p_23827_, E p_23828_, long p_23829_) {
      this.wrappedBehavior.tick(p_23827_, p_23828_, p_23829_);
   }

   protected void stop(ServerLevel p_23823_, E p_23824_, long p_23825_) {
      this.wrappedBehavior.stop(p_23823_, p_23824_, p_23825_);
   }

   public String toString() {
      return "RunIf: " + this.wrappedBehavior;
   }
}