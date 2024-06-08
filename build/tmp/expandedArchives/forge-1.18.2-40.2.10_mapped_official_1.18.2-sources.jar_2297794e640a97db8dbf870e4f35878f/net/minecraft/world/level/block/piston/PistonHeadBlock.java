package net.minecraft.world.level.block.piston;

import java.util.Arrays;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.PistonType;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class PistonHeadBlock extends DirectionalBlock {
   public static final EnumProperty<PistonType> TYPE = BlockStateProperties.PISTON_TYPE;
   public static final BooleanProperty SHORT = BlockStateProperties.SHORT;
   public static final float PLATFORM = 4.0F;
   protected static final VoxelShape EAST_AABB = Block.box(12.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);
   protected static final VoxelShape WEST_AABB = Block.box(0.0D, 0.0D, 0.0D, 4.0D, 16.0D, 16.0D);
   protected static final VoxelShape SOUTH_AABB = Block.box(0.0D, 0.0D, 12.0D, 16.0D, 16.0D, 16.0D);
   protected static final VoxelShape NORTH_AABB = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 4.0D);
   protected static final VoxelShape UP_AABB = Block.box(0.0D, 12.0D, 0.0D, 16.0D, 16.0D, 16.0D);
   protected static final VoxelShape DOWN_AABB = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 4.0D, 16.0D);
   protected static final float AABB_OFFSET = 2.0F;
   protected static final float EDGE_MIN = 6.0F;
   protected static final float EDGE_MAX = 10.0F;
   protected static final VoxelShape UP_ARM_AABB = Block.box(6.0D, -4.0D, 6.0D, 10.0D, 12.0D, 10.0D);
   protected static final VoxelShape DOWN_ARM_AABB = Block.box(6.0D, 4.0D, 6.0D, 10.0D, 20.0D, 10.0D);
   protected static final VoxelShape SOUTH_ARM_AABB = Block.box(6.0D, 6.0D, -4.0D, 10.0D, 10.0D, 12.0D);
   protected static final VoxelShape NORTH_ARM_AABB = Block.box(6.0D, 6.0D, 4.0D, 10.0D, 10.0D, 20.0D);
   protected static final VoxelShape EAST_ARM_AABB = Block.box(-4.0D, 6.0D, 6.0D, 12.0D, 10.0D, 10.0D);
   protected static final VoxelShape WEST_ARM_AABB = Block.box(4.0D, 6.0D, 6.0D, 20.0D, 10.0D, 10.0D);
   protected static final VoxelShape SHORT_UP_ARM_AABB = Block.box(6.0D, 0.0D, 6.0D, 10.0D, 12.0D, 10.0D);
   protected static final VoxelShape SHORT_DOWN_ARM_AABB = Block.box(6.0D, 4.0D, 6.0D, 10.0D, 16.0D, 10.0D);
   protected static final VoxelShape SHORT_SOUTH_ARM_AABB = Block.box(6.0D, 6.0D, 0.0D, 10.0D, 10.0D, 12.0D);
   protected static final VoxelShape SHORT_NORTH_ARM_AABB = Block.box(6.0D, 6.0D, 4.0D, 10.0D, 10.0D, 16.0D);
   protected static final VoxelShape SHORT_EAST_ARM_AABB = Block.box(0.0D, 6.0D, 6.0D, 12.0D, 10.0D, 10.0D);
   protected static final VoxelShape SHORT_WEST_ARM_AABB = Block.box(4.0D, 6.0D, 6.0D, 16.0D, 10.0D, 10.0D);
   private static final VoxelShape[] SHAPES_SHORT = makeShapes(true);
   private static final VoxelShape[] SHAPES_LONG = makeShapes(false);

   private static VoxelShape[] makeShapes(boolean p_60313_) {
      return Arrays.stream(Direction.values()).map((p_60316_) -> {
         return calculateShape(p_60316_, p_60313_);
      }).toArray((p_60318_) -> {
         return new VoxelShape[p_60318_];
      });
   }

   private static VoxelShape calculateShape(Direction p_60310_, boolean p_60311_) {
      switch(p_60310_) {
      case DOWN:
      default:
         return Shapes.or(DOWN_AABB, p_60311_ ? SHORT_DOWN_ARM_AABB : DOWN_ARM_AABB);
      case UP:
         return Shapes.or(UP_AABB, p_60311_ ? SHORT_UP_ARM_AABB : UP_ARM_AABB);
      case NORTH:
         return Shapes.or(NORTH_AABB, p_60311_ ? SHORT_NORTH_ARM_AABB : NORTH_ARM_AABB);
      case SOUTH:
         return Shapes.or(SOUTH_AABB, p_60311_ ? SHORT_SOUTH_ARM_AABB : SOUTH_ARM_AABB);
      case WEST:
         return Shapes.or(WEST_AABB, p_60311_ ? SHORT_WEST_ARM_AABB : WEST_ARM_AABB);
      case EAST:
         return Shapes.or(EAST_AABB, p_60311_ ? SHORT_EAST_ARM_AABB : EAST_ARM_AABB);
      }
   }

   public PistonHeadBlock(BlockBehaviour.Properties p_60259_) {
      super(p_60259_);
      this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(TYPE, PistonType.DEFAULT).setValue(SHORT, Boolean.valueOf(false)));
   }

   public boolean useShapeForLightOcclusion(BlockState p_60325_) {
      return true;
   }

   public VoxelShape getShape(BlockState p_60320_, BlockGetter p_60321_, BlockPos p_60322_, CollisionContext p_60323_) {
      return (p_60320_.getValue(SHORT) ? SHAPES_SHORT : SHAPES_LONG)[p_60320_.getValue(FACING).ordinal()];
   }

   private boolean isFittingBase(BlockState p_60298_, BlockState p_60299_) {
      Block block = p_60298_.getValue(TYPE) == PistonType.DEFAULT ? Blocks.PISTON : Blocks.STICKY_PISTON;
      return p_60299_.is(block) && p_60299_.getValue(PistonBaseBlock.EXTENDED) && p_60299_.getValue(FACING) == p_60298_.getValue(FACING);
   }

   public void playerWillDestroy(Level p_60265_, BlockPos p_60266_, BlockState p_60267_, Player p_60268_) {
      if (!p_60265_.isClientSide && p_60268_.getAbilities().instabuild) {
         BlockPos blockpos = p_60266_.relative(p_60267_.getValue(FACING).getOpposite());
         if (this.isFittingBase(p_60267_, p_60265_.getBlockState(blockpos))) {
            p_60265_.destroyBlock(blockpos, false);
         }
      }

      super.playerWillDestroy(p_60265_, p_60266_, p_60267_, p_60268_);
   }

   public void onRemove(BlockState p_60282_, Level p_60283_, BlockPos p_60284_, BlockState p_60285_, boolean p_60286_) {
      if (!p_60282_.is(p_60285_.getBlock())) {
         super.onRemove(p_60282_, p_60283_, p_60284_, p_60285_, p_60286_);
         BlockPos blockpos = p_60284_.relative(p_60282_.getValue(FACING).getOpposite());
         if (this.isFittingBase(p_60282_, p_60283_.getBlockState(blockpos))) {
            p_60283_.destroyBlock(blockpos, true);
         }

      }
   }

   public BlockState updateShape(BlockState p_60301_, Direction p_60302_, BlockState p_60303_, LevelAccessor p_60304_, BlockPos p_60305_, BlockPos p_60306_) {
      return p_60302_.getOpposite() == p_60301_.getValue(FACING) && !p_60301_.canSurvive(p_60304_, p_60305_) ? Blocks.AIR.defaultBlockState() : super.updateShape(p_60301_, p_60302_, p_60303_, p_60304_, p_60305_, p_60306_);
   }

   public boolean canSurvive(BlockState p_60288_, LevelReader p_60289_, BlockPos p_60290_) {
      BlockState blockstate = p_60289_.getBlockState(p_60290_.relative(p_60288_.getValue(FACING).getOpposite()));
      return this.isFittingBase(p_60288_, blockstate) || blockstate.is(Blocks.MOVING_PISTON) && blockstate.getValue(FACING) == p_60288_.getValue(FACING);
   }

   public void neighborChanged(BlockState p_60275_, Level p_60276_, BlockPos p_60277_, Block p_60278_, BlockPos p_60279_, boolean p_60280_) {
      if (p_60275_.canSurvive(p_60276_, p_60277_)) {
         BlockPos blockpos = p_60277_.relative(p_60275_.getValue(FACING).getOpposite());
         p_60276_.getBlockState(blockpos).neighborChanged(p_60276_, blockpos, p_60278_, p_60279_, false);
      }

   }

   public ItemStack getCloneItemStack(BlockGetter p_60261_, BlockPos p_60262_, BlockState p_60263_) {
      return new ItemStack(p_60263_.getValue(TYPE) == PistonType.STICKY ? Blocks.STICKY_PISTON : Blocks.PISTON);
   }

   public BlockState rotate(BlockState p_60295_, Rotation p_60296_) {
      return p_60295_.setValue(FACING, p_60296_.rotate(p_60295_.getValue(FACING)));
   }

   public BlockState mirror(BlockState p_60292_, Mirror p_60293_) {
      return p_60292_.rotate(p_60293_.getRotation(p_60292_.getValue(FACING)));
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_60308_) {
      p_60308_.add(FACING, TYPE, SHORT);
   }

   public boolean isPathfindable(BlockState p_60270_, BlockGetter p_60271_, BlockPos p_60272_, PathComputationType p_60273_) {
      return false;
   }
}