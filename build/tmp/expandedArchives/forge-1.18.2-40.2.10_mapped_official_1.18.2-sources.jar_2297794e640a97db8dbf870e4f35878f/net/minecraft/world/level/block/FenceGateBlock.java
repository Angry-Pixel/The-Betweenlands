package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class FenceGateBlock extends HorizontalDirectionalBlock {
   public static final BooleanProperty OPEN = BlockStateProperties.OPEN;
   public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
   public static final BooleanProperty IN_WALL = BlockStateProperties.IN_WALL;
   protected static final VoxelShape Z_SHAPE = Block.box(0.0D, 0.0D, 6.0D, 16.0D, 16.0D, 10.0D);
   protected static final VoxelShape X_SHAPE = Block.box(6.0D, 0.0D, 0.0D, 10.0D, 16.0D, 16.0D);
   protected static final VoxelShape Z_SHAPE_LOW = Block.box(0.0D, 0.0D, 6.0D, 16.0D, 13.0D, 10.0D);
   protected static final VoxelShape X_SHAPE_LOW = Block.box(6.0D, 0.0D, 0.0D, 10.0D, 13.0D, 16.0D);
   protected static final VoxelShape Z_COLLISION_SHAPE = Block.box(0.0D, 0.0D, 6.0D, 16.0D, 24.0D, 10.0D);
   protected static final VoxelShape X_COLLISION_SHAPE = Block.box(6.0D, 0.0D, 0.0D, 10.0D, 24.0D, 16.0D);
   protected static final VoxelShape Z_OCCLUSION_SHAPE = Shapes.or(Block.box(0.0D, 5.0D, 7.0D, 2.0D, 16.0D, 9.0D), Block.box(14.0D, 5.0D, 7.0D, 16.0D, 16.0D, 9.0D));
   protected static final VoxelShape X_OCCLUSION_SHAPE = Shapes.or(Block.box(7.0D, 5.0D, 0.0D, 9.0D, 16.0D, 2.0D), Block.box(7.0D, 5.0D, 14.0D, 9.0D, 16.0D, 16.0D));
   protected static final VoxelShape Z_OCCLUSION_SHAPE_LOW = Shapes.or(Block.box(0.0D, 2.0D, 7.0D, 2.0D, 13.0D, 9.0D), Block.box(14.0D, 2.0D, 7.0D, 16.0D, 13.0D, 9.0D));
   protected static final VoxelShape X_OCCLUSION_SHAPE_LOW = Shapes.or(Block.box(7.0D, 2.0D, 0.0D, 9.0D, 13.0D, 2.0D), Block.box(7.0D, 2.0D, 14.0D, 9.0D, 13.0D, 16.0D));

   public FenceGateBlock(BlockBehaviour.Properties p_53356_) {
      super(p_53356_);
      this.registerDefaultState(this.stateDefinition.any().setValue(OPEN, Boolean.valueOf(false)).setValue(POWERED, Boolean.valueOf(false)).setValue(IN_WALL, Boolean.valueOf(false)));
   }

   public VoxelShape getShape(BlockState p_53391_, BlockGetter p_53392_, BlockPos p_53393_, CollisionContext p_53394_) {
      if (p_53391_.getValue(IN_WALL)) {
         return p_53391_.getValue(FACING).getAxis() == Direction.Axis.X ? X_SHAPE_LOW : Z_SHAPE_LOW;
      } else {
         return p_53391_.getValue(FACING).getAxis() == Direction.Axis.X ? X_SHAPE : Z_SHAPE;
      }
   }

   public BlockState updateShape(BlockState p_53382_, Direction p_53383_, BlockState p_53384_, LevelAccessor p_53385_, BlockPos p_53386_, BlockPos p_53387_) {
      Direction.Axis direction$axis = p_53383_.getAxis();
      if (p_53382_.getValue(FACING).getClockWise().getAxis() != direction$axis) {
         return super.updateShape(p_53382_, p_53383_, p_53384_, p_53385_, p_53386_, p_53387_);
      } else {
         boolean flag = this.isWall(p_53384_) || this.isWall(p_53385_.getBlockState(p_53386_.relative(p_53383_.getOpposite())));
         return p_53382_.setValue(IN_WALL, Boolean.valueOf(flag));
      }
   }

   public VoxelShape getCollisionShape(BlockState p_53396_, BlockGetter p_53397_, BlockPos p_53398_, CollisionContext p_53399_) {
      if (p_53396_.getValue(OPEN)) {
         return Shapes.empty();
      } else {
         return p_53396_.getValue(FACING).getAxis() == Direction.Axis.Z ? Z_COLLISION_SHAPE : X_COLLISION_SHAPE;
      }
   }

   public VoxelShape getOcclusionShape(BlockState p_53401_, BlockGetter p_53402_, BlockPos p_53403_) {
      if (p_53401_.getValue(IN_WALL)) {
         return p_53401_.getValue(FACING).getAxis() == Direction.Axis.X ? X_OCCLUSION_SHAPE_LOW : Z_OCCLUSION_SHAPE_LOW;
      } else {
         return p_53401_.getValue(FACING).getAxis() == Direction.Axis.X ? X_OCCLUSION_SHAPE : Z_OCCLUSION_SHAPE;
      }
   }

   public boolean isPathfindable(BlockState p_53360_, BlockGetter p_53361_, BlockPos p_53362_, PathComputationType p_53363_) {
      switch(p_53363_) {
      case LAND:
         return p_53360_.getValue(OPEN);
      case WATER:
         return false;
      case AIR:
         return p_53360_.getValue(OPEN);
      default:
         return false;
      }
   }

   public BlockState getStateForPlacement(BlockPlaceContext p_53358_) {
      Level level = p_53358_.getLevel();
      BlockPos blockpos = p_53358_.getClickedPos();
      boolean flag = level.hasNeighborSignal(blockpos);
      Direction direction = p_53358_.getHorizontalDirection();
      Direction.Axis direction$axis = direction.getAxis();
      boolean flag1 = direction$axis == Direction.Axis.Z && (this.isWall(level.getBlockState(blockpos.west())) || this.isWall(level.getBlockState(blockpos.east()))) || direction$axis == Direction.Axis.X && (this.isWall(level.getBlockState(blockpos.north())) || this.isWall(level.getBlockState(blockpos.south())));
      return this.defaultBlockState().setValue(FACING, direction).setValue(OPEN, Boolean.valueOf(flag)).setValue(POWERED, Boolean.valueOf(flag)).setValue(IN_WALL, Boolean.valueOf(flag1));
   }

   private boolean isWall(BlockState p_53405_) {
      return p_53405_.is(BlockTags.WALLS);
   }

   public InteractionResult use(BlockState p_53365_, Level p_53366_, BlockPos p_53367_, Player p_53368_, InteractionHand p_53369_, BlockHitResult p_53370_) {
      if (p_53365_.getValue(OPEN)) {
         p_53365_ = p_53365_.setValue(OPEN, Boolean.valueOf(false));
         p_53366_.setBlock(p_53367_, p_53365_, 10);
      } else {
         Direction direction = p_53368_.getDirection();
         if (p_53365_.getValue(FACING) == direction.getOpposite()) {
            p_53365_ = p_53365_.setValue(FACING, direction);
         }

         p_53365_ = p_53365_.setValue(OPEN, Boolean.valueOf(true));
         p_53366_.setBlock(p_53367_, p_53365_, 10);
      }

      boolean flag = p_53365_.getValue(OPEN);
      p_53366_.levelEvent(p_53368_, flag ? 1008 : 1014, p_53367_, 0);
      p_53366_.gameEvent(p_53368_, flag ? GameEvent.BLOCK_OPEN : GameEvent.BLOCK_CLOSE, p_53367_);
      return InteractionResult.sidedSuccess(p_53366_.isClientSide);
   }

   public void neighborChanged(BlockState p_53372_, Level p_53373_, BlockPos p_53374_, Block p_53375_, BlockPos p_53376_, boolean p_53377_) {
      if (!p_53373_.isClientSide) {
         boolean flag = p_53373_.hasNeighborSignal(p_53374_);
         if (p_53372_.getValue(POWERED) != flag) {
            p_53373_.setBlock(p_53374_, p_53372_.setValue(POWERED, Boolean.valueOf(flag)).setValue(OPEN, Boolean.valueOf(flag)), 2);
            if (p_53372_.getValue(OPEN) != flag) {
               p_53373_.levelEvent((Player)null, flag ? 1008 : 1014, p_53374_, 0);
               p_53373_.gameEvent(flag ? GameEvent.BLOCK_OPEN : GameEvent.BLOCK_CLOSE, p_53374_);
            }
         }

      }
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_53389_) {
      p_53389_.add(FACING, OPEN, POWERED, IN_WALL);
   }

   public static boolean connectsToDirection(BlockState p_53379_, Direction p_53380_) {
      return p_53379_.getValue(FACING).getAxis() == p_53380_.getClockWise().getAxis();
   }
}