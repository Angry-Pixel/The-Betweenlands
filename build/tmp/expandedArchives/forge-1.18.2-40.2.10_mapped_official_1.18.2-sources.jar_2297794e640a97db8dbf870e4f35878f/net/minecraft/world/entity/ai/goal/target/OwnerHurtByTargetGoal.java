package net.minecraft.world.entity.ai.goal.target;

import java.util.EnumSet;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;

public class OwnerHurtByTargetGoal extends TargetGoal {
   private final TamableAnimal tameAnimal;
   private LivingEntity ownerLastHurtBy;
   private int timestamp;

   public OwnerHurtByTargetGoal(TamableAnimal p_26107_) {
      super(p_26107_, false);
      this.tameAnimal = p_26107_;
      this.setFlags(EnumSet.of(Goal.Flag.TARGET));
   }

   public boolean canUse() {
      if (this.tameAnimal.isTame() && !this.tameAnimal.isOrderedToSit()) {
         LivingEntity livingentity = this.tameAnimal.getOwner();
         if (livingentity == null) {
            return false;
         } else {
            this.ownerLastHurtBy = livingentity.getLastHurtByMob();
            int i = livingentity.getLastHurtByMobTimestamp();
            return i != this.timestamp && this.canAttack(this.ownerLastHurtBy, TargetingConditions.DEFAULT) && this.tameAnimal.wantsToAttack(this.ownerLastHurtBy, livingentity);
         }
      } else {
         return false;
      }
   }

   public void start() {
      this.mob.setTarget(this.ownerLastHurtBy);
      LivingEntity livingentity = this.tameAnimal.getOwner();
      if (livingentity != null) {
         this.timestamp = livingentity.getLastHurtByMobTimestamp();
      }

      super.start();
   }
}