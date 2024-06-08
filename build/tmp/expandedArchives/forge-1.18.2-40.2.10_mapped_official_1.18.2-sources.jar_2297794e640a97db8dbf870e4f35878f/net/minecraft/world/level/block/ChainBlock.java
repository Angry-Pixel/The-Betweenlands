package net.minecraft.world.level.block;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ChainBlock extends RotatedPillarBlock implements SimpleWaterloggedBlock {
   public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
   protected static final float AABB_MIN = 6.5F;
   protected static final float AABB_MAX = 9.5F;
   protected static final VoxelShape Y_AXIS_AABB = Block.box(6.5D, 0.0D, 6.5D, 9.5D, 16.0D, 9.5D);
   protected static final VoxelShape Z_AXIS_AABB = Block.box(6.5D, 6.5D, 0.0D, 9.5D, 9.5D, 16.0D);
   protected static final VoxelShape X_AXIS_AABB = Block.box(0.0D, 6.5D, 6.5D, 16.0D, 9.5D, 9.5D);

   public ChainBlock(BlockBehaviour.Properties p_51452_) {
      super(p_51452_);
      this.registerDefaultState(this.stateDefinition.any().setValue(WATERLOGGED, Boolean.valueOf(false)).setValue(AXIS, Direction.Axis.Y));
   }

   public VoxelShape getShape(BlockState p_51470_, BlockGetter p_51471_, BlockPos p_51472_, CollisionContext p_51473_) {
      switch((Direction.Axis)p_51470_.getValue(AXIS)) {
      case X:
      default:
         return X_AXIS_AABB;
      case Z:
         return Z_AXIS_AABB;
      case Y:
         return Y_AXIS_AABB;
      }
   }

   @Nullable
   public BlockState getStateForPlacement(BlockPlaceContext p_51454_) {
      FluidState fluidstate = p_51454_.getLevel().getFluidState(p_51454_.getClickedPos());
      boolean flag = fluidstate.getType() == Fluids.WATER;
      return super.getStateForPlacement(p_51454_).setValue(WATERLOGGED, Boolean.valueOf(flag));
   }

   public BlockState updateShape(BlockState p_51461_, Direction p_51462_, BlockState p_51463_, LevelAccessor p_51464_, BlockPos p_51465_, BlockPos p_51466_) {
      if (p_51461_.getValue(WATERLOGGED)) {
         p_51464_.scheduleTick(p_51465_, Fluids.WATER, Fluids.WATER.getTickDelay(p_51464_));
      }

      return super.updateShape(p_51461_, p_51462_, p_51463_, p_51464_, p_51465_, p_51466_);
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_51468_) {
      p_51468_.add(WATERLOGGED).add(AXIS);
   }

   public FluidState getFluidState(BlockState p_51475_) {
      return p_51475_.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(p_51475_);
   }

   public boolean isPathfindable(BlockState p_51456_, BlockGetter p_51457_, BlockPos p_51458_, PathComputationType p_51459_) {
      return false;
   }
}