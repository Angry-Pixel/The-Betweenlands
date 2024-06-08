package net.minecraft.world.entity.ai.behavior;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.raid.Raid;

public class GoOutsideToCelebrate extends MoveToSkySeeingSpot {
   public GoOutsideToCelebrate(float p_23050_) {
      super(p_23050_);
   }

   protected boolean checkExtraStartConditions(ServerLevel p_23052_, LivingEntity p_23053_) {
      Raid raid = p_23052_.getRaidAt(p_23053_.blockPosition());
      return raid != null && raid.isVictory() && super.checkExtraStartConditions(p_23052_, p_23053_);
   }
}