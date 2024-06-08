package net.minecraft.world.level.pathfinder;

import com.google.common.collect.ImmutableSet;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import java.util.EnumSet;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.PathNavigationRegion;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class FlyNodeEvaluator extends WalkNodeEvaluator {
   private final Long2ObjectMap<BlockPathTypes> pathTypeByPosCache = new Long2ObjectOpenHashMap<>();

   public void prepare(PathNavigationRegion p_77261_, Mob p_77262_) {
      super.prepare(p_77261_, p_77262_);
      this.pathTypeByPosCache.clear();
      this.oldWaterCost = p_77262_.getPathfindingMalus(BlockPathTypes.WATER);
   }

   public void done() {
      this.mob.setPathfindingMalus(BlockPathTypes.WATER, this.oldWaterCost);
      this.pathTypeByPosCache.clear();
      super.done();
   }

   public Node getStart() {
      int i;
      if (this.canFloat() && this.mob.isInWater()) {
         i = this.mob.getBlockY();
         BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos(this.mob.getX(), (double)i, this.mob.getZ());

         for(BlockState blockstate = this.level.getBlockState(blockpos$mutableblockpos); blockstate.is(Blocks.WATER); blockstate = this.level.getBlockState(blockpos$mutableblockpos)) {
            ++i;
            blockpos$mutableblockpos.set(this.mob.getX(), (double)i, this.mob.getZ());
         }
      } else {
         i = Mth.floor(this.mob.getY() + 0.5D);
      }

      BlockPos blockpos1 = this.mob.blockPosition();
      BlockPathTypes blockpathtypes1 = this.getCachedBlockPathType(blockpos1.getX(), i, blockpos1.getZ());
      if (this.mob.getPathfindingMalus(blockpathtypes1) < 0.0F) {
         for(BlockPos blockpos : ImmutableSet.of(new BlockPos(this.mob.getBoundingBox().minX, (double)i, this.mob.getBoundingBox().minZ), new BlockPos(this.mob.getBoundingBox().minX, (double)i, this.mob.getBoundingBox().maxZ), new BlockPos(this.mob.getBoundingBox().maxX, (double)i, this.mob.getBoundingBox().minZ), new BlockPos(this.mob.getBoundingBox().maxX, (double)i, this.mob.getBoundingBox().maxZ))) {
            BlockPathTypes blockpathtypes = this.getCachedBlockPathType(blockpos1.getX(), i, blockpos1.getZ());
            if (this.mob.getPathfindingMalus(blockpathtypes) >= 0.0F) {
               return super.getNode(blockpos.getX(), blockpos.getY(), blockpos.getZ());
            }
         }
      }

      return super.getNode(blockpos1.getX(), i, blockpos1.getZ());
   }

   public Target getGoal(double p_77229_, double p_77230_, double p_77231_) {
      return new Target(super.getNode(Mth.floor(p_77229_), Mth.floor(p_77230_), Mth.floor(p_77231_)));
   }

   public int getNeighbors(Node[] p_77266_, Node p_77267_) {
      int i = 0;
      Node node = this.getNode(p_77267_.x, p_77267_.y, p_77267_.z + 1);
      if (this.isOpen(node)) {
         p_77266_[i++] = node;
      }

      Node node1 = this.getNode(p_77267_.x - 1, p_77267_.y, p_77267_.z);
      if (this.isOpen(node1)) {
         p_77266_[i++] = node1;
      }

      Node node2 = this.getNode(p_77267_.x + 1, p_77267_.y, p_77267_.z);
      if (this.isOpen(node2)) {
         p_77266_[i++] = node2;
      }

      Node node3 = this.getNode(p_77267_.x, p_77267_.y, p_77267_.z - 1);
      if (this.isOpen(node3)) {
         p_77266_[i++] = node3;
      }

      Node node4 = this.getNode(p_77267_.x, p_77267_.y + 1, p_77267_.z);
      if (this.isOpen(node4)) {
         p_77266_[i++] = node4;
      }

      Node node5 = this.getNode(p_77267_.x, p_77267_.y - 1, p_77267_.z);
      if (this.isOpen(node5)) {
         p_77266_[i++] = node5;
      }

      Node node6 = this.getNode(p_77267_.x, p_77267_.y + 1, p_77267_.z + 1);
      if (this.isOpen(node6) && this.hasMalus(node) && this.hasMalus(node4)) {
         p_77266_[i++] = node6;
      }

      Node node7 = this.getNode(p_77267_.x - 1, p_77267_.y + 1, p_77267_.z);
      if (this.isOpen(node7) && this.hasMalus(node1) && this.hasMalus(node4)) {
         p_77266_[i++] = node7;
      }

      Node node8 = this.getNode(p_77267_.x + 1, p_77267_.y + 1, p_77267_.z);
      if (this.isOpen(node8) && this.hasMalus(node2) && this.hasMalus(node4)) {
         p_77266_[i++] = node8;
      }

      Node node9 = this.getNode(p_77267_.x, p_77267_.y + 1, p_77267_.z - 1);
      if (this.isOpen(node9) && this.hasMalus(node3) && this.hasMalus(node4)) {
         p_77266_[i++] = node9;
      }

      Node node10 = this.getNode(p_77267_.x, p_77267_.y - 1, p_77267_.z + 1);
      if (this.isOpen(node10) && this.hasMalus(node) && this.hasMalus(node5)) {
         p_77266_[i++] = node10;
      }

      Node node11 = this.getNode(p_77267_.x - 1, p_77267_.y - 1, p_77267_.z);
      if (this.isOpen(node11) && this.hasMalus(node1) && this.hasMalus(node5)) {
         p_77266_[i++] = node11;
      }

      Node node12 = this.getNode(p_77267_.x + 1, p_77267_.y - 1, p_77267_.z);
      if (this.isOpen(node12) && this.hasMalus(node2) && this.hasMalus(node5)) {
         p_77266_[i++] = node12;
      }

      Node node13 = this.getNode(p_77267_.x, p_77267_.y - 1, p_77267_.z - 1);
      if (this.isOpen(node13) && this.hasMalus(node3) && this.hasMalus(node5)) {
         p_77266_[i++] = node13;
      }

      Node node14 = this.getNode(p_77267_.x + 1, p_77267_.y, p_77267_.z - 1);
      if (this.isOpen(node14) && this.hasMalus(node3) && this.hasMalus(node2)) {
         p_77266_[i++] = node14;
      }

      Node node15 = this.getNode(p_77267_.x + 1, p_77267_.y, p_77267_.z + 1);
      if (this.isOpen(node15) && this.hasMalus(node) && this.hasMalus(node2)) {
         p_77266_[i++] = node15;
      }

      Node node16 = this.getNode(p_77267_.x - 1, p_77267_.y, p_77267_.z - 1);
      if (this.isOpen(node16) && this.hasMalus(node3) && this.hasMalus(node1)) {
         p_77266_[i++] = node16;
      }

      Node node17 = this.getNode(p_77267_.x - 1, p_77267_.y, p_77267_.z + 1);
      if (this.isOpen(node17) && this.hasMalus(node) && this.hasMalus(node1)) {
         p_77266_[i++] = node17;
      }

      Node node18 = this.getNode(p_77267_.x + 1, p_77267_.y + 1, p_77267_.z - 1);
      if (this.isOpen(node18) && this.hasMalus(node14) && this.hasMalus(node3) && this.hasMalus(node2) && this.hasMalus(node4) && this.hasMalus(node9) && this.hasMalus(node8)) {
         p_77266_[i++] = node18;
      }

      Node node19 = this.getNode(p_77267_.x + 1, p_77267_.y + 1, p_77267_.z + 1);
      if (this.isOpen(node19) && this.hasMalus(node15) && this.hasMalus(node) && this.hasMalus(node2) && this.hasMalus(node4) && this.hasMalus(node6) && this.hasMalus(node8)) {
         p_77266_[i++] = node19;
      }

      Node node20 = this.getNode(p_77267_.x - 1, p_77267_.y + 1, p_77267_.z - 1);
      if (this.isOpen(node20) && this.hasMalus(node16) && this.hasMalus(node3) && this.hasMalus(node1) && this.hasMalus(node4) && this.hasMalus(node9) && this.hasMalus(node7)) {
         p_77266_[i++] = node20;
      }

      Node node21 = this.getNode(p_77267_.x - 1, p_77267_.y + 1, p_77267_.z + 1);
      if (this.isOpen(node21) && this.hasMalus(node17) && this.hasMalus(node) && this.hasMalus(node1) && this.hasMalus(node4) && this.hasMalus(node6) && this.hasMalus(node7)) {
         p_77266_[i++] = node21;
      }

      Node node22 = this.getNode(p_77267_.x + 1, p_77267_.y - 1, p_77267_.z - 1);
      if (this.isOpen(node22) && this.hasMalus(node14) && this.hasMalus(node3) && this.hasMalus(node2) && this.hasMalus(node5) && this.hasMalus(node13) && this.hasMalus(node12)) {
         p_77266_[i++] = node22;
      }

      Node node23 = this.getNode(p_77267_.x + 1, p_77267_.y - 1, p_77267_.z + 1);
      if (this.isOpen(node23) && this.hasMalus(node15) && this.hasMalus(node) && this.hasMalus(node2) && this.hasMalus(node5) && this.hasMalus(node10) && this.hasMalus(node12)) {
         p_77266_[i++] = node23;
      }

      Node node24 = this.getNode(p_77267_.x - 1, p_77267_.y - 1, p_77267_.z - 1);
      if (this.isOpen(node24) && this.hasMalus(node16) && this.hasMalus(node3) && this.hasMalus(node1) && this.hasMalus(node5) && this.hasMalus(node13) && this.hasMalus(node11)) {
         p_77266_[i++] = node24;
      }

      Node node25 = this.getNode(p_77267_.x - 1, p_77267_.y - 1, p_77267_.z + 1);
      if (this.isOpen(node25) && this.hasMalus(node17) && this.hasMalus(node) && this.hasMalus(node1) && this.hasMalus(node5) && this.hasMalus(node10) && this.hasMalus(node11)) {
         p_77266_[i++] = node25;
      }

      return i;
   }

   private boolean hasMalus(@Nullable Node p_77264_) {
      return p_77264_ != null && p_77264_.costMalus >= 0.0F;
   }

   private boolean isOpen(@Nullable Node p_77270_) {
      return p_77270_ != null && !p_77270_.closed;
   }

   @Nullable
   protected Node getNode(int p_77233_, int p_77234_, int p_77235_) {
      Node node = null;
      BlockPathTypes blockpathtypes = this.getCachedBlockPathType(p_77233_, p_77234_, p_77235_);
      float f = this.mob.getPathfindingMalus(blockpathtypes);
      if (f >= 0.0F) {
         node = super.getNode(p_77233_, p_77234_, p_77235_);
         node.type = blockpathtypes;
         node.costMalus = Math.max(node.costMalus, f);
         if (blockpathtypes == BlockPathTypes.WALKABLE) {
            ++node.costMalus;
         }
      }

      return node;
   }

   private BlockPathTypes getCachedBlockPathType(int p_164694_, int p_164695_, int p_164696_) {
      return this.pathTypeByPosCache.computeIfAbsent(BlockPos.asLong(p_164694_, p_164695_, p_164696_), (p_164692_) -> {
         return this.getBlockPathType(this.level, p_164694_, p_164695_, p_164696_, this.mob, this.entityWidth, this.entityHeight, this.entityDepth, this.canOpenDoors(), this.canPassDoors());
      });
   }

   public BlockPathTypes getBlockPathType(BlockGetter p_77250_, int p_77251_, int p_77252_, int p_77253_, Mob p_77254_, int p_77255_, int p_77256_, int p_77257_, boolean p_77258_, boolean p_77259_) {
      EnumSet<BlockPathTypes> enumset = EnumSet.noneOf(BlockPathTypes.class);
      BlockPathTypes blockpathtypes = BlockPathTypes.BLOCKED;
      BlockPos blockpos = p_77254_.blockPosition();
      blockpathtypes = super.getBlockPathTypes(p_77250_, p_77251_, p_77252_, p_77253_, p_77255_, p_77256_, p_77257_, p_77258_, p_77259_, enumset, blockpathtypes, blockpos);
      if (enumset.contains(BlockPathTypes.FENCE)) {
         return BlockPathTypes.FENCE;
      } else {
         BlockPathTypes blockpathtypes1 = BlockPathTypes.BLOCKED;

         for(BlockPathTypes blockpathtypes2 : enumset) {
            if (p_77254_.getPathfindingMalus(blockpathtypes2) < 0.0F) {
               return blockpathtypes2;
            }

            if (p_77254_.getPathfindingMalus(blockpathtypes2) >= p_77254_.getPathfindingMalus(blockpathtypes1)) {
               blockpathtypes1 = blockpathtypes2;
            }
         }

         return blockpathtypes == BlockPathTypes.OPEN && p_77254_.getPathfindingMalus(blockpathtypes1) == 0.0F ? BlockPathTypes.OPEN : blockpathtypes1;
      }
   }

   public BlockPathTypes getBlockPathType(BlockGetter p_77245_, int p_77246_, int p_77247_, int p_77248_) {
      BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();
      BlockPathTypes blockpathtypes = getBlockPathTypeRaw(p_77245_, blockpos$mutableblockpos.set(p_77246_, p_77247_, p_77248_));
      if (blockpathtypes == BlockPathTypes.OPEN && p_77247_ >= p_77245_.getMinBuildHeight() + 1) {
         BlockPathTypes blockpathtypes1 = getBlockPathTypeRaw(p_77245_, blockpos$mutableblockpos.set(p_77246_, p_77247_ - 1, p_77248_));
         if (blockpathtypes1 != BlockPathTypes.DAMAGE_FIRE && blockpathtypes1 != BlockPathTypes.LAVA) {
            if (blockpathtypes1 == BlockPathTypes.DAMAGE_CACTUS) {
               blockpathtypes = BlockPathTypes.DAMAGE_CACTUS;
            } else if (blockpathtypes1 == BlockPathTypes.DAMAGE_OTHER) {
               blockpathtypes = BlockPathTypes.DAMAGE_OTHER;
            } else if (blockpathtypes1 == BlockPathTypes.COCOA) {
               blockpathtypes = BlockPathTypes.COCOA;
            } else if (blockpathtypes1 == BlockPathTypes.FENCE) {
               blockpathtypes = BlockPathTypes.FENCE;
            } else {
               blockpathtypes = blockpathtypes1 != BlockPathTypes.WALKABLE && blockpathtypes1 != BlockPathTypes.OPEN && blockpathtypes1 != BlockPathTypes.WATER ? BlockPathTypes.WALKABLE : BlockPathTypes.OPEN;
            }
         } else {
            blockpathtypes = BlockPathTypes.DAMAGE_FIRE;
         }
      }

      if (blockpathtypes == BlockPathTypes.WALKABLE || blockpathtypes == BlockPathTypes.OPEN) {
         blockpathtypes = checkNeighbourBlocks(p_77245_, blockpos$mutableblockpos.set(p_77246_, p_77247_, p_77248_), blockpathtypes);
      }

      return blockpathtypes;
   }
}