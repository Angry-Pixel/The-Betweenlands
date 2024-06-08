package net.minecraft.world.level.block;

import java.util.Random;
import java.util.stream.IntStream;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.StairsShape;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class StairBlock extends Block implements SimpleWaterloggedBlock {
   public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
   public static final EnumProperty<Half> HALF = BlockStateProperties.HALF;
   public static final EnumProperty<StairsShape> SHAPE = BlockStateProperties.STAIRS_SHAPE;
   public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
   protected static final VoxelShape TOP_AABB = SlabBlock.TOP_AABB;
   protected static final VoxelShape BOTTOM_AABB = SlabBlock.BOTTOM_AABB;
   protected static final VoxelShape OCTET_NNN = Block.box(0.0D, 0.0D, 0.0D, 8.0D, 8.0D, 8.0D);
   protected static final VoxelShape OCTET_NNP = Block.box(0.0D, 0.0D, 8.0D, 8.0D, 8.0D, 16.0D);
   protected static final VoxelShape OCTET_NPN = Block.box(0.0D, 8.0D, 0.0D, 8.0D, 16.0D, 8.0D);
   protected static final VoxelShape OCTET_NPP = Block.box(0.0D, 8.0D, 8.0D, 8.0D, 16.0D, 16.0D);
   protected static final VoxelShape OCTET_PNN = Block.box(8.0D, 0.0D, 0.0D, 16.0D, 8.0D, 8.0D);
   protected static final VoxelShape OCTET_PNP = Block.box(8.0D, 0.0D, 8.0D, 16.0D, 8.0D, 16.0D);
   protected static final VoxelShape OCTET_PPN = Block.box(8.0D, 8.0D, 0.0D, 16.0D, 16.0D, 8.0D);
   protected static final VoxelShape OCTET_PPP = Block.box(8.0D, 8.0D, 8.0D, 16.0D, 16.0D, 16.0D);
   protected static final VoxelShape[] TOP_SHAPES = makeShapes(TOP_AABB, OCTET_NNN, OCTET_PNN, OCTET_NNP, OCTET_PNP);
   protected static final VoxelShape[] BOTTOM_SHAPES = makeShapes(BOTTOM_AABB, OCTET_NPN, OCTET_PPN, OCTET_NPP, OCTET_PPP);
   private static final int[] SHAPE_BY_STATE = new int[]{12, 5, 3, 10, 14, 13, 7, 11, 13, 7, 11, 14, 8, 4, 1, 2, 4, 1, 2, 8};
   private final Block base;
   private final BlockState baseState;

   private static VoxelShape[] makeShapes(VoxelShape p_56934_, VoxelShape p_56935_, VoxelShape p_56936_, VoxelShape p_56937_, VoxelShape p_56938_) {
      return IntStream.range(0, 16).mapToObj((p_56945_) -> {
         return makeStairShape(p_56945_, p_56934_, p_56935_, p_56936_, p_56937_, p_56938_);
      }).toArray((p_56949_) -> {
         return new VoxelShape[p_56949_];
      });
   }

   private static VoxelShape makeStairShape(int p_56865_, VoxelShape p_56866_, VoxelShape p_56867_, VoxelShape p_56868_, VoxelShape p_56869_, VoxelShape p_56870_) {
      VoxelShape voxelshape = p_56866_;
      if ((p_56865_ & 1) != 0) {
         voxelshape = Shapes.or(p_56866_, p_56867_);
      }

      if ((p_56865_ & 2) != 0) {
         voxelshape = Shapes.or(voxelshape, p_56868_);
      }

      if ((p_56865_ & 4) != 0) {
         voxelshape = Shapes.or(voxelshape, p_56869_);
      }

      if ((p_56865_ & 8) != 0) {
         voxelshape = Shapes.or(voxelshape, p_56870_);
      }

      return voxelshape;
   }

   @Deprecated // Forge: Use the other constructor that takes a Supplier
   public StairBlock(BlockState p_56862_, BlockBehaviour.Properties p_56863_) {
      super(p_56863_);
      this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(HALF, Half.BOTTOM).setValue(SHAPE, StairsShape.STRAIGHT).setValue(WATERLOGGED, Boolean.valueOf(false)));
      this.base = p_56862_.getBlock();
      this.baseState = p_56862_;
      this.stateSupplier = () -> p_56862_;
   }

   public StairBlock(java.util.function.Supplier<BlockState> state, BlockBehaviour.Properties properties) {
      super(properties);
      this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(HALF, Half.BOTTOM).setValue(SHAPE, StairsShape.STRAIGHT).setValue(WATERLOGGED, Boolean.valueOf(false)));
      this.base = Blocks.AIR; // These are unused, fields are redirected
      this.baseState = Blocks.AIR.defaultBlockState();
      this.stateSupplier = state;
   }

   public boolean useShapeForLightOcclusion(BlockState p_56967_) {
      return true;
   }

   public VoxelShape getShape(BlockState p_56956_, BlockGetter p_56957_, BlockPos p_56958_, CollisionContext p_56959_) {
      return (p_56956_.getValue(HALF) == Half.TOP ? TOP_SHAPES : BOTTOM_SHAPES)[SHAPE_BY_STATE[this.getShapeIndex(p_56956_)]];
   }

   private int getShapeIndex(BlockState p_56983_) {
      return p_56983_.getValue(SHAPE).ordinal() * 4 + p_56983_.getValue(FACING).get2DDataValue();
   }

   public void animateTick(BlockState p_56914_, Level p_56915_, BlockPos p_56916_, Random p_56917_) {
      this.base.animateTick(p_56914_, p_56915_, p_56916_, p_56917_);
   }

   public void attack(BlockState p_56896_, Level p_56897_, BlockPos p_56898_, Player p_56899_) {
      this.baseState.attack(p_56897_, p_56898_, p_56899_);
   }

   public void destroy(LevelAccessor p_56882_, BlockPos p_56883_, BlockState p_56884_) {
      this.base.destroy(p_56882_, p_56883_, p_56884_);
   }

   public float getExplosionResistance() {
      return this.base.getExplosionResistance();
   }

   public void onPlace(BlockState p_56961_, Level p_56962_, BlockPos p_56963_, BlockState p_56964_, boolean p_56965_) {
      if (!p_56961_.is(p_56961_.getBlock())) {
         this.baseState.neighborChanged(p_56962_, p_56963_, Blocks.AIR, p_56963_, false);
         this.base.onPlace(this.baseState, p_56962_, p_56963_, p_56964_, false);
      }
   }

   public void onRemove(BlockState p_56908_, Level p_56909_, BlockPos p_56910_, BlockState p_56911_, boolean p_56912_) {
      if (!p_56908_.is(p_56911_.getBlock())) {
         this.baseState.onRemove(p_56909_, p_56910_, p_56911_, p_56912_);
      }
   }

   public void stepOn(Level p_154720_, BlockPos p_154721_, BlockState p_154722_, Entity p_154723_) {
      this.base.stepOn(p_154720_, p_154721_, p_154722_, p_154723_);
   }

   public boolean isRandomlyTicking(BlockState p_56947_) {
      return this.base.isRandomlyTicking(p_56947_);
   }

   public void randomTick(BlockState p_56951_, ServerLevel p_56952_, BlockPos p_56953_, Random p_56954_) {
      this.base.randomTick(p_56951_, p_56952_, p_56953_, p_56954_);
   }

   public void tick(BlockState p_56886_, ServerLevel p_56887_, BlockPos p_56888_, Random p_56889_) {
      this.base.tick(p_56886_, p_56887_, p_56888_, p_56889_);
   }

   public InteractionResult use(BlockState p_56901_, Level p_56902_, BlockPos p_56903_, Player p_56904_, InteractionHand p_56905_, BlockHitResult p_56906_) {
      return this.baseState.use(p_56902_, p_56904_, p_56905_, p_56906_);
   }

   public void wasExploded(Level p_56878_, BlockPos p_56879_, Explosion p_56880_) {
      this.base.wasExploded(p_56878_, p_56879_, p_56880_);
   }

   public BlockState getStateForPlacement(BlockPlaceContext p_56872_) {
      Direction direction = p_56872_.getClickedFace();
      BlockPos blockpos = p_56872_.getClickedPos();
      FluidState fluidstate = p_56872_.getLevel().getFluidState(blockpos);
      BlockState blockstate = this.defaultBlockState().setValue(FACING, p_56872_.getHorizontalDirection()).setValue(HALF, direction != Direction.DOWN && (direction == Direction.UP || !(p_56872_.getClickLocation().y - (double)blockpos.getY() > 0.5D)) ? Half.BOTTOM : Half.TOP).setValue(WATERLOGGED, Boolean.valueOf(fluidstate.getType() == Fluids.WATER));
      return blockstate.setValue(SHAPE, getStairsShape(blockstate, p_56872_.getLevel(), blockpos));
   }

   public BlockState updateShape(BlockState p_56925_, Direction p_56926_, BlockState p_56927_, LevelAccessor p_56928_, BlockPos p_56929_, BlockPos p_56930_) {
      if (p_56925_.getValue(WATERLOGGED)) {
         p_56928_.scheduleTick(p_56929_, Fluids.WATER, Fluids.WATER.getTickDelay(p_56928_));
      }

      return p_56926_.getAxis().isHorizontal() ? p_56925_.setValue(SHAPE, getStairsShape(p_56925_, p_56928_, p_56929_)) : super.updateShape(p_56925_, p_56926_, p_56927_, p_56928_, p_56929_, p_56930_);
   }

   private static StairsShape getStairsShape(BlockState p_56977_, BlockGetter p_56978_, BlockPos p_56979_) {
      Direction direction = p_56977_.getValue(FACING);
      BlockState blockstate = p_56978_.getBlockState(p_56979_.relative(direction));
      if (isStairs(blockstate) && p_56977_.getValue(HALF) == blockstate.getValue(HALF)) {
         Direction direction1 = blockstate.getValue(FACING);
         if (direction1.getAxis() != p_56977_.getValue(FACING).getAxis() && canTakeShape(p_56977_, p_56978_, p_56979_, direction1.getOpposite())) {
            if (direction1 == direction.getCounterClockWise()) {
               return StairsShape.OUTER_LEFT;
            }

            return StairsShape.OUTER_RIGHT;
         }
      }

      BlockState blockstate1 = p_56978_.getBlockState(p_56979_.relative(direction.getOpposite()));
      if (isStairs(blockstate1) && p_56977_.getValue(HALF) == blockstate1.getValue(HALF)) {
         Direction direction2 = blockstate1.getValue(FACING);
         if (direction2.getAxis() != p_56977_.getValue(FACING).getAxis() && canTakeShape(p_56977_, p_56978_, p_56979_, direction2)) {
            if (direction2 == direction.getCounterClockWise()) {
               return StairsShape.INNER_LEFT;
            }

            return StairsShape.INNER_RIGHT;
         }
      }

      return StairsShape.STRAIGHT;
   }

   private static boolean canTakeShape(BlockState p_56971_, BlockGetter p_56972_, BlockPos p_56973_, Direction p_56974_) {
      BlockState blockstate = p_56972_.getBlockState(p_56973_.relative(p_56974_));
      return !isStairs(blockstate) || blockstate.getValue(FACING) != p_56971_.getValue(FACING) || blockstate.getValue(HALF) != p_56971_.getValue(HALF);
   }

   public static boolean isStairs(BlockState p_56981_) {
      return p_56981_.getBlock() instanceof StairBlock;
   }

   public BlockState rotate(BlockState p_56922_, Rotation p_56923_) {
      return p_56922_.setValue(FACING, p_56923_.rotate(p_56922_.getValue(FACING)));
   }

   public BlockState mirror(BlockState p_56919_, Mirror p_56920_) {
      Direction direction = p_56919_.getValue(FACING);
      StairsShape stairsshape = p_56919_.getValue(SHAPE);
      switch(p_56920_) {
      case LEFT_RIGHT:
         if (direction.getAxis() == Direction.Axis.Z) {
            switch(stairsshape) {
            case INNER_LEFT:
               return p_56919_.rotate(Rotation.CLOCKWISE_180).setValue(SHAPE, StairsShape.INNER_RIGHT);
            case INNER_RIGHT:
               return p_56919_.rotate(Rotation.CLOCKWISE_180).setValue(SHAPE, StairsShape.INNER_LEFT);
            case OUTER_LEFT:
               return p_56919_.rotate(Rotation.CLOCKWISE_180).setValue(SHAPE, StairsShape.OUTER_RIGHT);
            case OUTER_RIGHT:
               return p_56919_.rotate(Rotation.CLOCKWISE_180).setValue(SHAPE, StairsShape.OUTER_LEFT);
            default:
               return p_56919_.rotate(Rotation.CLOCKWISE_180);
            }
         }
         break;
      case FRONT_BACK:
         if (direction.getAxis() == Direction.Axis.X) {
            switch(stairsshape) {
            case INNER_LEFT:
               return p_56919_.rotate(Rotation.CLOCKWISE_180).setValue(SHAPE, StairsShape.INNER_LEFT);
            case INNER_RIGHT:
               return p_56919_.rotate(Rotation.CLOCKWISE_180).setValue(SHAPE, StairsShape.INNER_RIGHT);
            case OUTER_LEFT:
               return p_56919_.rotate(Rotation.CLOCKWISE_180).setValue(SHAPE, StairsShape.OUTER_RIGHT);
            case OUTER_RIGHT:
               return p_56919_.rotate(Rotation.CLOCKWISE_180).setValue(SHAPE, StairsShape.OUTER_LEFT);
            case STRAIGHT:
               return p_56919_.rotate(Rotation.CLOCKWISE_180);
            }
         }
      }

      return super.mirror(p_56919_, p_56920_);
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_56932_) {
      p_56932_.add(FACING, HALF, SHAPE, WATERLOGGED);
   }

   public FluidState getFluidState(BlockState p_56969_) {
      return p_56969_.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(p_56969_);
   }

   public boolean isPathfindable(BlockState p_56891_, BlockGetter p_56892_, BlockPos p_56893_, PathComputationType p_56894_) {
      return false;
   }

   // Forge Start
   private final java.util.function.Supplier<BlockState> stateSupplier;
   private Block getModelBlock() {
       return getModelState().getBlock();
   }
   private BlockState getModelState() {
       return stateSupplier.get();
   }
   // Forge end
}
