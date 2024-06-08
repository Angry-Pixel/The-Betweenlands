package net.minecraft.world.level.block;

import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class RootedDirtBlock extends Block implements BonemealableBlock {
   public RootedDirtBlock(BlockBehaviour.Properties p_154359_) {
      super(p_154359_);
   }

   public boolean isValidBonemealTarget(BlockGetter p_154366_, BlockPos p_154367_, BlockState p_154368_, boolean p_154369_) {
      return p_154366_.getBlockState(p_154367_.below()).isAir();
   }

   public boolean isBonemealSuccess(Level p_154371_, Random p_154372_, BlockPos p_154373_, BlockState p_154374_) {
      return true;
   }

   public void performBonemeal(ServerLevel p_154361_, Random p_154362_, BlockPos p_154363_, BlockState p_154364_) {
      p_154361_.setBlockAndUpdate(p_154363_.below(), Blocks.HANGING_ROOTS.defaultBlockState());
   }
}