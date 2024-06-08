package net.minecraft.world.entity.ai.util;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.phys.Vec3;

public class DefaultRandomPos {
   @Nullable
   public static Vec3 getPos(PathfinderMob p_148404_, int p_148405_, int p_148406_) {
      boolean flag = GoalUtils.mobRestricted(p_148404_, p_148405_);
      return RandomPos.generateRandomPos(p_148404_, () -> {
         BlockPos blockpos = RandomPos.generateRandomDirection(p_148404_.getRandom(), p_148405_, p_148406_);
         return generateRandomPosTowardDirection(p_148404_, p_148405_, flag, blockpos);
      });
   }

   @Nullable
   public static Vec3 getPosTowards(PathfinderMob p_148413_, int p_148414_, int p_148415_, Vec3 p_148416_, double p_148417_) {
      Vec3 vec3 = p_148416_.subtract(p_148413_.getX(), p_148413_.getY(), p_148413_.getZ());
      boolean flag = GoalUtils.mobRestricted(p_148413_, p_148414_);
      return RandomPos.generateRandomPos(p_148413_, () -> {
         BlockPos blockpos = RandomPos.generateRandomDirectionWithinRadians(p_148413_.getRandom(), p_148414_, p_148415_, 0, vec3.x, vec3.z, p_148417_);
         return blockpos == null ? null : generateRandomPosTowardDirection(p_148413_, p_148414_, flag, blockpos);
      });
   }

   @Nullable
   public static Vec3 getPosAway(PathfinderMob p_148408_, int p_148409_, int p_148410_, Vec3 p_148411_) {
      Vec3 vec3 = p_148408_.position().subtract(p_148411_);
      boolean flag = GoalUtils.mobRestricted(p_148408_, p_148409_);
      return RandomPos.generateRandomPos(p_148408_, () -> {
         BlockPos blockpos = RandomPos.generateRandomDirectionWithinRadians(p_148408_.getRandom(), p_148409_, p_148410_, 0, vec3.x, vec3.z, (double)((float)Math.PI / 2F));
         return blockpos == null ? null : generateRandomPosTowardDirection(p_148408_, p_148409_, flag, blockpos);
      });
   }

   @Nullable
   private static BlockPos generateRandomPosTowardDirection(PathfinderMob p_148437_, int p_148438_, boolean p_148439_, BlockPos p_148440_) {
      BlockPos blockpos = RandomPos.generateRandomPosTowardDirection(p_148437_, p_148438_, p_148437_.getRandom(), p_148440_);
      return !GoalUtils.isOutsideLimits(blockpos, p_148437_) && !GoalUtils.isRestricted(p_148439_, p_148437_, blockpos) && !GoalUtils.isNotStable(p_148437_.getNavigation(), blockpos) && !GoalUtils.hasMalus(p_148437_, blockpos) ? blockpos : null;
   }
}