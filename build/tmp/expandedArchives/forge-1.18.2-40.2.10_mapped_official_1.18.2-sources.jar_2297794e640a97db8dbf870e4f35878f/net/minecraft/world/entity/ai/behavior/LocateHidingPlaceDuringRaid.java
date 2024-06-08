package net.minecraft.world.entity.ai.behavior;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.raid.Raid;

public class LocateHidingPlaceDuringRaid extends LocateHidingPlace {
   public LocateHidingPlaceDuringRaid(int p_23427_, float p_23428_) {
      super(p_23427_, p_23428_, 1);
   }

   protected boolean checkExtraStartConditions(ServerLevel p_23430_, LivingEntity p_23431_) {
      Raid raid = p_23430_.getRaidAt(p_23431_.blockPosition());
      return super.checkExtraStartConditions(p_23430_, p_23431_) && raid != null && raid.isActive() && !raid.isVictory() && !raid.isLoss();
   }
}