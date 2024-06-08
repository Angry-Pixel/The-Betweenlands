package net.minecraft.world.entity.ai.goal;

import javax.annotation.Nullable;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.phys.Vec3;

public class WaterAvoidingRandomStrollGoal extends RandomStrollGoal {
   public static final float PROBABILITY = 0.001F;
   protected final float probability;

   public WaterAvoidingRandomStrollGoal(PathfinderMob p_25987_, double p_25988_) {
      this(p_25987_, p_25988_, 0.001F);
   }

   public WaterAvoidingRandomStrollGoal(PathfinderMob p_25990_, double p_25991_, float p_25992_) {
      super(p_25990_, p_25991_);
      this.probability = p_25992_;
   }

   @Nullable
   protected Vec3 getPosition() {
      if (this.mob.isInWaterOrBubble()) {
         Vec3 vec3 = LandRandomPos.getPos(this.mob, 15, 7);
         return vec3 == null ? super.getPosition() : vec3;
      } else {
         return this.mob.getRandom().nextFloat() >= this.probability ? LandRandomPos.getPos(this.mob, 10, 7) : super.getPosition();
      }
   }
}