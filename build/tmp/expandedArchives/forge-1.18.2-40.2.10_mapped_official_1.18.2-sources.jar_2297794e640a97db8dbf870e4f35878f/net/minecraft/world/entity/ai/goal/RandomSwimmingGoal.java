package net.minecraft.world.entity.ai.goal;

import javax.annotation.Nullable;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.phys.Vec3;

public class RandomSwimmingGoal extends RandomStrollGoal {
   public RandomSwimmingGoal(PathfinderMob p_25753_, double p_25754_, int p_25755_) {
      super(p_25753_, p_25754_, p_25755_);
   }

   @Nullable
   protected Vec3 getPosition() {
      return BehaviorUtils.getRandomSwimmablePos(this.mob, 10, 7);
   }
}