package net.minecraft.world.entity.monster.piglin;

import com.google.common.collect.ImmutableMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.monster.hoglin.Hoglin;

public class StartHuntingHoglin<E extends Piglin> extends Behavior<E> {
   public StartHuntingHoglin() {
      super(ImmutableMap.of(MemoryModuleType.NEAREST_VISIBLE_HUNTABLE_HOGLIN, MemoryStatus.VALUE_PRESENT, MemoryModuleType.ANGRY_AT, MemoryStatus.VALUE_ABSENT, MemoryModuleType.HUNTED_RECENTLY, MemoryStatus.VALUE_ABSENT, MemoryModuleType.NEAREST_VISIBLE_ADULT_PIGLINS, MemoryStatus.REGISTERED));
   }

   protected boolean checkExtraStartConditions(ServerLevel p_35164_, Piglin p_35165_) {
      return !p_35165_.isBaby() && !PiglinAi.hasAnyoneNearbyHuntedRecently(p_35165_);
   }

   protected void start(ServerLevel p_35167_, E p_35168_, long p_35169_) {
      Hoglin hoglin = p_35168_.getBrain().getMemory(MemoryModuleType.NEAREST_VISIBLE_HUNTABLE_HOGLIN).get();
      PiglinAi.setAngerTarget(p_35168_, hoglin);
      PiglinAi.dontKillAnyMoreHoglinsForAWhile(p_35168_);
      PiglinAi.broadcastAngerTarget(p_35168_, hoglin);
      PiglinAi.broadcastDontKillAnyMoreHoglinsForAWhile(p_35168_);
   }
}