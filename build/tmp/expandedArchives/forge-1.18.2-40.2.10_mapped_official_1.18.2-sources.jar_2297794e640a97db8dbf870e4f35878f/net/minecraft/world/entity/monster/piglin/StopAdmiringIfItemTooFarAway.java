package net.minecraft.world.entity.monster.piglin;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.item.ItemEntity;

public class StopAdmiringIfItemTooFarAway<E extends Piglin> extends Behavior<E> {
   private final int maxDistanceToItem;

   public StopAdmiringIfItemTooFarAway(int p_35212_) {
      super(ImmutableMap.of(MemoryModuleType.ADMIRING_ITEM, MemoryStatus.VALUE_PRESENT, MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM, MemoryStatus.REGISTERED));
      this.maxDistanceToItem = p_35212_;
   }

   protected boolean checkExtraStartConditions(ServerLevel p_35221_, E p_35222_) {
      if (!p_35222_.getOffhandItem().isEmpty()) {
         return false;
      } else {
         Optional<ItemEntity> optional = p_35222_.getBrain().getMemory(MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM);
         if (!optional.isPresent()) {
            return true;
         } else {
            return !optional.get().closerThan(p_35222_, (double)this.maxDistanceToItem);
         }
      }
   }

   protected void start(ServerLevel p_35224_, E p_35225_, long p_35226_) {
      p_35225_.getBrain().eraseMemory(MemoryModuleType.ADMIRING_ITEM);
   }
}