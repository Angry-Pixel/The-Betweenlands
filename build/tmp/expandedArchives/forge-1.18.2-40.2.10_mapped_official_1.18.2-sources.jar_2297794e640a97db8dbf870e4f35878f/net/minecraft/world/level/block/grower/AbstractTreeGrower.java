package net.minecraft.world.level.block.grower;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;

public abstract class AbstractTreeGrower {
   @Nullable
   protected abstract Holder<? extends ConfiguredFeature<?, ?>> getConfiguredFeature(Random p_204307_, boolean p_204308_);

   public boolean growTree(ServerLevel p_60006_, ChunkGenerator p_60007_, BlockPos p_60008_, BlockState p_60009_, Random p_60010_) {
      Holder<? extends ConfiguredFeature<?, ?>> holder = this.getConfiguredFeature(p_60010_, this.hasFlowers(p_60006_, p_60008_));
      net.minecraftforge.event.world.SaplingGrowTreeEvent event = net.minecraftforge.event.ForgeEventFactory.blockGrowFeature(p_60006_, p_60010_, p_60008_, holder);
      if (event.getResult().equals(net.minecraftforge.eventbus.api.Event.Result.DENY) || holder == null) {
         return false;
      } else {
         ConfiguredFeature<?, ?> configuredfeature = event.getFeature().value();
         p_60006_.setBlock(p_60008_, Blocks.AIR.defaultBlockState(), 4);
         if (configuredfeature.place(p_60006_, p_60007_, p_60010_, p_60008_)) {
            return true;
         } else {
            p_60006_.setBlock(p_60008_, p_60009_, 4);
            return false;
         }
      }
   }

   private boolean hasFlowers(LevelAccessor p_60012_, BlockPos p_60013_) {
      for(BlockPos blockpos : BlockPos.MutableBlockPos.betweenClosed(p_60013_.below().north(2).west(2), p_60013_.above().south(2).east(2))) {
         if (p_60012_.getBlockState(blockpos).is(BlockTags.FLOWERS)) {
            return true;
         }
      }

      return false;
   }
}
