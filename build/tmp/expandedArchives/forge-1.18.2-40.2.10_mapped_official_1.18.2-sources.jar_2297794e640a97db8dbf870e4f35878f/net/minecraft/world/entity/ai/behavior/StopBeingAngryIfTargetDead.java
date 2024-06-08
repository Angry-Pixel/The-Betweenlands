package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.level.GameRules;

public class StopBeingAngryIfTargetDead<E extends Mob> extends Behavior<E> {
   public StopBeingAngryIfTargetDead() {
      super(ImmutableMap.of(MemoryModuleType.ANGRY_AT, MemoryStatus.VALUE_PRESENT));
   }

   protected void start(ServerLevel p_24263_, E p_24264_, long p_24265_) {
      BehaviorUtils.getLivingEntityFromUUIDMemory(p_24264_, MemoryModuleType.ANGRY_AT).ifPresent((p_24269_) -> {
         if (p_24269_.isDeadOrDying() && (p_24269_.getType() != EntityType.PLAYER || p_24263_.getGameRules().getBoolean(GameRules.RULE_FORGIVE_DEAD_PLAYERS))) {
            p_24264_.getBrain().eraseMemory(MemoryModuleType.ANGRY_AT);
         }

      });
   }
}