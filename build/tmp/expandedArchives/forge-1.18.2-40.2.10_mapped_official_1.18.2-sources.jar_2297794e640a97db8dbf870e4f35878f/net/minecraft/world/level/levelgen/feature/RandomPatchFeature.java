package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.feature.configurations.RandomPatchConfiguration;

public class RandomPatchFeature extends Feature<RandomPatchConfiguration> {
   public RandomPatchFeature(Codec<RandomPatchConfiguration> p_66605_) {
      super(p_66605_);
   }

   public boolean place(FeaturePlaceContext<RandomPatchConfiguration> p_160210_) {
      RandomPatchConfiguration randompatchconfiguration = p_160210_.config();
      Random random = p_160210_.random();
      BlockPos blockpos = p_160210_.origin();
      WorldGenLevel worldgenlevel = p_160210_.level();
      int i = 0;
      BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();
      int j = randompatchconfiguration.xzSpread() + 1;
      int k = randompatchconfiguration.ySpread() + 1;

      for(int l = 0; l < randompatchconfiguration.tries(); ++l) {
         blockpos$mutableblockpos.setWithOffset(blockpos, random.nextInt(j) - random.nextInt(j), random.nextInt(k) - random.nextInt(k), random.nextInt(j) - random.nextInt(j));
         if (randompatchconfiguration.feature().value().place(worldgenlevel, p_160210_.chunkGenerator(), random, blockpos$mutableblockpos)) {
            ++i;
         }
      }

      return i > 0;
   }
}