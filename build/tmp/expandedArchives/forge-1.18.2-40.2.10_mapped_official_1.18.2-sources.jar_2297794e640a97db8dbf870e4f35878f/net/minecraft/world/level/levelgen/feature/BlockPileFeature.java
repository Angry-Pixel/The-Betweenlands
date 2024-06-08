package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.BlockPileConfiguration;

public class BlockPileFeature extends Feature<BlockPileConfiguration> {
   public BlockPileFeature(Codec<BlockPileConfiguration> p_65262_) {
      super(p_65262_);
   }

   public boolean place(FeaturePlaceContext<BlockPileConfiguration> p_159473_) {
      BlockPos blockpos = p_159473_.origin();
      WorldGenLevel worldgenlevel = p_159473_.level();
      Random random = p_159473_.random();
      BlockPileConfiguration blockpileconfiguration = p_159473_.config();
      if (blockpos.getY() < worldgenlevel.getMinBuildHeight() + 5) {
         return false;
      } else {
         int i = 2 + random.nextInt(2);
         int j = 2 + random.nextInt(2);

         for(BlockPos blockpos1 : BlockPos.betweenClosed(blockpos.offset(-i, 0, -j), blockpos.offset(i, 1, j))) {
            int k = blockpos.getX() - blockpos1.getX();
            int l = blockpos.getZ() - blockpos1.getZ();
            if ((float)(k * k + l * l) <= random.nextFloat() * 10.0F - random.nextFloat() * 6.0F) {
               this.tryPlaceBlock(worldgenlevel, blockpos1, random, blockpileconfiguration);
            } else if ((double)random.nextFloat() < 0.031D) {
               this.tryPlaceBlock(worldgenlevel, blockpos1, random, blockpileconfiguration);
            }
         }

         return true;
      }
   }

   private boolean mayPlaceOn(LevelAccessor p_65264_, BlockPos p_65265_, Random p_65266_) {
      BlockPos blockpos = p_65265_.below();
      BlockState blockstate = p_65264_.getBlockState(blockpos);
      return blockstate.is(Blocks.DIRT_PATH) ? p_65266_.nextBoolean() : blockstate.isFaceSturdy(p_65264_, blockpos, Direction.UP);
   }

   private void tryPlaceBlock(LevelAccessor p_65268_, BlockPos p_65269_, Random p_65270_, BlockPileConfiguration p_65271_) {
      if (p_65268_.isEmptyBlock(p_65269_) && this.mayPlaceOn(p_65268_, p_65269_, p_65270_)) {
         p_65268_.setBlock(p_65269_, p_65271_.stateProvider.getState(p_65270_, p_65269_), 4);
      }

   }
}