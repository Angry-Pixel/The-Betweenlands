package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public enum SupportType {
   FULL {
      public boolean isSupporting(BlockState p_57220_, BlockGetter p_57221_, BlockPos p_57222_, Direction p_57223_) {
         return Block.isFaceFull(p_57220_.getBlockSupportShape(p_57221_, p_57222_), p_57223_);
      }
   },
   CENTER {
      private final int CENTER_SUPPORT_WIDTH = 1;
      private final VoxelShape CENTER_SUPPORT_SHAPE = Block.box(7.0D, 0.0D, 7.0D, 9.0D, 10.0D, 9.0D);

      public boolean isSupporting(BlockState p_57230_, BlockGetter p_57231_, BlockPos p_57232_, Direction p_57233_) {
         return !Shapes.joinIsNotEmpty(p_57230_.getBlockSupportShape(p_57231_, p_57232_).getFaceShape(p_57233_), this.CENTER_SUPPORT_SHAPE, BooleanOp.ONLY_SECOND);
      }
   },
   RIGID {
      private final int RIGID_SUPPORT_WIDTH = 2;
      private final VoxelShape RIGID_SUPPORT_SHAPE = Shapes.join(Shapes.block(), Block.box(2.0D, 0.0D, 2.0D, 14.0D, 16.0D, 14.0D), BooleanOp.ONLY_FIRST);

      public boolean isSupporting(BlockState p_57240_, BlockGetter p_57241_, BlockPos p_57242_, Direction p_57243_) {
         return !Shapes.joinIsNotEmpty(p_57240_.getBlockSupportShape(p_57241_, p_57242_).getFaceShape(p_57243_), this.RIGID_SUPPORT_SHAPE, BooleanOp.ONLY_SECOND);
      }
   };

   public abstract boolean isSupporting(BlockState p_57209_, BlockGetter p_57210_, BlockPos p_57211_, Direction p_57212_);
}