package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class EndIslandFeature extends Feature<NoneFeatureConfiguration> {
   public EndIslandFeature(Codec<NoneFeatureConfiguration> p_65701_) {
      super(p_65701_);
   }

   public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> p_159717_) {
      WorldGenLevel worldgenlevel = p_159717_.level();
      Random random = p_159717_.random();
      BlockPos blockpos = p_159717_.origin();
      float f = (float)random.nextInt(3) + 4.0F;

      for(int i = 0; f > 0.5F; --i) {
         for(int j = Mth.floor(-f); j <= Mth.ceil(f); ++j) {
            for(int k = Mth.floor(-f); k <= Mth.ceil(f); ++k) {
               if ((float)(j * j + k * k) <= (f + 1.0F) * (f + 1.0F)) {
                  this.setBlock(worldgenlevel, blockpos.offset(j, i, k), Blocks.END_STONE.defaultBlockState());
               }
            }
         }

         f -= (float)random.nextInt(2) + 0.5F;
      }

      return true;
   }
}