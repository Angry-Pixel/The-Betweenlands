package net.minecraft.world.level.pathfinder;

import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.PathNavigationRegion;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;

public class SwimNodeEvaluator extends NodeEvaluator {
   private final boolean allowBreaching;
   private final Long2ObjectMap<BlockPathTypes> pathTypesByPosCache = new Long2ObjectOpenHashMap<>();

   public SwimNodeEvaluator(boolean p_77457_) {
      this.allowBreaching = p_77457_;
   }

   public void prepare(PathNavigationRegion p_192959_, Mob p_192960_) {
      super.prepare(p_192959_, p_192960_);
      this.pathTypesByPosCache.clear();
   }

   public void done() {
      super.done();
      this.pathTypesByPosCache.clear();
   }

   public Node getStart() {
      return super.getNode(Mth.floor(this.mob.getBoundingBox().minX), Mth.floor(this.mob.getBoundingBox().minY + 0.5D), Mth.floor(this.mob.getBoundingBox().minZ));
   }

   public Target getGoal(double p_77459_, double p_77460_, double p_77461_) {
      return new Target(super.getNode(Mth.floor(p_77459_), Mth.floor(p_77460_), Mth.floor(p_77461_)));
   }

   public int getNeighbors(Node[] p_77483_, Node p_77484_) {
      int i = 0;
      Map<Direction, Node> map = Maps.newEnumMap(Direction.class);

      for(Direction direction : Direction.values()) {
         Node node = this.getNode(p_77484_.x + direction.getStepX(), p_77484_.y + direction.getStepY(), p_77484_.z + direction.getStepZ());
         map.put(direction, node);
         if (this.isNodeValid(node)) {
            p_77483_[i++] = node;
         }
      }

      for(Direction direction1 : Direction.Plane.HORIZONTAL) {
         Direction direction2 = direction1.getClockWise();
         Node node1 = this.getNode(p_77484_.x + direction1.getStepX() + direction2.getStepX(), p_77484_.y, p_77484_.z + direction1.getStepZ() + direction2.getStepZ());
         if (this.isDiagonalNodeValid(node1, map.get(direction1), map.get(direction2))) {
            p_77483_[i++] = node1;
         }
      }

      return i;
   }

   protected boolean isNodeValid(@Nullable Node p_192962_) {
      return p_192962_ != null && !p_192962_.closed;
   }

   protected boolean isDiagonalNodeValid(@Nullable Node p_192964_, @Nullable Node p_192965_, @Nullable Node p_192966_) {
      return this.isNodeValid(p_192964_) && p_192965_ != null && p_192965_.costMalus >= 0.0F && p_192966_ != null && p_192966_.costMalus >= 0.0F;
   }

   @Nullable
   protected Node getNode(int p_77463_, int p_77464_, int p_77465_) {
      Node node = null;
      BlockPathTypes blockpathtypes = this.getCachedBlockType(p_77463_, p_77464_, p_77465_);
      if (this.allowBreaching && blockpathtypes == BlockPathTypes.BREACH || blockpathtypes == BlockPathTypes.WATER) {
         float f = this.mob.getPathfindingMalus(blockpathtypes);
         if (f >= 0.0F) {
            node = super.getNode(p_77463_, p_77464_, p_77465_);
            node.type = blockpathtypes;
            node.costMalus = Math.max(node.costMalus, f);
            if (this.level.getFluidState(new BlockPos(p_77463_, p_77464_, p_77465_)).isEmpty()) {
               node.costMalus += 8.0F;
            }
         }
      }

      return node;
   }

   protected BlockPathTypes getCachedBlockType(int p_192968_, int p_192969_, int p_192970_) {
      return this.pathTypesByPosCache.computeIfAbsent(BlockPos.asLong(p_192968_, p_192969_, p_192970_), (p_192957_) -> {
         return this.getBlockPathType(this.level, p_192968_, p_192969_, p_192970_);
      });
   }

   public BlockPathTypes getBlockPathType(BlockGetter p_77467_, int p_77468_, int p_77469_, int p_77470_) {
      return this.getBlockPathType(p_77467_, p_77468_, p_77469_, p_77470_, this.mob, this.entityWidth, this.entityHeight, this.entityDepth, this.canOpenDoors(), this.canPassDoors());
   }

   public BlockPathTypes getBlockPathType(BlockGetter p_77472_, int p_77473_, int p_77474_, int p_77475_, Mob p_77476_, int p_77477_, int p_77478_, int p_77479_, boolean p_77480_, boolean p_77481_) {
      BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

      for(int i = p_77473_; i < p_77473_ + p_77477_; ++i) {
         for(int j = p_77474_; j < p_77474_ + p_77478_; ++j) {
            for(int k = p_77475_; k < p_77475_ + p_77479_; ++k) {
               FluidState fluidstate = p_77472_.getFluidState(blockpos$mutableblockpos.set(i, j, k));
               BlockState blockstate = p_77472_.getBlockState(blockpos$mutableblockpos.set(i, j, k));
               if (fluidstate.isEmpty() && blockstate.isPathfindable(p_77472_, blockpos$mutableblockpos.below(), PathComputationType.WATER) && blockstate.isAir()) {
                  return BlockPathTypes.BREACH;
               }

               if (!fluidstate.is(FluidTags.WATER)) {
                  return BlockPathTypes.BLOCKED;
               }
            }
         }
      }

      BlockState blockstate1 = p_77472_.getBlockState(blockpos$mutableblockpos);
      return blockstate1.isPathfindable(p_77472_, blockpos$mutableblockpos, PathComputationType.WATER) ? BlockPathTypes.WATER : BlockPathTypes.BLOCKED;
   }
}