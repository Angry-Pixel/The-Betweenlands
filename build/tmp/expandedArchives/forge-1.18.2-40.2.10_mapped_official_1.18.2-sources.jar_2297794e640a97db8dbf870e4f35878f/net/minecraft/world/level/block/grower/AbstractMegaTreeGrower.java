package net.minecraft.world.level.block.grower;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;

public abstract class AbstractMegaTreeGrower extends AbstractTreeGrower {
   public boolean growTree(ServerLevel p_59985_, ChunkGenerator p_59986_, BlockPos p_59987_, BlockState p_59988_, Random p_59989_) {
      for(int i = 0; i >= -1; --i) {
         for(int j = 0; j >= -1; --j) {
            if (isTwoByTwoSapling(p_59988_, p_59985_, p_59987_, i, j)) {
               return this.placeMega(p_59985_, p_59986_, p_59987_, p_59988_, p_59989_, i, j);
            }
         }
      }

      return super.growTree(p_59985_, p_59986_, p_59987_, p_59988_, p_59989_);
   }

   @Nullable
   protected abstract Holder<? extends ConfiguredFeature<?, ?>> getConfiguredMegaFeature(Random p_204306_);

   public boolean placeMega(ServerLevel p_59991_, ChunkGenerator p_59992_, BlockPos p_59993_, BlockState p_59994_, Random p_59995_, int p_59996_, int p_59997_) {
      Holder<? extends ConfiguredFeature<?, ?>> holder = this.getConfiguredMegaFeature(p_59995_);
      net.minecraftforge.event.world.SaplingGrowTreeEvent event = net.minecraftforge.event.ForgeEventFactory.blockGrowFeature(p_59991_, p_59995_, p_59993_, holder);
      if (event.getResult().equals(net.minecraftforge.eventbus.api.Event.Result.DENY) || holder == null) {
         return false;
      } else {
         ConfiguredFeature<?, ?> configuredfeature = event.getFeature().value();
         BlockState blockstate = Blocks.AIR.defaultBlockState();
         p_59991_.setBlock(p_59993_.offset(p_59996_, 0, p_59997_), blockstate, 4);
         p_59991_.setBlock(p_59993_.offset(p_59996_ + 1, 0, p_59997_), blockstate, 4);
         p_59991_.setBlock(p_59993_.offset(p_59996_, 0, p_59997_ + 1), blockstate, 4);
         p_59991_.setBlock(p_59993_.offset(p_59996_ + 1, 0, p_59997_ + 1), blockstate, 4);
         if (configuredfeature.place(p_59991_, p_59992_, p_59995_, p_59993_.offset(p_59996_, 0, p_59997_))) {
            return true;
         } else {
            p_59991_.setBlock(p_59993_.offset(p_59996_, 0, p_59997_), p_59994_, 4);
            p_59991_.setBlock(p_59993_.offset(p_59996_ + 1, 0, p_59997_), p_59994_, 4);
            p_59991_.setBlock(p_59993_.offset(p_59996_, 0, p_59997_ + 1), p_59994_, 4);
            p_59991_.setBlock(p_59993_.offset(p_59996_ + 1, 0, p_59997_ + 1), p_59994_, 4);
            return false;
         }
      }
   }

   public static boolean isTwoByTwoSapling(BlockState p_59999_, BlockGetter p_60000_, BlockPos p_60001_, int p_60002_, int p_60003_) {
      Block block = p_59999_.getBlock();
      return p_60000_.getBlockState(p_60001_.offset(p_60002_, 0, p_60003_)).is(block) && p_60000_.getBlockState(p_60001_.offset(p_60002_ + 1, 0, p_60003_)).is(block) && p_60000_.getBlockState(p_60001_.offset(p_60002_, 0, p_60003_ + 1)).is(block) && p_60000_.getBlockState(p_60001_.offset(p_60002_ + 1, 0, p_60003_ + 1)).is(block);
   }
}
