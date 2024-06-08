package net.minecraft.world.entity.ai.navigation;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.PathFinder;
import net.minecraft.world.level.pathfinder.SwimNodeEvaluator;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class WaterBoundPathNavigation extends PathNavigation {
   private boolean allowBreaching;

   public WaterBoundPathNavigation(Mob p_26594_, Level p_26595_) {
      super(p_26594_, p_26595_);
   }

   protected PathFinder createPathFinder(int p_26598_) {
      this.allowBreaching = this.mob.getType() == EntityType.DOLPHIN;
      this.nodeEvaluator = new SwimNodeEvaluator(this.allowBreaching);
      return new PathFinder(this.nodeEvaluator, p_26598_);
   }

   protected boolean canUpdatePath() {
      return this.allowBreaching || this.isInLiquid();
   }

   protected Vec3 getTempMobPos() {
      return new Vec3(this.mob.getX(), this.mob.getY(0.5D), this.mob.getZ());
   }

   protected double getGroundY(Vec3 p_186136_) {
      return p_186136_.y;
   }

   protected boolean canMoveDirectly(Vec3 p_186138_, Vec3 p_186139_) {
      Vec3 vec3 = new Vec3(p_186139_.x, p_186139_.y + (double)this.mob.getBbHeight() * 0.5D, p_186139_.z);
      return this.level.clip(new ClipContext(p_186138_, vec3, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this.mob)).getType() == HitResult.Type.MISS;
   }

   public boolean isStableDestination(BlockPos p_26608_) {
      return !this.level.getBlockState(p_26608_).isSolidRender(this.level, p_26608_);
   }

   public void setCanFloat(boolean p_26612_) {
   }
}