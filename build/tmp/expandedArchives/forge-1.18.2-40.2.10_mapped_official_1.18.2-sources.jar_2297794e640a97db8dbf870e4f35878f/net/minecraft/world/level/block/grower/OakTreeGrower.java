package net.minecraft.world.level.block.grower;

import java.util.Random;
import net.minecraft.core.Holder;
import net.minecraft.data.worldgen.features.TreeFeatures;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;

public class OakTreeGrower extends AbstractTreeGrower {
   protected Holder<? extends ConfiguredFeature<?, ?>> getConfiguredFeature(Random p_204329_, boolean p_204330_) {
      if (p_204329_.nextInt(10) == 0) {
         return p_204330_ ? TreeFeatures.FANCY_OAK_BEES_005 : TreeFeatures.FANCY_OAK;
      } else {
         return p_204330_ ? TreeFeatures.OAK_BEES_005 : TreeFeatures.OAK;
      }
   }
}