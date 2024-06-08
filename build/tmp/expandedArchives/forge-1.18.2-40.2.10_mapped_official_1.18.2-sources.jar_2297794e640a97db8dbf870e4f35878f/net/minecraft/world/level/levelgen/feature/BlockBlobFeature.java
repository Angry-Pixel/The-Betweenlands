package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.BlockStateConfiguration;

public class BlockBlobFeature extends Feature<BlockStateConfiguration> {
   public BlockBlobFeature(Codec<BlockStateConfiguration> p_65248_) {
      super(p_65248_);
   }

   public boolean place(FeaturePlaceContext<BlockStateConfiguration> p_159471_) {
      BlockPos blockpos = p_159471_.origin();
      WorldGenLevel worldgenlevel = p_159471_.level();
      Random random = p_159471_.random();

      BlockStateConfiguration blockstateconfiguration;
      for(blockstateconfiguration = p_159471_.config(); blockpos.getY() > worldgenlevel.getMinBuildHeight() + 3; blockpos = blockpos.below()) {
         if (!worldgenlevel.isEmptyBlock(blockpos.below())) {
            BlockState blockstate = worldgenlevel.getBlockState(blockpos.below());
            if (isDirt(blockstate) || isStone(blockstate)) {
               break;
            }
         }
      }

      if (blockpos.getY() <= worldgenlevel.getMinBuildHeight() + 3) {
         return false;
      } else {
         for(int l = 0; l < 3; ++l) {
            int i = random.nextInt(2);
            int j = random.nextInt(2);
            int k = random.nextInt(2);
            float f = (float)(i + j + k) * 0.333F + 0.5F;

            for(BlockPos blockpos1 : BlockPos.betweenClosed(blockpos.offset(-i, -j, -k), blockpos.offset(i, j, k))) {
               if (blockpos1.distSqr(blockpos) <= (double)(f * f)) {
                  worldgenlevel.setBlock(blockpos1, blockstateconfiguration.state, 4);
               }
            }

            blockpos = blockpos.offset(-1 + random.nextInt(2), -random.nextInt(2), -1 + random.nextInt(2));
         }

         return true;
      }
   }
}