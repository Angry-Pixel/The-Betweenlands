package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.HugeMushroomFeatureConfiguration;

public abstract class AbstractHugeMushroomFeature extends Feature<HugeMushroomFeatureConfiguration> {
   public AbstractHugeMushroomFeature(Codec<HugeMushroomFeatureConfiguration> p_65093_) {
      super(p_65093_);
   }

   protected void placeTrunk(LevelAccessor p_65111_, Random p_65112_, BlockPos p_65113_, HugeMushroomFeatureConfiguration p_65114_, int p_65115_, BlockPos.MutableBlockPos p_65116_) {
      for(int i = 0; i < p_65115_; ++i) {
         p_65116_.set(p_65113_).move(Direction.UP, i);
         if (!p_65111_.getBlockState(p_65116_).isSolidRender(p_65111_, p_65116_)) {
            this.setBlock(p_65111_, p_65116_, p_65114_.stemProvider.getState(p_65112_, p_65113_));
         }
      }

   }

   protected int getTreeHeight(Random p_65130_) {
      int i = p_65130_.nextInt(3) + 4;
      if (p_65130_.nextInt(12) == 0) {
         i *= 2;
      }

      return i;
   }

   protected boolean isValidPosition(LevelAccessor p_65099_, BlockPos p_65100_, int p_65101_, BlockPos.MutableBlockPos p_65102_, HugeMushroomFeatureConfiguration p_65103_) {
      int i = p_65100_.getY();
      if (i >= p_65099_.getMinBuildHeight() + 1 && i + p_65101_ + 1 < p_65099_.getMaxBuildHeight()) {
         BlockState blockstate = p_65099_.getBlockState(p_65100_.below());
         if (!isDirt(blockstate) && !blockstate.is(BlockTags.MUSHROOM_GROW_BLOCK)) {
            return false;
         } else {
            for(int j = 0; j <= p_65101_; ++j) {
               int k = this.getTreeRadiusForHeight(-1, -1, p_65103_.foliageRadius, j);

               for(int l = -k; l <= k; ++l) {
                  for(int i1 = -k; i1 <= k; ++i1) {
                     BlockState blockstate1 = p_65099_.getBlockState(p_65102_.setWithOffset(p_65100_, l, j, i1));
                     if (!blockstate1.isAir() && !blockstate1.is(BlockTags.LEAVES)) {
                        return false;
                     }
                  }
               }
            }

            return true;
         }
      } else {
         return false;
      }
   }

   public boolean place(FeaturePlaceContext<HugeMushroomFeatureConfiguration> p_159436_) {
      WorldGenLevel worldgenlevel = p_159436_.level();
      BlockPos blockpos = p_159436_.origin();
      Random random = p_159436_.random();
      HugeMushroomFeatureConfiguration hugemushroomfeatureconfiguration = p_159436_.config();
      int i = this.getTreeHeight(random);
      BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();
      if (!this.isValidPosition(worldgenlevel, blockpos, i, blockpos$mutableblockpos, hugemushroomfeatureconfiguration)) {
         return false;
      } else {
         this.makeCap(worldgenlevel, random, blockpos, i, blockpos$mutableblockpos, hugemushroomfeatureconfiguration);
         this.placeTrunk(worldgenlevel, random, blockpos, hugemushroomfeatureconfiguration, i, blockpos$mutableblockpos);
         return true;
      }
   }

   protected abstract int getTreeRadiusForHeight(int p_65094_, int p_65095_, int p_65096_, int p_65097_);

   protected abstract void makeCap(LevelAccessor p_65104_, Random p_65105_, BlockPos p_65106_, int p_65107_, BlockPos.MutableBlockPos p_65108_, HugeMushroomFeatureConfiguration p_65109_);
}