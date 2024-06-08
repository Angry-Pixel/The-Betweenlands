package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class IronBarsBlock extends CrossCollisionBlock {
   public IronBarsBlock(BlockBehaviour.Properties p_54198_) {
      super(1.0F, 1.0F, 16.0F, 16.0F, 16.0F, p_54198_);
      this.registerDefaultState(this.stateDefinition.any().setValue(NORTH, Boolean.valueOf(false)).setValue(EAST, Boolean.valueOf(false)).setValue(SOUTH, Boolean.valueOf(false)).setValue(WEST, Boolean.valueOf(false)).setValue(WATERLOGGED, Boolean.valueOf(false)));
   }

   public BlockState getStateForPlacement(BlockPlaceContext p_54200_) {
      BlockGetter blockgetter = p_54200_.getLevel();
      BlockPos blockpos = p_54200_.getClickedPos();
      FluidState fluidstate = p_54200_.getLevel().getFluidState(p_54200_.getClickedPos());
      BlockPos blockpos1 = blockpos.north();
      BlockPos blockpos2 = blockpos.south();
      BlockPos blockpos3 = blockpos.west();
      BlockPos blockpos4 = blockpos.east();
      BlockState blockstate = blockgetter.getBlockState(blockpos1);
      BlockState blockstate1 = blockgetter.getBlockState(blockpos2);
      BlockState blockstate2 = blockgetter.getBlockState(blockpos3);
      BlockState blockstate3 = blockgetter.getBlockState(blockpos4);
      return this.defaultBlockState().setValue(NORTH, Boolean.valueOf(this.attachsTo(blockstate, blockstate.isFaceSturdy(blockgetter, blockpos1, Direction.SOUTH)))).setValue(SOUTH, Boolean.valueOf(this.attachsTo(blockstate1, blockstate1.isFaceSturdy(blockgetter, blockpos2, Direction.NORTH)))).setValue(WEST, Boolean.valueOf(this.attachsTo(blockstate2, blockstate2.isFaceSturdy(blockgetter, blockpos3, Direction.EAST)))).setValue(EAST, Boolean.valueOf(this.attachsTo(blockstate3, blockstate3.isFaceSturdy(blockgetter, blockpos4, Direction.WEST)))).setValue(WATERLOGGED, Boolean.valueOf(fluidstate.getType() == Fluids.WATER));
   }

   public BlockState updateShape(BlockState p_54211_, Direction p_54212_, BlockState p_54213_, LevelAccessor p_54214_, BlockPos p_54215_, BlockPos p_54216_) {
      if (p_54211_.getValue(WATERLOGGED)) {
         p_54214_.scheduleTick(p_54215_, Fluids.WATER, Fluids.WATER.getTickDelay(p_54214_));
      }

      return p_54212_.getAxis().isHorizontal() ? p_54211_.setValue(PROPERTY_BY_DIRECTION.get(p_54212_), Boolean.valueOf(this.attachsTo(p_54213_, p_54213_.isFaceSturdy(p_54214_, p_54216_, p_54212_.getOpposite())))) : super.updateShape(p_54211_, p_54212_, p_54213_, p_54214_, p_54215_, p_54216_);
   }

   public VoxelShape getVisualShape(BlockState p_54202_, BlockGetter p_54203_, BlockPos p_54204_, CollisionContext p_54205_) {
      return Shapes.empty();
   }

   public boolean skipRendering(BlockState p_54207_, BlockState p_54208_, Direction p_54209_) {
      if (p_54208_.is(this)) {
         if (!p_54209_.getAxis().isHorizontal()) {
            return true;
         }

         if (p_54207_.getValue(PROPERTY_BY_DIRECTION.get(p_54209_)) && p_54208_.getValue(PROPERTY_BY_DIRECTION.get(p_54209_.getOpposite()))) {
            return true;
         }
      }

      return super.skipRendering(p_54207_, p_54208_, p_54209_);
   }

   public final boolean attachsTo(BlockState p_54218_, boolean p_54219_) {
      return !isExceptionForConnection(p_54218_) && p_54219_ || p_54218_.getBlock() instanceof IronBarsBlock || p_54218_.is(BlockTags.WALLS);
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_54221_) {
      p_54221_.add(NORTH, EAST, WEST, SOUTH, WATERLOGGED);
   }
}