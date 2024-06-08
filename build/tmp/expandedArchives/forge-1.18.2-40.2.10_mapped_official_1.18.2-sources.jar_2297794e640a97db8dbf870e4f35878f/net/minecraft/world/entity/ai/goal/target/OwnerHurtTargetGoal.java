package net.minecraft.world.entity.ai.goal.target;

import java.util.EnumSet;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;

public class OwnerHurtTargetGoal extends TargetGoal {
   private final TamableAnimal tameAnimal;
   private LivingEntity ownerLastHurt;
   private int timestamp;

   public OwnerHurtTargetGoal(TamableAnimal p_26114_) {
      super(p_26114_, false);
      this.tameAnimal = p_26114_;
      this.setFlags(EnumSet.of(Goal.Flag.TARGET));
   }

   public boolean canUse() {
      if (this.tameAnimal.isTame() && !this.tameAnimal.isOrderedToSit()) {
         LivingEntity livingentity = this.tameAnimal.getOwner();
         if (livingentity == null) {
            return false;
         } else {
            this.ownerLastHurt = livingentity.getLastHurtMob();
            int i = livingentity.getLastHurtMobTimestamp();
            return i != this.timestamp && this.canAttack(this.ownerLastHurt, TargetingConditions.DEFAULT) && this.tameAnimal.wantsToAttack(this.ownerLastHurt, livingentity);
         }
      } else {
         return false;
      }
   }

   public void start() {
      this.mob.setTarget(this.ownerLastHurt);
      LivingEntity livingentity = this.tameAnimal.getOwner();
      if (livingentity != null) {
         this.timestamp = livingentity.getLastHurtMobTimestamp();
      }

      super.start();
   }
}