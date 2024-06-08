package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;

public class UpdateActivityFromSchedule extends Behavior<LivingEntity> {
   public UpdateActivityFromSchedule() {
      super(ImmutableMap.of());
   }

   protected void start(ServerLevel p_24458_, LivingEntity p_24459_, long p_24460_) {
      p_24459_.getBrain().updateActivityFromSchedule(p_24458_.getDayTime(), p_24458_.getGameTime());
   }
}