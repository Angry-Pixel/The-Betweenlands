package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class BasaltPillarFeature extends Feature<NoneFeatureConfiguration> {
   public BasaltPillarFeature(Codec<NoneFeatureConfiguration> p_65190_) {
      super(p_65190_);
   }

   public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> p_159446_) {
      BlockPos blockpos = p_159446_.origin();
      WorldGenLevel worldgenlevel = p_159446_.level();
      Random random = p_159446_.random();
      if (worldgenlevel.isEmptyBlock(blockpos) && !worldgenlevel.isEmptyBlock(blockpos.above())) {
         BlockPos.MutableBlockPos blockpos$mutableblockpos = blockpos.mutable();
         BlockPos.MutableBlockPos blockpos$mutableblockpos1 = blockpos.mutable();
         boolean flag = true;
         boolean flag1 = true;
         boolean flag2 = true;
         boolean flag3 = true;

         while(worldgenlevel.isEmptyBlock(blockpos$mutableblockpos)) {
            if (worldgenlevel.isOutsideBuildHeight(blockpos$mutableblockpos)) {
               return true;
            }

            worldgenlevel.setBlock(blockpos$mutableblockpos, Blocks.BASALT.defaultBlockState(), 2);
            flag = flag && this.placeHangOff(worldgenlevel, random, blockpos$mutableblockpos1.setWithOffset(blockpos$mutableblockpos, Direction.NORTH));
            flag1 = flag1 && this.placeHangOff(worldgenlevel, random, blockpos$mutableblockpos1.setWithOffset(blockpos$mutableblockpos, Direction.SOUTH));
            flag2 = flag2 && this.placeHangOff(worldgenlevel, random, blockpos$mutableblockpos1.setWithOffset(blockpos$mutableblockpos, Direction.WEST));
            flag3 = flag3 && this.placeHangOff(worldgenlevel, random, blockpos$mutableblockpos1.setWithOffset(blockpos$mutableblockpos, Direction.EAST));
            blockpos$mutableblockpos.move(Direction.DOWN);
         }

         blockpos$mutableblockpos.move(Direction.UP);
         this.placeBaseHangOff(worldgenlevel, random, blockpos$mutableblockpos1.setWithOffset(blockpos$mutableblockpos, Direction.NORTH));
         this.placeBaseHangOff(worldgenlevel, random, blockpos$mutableblockpos1.setWithOffset(blockpos$mutableblockpos, Direction.SOUTH));
         this.placeBaseHangOff(worldgenlevel, random, blockpos$mutableblockpos1.setWithOffset(blockpos$mutableblockpos, Direction.WEST));
         this.placeBaseHangOff(worldgenlevel, random, blockpos$mutableblockpos1.setWithOffset(blockpos$mutableblockpos, Direction.EAST));
         blockpos$mutableblockpos.move(Direction.DOWN);
         BlockPos.MutableBlockPos blockpos$mutableblockpos2 = new BlockPos.MutableBlockPos();

         for(int i = -3; i < 4; ++i) {
            for(int j = -3; j < 4; ++j) {
               int k = Mth.abs(i) * Mth.abs(j);
               if (random.nextInt(10) < 10 - k) {
                  blockpos$mutableblockpos2.set(blockpos$mutableblockpos.offset(i, 0, j));
                  int l = 3;

                  while(worldgenlevel.isEmptyBlock(blockpos$mutableblockpos1.setWithOffset(blockpos$mutableblockpos2, Direction.DOWN))) {
                     blockpos$mutableblockpos2.move(Direction.DOWN);
                     --l;
                     if (l <= 0) {
                        break;
                     }
                  }

                  if (!worldgenlevel.isEmptyBlock(blockpos$mutableblockpos1.setWithOffset(blockpos$mutableblockpos2, Direction.DOWN))) {
                     worldgenlevel.setBlock(blockpos$mutableblockpos2, Blocks.BASALT.defaultBlockState(), 2);
                  }
               }
            }
         }

         return true;
      } else {
         return false;
      }
   }

   private void placeBaseHangOff(LevelAccessor p_65192_, Random p_65193_, BlockPos p_65194_) {
      if (p_65193_.nextBoolean()) {
         p_65192_.setBlock(p_65194_, Blocks.BASALT.defaultBlockState(), 2);
      }

   }

   private boolean placeHangOff(LevelAccessor p_65208_, Random p_65209_, BlockPos p_65210_) {
      if (p_65209_.nextInt(10) != 0) {
         p_65208_.setBlock(p_65210_, Blocks.BASALT.defaultBlockState(), 2);
         return true;
      } else {
         return false;
      }
   }
}