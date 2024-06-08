package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.HugeMushroomBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.HugeMushroomFeatureConfiguration;

public class HugeRedMushroomFeature extends AbstractHugeMushroomFeature {
   public HugeRedMushroomFeature(Codec<HugeMushroomFeatureConfiguration> p_65975_) {
      super(p_65975_);
   }

   protected void makeCap(LevelAccessor p_65982_, Random p_65983_, BlockPos p_65984_, int p_65985_, BlockPos.MutableBlockPos p_65986_, HugeMushroomFeatureConfiguration p_65987_) {
      for(int i = p_65985_ - 3; i <= p_65985_; ++i) {
         int j = i < p_65985_ ? p_65987_.foliageRadius : p_65987_.foliageRadius - 1;
         int k = p_65987_.foliageRadius - 2;

         for(int l = -j; l <= j; ++l) {
            for(int i1 = -j; i1 <= j; ++i1) {
               boolean flag = l == -j;
               boolean flag1 = l == j;
               boolean flag2 = i1 == -j;
               boolean flag3 = i1 == j;
               boolean flag4 = flag || flag1;
               boolean flag5 = flag2 || flag3;
               if (i >= p_65985_ || flag4 != flag5) {
                  p_65986_.setWithOffset(p_65984_, l, i, i1);
                  if (!p_65982_.getBlockState(p_65986_).isSolidRender(p_65982_, p_65986_)) {
                     BlockState blockstate = p_65987_.capProvider.getState(p_65983_, p_65984_);
                     if (blockstate.hasProperty(HugeMushroomBlock.WEST) && blockstate.hasProperty(HugeMushroomBlock.EAST) && blockstate.hasProperty(HugeMushroomBlock.NORTH) && blockstate.hasProperty(HugeMushroomBlock.SOUTH) && blockstate.hasProperty(HugeMushroomBlock.UP)) {
                        blockstate = blockstate.setValue(HugeMushroomBlock.UP, Boolean.valueOf(i >= p_65985_ - 1)).setValue(HugeMushroomBlock.WEST, Boolean.valueOf(l < -k)).setValue(HugeMushroomBlock.EAST, Boolean.valueOf(l > k)).setValue(HugeMushroomBlock.NORTH, Boolean.valueOf(i1 < -k)).setValue(HugeMushroomBlock.SOUTH, Boolean.valueOf(i1 > k));
                     }

                     this.setBlock(p_65982_, p_65986_, blockstate);
                  }
               }
            }
         }
      }

   }

   protected int getTreeRadiusForHeight(int p_65977_, int p_65978_, int p_65979_, int p_65980_) {
      int i = 0;
      if (p_65980_ < p_65978_ && p_65980_ >= p_65978_ - 3) {
         i = p_65979_;
      } else if (p_65980_ == p_65978_) {
         i = p_65979_;
      }

      return i;
   }
}