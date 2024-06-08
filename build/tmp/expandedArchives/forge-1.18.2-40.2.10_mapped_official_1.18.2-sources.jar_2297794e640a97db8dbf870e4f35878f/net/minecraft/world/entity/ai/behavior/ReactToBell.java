package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.entity.schedule.Activity;

public class ReactToBell extends Behavior<LivingEntity> {
   public ReactToBell() {
      super(ImmutableMap.of(MemoryModuleType.HEARD_BELL_TIME, MemoryStatus.VALUE_PRESENT));
   }

   protected void start(ServerLevel p_23761_, LivingEntity p_23762_, long p_23763_) {
      Brain<?> brain = p_23762_.getBrain();
      Raid raid = p_23761_.getRaidAt(p_23762_.blockPosition());
      if (raid == null) {
         brain.setActiveActivityIfPossible(Activity.HIDE);
      }

   }
}