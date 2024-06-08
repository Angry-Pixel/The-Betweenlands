package net.minecraft.world.level.block;

import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class NetherrackBlock extends Block implements BonemealableBlock {
   public NetherrackBlock(BlockBehaviour.Properties p_54995_) {
      super(p_54995_);
   }

   public boolean isValidBonemealTarget(BlockGetter p_55002_, BlockPos p_55003_, BlockState p_55004_, boolean p_55005_) {
      if (!p_55002_.getBlockState(p_55003_.above()).propagatesSkylightDown(p_55002_, p_55003_)) {
         return false;
      } else {
         for(BlockPos blockpos : BlockPos.betweenClosed(p_55003_.offset(-1, -1, -1), p_55003_.offset(1, 1, 1))) {
            if (p_55002_.getBlockState(blockpos).is(BlockTags.NYLIUM)) {
               return true;
            }
         }

         return false;
      }
   }

   public boolean isBonemealSuccess(Level p_55007_, Random p_55008_, BlockPos p_55009_, BlockState p_55010_) {
      return true;
   }

   public void performBonemeal(ServerLevel p_54997_, Random p_54998_, BlockPos p_54999_, BlockState p_55000_) {
      boolean flag = false;
      boolean flag1 = false;

      for(BlockPos blockpos : BlockPos.betweenClosed(p_54999_.offset(-1, -1, -1), p_54999_.offset(1, 1, 1))) {
         BlockState blockstate = p_54997_.getBlockState(blockpos);
         if (blockstate.is(Blocks.WARPED_NYLIUM)) {
            flag1 = true;
         }

         if (blockstate.is(Blocks.CRIMSON_NYLIUM)) {
            flag = true;
         }

         if (flag1 && flag) {
            break;
         }
      }

      if (flag1 && flag) {
         p_54997_.setBlock(p_54999_, p_54998_.nextBoolean() ? Blocks.WARPED_NYLIUM.defaultBlockState() : Blocks.CRIMSON_NYLIUM.defaultBlockState(), 3);
      } else if (flag1) {
         p_54997_.setBlock(p_54999_, Blocks.WARPED_NYLIUM.defaultBlockState(), 3);
      } else if (flag) {
         p_54997_.setBlock(p_54999_, Blocks.CRIMSON_NYLIUM.defaultBlockState(), 3);
      }

   }
}