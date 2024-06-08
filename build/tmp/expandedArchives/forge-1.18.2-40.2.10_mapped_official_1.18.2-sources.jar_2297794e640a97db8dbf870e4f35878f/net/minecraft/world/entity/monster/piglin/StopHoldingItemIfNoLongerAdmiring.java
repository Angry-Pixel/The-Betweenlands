package net.minecraft.world.entity.monster.piglin;

import com.google.common.collect.ImmutableMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.item.Items;

public class StopHoldingItemIfNoLongerAdmiring<E extends Piglin> extends Behavior<E> {
   public StopHoldingItemIfNoLongerAdmiring() {
      super(ImmutableMap.of(MemoryModuleType.ADMIRING_ITEM, MemoryStatus.VALUE_ABSENT));
   }

   protected boolean checkExtraStartConditions(ServerLevel p_35255_, E p_35256_) {
      return !p_35256_.getOffhandItem().isEmpty() && !p_35256_.getOffhandItem().canPerformAction(net.minecraftforge.common.ToolActions.SHIELD_BLOCK);
   }

   protected void start(ServerLevel p_35258_, E p_35259_, long p_35260_) {
      PiglinAi.stopHoldingOffHandItem(p_35259_, true);
   }
}
