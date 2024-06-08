package net.minecraft.world.entity.ai.behavior;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.raid.Raid;

public class VictoryStroll extends VillageBoundRandomStroll {
   public VictoryStroll(float p_24535_) {
      super(p_24535_);
   }

   protected boolean checkExtraStartConditions(ServerLevel p_24540_, PathfinderMob p_24541_) {
      Raid raid = p_24540_.getRaidAt(p_24541_.blockPosition());
      return raid != null && raid.isVictory() && super.checkExtraStartConditions(p_24540_, p_24541_);
   }
}