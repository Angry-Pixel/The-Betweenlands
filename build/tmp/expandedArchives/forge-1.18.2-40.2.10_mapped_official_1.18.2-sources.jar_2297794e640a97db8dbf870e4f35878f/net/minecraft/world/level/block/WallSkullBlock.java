package net.minecraft.world.level.block;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class WallSkullBlock extends AbstractSkullBlock {
   public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
   private static final Map<Direction, VoxelShape> AABBS = Maps.newEnumMap(ImmutableMap.of(Direction.NORTH, Block.box(4.0D, 4.0D, 8.0D, 12.0D, 12.0D, 16.0D), Direction.SOUTH, Block.box(4.0D, 4.0D, 0.0D, 12.0D, 12.0D, 8.0D), Direction.EAST, Block.box(0.0D, 4.0D, 4.0D, 8.0D, 12.0D, 12.0D), Direction.WEST, Block.box(8.0D, 4.0D, 4.0D, 16.0D, 12.0D, 12.0D)));

   public WallSkullBlock(SkullBlock.Type p_58101_, BlockBehaviour.Properties p_58102_) {
      super(p_58101_, p_58102_);
      this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
   }

   public String getDescriptionId() {
      return this.asItem().getDescriptionId();
   }

   public VoxelShape getShape(BlockState p_58114_, BlockGetter p_58115_, BlockPos p_58116_, CollisionContext p_58117_) {
      return AABBS.get(p_58114_.getValue(FACING));
   }

   public BlockState getStateForPlacement(BlockPlaceContext p_58104_) {
      BlockState blockstate = this.defaultBlockState();
      BlockGetter blockgetter = p_58104_.getLevel();
      BlockPos blockpos = p_58104_.getClickedPos();
      Direction[] adirection = p_58104_.getNearestLookingDirections();

      for(Direction direction : adirection) {
         if (direction.getAxis().isHorizontal()) {
            Direction direction1 = direction.getOpposite();
            blockstate = blockstate.setValue(FACING, direction1);
            if (!blockgetter.getBlockState(blockpos.relative(direction)).canBeReplaced(p_58104_)) {
               return blockstate;
            }
         }
      }

      return null;
   }

   public BlockState rotate(BlockState p_58109_, Rotation p_58110_) {
      return p_58109_.setValue(FACING, p_58110_.rotate(p_58109_.getValue(FACING)));
   }

   public BlockState mirror(BlockState p_58106_, Mirror p_58107_) {
      return p_58106_.rotate(p_58107_.getRotation(p_58106_.getValue(FACING)));
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_58112_) {
      p_58112_.add(FACING);
   }
}