package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.BambooBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BambooLeaves;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.configurations.ProbabilityFeatureConfiguration;

public class BambooFeature extends Feature<ProbabilityFeatureConfiguration> {
   private static final BlockState BAMBOO_TRUNK = Blocks.BAMBOO.defaultBlockState().setValue(BambooBlock.AGE, Integer.valueOf(1)).setValue(BambooBlock.LEAVES, BambooLeaves.NONE).setValue(BambooBlock.STAGE, Integer.valueOf(0));
   private static final BlockState BAMBOO_FINAL_LARGE = BAMBOO_TRUNK.setValue(BambooBlock.LEAVES, BambooLeaves.LARGE).setValue(BambooBlock.STAGE, Integer.valueOf(1));
   private static final BlockState BAMBOO_TOP_LARGE = BAMBOO_TRUNK.setValue(BambooBlock.LEAVES, BambooLeaves.LARGE);
   private static final BlockState BAMBOO_TOP_SMALL = BAMBOO_TRUNK.setValue(BambooBlock.LEAVES, BambooLeaves.SMALL);

   public BambooFeature(Codec<ProbabilityFeatureConfiguration> p_65137_) {
      super(p_65137_);
   }

   public boolean place(FeaturePlaceContext<ProbabilityFeatureConfiguration> p_159438_) {
      int i = 0;
      BlockPos blockpos = p_159438_.origin();
      WorldGenLevel worldgenlevel = p_159438_.level();
      Random random = p_159438_.random();
      ProbabilityFeatureConfiguration probabilityfeatureconfiguration = p_159438_.config();
      BlockPos.MutableBlockPos blockpos$mutableblockpos = blockpos.mutable();
      BlockPos.MutableBlockPos blockpos$mutableblockpos1 = blockpos.mutable();
      if (worldgenlevel.isEmptyBlock(blockpos$mutableblockpos)) {
         if (Blocks.BAMBOO.defaultBlockState().canSurvive(worldgenlevel, blockpos$mutableblockpos)) {
            int j = random.nextInt(12) + 5;
            if (random.nextFloat() < probabilityfeatureconfiguration.probability) {
               int k = random.nextInt(4) + 1;

               for(int l = blockpos.getX() - k; l <= blockpos.getX() + k; ++l) {
                  for(int i1 = blockpos.getZ() - k; i1 <= blockpos.getZ() + k; ++i1) {
                     int j1 = l - blockpos.getX();
                     int k1 = i1 - blockpos.getZ();
                     if (j1 * j1 + k1 * k1 <= k * k) {
                        blockpos$mutableblockpos1.set(l, worldgenlevel.getHeight(Heightmap.Types.WORLD_SURFACE, l, i1) - 1, i1);
                        if (isDirt(worldgenlevel.getBlockState(blockpos$mutableblockpos1))) {
                           worldgenlevel.setBlock(blockpos$mutableblockpos1, Blocks.PODZOL.defaultBlockState(), 2);
                        }
                     }
                  }
               }
            }

            for(int l1 = 0; l1 < j && worldgenlevel.isEmptyBlock(blockpos$mutableblockpos); ++l1) {
               worldgenlevel.setBlock(blockpos$mutableblockpos, BAMBOO_TRUNK, 2);
               blockpos$mutableblockpos.move(Direction.UP, 1);
            }

            if (blockpos$mutableblockpos.getY() - blockpos.getY() >= 3) {
               worldgenlevel.setBlock(blockpos$mutableblockpos, BAMBOO_FINAL_LARGE, 2);
               worldgenlevel.setBlock(blockpos$mutableblockpos.move(Direction.DOWN, 1), BAMBOO_TOP_LARGE, 2);
               worldgenlevel.setBlock(blockpos$mutableblockpos.move(Direction.DOWN, 1), BAMBOO_TOP_SMALL, 2);
            }
         }

         ++i;
      }

      return i > 0;
   }
}