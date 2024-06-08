package net.minecraft.world.entity.vehicle;

import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.CollisionGetter;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class DismountHelper {
   public static int[][] offsetsForDirection(Direction p_38468_) {
      Direction direction = p_38468_.getClockWise();
      Direction direction1 = direction.getOpposite();
      Direction direction2 = p_38468_.getOpposite();
      return new int[][]{{direction.getStepX(), direction.getStepZ()}, {direction1.getStepX(), direction1.getStepZ()}, {direction2.getStepX() + direction.getStepX(), direction2.getStepZ() + direction.getStepZ()}, {direction2.getStepX() + direction1.getStepX(), direction2.getStepZ() + direction1.getStepZ()}, {p_38468_.getStepX() + direction.getStepX(), p_38468_.getStepZ() + direction.getStepZ()}, {p_38468_.getStepX() + direction1.getStepX(), p_38468_.getStepZ() + direction1.getStepZ()}, {direction2.getStepX(), direction2.getStepZ()}, {p_38468_.getStepX(), p_38468_.getStepZ()}};
   }

   public static boolean isBlockFloorValid(double p_38440_) {
      return !Double.isInfinite(p_38440_) && p_38440_ < 1.0D;
   }

   public static boolean canDismountTo(CollisionGetter p_38457_, LivingEntity p_38458_, AABB p_38459_) {
      for(VoxelShape voxelshape : p_38457_.getBlockCollisions(p_38458_, p_38459_)) {
         if (!voxelshape.isEmpty()) {
            return false;
         }
      }

      return p_38457_.getWorldBorder().isWithinBounds(p_38459_);
   }

   public static boolean canDismountTo(CollisionGetter p_150280_, Vec3 p_150281_, LivingEntity p_150282_, Pose p_150283_) {
      return canDismountTo(p_150280_, p_150282_, p_150282_.getLocalBoundsForPose(p_150283_).move(p_150281_));
   }

   public static VoxelShape nonClimbableShape(BlockGetter p_38447_, BlockPos p_38448_) {
      BlockState blockstate = p_38447_.getBlockState(p_38448_);
      return !blockstate.is(BlockTags.CLIMBABLE) && (!(blockstate.getBlock() instanceof TrapDoorBlock) || !blockstate.getValue(TrapDoorBlock.OPEN)) ? blockstate.getCollisionShape(p_38447_, p_38448_) : Shapes.empty();
   }

   public static double findCeilingFrom(BlockPos p_38464_, int p_38465_, Function<BlockPos, VoxelShape> p_38466_) {
      BlockPos.MutableBlockPos blockpos$mutableblockpos = p_38464_.mutable();
      int i = 0;

      while(i < p_38465_) {
         VoxelShape voxelshape = p_38466_.apply(blockpos$mutableblockpos);
         if (!voxelshape.isEmpty()) {
            return (double)(p_38464_.getY() + i) + voxelshape.min(Direction.Axis.Y);
         }

         ++i;
         blockpos$mutableblockpos.move(Direction.UP);
      }

      return Double.POSITIVE_INFINITY;
   }

   @Nullable
   public static Vec3 findSafeDismountLocation(EntityType<?> p_38442_, CollisionGetter p_38443_, BlockPos p_38444_, boolean p_38445_) {
      if (p_38445_ && p_38442_.isBlockDangerous(p_38443_.getBlockState(p_38444_))) {
         return null;
      } else {
         double d0 = p_38443_.getBlockFloorHeight(nonClimbableShape(p_38443_, p_38444_), () -> {
            return nonClimbableShape(p_38443_, p_38444_.below());
         });
         if (!isBlockFloorValid(d0)) {
            return null;
         } else if (p_38445_ && d0 <= 0.0D && p_38442_.isBlockDangerous(p_38443_.getBlockState(p_38444_.below()))) {
            return null;
         } else {
            Vec3 vec3 = Vec3.upFromBottomCenterOf(p_38444_, d0);
            AABB aabb = p_38442_.getDimensions().makeBoundingBox(vec3);

            for(VoxelShape voxelshape : p_38443_.getBlockCollisions((Entity)null, aabb)) {
               if (!voxelshape.isEmpty()) {
                  return null;
               }
            }

            return !p_38443_.getWorldBorder().isWithinBounds(aabb) ? null : vec3;
         }
      }
   }
}