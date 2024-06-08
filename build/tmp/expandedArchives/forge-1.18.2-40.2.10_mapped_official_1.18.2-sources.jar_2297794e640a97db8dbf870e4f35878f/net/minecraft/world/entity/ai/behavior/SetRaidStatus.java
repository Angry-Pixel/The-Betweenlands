package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.entity.schedule.Activity;

public class SetRaidStatus extends Behavior<LivingEntity> {
   public SetRaidStatus() {
      super(ImmutableMap.of());
   }

   protected boolean checkExtraStartConditions(ServerLevel p_23976_, LivingEntity p_23977_) {
      return p_23976_.random.nextInt(20) == 0;
   }

   protected void start(ServerLevel p_23979_, LivingEntity p_23980_, long p_23981_) {
      Brain<?> brain = p_23980_.getBrain();
      Raid raid = p_23979_.getRaidAt(p_23980_.blockPosition());
      if (raid != null) {
         if (raid.hasFirstWaveSpawned() && !raid.isBetweenWaves()) {
            brain.setDefaultActivity(Activity.RAID);
            brain.setActiveActivityIfPossible(Activity.RAID);
         } else {
            brain.setDefaultActivity(Activity.PRE_RAID);
            brain.setActiveActivityIfPossible(Activity.PRE_RAID);
         }
      }

   }
}