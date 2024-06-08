package net.minecraft.world.entity.ai.goal;

import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;

public class LookAtTradingPlayerGoal extends LookAtPlayerGoal {
   private final AbstractVillager villager;

   public LookAtTradingPlayerGoal(AbstractVillager p_25538_) {
      super(p_25538_, Player.class, 8.0F);
      this.villager = p_25538_;
   }

   public boolean canUse() {
      if (this.villager.isTrading()) {
         this.lookAt = this.villager.getTradingPlayer();
         return true;
      } else {
         return false;
      }
   }
}