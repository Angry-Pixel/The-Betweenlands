package net.minecraft.world.entity.ai.goal;

import java.util.EnumSet;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.phys.Vec3;

public class PanicGoal extends Goal {
   public static final int WATER_CHECK_DISTANCE_VERTICAL = 1;
   protected final PathfinderMob mob;
   protected final double speedModifier;
   protected double posX;
   protected double posY;
   protected double posZ;
   protected boolean isRunning;

   public PanicGoal(PathfinderMob p_25691_, double p_25692_) {
      this.mob = p_25691_;
      this.speedModifier = p_25692_;
      this.setFlags(EnumSet.of(Goal.Flag.MOVE));
   }

   public boolean canUse() {
      if (!this.shouldPanic()) {
         return false;
      } else {
         if (this.mob.isOnFire()) {
            BlockPos blockpos = this.lookForWater(this.mob.level, this.mob, 5);
            if (blockpos != null) {
               this.posX = (double)blockpos.getX();
               this.posY = (double)blockpos.getY();
               this.posZ = (double)blockpos.getZ();
               return true;
            }
         }

         return this.findRandomPosition();
      }
   }

   protected boolean shouldPanic() {
      return this.mob.getLastHurtByMob() != null || this.mob.isFreezing() || this.mob.isOnFire();
   }

   protected boolean findRandomPosition() {
      Vec3 vec3 = DefaultRandomPos.getPos(this.mob, 5, 4);
      if (vec3 == null) {
         return false;
      } else {
         this.posX = vec3.x;
         this.posY = vec3.y;
         this.posZ = vec3.z;
         return true;
      }
   }

   public boolean isRunning() {
      return this.isRunning;
   }

   public void start() {
      this.mob.getNavigation().moveTo(this.posX, this.posY, this.posZ, this.speedModifier);
      this.isRunning = true;
   }

   public void stop() {
      this.isRunning = false;
   }

   public boolean canContinueToUse() {
      return !this.mob.getNavigation().isDone();
   }

   @Nullable
   protected BlockPos lookForWater(BlockGetter p_198173_, Entity p_198174_, int p_198175_) {
      BlockPos blockpos = p_198174_.blockPosition();
      return !p_198173_.getBlockState(blockpos).getCollisionShape(p_198173_, blockpos).isEmpty() ? null : BlockPos.findClosestMatch(p_198174_.blockPosition(), p_198175_, 1, (p_196649_) -> {
         return p_198173_.getFluidState(p_196649_).is(FluidTags.WATER);
      }).orElse((BlockPos)null);
   }
}