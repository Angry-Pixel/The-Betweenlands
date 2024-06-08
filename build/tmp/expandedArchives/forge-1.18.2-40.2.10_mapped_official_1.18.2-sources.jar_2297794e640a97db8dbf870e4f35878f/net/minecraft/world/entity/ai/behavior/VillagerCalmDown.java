package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.npc.Villager;

public class VillagerCalmDown extends Behavior<Villager> {
   private static final int SAFE_DISTANCE_FROM_DANGER = 36;

   public VillagerCalmDown() {
      super(ImmutableMap.of());
   }

   protected void start(ServerLevel p_24574_, Villager p_24575_, long p_24576_) {
      boolean flag = VillagerPanicTrigger.isHurt(p_24575_) || VillagerPanicTrigger.hasHostile(p_24575_) || isCloseToEntityThatHurtMe(p_24575_);
      if (!flag) {
         p_24575_.getBrain().eraseMemory(MemoryModuleType.HURT_BY);
         p_24575_.getBrain().eraseMemory(MemoryModuleType.HURT_BY_ENTITY);
         p_24575_.getBrain().updateActivityFromSchedule(p_24574_.getDayTime(), p_24574_.getGameTime());
      }

   }

   private static boolean isCloseToEntityThatHurtMe(Villager p_24578_) {
      return p_24578_.getBrain().getMemory(MemoryModuleType.HURT_BY_ENTITY).filter((p_24581_) -> {
         return p_24581_.distanceToSqr(p_24578_) <= 36.0D;
      }).isPresent();
   }
}