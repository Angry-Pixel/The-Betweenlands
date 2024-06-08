package net.minecraft.world.entity.ai.goal;

import java.util.EnumSet;
import javax.annotation.Nullable;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.phys.Vec3;

public class RandomStrollGoal extends Goal {
   public static final int DEFAULT_INTERVAL = 120;
   protected final PathfinderMob mob;
   protected double wantedX;
   protected double wantedY;
   protected double wantedZ;
   protected final double speedModifier;
   protected int interval;
   protected boolean forceTrigger;
   private final boolean checkNoActionTime;

   public RandomStrollGoal(PathfinderMob p_25734_, double p_25735_) {
      this(p_25734_, p_25735_, 120);
   }

   public RandomStrollGoal(PathfinderMob p_25737_, double p_25738_, int p_25739_) {
      this(p_25737_, p_25738_, p_25739_, true);
   }

   public RandomStrollGoal(PathfinderMob p_25741_, double p_25742_, int p_25743_, boolean p_25744_) {
      this.mob = p_25741_;
      this.speedModifier = p_25742_;
      this.interval = p_25743_;
      this.checkNoActionTime = p_25744_;
      this.setFlags(EnumSet.of(Goal.Flag.MOVE));
   }

   public boolean canUse() {
      if (this.mob.isVehicle()) {
         return false;
      } else {
         if (!this.forceTrigger) {
            if (this.checkNoActionTime && this.mob.getNoActionTime() >= 100) {
               return false;
            }

            if (this.mob.getRandom().nextInt(reducedTickDelay(this.interval)) != 0) {
               return false;
            }
         }

         Vec3 vec3 = this.getPosition();
         if (vec3 == null) {
            return false;
         } else {
            this.wantedX = vec3.x;
            this.wantedY = vec3.y;
            this.wantedZ = vec3.z;
            this.forceTrigger = false;
            return true;
         }
      }
   }

   @Nullable
   protected Vec3 getPosition() {
      return DefaultRandomPos.getPos(this.mob, 10, 7);
   }

   public boolean canContinueToUse() {
      return !this.mob.getNavigation().isDone() && !this.mob.isVehicle();
   }

   public void start() {
      this.mob.getNavigation().moveTo(this.wantedX, this.wantedY, this.wantedZ, this.speedModifier);
   }

   public void stop() {
      this.mob.getNavigation().stop();
      super.stop();
   }

   public void trigger() {
      this.forceTrigger = true;
   }

   public void setInterval(int p_25747_) {
      this.interval = p_25747_;
   }
}