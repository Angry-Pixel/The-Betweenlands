package net.minecraft.world.level.block;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class SlabBlock extends Block implements SimpleWaterloggedBlock {
   public static final EnumProperty<SlabType> TYPE = BlockStateProperties.SLAB_TYPE;
   public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
   protected static final VoxelShape BOTTOM_AABB = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D);
   protected static final VoxelShape TOP_AABB = Block.box(0.0D, 8.0D, 0.0D, 16.0D, 16.0D, 16.0D);

   public SlabBlock(BlockBehaviour.Properties p_56359_) {
      super(p_56359_);
      this.registerDefaultState(this.defaultBlockState().setValue(TYPE, SlabType.BOTTOM).setValue(WATERLOGGED, Boolean.valueOf(false)));
   }

   public boolean useShapeForLightOcclusion(BlockState p_56395_) {
      return p_56395_.getValue(TYPE) != SlabType.DOUBLE;
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_56388_) {
      p_56388_.add(TYPE, WATERLOGGED);
   }

   public VoxelShape getShape(BlockState p_56390_, BlockGetter p_56391_, BlockPos p_56392_, CollisionContext p_56393_) {
      SlabType slabtype = p_56390_.getValue(TYPE);
      switch(slabtype) {
      case DOUBLE:
         return Shapes.block();
      case TOP:
         return TOP_AABB;
      default:
         return BOTTOM_AABB;
      }
   }

   @Nullable
   public BlockState getStateForPlacement(BlockPlaceContext p_56361_) {
      BlockPos blockpos = p_56361_.getClickedPos();
      BlockState blockstate = p_56361_.getLevel().getBlockState(blockpos);
      if (blockstate.is(this)) {
         return blockstate.setValue(TYPE, SlabType.DOUBLE).setValue(WATERLOGGED, Boolean.valueOf(false));
      } else {
         FluidState fluidstate = p_56361_.getLevel().getFluidState(blockpos);
         BlockState blockstate1 = this.defaultBlockState().setValue(TYPE, SlabType.BOTTOM).setValue(WATERLOGGED, Boolean.valueOf(fluidstate.getType() == Fluids.WATER));
         Direction direction = p_56361_.getClickedFace();
         return direction != Direction.DOWN && (direction == Direction.UP || !(p_56361_.getClickLocation().y - (double)blockpos.getY() > 0.5D)) ? blockstate1 : blockstate1.setValue(TYPE, SlabType.TOP);
      }
   }

   public boolean canBeReplaced(BlockState p_56373_, BlockPlaceContext p_56374_) {
      ItemStack itemstack = p_56374_.getItemInHand();
      SlabType slabtype = p_56373_.getValue(TYPE);
      if (slabtype != SlabType.DOUBLE && itemstack.is(this.asItem())) {
         if (p_56374_.replacingClickedOnBlock()) {
            boolean flag = p_56374_.getClickLocation().y - (double)p_56374_.getClickedPos().getY() > 0.5D;
            Direction direction = p_56374_.getClickedFace();
            if (slabtype == SlabType.BOTTOM) {
               return direction == Direction.UP || flag && direction.getAxis().isHorizontal();
            } else {
               return direction == Direction.DOWN || !flag && direction.getAxis().isHorizontal();
            }
         } else {
            return true;
         }
      } else {
         return false;
      }
   }

   public FluidState getFluidState(BlockState p_56397_) {
      return p_56397_.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(p_56397_);
   }

   public boolean placeLiquid(LevelAccessor p_56368_, BlockPos p_56369_, BlockState p_56370_, FluidState p_56371_) {
      return p_56370_.getValue(TYPE) != SlabType.DOUBLE ? SimpleWaterloggedBlock.super.placeLiquid(p_56368_, p_56369_, p_56370_, p_56371_) : false;
   }

   public boolean canPlaceLiquid(BlockGetter p_56363_, BlockPos p_56364_, BlockState p_56365_, Fluid p_56366_) {
      return p_56365_.getValue(TYPE) != SlabType.DOUBLE ? SimpleWaterloggedBlock.super.canPlaceLiquid(p_56363_, p_56364_, p_56365_, p_56366_) : false;
   }

   public BlockState updateShape(BlockState p_56381_, Direction p_56382_, BlockState p_56383_, LevelAccessor p_56384_, BlockPos p_56385_, BlockPos p_56386_) {
      if (p_56381_.getValue(WATERLOGGED)) {
         p_56384_.scheduleTick(p_56385_, Fluids.WATER, Fluids.WATER.getTickDelay(p_56384_));
      }

      return super.updateShape(p_56381_, p_56382_, p_56383_, p_56384_, p_56385_, p_56386_);
   }

   public boolean isPathfindable(BlockState p_56376_, BlockGetter p_56377_, BlockPos p_56378_, PathComputationType p_56379_) {
      switch(p_56379_) {
      case LAND:
         return false;
      case WATER:
         return p_56377_.getFluidState(p_56378_).is(FluidTags.WATER);
      case AIR:
         return false;
      default:
         return false;
      }
   }
}