package net.minecraft.world.level.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

public class TheEndPortalBlockEntity extends BlockEntity {
   protected TheEndPortalBlockEntity(BlockEntityType<?> p_155855_, BlockPos p_155856_, BlockState p_155857_) {
      super(p_155855_, p_155856_, p_155857_);
   }

   public TheEndPortalBlockEntity(BlockPos p_155859_, BlockState p_155860_) {
      this(BlockEntityType.END_PORTAL, p_155859_, p_155860_);
   }

   public boolean shouldRenderFace(Direction p_59980_) {
      return p_59980_.getAxis() == Direction.Axis.Y;
   }
}