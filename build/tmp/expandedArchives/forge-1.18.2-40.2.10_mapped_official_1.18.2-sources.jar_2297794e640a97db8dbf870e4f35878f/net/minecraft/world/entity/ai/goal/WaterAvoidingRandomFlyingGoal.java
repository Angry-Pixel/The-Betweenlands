package net.minecraft.world.entity.ai.goal;

import javax.annotation.Nullable;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.util.AirAndWaterRandomPos;
import net.minecraft.world.entity.ai.util.HoverRandomPos;
import net.minecraft.world.phys.Vec3;

public class WaterAvoidingRandomFlyingGoal extends WaterAvoidingRandomStrollGoal {
   public WaterAvoidingRandomFlyingGoal(PathfinderMob p_25981_, double p_25982_) {
      super(p_25981_, p_25982_);
   }

   @Nullable
   protected Vec3 getPosition() {
      Vec3 vec3 = this.mob.getViewVector(0.0F);
      int i = 8;
      Vec3 vec31 = HoverRandomPos.getPos(this.mob, 8, 7, vec3.x, vec3.z, ((float)Math.PI / 2F), 3, 1);
      return vec31 != null ? vec31 : AirAndWaterRandomPos.getPos(this.mob, 8, 4, -2, vec3.x, vec3.z, (double)((float)Math.PI / 2F));
   }
}