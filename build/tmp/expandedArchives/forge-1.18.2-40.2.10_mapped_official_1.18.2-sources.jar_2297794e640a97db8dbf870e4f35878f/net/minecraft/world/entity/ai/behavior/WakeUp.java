package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.schedule.Activity;

public class WakeUp extends Behavior<LivingEntity> {
   public WakeUp() {
      super(ImmutableMap.of());
   }

   protected boolean checkExtraStartConditions(ServerLevel p_24709_, LivingEntity p_24710_) {
      return !p_24710_.getBrain().isActive(Activity.REST) && p_24710_.isSleeping();
   }

   protected void start(ServerLevel p_24712_, LivingEntity p_24713_, long p_24714_) {
      p_24713_.stopSleeping();
   }
}