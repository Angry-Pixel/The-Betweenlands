package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.TallSeagrassBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.configurations.ProbabilityFeatureConfiguration;

public class SeagrassFeature extends Feature<ProbabilityFeatureConfiguration> {
   public SeagrassFeature(Codec<ProbabilityFeatureConfiguration> p_66768_) {
      super(p_66768_);
   }

   public boolean place(FeaturePlaceContext<ProbabilityFeatureConfiguration> p_160318_) {
      boolean flag = false;
      Random random = p_160318_.random();
      WorldGenLevel worldgenlevel = p_160318_.level();
      BlockPos blockpos = p_160318_.origin();
      ProbabilityFeatureConfiguration probabilityfeatureconfiguration = p_160318_.config();
      int i = random.nextInt(8) - random.nextInt(8);
      int j = random.nextInt(8) - random.nextInt(8);
      int k = worldgenlevel.getHeight(Heightmap.Types.OCEAN_FLOOR, blockpos.getX() + i, blockpos.getZ() + j);
      BlockPos blockpos1 = new BlockPos(blockpos.getX() + i, k, blockpos.getZ() + j);
      if (worldgenlevel.getBlockState(blockpos1).is(Blocks.WATER)) {
         boolean flag1 = random.nextDouble() < (double)probabilityfeatureconfiguration.probability;
         BlockState blockstate = flag1 ? Blocks.TALL_SEAGRASS.defaultBlockState() : Blocks.SEAGRASS.defaultBlockState();
         if (blockstate.canSurvive(worldgenlevel, blockpos1)) {
            if (flag1) {
               BlockState blockstate1 = blockstate.setValue(TallSeagrassBlock.HALF, DoubleBlockHalf.UPPER);
               BlockPos blockpos2 = blockpos1.above();
               if (worldgenlevel.getBlockState(blockpos2).is(Blocks.WATER)) {
                  worldgenlevel.setBlock(blockpos1, blockstate, 2);
                  worldgenlevel.setBlock(blockpos2, blockstate1, 2);
               }
            } else {
               worldgenlevel.setBlock(blockpos1, blockstate, 2);
            }

            flag = true;
         }
      }

      return flag;
   }
}