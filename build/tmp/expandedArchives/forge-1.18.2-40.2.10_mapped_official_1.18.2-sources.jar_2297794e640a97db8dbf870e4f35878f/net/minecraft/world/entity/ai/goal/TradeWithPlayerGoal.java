package net.minecraft.world.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;

public class TradeWithPlayerGoal extends Goal {
   private final AbstractVillager mob;

   public TradeWithPlayerGoal(AbstractVillager p_25958_) {
      this.mob = p_25958_;
      this.setFlags(EnumSet.of(Goal.Flag.JUMP, Goal.Flag.MOVE));
   }

   public boolean canUse() {
      if (!this.mob.isAlive()) {
         return false;
      } else if (this.mob.isInWater()) {
         return false;
      } else if (!this.mob.isOnGround()) {
         return false;
      } else if (this.mob.hurtMarked) {
         return false;
      } else {
         Player player = this.mob.getTradingPlayer();
         if (player == null) {
            return false;
         } else if (this.mob.distanceToSqr(player) > 16.0D) {
            return false;
         } else {
            return player.containerMenu != null;
         }
      }
   }

   public void start() {
      this.mob.getNavigation().stop();
   }

   public void stop() {
      this.mob.setTradingPlayer((Player)null);
   }
}