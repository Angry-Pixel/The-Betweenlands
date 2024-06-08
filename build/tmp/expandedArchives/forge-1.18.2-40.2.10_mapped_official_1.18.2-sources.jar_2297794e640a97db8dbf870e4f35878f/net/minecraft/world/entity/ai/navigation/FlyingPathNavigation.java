package net.minecraft.world.entity.ai.navigation;

import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.FlyNodeEvaluator;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.level.pathfinder.PathFinder;
import net.minecraft.world.phys.Vec3;

public class FlyingPathNavigation extends PathNavigation {
   public FlyingPathNavigation(Mob p_26424_, Level p_26425_) {
      super(p_26424_, p_26425_);
   }

   protected PathFinder createPathFinder(int p_26428_) {
      this.nodeEvaluator = new FlyNodeEvaluator();
      this.nodeEvaluator.setCanPassDoors(true);
      return new PathFinder(this.nodeEvaluator, p_26428_);
   }

   protected boolean canUpdatePath() {
      return this.canFloat() && this.isInLiquid() || !this.mob.isPassenger();
   }

   protected Vec3 getTempMobPos() {
      return this.mob.position();
   }

   public Path createPath(Entity p_26430_, int p_26431_) {
      return this.createPath(p_26430_.blockPosition(), p_26431_);
   }

   public void tick() {
      ++this.tick;
      if (this.hasDelayedRecomputation) {
         this.recomputePath();
      }

      if (!this.isDone()) {
         if (this.canUpdatePath()) {
            this.followThePath();
         } else if (this.path != null && !this.path.isDone()) {
            Vec3 vec3 = this.path.getNextEntityPos(this.mob);
            if (this.mob.getBlockX() == Mth.floor(vec3.x) && this.mob.getBlockY() == Mth.floor(vec3.y) && this.mob.getBlockZ() == Mth.floor(vec3.z)) {
               this.path.advance();
            }
         }

         DebugPackets.sendPathFindingPacket(this.level, this.mob, this.path, this.maxDistanceToWaypoint);
         if (!this.isDone()) {
            Vec3 vec31 = this.path.getNextEntityPos(this.mob);
            this.mob.getMoveControl().setWantedPosition(vec31.x, vec31.y, vec31.z, this.speedModifier);
         }
      }
   }

   public void setCanOpenDoors(boolean p_26441_) {
      this.nodeEvaluator.setCanOpenDoors(p_26441_);
   }

   public boolean canPassDoors() {
      return this.nodeEvaluator.canPassDoors();
   }

   public void setCanPassDoors(boolean p_26444_) {
      this.nodeEvaluator.setCanPassDoors(p_26444_);
   }

   public boolean canOpenDoors() {
      return this.nodeEvaluator.canPassDoors();
   }

   public boolean isStableDestination(BlockPos p_26439_) {
      return this.level.getBlockState(p_26439_).entityCanStandOn(this.level, p_26439_, this.mob);
   }
}