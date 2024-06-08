package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathComputationType;

public class BushBlock extends Block implements net.minecraftforge.common.IPlantable {
   public BushBlock(BlockBehaviour.Properties p_51021_) {
      super(p_51021_);
   }

   protected boolean mayPlaceOn(BlockState p_51042_, BlockGetter p_51043_, BlockPos p_51044_) {
      return p_51042_.is(BlockTags.DIRT) || p_51042_.is(Blocks.FARMLAND);
   }

   public BlockState updateShape(BlockState p_51032_, Direction p_51033_, BlockState p_51034_, LevelAccessor p_51035_, BlockPos p_51036_, BlockPos p_51037_) {
      return !p_51032_.canSurvive(p_51035_, p_51036_) ? Blocks.AIR.defaultBlockState() : super.updateShape(p_51032_, p_51033_, p_51034_, p_51035_, p_51036_, p_51037_);
   }

   public boolean canSurvive(BlockState p_51028_, LevelReader p_51029_, BlockPos p_51030_) {
      BlockPos blockpos = p_51030_.below();
      if (p_51028_.getBlock() == this) //Forge: This function is called during world gen and placement, before this block is set, so if we are not 'here' then assume it's the pre-check.
         return p_51029_.getBlockState(blockpos).canSustainPlant(p_51029_, blockpos, Direction.UP, this);
      return this.mayPlaceOn(p_51029_.getBlockState(blockpos), p_51029_, blockpos);
   }

   public boolean propagatesSkylightDown(BlockState p_51039_, BlockGetter p_51040_, BlockPos p_51041_) {
      return p_51039_.getFluidState().isEmpty();
   }

   public boolean isPathfindable(BlockState p_51023_, BlockGetter p_51024_, BlockPos p_51025_, PathComputationType p_51026_) {
      return p_51026_ == PathComputationType.AIR && !this.hasCollision ? true : super.isPathfindable(p_51023_, p_51024_, p_51025_, p_51026_);
   }

   @Override
   public BlockState getPlant(BlockGetter world, BlockPos pos) {
      BlockState state = world.getBlockState(pos);
      if (state.getBlock() != this) return defaultBlockState();
      return state;
   }
}
