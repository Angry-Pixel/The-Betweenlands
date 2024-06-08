package net.minecraft.world.level.block;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.DoorHingeSide;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class DoorBlock extends Block {
   public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
   public static final BooleanProperty OPEN = BlockStateProperties.OPEN;
   public static final EnumProperty<DoorHingeSide> HINGE = BlockStateProperties.DOOR_HINGE;
   public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
   public static final EnumProperty<DoubleBlockHalf> HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;
   protected static final float AABB_DOOR_THICKNESS = 3.0F;
   protected static final VoxelShape SOUTH_AABB = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 3.0D);
   protected static final VoxelShape NORTH_AABB = Block.box(0.0D, 0.0D, 13.0D, 16.0D, 16.0D, 16.0D);
   protected static final VoxelShape WEST_AABB = Block.box(13.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);
   protected static final VoxelShape EAST_AABB = Block.box(0.0D, 0.0D, 0.0D, 3.0D, 16.0D, 16.0D);

   public DoorBlock(BlockBehaviour.Properties p_52737_) {
      super(p_52737_);
      this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(OPEN, Boolean.valueOf(false)).setValue(HINGE, DoorHingeSide.LEFT).setValue(POWERED, Boolean.valueOf(false)).setValue(HALF, DoubleBlockHalf.LOWER));
   }

   public VoxelShape getShape(BlockState p_52807_, BlockGetter p_52808_, BlockPos p_52809_, CollisionContext p_52810_) {
      Direction direction = p_52807_.getValue(FACING);
      boolean flag = !p_52807_.getValue(OPEN);
      boolean flag1 = p_52807_.getValue(HINGE) == DoorHingeSide.RIGHT;
      switch(direction) {
      case EAST:
      default:
         return flag ? EAST_AABB : (flag1 ? NORTH_AABB : SOUTH_AABB);
      case SOUTH:
         return flag ? SOUTH_AABB : (flag1 ? EAST_AABB : WEST_AABB);
      case WEST:
         return flag ? WEST_AABB : (flag1 ? SOUTH_AABB : NORTH_AABB);
      case NORTH:
         return flag ? NORTH_AABB : (flag1 ? WEST_AABB : EAST_AABB);
      }
   }

   public BlockState updateShape(BlockState p_52796_, Direction p_52797_, BlockState p_52798_, LevelAccessor p_52799_, BlockPos p_52800_, BlockPos p_52801_) {
      DoubleBlockHalf doubleblockhalf = p_52796_.getValue(HALF);
      if (p_52797_.getAxis() == Direction.Axis.Y && doubleblockhalf == DoubleBlockHalf.LOWER == (p_52797_ == Direction.UP)) {
         return p_52798_.is(this) && p_52798_.getValue(HALF) != doubleblockhalf ? p_52796_.setValue(FACING, p_52798_.getValue(FACING)).setValue(OPEN, p_52798_.getValue(OPEN)).setValue(HINGE, p_52798_.getValue(HINGE)).setValue(POWERED, p_52798_.getValue(POWERED)) : Blocks.AIR.defaultBlockState();
      } else {
         return doubleblockhalf == DoubleBlockHalf.LOWER && p_52797_ == Direction.DOWN && !p_52796_.canSurvive(p_52799_, p_52800_) ? Blocks.AIR.defaultBlockState() : super.updateShape(p_52796_, p_52797_, p_52798_, p_52799_, p_52800_, p_52801_);
      }
   }

   public void playerWillDestroy(Level p_52755_, BlockPos p_52756_, BlockState p_52757_, Player p_52758_) {
      if (!p_52755_.isClientSide && p_52758_.isCreative()) {
         DoublePlantBlock.preventCreativeDropFromBottomPart(p_52755_, p_52756_, p_52757_, p_52758_);
      }

      super.playerWillDestroy(p_52755_, p_52756_, p_52757_, p_52758_);
   }

   public boolean isPathfindable(BlockState p_52764_, BlockGetter p_52765_, BlockPos p_52766_, PathComputationType p_52767_) {
      switch(p_52767_) {
      case LAND:
         return p_52764_.getValue(OPEN);
      case WATER:
         return false;
      case AIR:
         return p_52764_.getValue(OPEN);
      default:
         return false;
      }
   }

   private int getCloseSound() {
      return this.material == Material.METAL ? 1011 : 1012;
   }

   private int getOpenSound() {
      return this.material == Material.METAL ? 1005 : 1006;
   }

   @Nullable
   public BlockState getStateForPlacement(BlockPlaceContext p_52739_) {
      BlockPos blockpos = p_52739_.getClickedPos();
      Level level = p_52739_.getLevel();
      if (blockpos.getY() < level.getMaxBuildHeight() - 1 && level.getBlockState(blockpos.above()).canBeReplaced(p_52739_)) {
         boolean flag = level.hasNeighborSignal(blockpos) || level.hasNeighborSignal(blockpos.above());
         return this.defaultBlockState().setValue(FACING, p_52739_.getHorizontalDirection()).setValue(HINGE, this.getHinge(p_52739_)).setValue(POWERED, Boolean.valueOf(flag)).setValue(OPEN, Boolean.valueOf(flag)).setValue(HALF, DoubleBlockHalf.LOWER);
      } else {
         return null;
      }
   }

   public void setPlacedBy(Level p_52749_, BlockPos p_52750_, BlockState p_52751_, LivingEntity p_52752_, ItemStack p_52753_) {
      p_52749_.setBlock(p_52750_.above(), p_52751_.setValue(HALF, DoubleBlockHalf.UPPER), 3);
   }

   private DoorHingeSide getHinge(BlockPlaceContext p_52805_) {
      BlockGetter blockgetter = p_52805_.getLevel();
      BlockPos blockpos = p_52805_.getClickedPos();
      Direction direction = p_52805_.getHorizontalDirection();
      BlockPos blockpos1 = blockpos.above();
      Direction direction1 = direction.getCounterClockWise();
      BlockPos blockpos2 = blockpos.relative(direction1);
      BlockState blockstate = blockgetter.getBlockState(blockpos2);
      BlockPos blockpos3 = blockpos1.relative(direction1);
      BlockState blockstate1 = blockgetter.getBlockState(blockpos3);
      Direction direction2 = direction.getClockWise();
      BlockPos blockpos4 = blockpos.relative(direction2);
      BlockState blockstate2 = blockgetter.getBlockState(blockpos4);
      BlockPos blockpos5 = blockpos1.relative(direction2);
      BlockState blockstate3 = blockgetter.getBlockState(blockpos5);
      int i = (blockstate.isCollisionShapeFullBlock(blockgetter, blockpos2) ? -1 : 0) + (blockstate1.isCollisionShapeFullBlock(blockgetter, blockpos3) ? -1 : 0) + (blockstate2.isCollisionShapeFullBlock(blockgetter, blockpos4) ? 1 : 0) + (blockstate3.isCollisionShapeFullBlock(blockgetter, blockpos5) ? 1 : 0);
      boolean flag = blockstate.is(this) && blockstate.getValue(HALF) == DoubleBlockHalf.LOWER;
      boolean flag1 = blockstate2.is(this) && blockstate2.getValue(HALF) == DoubleBlockHalf.LOWER;
      if ((!flag || flag1) && i <= 0) {
         if ((!flag1 || flag) && i >= 0) {
            int j = direction.getStepX();
            int k = direction.getStepZ();
            Vec3 vec3 = p_52805_.getClickLocation();
            double d0 = vec3.x - (double)blockpos.getX();
            double d1 = vec3.z - (double)blockpos.getZ();
            return (j >= 0 || !(d1 < 0.5D)) && (j <= 0 || !(d1 > 0.5D)) && (k >= 0 || !(d0 > 0.5D)) && (k <= 0 || !(d0 < 0.5D)) ? DoorHingeSide.LEFT : DoorHingeSide.RIGHT;
         } else {
            return DoorHingeSide.LEFT;
         }
      } else {
         return DoorHingeSide.RIGHT;
      }
   }

   public InteractionResult use(BlockState p_52769_, Level p_52770_, BlockPos p_52771_, Player p_52772_, InteractionHand p_52773_, BlockHitResult p_52774_) {
      if (this.material == Material.METAL) {
         return InteractionResult.PASS;
      } else {
         p_52769_ = p_52769_.cycle(OPEN);
         p_52770_.setBlock(p_52771_, p_52769_, 10);
         p_52770_.levelEvent(p_52772_, p_52769_.getValue(OPEN) ? this.getOpenSound() : this.getCloseSound(), p_52771_, 0);
         p_52770_.gameEvent(p_52772_, this.isOpen(p_52769_) ? GameEvent.BLOCK_OPEN : GameEvent.BLOCK_CLOSE, p_52771_);
         return InteractionResult.sidedSuccess(p_52770_.isClientSide);
      }
   }

   public boolean isOpen(BlockState p_52816_) {
      return p_52816_.getValue(OPEN);
   }

   public void setOpen(@Nullable Entity p_153166_, Level p_153167_, BlockState p_153168_, BlockPos p_153169_, boolean p_153170_) {
      if (p_153168_.is(this) && p_153168_.getValue(OPEN) != p_153170_) {
         p_153167_.setBlock(p_153169_, p_153168_.setValue(OPEN, Boolean.valueOf(p_153170_)), 10);
         this.playSound(p_153167_, p_153169_, p_153170_);
         p_153167_.gameEvent(p_153166_, p_153170_ ? GameEvent.BLOCK_OPEN : GameEvent.BLOCK_CLOSE, p_153169_);
      }
   }

   public void neighborChanged(BlockState p_52776_, Level p_52777_, BlockPos p_52778_, Block p_52779_, BlockPos p_52780_, boolean p_52781_) {
      boolean flag = p_52777_.hasNeighborSignal(p_52778_) || p_52777_.hasNeighborSignal(p_52778_.relative(p_52776_.getValue(HALF) == DoubleBlockHalf.LOWER ? Direction.UP : Direction.DOWN));
      if (!this.defaultBlockState().is(p_52779_) && flag != p_52776_.getValue(POWERED)) {
         if (flag != p_52776_.getValue(OPEN)) {
            this.playSound(p_52777_, p_52778_, flag);
            p_52777_.gameEvent(flag ? GameEvent.BLOCK_OPEN : GameEvent.BLOCK_CLOSE, p_52778_);
         }

         p_52777_.setBlock(p_52778_, p_52776_.setValue(POWERED, Boolean.valueOf(flag)).setValue(OPEN, Boolean.valueOf(flag)), 2);
      }

   }

   public boolean canSurvive(BlockState p_52783_, LevelReader p_52784_, BlockPos p_52785_) {
      BlockPos blockpos = p_52785_.below();
      BlockState blockstate = p_52784_.getBlockState(blockpos);
      return p_52783_.getValue(HALF) == DoubleBlockHalf.LOWER ? blockstate.isFaceSturdy(p_52784_, blockpos, Direction.UP) : blockstate.is(this);
   }

   private void playSound(Level p_52760_, BlockPos p_52761_, boolean p_52762_) {
      p_52760_.levelEvent((Player)null, p_52762_ ? this.getOpenSound() : this.getCloseSound(), p_52761_, 0);
   }

   public PushReaction getPistonPushReaction(BlockState p_52814_) {
      return PushReaction.DESTROY;
   }

   public BlockState rotate(BlockState p_52790_, Rotation p_52791_) {
      return p_52790_.setValue(FACING, p_52791_.rotate(p_52790_.getValue(FACING)));
   }

   public BlockState mirror(BlockState p_52787_, Mirror p_52788_) {
      return p_52788_ == Mirror.NONE ? p_52787_ : p_52787_.rotate(p_52788_.getRotation(p_52787_.getValue(FACING))).cycle(HINGE);
   }

   public long getSeed(BlockState p_52793_, BlockPos p_52794_) {
      return Mth.getSeed(p_52794_.getX(), p_52794_.below(p_52793_.getValue(HALF) == DoubleBlockHalf.LOWER ? 0 : 1).getY(), p_52794_.getZ());
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_52803_) {
      p_52803_.add(HALF, FACING, OPEN, HINGE, POWERED);
   }

   public static boolean isWoodenDoor(Level p_52746_, BlockPos p_52747_) {
      return isWoodenDoor(p_52746_.getBlockState(p_52747_));
   }

   public static boolean isWoodenDoor(BlockState p_52818_) {
      return p_52818_.getBlock() instanceof DoorBlock && (p_52818_.getMaterial() == Material.WOOD || p_52818_.getMaterial() == Material.NETHER_WOOD);
   }
}