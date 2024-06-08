package net.minecraft.world.entity.ai.behavior;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.phys.Vec3;

public class RandomSwim extends RandomStroll {
   public static final int[][] XY_DISTANCE_TIERS = new int[][]{{1, 1}, {3, 3}, {5, 5}, {6, 5}, {7, 7}, {10, 7}};

   public RandomSwim(float p_147853_) {
      super(p_147853_);
   }

   protected boolean checkExtraStartConditions(ServerLevel p_147858_, PathfinderMob p_147859_) {
      return p_147859_.isInWaterOrBubble();
   }

   @Nullable
   protected Vec3 getTargetPos(PathfinderMob p_147861_) {
      Vec3 vec3 = null;
      Vec3 vec31 = null;

      for(int[] aint : XY_DISTANCE_TIERS) {
         if (vec3 == null) {
            vec31 = BehaviorUtils.getRandomSwimmablePos(p_147861_, aint[0], aint[1]);
         } else {
            vec31 = p_147861_.position().add(p_147861_.position().vectorTo(vec3).normalize().multiply((double)aint[0], (double)aint[1], (double)aint[0]));
         }

         if (vec31 == null || p_147861_.level.getFluidState(new BlockPos(vec31)).isEmpty()) {
            return vec3;
         }

         vec3 = vec31;
      }

      return vec31;
   }
}