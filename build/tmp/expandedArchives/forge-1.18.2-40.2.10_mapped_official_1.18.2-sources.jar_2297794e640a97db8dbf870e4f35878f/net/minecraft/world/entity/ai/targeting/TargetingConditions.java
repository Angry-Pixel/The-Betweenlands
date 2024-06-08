package net.minecraft.world.entity.ai.targeting;

import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;

public class TargetingConditions {
   public static final TargetingConditions DEFAULT = forCombat();
   private static final double MIN_VISIBILITY_DISTANCE_FOR_INVISIBLE_TARGET = 2.0D;
   private final boolean isCombat;
   private double range = -1.0D;
   private boolean checkLineOfSight = true;
   private boolean testInvisible = true;
   @Nullable
   private Predicate<LivingEntity> selector;

   private TargetingConditions(boolean p_148351_) {
      this.isCombat = p_148351_;
   }

   public static TargetingConditions forCombat() {
      return new TargetingConditions(true);
   }

   public static TargetingConditions forNonCombat() {
      return new TargetingConditions(false);
   }

   public TargetingConditions copy() {
      TargetingConditions targetingconditions = this.isCombat ? forCombat() : forNonCombat();
      targetingconditions.range = this.range;
      targetingconditions.checkLineOfSight = this.checkLineOfSight;
      targetingconditions.testInvisible = this.testInvisible;
      targetingconditions.selector = this.selector;
      return targetingconditions;
   }

   public TargetingConditions range(double p_26884_) {
      this.range = p_26884_;
      return this;
   }

   public TargetingConditions ignoreLineOfSight() {
      this.checkLineOfSight = false;
      return this;
   }

   public TargetingConditions ignoreInvisibilityTesting() {
      this.testInvisible = false;
      return this;
   }

   public TargetingConditions selector(@Nullable Predicate<LivingEntity> p_26889_) {
      this.selector = p_26889_;
      return this;
   }

   public boolean test(@Nullable LivingEntity p_26886_, LivingEntity p_26887_) {
      if (p_26886_ == p_26887_) {
         return false;
      } else if (!p_26887_.canBeSeenByAnyone()) {
         return false;
      } else if (this.selector != null && !this.selector.test(p_26887_)) {
         return false;
      } else {
         if (p_26886_ == null) {
            if (this.isCombat && (!p_26887_.canBeSeenAsEnemy() || p_26887_.level.getDifficulty() == Difficulty.PEACEFUL)) {
               return false;
            }
         } else {
            if (this.isCombat && (!p_26886_.canAttack(p_26887_) || !p_26886_.canAttackType(p_26887_.getType()) || p_26886_.isAlliedTo(p_26887_))) {
               return false;
            }

            if (this.range > 0.0D) {
               double d0 = this.testInvisible ? p_26887_.getVisibilityPercent(p_26886_) : 1.0D;
               double d1 = Math.max(this.range * d0, 2.0D);
               double d2 = p_26886_.distanceToSqr(p_26887_.getX(), p_26887_.getY(), p_26887_.getZ());
               if (d2 > d1 * d1) {
                  return false;
               }
            }

            if (this.checkLineOfSight && p_26886_ instanceof Mob) {
               Mob mob = (Mob)p_26886_;
               if (!mob.getSensing().hasLineOfSight(p_26887_)) {
                  return false;
               }
            }
         }

         return true;
      }
   }
}