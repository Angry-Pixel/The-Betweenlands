package net.minecraft.world.level.block.grower;

import java.util.Random;
import net.minecraft.core.Holder;
import net.minecraft.data.worldgen.features.TreeFeatures;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;

public class JungleTreeGrower extends AbstractMegaTreeGrower {
   protected Holder<? extends ConfiguredFeature<?, ?>> getConfiguredFeature(Random p_204326_, boolean p_204327_) {
      return TreeFeatures.JUNGLE_TREE_NO_VINE;
   }

   protected Holder<? extends ConfiguredFeature<?, ?>> getConfiguredMegaFeature(Random p_204324_) {
      return TreeFeatures.MEGA_JUNGLE_TREE;
   }
}