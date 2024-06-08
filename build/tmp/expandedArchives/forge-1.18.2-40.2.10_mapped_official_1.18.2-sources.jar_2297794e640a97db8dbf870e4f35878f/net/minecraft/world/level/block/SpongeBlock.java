package net.minecraft.world.level.block;

import com.google.common.collect.Lists;
import java.util.Queue;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Tuple;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Material;

public class SpongeBlock extends Block {
   public static final int MAX_DEPTH = 6;
   public static final int MAX_COUNT = 64;

   public SpongeBlock(BlockBehaviour.Properties p_56796_) {
      super(p_56796_);
   }

   public void onPlace(BlockState p_56811_, Level p_56812_, BlockPos p_56813_, BlockState p_56814_, boolean p_56815_) {
      if (!p_56814_.is(p_56811_.getBlock())) {
         this.tryAbsorbWater(p_56812_, p_56813_);
      }
   }

   public void neighborChanged(BlockState p_56801_, Level p_56802_, BlockPos p_56803_, Block p_56804_, BlockPos p_56805_, boolean p_56806_) {
      this.tryAbsorbWater(p_56802_, p_56803_);
      super.neighborChanged(p_56801_, p_56802_, p_56803_, p_56804_, p_56805_, p_56806_);
   }

   protected void tryAbsorbWater(Level p_56798_, BlockPos p_56799_) {
      if (this.removeWaterBreadthFirstSearch(p_56798_, p_56799_)) {
         p_56798_.setBlock(p_56799_, Blocks.WET_SPONGE.defaultBlockState(), 2);
         p_56798_.levelEvent(2001, p_56799_, Block.getId(Blocks.WATER.defaultBlockState()));
      }

   }

   private boolean removeWaterBreadthFirstSearch(Level p_56808_, BlockPos p_56809_) {
      Queue<Tuple<BlockPos, Integer>> queue = Lists.newLinkedList();
      queue.add(new Tuple<>(p_56809_, 0));
      int i = 0;

      while(!queue.isEmpty()) {
         Tuple<BlockPos, Integer> tuple = queue.poll();
         BlockPos blockpos = tuple.getA();
         int j = tuple.getB();

         for(Direction direction : Direction.values()) {
            BlockPos blockpos1 = blockpos.relative(direction);
            BlockState blockstate = p_56808_.getBlockState(blockpos1);
            FluidState fluidstate = p_56808_.getFluidState(blockpos1);
            Material material = blockstate.getMaterial();
            if (fluidstate.is(FluidTags.WATER)) {
               if (blockstate.getBlock() instanceof BucketPickup && !((BucketPickup)blockstate.getBlock()).pickupBlock(p_56808_, blockpos1, blockstate).isEmpty()) {
                  ++i;
                  if (j < 6) {
                     queue.add(new Tuple<>(blockpos1, j + 1));
                  }
               } else if (blockstate.getBlock() instanceof LiquidBlock) {
                  p_56808_.setBlock(blockpos1, Blocks.AIR.defaultBlockState(), 3);
                  ++i;
                  if (j < 6) {
                     queue.add(new Tuple<>(blockpos1, j + 1));
                  }
               } else if (material == Material.WATER_PLANT || material == Material.REPLACEABLE_WATER_PLANT) {
                  BlockEntity blockentity = blockstate.hasBlockEntity() ? p_56808_.getBlockEntity(blockpos1) : null;
                  dropResources(blockstate, p_56808_, blockpos1, blockentity);
                  p_56808_.setBlock(blockpos1, Blocks.AIR.defaultBlockState(), 3);
                  ++i;
                  if (j < 6) {
                     queue.add(new Tuple<>(blockpos1, j + 1));
                  }
               }
            }
         }

         if (i > 64) {
            break;
         }
      }

      return i > 0;
   }
}