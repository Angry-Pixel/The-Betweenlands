package net.minecraft.world.level.pathfinder;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.PathNavigationRegion;

public class AmphibiousNodeEvaluator extends WalkNodeEvaluator {
   private final boolean prefersShallowSwimming;
   private float oldWalkableCost;
   private float oldWaterBorderCost;

   public AmphibiousNodeEvaluator(boolean p_164659_) {
      this.prefersShallowSwimming = p_164659_;
   }

   public void prepare(PathNavigationRegion p_164671_, Mob p_164672_) {
      super.prepare(p_164671_, p_164672_);
      p_164672_.setPathfindingMalus(BlockPathTypes.WATER, 0.0F);
      this.oldWalkableCost = p_164672_.getPathfindingMalus(BlockPathTypes.WALKABLE);
      p_164672_.setPathfindingMalus(BlockPathTypes.WALKABLE, 6.0F);
      this.oldWaterBorderCost = p_164672_.getPathfindingMalus(BlockPathTypes.WATER_BORDER);
      p_164672_.setPathfindingMalus(BlockPathTypes.WATER_BORDER, 4.0F);
   }

   public void done() {
      this.mob.setPathfindingMalus(BlockPathTypes.WALKABLE, this.oldWalkableCost);
      this.mob.setPathfindingMalus(BlockPathTypes.WATER_BORDER, this.oldWaterBorderCost);
      super.done();
   }

   public Node getStart() {
      return this.getNode(Mth.floor(this.mob.getBoundingBox().minX), Mth.floor(this.mob.getBoundingBox().minY + 0.5D), Mth.floor(this.mob.getBoundingBox().minZ));
   }

   public Target getGoal(double p_164662_, double p_164663_, double p_164664_) {
      return new Target(this.getNode(Mth.floor(p_164662_), Mth.floor(p_164663_ + 0.5D), Mth.floor(p_164664_)));
   }

   public int getNeighbors(Node[] p_164676_, Node p_164677_) {
      int i = super.getNeighbors(p_164676_, p_164677_);
      BlockPathTypes blockpathtypes = this.getCachedBlockType(this.mob, p_164677_.x, p_164677_.y + 1, p_164677_.z);
      BlockPathTypes blockpathtypes1 = this.getCachedBlockType(this.mob, p_164677_.x, p_164677_.y, p_164677_.z);
      int j;
      if (this.mob.getPathfindingMalus(blockpathtypes) >= 0.0F && blockpathtypes1 != BlockPathTypes.STICKY_HONEY) {
         j = Mth.floor(Math.max(1.0F, this.mob.getStepHeight()));
      } else {
         j = 0;
      }

      double d0 = this.getFloorLevel(new BlockPos(p_164677_.x, p_164677_.y, p_164677_.z));
      Node node = this.findAcceptedNode(p_164677_.x, p_164677_.y + 1, p_164677_.z, Math.max(0, j - 1), d0, Direction.UP, blockpathtypes1);
      Node node1 = this.findAcceptedNode(p_164677_.x, p_164677_.y - 1, p_164677_.z, j, d0, Direction.DOWN, blockpathtypes1);
      if (this.isNeighborValid(node, p_164677_)) {
         p_164676_[i++] = node;
      }

      if (this.isNeighborValid(node1, p_164677_) && blockpathtypes1 != BlockPathTypes.TRAPDOOR) {
         p_164676_[i++] = node1;
      }

      for(int k = 0; k < i; ++k) {
         Node node2 = p_164676_[k];
         if (node2.type == BlockPathTypes.WATER && this.prefersShallowSwimming && node2.y < this.mob.level.getSeaLevel() - 10) {
            ++node2.costMalus;
         }
      }

      return i;
   }

   protected double getFloorLevel(BlockPos p_164674_) {
      return this.mob.isInWater() ? (double)p_164674_.getY() + 0.5D : super.getFloorLevel(p_164674_);
   }

   protected boolean isAmphibious() {
      return true;
   }

   public BlockPathTypes getBlockPathType(BlockGetter p_164666_, int p_164667_, int p_164668_, int p_164669_) {
      BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();
      BlockPathTypes blockpathtypes = getBlockPathTypeRaw(p_164666_, blockpos$mutableblockpos.set(p_164667_, p_164668_, p_164669_));
      if (blockpathtypes == BlockPathTypes.WATER) {
         for(Direction direction : Direction.values()) {
            BlockPathTypes blockpathtypes1 = getBlockPathTypeRaw(p_164666_, blockpos$mutableblockpos.set(p_164667_, p_164668_, p_164669_).move(direction));
            if (blockpathtypes1 == BlockPathTypes.BLOCKED) {
               return BlockPathTypes.WATER_BORDER;
            }
         }

         return BlockPathTypes.WATER;
      } else {
         return getBlockPathTypeStatic(p_164666_, blockpos$mutableblockpos);
      }
   }
}
