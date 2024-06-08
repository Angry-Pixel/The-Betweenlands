package net.minecraft.world.level.block;

import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

public class HugeMushroomBlock extends Block {
   public static final BooleanProperty NORTH = PipeBlock.NORTH;
   public static final BooleanProperty EAST = PipeBlock.EAST;
   public static final BooleanProperty SOUTH = PipeBlock.SOUTH;
   public static final BooleanProperty WEST = PipeBlock.WEST;
   public static final BooleanProperty UP = PipeBlock.UP;
   public static final BooleanProperty DOWN = PipeBlock.DOWN;
   private static final Map<Direction, BooleanProperty> PROPERTY_BY_DIRECTION = PipeBlock.PROPERTY_BY_DIRECTION;

   public HugeMushroomBlock(BlockBehaviour.Properties p_54136_) {
      super(p_54136_);
      this.registerDefaultState(this.stateDefinition.any().setValue(NORTH, Boolean.valueOf(true)).setValue(EAST, Boolean.valueOf(true)).setValue(SOUTH, Boolean.valueOf(true)).setValue(WEST, Boolean.valueOf(true)).setValue(UP, Boolean.valueOf(true)).setValue(DOWN, Boolean.valueOf(true)));
   }

   public BlockState getStateForPlacement(BlockPlaceContext p_54138_) {
      BlockGetter blockgetter = p_54138_.getLevel();
      BlockPos blockpos = p_54138_.getClickedPos();
      return this.defaultBlockState().setValue(DOWN, Boolean.valueOf(!blockgetter.getBlockState(blockpos.below()).is(this))).setValue(UP, Boolean.valueOf(!blockgetter.getBlockState(blockpos.above()).is(this))).setValue(NORTH, Boolean.valueOf(!blockgetter.getBlockState(blockpos.north()).is(this))).setValue(EAST, Boolean.valueOf(!blockgetter.getBlockState(blockpos.east()).is(this))).setValue(SOUTH, Boolean.valueOf(!blockgetter.getBlockState(blockpos.south()).is(this))).setValue(WEST, Boolean.valueOf(!blockgetter.getBlockState(blockpos.west()).is(this)));
   }

   public BlockState updateShape(BlockState p_54146_, Direction p_54147_, BlockState p_54148_, LevelAccessor p_54149_, BlockPos p_54150_, BlockPos p_54151_) {
      return p_54148_.is(this) ? p_54146_.setValue(PROPERTY_BY_DIRECTION.get(p_54147_), Boolean.valueOf(false)) : super.updateShape(p_54146_, p_54147_, p_54148_, p_54149_, p_54150_, p_54151_);
   }

   public BlockState rotate(BlockState p_54143_, Rotation p_54144_) {
      return p_54143_.setValue(PROPERTY_BY_DIRECTION.get(p_54144_.rotate(Direction.NORTH)), p_54143_.getValue(NORTH)).setValue(PROPERTY_BY_DIRECTION.get(p_54144_.rotate(Direction.SOUTH)), p_54143_.getValue(SOUTH)).setValue(PROPERTY_BY_DIRECTION.get(p_54144_.rotate(Direction.EAST)), p_54143_.getValue(EAST)).setValue(PROPERTY_BY_DIRECTION.get(p_54144_.rotate(Direction.WEST)), p_54143_.getValue(WEST)).setValue(PROPERTY_BY_DIRECTION.get(p_54144_.rotate(Direction.UP)), p_54143_.getValue(UP)).setValue(PROPERTY_BY_DIRECTION.get(p_54144_.rotate(Direction.DOWN)), p_54143_.getValue(DOWN));
   }

   public BlockState mirror(BlockState p_54140_, Mirror p_54141_) {
      return p_54140_.setValue(PROPERTY_BY_DIRECTION.get(p_54141_.mirror(Direction.NORTH)), p_54140_.getValue(NORTH)).setValue(PROPERTY_BY_DIRECTION.get(p_54141_.mirror(Direction.SOUTH)), p_54140_.getValue(SOUTH)).setValue(PROPERTY_BY_DIRECTION.get(p_54141_.mirror(Direction.EAST)), p_54140_.getValue(EAST)).setValue(PROPERTY_BY_DIRECTION.get(p_54141_.mirror(Direction.WEST)), p_54140_.getValue(WEST)).setValue(PROPERTY_BY_DIRECTION.get(p_54141_.mirror(Direction.UP)), p_54140_.getValue(UP)).setValue(PROPERTY_BY_DIRECTION.get(p_54141_.mirror(Direction.DOWN)), p_54140_.getValue(DOWN));
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_54153_) {
      p_54153_.add(UP, DOWN, NORTH, EAST, SOUTH, WEST);
   }
}