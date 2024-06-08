package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class ConcretePowderBlock extends FallingBlock {
   private final BlockState concrete;

   public ConcretePowderBlock(Block p_52060_, BlockBehaviour.Properties p_52061_) {
      super(p_52061_);
      this.concrete = p_52060_.defaultBlockState();
   }

   public void onLand(Level p_52068_, BlockPos p_52069_, BlockState p_52070_, BlockState p_52071_, FallingBlockEntity p_52072_) {
      if (shouldSolidify(p_52068_, p_52069_, p_52071_)) {
         p_52068_.setBlock(p_52069_, this.concrete, 3);
      }

   }

   public BlockState getStateForPlacement(BlockPlaceContext p_52063_) {
      BlockGetter blockgetter = p_52063_.getLevel();
      BlockPos blockpos = p_52063_.getClickedPos();
      BlockState blockstate = blockgetter.getBlockState(blockpos);
      return shouldSolidify(blockgetter, blockpos, blockstate) ? this.concrete : super.getStateForPlacement(p_52063_);
   }

   private static boolean shouldSolidify(BlockGetter p_52081_, BlockPos p_52082_, BlockState p_52083_) {
      return canSolidify(p_52083_) || touchesLiquid(p_52081_, p_52082_);
   }

   private static boolean touchesLiquid(BlockGetter p_52065_, BlockPos p_52066_) {
      boolean flag = false;
      BlockPos.MutableBlockPos blockpos$mutableblockpos = p_52066_.mutable();

      for(Direction direction : Direction.values()) {
         BlockState blockstate = p_52065_.getBlockState(blockpos$mutableblockpos);
         if (direction != Direction.DOWN || canSolidify(blockstate)) {
            blockpos$mutableblockpos.setWithOffset(p_52066_, direction);
            blockstate = p_52065_.getBlockState(blockpos$mutableblockpos);
            if (canSolidify(blockstate) && !blockstate.isFaceSturdy(p_52065_, p_52066_, direction.getOpposite())) {
               flag = true;
               break;
            }
         }
      }

      return flag;
   }

   private static boolean canSolidify(BlockState p_52089_) {
      return p_52089_.getFluidState().is(FluidTags.WATER);
   }

   public BlockState updateShape(BlockState p_52074_, Direction p_52075_, BlockState p_52076_, LevelAccessor p_52077_, BlockPos p_52078_, BlockPos p_52079_) {
      return touchesLiquid(p_52077_, p_52078_) ? this.concrete : super.updateShape(p_52074_, p_52075_, p_52076_, p_52077_, p_52078_, p_52079_);
   }

   public int getDustColor(BlockState p_52085_, BlockGetter p_52086_, BlockPos p_52087_) {
      return p_52085_.getMapColor(p_52086_, p_52087_).col;
   }
}