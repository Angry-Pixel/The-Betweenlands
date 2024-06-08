package net.minecraft.world.entity.ai.util;

import java.util.function.ToDoubleFunction;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.phys.Vec3;

public class LandRandomPos {
   @Nullable
   public static Vec3 getPos(PathfinderMob p_148489_, int p_148490_, int p_148491_) {
      return getPos(p_148489_, p_148490_, p_148491_, p_148489_::getWalkTargetValue);
   }

   @Nullable
   public static Vec3 getPos(PathfinderMob p_148504_, int p_148505_, int p_148506_, ToDoubleFunction<BlockPos> p_148507_) {
      boolean flag = GoalUtils.mobRestricted(p_148504_, p_148505_);
      return RandomPos.generateRandomPos(() -> {
         BlockPos blockpos = RandomPos.generateRandomDirection(p_148504_.getRandom(), p_148505_, p_148506_);
         BlockPos blockpos1 = generateRandomPosTowardDirection(p_148504_, p_148505_, flag, blockpos);
         return blockpos1 == null ? null : movePosUpOutOfSolid(p_148504_, blockpos1);
      }, p_148507_);
   }

   @Nullable
   public static Vec3 getPosTowards(PathfinderMob p_148493_, int p_148494_, int p_148495_, Vec3 p_148496_) {
      Vec3 vec3 = p_148496_.subtract(p_148493_.getX(), p_148493_.getY(), p_148493_.getZ());
      boolean flag = GoalUtils.mobRestricted(p_148493_, p_148494_);
      return getPosInDirection(p_148493_, p_148494_, p_148495_, vec3, flag);
   }

   @Nullable
   public static Vec3 getPosAway(PathfinderMob p_148522_, int p_148523_, int p_148524_, Vec3 p_148525_) {
      Vec3 vec3 = p_148522_.position().subtract(p_148525_);
      boolean flag = GoalUtils.mobRestricted(p_148522_, p_148523_);
      return getPosInDirection(p_148522_, p_148523_, p_148524_, vec3, flag);
   }

   @Nullable
   private static Vec3 getPosInDirection(PathfinderMob p_148498_, int p_148499_, int p_148500_, Vec3 p_148501_, boolean p_148502_) {
      return RandomPos.generateRandomPos(p_148498_, () -> {
         BlockPos blockpos = RandomPos.generateRandomDirectionWithinRadians(p_148498_.getRandom(), p_148499_, p_148500_, 0, p_148501_.x, p_148501_.z, (double)((float)Math.PI / 2F));
         if (blockpos == null) {
            return null;
         } else {
            BlockPos blockpos1 = generateRandomPosTowardDirection(p_148498_, p_148499_, p_148502_, blockpos);
            return blockpos1 == null ? null : movePosUpOutOfSolid(p_148498_, blockpos1);
         }
      });
   }

   @Nullable
   public static BlockPos movePosUpOutOfSolid(PathfinderMob p_148519_, BlockPos p_148520_) {
      p_148520_ = RandomPos.moveUpOutOfSolid(p_148520_, p_148519_.level.getMaxBuildHeight(), (p_148534_) -> {
         return GoalUtils.isSolid(p_148519_, p_148534_);
      });
      return !GoalUtils.isWater(p_148519_, p_148520_) && !GoalUtils.hasMalus(p_148519_, p_148520_) ? p_148520_ : null;
   }

   @Nullable
   public static BlockPos generateRandomPosTowardDirection(PathfinderMob p_148514_, int p_148515_, boolean p_148516_, BlockPos p_148517_) {
      BlockPos blockpos = RandomPos.generateRandomPosTowardDirection(p_148514_, p_148515_, p_148514_.getRandom(), p_148517_);
      return !GoalUtils.isOutsideLimits(blockpos, p_148514_) && !GoalUtils.isRestricted(p_148516_, p_148514_, blockpos) && !GoalUtils.isNotStable(p_148514_.getNavigation(), blockpos) ? blockpos : null;
   }
}