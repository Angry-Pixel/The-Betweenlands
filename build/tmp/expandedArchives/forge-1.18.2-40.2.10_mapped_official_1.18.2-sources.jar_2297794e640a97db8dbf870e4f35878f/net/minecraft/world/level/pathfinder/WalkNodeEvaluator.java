package net.minecraft.world.level.pathfinder;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import java.util.EnumSet;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.PathNavigationRegion;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

public class WalkNodeEvaluator extends NodeEvaluator {
   public static final double SPACE_BETWEEN_WALL_POSTS = 0.5D;
   protected float oldWaterCost;
   private final Long2ObjectMap<BlockPathTypes> pathTypesByPosCache = new Long2ObjectOpenHashMap<>();
   private final Object2BooleanMap<AABB> collisionCache = new Object2BooleanOpenHashMap<>();

   public void prepare(PathNavigationRegion p_77620_, Mob p_77621_) {
      super.prepare(p_77620_, p_77621_);
      this.oldWaterCost = p_77621_.getPathfindingMalus(BlockPathTypes.WATER);
   }

   public void done() {
      this.mob.setPathfindingMalus(BlockPathTypes.WATER, this.oldWaterCost);
      this.pathTypesByPosCache.clear();
      this.collisionCache.clear();
      super.done();
   }

   public Node getStart() {
      BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();
      int i = this.mob.getBlockY();
      BlockState blockstate = this.level.getBlockState(blockpos$mutableblockpos.set(this.mob.getX(), (double)i, this.mob.getZ()));
      if (!this.mob.canStandOnFluid(blockstate.getFluidState())) {
         if (this.canFloat() && this.mob.isInWater()) {
            while(true) {
               if (!blockstate.is(Blocks.WATER) && blockstate.getFluidState() != Fluids.WATER.getSource(false)) {
                  --i;
                  break;
               }

               ++i;
               blockstate = this.level.getBlockState(blockpos$mutableblockpos.set(this.mob.getX(), (double)i, this.mob.getZ()));
            }
         } else if (this.mob.isOnGround()) {
            i = Mth.floor(this.mob.getY() + 0.5D);
         } else {
            BlockPos blockpos;
            for(blockpos = this.mob.blockPosition(); (this.level.getBlockState(blockpos).isAir() || this.level.getBlockState(blockpos).isPathfindable(this.level, blockpos, PathComputationType.LAND)) && blockpos.getY() > this.mob.level.getMinBuildHeight(); blockpos = blockpos.below()) {
            }

            i = blockpos.above().getY();
         }
      } else {
         while(this.mob.canStandOnFluid(blockstate.getFluidState())) {
            ++i;
            blockstate = this.level.getBlockState(blockpos$mutableblockpos.set(this.mob.getX(), (double)i, this.mob.getZ()));
         }

         --i;
      }

      BlockPos blockpos1 = this.mob.blockPosition();
      BlockPathTypes blockpathtypes = this.getCachedBlockType(this.mob, blockpos1.getX(), i, blockpos1.getZ());
      if (this.mob.getPathfindingMalus(blockpathtypes) < 0.0F) {
         AABB aabb = this.mob.getBoundingBox();
         if (this.hasPositiveMalus(blockpos$mutableblockpos.set(aabb.minX, (double)i, aabb.minZ)) || this.hasPositiveMalus(blockpos$mutableblockpos.set(aabb.minX, (double)i, aabb.maxZ)) || this.hasPositiveMalus(blockpos$mutableblockpos.set(aabb.maxX, (double)i, aabb.minZ)) || this.hasPositiveMalus(blockpos$mutableblockpos.set(aabb.maxX, (double)i, aabb.maxZ))) {
            Node node = this.getNode(blockpos$mutableblockpos);
            node.type = this.getBlockPathType(this.mob, node.asBlockPos());
            node.costMalus = this.mob.getPathfindingMalus(node.type);
            return node;
         }
      }

      Node node1 = this.getNode(blockpos1.getX(), i, blockpos1.getZ());
      node1.type = this.getBlockPathType(this.mob, node1.asBlockPos());
      node1.costMalus = this.mob.getPathfindingMalus(node1.type);
      return node1;
   }

   private boolean hasPositiveMalus(BlockPos p_77647_) {
      BlockPathTypes blockpathtypes = this.getBlockPathType(this.mob, p_77647_);
      return this.mob.getPathfindingMalus(blockpathtypes) >= 0.0F;
   }

   public Target getGoal(double p_77550_, double p_77551_, double p_77552_) {
      return new Target(this.getNode(Mth.floor(p_77550_), Mth.floor(p_77551_), Mth.floor(p_77552_)));
   }

   public int getNeighbors(Node[] p_77640_, Node p_77641_) {
      int i = 0;
      int j = 0;
      BlockPathTypes blockpathtypes = this.getCachedBlockType(this.mob, p_77641_.x, p_77641_.y + 1, p_77641_.z);
      BlockPathTypes blockpathtypes1 = this.getCachedBlockType(this.mob, p_77641_.x, p_77641_.y, p_77641_.z);
      if (this.mob.getPathfindingMalus(blockpathtypes) >= 0.0F && blockpathtypes1 != BlockPathTypes.STICKY_HONEY) {
         j = Mth.floor(Math.max(1.0F, this.mob.getStepHeight()));
      }

      double d0 = this.getFloorLevel(new BlockPos(p_77641_.x, p_77641_.y, p_77641_.z));
      Node node = this.findAcceptedNode(p_77641_.x, p_77641_.y, p_77641_.z + 1, j, d0, Direction.SOUTH, blockpathtypes1);
      if (this.isNeighborValid(node, p_77641_)) {
         p_77640_[i++] = node;
      }

      Node node1 = this.findAcceptedNode(p_77641_.x - 1, p_77641_.y, p_77641_.z, j, d0, Direction.WEST, blockpathtypes1);
      if (this.isNeighborValid(node1, p_77641_)) {
         p_77640_[i++] = node1;
      }

      Node node2 = this.findAcceptedNode(p_77641_.x + 1, p_77641_.y, p_77641_.z, j, d0, Direction.EAST, blockpathtypes1);
      if (this.isNeighborValid(node2, p_77641_)) {
         p_77640_[i++] = node2;
      }

      Node node3 = this.findAcceptedNode(p_77641_.x, p_77641_.y, p_77641_.z - 1, j, d0, Direction.NORTH, blockpathtypes1);
      if (this.isNeighborValid(node3, p_77641_)) {
         p_77640_[i++] = node3;
      }

      Node node4 = this.findAcceptedNode(p_77641_.x - 1, p_77641_.y, p_77641_.z - 1, j, d0, Direction.NORTH, blockpathtypes1);
      if (this.isDiagonalValid(p_77641_, node1, node3, node4)) {
         p_77640_[i++] = node4;
      }

      Node node5 = this.findAcceptedNode(p_77641_.x + 1, p_77641_.y, p_77641_.z - 1, j, d0, Direction.NORTH, blockpathtypes1);
      if (this.isDiagonalValid(p_77641_, node2, node3, node5)) {
         p_77640_[i++] = node5;
      }

      Node node6 = this.findAcceptedNode(p_77641_.x - 1, p_77641_.y, p_77641_.z + 1, j, d0, Direction.SOUTH, blockpathtypes1);
      if (this.isDiagonalValid(p_77641_, node1, node, node6)) {
         p_77640_[i++] = node6;
      }

      Node node7 = this.findAcceptedNode(p_77641_.x + 1, p_77641_.y, p_77641_.z + 1, j, d0, Direction.SOUTH, blockpathtypes1);
      if (this.isDiagonalValid(p_77641_, node2, node, node7)) {
         p_77640_[i++] = node7;
      }

      return i;
   }

   protected boolean isNeighborValid(@Nullable Node p_77627_, Node p_77628_) {
      return p_77627_ != null && !p_77627_.closed && (p_77627_.costMalus >= 0.0F || p_77628_.costMalus < 0.0F);
   }

   protected boolean isDiagonalValid(Node p_77630_, @Nullable Node p_77631_, @Nullable Node p_77632_, @Nullable Node p_77633_) {
      if (p_77633_ != null && p_77632_ != null && p_77631_ != null) {
         if (p_77633_.closed) {
            return false;
         } else if (p_77632_.y <= p_77630_.y && p_77631_.y <= p_77630_.y) {
            if (p_77631_.type != BlockPathTypes.WALKABLE_DOOR && p_77632_.type != BlockPathTypes.WALKABLE_DOOR && p_77633_.type != BlockPathTypes.WALKABLE_DOOR) {
               boolean flag = p_77632_.type == BlockPathTypes.FENCE && p_77631_.type == BlockPathTypes.FENCE && (double)this.mob.getBbWidth() < 0.5D;
               return p_77633_.costMalus >= 0.0F && (p_77632_.y < p_77630_.y || p_77632_.costMalus >= 0.0F || flag) && (p_77631_.y < p_77630_.y || p_77631_.costMalus >= 0.0F || flag);
            } else {
               return false;
            }
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   private boolean canReachWithoutCollision(Node p_77625_) {
      Vec3 vec3 = new Vec3((double)p_77625_.x - this.mob.getX(), (double)p_77625_.y - this.mob.getY(), (double)p_77625_.z - this.mob.getZ());
      AABB aabb = this.mob.getBoundingBox();
      int i = Mth.ceil(vec3.length() / aabb.getSize());
      vec3 = vec3.scale((double)(1.0F / (float)i));

      for(int j = 1; j <= i; ++j) {
         aabb = aabb.move(vec3);
         if (this.hasCollisions(aabb)) {
            return false;
         }
      }

      return true;
   }

   protected double getFloorLevel(BlockPos p_164733_) {
      return getFloorLevel(this.level, p_164733_);
   }

   public static double getFloorLevel(BlockGetter p_77612_, BlockPos p_77613_) {
      BlockPos blockpos = p_77613_.below();
      VoxelShape voxelshape = p_77612_.getBlockState(blockpos).getCollisionShape(p_77612_, blockpos);
      return (double)blockpos.getY() + (voxelshape.isEmpty() ? 0.0D : voxelshape.max(Direction.Axis.Y));
   }

   protected boolean isAmphibious() {
      return false;
   }

   @Nullable
   protected Node findAcceptedNode(int p_164726_, int p_164727_, int p_164728_, int p_164729_, double p_164730_, Direction p_164731_, BlockPathTypes p_164732_) {
      Node node = null;
      BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();
      double d0 = this.getFloorLevel(blockpos$mutableblockpos.set(p_164726_, p_164727_, p_164728_));
      if (d0 - p_164730_ > 1.125D) {
         return null;
      } else {
         BlockPathTypes blockpathtypes = this.getCachedBlockType(this.mob, p_164726_, p_164727_, p_164728_);
         float f = this.mob.getPathfindingMalus(blockpathtypes);
         double d1 = (double)this.mob.getBbWidth() / 2.0D;
         if (f >= 0.0F) {
            node = this.getNode(p_164726_, p_164727_, p_164728_);
            node.type = blockpathtypes;
            node.costMalus = Math.max(node.costMalus, f);
         }

         if (p_164732_ == BlockPathTypes.FENCE && node != null && node.costMalus >= 0.0F && !this.canReachWithoutCollision(node)) {
            node = null;
         }

         if (blockpathtypes != BlockPathTypes.WALKABLE && (!this.isAmphibious() || blockpathtypes != BlockPathTypes.WATER)) {
            if ((node == null || node.costMalus < 0.0F) && p_164729_ > 0 && blockpathtypes != BlockPathTypes.FENCE && blockpathtypes != BlockPathTypes.UNPASSABLE_RAIL && blockpathtypes != BlockPathTypes.TRAPDOOR && blockpathtypes != BlockPathTypes.POWDER_SNOW) {
               node = this.findAcceptedNode(p_164726_, p_164727_ + 1, p_164728_, p_164729_ - 1, p_164730_, p_164731_, p_164732_);
               if (node != null && (node.type == BlockPathTypes.OPEN || node.type == BlockPathTypes.WALKABLE) && this.mob.getBbWidth() < 1.0F) {
                  double d2 = (double)(p_164726_ - p_164731_.getStepX()) + 0.5D;
                  double d3 = (double)(p_164728_ - p_164731_.getStepZ()) + 0.5D;
                  AABB aabb = new AABB(d2 - d1, getFloorLevel(this.level, blockpos$mutableblockpos.set(d2, (double)(p_164727_ + 1), d3)) + 0.001D, d3 - d1, d2 + d1, (double)this.mob.getBbHeight() + getFloorLevel(this.level, blockpos$mutableblockpos.set((double)node.x, (double)node.y, (double)node.z)) - 0.002D, d3 + d1);
                  if (this.hasCollisions(aabb)) {
                     node = null;
                  }
               }
            }

            if (!this.isAmphibious() && blockpathtypes == BlockPathTypes.WATER && !this.canFloat()) {
               if (this.getCachedBlockType(this.mob, p_164726_, p_164727_ - 1, p_164728_) != BlockPathTypes.WATER) {
                  return node;
               }

               while(p_164727_ > this.mob.level.getMinBuildHeight()) {
                  --p_164727_;
                  blockpathtypes = this.getCachedBlockType(this.mob, p_164726_, p_164727_, p_164728_);
                  if (blockpathtypes != BlockPathTypes.WATER) {
                     return node;
                  }

                  node = this.getNode(p_164726_, p_164727_, p_164728_);
                  node.type = blockpathtypes;
                  node.costMalus = Math.max(node.costMalus, this.mob.getPathfindingMalus(blockpathtypes));
               }
            }

            if (blockpathtypes == BlockPathTypes.OPEN) {
               int j = 0;
               int i = p_164727_;

               while(blockpathtypes == BlockPathTypes.OPEN) {
                  --p_164727_;
                  if (p_164727_ < this.mob.level.getMinBuildHeight()) {
                     Node node3 = this.getNode(p_164726_, i, p_164728_);
                     node3.type = BlockPathTypes.BLOCKED;
                     node3.costMalus = -1.0F;
                     return node3;
                  }

                  if (j++ >= this.mob.getMaxFallDistance()) {
                     Node node2 = this.getNode(p_164726_, p_164727_, p_164728_);
                     node2.type = BlockPathTypes.BLOCKED;
                     node2.costMalus = -1.0F;
                     return node2;
                  }

                  blockpathtypes = this.getCachedBlockType(this.mob, p_164726_, p_164727_, p_164728_);
                  f = this.mob.getPathfindingMalus(blockpathtypes);
                  if (blockpathtypes != BlockPathTypes.OPEN && f >= 0.0F) {
                     node = this.getNode(p_164726_, p_164727_, p_164728_);
                     node.type = blockpathtypes;
                     node.costMalus = Math.max(node.costMalus, f);
                     break;
                  }

                  if (f < 0.0F) {
                     Node node1 = this.getNode(p_164726_, p_164727_, p_164728_);
                     node1.type = BlockPathTypes.BLOCKED;
                     node1.costMalus = -1.0F;
                     return node1;
                  }
               }
            }

            if (blockpathtypes == BlockPathTypes.FENCE) {
               node = this.getNode(p_164726_, p_164727_, p_164728_);
               node.closed = true;
               node.type = blockpathtypes;
               node.costMalus = blockpathtypes.getMalus();
            }

            return node;
         } else {
            return node;
         }
      }
   }

   private boolean hasCollisions(AABB p_77635_) {
      return this.collisionCache.computeIfAbsent(p_77635_, (p_192973_) -> {
         return !this.level.noCollision(this.mob, p_77635_);
      });
   }

   public BlockPathTypes getBlockPathType(BlockGetter p_77594_, int p_77595_, int p_77596_, int p_77597_, Mob p_77598_, int p_77599_, int p_77600_, int p_77601_, boolean p_77602_, boolean p_77603_) {
      EnumSet<BlockPathTypes> enumset = EnumSet.noneOf(BlockPathTypes.class);
      BlockPathTypes blockpathtypes = BlockPathTypes.BLOCKED;
      BlockPos blockpos = p_77598_.blockPosition();
      blockpathtypes = this.getBlockPathTypes(p_77594_, p_77595_, p_77596_, p_77597_, p_77599_, p_77600_, p_77601_, p_77602_, p_77603_, enumset, blockpathtypes, blockpos);
      if (enumset.contains(BlockPathTypes.FENCE)) {
         return BlockPathTypes.FENCE;
      } else if (enumset.contains(BlockPathTypes.UNPASSABLE_RAIL)) {
         return BlockPathTypes.UNPASSABLE_RAIL;
      } else {
         BlockPathTypes blockpathtypes1 = BlockPathTypes.BLOCKED;

         for(BlockPathTypes blockpathtypes2 : enumset) {
            if (p_77598_.getPathfindingMalus(blockpathtypes2) < 0.0F) {
               return blockpathtypes2;
            }

            if (p_77598_.getPathfindingMalus(blockpathtypes2) >= p_77598_.getPathfindingMalus(blockpathtypes1)) {
               blockpathtypes1 = blockpathtypes2;
            }
         }

         return blockpathtypes == BlockPathTypes.OPEN && p_77598_.getPathfindingMalus(blockpathtypes1) == 0.0F && p_77599_ <= 1 ? BlockPathTypes.OPEN : blockpathtypes1;
      }
   }

   public BlockPathTypes getBlockPathTypes(BlockGetter p_77581_, int p_77582_, int p_77583_, int p_77584_, int p_77585_, int p_77586_, int p_77587_, boolean p_77588_, boolean p_77589_, EnumSet<BlockPathTypes> p_77590_, BlockPathTypes p_77591_, BlockPos p_77592_) {
      for(int i = 0; i < p_77585_; ++i) {
         for(int j = 0; j < p_77586_; ++j) {
            for(int k = 0; k < p_77587_; ++k) {
               int l = i + p_77582_;
               int i1 = j + p_77583_;
               int j1 = k + p_77584_;
               BlockPathTypes blockpathtypes = this.getBlockPathType(p_77581_, l, i1, j1);
               blockpathtypes = this.evaluateBlockPathType(p_77581_, p_77588_, p_77589_, p_77592_, blockpathtypes);
               if (i == 0 && j == 0 && k == 0) {
                  p_77591_ = blockpathtypes;
               }

               p_77590_.add(blockpathtypes);
            }
         }
      }

      return p_77591_;
   }

   protected BlockPathTypes evaluateBlockPathType(BlockGetter p_77614_, boolean p_77615_, boolean p_77616_, BlockPos p_77617_, BlockPathTypes p_77618_) {
      if (p_77618_ == BlockPathTypes.DOOR_WOOD_CLOSED && p_77615_ && p_77616_) {
         p_77618_ = BlockPathTypes.WALKABLE_DOOR;
      }

      if (p_77618_ == BlockPathTypes.DOOR_OPEN && !p_77616_) {
         p_77618_ = BlockPathTypes.BLOCKED;
      }

      if (p_77618_ == BlockPathTypes.RAIL && !(p_77614_.getBlockState(p_77617_).getBlock() instanceof BaseRailBlock) && !(p_77614_.getBlockState(p_77617_.below()).getBlock() instanceof BaseRailBlock)) {
         p_77618_ = BlockPathTypes.UNPASSABLE_RAIL;
      }

      if (p_77618_ == BlockPathTypes.LEAVES) {
         p_77618_ = BlockPathTypes.BLOCKED;
      }

      return p_77618_;
   }

   private BlockPathTypes getBlockPathType(Mob p_77573_, BlockPos p_77574_) {
      return this.getCachedBlockType(p_77573_, p_77574_.getX(), p_77574_.getY(), p_77574_.getZ());
   }

   protected BlockPathTypes getCachedBlockType(Mob p_77568_, int p_77569_, int p_77570_, int p_77571_) {
      return this.pathTypesByPosCache.computeIfAbsent(BlockPos.asLong(p_77569_, p_77570_, p_77571_), (p_77566_) -> {
         return this.getBlockPathType(this.level, p_77569_, p_77570_, p_77571_, p_77568_, this.entityWidth, this.entityHeight, this.entityDepth, this.canOpenDoors(), this.canPassDoors());
      });
   }

   public BlockPathTypes getBlockPathType(BlockGetter p_77576_, int p_77577_, int p_77578_, int p_77579_) {
      return getBlockPathTypeStatic(p_77576_, new BlockPos.MutableBlockPos(p_77577_, p_77578_, p_77579_));
   }

   public static BlockPathTypes getBlockPathTypeStatic(BlockGetter p_77605_, BlockPos.MutableBlockPos p_77606_) {
      int i = p_77606_.getX();
      int j = p_77606_.getY();
      int k = p_77606_.getZ();
      BlockPathTypes blockpathtypes = getBlockPathTypeRaw(p_77605_, p_77606_);
      if (blockpathtypes == BlockPathTypes.OPEN && j >= p_77605_.getMinBuildHeight() + 1) {
         BlockPathTypes blockpathtypes1 = getBlockPathTypeRaw(p_77605_, p_77606_.set(i, j - 1, k));
         blockpathtypes = blockpathtypes1 != BlockPathTypes.WALKABLE && blockpathtypes1 != BlockPathTypes.OPEN && blockpathtypes1 != BlockPathTypes.WATER && blockpathtypes1 != BlockPathTypes.LAVA ? BlockPathTypes.WALKABLE : BlockPathTypes.OPEN;
         if (blockpathtypes1 == BlockPathTypes.DAMAGE_FIRE) {
            blockpathtypes = BlockPathTypes.DAMAGE_FIRE;
         }

         if (blockpathtypes1 == BlockPathTypes.DAMAGE_CACTUS) {
            blockpathtypes = BlockPathTypes.DAMAGE_CACTUS;
         }

         if (blockpathtypes1 == BlockPathTypes.DAMAGE_OTHER) {
            blockpathtypes = BlockPathTypes.DAMAGE_OTHER;
         }

         if (blockpathtypes1 == BlockPathTypes.STICKY_HONEY) {
            blockpathtypes = BlockPathTypes.STICKY_HONEY;
         }

         if (blockpathtypes1 == BlockPathTypes.POWDER_SNOW) {
            blockpathtypes = BlockPathTypes.DANGER_POWDER_SNOW;
         }
      }

      if (blockpathtypes == BlockPathTypes.WALKABLE) {
         blockpathtypes = checkNeighbourBlocks(p_77605_, p_77606_.set(i, j, k), blockpathtypes);
      }

      return blockpathtypes;
   }

   public static BlockPathTypes checkNeighbourBlocks(BlockGetter p_77608_, BlockPos.MutableBlockPos p_77609_, BlockPathTypes p_77610_) {
      int i = p_77609_.getX();
      int j = p_77609_.getY();
      int k = p_77609_.getZ();

      for(int l = -1; l <= 1; ++l) {
         for(int i1 = -1; i1 <= 1; ++i1) {
            for(int j1 = -1; j1 <= 1; ++j1) {
               if (l != 0 || j1 != 0) {
                  p_77609_.set(i + l, j + i1, k + j1);
                  BlockState blockstate = p_77608_.getBlockState(p_77609_);
                  if (blockstate.is(Blocks.CACTUS)) {
                     return BlockPathTypes.DANGER_CACTUS;
                  }

                  if (blockstate.is(Blocks.SWEET_BERRY_BUSH)) {
                     return BlockPathTypes.DANGER_OTHER;
                  }

                  if (isBurningBlock(blockstate)) {
                     return BlockPathTypes.DANGER_FIRE;
                  }

                  if (p_77608_.getFluidState(p_77609_).is(FluidTags.WATER)) {
                     return BlockPathTypes.WATER_BORDER;
                  }
               }
            }
         }
      }

      return p_77610_;
   }

   protected static BlockPathTypes getBlockPathTypeRaw(BlockGetter p_77644_, BlockPos p_77645_) {
      BlockState blockstate = p_77644_.getBlockState(p_77645_);
      BlockPathTypes type = blockstate.getBlockPathType(p_77644_, p_77645_);
      if (type != null) return type;
      Block block = blockstate.getBlock();
      Material material = blockstate.getMaterial();
      if (blockstate.isAir()) {
         return BlockPathTypes.OPEN;
      } else if (!blockstate.is(BlockTags.TRAPDOORS) && !blockstate.is(Blocks.LILY_PAD) && !blockstate.is(Blocks.BIG_DRIPLEAF)) {
         if (blockstate.is(Blocks.POWDER_SNOW)) {
            return BlockPathTypes.POWDER_SNOW;
         } else if (blockstate.is(Blocks.CACTUS)) {
            return BlockPathTypes.DAMAGE_CACTUS;
         } else if (blockstate.is(Blocks.SWEET_BERRY_BUSH)) {
            return BlockPathTypes.DAMAGE_OTHER;
         } else if (blockstate.is(Blocks.HONEY_BLOCK)) {
            return BlockPathTypes.STICKY_HONEY;
         } else if (blockstate.is(Blocks.COCOA)) {
            return BlockPathTypes.COCOA;
         } else {
            FluidState fluidstate = p_77644_.getFluidState(p_77645_);
            if (fluidstate.is(FluidTags.LAVA)) {
               return BlockPathTypes.LAVA;
            } else if (isBurningBlock(blockstate)) {
               return BlockPathTypes.DAMAGE_FIRE;
            } else if (DoorBlock.isWoodenDoor(blockstate) && !blockstate.getValue(DoorBlock.OPEN)) {
               return BlockPathTypes.DOOR_WOOD_CLOSED;
            } else if (block instanceof DoorBlock && material == Material.METAL && !blockstate.getValue(DoorBlock.OPEN)) {
               return BlockPathTypes.DOOR_IRON_CLOSED;
            } else if (block instanceof DoorBlock && blockstate.getValue(DoorBlock.OPEN)) {
               return BlockPathTypes.DOOR_OPEN;
            } else if (block instanceof BaseRailBlock) {
               return BlockPathTypes.RAIL;
            } else if (block instanceof LeavesBlock) {
               return BlockPathTypes.LEAVES;
            } else if (!blockstate.is(BlockTags.FENCES) && !blockstate.is(BlockTags.WALLS) && (!(block instanceof FenceGateBlock) || blockstate.getValue(FenceGateBlock.OPEN))) {
               if (!blockstate.isPathfindable(p_77644_, p_77645_, PathComputationType.LAND)) {
                  return BlockPathTypes.BLOCKED;
               } else {
                  return fluidstate.is(FluidTags.WATER) ? BlockPathTypes.WATER : BlockPathTypes.OPEN;
               }
            } else {
               return BlockPathTypes.FENCE;
            }
         }
      } else {
         return BlockPathTypes.TRAPDOOR;
      }
   }

   public static boolean isBurningBlock(BlockState p_77623_) {
      return p_77623_.is(BlockTags.FIRE) || p_77623_.is(Blocks.LAVA) || p_77623_.is(Blocks.MAGMA_BLOCK) || CampfireBlock.isLitCampfire(p_77623_) || p_77623_.is(Blocks.LAVA_CAULDRON);
   }
}
