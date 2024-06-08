package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class RodBlock extends DirectionalBlock {
   protected static final float AABB_MIN = 6.0F;
   protected static final float AABB_MAX = 10.0F;
   protected static final VoxelShape Y_AXIS_AABB = Block.box(6.0D, 0.0D, 6.0D, 10.0D, 16.0D, 10.0D);
   protected static final VoxelShape Z_AXIS_AABB = Block.box(6.0D, 6.0D, 0.0D, 10.0D, 10.0D, 16.0D);
   protected static final VoxelShape X_AXIS_AABB = Block.box(0.0D, 6.0D, 6.0D, 16.0D, 10.0D, 10.0D);

   public RodBlock(BlockBehaviour.Properties p_154339_) {
      super(p_154339_);
   }

   public VoxelShape getShape(BlockState p_154346_, BlockGetter p_154347_, BlockPos p_154348_, CollisionContext p_154349_) {
      switch(p_154346_.getValue(FACING).getAxis()) {
      case X:
      default:
         return X_AXIS_AABB;
      case Z:
         return Z_AXIS_AABB;
      case Y:
         return Y_AXIS_AABB;
      }
   }

   public BlockState rotate(BlockState p_154354_, Rotation p_154355_) {
      return p_154354_.setValue(FACING, p_154355_.rotate(p_154354_.getValue(FACING)));
   }

   public BlockState mirror(BlockState p_154351_, Mirror p_154352_) {
      return p_154351_.setValue(FACING, p_154352_.mirror(p_154351_.getValue(FACING)));
   }

   public boolean isPathfindable(BlockState p_154341_, BlockGetter p_154342_, BlockPos p_154343_, PathComputationType p_154344_) {
      return false;
   }
}