package net.minecraft.world.level.block;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;

public class FaceAttachedHorizontalDirectionalBlock extends HorizontalDirectionalBlock {
   public static final EnumProperty<AttachFace> FACE = BlockStateProperties.ATTACH_FACE;

   public FaceAttachedHorizontalDirectionalBlock(BlockBehaviour.Properties p_53182_) {
      super(p_53182_);
   }

   public boolean canSurvive(BlockState p_53186_, LevelReader p_53187_, BlockPos p_53188_) {
      return canAttach(p_53187_, p_53188_, getConnectedDirection(p_53186_).getOpposite());
   }

   public static boolean canAttach(LevelReader p_53197_, BlockPos p_53198_, Direction p_53199_) {
      BlockPos blockpos = p_53198_.relative(p_53199_);
      return p_53197_.getBlockState(blockpos).isFaceSturdy(p_53197_, blockpos, p_53199_.getOpposite());
   }

   @Nullable
   public BlockState getStateForPlacement(BlockPlaceContext p_53184_) {
      for(Direction direction : p_53184_.getNearestLookingDirections()) {
         BlockState blockstate;
         if (direction.getAxis() == Direction.Axis.Y) {
            blockstate = this.defaultBlockState().setValue(FACE, direction == Direction.UP ? AttachFace.CEILING : AttachFace.FLOOR).setValue(FACING, p_53184_.getHorizontalDirection());
         } else {
            blockstate = this.defaultBlockState().setValue(FACE, AttachFace.WALL).setValue(FACING, direction.getOpposite());
         }

         if (blockstate.canSurvive(p_53184_.getLevel(), p_53184_.getClickedPos())) {
            return blockstate;
         }
      }

      return null;
   }

   public BlockState updateShape(BlockState p_53190_, Direction p_53191_, BlockState p_53192_, LevelAccessor p_53193_, BlockPos p_53194_, BlockPos p_53195_) {
      return getConnectedDirection(p_53190_).getOpposite() == p_53191_ && !p_53190_.canSurvive(p_53193_, p_53194_) ? Blocks.AIR.defaultBlockState() : super.updateShape(p_53190_, p_53191_, p_53192_, p_53193_, p_53194_, p_53195_);
   }

   protected static Direction getConnectedDirection(BlockState p_53201_) {
      switch((AttachFace)p_53201_.getValue(FACE)) {
      case CEILING:
         return Direction.DOWN;
      case FLOOR:
         return Direction.UP;
      default:
         return p_53201_.getValue(FACING);
      }
   }
}