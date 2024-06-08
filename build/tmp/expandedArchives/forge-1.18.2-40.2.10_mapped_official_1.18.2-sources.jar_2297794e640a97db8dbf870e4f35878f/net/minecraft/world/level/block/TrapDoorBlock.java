package net.minecraft.world.level.block;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class TrapDoorBlock extends HorizontalDirectionalBlock implements SimpleWaterloggedBlock {
   public static final BooleanProperty OPEN = BlockStateProperties.OPEN;
   public static final EnumProperty<Half> HALF = BlockStateProperties.HALF;
   public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
   public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
   protected static final int AABB_THICKNESS = 3;
   protected static final VoxelShape EAST_OPEN_AABB = Block.box(0.0D, 0.0D, 0.0D, 3.0D, 16.0D, 16.0D);
   protected static final VoxelShape WEST_OPEN_AABB = Block.box(13.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);
   protected static final VoxelShape SOUTH_OPEN_AABB = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 3.0D);
   protected static final VoxelShape NORTH_OPEN_AABB = Block.box(0.0D, 0.0D, 13.0D, 16.0D, 16.0D, 16.0D);
   protected static final VoxelShape BOTTOM_AABB = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 3.0D, 16.0D);
   protected static final VoxelShape TOP_AABB = Block.box(0.0D, 13.0D, 0.0D, 16.0D, 16.0D, 16.0D);

   public TrapDoorBlock(BlockBehaviour.Properties p_57526_) {
      super(p_57526_);
      this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(OPEN, Boolean.valueOf(false)).setValue(HALF, Half.BOTTOM).setValue(POWERED, Boolean.valueOf(false)).setValue(WATERLOGGED, Boolean.valueOf(false)));
   }

   public VoxelShape getShape(BlockState p_57563_, BlockGetter p_57564_, BlockPos p_57565_, CollisionContext p_57566_) {
      if (!p_57563_.getValue(OPEN)) {
         return p_57563_.getValue(HALF) == Half.TOP ? TOP_AABB : BOTTOM_AABB;
      } else {
         switch((Direction)p_57563_.getValue(FACING)) {
         case NORTH:
         default:
            return NORTH_OPEN_AABB;
         case SOUTH:
            return SOUTH_OPEN_AABB;
         case WEST:
            return WEST_OPEN_AABB;
         case EAST:
            return EAST_OPEN_AABB;
         }
      }
   }

   public boolean isPathfindable(BlockState p_57535_, BlockGetter p_57536_, BlockPos p_57537_, PathComputationType p_57538_) {
      switch(p_57538_) {
      case LAND:
         return p_57535_.getValue(OPEN);
      case WATER:
         return p_57535_.getValue(WATERLOGGED);
      case AIR:
         return p_57535_.getValue(OPEN);
      default:
         return false;
      }
   }

   public InteractionResult use(BlockState p_57540_, Level p_57541_, BlockPos p_57542_, Player p_57543_, InteractionHand p_57544_, BlockHitResult p_57545_) {
      if (this.material == Material.METAL) {
         return InteractionResult.PASS;
      } else {
         p_57540_ = p_57540_.cycle(OPEN);
         p_57541_.setBlock(p_57542_, p_57540_, 2);
         if (p_57540_.getValue(WATERLOGGED)) {
            p_57541_.scheduleTick(p_57542_, Fluids.WATER, Fluids.WATER.getTickDelay(p_57541_));
         }

         this.playSound(p_57543_, p_57541_, p_57542_, p_57540_.getValue(OPEN));
         return InteractionResult.sidedSuccess(p_57541_.isClientSide);
      }
   }

   protected void playSound(@Nullable Player p_57528_, Level p_57529_, BlockPos p_57530_, boolean p_57531_) {
      if (p_57531_) {
         int i = this.material == Material.METAL ? 1037 : 1007;
         p_57529_.levelEvent(p_57528_, i, p_57530_, 0);
      } else {
         int j = this.material == Material.METAL ? 1036 : 1013;
         p_57529_.levelEvent(p_57528_, j, p_57530_, 0);
      }

      p_57529_.gameEvent(p_57528_, p_57531_ ? GameEvent.BLOCK_OPEN : GameEvent.BLOCK_CLOSE, p_57530_);
   }

   public void neighborChanged(BlockState p_57547_, Level p_57548_, BlockPos p_57549_, Block p_57550_, BlockPos p_57551_, boolean p_57552_) {
      if (!p_57548_.isClientSide) {
         boolean flag = p_57548_.hasNeighborSignal(p_57549_);
         if (flag != p_57547_.getValue(POWERED)) {
            if (p_57547_.getValue(OPEN) != flag) {
               p_57547_ = p_57547_.setValue(OPEN, Boolean.valueOf(flag));
               this.playSound((Player)null, p_57548_, p_57549_, flag);
            }

            p_57548_.setBlock(p_57549_, p_57547_.setValue(POWERED, Boolean.valueOf(flag)), 2);
            if (p_57547_.getValue(WATERLOGGED)) {
               p_57548_.scheduleTick(p_57549_, Fluids.WATER, Fluids.WATER.getTickDelay(p_57548_));
            }
         }

      }
   }

   public BlockState getStateForPlacement(BlockPlaceContext p_57533_) {
      BlockState blockstate = this.defaultBlockState();
      FluidState fluidstate = p_57533_.getLevel().getFluidState(p_57533_.getClickedPos());
      Direction direction = p_57533_.getClickedFace();
      if (!p_57533_.replacingClickedOnBlock() && direction.getAxis().isHorizontal()) {
         blockstate = blockstate.setValue(FACING, direction).setValue(HALF, p_57533_.getClickLocation().y - (double)p_57533_.getClickedPos().getY() > 0.5D ? Half.TOP : Half.BOTTOM);
      } else {
         blockstate = blockstate.setValue(FACING, p_57533_.getHorizontalDirection().getOpposite()).setValue(HALF, direction == Direction.UP ? Half.BOTTOM : Half.TOP);
      }

      if (p_57533_.getLevel().hasNeighborSignal(p_57533_.getClickedPos())) {
         blockstate = blockstate.setValue(OPEN, Boolean.valueOf(true)).setValue(POWERED, Boolean.valueOf(true));
      }

      return blockstate.setValue(WATERLOGGED, Boolean.valueOf(fluidstate.getType() == Fluids.WATER));
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_57561_) {
      p_57561_.add(FACING, OPEN, HALF, POWERED, WATERLOGGED);
   }

   public FluidState getFluidState(BlockState p_57568_) {
      return p_57568_.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(p_57568_);
   }

   public BlockState updateShape(BlockState p_57554_, Direction p_57555_, BlockState p_57556_, LevelAccessor p_57557_, BlockPos p_57558_, BlockPos p_57559_) {
      if (p_57554_.getValue(WATERLOGGED)) {
         p_57557_.scheduleTick(p_57558_, Fluids.WATER, Fluids.WATER.getTickDelay(p_57557_));
      }

      return super.updateShape(p_57554_, p_57555_, p_57556_, p_57557_, p_57558_, p_57559_);
   }

   //Forge Start
   @Override
   public boolean isLadder(BlockState state, net.minecraft.world.level.LevelReader world, BlockPos pos, net.minecraft.world.entity.LivingEntity entity) {
      if (state.getValue(OPEN)) {
         BlockPos downPos = pos.below();
         BlockState down = world.getBlockState(downPos);
         return down.getBlock().makesOpenTrapdoorAboveClimbable(down, world, downPos, state);
      }
      return false;
   }
   //Forge End

}
