package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChorusFlowerBlock;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class ChorusPlantFeature extends Feature<NoneFeatureConfiguration> {
   public ChorusPlantFeature(Codec<NoneFeatureConfiguration> p_65360_) {
      super(p_65360_);
   }

   public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> p_159521_) {
      WorldGenLevel worldgenlevel = p_159521_.level();
      BlockPos blockpos = p_159521_.origin();
      Random random = p_159521_.random();
      if (worldgenlevel.isEmptyBlock(blockpos) && worldgenlevel.getBlockState(blockpos.below()).is(Blocks.END_STONE)) {
         ChorusFlowerBlock.generatePlant(worldgenlevel, blockpos, random, 8);
         return true;
      } else {
         return false;
      }
   }
}