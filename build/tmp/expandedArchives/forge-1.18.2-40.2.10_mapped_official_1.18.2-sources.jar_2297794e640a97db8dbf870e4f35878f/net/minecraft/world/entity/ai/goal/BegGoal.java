package net.minecraft.world.entity.ai.goal;

import java.util.EnumSet;
import javax.annotation.Nullable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

public class BegGoal extends Goal {
   private final Wolf wolf;
   @Nullable
   private Player player;
   private final Level level;
   private final float lookDistance;
   private int lookTime;
   private final TargetingConditions begTargeting;

   public BegGoal(Wolf p_25063_, float p_25064_) {
      this.wolf = p_25063_;
      this.level = p_25063_.level;
      this.lookDistance = p_25064_;
      this.begTargeting = TargetingConditions.forNonCombat().range((double)p_25064_);
      this.setFlags(EnumSet.of(Goal.Flag.LOOK));
   }

   public boolean canUse() {
      this.player = this.level.getNearestPlayer(this.begTargeting, this.wolf);
      return this.player == null ? false : this.playerHoldingInteresting(this.player);
   }

   public boolean canContinueToUse() {
      if (!this.player.isAlive()) {
         return false;
      } else if (this.wolf.distanceToSqr(this.player) > (double)(this.lookDistance * this.lookDistance)) {
         return false;
      } else {
         return this.lookTime > 0 && this.playerHoldingInteresting(this.player);
      }
   }

   public void start() {
      this.wolf.setIsInterested(true);
      this.lookTime = this.adjustedTickDelay(40 + this.wolf.getRandom().nextInt(40));
   }

   public void stop() {
      this.wolf.setIsInterested(false);
      this.player = null;
   }

   public void tick() {
      this.wolf.getLookControl().setLookAt(this.player.getX(), this.player.getEyeY(), this.player.getZ(), 10.0F, (float)this.wolf.getMaxHeadXRot());
      --this.lookTime;
   }

   private boolean playerHoldingInteresting(Player p_25067_) {
      for(InteractionHand interactionhand : InteractionHand.values()) {
         ItemStack itemstack = p_25067_.getItemInHand(interactionhand);
         if (this.wolf.isTame() && itemstack.is(Items.BONE)) {
            return true;
         }

         if (this.wolf.isFood(itemstack)) {
            return true;
         }
      }

      return false;
   }
}