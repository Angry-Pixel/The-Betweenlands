package net.minecraft.world.level.block;

import java.util.Optional;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;

public interface ChangeOverTimeBlock<T extends Enum<T>> {
   int SCAN_DISTANCE = 4;

   Optional<BlockState> getNext(BlockState p_153040_);

   float getChanceModifier();

   default void onRandomTick(BlockState p_153042_, ServerLevel p_153043_, BlockPos p_153044_, Random p_153045_) {
      float f = 0.05688889F;
      if (p_153045_.nextFloat() < 0.05688889F) {
         this.applyChangeOverTime(p_153042_, p_153043_, p_153044_, p_153045_);
      }

   }

   T getAge();

   default void applyChangeOverTime(BlockState p_153047_, ServerLevel p_153048_, BlockPos p_153049_, Random p_153050_) {
      int i = this.getAge().ordinal();
      int j = 0;
      int k = 0;

      for(BlockPos blockpos : BlockPos.withinManhattan(p_153049_, 4, 4, 4)) {
         int l = blockpos.distManhattan(p_153049_);
         if (l > 4) {
            break;
         }

         if (!blockpos.equals(p_153049_)) {
            BlockState blockstate = p_153048_.getBlockState(blockpos);
            Block block = blockstate.getBlock();
            if (block instanceof ChangeOverTimeBlock) {
               Enum<?> oenum = ((ChangeOverTimeBlock)block).getAge();
               if (this.getAge().getClass() == oenum.getClass()) {
                  int i1 = oenum.ordinal();
                  if (i1 < i) {
                     return;
                  }

                  if (i1 > i) {
                     ++k;
                  } else {
                     ++j;
                  }
               }
            }
         }
      }

      float f = (float)(k + 1) / (float)(k + j + 1);
      float f1 = f * f * this.getChanceModifier();
      if (p_153050_.nextFloat() < f1) {
         this.getNext(p_153047_).ifPresent((p_153039_) -> {
            p_153048_.setBlockAndUpdate(p_153049_, p_153039_);
         });
      }

   }
}