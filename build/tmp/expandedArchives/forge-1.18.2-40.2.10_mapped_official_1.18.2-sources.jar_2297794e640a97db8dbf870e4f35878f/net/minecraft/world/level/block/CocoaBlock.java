package net.minecraft.world.level.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class CocoaBlock extends HorizontalDirectionalBlock implements BonemealableBlock {
   public static final int MAX_AGE = 2;
   public static final IntegerProperty AGE = BlockStateProperties.AGE_2;
   protected static final int AGE_0_WIDTH = 4;
   protected static final int AGE_0_HEIGHT = 5;
   protected static final int AGE_0_HALFWIDTH = 2;
   protected static final int AGE_1_WIDTH = 6;
   protected static final int AGE_1_HEIGHT = 7;
   protected static final int AGE_1_HALFWIDTH = 3;
   protected static final int AGE_2_WIDTH = 8;
   protected static final int AGE_2_HEIGHT = 9;
   protected static final int AGE_2_HALFWIDTH = 4;
   protected static final VoxelShape[] EAST_AABB = new VoxelShape[]{Block.box(11.0D, 7.0D, 6.0D, 15.0D, 12.0D, 10.0D), Block.box(9.0D, 5.0D, 5.0D, 15.0D, 12.0D, 11.0D), Block.box(7.0D, 3.0D, 4.0D, 15.0D, 12.0D, 12.0D)};
   protected static final VoxelShape[] WEST_AABB = new VoxelShape[]{Block.box(1.0D, 7.0D, 6.0D, 5.0D, 12.0D, 10.0D), Block.box(1.0D, 5.0D, 5.0D, 7.0D, 12.0D, 11.0D), Block.box(1.0D, 3.0D, 4.0D, 9.0D, 12.0D, 12.0D)};
   protected static final VoxelShape[] NORTH_AABB = new VoxelShape[]{Block.box(6.0D, 7.0D, 1.0D, 10.0D, 12.0D, 5.0D), Block.box(5.0D, 5.0D, 1.0D, 11.0D, 12.0D, 7.0D), Block.box(4.0D, 3.0D, 1.0D, 12.0D, 12.0D, 9.0D)};
   protected static final VoxelShape[] SOUTH_AABB = new VoxelShape[]{Block.box(6.0D, 7.0D, 11.0D, 10.0D, 12.0D, 15.0D), Block.box(5.0D, 5.0D, 9.0D, 11.0D, 12.0D, 15.0D), Block.box(4.0D, 3.0D, 7.0D, 12.0D, 12.0D, 15.0D)};

   public CocoaBlock(BlockBehaviour.Properties p_51743_) {
      super(p_51743_);
      this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(AGE, Integer.valueOf(0)));
   }

   public boolean isRandomlyTicking(BlockState p_51780_) {
      return p_51780_.getValue(AGE) < 2;
   }

   public void randomTick(BlockState p_51782_, ServerLevel p_51783_, BlockPos p_51784_, Random p_51785_) {
      if (true) {
         int i = p_51782_.getValue(AGE);
         if (i < 2 && net.minecraftforge.common.ForgeHooks.onCropsGrowPre(p_51783_, p_51784_, p_51782_, p_51783_.random.nextInt(5) == 0)) {
            p_51783_.setBlock(p_51784_, p_51782_.setValue(AGE, Integer.valueOf(i + 1)), 2);
            net.minecraftforge.common.ForgeHooks.onCropsGrowPost(p_51783_, p_51784_, p_51782_);
         }
      }

   }

   public boolean canSurvive(BlockState p_51767_, LevelReader p_51768_, BlockPos p_51769_) {
      BlockState blockstate = p_51768_.getBlockState(p_51769_.relative(p_51767_.getValue(FACING)));
      return blockstate.is(BlockTags.JUNGLE_LOGS);
   }

   public VoxelShape getShape(BlockState p_51787_, BlockGetter p_51788_, BlockPos p_51789_, CollisionContext p_51790_) {
      int i = p_51787_.getValue(AGE);
      switch((Direction)p_51787_.getValue(FACING)) {
      case SOUTH:
         return SOUTH_AABB[i];
      case NORTH:
      default:
         return NORTH_AABB[i];
      case WEST:
         return WEST_AABB[i];
      case EAST:
         return EAST_AABB[i];
      }
   }

   @Nullable
   public BlockState getStateForPlacement(BlockPlaceContext p_51750_) {
      BlockState blockstate = this.defaultBlockState();
      LevelReader levelreader = p_51750_.getLevel();
      BlockPos blockpos = p_51750_.getClickedPos();

      for(Direction direction : p_51750_.getNearestLookingDirections()) {
         if (direction.getAxis().isHorizontal()) {
            blockstate = blockstate.setValue(FACING, direction);
            if (blockstate.canSurvive(levelreader, blockpos)) {
               return blockstate;
            }
         }
      }

      return null;
   }

   public BlockState updateShape(BlockState p_51771_, Direction p_51772_, BlockState p_51773_, LevelAccessor p_51774_, BlockPos p_51775_, BlockPos p_51776_) {
      return p_51772_ == p_51771_.getValue(FACING) && !p_51771_.canSurvive(p_51774_, p_51775_) ? Blocks.AIR.defaultBlockState() : super.updateShape(p_51771_, p_51772_, p_51773_, p_51774_, p_51775_, p_51776_);
   }

   public boolean isValidBonemealTarget(BlockGetter p_51752_, BlockPos p_51753_, BlockState p_51754_, boolean p_51755_) {
      return p_51754_.getValue(AGE) < 2;
   }

   public boolean isBonemealSuccess(Level p_51757_, Random p_51758_, BlockPos p_51759_, BlockState p_51760_) {
      return true;
   }

   public void performBonemeal(ServerLevel p_51745_, Random p_51746_, BlockPos p_51747_, BlockState p_51748_) {
      p_51745_.setBlock(p_51747_, p_51748_.setValue(AGE, Integer.valueOf(p_51748_.getValue(AGE) + 1)), 2);
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_51778_) {
      p_51778_.add(FACING, AGE);
   }

   public boolean isPathfindable(BlockState p_51762_, BlockGetter p_51763_, BlockPos p_51764_, PathComputationType p_51765_) {
      return false;
   }
}
