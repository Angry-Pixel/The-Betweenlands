package net.minecraft.world.entity.monster.piglin;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;

public class StopAdmiringIfTiredOfTryingToReachItem<E extends Piglin> extends Behavior<E> {
   private final int maxTimeToReachItem;
   private final int disableTime;

   public StopAdmiringIfTiredOfTryingToReachItem(int p_35230_, int p_35231_) {
      super(ImmutableMap.of(MemoryModuleType.ADMIRING_ITEM, MemoryStatus.VALUE_PRESENT, MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM, MemoryStatus.VALUE_PRESENT, MemoryModuleType.TIME_TRYING_TO_REACH_ADMIRE_ITEM, MemoryStatus.REGISTERED, MemoryModuleType.DISABLE_WALK_TO_ADMIRE_ITEM, MemoryStatus.REGISTERED));
      this.maxTimeToReachItem = p_35230_;
      this.disableTime = p_35231_;
   }

   protected boolean checkExtraStartConditions(ServerLevel p_35240_, E p_35241_) {
      return p_35241_.getOffhandItem().isEmpty();
   }

   protected void start(ServerLevel p_35243_, E p_35244_, long p_35245_) {
      Brain<Piglin> brain = p_35244_.getBrain();
      Optional<Integer> optional = brain.getMemory(MemoryModuleType.TIME_TRYING_TO_REACH_ADMIRE_ITEM);
      if (!optional.isPresent()) {
         brain.setMemory(MemoryModuleType.TIME_TRYING_TO_REACH_ADMIRE_ITEM, 0);
      } else {
         int i = optional.get();
         if (i > this.maxTimeToReachItem) {
            brain.eraseMemory(MemoryModuleType.ADMIRING_ITEM);
            brain.eraseMemory(MemoryModuleType.TIME_TRYING_TO_REACH_ADMIRE_ITEM);
            brain.setMemoryWithExpiry(MemoryModuleType.DISABLE_WALK_TO_ADMIRE_ITEM, true, (long)this.disableTime);
         } else {
            brain.setMemory(MemoryModuleType.TIME_TRYING_TO_REACH_ADMIRE_ITEM, i + 1);
         }
      }

   }
}