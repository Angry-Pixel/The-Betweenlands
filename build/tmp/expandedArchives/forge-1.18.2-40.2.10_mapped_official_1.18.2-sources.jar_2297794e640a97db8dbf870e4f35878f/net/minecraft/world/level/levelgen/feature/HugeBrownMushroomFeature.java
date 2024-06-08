package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.HugeMushroomBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.HugeMushroomFeatureConfiguration;

public class HugeBrownMushroomFeature extends AbstractHugeMushroomFeature {
   public HugeBrownMushroomFeature(Codec<HugeMushroomFeatureConfiguration> p_65879_) {
      super(p_65879_);
   }

   protected void makeCap(LevelAccessor p_65886_, Random p_65887_, BlockPos p_65888_, int p_65889_, BlockPos.MutableBlockPos p_65890_, HugeMushroomFeatureConfiguration p_65891_) {
      int i = p_65891_.foliageRadius;

      for(int j = -i; j <= i; ++j) {
         for(int k = -i; k <= i; ++k) {
            boolean flag = j == -i;
            boolean flag1 = j == i;
            boolean flag2 = k == -i;
            boolean flag3 = k == i;
            boolean flag4 = flag || flag1;
            boolean flag5 = flag2 || flag3;
            if (!flag4 || !flag5) {
               p_65890_.setWithOffset(p_65888_, j, p_65889_, k);
               if (!p_65886_.getBlockState(p_65890_).isSolidRender(p_65886_, p_65890_)) {
                  boolean flag6 = flag || flag5 && j == 1 - i;
                  boolean flag7 = flag1 || flag5 && j == i - 1;
                  boolean flag8 = flag2 || flag4 && k == 1 - i;
                  boolean flag9 = flag3 || flag4 && k == i - 1;
                  BlockState blockstate = p_65891_.capProvider.getState(p_65887_, p_65888_);
                  if (blockstate.hasProperty(HugeMushroomBlock.WEST) && blockstate.hasProperty(HugeMushroomBlock.EAST) && blockstate.hasProperty(HugeMushroomBlock.NORTH) && blockstate.hasProperty(HugeMushroomBlock.SOUTH)) {
                     blockstate = blockstate.setValue(HugeMushroomBlock.WEST, Boolean.valueOf(flag6)).setValue(HugeMushroomBlock.EAST, Boolean.valueOf(flag7)).setValue(HugeMushroomBlock.NORTH, Boolean.valueOf(flag8)).setValue(HugeMushroomBlock.SOUTH, Boolean.valueOf(flag9));
                  }

                  this.setBlock(p_65886_, p_65890_, blockstate);
               }
            }
         }
      }

   }

   protected int getTreeRadiusForHeight(int p_65881_, int p_65882_, int p_65883_, int p_65884_) {
      return p_65884_ <= 3 ? 0 : p_65883_;
   }
}