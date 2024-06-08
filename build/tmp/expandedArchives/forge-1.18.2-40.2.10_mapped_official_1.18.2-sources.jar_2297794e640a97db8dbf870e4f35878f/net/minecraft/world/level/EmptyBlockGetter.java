package net.minecraft.world.level;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;

public enum EmptyBlockGetter implements BlockGetter {
   INSTANCE;

   @Nullable
   public BlockEntity getBlockEntity(BlockPos p_45867_) {
      return null;
   }

   public BlockState getBlockState(BlockPos p_45869_) {
      return Blocks.AIR.defaultBlockState();
   }

   public FluidState getFluidState(BlockPos p_45865_) {
      return Fluids.EMPTY.defaultFluidState();
   }

   public int getMinBuildHeight() {
      return 0;
   }

   public int getHeight() {
      return 0;
   }
}