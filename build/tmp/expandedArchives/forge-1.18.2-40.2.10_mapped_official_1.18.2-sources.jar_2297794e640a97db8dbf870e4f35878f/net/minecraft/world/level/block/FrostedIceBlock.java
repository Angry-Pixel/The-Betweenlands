package net.minecraft.world.level.block;

import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

public class FrostedIceBlock extends IceBlock {
   public static final int MAX_AGE = 3;
   public static final IntegerProperty AGE = BlockStateProperties.AGE_3;
   private static final int NEIGHBORS_TO_AGE = 4;
   private static final int NEIGHBORS_TO_MELT = 2;

   public FrostedIceBlock(BlockBehaviour.Properties p_53564_) {
      super(p_53564_);
      this.registerDefaultState(this.stateDefinition.any().setValue(AGE, Integer.valueOf(0)));
   }

   public void randomTick(BlockState p_53588_, ServerLevel p_53589_, BlockPos p_53590_, Random p_53591_) {
      this.tick(p_53588_, p_53589_, p_53590_, p_53591_);
   }

   public void tick(BlockState p_53574_, ServerLevel p_53575_, BlockPos p_53576_, Random p_53577_) {
      if ((p_53577_.nextInt(3) == 0 || this.fewerNeigboursThan(p_53575_, p_53576_, 4)) && p_53575_.getMaxLocalRawBrightness(p_53576_) > 11 - p_53574_.getValue(AGE) - p_53574_.getLightBlock(p_53575_, p_53576_) && this.slightlyMelt(p_53574_, p_53575_, p_53576_)) {
         BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

         for(Direction direction : Direction.values()) {
            blockpos$mutableblockpos.setWithOffset(p_53576_, direction);
            BlockState blockstate = p_53575_.getBlockState(blockpos$mutableblockpos);
            if (blockstate.is(this) && !this.slightlyMelt(blockstate, p_53575_, blockpos$mutableblockpos)) {
               p_53575_.scheduleTick(blockpos$mutableblockpos, this, Mth.nextInt(p_53577_, 20, 40));
            }
         }

      } else {
         p_53575_.scheduleTick(p_53576_, this, Mth.nextInt(p_53577_, 20, 40));
      }
   }

   private boolean slightlyMelt(BlockState p_53593_, Level p_53594_, BlockPos p_53595_) {
      int i = p_53593_.getValue(AGE);
      if (i < 3) {
         p_53594_.setBlock(p_53595_, p_53593_.setValue(AGE, Integer.valueOf(i + 1)), 2);
         return false;
      } else {
         this.melt(p_53593_, p_53594_, p_53595_);
         return true;
      }
   }

   public void neighborChanged(BlockState p_53579_, Level p_53580_, BlockPos p_53581_, Block p_53582_, BlockPos p_53583_, boolean p_53584_) {
      if (p_53582_.defaultBlockState().is(this) && this.fewerNeigboursThan(p_53580_, p_53581_, 2)) {
         this.melt(p_53579_, p_53580_, p_53581_);
      }

      super.neighborChanged(p_53579_, p_53580_, p_53581_, p_53582_, p_53583_, p_53584_);
   }

   private boolean fewerNeigboursThan(BlockGetter p_53566_, BlockPos p_53567_, int p_53568_) {
      int i = 0;
      BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

      for(Direction direction : Direction.values()) {
         blockpos$mutableblockpos.setWithOffset(p_53567_, direction);
         if (p_53566_.getBlockState(blockpos$mutableblockpos).is(this)) {
            ++i;
            if (i >= p_53568_) {
               return false;
            }
         }
      }

      return true;
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_53586_) {
      p_53586_.add(AGE);
   }

   public ItemStack getCloneItemStack(BlockGetter p_53570_, BlockPos p_53571_, BlockState p_53572_) {
      return ItemStack.EMPTY;
   }
}