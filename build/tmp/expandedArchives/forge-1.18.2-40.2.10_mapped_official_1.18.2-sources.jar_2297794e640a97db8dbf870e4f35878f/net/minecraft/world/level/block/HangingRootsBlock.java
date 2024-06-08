package net.minecraft.world.level.block;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class HangingRootsBlock extends Block implements SimpleWaterloggedBlock {
   private static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
   protected static final VoxelShape SHAPE = Block.box(2.0D, 10.0D, 2.0D, 14.0D, 16.0D, 14.0D);

   public HangingRootsBlock(BlockBehaviour.Properties p_153337_) {
      super(p_153337_);
      this.registerDefaultState(this.stateDefinition.any().setValue(WATERLOGGED, Boolean.valueOf(false)));
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_153358_) {
      p_153358_.add(WATERLOGGED);
   }

   public FluidState getFluidState(BlockState p_153360_) {
      return p_153360_.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(p_153360_);
   }

   @Nullable
   public BlockState getStateForPlacement(BlockPlaceContext p_153340_) {
      BlockState blockstate = super.getStateForPlacement(p_153340_);
      if (blockstate != null) {
         FluidState fluidstate = p_153340_.getLevel().getFluidState(p_153340_.getClickedPos());
         return blockstate.setValue(WATERLOGGED, Boolean.valueOf(fluidstate.getType() == Fluids.WATER));
      } else {
         return null;
      }
   }

   public boolean canSurvive(BlockState p_153347_, LevelReader p_153348_, BlockPos p_153349_) {
      BlockPos blockpos = p_153349_.above();
      BlockState blockstate = p_153348_.getBlockState(blockpos);
      return blockstate.isFaceSturdy(p_153348_, blockpos, Direction.DOWN);
   }

   public VoxelShape getShape(BlockState p_153342_, BlockGetter p_153343_, BlockPos p_153344_, CollisionContext p_153345_) {
      return SHAPE;
   }

   public BlockState updateShape(BlockState p_153351_, Direction p_153352_, BlockState p_153353_, LevelAccessor p_153354_, BlockPos p_153355_, BlockPos p_153356_) {
      if (p_153352_ == Direction.UP && !this.canSurvive(p_153351_, p_153354_, p_153355_)) {
         return Blocks.AIR.defaultBlockState();
      } else {
         if (p_153351_.getValue(WATERLOGGED)) {
            p_153354_.scheduleTick(p_153355_, Fluids.WATER, Fluids.WATER.getTickDelay(p_153354_));
         }

         return super.updateShape(p_153351_, p_153352_, p_153353_, p_153354_, p_153355_, p_153356_);
      }
   }

   public BlockBehaviour.OffsetType getOffsetType() {
      return BlockBehaviour.OffsetType.XZ;
   }
}