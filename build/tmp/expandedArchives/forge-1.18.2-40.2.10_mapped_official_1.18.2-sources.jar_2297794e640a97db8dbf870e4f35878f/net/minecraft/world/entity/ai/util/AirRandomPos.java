package net.minecraft.world.entity.ai.util;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.phys.Vec3;

public class AirRandomPos {
   @Nullable
   public static Vec3 getPosTowards(PathfinderMob p_148388_, int p_148389_, int p_148390_, int p_148391_, Vec3 p_148392_, double p_148393_) {
      Vec3 vec3 = p_148392_.subtract(p_148388_.getX(), p_148388_.getY(), p_148388_.getZ());
      boolean flag = GoalUtils.mobRestricted(p_148388_, p_148389_);
      return RandomPos.generateRandomPos(p_148388_, () -> {
         BlockPos blockpos = AirAndWaterRandomPos.generateRandomPos(p_148388_, p_148389_, p_148390_, p_148391_, vec3.x, vec3.z, p_148393_, flag);
         return blockpos != null && !GoalUtils.isWater(p_148388_, blockpos) ? blockpos : null;
      });
   }
}