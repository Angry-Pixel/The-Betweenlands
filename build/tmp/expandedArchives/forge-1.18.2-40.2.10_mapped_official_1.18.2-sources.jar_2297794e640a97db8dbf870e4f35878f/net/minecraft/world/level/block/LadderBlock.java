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
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class LadderBlock extends Block implements SimpleWaterloggedBlock {
   public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
   public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
   protected static final float AABB_OFFSET = 3.0F;
   protected static final VoxelShape EAST_AABB = Block.box(0.0D, 0.0D, 0.0D, 3.0D, 16.0D, 16.0D);
   protected static final VoxelShape WEST_AABB = Block.box(13.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);
   protected static final VoxelShape SOUTH_AABB = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 3.0D);
   protected static final VoxelShape NORTH_AABB = Block.box(0.0D, 0.0D, 13.0D, 16.0D, 16.0D, 16.0D);

   public LadderBlock(BlockBehaviour.Properties p_54345_) {
      super(p_54345_);
      this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(WATERLOGGED, Boolean.valueOf(false)));
   }

   public VoxelShape getShape(BlockState p_54372_, BlockGetter p_54373_, BlockPos p_54374_, CollisionContext p_54375_) {
      switch((Direction)p_54372_.getValue(FACING)) {
      case NORTH:
         return NORTH_AABB;
      case SOUTH:
         return SOUTH_AABB;
      case WEST:
         return WEST_AABB;
      case EAST:
      default:
         return EAST_AABB;
      }
   }

   private boolean canAttachTo(BlockGetter p_54349_, BlockPos p_54350_, Direction p_54351_) {
      BlockState blockstate = p_54349_.getBlockState(p_54350_);
      return blockstate.isFaceSturdy(p_54349_, p_54350_, p_54351_);
   }

   public boolean canSurvive(BlockState p_54353_, LevelReader p_54354_, BlockPos p_54355_) {
      Direction direction = p_54353_.getValue(FACING);
      return this.canAttachTo(p_54354_, p_54355_.relative(direction.getOpposite()), direction);
   }

   public BlockState updateShape(BlockState p_54363_, Direction p_54364_, BlockState p_54365_, LevelAccessor p_54366_, BlockPos p_54367_, BlockPos p_54368_) {
      if (p_54364_.getOpposite() == p_54363_.getValue(FACING) && !p_54363_.canSurvive(p_54366_, p_54367_)) {
         return Blocks.AIR.defaultBlockState();
      } else {
         if (p_54363_.getValue(WATERLOGGED)) {
            p_54366_.scheduleTick(p_54367_, Fluids.WATER, Fluids.WATER.getTickDelay(p_54366_));
         }

         return super.updateShape(p_54363_, p_54364_, p_54365_, p_54366_, p_54367_, p_54368_);
      }
   }

   @Nullable
   public BlockState getStateForPlacement(BlockPlaceContext p_54347_) {
      if (!p_54347_.replacingClickedOnBlock()) {
         BlockState blockstate = p_54347_.getLevel().getBlockState(p_54347_.getClickedPos().relative(p_54347_.getClickedFace().getOpposite()));
         if (blockstate.is(this) && blockstate.getValue(FACING) == p_54347_.getClickedFace()) {
            return null;
         }
      }

      BlockState blockstate1 = this.defaultBlockState();
      LevelReader levelreader = p_54347_.getLevel();
      BlockPos blockpos = p_54347_.getClickedPos();
      FluidState fluidstate = p_54347_.getLevel().getFluidState(p_54347_.getClickedPos());

      for(Direction direction : p_54347_.getNearestLookingDirections()) {
         if (direction.getAxis().isHorizontal()) {
            blockstate1 = blockstate1.setValue(FACING, direction.getOpposite());
            if (blockstate1.canSurvive(levelreader, blockpos)) {
               return blockstate1.setValue(WATERLOGGED, Boolean.valueOf(fluidstate.getType() == Fluids.WATER));
            }
         }
      }

      return null;
   }

   public BlockState rotate(BlockState p_54360_, Rotation p_54361_) {
      return p_54360_.setValue(FACING, p_54361_.rotate(p_54360_.getValue(FACING)));
   }

   public BlockState mirror(BlockState p_54357_, Mirror p_54358_) {
      return p_54357_.rotate(p_54358_.getRotation(p_54357_.getValue(FACING)));
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_54370_) {
      p_54370_.add(FACING, WATERLOGGED);
   }

   public FluidState getFluidState(BlockState p_54377_) {
      return p_54377_.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(p_54377_);
   }
}