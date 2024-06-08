package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.entity.schedule.Activity;

public class ResetRaidStatus extends Behavior<LivingEntity> {
   public ResetRaidStatus() {
      super(ImmutableMap.of());
   }

   protected boolean checkExtraStartConditions(ServerLevel p_23781_, LivingEntity p_23782_) {
      return p_23781_.random.nextInt(20) == 0;
   }

   protected void start(ServerLevel p_23784_, LivingEntity p_23785_, long p_23786_) {
      Brain<?> brain = p_23785_.getBrain();
      Raid raid = p_23784_.getRaidAt(p_23785_.blockPosition());
      if (raid == null || raid.isStopped() || raid.isLoss()) {
         brain.setDefaultActivity(Activity.IDLE);
         brain.updateActivityFromSchedule(p_23784_.getDayTime(), p_23784_.getGameTime());
      }

   }
}