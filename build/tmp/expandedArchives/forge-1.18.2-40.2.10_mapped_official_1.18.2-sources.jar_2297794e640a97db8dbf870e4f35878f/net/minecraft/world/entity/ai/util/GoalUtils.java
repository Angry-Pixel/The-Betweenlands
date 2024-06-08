package net.minecraft.world.entity.ai.util;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;

public class GoalUtils {
   public static boolean hasGroundPathNavigation(Mob p_26895_) {
      return p_26895_.getNavigation() instanceof GroundPathNavigation;
   }

   public static boolean mobRestricted(PathfinderMob p_148443_, int p_148444_) {
      return p_148443_.hasRestriction() && p_148443_.getRestrictCenter().closerToCenterThan(p_148443_.position(), (double)(p_148443_.getRestrictRadius() + (float)p_148444_) + 1.0D);
   }

   public static boolean isOutsideLimits(BlockPos p_148452_, PathfinderMob p_148453_) {
      return p_148452_.getY() < p_148453_.level.getMinBuildHeight() || p_148452_.getY() > p_148453_.level.getMaxBuildHeight();
   }

   public static boolean isRestricted(boolean p_148455_, PathfinderMob p_148456_, BlockPos p_148457_) {
      return p_148455_ && !p_148456_.isWithinRestriction(p_148457_);
   }

   public static boolean isNotStable(PathNavigation p_148449_, BlockPos p_148450_) {
      return !p_148449_.isStableDestination(p_148450_);
   }

   public static boolean isWater(PathfinderMob p_148446_, BlockPos p_148447_) {
      return p_148446_.level.getFluidState(p_148447_).is(FluidTags.WATER);
   }

   public static boolean hasMalus(PathfinderMob p_148459_, BlockPos p_148460_) {
      return p_148459_.getPathfindingMalus(WalkNodeEvaluator.getBlockPathTypeStatic(p_148459_.level, p_148460_.mutable())) != 0.0F;
   }

   public static boolean isSolid(PathfinderMob p_148462_, BlockPos p_148463_) {
      return p_148462_.level.getBlockState(p_148463_).getMaterial().isSolid();
   }
}