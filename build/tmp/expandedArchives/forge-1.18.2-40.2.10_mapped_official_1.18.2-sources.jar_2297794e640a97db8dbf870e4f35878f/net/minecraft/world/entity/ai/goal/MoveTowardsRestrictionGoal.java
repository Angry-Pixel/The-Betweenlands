package net.minecraft.world.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.phys.Vec3;

public class MoveTowardsRestrictionGoal extends Goal {
   private final PathfinderMob mob;
   private double wantedX;
   private double wantedY;
   private double wantedZ;
   private final double speedModifier;

   public MoveTowardsRestrictionGoal(PathfinderMob p_25633_, double p_25634_) {
      this.mob = p_25633_;
      this.speedModifier = p_25634_;
      this.setFlags(EnumSet.of(Goal.Flag.MOVE));
   }

   public boolean canUse() {
      if (this.mob.isWithinRestriction()) {
         return false;
      } else {
         Vec3 vec3 = DefaultRandomPos.getPosTowards(this.mob, 16, 7, Vec3.atBottomCenterOf(this.mob.getRestrictCenter()), (double)((float)Math.PI / 2F));
         if (vec3 == null) {
            return false;
         } else {
            this.wantedX = vec3.x;
            this.wantedY = vec3.y;
            this.wantedZ = vec3.z;
            return true;
         }
      }
   }

   public boolean canContinueToUse() {
      return !this.mob.getNavigation().isDone();
   }

   public void start() {
      this.mob.getNavigation().moveTo(this.wantedX, this.wantedY, this.wantedZ, this.speedModifier);
   }
}