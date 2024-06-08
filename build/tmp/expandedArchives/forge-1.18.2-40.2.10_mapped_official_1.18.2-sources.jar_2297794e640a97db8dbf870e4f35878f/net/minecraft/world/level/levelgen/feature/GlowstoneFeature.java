package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class GlowstoneFeature extends Feature<NoneFeatureConfiguration> {
   public GlowstoneFeature(Codec<NoneFeatureConfiguration> p_65865_) {
      super(p_65865_);
   }

   public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> p_159861_) {
      WorldGenLevel worldgenlevel = p_159861_.level();
      BlockPos blockpos = p_159861_.origin();
      Random random = p_159861_.random();
      if (!worldgenlevel.isEmptyBlock(blockpos)) {
         return false;
      } else {
         BlockState blockstate = worldgenlevel.getBlockState(blockpos.above());
         if (!blockstate.is(Blocks.NETHERRACK) && !blockstate.is(Blocks.BASALT) && !blockstate.is(Blocks.BLACKSTONE)) {
            return false;
         } else {
            worldgenlevel.setBlock(blockpos, Blocks.GLOWSTONE.defaultBlockState(), 2);

            for(int i = 0; i < 1500; ++i) {
               BlockPos blockpos1 = blockpos.offset(random.nextInt(8) - random.nextInt(8), -random.nextInt(12), random.nextInt(8) - random.nextInt(8));
               if (worldgenlevel.getBlockState(blockpos1).isAir()) {
                  int j = 0;

                  for(Direction direction : Direction.values()) {
                     if (worldgenlevel.getBlockState(blockpos1.relative(direction)).is(Blocks.GLOWSTONE)) {
                        ++j;
                     }

                     if (j > 1) {
                        break;
                     }
                  }

                  if (j == 1) {
                     worldgenlevel.setBlock(blockpos1, Blocks.GLOWSTONE.defaultBlockState(), 2);
                  }
               }
            }

            return true;
         }
      }
   }
}