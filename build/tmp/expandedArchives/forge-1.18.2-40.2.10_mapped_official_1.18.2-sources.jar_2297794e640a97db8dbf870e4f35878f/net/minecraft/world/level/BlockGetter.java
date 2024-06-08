package net.minecraft.world.level;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

public interface BlockGetter extends LevelHeightAccessor, net.minecraftforge.common.extensions.IForgeBlockGetter {
   @Nullable
   BlockEntity getBlockEntity(BlockPos p_45570_);

   default <T extends BlockEntity> Optional<T> getBlockEntity(BlockPos p_151367_, BlockEntityType<T> p_151368_) {
      BlockEntity blockentity = this.getBlockEntity(p_151367_);
      return blockentity != null && blockentity.getType() == p_151368_ ? Optional.of((T)blockentity) : Optional.empty();
   }

   BlockState getBlockState(BlockPos p_45571_);

   FluidState getFluidState(BlockPos p_45569_);

   default int getLightEmission(BlockPos p_45572_) {
      return this.getBlockState(p_45572_).getLightEmission(this, p_45572_);
   }

   default int getMaxLightLevel() {
      return 15;
   }

   default Stream<BlockState> getBlockStates(AABB p_45557_) {
      return BlockPos.betweenClosedStream(p_45557_).map(this::getBlockState);
   }

   default BlockHitResult isBlockInLine(ClipBlockStateContext p_151354_) {
      return traverseBlocks(p_151354_.getFrom(), p_151354_.getTo(), p_151354_, (p_151356_, p_151357_) -> {
         BlockState blockstate = this.getBlockState(p_151357_);
         Vec3 vec3 = p_151356_.getFrom().subtract(p_151356_.getTo());
         return p_151356_.isTargetBlock().test(blockstate) ? new BlockHitResult(p_151356_.getTo(), Direction.getNearest(vec3.x, vec3.y, vec3.z), new BlockPos(p_151356_.getTo()), false) : null;
      }, (p_151370_) -> {
         Vec3 vec3 = p_151370_.getFrom().subtract(p_151370_.getTo());
         return BlockHitResult.miss(p_151370_.getTo(), Direction.getNearest(vec3.x, vec3.y, vec3.z), new BlockPos(p_151370_.getTo()));
      });
   }

   default BlockHitResult clip(ClipContext p_45548_) {
      return traverseBlocks(p_45548_.getFrom(), p_45548_.getTo(), p_45548_, (p_151359_, p_151360_) -> {
         BlockState blockstate = this.getBlockState(p_151360_);
         FluidState fluidstate = this.getFluidState(p_151360_);
         Vec3 vec3 = p_151359_.getFrom();
         Vec3 vec31 = p_151359_.getTo();
         VoxelShape voxelshape = p_151359_.getBlockShape(blockstate, this, p_151360_);
         BlockHitResult blockhitresult = this.clipWithInteractionOverride(vec3, vec31, p_151360_, voxelshape, blockstate);
         VoxelShape voxelshape1 = p_151359_.getFluidShape(fluidstate, this, p_151360_);
         BlockHitResult blockhitresult1 = voxelshape1.clip(vec3, vec31, p_151360_);
         double d0 = blockhitresult == null ? Double.MAX_VALUE : p_151359_.getFrom().distanceToSqr(blockhitresult.getLocation());
         double d1 = blockhitresult1 == null ? Double.MAX_VALUE : p_151359_.getFrom().distanceToSqr(blockhitresult1.getLocation());
         return d0 <= d1 ? blockhitresult : blockhitresult1;
      }, (p_151372_) -> {
         Vec3 vec3 = p_151372_.getFrom().subtract(p_151372_.getTo());
         return BlockHitResult.miss(p_151372_.getTo(), Direction.getNearest(vec3.x, vec3.y, vec3.z), new BlockPos(p_151372_.getTo()));
      });
   }

   @Nullable
   default BlockHitResult clipWithInteractionOverride(Vec3 p_45559_, Vec3 p_45560_, BlockPos p_45561_, VoxelShape p_45562_, BlockState p_45563_) {
      BlockHitResult blockhitresult = p_45562_.clip(p_45559_, p_45560_, p_45561_);
      if (blockhitresult != null) {
         BlockHitResult blockhitresult1 = p_45563_.getInteractionShape(this, p_45561_).clip(p_45559_, p_45560_, p_45561_);
         if (blockhitresult1 != null && blockhitresult1.getLocation().subtract(p_45559_).lengthSqr() < blockhitresult.getLocation().subtract(p_45559_).lengthSqr()) {
            return blockhitresult.withDirection(blockhitresult1.getDirection());
         }
      }

      return blockhitresult;
   }

   default double getBlockFloorHeight(VoxelShape p_45565_, Supplier<VoxelShape> p_45566_) {
      if (!p_45565_.isEmpty()) {
         return p_45565_.max(Direction.Axis.Y);
      } else {
         double d0 = p_45566_.get().max(Direction.Axis.Y);
         return d0 >= 1.0D ? d0 - 1.0D : Double.NEGATIVE_INFINITY;
      }
   }

   default double getBlockFloorHeight(BlockPos p_45574_) {
      return this.getBlockFloorHeight(this.getBlockState(p_45574_).getCollisionShape(this, p_45574_), () -> {
         BlockPos blockpos = p_45574_.below();
         return this.getBlockState(blockpos).getCollisionShape(this, blockpos);
      });
   }

   static <T, C> T traverseBlocks(Vec3 p_151362_, Vec3 p_151363_, C p_151364_, BiFunction<C, BlockPos, T> p_151365_, Function<C, T> p_151366_) {
      if (p_151362_.equals(p_151363_)) {
         return p_151366_.apply(p_151364_);
      } else {
         double d0 = Mth.lerp(-1.0E-7D, p_151363_.x, p_151362_.x);
         double d1 = Mth.lerp(-1.0E-7D, p_151363_.y, p_151362_.y);
         double d2 = Mth.lerp(-1.0E-7D, p_151363_.z, p_151362_.z);
         double d3 = Mth.lerp(-1.0E-7D, p_151362_.x, p_151363_.x);
         double d4 = Mth.lerp(-1.0E-7D, p_151362_.y, p_151363_.y);
         double d5 = Mth.lerp(-1.0E-7D, p_151362_.z, p_151363_.z);
         int i = Mth.floor(d3);
         int j = Mth.floor(d4);
         int k = Mth.floor(d5);
         BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos(i, j, k);
         T t = p_151365_.apply(p_151364_, blockpos$mutableblockpos);
         if (t != null) {
            return t;
         } else {
            double d6 = d0 - d3;
            double d7 = d1 - d4;
            double d8 = d2 - d5;
            int l = Mth.sign(d6);
            int i1 = Mth.sign(d7);
            int j1 = Mth.sign(d8);
            double d9 = l == 0 ? Double.MAX_VALUE : (double)l / d6;
            double d10 = i1 == 0 ? Double.MAX_VALUE : (double)i1 / d7;
            double d11 = j1 == 0 ? Double.MAX_VALUE : (double)j1 / d8;
            double d12 = d9 * (l > 0 ? 1.0D - Mth.frac(d3) : Mth.frac(d3));
            double d13 = d10 * (i1 > 0 ? 1.0D - Mth.frac(d4) : Mth.frac(d4));
            double d14 = d11 * (j1 > 0 ? 1.0D - Mth.frac(d5) : Mth.frac(d5));

            while(d12 <= 1.0D || d13 <= 1.0D || d14 <= 1.0D) {
               if (d12 < d13) {
                  if (d12 < d14) {
                     i += l;
                     d12 += d9;
                  } else {
                     k += j1;
                     d14 += d11;
                  }
               } else if (d13 < d14) {
                  j += i1;
                  d13 += d10;
               } else {
                  k += j1;
                  d14 += d11;
               }

               T t1 = p_151365_.apply(p_151364_, blockpos$mutableblockpos.set(i, j, k));
               if (t1 != null) {
                  return t1;
               }
            }

            return p_151366_.apply(p_151364_);
         }
      }
   }
}
